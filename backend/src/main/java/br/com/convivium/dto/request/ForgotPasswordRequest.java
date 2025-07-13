package br.com.convivium.dto.request;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String emailCpf;

    public String getEmailCpf() {
        return emailCpf;
    }

    public void setEmailCpf(String emailCpf) {
        this.emailCpf = emailCpf;
    }
}

