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
        dto.setCpf(user.getCpf());
        dto.setTelefone(user.getTelefone());
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
            empresaDTO.setCnpj(user.getEmpresa().getCnpj());
            empresaDTO.setCodigoPublico(user.getEmpresa().getCodigoPublico());

            dto.setEmpresa(empresaDTO);
        }

        return dto;
    }
}

