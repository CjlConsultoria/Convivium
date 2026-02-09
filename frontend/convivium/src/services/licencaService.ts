import api from './api'

export interface LicencaDTO {
  id?: number
  empresaId: number
  dataInicio: string
  dataFim: string
  ativa?: boolean
  tipo: string
  limiteUsuarios: number
}

/** Resposta paginada da busca de licenças (search) */
export interface LicencaDetalhadaDTO {
  id: number
  empresaId: number
  empresaNome: string
  empresaCnpj: string
  dataInicio: string
  dataFim: string
  ativa: boolean
  tipo: string
  limiteUsuarios: number
  responsavelId?: number
  responsavelNome?: string
  responsavelCpf?: string
  perfil?: string
  validadeExpirada: boolean
  diasRestantes: number
}

export interface PageLicencas {
  content: LicencaDetalhadaDTO[]
  totalPages: number
  number: number
  first: boolean
  last: boolean
}

export const salvarLicenca = async (dto: LicencaDTO): Promise<LicencaDTO> => {
  const response = await api.post<LicencaDTO>('/licencas', dto)
  return response.data
}

export const listarLicencas = async (
  page = 0,
  size = 10,
  empresaNome?: string,
  usuarioNome?: string,
  cpf?: string,
): Promise<PageLicencas> => {
  const params: Record<string, string | number> = { page, size }
  if (empresaNome) params.empresaNome = empresaNome
  if (usuarioNome) params.usuarioNome = usuarioNome
  if (cpf) params.cpf = cpf
  const response = await api.get<PageLicencas>('/licencas/search', { params })
  return response.data
}

export async function excluirLicenca(id: number): Promise<void> {
  await api.delete(`/licencas/${id}`)
}
