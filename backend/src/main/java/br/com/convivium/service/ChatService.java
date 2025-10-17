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
import java.util.regex.Pattern;

@Service
public class ChatService {

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private ReclamacaoService reclamacaoService;

    @Autowired
    private ChatAprendizadoService aprendizadoService;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // === Processa a mensagem recebida ===
    public ChatResponse processMessage(ChatRequest request) {
        String msg = request.getMessage().toLowerCase();
        Long condominioId = request.getCondominioId();
        Long usuarioId = request.getUsuarioId();

        try {
            Empresa condominio = empresaService.validarOuBuscar(condominioId);
            CondominioInfo info = condominio.getInfoCondominio();

            // ===== Login =====
            if (matchesLogin(msg)) {
                return new ChatResponse("Você pode acessar o sistema aqui: https://meucondominio.com/login");
            }

            // ===== Reclamações =====
            if (matchesReclamacao(msg)) {
                String tipo = detectTipoReclamacao(msg);
                Reclamacao r = reclamacaoService.criarReclamacao(tipo, request.getMessage(), usuarioId, condominio.getId(), null);
                return new ChatResponse("Sua reclamação de tipo '" + tipo + "' foi registrada com sucesso! Protocolo: " + r.getId());
            }

            // ===== Áreas do condomínio (respostas locais, sem Gemini) =====
            if (matchesPiscina(msg)) return respostaLocal(info, "piscina", info != null ? info.getHorarioPiscina() : null);
            if (matchesAcademia(msg)) return respostaLocal(info, "academia", info != null ? info.getHorarioAcademia() : null);
            if (matchesChurrasqueira(msg)) return respostaLocal(info, "churrasqueira", info != null ? info.getHorarioChurrasco() : null);
            if (matchesSalao(msg)) return respostaLocal(info, "salão de festas", info != null ? info.getHorarioSalaoFestas() : null);
            if (matchesQuadra(msg)) return respostaLocal(info, "quadra", info != null ? info.getHorarioQuadra() : null);
            if (matchesPlayground(msg)) return respostaLocal(info, "playground", info != null ? info.getHorarioPlayground() : null);
            if (matchesElevador(msg)) return respostaLocal(info, "elevador", info != null ? info.getHorarioElevador() : null);
            if (matchesPortaria(msg)) return respostaLocal(info, "portaria", info != null ? info.getTelefonePortaria() : null);
            if (matchesSindico(msg)) return respostaLocal(info, "síndico", info != null ? info.getContatoSindico() : null);
            if (matchesBarulho(msg)) return respostaLocal(info, "horário de silêncio", info != null ? info.getHorarioBarulho() : null);
            if (matchesLixo(msg)) return respostaLocal(info, "coleta de lixo", info != null ? info.getColetaLixo() : null);
            if (matchesManutencao(msg)) return respostaLocal(info, "manutenção", info != null ? info.getManutencao() : null);
            if (matchesIluminacao(msg)) return respostaLocal(info, "iluminação", info != null ? info.getIluminacao() : null);
            if (matchesAgua(msg)) return respostaLocal(info, "água", info != null ? info.getAgua() : null);
            if (matchesBoleto(msg)) return respostaLocal(info, "boleto do condomínio", info != null ? info.getBoletoLink() : null);
            if (matchesReserva(msg)) return respostaLocal(info, "reservas", info != null ? info.getReservaLink() : null);
            if (matchesRegra(msg)) return respostaLocal(info, "regras do condomínio", info != null ? info.getRegrasGerais() : null);
            if (matchesContato(msg)) return respostaLocal(info, "contatos administrativos", info != null ? info.getContatoSindico() : null);

            // ===== Consultar banco dinamicamente antes de Gemini =====
            ChatResponse respostaBanco = aprendizadoService.buscarResposta(info, msg);
            if (respostaBanco != null) return respostaBanco;

            // ===== Fallback → Gemini =====
            String geminiReply = sendToGemini(msg);
            return new ChatResponse(simplificarRespostaGemini(geminiReply));


        } catch (IllegalArgumentException e) {
            return new ChatResponse("Condomínio inválido ou não encontrado.");
        } catch (Exception e) {
            return new ChatResponse("Erro ao processar sua solicitação: " + e.getMessage());
        }
    }

    // === Detecção de intenções ===
    private boolean matchesLogin(String msg) {
        return Pattern.compile("\\b(login|entrar|acessar|meu condominio|painel|senha|usu[aá]rio)\\b").matcher(msg).find();
    }

    private boolean matchesReclamacao(String msg) {
        return Pattern.compile("\\b(reclam[açc][aã]o|den[uú]ncia|denunciar|reportar|problema|ocorr[êe]ncia|queixa|elogio|sugest[aã]o)\\b").matcher(msg).find();
    }

    private boolean matchesPiscina(String msg) {
        return Pattern.compile("\\b(piscina|pool)\\b").matcher(msg).find();
    }

    private boolean matchesAcademia(String msg) {
        return Pattern.compile("\\b(academia|gin[áa]sio|muscula[cç][aã]o|fitness|gym)\\b").matcher(msg).find();
    }

    private boolean matchesChurrasqueira(String msg) {
        return Pattern.compile("\\b(churrasqueira|churras|churrasquinho|espa[cç]o gourmet)\\b").matcher(msg).find();
    }

    private boolean matchesSalao(String msg) {
        return Pattern.compile("\\b(sal[aã]o|sal[aã]o de festas|salao de festas|eventos|festa)\\b").matcher(msg).find();
    }

