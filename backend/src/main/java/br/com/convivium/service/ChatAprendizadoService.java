package br.com.convivium.service;

import br.com.convivium.dto.response.ChatResponse;
import br.com.convivium.entity.CondominioInfo;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatAprendizadoService {

    // Mapa inicial de palavras-chave → campo do CondominioInfo
    private static final Map<String, String> campoPorPalavraChave = new LinkedHashMap<>();

    // Aprendizado dinâmico (palavra-chave → campo)
    private final Map<String, String> aprendizadoDinamico = new ConcurrentHashMap<>();

    static {
        campoPorPalavraChave.put("piscina", "horarioPiscina");
        campoPorPalavraChave.put("academia", "horarioAcademia");
        campoPorPalavraChave.put("churrasqueira", "horarioChurrasco");
        campoPorPalavraChave.put("salão de festas", "horarioSalaoFestas");
        campoPorPalavraChave.put("salao de festas", "horarioSalaoFestas");
        campoPorPalavraChave.put("quadra", "horarioQuadra");
        campoPorPalavraChave.put("playground", "horarioPlayground");
        campoPorPalavraChave.put("elevador", "horarioElevador");
        campoPorPalavraChave.put("portaria", "telefonePortaria");
        campoPorPalavraChave.put("síndico", "contatoSindico");
        campoPorPalavraChave.put("administradora", "contatoAdministradora");
        campoPorPalavraChave.put("barulho", "horarioBarulho");
        campoPorPalavraChave.put("regras", "regrasGerais");
        campoPorPalavraChave.put("boleto", "boletoLink");
        campoPorPalavraChave.put("reserva", "reservaLink");
        campoPorPalavraChave.put("manutenção", "manutencao");
        campoPorPalavraChave.put("limpeza", "limpeza");
        campoPorPalavraChave.put("lixo", "coletaLixo");
        campoPorPalavraChave.put("animais", "animaisInfo");
        campoPorPalavraChave.put("visitantes", "visitantesInfo");
        campoPorPalavraChave.put("estacionamento", "estacionamentoInfo");
    }

    public ChatResponse buscarResposta(CondominioInfo info, String mensagem) {
        mensagem = mensagem.toLowerCase();

        // Verifica palavras-chave fixas
        for (Map.Entry<String, String> entry : campoPorPalavraChave.entrySet()) {
            ChatResponse resp = buscarCampo(info, mensagem, entry.getKey(), entry.getValue());
            if (resp != null) return resp;
        }

        // Verifica palavras-chave aprendidas dinamicamente
        for (Map.Entry<String, String> entry : aprendizadoDinamico.entrySet()) {
            ChatResponse resp = buscarCampo(info, mensagem, entry.getKey(), entry.getValue());
            if (resp != null) return resp;
        }

        // Nenhuma correspondência encontrada
        return null;
    }

    private ChatResponse buscarCampo(CondominioInfo info, String mensagem, String palavraChave, String campo) {
        if (mensagem.contains(palavraChave.toLowerCase())) {
            try {
                Field field = CondominioInfo.class.getDeclaredField(campo);
                field.setAccessible(true);
                Object valor = field.get(info);

                if (valor != null && !valor.toString().isEmpty()) {
                    return new ChatResponse(valor.toString());
                } else {
                    // Permite que o sistema aprenda essa palavra-chave para futuras consultas
                    aprendizadoDinamico.putIfAbsent(palavraChave, campo);
                    return new ChatResponse("No momento não há informações cadastradas sobre '" + palavraChave + "'. Verifique com a administração ou síndico.");
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
                return new ChatResponse("Erro ao buscar a informação do condomínio.");
            }
        }
        return null;
    }

    // Método para adicionar novas palavras-chave dinamicamente
    public void adicionarPalavraChave(String palavra, String campo) {
        aprendizadoDinamico.put(palavra.toLowerCase(), campo);
    }

    // Retorna todas as palavras-chave atuais (fixas + dinâmicas)
    public Set<String> listarPalavrasChave() {
        Set<String> todas = new LinkedHashSet<>(campoPorPalavraChave.keySet());
        todas.addAll(aprendizadoDinamico.keySet());
        return todas;
    }
}
