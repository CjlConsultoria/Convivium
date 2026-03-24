package br.com.convivium.dto;

import br.com.convivium.dto.response.EmpresaResumoDTO;
import br.com.convivium.dto.response.UserResponseAuthDTO;
import br.com.convivium.dto.response.UserResponseDTO;
import br.com.convivium.entity.User;

public class UserMapper {

    public static UserResponseAuthDTO toDTO(User user) {
        UserResponseAuthDTO dto = new UserResponseAuthDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCpf(maskCpf(user.getCpf()));
        dto.setTelefone(maskTelefone(user.getTelefone()));
        dto.setAtivo(user.getAtivo());
        dto.setSobrenome(user.getSobrenome());
        dto.setGenero(user.getGenero());
        dto.setCep(user.getCep());
        dto.setLogradouro(user.getLogradouro());
        dto.setCidade(user.getCidade());
        dto.setEstado(user.getEstado());
        dto.setBairro(user.getBairro());
        dto.setNumero(user.getNumero());
        dto.setComplemento(user.getComplemento());
        dto.setAlerta(user.getAlerta());
        dto.setBloco(user.getBloco());
        dto.setApartamento(user.getApartamento());
        dto.setVagaCarro(user.getVagaCarro());
        dto.setVagaMoto(user.getVagaMoto());
        dto.setRole(user.getRole().getName());
        dto.setTipo(user.getTipo().getName());
        if (user.getEmpresa() != null) {
            EmpresaResumoDTO empresaDTO = new EmpresaResumoDTO();
            empresaDTO.setId(user.getEmpresa().getId());
            empresaDTO.setNome(user.getEmpresa().getName());
            empresaDTO.setCnpj(maskCnpj(user.getEmpresa().getCnpj()));
            empresaDTO.setCodigoPublico(user.getEmpresa().getCodigoPublico());

            dto.setEmpresa(empresaDTO);
        }

        return dto;
    }

    private static String maskCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return cpf;
        }
        return cpf.substring(0, 3) + "*****" + cpf.substring(8);
    }

    private static String maskCnpj(String cnpj) {
        if (cnpj == null || cnpj.length() < 8) {
            return cnpj;
        }
        return cnpj.substring(0, 2) + "******" + cnpj.substring(cnpj.length() - 2);
    }

    private static String maskTelefone(String telefone) {
        if (telefone == null || telefone.length() < 8) {
            return telefone;
        }
        return telefone.substring(0, 2) + "****" + telefone.substring(telefone.length() - 2);
    }
}