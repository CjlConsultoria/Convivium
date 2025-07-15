package br.com.convivium.service;

import br.com.caelum.stella.validation.CNPJValidator;
import br.com.caelum.stella.validation.InvalidStateException;
import br.com.convivium.dto.request.EmpresaCreateDTO;
import br.com.convivium.entity.Empresa;
import br.com.convivium.entity.specification.EmpresaSpecification;
import br.com.convivium.exception.EmpresaComUsuariosException;
import br.com.convivium.repository.EmpresaRepository;
import br.com.convivium.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final UserRepository usuarioRepository; // supondo que exista
    public EmpresaService(EmpresaRepository empresaRepository, UserRepository usuarioRepository) {
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
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

    public Page<Empresa> buscarEmpresasAll(String nome, String cnpj, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Empresa> spec = EmpresaSpecification.filterByNomeOrCnpj(nome, cnpj);
        return empresaRepository.findAll(spec, pageable);
    }


    public Empresa atualizarEndereco(Long id, Empresa enderecoAtualizado) {
        return empresaRepository.findById(id).map(empresa -> {
            // Não permite alterar nome nem cnpj
            empresa.setCep(enderecoAtualizado.getCep());
            empresa.setLogradouro(enderecoAtualizado.getLogradouro());
            empresa.setNumero(enderecoAtualizado.getNumero());
            empresa.setComplemento(enderecoAtualizado.getComplemento());
            empresa.setBairro(enderecoAtualizado.getBairro());
            empresa.setCidade(enderecoAtualizado.getCidade());
            empresa.setEstado(enderecoAtualizado.getEstado());
            return empresaRepository.save(empresa);
        }).orElse(null);
    }

    public boolean excluirEmpresa(Long id) {
        if (!empresaRepository.existsById(id)) {
            return false;
        }
        // Verificar se existe usuário associado à empresa
        boolean possuiUsuarios = usuarioRepository.existsByEmpresaId(id);
        if (possuiUsuarios) {
            throw new EmpresaComUsuariosException(
                    "Não é possível excluir a empresa pois existem usuários associados a ela. Apague os usuários primeiro."
            );
        }
        empresaRepository.deleteById(id);
        return true;
    }

}
