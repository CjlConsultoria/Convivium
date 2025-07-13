import api from './api'

const getAuthToken = () => {
  return localStorage.getItem('authToken')
}
export const fetchUsersByEmpresaRaw = async (
  idEmpresa: number,
  page = 0,
  size = 10,
  sort = 'username,asc',
) => {
  const token = getAuthToken()
  if (!token) {
    throw new Error('Token de autenticação não encontrado.')
  }

  try {
    const response = await api.get(`/user/list/${idEmpresa}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      params: {
        page,
        size,
        sort,
      },
    })
    return response.data // Retorna o objeto completo do Spring Data Page
  } catch (error) {
    console.error('Erro ao buscar usuários paginados:', error)
    throw error
  }
}

// Buscar lista de roles
export const fetchRolesUsuario = async () => {
  const token = getAuthToken()
  if (!token) {
    throw new Error('Token de autenticação não encontrado.')
  }

  try {
    const response = await api.get('/user/list/role', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
    return response.data
  } catch (error) {
    console.error('Erro ao buscar roles do usuário:', error)
    throw error
  }
}

// Buscar lista de tipos de usuário
export const fetchTiposUsuario = async () => {
  const token = getAuthToken()
  if (!token) {
    throw new Error('Token de autenticação não encontrado.')
  }

  try {
    const response = await api.get('/user/list/tipo', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
    return response.data
  } catch (error) {
    console.error('Erro ao buscar tipos de usuário:', error)
    throw error
  }
}
export const buscarUsuarioPorId = async (id: number) => {
  const token = getAuthToken()
  if (!token) {
    throw new Error('Token de autenticação não encontrado.')
  }

  try {
    const response = await api.get(`/user/${id}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
    return response.data
  } catch (error) {
    console.error(`Erro ao buscar usuário pelo ID ${id}:`, error)
    throw error
  }
}
export const ativarConta = async (idUsuario: string | number, senha: string) => {
  try {
    await api.put(`/user/ativar-conta/${idUsuario}`, { senha })
  } catch (error) {
    console.error('Erro ao ativar conta:', error)
    throw error
  }
}
export const buscarUsuarioPorCpf = async (cpf: string) => {
  const token = getAuthToken()
  if (!token) {
    throw new Error('Token de autenticação não encontrado.')
  }

  try {
    const response = await api.get(`/auth/buscar-por-cpf/${cpf}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
    return response.data
  } catch (error) {
    console.error(`Erro ao buscar usuário pelo CPF ${cpf}:`, error)
    throw error
  }
}
