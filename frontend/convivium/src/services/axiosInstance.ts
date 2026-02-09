import axios from 'axios'
import router from '@/router'
import { toast } from 'vue3-toastify'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/convivium/api',
  timeout: 30000, // 30 segundos
  headers: {
    'Content-Type': 'application/json',
  },
})

// Interceptor de requisição - adiciona token automaticamente
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Interceptor de resposta - tratamento unificado de erros
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      const { status, data } = error.response

      switch (status) {
        case 401:
          // Token expirado ou inválido
          localStorage.clear()
          sessionStorage.clear()
          delete api.defaults.headers['Authorization']
          toast.error('Sessão expirada. Por favor, faça login novamente.')
          setTimeout(() => {
            router.push({ name: 'login' })
          }, 1000)
          break

        case 403:
          toast.error('Você não tem permissão para realizar esta ação.')
          break

        case 404:
          toast.error('Recurso não encontrado.')
          break

        case 500:
          toast.error('Erro interno do servidor. Tente novamente mais tarde.')
          break

        default:
          const message = data?.message || data?.error || 'Erro ao processar requisição.'
          toast.error(message)
      }
    } else if (error.request) {
      // Requisição foi feita mas não houve resposta
      toast.error('Erro de conexão. Verifique sua internet.')
    } else {
      // Erro ao configurar a requisição
      toast.error('Erro ao processar requisição.')
    }

    return Promise.reject(error)
  }
)

export default api
