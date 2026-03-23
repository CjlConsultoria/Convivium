package br.com.convivium.service;

import br.com.convivium.dto.request.ChatRequest;
import br.com.convivium.dto.response.ChatResponse;
import br.com.convivium.entity.CondominioInfo;
import br.com.convivium.repository.CondominioInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private CondominioInfoRepository condominioInfoRepository;

    @Autowired
    private ChatAprendizadoService aprendizadoService;

    @Autowired
    private ChatInteligenciaService inteligenciaService;

    public ChatResponse processMessage(ChatRequest request) {
        try {
            // Busca informações do condomínio
            Optional<CondominioInfo> condominioOpt = condominioInfoRepository.findById(request.getCondominioId());
            
            if (condominioOpt.isEmpty()) {
                return new ChatResponse("Condomínio não encontrado. Verifique se você está logado corretamente.");
            }

            CondominioInfo condominio = condominioOpt.get();
            String mensagem = request.getMessage();

            // Primeira tentativa: busca por palavras-chave específicas
            ChatResponse resposta = aprendizadoService.buscarResposta(condominio, mensagem);
            
            if (resposta != null) {
                return resposta;
            }

            // Segunda tentativa: usa IA para resposta mais inteligente
            return inteligenciaService.processarMensagemInteligente(condominio, mensagem, request.getUsuarioId());
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ChatResponse("Desculpe, ocorreu um erro ao processar sua mensagem. Tente novamente mais tarde.");
        }
    }
}