import { createRouter, createWebHistory } from 'vue-router'
import Admin from '../views/Admin.vue'
import Login from '../views/Login.vue'
import MeusDados from '../views/MeusDados.vue'
import Inicio from '../views/Home.vue'
import Denuncia from '../views/DenunciaForm.vue'
import Licenca from '../views/LicenseManagement.vue'
import AdminMoradores from '@/views/AdminMoradores.vue'
import AdminReclamacoes from '@/views/AdminReclamacoes.vue'
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
      path: '/empresa/admin/admin',
      name: 'painel-admin',
      component: () => import('@/views/PainelAdministracao.vue'),
      meta: { requiresAuth: true },
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
      path: '/moradores',
      name: 'moradores',
      component: AdminMoradores,
      meta: { requiresAuth: true, allowedRoles: ['ADMINISTRATIVO', 'ADMIN'] }, // permitir ADMIN aqui também
    },
    {
      path: '/reclamacoes',
      name: 'reclamacoes',
      component: AdminReclamacoes,
      meta: { requiresAuth: true, allowedRoles: ['ADMINISTRATIVO', 'ADMIN'] }, // permitir ADMIN aqui também
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
router.beforeEach(async (to, from, next) => {
  const requiresAuth = to.meta.requiresAuth
  const token = localStorage.getItem('authToken')

  if (requiresAuth && !token) {
    return next({ name: 'login' })
  }

  if (!token) return next()

  try {
    const userData = await fetchUserData()

    localStorage.setItem('userName', userData.username)
    localStorage.setItem('userPerfil', userData.role) // <-- role é string direta
    localStorage.setItem('userId', userData.id.toString())
    localStorage.setItem('userEmpresa', JSON.stringify(userData.empresa || {}))
    window.dispatchEvent(new Event('storage'))

    const perfil = userData.role // <-- usar direto

    const allowedRoles = (to.meta?.allowedRoles || []) as string[]
    const codigoEmpresa = userData.empresa?.codigoPublico

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

      return next({ name: 'painel-admin' })
    }

    if (allowedRoles.length > 0 && !allowedRoles.includes(perfil)) {
      if (perfil === 'ADMIN') return next({ name: 'licenca' })
      if (perfil === 'ADMINISTRATIVO')
        return next({ name: 'adminPorEmpresa', params: { codigo: codigoEmpresa } })
      return next({ name: 'denuncia' })
    }

    return next()
  } catch (err) {
    console.error('Erro ao buscar dados do usuário', err)
    localStorage.clear()
    return next({ name: 'login' })
  }
})

export default router
