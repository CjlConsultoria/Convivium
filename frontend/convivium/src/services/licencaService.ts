import api from './api'

export interface LicencaDTO {
  id?: number
  empresaId: number
  dataInicio: string // formato ISO yyyy-MM-dd
  dataFim: string
  ativa?: boolean
  tipo: string
  limiteUsuarios: number
}

export const salvarLicenca = async (dto: LicencaDTO): Promise<LicencaDTO> => {
  try {
    const response = await api.post<LicencaDTO>('/licencas', dto)
    return response.data
  } catch (error) {
    console.error('Erro ao salvar licença:', error)
    throw error
  }
}

export const buscarLicencaPorId = async (id: number): Promise<LicencaDTO> => {
  try {
    const response = await api.get<LicencaDTO>(`/licencas/${id}`)
    return response.data
  } catch (error) {
    console.error('Erro ao buscar licença:', error)
    throw error
  }
}

export const listarLicencas = async (
  page = 0,
  size = 10,
  empresaNome?: string,
): Promise<{ content: LicencaDTO[]; totalPages: number }> => {
  try {
    const response = await api.get<{ content: LicencaDTO[]; totalPages: number }>(
      '/licencas/search',
      {
        params: {
          page,
          size,
          empresaNome,
        },
      },
    )
    return response.data
  } catch (error) {
    console.error('Erro ao listar licenças:', error)
    throw error
  }
}

export async function excluirLicenca(id: number): Promise<void> {
  try {
    await api.delete(`/licencas/${id}`)
  } catch (error) {
    console.error('Erro ao excluir licença:', error)
    throw error
  }
}
