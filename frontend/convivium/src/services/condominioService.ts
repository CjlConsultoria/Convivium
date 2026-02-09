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

export interface CondominioInfoDTO {
  id?: number
  empresaId?: number
  nomeCondominio?: string
  horarioPiscina?: string
  horarioAcademia?: string
  horarioChurrasco?: string
  horarioSalaoFestas?: string
  horarioQuadra?: string
  horarioElevador?: string
  horarioPlayground?: string
  telefonePortaria?: string
  contatoSindico?: string
  contatoAdministradora?: string
  horarioBarulho?: string
  regrasGerais?: string
  regrasGaragem?: string
  regrasPiscina?: string
  regrasChurrasqueira?: string
  regrasSalaoFestas?: string
  regrasQuadra?: string
  limpeza?: string
  manutencao?: string
  iluminacao?: string
  agua?: string
  coletaLixo?: string
  boletoLink?: string
  formaPagamento?: string
  taxaCondominio?: string
  reservaLink?: string
  politicaReservas?: string
  estacionamentoInfo?: string
  visitantesInfo?: string
  animaisInfo?: string
}

export const buscarInfoCondominioPorCodigo = async (codigoPublico: string): Promise<CondominioInfoDTO | null> => {
  try {
    const response = await api.get(`/public/condominio/codigo/${encodeURIComponent(codigoPublico)}/info`)
    return response.data
  } catch (error: any) {
    if (error.response?.status === 404) return null
    console.error('Erro ao buscar informações do condomínio:', error)
    throw error
  }
}

export const buscarInfoCondominioPorEmpresaId = async (empresaId: number): Promise<CondominioInfoDTO | null> => {
  try {
    const response = await api.get(`/condominio-info/empresa/${empresaId}`)
    return response.data
  } catch (error: any) {
    if (error.response?.status === 404) return null
    throw error
  }
}

export const atualizarInfoCondominio = async (empresaId: number, data: Partial<CondominioInfoDTO>): Promise<CondominioInfoDTO> => {
  const response = await api.put(`/condominio-info/empresa/${empresaId}`, data)
  return response.data
}
