package br.com.convivium.service;

import br.com.convivium.entity.enums.TipoTemplateEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.from}")
    private String from;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void enviarEmailComTemplate(String to, TipoTemplateEmail tipo, Map<String, Object> variables) {
        // Garante que 'variables' seja mutável
        Map<String, Object> varsMutaveis = new HashMap<>(variables);

        if (tipo.equals(TipoTemplateEmail.ESQUECEU_SENHA)) {
            String token = (String) varsMutaveis.get("token");
            String url = frontendUrl + "/redefinir-senha?token=" + token;
            varsMutaveis.put("url", url); // opcional

            // Insere a URL também dentro do mapa "usuario"
            Object usuarioObj = varsMutaveis.get("usuario");
            if (usuarioObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> usuarioMap = new HashMap<>((Map<String, Object>) usuarioObj);
                usuarioMap.put("url", url);
                varsMutaveis.put("usuario", usuarioMap); // atualiza o mapa principal com o novo mapa mutável
            }
        }
        if (tipo.equals(TipoTemplateEmail.BEM_VINDO)) {
            String token = (String) varsMutaveis.get("token");
            String empresaId = (String) varsMutaveis.get("empresaId");
            String empresaNome = (String) varsMutaveis.get("empresaNome");
            String url = frontendUrl + "/empresa/" + empresaNome + "/cadastro-condominio/" + empresaId + "?token=" + token;
            varsMutaveis.put("url", url);

            Object usuarioObj = varsMutaveis.get("usuario");
            if (usuarioObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> usuarioMap = new HashMap<>((Map<String, Object>) usuarioObj);
                usuarioMap.put("url", url);
                varsMutaveis.put("usuario", usuarioMap);
            }
        }
        enviarTemplate(to, tipo.getTitulo(), tipo.getTemplateNome(), varsMutaveis);
    }



    public void enviarTemplate(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            Context context = new Context();
            context.setVariables(variables);

            String html = templateEngine.process(templateName, context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            //helper.setTo("watchcarsystem@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

