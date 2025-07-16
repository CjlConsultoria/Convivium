<script setup lang="ts">
import { ref, computed, watchEffect, onMounted, onUnmounted } from 'vue'
import { RouterLink, RouterView, useRouter } from 'vue-router'
import { toast } from 'vue3-toastify'
import { buscarPerfisDoUsuario, type Perfil } from '@/services/perfilService.ts'
import LoadingOverlay from '@/components/LoadingOverlay.vue'
import 'vue3-toastify/dist/index.css'

const mostrarDropdown = ref(false)
const menuAberto = ref(false)

const router = useRouter()

// Dados do usuário e empresa, carregados do localStorage
const authToken = ref(localStorage.getItem('authToken'))
const userName = ref(localStorage.getItem('userName') || 'Usuário')
const perfilUsuario = ref(localStorage.getItem('userPerfil') || 'PUBLICO')
const empresa = ref(JSON.parse(localStorage.getItem('userEmpresa') || '{}'))

const userEmpresa = computed(() => empresa.value.nome || '')
const userEmpresacnpj = computed(() => empresa.value.cnpj || '')

const perfisUsuario = ref<Perfil[]>([])

const isLoggedIn = computed(() => authToken.value !== null)

// Atualiza dados do usuário com localStorage
function updateUserData() {
  authToken.value = localStorage.getItem('authToken')
  userName.value = localStorage.getItem('userName') || 'Usuário'
  perfilUsuario.value = localStorage.getItem('userPerfil') || 'PUBLICO'
  empresa.value = JSON.parse(localStorage.getItem('userEmpresa') || '{}')
}

onMounted(() => {
  updateUserData()
  window.addEventListener('storage', updateUserData)
  document.addEventListener('click', onClickOutside)
})

onUnmounted(() => {
  window.removeEventListener('storage', updateUserData)
  document.removeEventListener('click', onClickOutside)
})

const dropdownRef = ref<HTMLElement | null>(null)
const buttonRef = ref<HTMLElement | null>(null)

function onClickOutside(event: MouseEvent) {
  if (
    dropdownRef.value &&
    buttonRef.value &&
    !dropdownRef.value.contains(event.target as Node) &&
    !buttonRef.value.contains(event.target as Node)
  ) {
    mostrarDropdown.value = false
  }
}

const toggleDropdown = (event: MouseEvent) => {
  event.stopPropagation()
  mostrarDropdown.value = !mostrarDropdown.value
  console.log('toggleDropdown chamado', mostrarDropdown.value)
}

const fecharDropdown = () => {
  mostrarDropdown.value = false
}

const toggleMenu = () => {
  menuAberto.value = !menuAberto.value
}

const fecharMenu = () => {
  menuAberto.value = false
}

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
  fecharDropdown()
  fecharMenu()
  if (router.currentRoute.value.name !== 'login') {
    router.push({ name: 'login' })
  }
}
</script>

<template lang="pug">
div.layout
  header.navbar
    .container
      // Logo
      img.logo(src="@/assets/logocjl.png", alt="CJL logo", width="60", height="60")

      // Botão de abrir/fechar menu (visível no mobile)
      button.menu-toggle(@click="toggleMenu" aria-label="Alternar menu")
        i.fas(:class="menuAberto ? 'fa-times' : 'fa-bars'")

      // Menu completo (visível no desktop e aberto no mobile)
      nav.nav-content(:class="{ aberto: menuAberto }")
        nav.nav-menu
          RouterLink.nav-link(to="/Inicio" @click="fecharMenu") Início
          RouterLink.nav-link(to="/denuncia" @click="fecharMenu") Reclamacao/Denuncia

          // Administração - só para ADMINISTRATIVO e ADMIN, e empresa válida
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
            button.nav-button.dropdown-toggle(
              @click="toggleDropdown"
              tabindex="0"
              aria-haspopup="true"
              :aria-expanded="mostrarDropdown.toString()"
              type="button"
              title="Menu usuário"
              ref="buttonRef"
            )
              i.fas.fa-user-circle.icone-usuario
              |  {{ userName }}

            div.dropdown-menu(v-show="mostrarDropdown" ref="dropdownRef" tabindex="-1")
              .dropdown-header
                i.fas.fa-building
                span Empresa
              p.dropdown-info
                strong Nome:&nbsp;
                span {{ userEmpresa }}

              p.dropdown-info
                strong CNPJ:&nbsp;
                span {{ userEmpresacnpj }}

              hr.dropdown-divider

              RouterLink.nav-button.small-button(
                :to="`/empresa/${empresa.codigoPublico}/meus-dados`"
                @click="() => { fecharDropdown(); fecharMenu() }"
              )
                i.fas.fa-user-cog
                span Meus Dados

              button.nav-button.primary(type="button" @click="handleLogout")
                i.fas.fa-sign-out-alt
                span Sair


  main.main-content
    RouterView
    LoadingOverlay

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

/* NAVBAR */
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
  position: relative;
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
  align-items: center;
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
  cursor: pointer;
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
  background-color: #a2850f;
}

/* Seção do usuário no navbar */
.nav-user-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  gap: 1rem;
  min-width: 200px;
}

.nav-button.dropdown-toggle {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  background: transparent;
  border: none;
  color: #fff;
  cursor: pointer;
  font-weight: 600;
  font-size: 1rem;
  padding: 0.3rem 0.6rem;
  border-radius: 4px;
}

.nav-button.dropdown-toggle i {
  font-size: 1.3rem;
}

.nav-button.dropdown-toggle:focus {
  outline: 2px solid #886f0d;
  outline-offset: 2px;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  background-color: #1c1c1c;
  border: 1px solid #d4af37;
  border-radius: 10px;
  padding: 1rem;
  min-width: 260px;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.35);
  z-index: 1000;
  color: #f5f5f5;
  margin-top: 6px;
  font-size: 0.95rem;
}

.dropdown-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: bold;
  font-size: 1rem;
  color: #f0e68c;
  margin-bottom: 0.75rem;
}

.dropdown-info {
  margin: 0.2rem 0;
  font-size: 0.9rem;
  line-height: 1.4;
}

.dropdown-info strong {
  color: #fff;
  font-weight: 600;
}

.dropdown-divider {
  border: none;
  height: 1px;
  background-color: #444;
  margin: 0.8rem 0;
}

.dropdown-menu .nav-button {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-top: 0.5rem;
  padding: 0.5rem 0.8rem;
  font-size: 0.95rem;
  font-weight: 500;
}

.dropdown-menu .nav-button i {
  font-size: 1rem;
}

.dropdown-menu .nav-button.primary {
  background-color: #d4af37;
  color: #000;
  border-color: #d4af37;
}

.dropdown-menu .nav-button.primary:hover {
  background-color: #e5c94f;
}

.info-empresa {
  margin: 0 0 0.5rem 0;
  font-size: 0.9rem;
  color: #d4af37;
}

.info-empresa strong {
  color: #fff;
}

.dropdown-menu .nav-button {
  width: 100%;
  margin-top: 0.5rem;
}

.dropdown-menu .nav-button.primary {
  margin-top: 0.75rem;
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
    max-width: 100%;
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

.main-content {
  flex: 1;
  width: 100%;
  box-sizing: border-box;

  padding-left: 0px;
  padding-top: 0px;
  padding-bottom: 0px;

  padding-right: 0px;
}

footer.footer {
  background-color: black;
  color: white;
  text-align: center;
  height: 50px;
  line-height: 50px;
  width: 100%;
}
.icone-usuario {
  color: #d4af37;
  font-size: 1.4rem;
}
</style>
