<template lang="pug">
.container
  h2 Cadastro no Condomínio
  p Por favor, informe seu CPF para iniciar.

  form(@submit.prevent="verificarCPF")
    .form-group
      label(for="cpf") CPF
      input#cpf(
        type="text"
        v-model="cpfFormatted"
        required
        placeholder="Digite seu CPF"
        autocomplete="off"
        maxlength="14"
      )
      p.text-erro(v-if="cpfError") {{ cpfError }}

    button.btn-principal(type="submit" :disabled="loading || Boolean(cpfError)")
      span(v-if="!loading") Continuar
      span(v-else) Verificando...

  p.text-erro(v-if="erro") {{ erro }}
</template>

<script lang="ts" setup>
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { verificarCPFNoCondominio } from '@/services/condominioService'

const cpfRaw = ref('')
const cpfFormatted = ref('')
const cpfError = ref('')
const erro = ref('')
const loading = ref(false)
const router = useRouter()
const route = useRoute()

const idCondominioParam = route.params.id
const idCondominio = Array.isArray(idCondominioParam) ? idCondominioParam[0] : idCondominioParam

function formatCPF(value: string): string {
  return value
    .replace(/\D/g, '')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d{1,2})$/, '$1-$2')
}

function isValidCPF(cpf: string): boolean {
  cpf = cpf.replace(/[^\d]+/g, '')

  let sum = 0
  for (let i = 0; i < 9; i++) sum += parseInt(cpf.charAt(i)) * (10 - i)
  let rev = 11 - (sum % 11)
  if (rev === 10 || rev === 11) rev = 0
  if (rev !== parseInt(cpf.charAt(9))) return false

  sum = 0
  for (let i = 0; i < 10; i++) sum += parseInt(cpf.charAt(i)) * (11 - i)
  rev = 11 - (sum % 11)
  if (rev === 10 || rev === 11) rev = 0
  return rev === parseInt(cpf.charAt(10))
}

// Watcher para atualizar cpfRaw e validar
watch(cpfFormatted, (val) => {
  const digits = val.replace(/\D/g, '').slice(0, 11)
  cpfRaw.value = digits

  const formatted = formatCPF(digits)
  if (formatted !== val) {
    cpfFormatted.value = formatted
  }

  if (digits.length < 11) {
    cpfError.value = 'CPF inválido'
  } else if (/^(\d)\1{10}$/.test(digits)) {
    cpfError.value = ''
  } else {
    cpfError.value = !isValidCPF(digits) ? 'CPF inválido' : ''
  }
})

async function verificarCPF() {
  erro.value = ''
  loading.value = true

  try {
    const data = await verificarCPFNoCondominio(idCondominio, cpfRaw.value)

    if (data.status === 'ativo') {
      erro.value = 'Seu CPF já está ativo no sistema. Faça login normalmente.'
    } else if (data.status === 'pendente_ativacao') {
      const idUsuario = data.idUsuario || data.usuario?.id
      router.push(`/ativar-conta/${idUsuario}`)
    } else if (data.status === 'nao_encontrado') {
      erro.value = 'Seu CPF não está no sistema.'
    } else {
      erro.value = 'Resposta inesperada do servidor.'
    }
  } catch (e) {
    erro.value = 'Erro ao verificar CPF. Tente novamente.'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.container {
  max-width: 600px; /* maior largura */
  min-height: 450px; /* altura mínima */
  margin: 4rem auto; /* margem topo e base maior */
  padding: 3rem 2.5rem;
  background-color: #f9fafb;
  border-radius: 12px;
  box-shadow: 0 0 12px rgba(0, 0, 0, 0.12);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

h2 {
  color: #2c3e50;
  margin-bottom: 1rem;
  text-align: center;
  font-size: 2.25rem; /* maior */
  font-weight: 700;
}

.descricao {
  color: #34495e;
  margin-bottom: 2.5rem;
  text-align: center;
  font-size: 1.25rem;
  line-height: 1.4;
}

.form-group {
  margin-bottom: 2rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

label {
  font-weight: 600;
  color: #2c3e50;
  font-size: 1.15rem;
}

input[type='text'] {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1.5px solid #ccc;
  border-radius: 8px;
  font-size: 1.25rem;
  transition: border-color 0.3s;
}

input[type='text']:focus {
  border-color: #b38600;
  outline: none;
  box-shadow: 0 0 6px rgba(66, 185, 131, 0.4);
}

.btn-principal {
  display: block;
  width: 100%;
  padding: 1rem 0;
  background-color: #b38600;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1.3rem;
  font-weight: 700;
  cursor: pointer;
  transition: background-color 0.3s;
}

.btn-principal:disabled {
  background-color: #d1c5a2;
  cursor: not-allowed;
}

.btn-principal:hover:not(:disabled) {
  background-color: #b38600;
}

.text-erro {
  margin-top: 2rem;
  color: #e74c3c;
  font-weight: 700;
  font-size: 1.1rem;
  text-align: center;
}
</style>
