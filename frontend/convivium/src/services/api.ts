// src/services/api.ts
import axios from 'axios'
import { toast } from 'vue3-toastify'
import router from '@/router'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL, // base da sua API
  headers: {
    'Content-Type': 'application/json',
  },
})

// Interceptor de resposta
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Verifica se é um erro 401 (não autorizado)
    if (error.response && error.response.status === 401) {
      // Limpa o storage (localStorage, sessionStorage)
      localStorage.clear() // Limpa o localStorage
      sessionStorage.clear() // Limpa o sessionStorage

      // Limpa o cabeçalho de Authorization do Axios
      delete api.defaults.headers['Authorization']

      // Redireciona o usuário para a tela de login após 1 segundo
      setTimeout(() => {
        router.push('/login') // Redireciona para a página de login
      }, 1000)
    }

    return Promise.reject(error) // Rejeita o erro
  },
)

export default api
