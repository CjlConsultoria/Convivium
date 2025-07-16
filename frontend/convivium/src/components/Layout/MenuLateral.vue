<template lang="pug">
aside.menu-lateral
  h3 Menu
  ul
    li(
      v-if="(perfilUsuario === 'ADMINISTRATIVO' || perfilUsuario === 'ADMIN') && empresa?.codigoPublico"
      :class="{ ativo: itemSelecionado === 'inicio' }"
    )
      RouterLink(:to="`/empresa/admin/admin`", @click.prevent="selecionar('inicio')") Início

    li.submenu-container(ref="submenuRef"
        @mouseenter="abrirSubmenu"
        @mouseleave="fecharSubmenu")
      
      button.submenu-toggle(type="button", @click="toggleSubmenu")
        | Administração
        span.seta(v-if="!submenuAberto") ▸
        span.seta(v-else) ▼

      ul.submenu-lateral(v-show="submenuAberto")
        li(:class="{ ativo: itemSelecionado === 'moradores' }")
          RouterLink(to="/moradores", @click.prevent="selecionar('moradores')") Moradores

        li(:class="{ ativo: itemSelecionado === 'reclamacoes' }")
          RouterLink(to="/reclamacoes", @click.prevent="selecionar('reclamacoes')") Reclamações

    li(v-if="isLoggedIn", :class="{ ativo: itemSelecionado === 'meus-dados' }")
      RouterLink(:to="`/empresa/${empresa?.codigoPublico}/meus-dados`", @click="fecharMenu") Meus Dados

    li(v-if="perfilUsuario === 'ADMIN'", :class="{ ativo: itemSelecionado === 'licenca' }")
      RouterLink(to="/licenca", @click="fecharMenu") Gestão de Licenças

    li(v-if="isLoggedIn")
      button.btn-sair(@click="sair") Sair


</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps<{ itemSelecionado: string }>()
const emit = defineEmits<{ (e: 'selecao', valor: string): void; (e: 'fechar-menu'): void }>()

const router = useRouter()

const submenuAberto = ref(false)

const authToken = ref<string | null>(null)
const perfilUsuario = ref<string>('PUBLICO')
const empresa = ref<{ codigoPublico?: string }>({})
const submenuRef = ref<HTMLElement | null>(null)
const isLoggedIn = computed(() => !!authToken.value)
function handleClickFora(event: MouseEvent) {
  const target = event.target as Node
  if (submenuRef.value && !submenuRef.value.contains(target)) {
    submenuAberto.value = false
  }
}
function updateUserData() {
  authToken.value = localStorage.getItem('authToken')
  perfilUsuario.value = localStorage.getItem('userPerfil') || 'PUBLICO'
  try {
    empresa.value = JSON.parse(localStorage.getItem('userEmpresa') || '{}')
  } catch {
    empresa.value = {}
  }
}
function emitirSelecao(valor: string) {
  emit('selecao', valor)
  submenuAberto.value = false
  emit('fechar-menu')

  // Navegar para a rota correta
  router.push(`/empresa/admin/${valor}`)
}

onMounted(() => {
  updateUserData()
  window.addEventListener('storage', updateUserData)
})

onUnmounted(() => {
  window.removeEventListener('storage', updateUserData)
})

onMounted(() => {
  document.addEventListener('click', handleClickFora)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickFora)
})

function sair() {
  localStorage.removeItem('authToken')
  localStorage.removeItem('userName')
  localStorage.removeItem('userPerfil')
  localStorage.removeItem('userEmpresa')
  router.push({ name: 'login' })
}

function abrirSubmenu() {
  submenuAberto.value = true
}

function fecharSubmenu() {
  submenuAberto.value = false
}

function toggleSubmenu() {
  submenuAberto.value = !submenuAberto.value
}

function selecionar(valor: string) {
  emit('selecao', valor) // avisa o pai
  fecharSubmenu() // fecha submenu
}

function fecharMenu() {
  submenuAberto.value = false
  emit('fechar-menu')
}
</script>

<style scoped>
.menu-lateral {
  width: 220px;
  max-width: 240px;
  min-width: 180px;
  background-color: #f9f7e8;
  border-right: 2px solid #d9c877;
  padding: 1.5rem 1rem;
  position: sticky;
  top: 0;
  height: 100vh;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  z-index: 10;
}

.menu-lateral h3 {
  font-size: 1.2rem;
  margin-bottom: 1.5rem;
  color: #b38600;
  line-height: 1.2;
}

.menu-lateral ul {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
  flex-grow: 1;
  position: relative;
}

.menu-lateral li,
.menu-lateral ul.submenu-lateral li {
  cursor: pointer;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  font-weight: 600;
  color: #333;
  transition: background 0.2s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  box-sizing: border-box;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.menu-lateral li:hover:not(.ativo),
.menu-lateral ul.submenu-lateral li:hover:not(.ativo) {
  background-color: #fff8d1;
}

.menu-lateral li.ativo,
.menu-lateral a.ativo {
  background-color: #b38600;
  color: white;
  pointer-events: none;
  border-radius: 8px;
  height: 2.5rem;
  display: flex;
  align-items: center;
}

/* Submenu lateral que abre para o lado */
.menu-lateral ul.submenu-lateral {
  position: absolute;
  top: 0;
  left: 100%;
  margin-left: 6px;
  background-color: #fff8d1;
  border: 1px solid #d9c877;
  box-shadow: 2px 2px 10px rgba(0, 0, 0, 0.05);
  border-radius: 8px;
  min-width: 180px;
  z-index: 20;
  padding: 0.5rem 0;
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  transition: opacity 0.2s ease;
}

/* Botão Administração */
.menu-lateral button.submenu-toggle {
  background: none;
  border: none;
  width: 100%;
  text-align: left;
  font-weight: 700;
  font-size: 1rem;
  padding: 0.5rem 1rem;
  cursor: pointer;
  color: #b38600;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-radius: 8px;
  box-sizing: border-box;
}

.menu-lateral button.submenu-toggle:hover {
  background-color: #fff8d1;
}

.menu-lateral .seta {
  font-size: 0.8rem;
  user-select: none;
  color: #b38600;
}

.menu-lateral a {
  color: inherit;
  text-decoration: none;
  font-weight: 600;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s ease;
  display: block;
  height: 2.5rem;
  line-height: 2.5rem;
  padding: 0 1rem;
  box-sizing: border-box;
}

.btn-sair {
  width: 100%;
  background: none;
  border: none;
  text-align: left;
  padding: 0.6rem 1rem;
  font-weight: 600;
  color: #a00000;
  cursor: pointer;
  border-radius: 8px;
  margin-top: auto;
  box-sizing: border-box;
}

.btn-sair:hover {
  background-color: #ffe0e0;
}
</style>
