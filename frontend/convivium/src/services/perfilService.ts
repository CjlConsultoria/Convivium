import api from './api'

export interface Perfil {
  id: number
  name: string
}

export async function buscarPerfisDoUsuario(token: string): Promise<Perfil[]> {
  const response = await api.get<Perfil[]>('/empresas/all', {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  })

  return response.data
}
