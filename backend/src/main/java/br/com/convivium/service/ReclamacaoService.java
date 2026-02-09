package br.com.convivium.service;

import br.com.convivium.dto.request.AcaoReclamacaoDTO;
import br.com.convivium.dto.request.ReclamacaoFiltroDTO;
import br.com.convivium.dto.request.SolucaoReclamacaoDTO;
import br.com.convivium.dto.response.*;
import br.com.convivium.entity.*;
import br.com.convivium.exception.ApiException;
import br.com.convivium.entity.enums.StatusReclamacao;
import br.com.convivium.entity.enums.TipoTemplateEmail;
import br.com.convivium.entity.specification.ReclamacaoSpecification;
import br.com.convivium.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ReclamacaoService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private final ReclamacaoRepository reclamacaoRepository;
    private final UserRepository userRepository;
    private final EmpresaRepository empresaRepository;
    private final AnexoRepository anexoRepository;
    private final AcaoReclamacaoRepository acaoReclamacaoRepository;
    private final EmailService emailService;

    public ReclamacaoService(ReclamacaoRepository reclamacaoRepository,
                             UserRepository userRepository,
                             EmpresaRepository empresaRepository,
                             AnexoRepository anexoRepository, AcaoReclamacaoRepository acaoReclamacaoRepository, EmailService emailService) {
        this.reclamacaoRepository = reclamacaoRepository;
        this.userRepository = userRepository;
        this.empresaRepository = empresaRepository;
        this.anexoRepository = anexoRepository;
        this.acaoReclamacaoRepository = acaoReclamacaoRepository;
        this.emailService = emailService;
    }

    @Transactional
    public Reclamacao criarReclamacao(String tipo, String detalhes, Long usuarioId, Long empresaId, MultipartFile[] arquivos) throws IOException {
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new ApiException.NotFoundException("Usuário não encontrado"));

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new ApiException.NotFoundException("Empresa não encontrada"));

        if (usuario.getEmpresa() == null || !usuario.getEmpresa().getId().equals(empresaId)) {
            throw new ApiException.ForbiddenException("Usuário não pertence a este condomínio.");
        }

        Reclamacao reclamacao = new Reclamacao();
        reclamacao.setTipo(tipo);
        reclamacao.setDetalhes(detalhes);
        reclamacao.setUsuario(usuario);
        reclamacao.setEmpresa(empresa);
        reclamacao.setStatus(StatusReclamacao.EM_ANALISE);

        reclamacao = reclamacaoRepository.save(reclamacao);

        if (arquivos != null) {
            for (MultipartFile arquivo : arquivos) {
                String nomeArquivo = System.currentTimeMillis() + "_" + arquivo.getOriginalFilename();
                Path caminho = Paths.get(uploadDir).resolve(nomeArquivo);
                Files.createDirectories(caminho.getParent());
                arquivo.transferTo(caminho);

                Anexo anexo = new Anexo();
                anexo.setNomeArquivo(arquivo.getOriginalFilename());
                anexo.setCaminhoArquivo(caminho.toString());
                anexo.setReclamacao(reclamacao);

                anexoRepository.save(anexo);
                reclamacao.getAnexos().add(anexo);
            }
        }

        // --- Enviar email de notificação ao síndico ---
        Map<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("nome", usuario.getUsername());
        usuarioMap.put("email", usuario.getEmail());

        Map<String, Object> reclamacaoMap = new HashMap<>();
        reclamacaoMap.put("id", reclamacao.getId());
        reclamacaoMap.put("tipo", reclamacao.getTipo());
        reclamacaoMap.put("detalhes", reclamacao.getDetalhes());
        reclamacaoMap.put("status", reclamacao.getStatus().name());
        reclamacaoMap.put("dataHora", reclamacao.getDataCriacao()); // se tiver createdAt ou data de criação

        Map<String, Object> empresaMap = new HashMap<>();
        empresaMap.put("nome", empresa.getName());
        empresaMap.put("codigoPublico", empresa.getCodigoPublico());

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("usuario", usuarioMap);
        templateVariables.put("reclamacao", reclamacaoMap);
        templateVariables.put("empresa", empresaMap);

        User responsavel = empresa.getUsuarioResponsavel();
        String emailSindico = responsavel != null ? responsavel.getEmail() : null;

        if (emailSindico != null && !emailSindico.isEmpty()) {
            emailService.enviarEmailComTemplate(
                    emailSindico,
                    TipoTemplateEmail.NOVA_DENUNCIA,
                    templateVariables
            );
        }

        return reclamacaoRepository.save(reclamacao);
    }

    public ReclamacaoDTO toDto(Reclamacao reclamacao) {
        ReclamacaoDTO dto = new ReclamacaoDTO();
        dto.setId(reclamacao.getId());
        dto.setTipo(reclamacao.getTipo());
        dto.setDetalhes(reclamacao.getDetalhes());
        dto.setDataCriacao(reclamacao.getDataCriacao());
        dto.setStatus(reclamacao.getStatus());
        dto.setSolucao(reclamacao.getDescricaoSolucao());
        // Usuario simplificado
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(reclamacao.getUsuario().getId());
        usuarioDTO.setUsername(reclamacao.getUsuario().getUsername());
        usuarioDTO.setEmail(reclamacao.getUsuario().getEmail());
        usuarioDTO.setBloco(reclamacao.getUsuario().getBloco());
        usuarioDTO.setApartamento(reclamacao.getUsuario().getApartamento());
        usuarioDTO.setTipoPerfil(reclamacao.getUsuario().getTipo() != null ? reclamacao.getUsuario().getTipo().getName() : null);
        dto.setUsuario(usuarioDTO);

        // Empresa simplificada
        EmpresaDTO empresaDTO = new EmpresaDTO();
        empresaDTO.setId(reclamacao.getEmpresa().getId());
        empresaDTO.setName(reclamacao.getEmpresa().getName());
        empresaDTO.setCnpj(reclamacao.getEmpresa().getCnpj());
        dto.setEmpresa(empresaDTO);

        // Anexos simplificados
        List<AnexoDTO> anexosDTO = reclamacao.getAnexos().stream().map(anexo -> {
            AnexoDTO a = new AnexoDTO();
            a.setId(anexo.getId());
            a.setNomeArquivo(anexo.getNomeArquivo());
            return a;
        }).collect(Collectors.toList());
        dto.setAnexos(anexosDTO);

        return dto;
    }

    @Transactional(readOnly = true)
    public Page<ReclamacaoDTO> listarComFiltro(ReclamacaoFiltroDTO filtro, Pageable pageable) {
        Specification<Reclamacao> spec = ReclamacaoSpecification.filtro(filtro);
        Page<Reclamacao> page = reclamacaoRepository.findAll(spec, pageable);
        return page.map(this::toDto);
    }

    @Transactional
    public AcaoReclamacao adicionarAcao(Long reclamacaoId, AcaoReclamacaoDTO dto) {
        Reclamacao reclamacao = reclamacaoRepository.findById(reclamacaoId)
                .orElseThrow(() -> new ApiException.NotFoundException("Reclamação não encontrada"));

        AcaoReclamacao acao = new AcaoReclamacao();
        acao.setStatus(dto.getTipo());
        acao.setDescricao(dto.getDescricao());
        acao.setReclamacao(reclamacao);
        reclamacao.setDataAtualizacao(LocalDateTime.now());

        reclamacao.setStatus(dto.getTipo());
        if(dto.getTipo().equals(StatusReclamacao.SOLUCIONADA)){
            reclamacao.setDescricaoSolucao(dto.getDescricao());
            reclamacao.setDataResolucao(LocalDateTime.now());
        }
        Map<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("username", reclamacao.getUsuario().getUsername());
        usuarioMap.put("cpf", reclamacao.getUsuario().getCpf());
        usuarioMap.put("email", reclamacao.getUsuario().getEmail());

        Map<String, Object> acaoMap = new HashMap<>();
        acaoMap.put("status", dto.getTipo().name());
        acaoMap.put("descricao", dto.getDescricao());
        acaoMap.put("dataCriacao", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("usuario", usuarioMap);
        templateVariables.put("acao", acaoMap);
        templateVariables.put("reclamacao", reclamacao);
        templateVariables.put("empresaNome", reclamacao.getUsuario().getEmpresa().getName());
        templateVariables.put("empresaCnpj", reclamacao.getUsuario().getEmpresa().getCnpj());

        emailService.enviarEmailComTemplate(
                reclamacao.getUsuario().getEmail(),
                TipoTemplateEmail.ATUALIZACAO,
                templateVariables
        );

        return acaoReclamacaoRepository.save(acao);
    }

    @Transactional
    public Reclamacao solucionarReclamacao(Long reclamacaoId, SolucaoReclamacaoDTO dto) {
        Reclamacao reclamacao = reclamacaoRepository.findById(reclamacaoId)
                .orElseThrow(() -> new ApiException.NotFoundException("Reclamação não encontrada"));

        reclamacao.setDescricaoSolucao(dto.getDescricao());
        reclamacao.setDataResolucao(LocalDateTime.now());
        reclamacao.setStatus(StatusReclamacao.SOLUCIONADA);

        AcaoReclamacao acao = new AcaoReclamacao();
        acao.setStatus(StatusReclamacao.SOLUCIONADA);
        acao.setDescricao(dto.getDescricao());
        acao.setReclamacao(reclamacao);
        acaoReclamacaoRepository.save(acao);

        Map<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("username", reclamacao.getUsuario().getUsername());
        usuarioMap.put("cpf", reclamacao.getUsuario().getCpf());
        usuarioMap.put("email", reclamacao.getUsuario().getEmail());

        Map<String, Object> acaoMap = new HashMap<>();
        acaoMap.put("status", StatusReclamacao.SOLUCIONADA);
        acaoMap.put("descricao", dto.getDescricao());
        acaoMap.put("dataCriacao", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("usuario", usuarioMap);
        templateVariables.put("acao", acaoMap);
        templateVariables.put("reclamacao", reclamacao);
        templateVariables.put("empresaNome", reclamacao.getUsuario().getEmpresa().getName());
        templateVariables.put("empresaCnpj", reclamacao.getUsuario().getEmpresa().getCnpj());

        emailService.enviarEmailComTemplate(
                reclamacao.getUsuario().getEmail(),
                TipoTemplateEmail.ATUALIZACAO,
                templateVariables
        );
        return reclamacaoRepository.save(reclamacao);
    }

    public List<AcaoReclamacao> listarTodas() {
        return acaoReclamacaoRepository.findAll();
    }

    public List<AcaoReclamacao> listarPorReclamacaoId(Long reclamacaoId) {
        return acaoReclamacaoRepository.findByReclamacaoIdOrderByIdAsc(reclamacaoId);
    }

    public byte[] gerarZipAnexos(Long idReclamacao) {
        Reclamacao reclamacao = reclamacaoRepository.findById(idReclamacao)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reclamação não encontrada"));

        List<Anexo> anexos = reclamacao.getAnexos();

        if (anexos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Reclamação não possui anexos");
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (Anexo anexo : anexos) {
                File arquivo = new File(anexo.getCaminhoArquivo());

                if (!arquivo.exists()) {
                    // Se desejar ignorar arquivos ausentes, use continue ao invés de lançar exceção
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Arquivo não encontrado: " + anexo.getNomeArquivo());
                }

                zos.putNextEntry(new ZipEntry(anexo.getNomeArquivo()));
                Files.copy(arquivo.toPath(), zos);
                zos.closeEntry();
            }

            zos.finish();
            return baos.toByteArray();

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao gerar ZIP", e);
        }
    }

    //relatorio
    public List<ReclamacaoResumoDTO> buscarReclamacoesPorPeriodo(LocalDate inicio, LocalDate fim) {
        LocalDateTime start = inicio.atStartOfDay();
        LocalDateTime end = fim.atTime(23, 59, 59);

        List<Reclamacao> reclamacoes = reclamacaoRepository.findByDataCriacaoBetween(start, end);
        return reclamacoes.stream()
                .map(this::toResumoDTO)
                .collect(Collectors.toList());
    }

    private List<Reclamacao> listarReclamacoesPorPeriodo(LocalDate inicio, LocalDate fim, Long empresaId) {
        LocalDateTime start = inicio.atStartOfDay();
        LocalDateTime end = fim.atTime(23, 59, 59);
        if (empresaId != null) {
            return reclamacaoRepository.findByEmpresa_IdAndDataCriacaoBetween(empresaId, start, end);
        }
        return reclamacaoRepository.findByDataCriacaoBetween(start, end);
    }

    public long contarTotalReclamacoes(LocalDate inicio, LocalDate fim) {
        return contarTotalReclamacoes(inicio, fim, null);
    }

    public long contarTotalReclamacoes(LocalDate inicio, LocalDate fim, Long empresaId) {
        return listarReclamacoesPorPeriodo(inicio, fim, empresaId).size();
    }

    public long contarPendentes(LocalDate inicio, LocalDate fim) {
        return contarPendentes(inicio, fim, null);
    }

    public long contarPendentes(LocalDate inicio, LocalDate fim, Long empresaId) {
        return listarReclamacoesPorPeriodo(inicio, fim, empresaId).stream()
                .filter(r -> r.getStatus() == StatusReclamacao.EM_ANALISE)
                .count();
    }

    public double calcularTempoMedioResolucao(LocalDate inicio, LocalDate fim) {
        return calcularTempoMedioResolucao(inicio, fim, null);
    }

    public double calcularTempoMedioResolucao(LocalDate inicio, LocalDate fim, Long empresaId) {
        List<Reclamacao> resolvidas = listarReclamacoesPorPeriodo(inicio, fim, empresaId).stream()
                .filter(r -> r.getStatus() == StatusReclamacao.SOLUCIONADA && r.getDataResolucao() != null)
                .collect(Collectors.toList());

        if (resolvidas.isEmpty()) return 0;

        double somaDias = resolvidas.stream()
                .mapToDouble(r -> ChronoUnit.DAYS.between(r.getDataCriacao(), r.getDataResolucao()))
                .sum();

        return somaDias / resolvidas.size();
    }

    public String unidadeComMaisReclamacoes(LocalDate inicio, LocalDate fim) {
        return unidadeComMaisReclamacoes(inicio, fim, null);
    }

    public String unidadeComMaisReclamacoes(LocalDate inicio, LocalDate fim, Long empresaId) {
        List<Reclamacao> reclamacoes = listarReclamacoesPorPeriodo(inicio, fim, empresaId);

        Map<String, Long> contagem = reclamacoes.stream()
                .collect(Collectors.groupingBy(
                        r -> {
                            String bloco = Optional.ofNullable(r.getUsuario().getBloco()).orElse("-");
                            String apt = Optional.ofNullable(r.getUsuario().getApartamento()).orElse("-");
                            return "Bloco " + bloco + " Apart. " + apt;
                        },
                        Collectors.counting()));

        return contagem.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey() + " (" + e.getValue() + ")")
                .orElse("-");
    }

    public List<ReclamacaoResumoDTO> ultimas10ReclamacoesAbertas() {
        return ultimas10ReclamacoesAbertas(null);
    }

    public List<ReclamacaoResumoDTO> ultimas10ReclamacoesAbertas(Long empresaId) {
        List<Reclamacao> list = empresaId != null
                ? reclamacaoRepository.findTop10ByEmpresa_IdAndStatusOrderByDataCriacaoDesc(empresaId, StatusReclamacao.EM_ANALISE)
                : reclamacaoRepository.findTop10ByStatusOrderByDataCriacaoDesc(StatusReclamacao.EM_ANALISE);
        return list.stream().map(this::toResumoDTO).collect(Collectors.toList());
    }

    public List<UnidadeQtdDTO> topUnidadesQueMaisReclamam(LocalDate inicio, LocalDate fim) {
        return topUnidadesQueMaisReclamam(inicio, fim, null);
    }

    public List<UnidadeQtdDTO> topUnidadesQueMaisReclamam(LocalDate inicio, LocalDate fim, Long empresaId) {
        List<Reclamacao> reclamacoes = listarReclamacoesPorPeriodo(inicio, fim, empresaId);

        Map<String, Long> contagem = reclamacoes.stream()
                .collect(Collectors.groupingBy(
                        r -> {
                            String bloco = Optional.ofNullable(r.getUsuario().getBloco()).orElse("-");
                            String apt = Optional.ofNullable(r.getUsuario().getApartamento()).orElse("-");
                            return "Bloco " + bloco + " Apart. " + apt;
                        },
                        Collectors.counting()));

        return contagem.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(e -> new UnidadeQtdDTO(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public List<UnidadeQtdDTO> topUnidadesMaisReclamadas(LocalDate inicio, LocalDate fim) {
        return topUnidadesMaisReclamadas(inicio, fim, null);
    }

    public List<UnidadeQtdDTO> topUnidadesMaisReclamadas(LocalDate inicio, LocalDate fim, Long empresaId) {
        return topUnidadesQueMaisReclamam(inicio, fim, empresaId);
    }

    public Optional<ReclamacaoDTO> buscarDetalhesPorId(Long id) {
        return reclamacaoRepository.findById(id)
                .map(this::toDto);
    }

    private ReclamacaoResumoDTO toResumoDTO(Reclamacao r) {
        String bloco = Optional.ofNullable(r.getUsuario().getBloco()).orElse("-");
        String apt = Optional.ofNullable(r.getUsuario().getApartamento()).orElse("-");
        String unidade = bloco + " - " + apt;

        return new ReclamacaoResumoDTO(
                unidade,
                r.getTipo(),
                r.getStatus(),
                r.getDataCriacao(),
                r.getDataAtualizacao(),
                r.getDetalhes()
        );
    }

}

