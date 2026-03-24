package br.com.convivium.service;

import br.com.convivium.entity.User;
import br.com.convivium.entity.UserToken;
import br.com.convivium.entity.enums.TipoTemplateEmail;
import br.com.convivium.entity.enums.TipoToken;
import br.com.convivium.repository.UserTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TwoFactorService {

    private final UserTokenRepository userTokenRepository;
    private final EmailService emailService;
    private static final Random RANDOM = new Random();

    public TwoFactorService(UserTokenRepository userTokenRepository, EmailService emailService) {
        this.userTokenRepository = userTokenRepository;
        this.emailService = emailService;
    }

    public String generateTwoFactorCode(User user, String action) {
        String code = String.format("%06d", RANDOM.nextInt(1000000));
        
        UserToken token = new UserToken();
        token.setToken(code);
        token.setUser(user);
        token.setTipo(TipoToken.TWO_FACTOR_AUTH);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        token.setUsed(false);
        
        userTokenRepository.save(token);
        
        // Enviar código por email
        Map<String, Object> variables = new HashMap<>();
        variables.put("usuario", Map.of("nome", user.getUsername()));
        variables.put("codigo", code);
        variables.put("acao", action);
        variables.put("validade", "5 minutos");
        
        emailService.enviarTemplate(
            user.getEmail(),
            "Código de Verificação 2FA - Encomendas",
            "two-factor-code",
            variables
        );
        
        return code;
    }

    public boolean validateTwoFactorCode(User user, String code) {
        Optional<UserToken> tokenOpt = userTokenRepository.findByTokenAndTipo(code, TipoToken.TWO_FACTOR_AUTH);
        
        if (tokenOpt.isEmpty()) {
            return false;
        }
        
        UserToken token = tokenOpt.get();
        
        if (token.isUsed() || token.getExpiryDate().isBefore(LocalDateTime.now()) || 
            !token.getUser().getId().equals(user.getId())) {
            return false;
        }
        
        token.setUsed(true);
        userTokenRepository.save(token);
        
        return true;
    }

    public String generateEncomendaToken(User user, String action) {
        String token = UUID.randomUUID().toString();
        
        UserToken userToken = new UserToken();
        userToken.setToken(token);
        userToken.setUser(user);
        userToken.setTipo(TipoToken.TWO_FACTOR_AUTH);
        userToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));
        userToken.setUsed(false);
        
        userTokenRepository.save(userToken);
        
        return token;
    }

    public boolean validateEncomendaToken(String token) {
        Optional<UserToken> tokenOpt = userTokenRepository.findByTokenAndTipo(token, TipoToken.TWO_FACTOR_AUTH);
        
        if (tokenOpt.isEmpty()) {
            return false;
        }
        
        UserToken userToken = tokenOpt.get();
        
        if (userToken.isUsed() || userToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }
        
        userToken.setUsed(true);
        userTokenRepository.save(userToken);
        
        return true;
    }
}
