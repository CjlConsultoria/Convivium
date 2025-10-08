package br.com.convivium.service;

import br.com.convivium.dto.request.ChatRequest;
import br.com.convivium.dto.response.ChatResponse;
import br.com.convivium.entity.CondominioInfo;
import br.com.convivium.entity.Empresa;
import br.com.convivium.entity.Reclamacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private ReclamacaoService reclamacaoService;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public ChatResponse processMessage(ChatRequest request) {
        String msg = request.getMessage().toLowerCase();
        Long condominioId = request.getCondominioId();
        Long usuarioId = request.getUsuarioId();

        try {
            Empresa condominio = empresaService.validarOuBuscar(condominioId);
            CondominioInfo info = condominio.getInfoCondominio();

            // ===== Login =====
            if (matchesLogin(msg)) {
                return new ChatResponse("Clique aqui para acessar o sistema: http://meucondominio.com/login");
            }

            // ===== Reclamações =====
            if (matchesReclamacao(msg)) {
                String tipo = detectTipoReclamacao(msg);
                Reclamacao r = reclamacaoService.criarReclamacao(tipo, request.getMessage(), usuarioId, condominio.getId(), null);
                return new ChatResponse("Sua reclamação foi registrada com sucesso! ID: " + r.getId());
            }

            // ===== Áreas do condomínio =====
            if (matchesPiscina(msg)) return respostaFlex(info, "piscina", info != null ? info.getHorarioPiscina() : null, condominio.getName(), msg);
            if (matchesAcademia(msg)) return respostaFlex(info, "academia", info != null ? info.getHorarioAcademia() : null, condominio.getName(), msg);
            if (matchesChurrasqueira(msg)) return respostaFlex(info, "churrasqueira", info != null ? info.getHorarioChurrasco() : null, condominio.getName(), msg);
            if (matchesSalao(msg)) return respostaFlex(info, "salão de festas", info != null ? info.getHorarioSalaoFestas() : null, condominio.getName(), msg);
            if (matchesPortaria(msg)) return respostaFlex(info, "telefone da portaria", info != null ? info.getTelefonePortaria() : null, condominio.getName(), msg);
            if (matchesSindico(msg)) return respostaFlex(info, "contato do síndico", info != null ? info.getContatoSindico() : null, condominio.getName(), msg);
            if (matchesBarulho(msg)) return respostaFlex(info, "horário de barulho", info != null ? info.getHorarioBarulho() : null, condominio.getName(), msg);
            if (matchesQuadra(msg)) return respostaFlex(info, "horário da quadra", info != null ? info.getHorarioQuadra() : null, condominio.getName(), msg);
            if (matchesElevador(msg)) return respostaFlex(info, "horário do elevador", info != null ? info.getHorarioElevador() : null, condominio.getName(), msg);
            if (matchesPlayground(msg)) return respostaFlex(info, "horário do playground", info != null ? info.getHorarioPlayground() : null, condominio.getName(), msg);

            // ===== Perguntas gerais → Gemini =====
            String geminiReply = sendToGemini(msg);
            return new ChatResponse(simplificarRespostaGemini(geminiReply));

        } catch (IllegalArgumentException e) {
            return new ChatResponse("Condomínio inválido.");
        } catch (Exception e) {
            return new ChatResponse("Erro ao processar sua solicitação: " + e.getMessage());
        }
    }

    // === Detecção de intenções flexível ===
    private boolean matchesLogin(String msg) {
        return msg.contains("login") || msg.contains("entrar no sistema");
    }

    private boolean matchesReclamacao(String msg) {
        return msg.contains("denuncia") || msg.contains("reclamacao");
    }

    private boolean matchesPiscina(String msg) {
        return msg.contains("piscina") && (msg.contains("horário") || msg.contains("funciona") || msg.contains("abre") || msg.contains("até que horas"));
    }

    private boolean matchesAcademia(String msg) {
        return msg.contains("academia") && (msg.contains("horário") || msg.contains("funciona") || msg.contains("abre") || msg.contains("até que horas"));
    }

    private boolean matchesChurrasqueira(String msg) {
        return msg.contains("churrasqueira") && (msg.contains("horário") || msg.contains("funciona") || msg.contains("abre") || msg.contains("até que horas"));
    }

    private boolean matchesSalao(String msg) {
        return (msg.contains("salão") || msg.contains("salao")) && (msg.contains("horário") || msg.contains("funciona") || msg.contains("abre") || msg.contains("até que horas"));
    }

    private boolean matchesPortaria(String msg) {
        return msg.contains("portaria") && (msg.contains("telefone") || msg.contains("contato"));
    }

    private boolean matchesSindico(String msg) {
        return (msg.contains("síndico") || msg.contains("sindico")) && (msg.contains("telefone") || msg.contains("contato"));
    }

    private boolean matchesBarulho(String msg) {
        return msg.contains("barulho") || msg.contains("até que horas pode fazer barulho") || msg.contains("permitido");
    }

    private boolean matchesQuadra(String msg) {
        return msg.contains("quadra") && (msg.contains("horário") || msg.contains("funciona") || msg.contains("abre") || msg.contains("até que horas"));
    }

    private boolean matchesElevador(String msg) {
        return msg.contains("elevador") && (msg.contains("horário") || msg.contains("funciona") || msg.contains("abre") || msg.contains("até que horas"));
    }

    private boolean matchesPlayground(String msg) {
        return msg.contains("playground") && (msg.contains("horário") || msg.contains("funciona") || msg.contains("abre") || msg.contains("até que horas"));
    }

    private String detectTipoReclamacao(String msg) {
        if (msg.contains("barulho")) return "Barulho";
        if (msg.contains("limpeza")) return "Limpeza";
        if (msg.contains("manutenção")) return "Manutenção";
        return "Geral";
    }

    // === Resposta flexível ou fallback Gemini ===
    private ChatResponse respostaFlex(CondominioInfo info, String campoNome, String valorCampo, String condominioName, String userMessage) {
        if (valorCampo != null && !valorCampo.isEmpty()) {
            return new ChatResponse("O " + campoNome + " do condomínio " + condominioName + " é: " + valorCampo + ".");
        } else {
            return new ChatResponse(sendToGeminiFallback(userMessage, condominioName, campoNome));
        }
    }

    private String sendToGemini(String message) {
        return callGeminiApi(message);
    }

    private String sendToGeminiFallback(String userMessage, String condominioName, String campoNome) {
        String prompt = "Responda em UMA FRASE CURTA, DIRETA E OBJETIVA. " +
                "Não há informações cadastradas sobre " + campoNome +
                " para o condomínio '" + condominioName + "'. " +
                "Informe isso claramente ao usuário. Pergunta do usuário: " + userMessage;
        return callGeminiApi(prompt);
    }

    // Simplificação da resposta do Gemini → deixa curta e objetiva
    private String simplificarRespostaGemini(String geminiReply) {
        if (geminiReply == null || geminiReply.isEmpty()) return "Desculpe, não consegui encontrar uma resposta.";
        // Remove parágrafos longos, quebras e excesso de texto
        String simplified = geminiReply.replaceAll("\\s+", " ").trim();
        // Pode cortar depois de uma frase completa
        int endIdx = simplified.indexOf(". ");
        if (endIdx != -1 && endIdx < 150) {
            simplified = simplified.substring(0, endIdx + 1);
        }
        return simplified;
    }

    // === Chamadas à API Gemini ===
    private String callGeminiApi(String message) {
        Map<String, Object> contents = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", message))))
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String fullUrl = geminiApiUrl + "?key=" + geminiApiKey;
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(contents, headers);

        ResponseEntity<Map> response = restTemplate.exchange(fullUrl, HttpMethod.POST, entity, Map.class);

        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
        return (String) parts.get(0).get("text");
    }
}
