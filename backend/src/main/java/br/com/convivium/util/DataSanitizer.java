package br.com.convivium.util;

import org.springframework.stereotype.Component;

@Component
public class DataSanitizer {

    public String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 11) {
            return "***.***.***-**";
        }
        return cpf.substring(0, 3) + ".***.***-" + cpf.substring(9);
    }

    public String maskCnpj(String cnpj) {
        if (cnpj == null || cnpj.length() < 14) {
            return "**.***.***/****-**";
        }
        return cnpj.substring(0, 2) + ".***.***/****-" + cnpj.substring(12);
    }

    public String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***@***.com";
        }
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        
        String maskedUsername = username.length() > 2 ? 
            username.substring(0, 2) + "***" : "***";
        
        return maskedUsername + "@" + domain;
    }

    public String removeSpecialChars(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("[<>\"'&;]", "");
    }
}