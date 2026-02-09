import api from './axiosInstance'

export interface EncomendaDTO {
  id: number
  codigoRetirada: string
  status: 'AGUARDANDO' | 'DISPONIVEL' | 'RETIRADA'
  descricao?: string
  dataRecebimento: string
  dataRetirada?: string
  moradorId: number
  moradorNome: string
  empresaId: number
  empresaNome: string
  registradoPorId?: number
  registradoPorNome?: string
  retiradaPorId?: number
  retiradaPorNome?: string
}

export interface EncomendaRegistroRequest {
  moradorId: number
  empresaId: number
  descricao?: string
}

export interface EncomendaRetiradaRequest {
  codigoRetirada: string
}

export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export async function listarEncomendas(params: {
  empresaId?: number
  moradorId?: number
  page?: number
  size?: number
}): Promise<PageResponse<EncomendaDTO>> {
  const { data } = await api.get<PageResponse<EncomendaDTO>>('/encomendas', {
    params: {
      empresaId: params.empresaId,
      moradorId: params.moradorId,
      page: params.page ?? 0,
      size: params.size ?? 10,
    },
  })
  return data
}

export async function registrarEncomenda(body: EncomendaRegistroRequest): Promise<EncomendaDTO> {
  const { data } = await api.post<EncomendaDTO>('/encomendas/registrar', body)
  return data
}

export async function marcarRetirada(codigoRetirada: string, empresaId: number): Promise<EncomendaDTO> {
  const { data } = await api.post<EncomendaDTO>('/encomendas/retirada', { codigoRetirada }, { params: { empresaId } })
  return data
}

export async function buscarEncomendaPorCodigo(codigoRetirada: string, empresaId: number): Promise<EncomendaDTO | null> {
  try {
    const { data } = await api.get<EncomendaDTO>(`/encomendas/codigo/${encodeURIComponent(codigoRetirada)}`, {
      params: { empresaId },
    })
    return data
  } catch {
    return null
  }
}
