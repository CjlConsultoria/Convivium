package br.com.convivium.service;

import br.com.convivium.dto.response.LicencaDTO;
import br.com.convivium.dto.response.LicencaDetalhadaDTO;
import br.com.convivium.dto.response.LicencaStatusDTO;
import br.com.convivium.entity.Empresa;
import br.com.convivium.entity.Licenca;
import br.com.convivium.entity.User;
import br.com.convivium.entity.specification.LicencaSpecification;
import br.com.convivium.exception.ApiException;
import br.com.convivium.repository.EmpresaRepository;
import br.com.convivium.repository.LicencaRepository;
import br.com.convivium.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class LicencaService {

    @Autowired
    private LicencaRepository licencaRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private UserRepository userRepository;

    public LicencaDTO criarOuAtualizar(LicencaDTO dto) {
        Empresa empresa = empresaRepository.findById(dto.getEmpresaId())
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));

        Licenca licenca = (dto.getId() != null)
                ? licencaRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Licença não encontrada"))
                : new Licenca();

        licenca.setEmpresa(empresa);
        licenca.setDataInicio(dto.getDataInicio());
        licenca.setDataFim(dto.getDataFim());
        licenca.setTipo(dto.getTipo());
        licenca.setLimiteUsuarios(dto.getLimiteUsuarios());
        licenca.setAtiva(dto.getAtiva() != null ? dto.getAtiva() : true);

        licenca = licencaRepository.save(licenca);
        dto.setId(licenca.getId());
        return dto;
    }

    public void desativarLicenca(Long id) {
        Licenca licenca = licencaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Licença não encontrada"));
        licenca.setAtiva(false);
        licencaRepository.save(licenca);
    }

    public boolean isLicencaValida(Long empresaId) {
        return licencaRepository.findTopByEmpresaIdAndAtivaTrueOrderByDataFimDesc(empresaId)
                .map(Licenca::isValida)
                .orElse(false);
    }

    public boolean excedeuLimiteUsuarios(Long empresaId) {
        Licenca licenca = licencaRepository.findTopByEmpresaIdAndAtivaTrueOrderByDataFimDesc(empresaId)
                .orElseThrow(() -> new RuntimeException("Licença não encontrada"));

        long usuariosAtivos = userRepository.countByEmpresaIdAndAtivoTrue(empresaId);
        return usuariosAtivos > licenca.getLimiteUsuarios();
    }

    public LicencaStatusDTO getStatusLicenca(Long empresaId) {
        Licenca licenca = licencaRepository.findTopByEmpresaIdAndAtivaTrueOrderByDataFimDesc(empresaId)
                .orElseThrow(() -> new RuntimeException("Licença não encontrada"));

        LocalDate hoje = LocalDate.now();
        boolean valida = licenca.isValida();
        boolean expirada = hoje.isAfter(licenca.getDataFim());
        long diasRestantes = ChronoUnit.DAYS.between(hoje, licenca.getDataFim());

        boolean excedeu = excedeuLimiteUsuarios(empresaId);

        return new LicencaStatusDTO(valida, expirada, diasRestantes, excedeu);
    }

    public Page<LicencaDetalhadaDTO> buscarLicencasPaginadas(String empresaNome, String usuarioNome, String cpf, Pageable pageable) {
        Specification<Licenca> spec = LicencaSpecification.filter(empresaNome, usuarioNome, cpf);
        Page<Licenca> page = licencaRepository.findAll(spec, pageable);

        return page.map(licenca -> {
            LicencaDetalhadaDTO dto = new LicencaDetalhadaDTO();
            dto.setId(licenca.getId());
            dto.setDataInicio(licenca.getDataInicio());
            dto.setDataFim(licenca.getDataFim());
            dto.setTipo(licenca.getTipo());
            dto.setLimiteUsuarios(licenca.getLimiteUsuarios());
            dto.setAtiva(licenca.getAtiva());

            Empresa empresa = licenca.getEmpresa();
            dto.setEmpresaId(empresa.getId());
            dto.setEmpresaNome(empresa.getName());
            dto.setEmpresaCnpj(empresa.getCnpj());

            if (empresa.getUsuarioResponsavel() != null) {
                User user = empresa.getUsuarioResponsavel();
                dto.setResponsavelId(user.getId());
                dto.setResponsavelNome(user.getUsername());
                dto.setResponsavelCpf(user.getCpf());
                dto.setPerfil(user.getRole().getName());
            }

            dto.setValidadeExpirada(licenca.getDataFim().isBefore(LocalDate.now()));
            dto.setDiasRestantes(ChronoUnit.DAYS.between(LocalDate.now(), licenca.getDataFim()));

            return dto;
        });
    }
}



