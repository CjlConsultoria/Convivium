<template lang="pug">
section.home-container
  div.content

    h1.title Sistema de Gestão de Condomínios

    // Se NÃO estiver logado, mostra descrição e botões de acesso
    p.description(v-if="!logado")
      | Organize, controle e facilite a administração do seu condomínio com tecnologia e praticidade.

    div.actions(v-if="!logado")
      RouterLink.btn-primary(to="/login") Acessar o Sistema
      RouterLink.btn-secondary(to="/sobre") Saiba Mais

    // Se estiver logado, mostra mensagem e botão para denúncia/reclamação
    div.logged-in-message(v-else)
      p.description
        | Você já está logado! Quer fazer uma denúncia ou reclamação?
      RouterLink.btn-primary(to="/denuncia") Fazer Denúncia / Reclamação
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const logado = ref(false)

onMounted(() => {
  logado.value = !!localStorage.getItem('authToken')
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: url('@/assets/fundo.jpg') no-repeat center center/cover;
  padding: 1rem;
  text-align: center;
  color: #f0f0f0; /* texto claro */
  position: relative;
}

.home-container::before {
  content: '';
  position: absolute;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.5); /* camada escura */
  z-index: 0;
}

.content {
  position: relative;
  z-index: 1;
  max-width: 480px;
  width: 100%;
}

.title {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
  color: #f0f0f0;
}

.description {
  font-size: 1.125rem;
  margin-bottom: 2rem;
  color: #f0f0f0;
}

.actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.btn-primary,
.btn-secondary {
  padding: 0.75rem 2rem;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
  transition: background-color 0.3s ease;
  display: inline-block;
}

.btn-primary {
  background-color: #f2c200; /* amarelo vivo */
  color: #000;
}

.btn-primary:hover {
  background-color: #d9a500;
}

.btn-secondary {
  border: 2px solid #f2c200;
  color: #f2c200;
  background-color: transparent;
}

.btn-secondary:hover {
  background-color: rgba(242, 194, 0, 0.1);
}

.logged-in-message {
  font-size: 1.125rem;
  color: #f0f0f0;
  margin-top: 1.5rem;
  text-align: center;
}

.logged-in-message p {
  margin-bottom: 1rem;
}
</style>
