<script setup lang="ts">
import { ref, computed, watchEffect, onMounted, onUnmounted } from 'vue'
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { toast } from 'vue3-toastify'
import { buscarPerfisDoUsuario, type Perfil } from '@/services/perfilService.ts'
import LoadingOverlay from '@/components/LoadingOverlay.vue'

const router = useRouter()

const authToken = ref(localStorage.getItem('authToken'))
const userName = ref(localStorage.getItem('userName') || 'Usuário')
const perfilUsuario = ref(localStorage.getItem('userPerfil') || 'PUBLICO')
const empresa = ref(JSON.parse(localStorage.getItem('userEmpresa') || '{}'))
const userEmpresa = computed(() => empresa.value.name || '')
const userEmpresacnpj = computed(() => empresa.value.cnpj || '')

const perfisUsuario = ref<Perfil[]>([])

const isLoggedIn = computed(() => authToken.value !== null)

function updateUserData() {
  authToken.value = localStorage.getItem('authToken')
  userName.value = localStorage.getItem('userName') || 'Usuário'
  perfilUsuario.value = localStorage.getItem('userPerfil') || 'PUBLICO'
  empresa.value = JSON.parse(localStorage.getItem('userEmpresa') || '{}')
}

onMounted(() => {
  updateUserData()
  window.addEventListener('storage', updateUserData)
})

onUnmounted(() => {
  window.removeEventListener('storage', updateUserData)
})

const podeVerEmpresa = (empresaAlvo: string) => {
  if (perfilUsuario.value === 'ADMIN') return true
  return userEmpresa.value === empresaAlvo
}

watchEffect(() => {
  if (authToken.value) {
    localStorage.setItem('authToken', authToken.value)
  } else {
    localStorage.removeItem('authToken')
  }
})

watchEffect(() => {
  if (userName.value && userName.value !== 'Usuário') {
    localStorage.setItem('userName', userName.value)
  } else {
    localStorage.removeItem('userName')
  }
})

async function carregarPerfis() {
  if (!authToken.value) return

  try {
    const perfis = await buscarPerfisDoUsuario(authToken.value)
    perfisUsuario.value = perfis

    const primeiroPerfil = perfis[0]?.name || 'PUBLICO'
    perfilUsuario.value = primeiroPerfil
    localStorage.setItem('userPerfil', primeiroPerfil)
  } catch (error) {
    toast.error('Erro ao buscar perfis do usuário')
    console.error(error)
  }
}

const handleLogout = () => {
  authToken.value = null
  userName.value = 'Usuário'
  perfilUsuario.value = 'PUBLICO'
  empresa.value = {}
  if (router.currentRoute.value.name !== 'login') {
    router.push({ name: 'login' })
  }
}

const menuAberto = ref(false)
const toggleMenu = () => {
  menuAberto.value = !menuAberto.value
}
const fecharMenu = () => {
  menuAberto.value = false
}
</script>

<template lang="pug">
div.layout
  header.navbar
    .container
      // Logo
      img.logo(src="@/assets/logocjl.png", alt="CJL logo", width="60", height="60")

      // Botão de abrir/fechar menu (visível no mobile)
      button.menu-toggle(@click="toggleMenu")
        i.fas(:class="menuAberto ? 'fa-times' : 'fa-bars'")

      // Menu completo (condicional no mobile)
      nav.nav-content(:class="{ aberto: menuAberto }")
        nav.nav-menu
          RouterLink.nav-link(to="/Inicio" @click="fecharMenu") Início
          RouterLink.nav-link(to="/denuncia" @click="fecharMenu") Reclamacao/Denuncia

          // Administração - só para SÍNDICO e ADMIN
          RouterLink.nav-link(
            v-if="(perfilUsuario === 'ADMINISTRATIVO' || perfilUsuario === 'ADMIN') && empresa?.codigoPublico"
            :to="`/empresa/${empresa.codigoPublico}/admin`"
            @click="fecharMenu"
          ) Administração

          // Licença - só para ADMIN
          RouterLink.nav-link(
            v-if="perfilUsuario && perfilUsuario.toUpperCase() === 'ADMIN'"
            to="/licenca"
            @click="fecharMenu"
          ) Gestão de Licenças





        nav.nav-buttons
          div(v-if="!isLoggedIn")
            RouterLink.nav-button(to="/login" @click="fecharMenu") Login
        div(v-if="isLoggedIn" class="nav-user-wrapper")
          div.nav-user-info
            span.nav-user-name {{ userName }}
            div.nav-user-role {{ userEmpresa }}
            div.nav-user-role {{ userEmpresacnpj }}
            RouterLink.nav-button.small-button.ml(:to="`/empresa/${empresa.codigoPublico}/meus-dados`" @click="fecharMenu") Meus Dados
          button.nav-button.primary(@click="() => { handleLogout(); fecharMenu() }") Sair



  main.main-content
    RouterView
    LoadingOverlay  <!-- Componente de Loader aqui -->
    ToastContainer  <!-- Adicionado ToastContainer -->

  footer.footer
    p © 2025 - Todos os direitos reservados
