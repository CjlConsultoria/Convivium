import api from './api' // seu axios ou fetch já configurado

export interface Empresa {
  id: number
  name: string
  cnpj?: string
  cep?: string
  logradouro?: string
  numero?: string
  complemento?: string
  bairro?: string
  cidade?: string
  estado?: string
}

export interface PaginatedResponse<T> {
  content: T[]
  totalPages: number
  number: number
  first: boolean
  last: boolean
  // outros campos se necessário
}

export const fetchEmpresas = async (
  page: number,
  nome?: string,
  cnpj?: string,
): Promise<PaginatedResponse<Empresa>> => {
  const queryParams = new URLSearchParams()
  queryParams.append('page', page.toString())
  queryParams.append('size', '10') // defina o tamanho fixo aqui ou passe como parâmetro se quiser

  if (nome) queryParams.append('nome', nome)
  if (cnpj) queryParams.append('cnpj', cnpj)

  const response = await api.get<PaginatedResponse<Empresa>>(
    `/empresas/all?${queryParams.toString()}`,
  )
  return response.data
}

export interface EmpresaCreateDTO {
  name: string
  cnpj: string
  cep?: string
  logradouro?: string
  numero?: string
  complemento?: string
  bairro?: string
  cidade?: string
  estado?: string
}

export const criarEmpresaService = async (empresa: EmpresaCreateDTO): Promise<Empresa> => {
  try {
    const response = await api.post<Empresa>('/empresas/criar', empresa)
    return response.data
  } catch (error) {
    console.error('Erro ao criar empresa:', error)
    throw error
  }
}

// ✅ NOVO: Atualizar empresa (somente endereço, já que CNPJ e Nome não mudam)
export const atualizarEmpresaService = async (
  id: number,
  dadosAtualizados: Partial<Empresa>,
): Promise<Empresa> => {
  try {
    const response = await api.put<Empresa>(`/empresas/${id}`, dadosAtualizados)
    return response.data
  } catch (error) {
    console.error('Erro ao atualizar empresa:', error)
    throw error
  }
}

// ✅ NOVO: Excluir empresa por ID
export const excluirEmpresaService = async (id: number): Promise<void> => {
  try {
    await api.delete(`/empresas/${id}`)
  } catch (error) {
    console.error('Erro ao excluir empresa:', error)
    throw error
  }
}
