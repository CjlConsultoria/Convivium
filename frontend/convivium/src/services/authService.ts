// src/services/authService.ts
import api from './api'

// Função para obter o token de autenticação
const getAuthToken = () => {
  return localStorage.getItem('authToken')
}

// Função de login
export const login = async (cpf: string, password: string) => {
  const response = await api.post('/auth/login', { cpf, password })
  return response.data
}

export interface MoradorPayload {
  username: string
  password: string
  email: string
  cpf: string
  tipoUsuario: number
  ativo?: boolean
  sobrenome?: string
  telefone?: string
  cep?: string
  logradouro?: string
  cidade?: string
  estado?: string
  bairro?: string
  numero?: string
  complemento?: string
  genero?: string
  alerta?: boolean
  bloco?: string
  apartamento?: string
  vagaCarro?: string
  vagaMoto?: string
  role?: number
  empresa?: number
}

export const salvarMoradorCompleto = async (dados: MoradorPayload) => {
  // força senha 123 e ativo false conforme solicitado
  const payload = {
    ...dados,
    password: '123',
    ativo: false,
  }

  const response = await api.post('/auth/register', payload)
  return response.data
}

export const fetchUserData = async () => {
  const token = getAuthToken()

  if (token) {
    try {
      const response = await api.get('/auth/user', {
        headers: {
          Authorization: 'Bearer ' + token,
        },
      })
      return response.data
    } catch (error) {
      console.error('Erro ao buscar dados do usuário:', error)
      throw error
    }
  } else {
    throw new Error('Token de autenticação não encontrado.')
  }
}

export const forgotPassword = async (emailCpf: string) => {
  try {
    const response = await api.post('/auth/forgotPassword', { emailCpf })
    return response.data
  } catch (error) {
    console.error('Erro ao enviar email de redefinição de senha:', error)
    throw error
  }
}

export const resetPassword = async (token: string, novaSenha: string, cpf: string) => {
  try {
    const response = await api.post('/auth/reset-password', { token, novaSenha, cpf })
    return response.data
  } catch (error) {
    console.error('Erro ao redefinir senha:', error)
    throw error
  }
}

// Atualizar dados do usuário
export const updateUserData = async (
  id: string,
  username: string,
  sobrenome: string,
  email: string,
  cpf: string,
  telefone: string,
  cep: string,
  logradouro: string,
  numero: string,
  complemento: string,
  bairro: string,
  cidade: string,
  estado: string,
  roleId: number, // para enviar só o id do perfil
  tipoId: number, // id do tipo do usuário
  status: string,
  genero: string,
  bloco: string,
  apartamento: string,
  vagaCarro: string,
  vagaMoto: string,
) => {
  const token = getAuthToken()

  if (!token) {
    throw new Error('Token de autenticação não encontrado.')
  }

  const body = {
    id,
    username,
    sobrenome,
    email,
    cpf,
    telefone,
    cep,
    logradouro,
    numero,
    complemento,
    bairro,
    cidade,
    estado,
    role: { id: roleId }, // envia só id do perfil
    tipo: { id: tipoId }, // envia só id do tipo
    status,
    genero,
    bloco,
    apartamento,
    vagaCarro,
    vagaMoto,
  }

  try {
    const response = await api.put(`/auth/usuario/update/${id}`, body, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })

    return response
  } catch (error) {
    console.error('Erro ao atualizar dados do usuário:', error)
    throw error
  }
}

export async function deletarUsuario(id: string) {
  return await api.delete(`/auth/usuario/delete/${id}`)
}

export async function atualizarUsuario(id: string, dados: any) {
  return await api.put(`/auth/usuario/update/${id}`, dados)
}

export const fetchUsuarios = async (params: {
  page: number
  size: number
  nome?: string
  cpf?: string
}) => {
  const { page, size, nome, cpf } = params
  const query = new URLSearchParams({
    page: String(page),
    size: String(size),
  }).toString()

  // Corpo só com filtro (nome e cpf)
  const filtro = { nome, cpf }

  const response = await api.post(`/user/filtrar?${query}`, filtro)
  return response.data
}
