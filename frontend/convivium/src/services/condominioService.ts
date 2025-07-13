// src/services/condominioService.ts
import api from './api'

export const verificarCPFNoCondominio = async (idCondominio: number | string, cpf: string) => {
  try {
    const response = await api.post('/public/condominio/verificar-cpf', { cpf, idCondominio })
    return response.data
  } catch (error) {
    console.error('Erro ao verificar CPF:', error)
    throw error
  }
}
