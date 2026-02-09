// src/services/relatorioService.ts
import api from './api'

export const buscarRelatorioReclamacoes = async (
  idEmpresa: number | string | null,
  inicio: string,
  fim: string,
) => {
  try {
    const params: Record<string, string | number> = { inicio, fim }
    if (idEmpresa != null) params.empresaId = idEmpresa
    const response = await api.get('/relatorios/metricas', { params })
    return response.data
  } catch (error) {
    console.error('Erro ao buscar relatório de reclamações:', error)
    throw error
  }
}
