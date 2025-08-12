<template lang="pug">
.container
  h1.titulo Ativação de Conta
  p.subtitulo Defina sua senha de acesso para ativar sua conta.

  form(@submit.prevent="ativarContaForm")
    .form-group
      label(for="senha") Nova Senha
      input#senha(
        type="password"
        v-model="senha"
        required
        placeholder="Digite sua nova senha"
        @input="validarSenha"
      )

    .form-group
      label(for="confirmarSenha") Confirmar Senha
      input#confirmarSenha(
        type="password"
        v-model="confirmarSenha"
        required
        placeholder="Confirme sua senha"
        @input="validarSenha"
      )

    p.text-erro(v-if="erro") {{ erro }}

    button.btn-principal(type="submit" :disabled="loading || !senhasBatem")
      span(v-if="!loading") Ativar Conta
      span(v-else) Ativando...
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ativarConta } from '@/services/userService'
const senha = ref('')
const confirmarSenha = ref('')
const erro = ref('')
const loading = ref(false)

const route = useRoute()
const router = useRouter()

const idUsuario = Array.isArray(route.params.id) ? route.params.id[0] : (route.params.id ?? '')

const senhasBatem = computed(() => senha.value === confirmarSenha.value && senha.value.length > 0)

function validarSenha() {
  if (!senhasBatem.value) {
    erro.value = 'As senhas não coincidem.'
  } else {
    erro.value = ''
  }
}

async function ativarContaForm() {
  erro.value = ''
  if (!senhasBatem.value) {
    erro.value = 'As senhas não coincidem.'
    return
  }

  loading.value = true
  try {
    const token = localStorage.getItem('tokenConta')!
    await ativarConta(idUsuario, senha.value, token)
    router.push({ path: '/login', query: { ativado: 'true' } })
  } catch (e) {
    erro.value = 'Erro ao ativar conta. Tente novamente.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.container {
  max-width: 600px;
  min-height: 420px;
  margin: 5rem auto;
  padding: 4rem 3rem;
  background-color: #f9fafb;
  border-radius: 12px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.12);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.titulo {
  font-size: 2.5rem;
  font-weight: 800;
  color: #2c3e50;
  text-align: center;
  margin-bottom: 1rem;
}

.subtitulo {
  font-size: 1.3rem;
  font-weight: 500;
  color: #606f7b;
  text-align: center;
  margin-bottom: 3rem;
}

.form-group {
  margin-bottom: 2.5rem;
}

label {
  display: block;
  font-weight: 700;
  margin-bottom: 0.75rem;
  color: #34495e;
  font-size: 1.1rem;
}

input[type='password'] {
  width: 100%;
  padding: 1.2rem 1.5rem;
  font-size: 1.3rem;
  border: 2px solid #d1d5db;
  border-radius: 8px;
  transition: border-color 0.3s ease;
}

input[type='password']:focus {
  outline: none;
  border-color: #b38600; /* azul para foco */
  box-shadow: 0 0 8px rgba(199, 171, 11, 0.4);
}

.btn-principal {
  width: 100%;
  padding: 1.2rem 0;
  background-color: #b38600;
  color: white;
  font-weight: 700;
  font-size: 1.4rem;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: background-color 0.25s ease-in-out;
  user-select: none;
}

.btn-principal:disabled {
  background-color: #cfc299;
  cursor: not-allowed;
}

.btn-principal:hover:not(:disabled) {
  background-color: #b38600;
}

.text-erro {
  color: #ef4444;
  font-weight: 700;
  font-size: 1.1rem;
  margin-top: -1.5rem;
  margin-bottom: 1.8rem;
  text-align: center;
  user-select: none;
}
</style>
