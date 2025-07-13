package br.com.convivium.service;

import br.com.convivium.dto.request.AcaoReclamacaoDTO;
import br.com.convivium.dto.request.ReclamacaoFiltroDTO;
import br.com.convivium.dto.request.SolucaoReclamacaoDTO;
import br.com.convivium.dto.response.AnexoDTO;
import br.com.convivium.dto.response.EmpresaDTO;
import br.com.convivium.dto.response.ReclamacaoDTO;
import br.com.convivium.dto.response.UsuarioDTO;
import br.com.convivium.entity.*;
import br.com.convivium.entity.enums.StatusReclamacao;
import br.com.convivium.entity.specification.ReclamacaoSpecification;
import br.com.convivium.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReclamacaoService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private final ReclamacaoRepository reclamacaoRepository;
    private final UserRepository userRepository;
    private final EmpresaRepository empresaRepository;
    private final AnexoRepository anexoRepository;
    private final AcaoReclamacaoRepository acaoReclamacaoRepository;

    public ReclamacaoService(ReclamacaoRepository reclamacaoRepository,
                             UserRepository userRepository,
                             EmpresaRepository empresaRepository,
                             AnexoRepository anexoRepository, AcaoReclamacaoRepository acaoReclamacaoRepository) {
        this.reclamacaoRepository = reclamacaoRepository;
        this.userRepository = userRepository;
        this.empresaRepository = empresaRepository;
        this.anexoRepository = anexoRepository;
        this.acaoReclamacaoRepository = acaoReclamacaoRepository;
    }

    public Reclamacao criarReclamacao(String tipo, String detalhes, Long usuarioId, Long empresaId, MultipartFile[] arquivos) throws IOException {
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

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

    public Page<ReclamacaoDTO> listarComFiltro(ReclamacaoFiltroDTO filtro, Pageable pageable) {
        Specification<Reclamacao> spec = ReclamacaoSpecification.filtro(filtro);
        Page<Reclamacao> page = reclamacaoRepository.findAll(spec, pageable);

        return page.map(this::toDto); // converte para DTO
    }

    @Transactional
    public AcaoReclamacao adicionarAcao(Long reclamacaoId, AcaoReclamacaoDTO dto) {
        Reclamacao reclamacao = reclamacaoRepository.findById(reclamacaoId)
                .orElseThrow(() -> new RuntimeException("Reclamação não encontrada"));

        AcaoReclamacao acao = new AcaoReclamacao();
        acao.setStatus(dto.getTipo());
        acao.setDescricao(dto.getDescricao());
        acao.setReclamacao(reclamacao);

        reclamacao.setStatus(dto.getTipo());
        if(dto.getTipo().equals(StatusReclamacao.SOLUCIONADA)){
            reclamacao.setDescricaoSolucao(dto.getDescricao());
        }
        return acaoReclamacaoRepository.save(acao);
    }

    @Transactional
    public Reclamacao solucionarReclamacao(Long reclamacaoId, SolucaoReclamacaoDTO dto) {
        Reclamacao reclamacao = reclamacaoRepository.findById(reclamacaoId)
                .orElseThrow(() -> new RuntimeException("Reclamação não encontrada"));

        reclamacao.setDescricaoSolucao(dto.getDescricao());
        reclamacao.setStatus(StatusReclamacao.SOLUCIONADA);

        AcaoReclamacao acao = new AcaoReclamacao();
        acao.setStatus(StatusReclamacao.SOLUCIONADA);
        acao.setDescricao(dto.getDescricao());
        acao.setReclamacao(reclamacao);
        acaoReclamacaoRepository.save(acao);
        return reclamacaoRepository.save(reclamacao);
    }

    public List<AcaoReclamacao> listarTodas() {
        return acaoReclamacaoRepository.findAll();
    }

    public List<AcaoReclamacao> listarPorReclamacaoId(Long reclamacaoId) {
        return acaoReclamacaoRepository.findByReclamacaoIdOrderByIdAsc(reclamacaoId);
    }

}

