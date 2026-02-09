<template lang="pug">
  div(v-if="mostrarAviso" class="cookie-banner")
    p
      | Este site usa cookies para melhorar sua experiência. Ao continuar navegando, você concorda com
      | nossa
      a(href="/politica-de-privacidade" target="_blank" rel="noopener") Política de Privacidade
    div.botoes
      button(@click="aceitarCookies") Aceitar
      button(@click="recusarCookies") Recusar
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const mostrarAviso = ref(false)

function aceitarCookies() {
  localStorage.setItem('cookieAceito', 'true')
  mostrarAviso.value = false
}

function recusarCookies() {
  localStorage.setItem('cookieAceito', 'false')
  mostrarAviso.value = false
}

onMounted(() => {
  const aceito = localStorage.getItem('cookieAceito')
  if (aceito !== 'true') {
    mostrarAviso.value = true
  }
})
</script>

<style scoped>
.cookie-banner {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: var(--color-primary);
  color: var(--color-primary-text-on);
  padding: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  z-index: 9999;
  flex-wrap: wrap;
}

.cookie-banner a {
  color: var(--color-primary-text-on);
  text-decoration: underline;
}

.botoes {
  display: flex;
  gap: 0.75rem;
  margin-top: 0.5rem;
}

.cookie-banner button {
  background: var(--color-primary-active);
  color: var(--color-primary-light);
  border: none;
  border-radius: 8px;
  padding: 0.5rem 1rem;
  cursor: pointer;
  font-weight: 700;
  white-space: nowrap;
}

.cookie-banner button:hover {
  background: var(--color-primary-hover);
  color: var(--color-primary-text-on);
}
</style>
