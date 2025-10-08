// src/services/chatService.ts
import api from './api'

export interface ChatRequest {
  message: string
  condominioId: number
  usuarioId: number
}

export interface ChatResponse {
  message: string
}

/**
 * Envia uma mensagem para o chat do condomínio.
 * @param request - Objeto com a mensagem, condominioId e usuarioId
 * @param token - Token JWT do usuário logado
 */
export async function enviarMensagem(request: ChatRequest, token: string): Promise<ChatResponse> {
  try {
    const response = await api.post<ChatResponse>('/chat', request, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })

    return response.data
  } catch (error) {
    console.error('Erro ao enviar mensagem:', error)
    throw error
  }
}

/**
 * Exemplo de função para obter histórico de chat, se precisar futuramente
 */
export async function buscarHistorico(
  condominioId: number,
  usuarioId: number,
  token: string,
): Promise<ChatResponse[]> {
  try {
    const response = await api.get<ChatResponse[]>(`/chat/historico`, {
      params: { condominioId, usuarioId },
      headers: { Authorization: `Bearer ${token}` },
    })
    return response.data
  } catch (error) {
    console.error('Erro ao buscar histórico de chat:', error)
    return []
  }
}
