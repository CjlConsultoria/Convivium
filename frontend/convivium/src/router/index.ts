import { createRouter, createWebHistory } from 'vue-router'
import Admin from '../views/Admin.vue'
import Login from '../views/Login.vue'
import MeusDados from '../views/MeusDados.vue'
import Inicio from '../views/Home.vue'
import Denuncia from '../views/DenunciaForm.vue'
import Licenca from '../views/LicenseManagement.vue'

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
      path: '/licenca',
      name: 'licenca',
      component: Licenca,
      meta: { requiresAuth: true, allowedRoles: ['ADMIN'] },
    },
    {
      path: '/empresa/:codigo/admin',
      name: 'adminPorEmpresa',
      component: Admin,
      meta: { requiresAuth: true, allowedRoles: ['ADMINISTRATIVO', 'ADMIN'] }, // permitir ADMIN aqui também
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
    return next({ name: 'login' })
  }

  if (!token) {
    // Não precisa de auth, deixa passar
    return next()
  }

  try {
    const userData = await fetchUserData()

    localStorage.setItem('userName', userData.username)
    localStorage.setItem('userPerfil', userData.role.name)
    localStorage.setItem('userId', userData.id)
    localStorage.setItem('userEmpresa', JSON.stringify(userData.empresa || {}))
    window.dispatchEvent(new Event('storage'))

    const perfil = userData.role.name
    const allowedRoles = (to.meta?.allowedRoles || []) as string[]
    const codigoEmpresa = userData.empresa?.codigoPublico

    // Se tentar acessar login/home e já está logado:
    if (['home', 'login'].includes(to.name?.toString() || '')) {
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

      // Redireciona conforme perfil
      if (perfil === 'ADMIN') {
        return next({ name: 'licenca' }) // Tela Licença
      }

      if (perfil === 'SINDICO') {
        return next({ name: 'adminPorEmpresa', params: { codigo: codigoEmpresa } })
      }

      // Outros perfis vão para a denúncia
      return next({ name: 'denuncia' })
    }

    // Rota tem allowedRoles?
    if (allowedRoles.length > 0 && !allowedRoles.includes(perfil)) {
      // Bloqueia acesso e direciona conforme perfil
      if (perfil === 'ADMIN') return next({ name: 'licenca' })
      if (perfil === 'SINDICO')
        return next({ name: 'adminPorEmpresa', params: { codigo: codigoEmpresa } })
      return next({ name: 'denuncia' })
    }

    // Verifica se perfil diferente só pode acessar a denúncia
    if (perfil !== 'ADMIN' && perfil !== 'SINDICO') {
      // Se tentar acessar qualquer rota que não seja denuncia
      if (to.name !== 'denuncia') {
        return next({ name: 'denuncia' })
      }
    }

    return next()
  } catch (err) {
    console.error('Erro ao buscar dados do usuário', err)
    localStorage.clear()
    return next({ name: 'login' })
  }
})

export default router