    private boolean matchesQuadra(String msg) {
        return Pattern.compile("\\b(quadra|campo|futebol|basquete|v[oô]lei|esporte)\\b").matcher(msg).find();
    }

    private boolean matchesPlayground(String msg) {
        return Pattern.compile("\\b(playground|parquinho|brinquedos|brinquedoteca|parque infantil)\\b").matcher(msg).find();
    }

    private boolean matchesElevador(String msg) {
        return Pattern.compile("\\b(elevador|ascensor)\\b").matcher(msg).find();
    }

    private boolean matchesPortaria(String msg) {
        return Pattern.compile("\\b(portaria|porteiro|port[eã]o)\\b").matcher(msg).find();
    }

    private boolean matchesSindico(String msg) {
        return Pattern.compile("\\b(s[íi]ndico|sindico|administrador do condominio)\\b").matcher(msg).find();
    }

    private boolean matchesBarulho(String msg) {
        return Pattern.compile("\\b(barulho|som alto|ru[ií]do|sil[êe]ncio|m[úu]sica|festa|perturba[cç][aã]o)\\b").matcher(msg).find();
    }
    // === Novas intenções ===
    private boolean matchesGaragem(String msg) {
        return Pattern.compile("\\b(garagem|estacionamento|vaga|carro|port[ãa]o|controle|acesso garagem)\\b").matcher(msg).find();
    }

    private boolean matchesLixo(String msg) {
        return Pattern.compile("\\b(lixo|coleta|entulho|reciclagem|sujeira)\\b").matcher(msg).find();
    }

    private boolean matchesManutencao(String msg) {
        return Pattern.compile("\\b(manuten[cç][aã]o|reparo|conserto|defeito|problema|pane|estragou)\\b").matcher(msg).find();
    }

    private boolean matchesIluminacao(String msg) {
        return Pattern.compile("\\b(ilumina[cç][aã]o|l[uú]z|lampada|poste|escuro)\\b").matcher(msg).find();
    }

    private boolean matchesAgua(String msg) {
        return Pattern.compile("\\b([aá]gua|torneira|vazamento|hidr[oô]metro|encanamento)\\b").matcher(msg).find();
    }

    private boolean matchesBoleto(String msg) {
        return Pattern.compile("\\b(boleto|pagamento|mensalidade|taxa|condom[ií]nio|segunda via|financeiro)\\b").matcher(msg).find();
    }

    private boolean matchesReserva(String msg) {
        return Pattern.compile("\\b(reservar|reserva|agendar|agendamento|locar|usar o sal[aã]o|usar a churrasqueira|usar a quadra)\\b").matcher(msg).find();
    }

    private boolean matchesRegra(String msg) {
        return Pattern.compile("\\b(regra|regras|normas|pode|n[aã]o pode|permitido|proibido|conduta)\\b").matcher(msg).find();
    }

    private boolean matchesContato(String msg) {
        return Pattern.compile("\\b(contato|telefone|email|administradora|s[íi]ndico|portaria)\\b").matcher(msg).find();
    }

    // === Tipo de reclamação ===
    private String detectTipoReclamacao(String msg) {
        msg = msg.toLowerCase();

        if (Pattern.compile("\\b(barulho|som alto|ru[ií]do|festa|m[úu]sica)\\b").matcher(msg).find()) return "Barulho";
        if (Pattern.compile("\\b(limpeza|lixo|sujeira|entulho|faxina)\\b").matcher(msg).find()) return "Limpeza";
        if (Pattern.compile("\\b(manuten[cç][aã]o|reparo|conserto|defeito|quebrado)\\b").matcher(msg).find()) return "Manutenção";
        if (Pattern.compile("\\b(ilumina[cç][aã]o|l[âa]mpada|poste|l[uú]z)\\b").matcher(msg).find()) return "Iluminação";
        if (Pattern.compile("\\b(seguran[cç]a|roubo|furto|suspeito|portaria|vigil[âa]ncia)\\b").matcher(msg).find()) return "Segurança";
        if (Pattern.compile("\\b(garagem|vaga|estacionamento|carro|port[ãa]o)\\b").matcher(msg).find()) return "Garagem";
        if (Pattern.compile("\\b(elevador|ascensor|pane|travou)\\b").matcher(msg).find()) return "Elevador";

        return "Geral";
    }

    // === Respostas automáticas locais (sem Gemini) ===
    private ChatResponse respostaLocal(CondominioInfo info, String campo, String valor) {
        if (valor != null && !valor.isEmpty()) {
            return new ChatResponse("O " + campo + " funciona nos seguintes horários: " + valor + ".");
        } else {
            return new ChatResponse("No momento não há informações cadastradas sobre o(a) " + campo + ". Verifique com a administração ou o síndico.");
        }
    }

    // === Gemini apenas para perguntas genéricas ===
    private String sendToGemini(String message) {
        try {
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

        } catch (Exception e) {
            return "Desculpe, não consegui encontrar uma resposta no momento.";
        }
    }

    private String simplificarRespostaGemini(String geminiReply) {
        if (geminiReply == null || geminiReply.isEmpty()) {
            return "Desculpe, não consegui encontrar uma resposta no momento.";
        }

        String simplified = geminiReply.replaceAll("\\s+", " ").trim();

        if (simplified.matches("(?i).*\\b(é uma palavra|significa|origem|idioma|latim)\\b.*")) {
            return "Posso te ajudar apenas com informações do condomínio. Verifique com a administração, por favor.";
        }

        if (simplified.length() > 200) {
            simplified = simplified.substring(0, 200).trim();
            if (!simplified.endsWith(".")) simplified += "...";
        }

        return simplified;
    }
}
