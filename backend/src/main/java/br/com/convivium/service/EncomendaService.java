package br.com.convivium.service;

import br.com.convivium.dto.request.EncomendaRegistroRequest;
import br.com.convivium.dto.request.EncomendaRetiradaRequest;
import br.com.convivium.dto.response.EncomendaDTO;
import br.com.convivium.entity.Empresa;
import br.com.convivium.entity.Encomenda;
import br.com.convivium.entity.User;
import br.com.convivium.entity.enums.StatusEncomenda;
import br.com.convivium.exception.ApiException;
import br.com.convivium.repository.EncomendaRepository;
import br.com.convivium.repository.EmpresaRepository;
import br.com.convivium.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class EncomendaService {

    private static final String TOKEN_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int TOKEN_LENGTH = 6;
    private static final Random RND = new Random();

    private final EncomendaRepository encomendaRepository;
    private final UserRepository userRepository;
    private final EmpresaRepository empresaRepository;

    public EncomendaService(EncomendaRepository encomendaRepository,
                            UserRepository userRepository,
                            EmpresaRepository empresaRepository) {
        this.encomendaRepository = encomendaRepository;
        this.userRepository = userRepository;
        this.empresaRepository = empresaRepository;
    }

    /** Gera código único de retirada (6 caracteres alfanuméricos). */
    public String gerarCodigoRetirada(Long empresaId) {
        for (int i = 0; i < 50; i++) {
            StringBuilder sb = new StringBuilder(TOKEN_LENGTH);
            for (int j = 0; j < TOKEN_LENGTH; j++) {
                sb.append(TOKEN_CHARS.charAt(RND.nextInt(TOKEN_CHARS.length())));
            }
            String codigo = sb.toString();
            if (encomendaRepository.findByCodigoRetiradaAndEmpresaId(codigo, empresaId).isEmpty()) {
                return codigo;
            }
        }
        return UUID.randomUUID().toString().replace("-", "").substring(0, TOKEN_LENGTH).toUpperCase();
    }

    @Transactional
    public Encomenda registrar(EncomendaRegistroRequest request, Long usuarioRegistradorId) {
        User morador = userRepository.findById(request.getMoradorId())
                .orElseThrow(() -> new ApiException.NotFoundException("Morador não encontrado"));
        Empresa empresa = empresaRepository.findById(request.getEmpresaId())
                .orElseThrow(() -> new ApiException.NotFoundException("Condomínio não encontrado"));
        User registrador = userRepository.findById(usuarioRegistradorId)
                .orElseThrow(() -> new ApiException.NotFoundException("Usuário registrador não encontrado"));

        if (morador.getEmpresa() == null || !morador.getEmpresa().getId().equals(empresa.getId())) {
            throw new ApiException.ForbiddenException("Morador não pertence a este condomínio.");
        }
        if (registrador.getEmpresa() == null || !registrador.getEmpresa().getId().equals(empresa.getId())) {
            throw new ApiException.ForbiddenException("Registrador não pertence a este condomínio.");
        }

        Encomenda encomenda = new Encomenda();
        encomenda.setMorador(morador);
        encomenda.setEmpresa(empresa);
        encomenda.setRegistradoPor(registrador);
        encomenda.setDescricao(request.getDescricao());
        encomenda.setStatus(StatusEncomenda.DISPONIVEL);
        encomenda.setCodigoRetirada(gerarCodigoRetirada(empresa.getId()));
        return encomendaRepository.save(encomenda);
    }

    @Transactional
    public Encomenda marcarRetirada(EncomendaRetiradaRequest request, Long empresaId, Long usuarioValidadorId) {
        Encomenda encomenda = encomendaRepository.findByCodigoRetiradaAndEmpresaId(request.getCodigoRetirada(), empresaId)
                .orElseThrow(() -> new ApiException.NotFoundException("Encomenda não encontrada para este código e condomínio."));
        if (encomenda.getStatus() == StatusEncomenda.RETIRADA) {
            throw new ApiException.BadRequestException("Encomenda já foi retirada.");
        }
        User validador = userRepository.findById(usuarioValidadorId)
                .orElseThrow(() -> new ApiException.NotFoundException("Usuário não encontrado"));
        encomenda.setStatus(StatusEncomenda.RETIRADA);
        encomenda.setDataRetirada(LocalDateTime.now());
        encomenda.setRetiradaPor(validador);
        return encomendaRepository.save(encomenda);
    }

    @Transactional(readOnly = true)
    public Page<EncomendaDTO> listarPorEmpresa(Long empresaId, Pageable pageable) {
        return encomendaRepository.findByEmpresaId(empresaId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Page<EncomendaDTO> listarPorMorador(Long moradorId, Long empresaId, Pageable pageable) {
        return encomendaRepository.findByMoradorIdAndEmpresaId(moradorId, empresaId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<EncomendaDTO> buscarPorCodigo(String codigoRetirada, Long empresaId) {
        return encomendaRepository.findByCodigoRetiradaAndEmpresaId(codigoRetirada, empresaId).map(this::toDto);
    }

    public EncomendaDTO toDto(Encomenda e) {
        EncomendaDTO dto = new EncomendaDTO();
        dto.setId(e.getId());
        dto.setCodigoRetirada(e.getCodigoRetirada());
        dto.setStatus(e.getStatus());
        dto.setDescricao(e.getDescricao());
        dto.setDataRecebimento(e.getDataRecebimento());
        dto.setDataRetirada(e.getDataRetirada());
        dto.setMoradorId(e.getMorador() != null ? e.getMorador().getId() : null);
        dto.setMoradorNome(e.getMorador() != null ? e.getMorador().getUsername() + " " + e.getMorador().getSobrenome() : null);
        dto.setEmpresaId(e.getEmpresa() != null ? e.getEmpresa().getId() : null);
        dto.setEmpresaNome(e.getEmpresa() != null ? e.getEmpresa().getName() : null);
        dto.setRegistradoPorId(e.getRegistradoPor() != null ? e.getRegistradoPor().getId() : null);
        dto.setRegistradoPorNome(e.getRegistradoPor() != null ? e.getRegistradoPor().getUsername() : null);
        dto.setRetiradaPorId(e.getRetiradaPor() != null ? e.getRetiradaPor().getId() : null);
        dto.setRetiradaPorNome(e.getRetiradaPor() != null ? e.getRetiradaPor().getUsername() : null);
        return dto;
    }
}