</template>
<style scoped>
html,
body {
  height: 100%;
  margin: 0;
  padding: 0;
}

.layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #ffffff;
  font-family: 'Segoe UI', sans-serif;
}

/* NAVBAR COMPACTA E BRANCA */
.navbar {
  background-color: #000000;
  padding: 0.2rem 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.logo {
  height: 60px;
}

.container {
  display: flex;
  align-items: center;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
}

.nav-content {
  display: flex;
  align-items: center;
  flex: 1;
  justify-content: center;
  gap: 2rem;
}

.nav-menu {
  display: flex;
  gap: 2rem;
  justify-content: center;
  align-items: center;
  flex: 1;
}

.nav-link {
  text-decoration: none;
  color: #ffffff;
  font-weight: 500;
  padding: 0.4rem 0.6rem;
  transition:
    color 0.2s,
    background-color 0.2s;
  background-color: transparent;
  border-radius: 4px;
}

.nav-link:hover {
  background-color: #886f0d;
  color: #000;
}

.nav-buttons {
  display: flex;
  gap: 1rem;
  margin-left: auto;
}

.nav-button {
  text-decoration: none;
  padding: 0.4rem 0.8rem;
  border: 1px solid #886f0d;
  border-radius: 4px;
  color: #ffffff;
  background-color: transparent;
  transition:
    background-color 0.2s,
    color 0.2s;
}

.nav-button:hover {
  background-color: #886f0d;
  color: #000;
}

.nav-button.primary {
  background-color: #886f0d;
  color: #000;
  border-color: #886f0d;
}

.nav-button.primary:hover {
  background-color: #886f0d;
}

.nav-user-wrapper {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.nav-user-info {
  display: flex;
  align-items: center;
  flex-direction: column;
  color: #ffffff;
}

.nav-user-name {
  font-weight: bold;
  color: #ffffff;
}

.nav-user-role {
  font-size: 0.75rem;
  color: #886f0d;
  margin-top: 0.2rem;
  text-align: center;
}

.main-content {
  flex: 1;
  padding-left: 0px;
  padding-right: 0px;
  padding-top: 0px;
  width: 100%;
  box-sizing: border-box;
}

.footer {
  background-color: #000000;
  padding: 1.5rem 2rem;
  text-align: center;
  font-size: 0.9rem;
  color: #555;
  width: 100%;
}

/* MOBILE */
@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    align-items: flex-start;
    padding: 0.5rem;
  }

  .container {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .nav-menu,
  .nav-buttons {
    flex-direction: column;
    width: 100%;
    gap: 0.5rem;
    align-items: flex-start;
  }

  .nav-link,
  .nav-button {
    width: 100%;
    text-align: left;
  }

  .nav-content {
    display: none;
    flex-direction: column;
    width: 100%;
    margin-top: 1rem;
  }

  .nav-content.aberto {
    display: flex;
  }

  .nav-user-wrapper {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.25rem;
  }

  .main-content {
    padding: 1rem;
  }

  .footer {
    font-size: 0.8rem;
    padding: 1rem;
    text-align: center;
  }
}

.menu-toggle {
  display: none;
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #886f0d;
}

@media (max-width: 768px) {
  .menu-toggle {
    display: block;
  }
}

.alerta-sistema {
  background-color: #ffe5e5;
  color: #a94442;
  padding: 0.5rem 1rem;
  text-align: center;
  font-size: 0.85rem;
  border-bottom: 1px solid #e0b4b4;
}

.nav-button.ml {
  margin-left: 0.5rem;
}

.small-button {
  padding: 0.25rem 0.5rem;
  font-size: 0.75rem;
  line-height: 1.2;
  border: 1px solid #886f0d;
  color: #886f0d;
  background-color: white;
  border-radius: 4px;
  text-align: center;
  margin-top: 4%;
  transition:
    background-color 0.2s,
    color 0.2s;
  font-weight: 500;
  cursor: pointer;
}

.small-button:hover {
  background-color: #886f0d;
  color: #000;
}
</style>
