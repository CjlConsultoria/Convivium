export async function enviarDenuncia(data: {
  tipo: string
  detalhes: string
  usuarioId: number
  empresaId: number
  arquivos: File[]
}) {
  const formData = new FormData()
  formData.append('tipo', data.tipo)
  formData.append('detalhes', data.detalhes)
  formData.append('usuarioId', data.usuarioId.toString())
  formData.append('empresaId', data.empresaId.toString())

  data.arquivos.forEach((file) => {
    formData.append('arquivos', file)
  })

  const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/reclamacoes`, {
    method: 'POST',
    body: formData,
    credentials: 'include', // se sua API usa cookies/sessão
  })

  if (!response.ok) {
    const error = await response.text()
    console.error('Erro ao enviar denúncia:', error)
    throw new Error('Erro ao enviar denúncia')
  }

  return await response.json()
}

import api from './api'

export interface Anexo {
  id: number
  nomeArquivo: string
}

export interface UsuarioResumo {
  id: number
  username: string
  email: string
  bloco: string
  apartamento: string
  tipoPerfil: string
}

export interface EmpresaResumo {
  id: number
  name: string
  cnpj: string
}

export interface ReclamacaoResumo {
  id: number
  tipo: string
  detalhes: string
  dataCriacao: string
  usuario: UsuarioResumo
  empresa: EmpresaResumo
  anexos: Anexo[]
}

export interface PaginaReclamacoes {
  content: ReclamacaoResumo[]
  totalElements: number
  totalPages: number
  number: number // página atual (0-based)
  size: number
}

export async function listarReclamacoes(
  page = 0,
  size = 10,
  filtro: {
    apartamento?: string
    bloco?: string
    nomeUsuario?: string
    dataInicio?: string
    dataFim?: string
    idEmpresa?: number // ✅ ADICIONADO AQUI
  } = {},
) {
  const params = {
    ...filtro,
    page,
    size,
  }

  const response = await api.get('/reclamacoes', { params })
  return response.data
}

export async function adicionarAcaoReclamacao(
  reclamacaoId: number,
  data: {
    tipo: string
    descricao: string
  },
) {
  const response = await api.post(`/reclamacoes/${reclamacaoId}/acoes`, data, {
    withCredentials: true, // se usar cookies/sessão
  })

  return response.data // retorna o objeto AcaoReclamacao criado
}

export async function solucionarReclamacao(
  reclamacaoId: number,
  data: {
    descricao: string
  },
) {
  const response = await api.post(`/reclamacoes/${reclamacaoId}/solucao`, data, {
    withCredentials: true,
  })

  return response.data // retorna o objeto Reclamacao atualizado
}

export interface AcaoReclamacao {
  id: number
  descricao: string
  status: string
  dataCriacao?: string
  dataAtualizacao?: string
}

// Buscar histórico de ações de uma reclamação pelo ID
export async function listarAcoesReclamacao(reclamacaoId: number): Promise<AcaoReclamacao[]> {
  const response = await api.get<AcaoReclamacao[]>(`/reclamacoes/${reclamacaoId}`, {
    withCredentials: true, // se usar cookies/sessão
  })
  return response.data
}

// Listar todas as ações (se precisar)
export async function listarTodasAcoes(): Promise<AcaoReclamacao[]> {
  const response = await api.get<AcaoReclamacao[]>('/reclamacoes/all', {
    withCredentials: true,
  })
  return response.data
}

export async function baixarAnexosZip(reclamacaoId: number): Promise<void> {
  try {
    const response = await api.get(`/reclamacoes/${reclamacaoId}/anexos/zip`, {
      responseType: 'blob', // essencial para downloads
      withCredentials: true,
    })

    const blob = new Blob([response.data], { type: 'application/zip' })
    const url = window.URL.createObjectURL(blob)

    const a = document.createElement('a')
    a.href = url
    a.download = `reclamacao_${reclamacaoId}_anexos.zip`
    document.body.appendChild(a)
    a.click()
    a.remove()
    window.URL.revokeObjectURL(url)
  } catch (error) {
    console.error('Erro ao baixar anexos zip:', error)
    throw new Error('Falha ao baixar os anexos da reclamação.')
  }
}
