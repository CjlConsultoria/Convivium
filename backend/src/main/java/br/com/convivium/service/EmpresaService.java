package br.com.convivium.service;

import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import br.com.convivium.entity.Empresa;
import br.com.convivium.repository.EmpresaRepository;
    import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public List<Empresa> buscarEmpresasAll() {
        return empresaRepository.findAll();
    }

    public Empresa buscarPorCodigoPublico(String codigoPublico) {
        return empresaRepository.findByCodigoPublico(codigoPublico);
    }

    // Salva empresa e gera código público slug + id
    public Empresa salvarEmpresa(Empresa empresa) {
        validarCamposObrigatorios(empresa);
        validarCnpjUnico(empresa.getCnpj());
        validarFormatoCnpj(empresa.getCnpj());

        // Salva primeiro para garantir ID
        if (empresa.getId() == null) {
            empresa = empresaRepository.save(empresa);
        }

        // Gera código público e salva novamente
        empresa.setCodigoPublico(gerarCodigoPublico(empresa));
        return empresaRepository.save(empresa);
    }

    private void validarCamposObrigatorios(Empresa empresa) {
        if (!StringUtils.hasText(empresa.getName())) {
            throw new IllegalArgumentException("Nome da empresa é obrigatório.");
        }
        if (!StringUtils.hasText(empresa.getCnpj())) {
            throw new IllegalArgumentException("CNPJ é obrigatório.");
        }
        if (!StringUtils.hasText(empresa.getEstado())) {
            throw new IllegalArgumentException("Estado é obrigatório.");
        }
        if (!StringUtils.hasText(empresa.getCidade())) {
            throw new IllegalArgumentException("Cidade é obrigatória.");
        }
        if (!StringUtils.hasText(empresa.getCep())) {
            throw new IllegalArgumentException("CEP é obrigatório.");
        }
    }

    private void validarCnpjUnico(String cnpj) {
        if (empresaRepository.existsByCnpj(cnpj)) {
            throw new IllegalArgumentException("CNPJ já cadastrado.");
        }
    }

    public void validarFormatoCnpj(String cnpj) {
        if (cnpj == null || cnpj.trim().isEmpty()) {
            throw new IllegalArgumentException("CNPJ é obrigatório.");
        }
        String cnpjLimpo = cnpj.replaceAll("[^\\d]", "");
        if (cnpjLimpo.length() != 14) {
            throw new IllegalArgumentException("CNPJ deve conter 14 dígitos.");
        }
    }



    private String gerarCodigoPublico(Empresa empresa) {
        String nomeSlug = empresa.getName()
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", ""); // remove traço no começo/fim
        return nomeSlug + "-" + empresa.getId();
    }
}
