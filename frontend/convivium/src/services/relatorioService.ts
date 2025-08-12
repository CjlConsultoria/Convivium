// src/services/relatorioService.ts
import api from './api'

export const buscarRelatorioReclamacoes = async (
  idCondominio: number | string,
  inicio: string,
  fim: string,
) => {
  try {
    const response = await api.get('/relatorios/metricas', {
      params: {
        idCondominio,
        inicio,
        fim,
      },
    })
    return response.data
  } catch (error) {
    console.error('Erro ao buscar relatório de reclamações:', error)
    throw error
  }
}
