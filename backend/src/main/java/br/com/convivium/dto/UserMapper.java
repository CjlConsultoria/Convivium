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
        dto.setAtivo(user.getAtivo());
        dto.setSobrenome(user.getSobrenome());
        dto.setAlerta(user.getAlerta());
        dto.setBloco(user.getBloco());
        dto.setApartamento(user.getApartamento());
        dto.setRole(user.getRole() != null ? user.getRole().getName() : null);
        dto.setTipo(user.getTipo() != null ? user.getTipo().getName() : null);
        
        if (user.getEmpresa() != null) {
            EmpresaResumoDTO empresaDTO = new EmpresaResumoDTO();
            empresaDTO.setId(user.getEmpresa().getId());
            empresaDTO.setNome(user.getEmpresa().getName());
            empresaDTO.setCnpj(user.getEmpresa().getCnpj());
            empresaDTO.setCodigoPublico(user.getEmpresa().getCodigoPublico());
            dto.setEmpresa(empresaDTO);
        }
        
        // Dados sensíveis apenas para o próprio usuário
        UserResponseAuthDTO.UserSensitiveDataDTO dadosSensiveis = new UserResponseAuthDTO.UserSensitiveDataDTO();
        dadosSensiveis.setCpf(user.getCpf());
        dadosSensiveis.setTelefone(user.getTelefone());
        dadosSensiveis.setGenero(user.getGenero());
        dadosSensiveis.setCep(user.getCep());
        dadosSensiveis.setLogradouro(user.getLogradouro());
        dadosSensiveis.setCidade(user.getCidade());
        dadosSensiveis.setEstado(user.getEstado());
        dadosSensiveis.setBairro(user.getBairro());
        dadosSensiveis.setNumero(user.getNumero());
        dadosSensiveis.setComplemento(user.getComplemento());
        dadosSensiveis.setVagaCarro(user.getVagaCarro());
        dadosSensiveis.setVagaMoto(user.getVagaMoto());
        dto.setDadosCompletos(dadosSensiveis);

        return dto;
    }
    
    public static UserResponseDTO toPublicDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAtivo(user.getAtivo());
        dto.setSobrenome(user.getSobrenome());
        dto.setAlerta(user.getAlerta());
        dto.setBloco(user.getBloco());
        dto.setApartamento(user.getApartamento());
        dto.setRole(user.getRole() != null ? user.getRole().getName() : null);
        dto.setTipo(user.getTipo() != null ? user.getTipo().getName() : null);
        dto.setEmpresa(user.getEmpresa() != null ? user.getEmpresa().getName() : null);
        
        // Dados sensíveis não são incluídos por padrão
        return dto;
    }
    
    public static UserResponseDTO toAdminDTO(User user) {
        UserResponseDTO dto = toPublicDTO(user);
        
        // Apenas administradores podem ver alguns dados sensíveis específicos
        UserResponseDTO.UserSensitiveDataDTO dadosSensiveis = new UserResponseDTO.UserSensitiveDataDTO();
        dadosSensiveis.setCpf(maskCpf(user.getCpf()));
        dadosSensiveis.setTelefone(maskPhone(user.getTelefone()));
        dto.setDadosSensiveis(dadosSensiveis);
        
        return dto;
    }
    
    private static String maskCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) return "***";
        return cpf.substring(0, 3) + "*****" + cpf.substring(8);
    }
    
    private static String maskPhone(String phone) {
        if (phone == null || phone.length() < 8) return "***";
        return phone.substring(0, 2) + "*****" + phone.substring(phone.length() - 2);
    }
}