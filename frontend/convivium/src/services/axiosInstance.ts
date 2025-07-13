import axios from 'axios'
import router from '@/router'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/convivium/api',
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Token expirado ou inválido → limpar e redirecionar
      localStorage.removeItem('authToken')
      localStorage.removeItem('userName')
      localStorage.removeItem('userPerfil')
      router.push({ name: 'login' })
    }
    return Promise.reject(error)
  },
)

export default api
