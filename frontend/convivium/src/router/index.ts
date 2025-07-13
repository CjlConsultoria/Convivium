import { createRouter, createWebHistory } from 'vue-router'
import Admin from '../views/Admin.vue'
import Login from '../views/Login.vue'
import MeusDados from '../views/MeusDados.vue'
import Inicio from '../views/Home.vue'
import Denuncia from '../views/DenunciaForm.vue'

import { fetchUserData } from '@/services/authService'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Login,
    },
    {
      path: '/inicio',
      name: 'inicio',
      component: Inicio,
    },
    {
      path: '/login',
      name: 'login',
      component: Login,
    },
    {
      path: '/denuncia',
      name: 'denuncia',
      component: Denuncia,
      meta: { requiresAuth: true },
    },
    {
      path: '/empresa/:codigo/admin',
      name: 'adminPorEmpresa',
      component: Admin,
      meta: { requiresAuth: true, allowedRoles: ['ADMIN'] }, // 👈 perfil permitido
    },
    {
      path: '/empresa/:codigo/meus-dados',
      name: 'meusDadosPorEmpresa',
      component: MeusDados,
      meta: { requiresAuth: true },
    },
    {
      path: '/empresa/:codigo/cadastro-condominio/:id',
      name: 'verificaCpfPorEmpresa',
      component: () => import('@/views/VerificaCpf.vue'),
    },
    {
      path: '/ativar-conta/:id',
      name: 'AtivarConta',
      component: () => import('@/views/AtivarConta.vue'),
    },
    {
      path: '/redefinir-senha',
      name: 'RedefinirSenha',
      component: () => import('@/views/components/PasswordResetModal.vue'),
    },
  ],
})

// Verifica autenticação
router.beforeEach((to, from, next) => {
  const requiresAuth = to.meta.requiresAuth
  const token = localStorage.getItem('authToken')

  if (requiresAuth && !token) {
    return next({ name: 'login' })
  }

  next()
})
router.beforeEach(async (to, from, next) => {
  const requiresAuth = to.meta.requiresAuth
  const token = localStorage.getItem('authToken')

  if (requiresAuth && !token) {
    // Se a rota precisa de auth e não tem token, manda pro login
    return next({ name: 'login' })
  }

  if (token) {
    try {
      const userData = await fetchUserData()

      localStorage.setItem('userName', userData.username)
      localStorage.setItem('userPerfil', userData.role.name)
      localStorage.setItem('userId', userData.id)
      localStorage.setItem('userEmpresa', JSON.stringify(userData.empresa || {}))
      window.dispatchEvent(new Event('storage'))

      const allowedRoles = (to.meta?.allowedRoles || []) as string[]

      if (allowedRoles.length > 0 && !allowedRoles.includes(userData.role.name)) {
        // Perfil não autorizado
        return next({ name: 'inicio' })
      }

      const routeName = to.name?.toString() || ''
      const codigoEmpresa = userData.empresa?.codigoPublico

      // Redireciona se tentar acessar home/login e já estiver logado
      if (['home', 'login'].includes(routeName)) {
        if (!codigoEmpresa) {
          localStorage.clear()
          return next({ name: 'login' })
        }

        if (!userData.ativo) {
          return next({
            name: 'verificaCpfPorEmpresa',
            params: { codigo: codigoEmpresa, id: userData.id },
          })
        }

        return next({ name: 'adminPorEmpresa', params: { codigo: codigoEmpresa } })
      }

      // Se chegou aqui, pode seguir para a rota normalmente
      return next()
    } catch (err) {
      console.error('Erro ao buscar dados do usuário', err)
      localStorage.clear()
      return next({ name: 'login' })
    }
  } else {
    // Não tem token e não precisa de auth: deixa passar
    return next()
  }
})

export default router
