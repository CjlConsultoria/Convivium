<template lang="pug">
//- BACKDROP
.modal-backdrop(@click.self="close" role="dialog" aria-modal="true")
  .modal-content(tabindex="-1" ref="modalContent")

    //- HEADER
    header.modal-header
      h3 Novo Morador - Cadastro Completo
      button.btn-close(aria-label="Fechar modal" @click="close") &times;

    //- FORM
    form.modal-body(@submit.prevent="salvarMoradorCompleto")

      //- SEÇÃO: Dados de Acesso
      h4.secao-form Dados de Acesso
      .form-grid
        div
          label.obrigatorio Nome
          input(type="text" v-model="formMoradorCompleto.username" :disabled="formDesabilitado" required autocomplete="off")
        div
          label.obrigatorio Sobrenome
          input(type="text" v-model="formMoradorCompleto.sobrenome" :disabled="formDesabilitado" required autocomplete="off")
        div(style="display: flex; flex-direction: column; max-width: 250px;")
          div(style="display: flex; align-items: center; gap: 0.5rem;")
            label.obrigatorio Senha
            button.btn-gerar-senha(
              type="button"
              :disabled="senhaGerada || formDesabilitado"
              @click="gerarSenha"
            ) 🔑 Gerar Senha
          input(type="password" v-model="formMoradorCompleto.password" :disabled="senhaGerada || formDesabilitado" autocomplete="off")
          p.senha-sucesso(v-if="senhaGerada") Senha gerada com sucesso.

        div
          label.obrigatorio Email
          input(type="email" v-model="formMoradorCompleto.email" :disabled="formDesabilitado" required autocomplete="off")

      //- SEÇÃO: Dados Pessoais
      h4.secao-form Dados Pessoais
      .form-grid
        div
          label.obrigatorio CPF
          input(
            type="text"
            v-model="formMoradorCompleto.cpf"
            maxlength="14"
            required
            autocomplete="off"
            @blur="() => { validarCPF(); buscarUsuarioPorCpf() }"
          )
          span.error-message(v-if="cpfError") {{ cpfError }}
        div
          label.obrigatorio Telefone
          input(
            type="tel"
            v-model="formMoradorCompleto.telefone"
            maxlength="15"
            @input="formatarTelefoneMoradorCompleto"
            :disabled="formDesabilitado"
            required
            autocomplete="off"
          )
        div
          label.obrigatorio Gênero
          select(v-model="formMoradorCompleto.genero" :disabled="formDesabilitado" required)
            option(value="" disabled) Selecione
            option(value="Masculino") Masculino
            option(value="Feminino") Feminino
            option(value="Outro") Outro
        .div-centralizado-alerta
          span.text-sm.font-medium.text-gray-700 Alerta de Email
          input(type="checkbox" v-model="formMoradorCompleto.alerta" :disabled="formDesabilitado")

      //- SEÇÃO: Endereço
      h4.secao-form Endereço
      .div-centralizado
        span.nowrap.text-sm.font-medium.text-gray-700 Usar endereço da empresa
        input(type="checkbox" v-model="usarEnderecoEmpresa" :disabled="formDesabilitado")

      .form-grid
        div
          label.obrigatorio CEP
          input(
            type="text"
            v-model="formMoradorCompleto.cep"
            maxlength="8"
            @blur="buscarEnderecoViaCep"
            :disabled="usarEnderecoEmpresa || formDesabilitado"
            required
            autocomplete="off"
          )
        div
          label.obrigatorio Logradouro
          input(type="text" v-model="formMoradorCompleto.logradouro" :disabled="usarEnderecoEmpresa || formDesabilitado" required autocomplete="off")
        div
          label.obrigatorio Número
          input(type="text" v-model="formMoradorCompleto.numero" :disabled="usarEnderecoEmpresa || formDesabilitado" required autocomplete="off")
        div
          label.obrigatorio Complemento
          input(type="text" v-model="formMoradorCompleto.complemento" :disabled="formDesabilitado" autocomplete="off")
        div
          label.obrigatorio Bairro
          input(type="text" v-model="formMoradorCompleto.bairro" :disabled="usarEnderecoEmpresa || formDesabilitado" required autocomplete="off")
        div
          label.obrigatorio Cidade
          input(type="text" v-model="formMoradorCompleto.cidade" :disabled="usarEnderecoEmpresa || formDesabilitado" required autocomplete="off")
        div
          label.obrigatorio Estado
          input(type="text" v-model="formMoradorCompleto.estado" maxlength="2" :disabled="usarEnderecoEmpresa || formDesabilitado" required autocomplete="off")

      //- SEÇÃO: Informações Internas
      h4.secao-form Informações Internas
      .form-grid
        div
          label.obrigatorio Tipo
          select(v-model="formMoradorCompleto.tipo" :disabled="formDesabilitado" required)
            option(:value="null" disabled) Selecione o tipo
            option(v-for="tipo in tiposUsuario" :key="tipo.id" :value="tipo") {{ tipo.name }}
        div
          label.obrigatorio Empresa
          input(type="text" :value="formMoradorCompleto.empresa?.name || ''" disabled)
        div
          label.obrigatorio Permissão
          select(v-model="formMoradorCompleto.role" :disabled="formDesabilitado" required)
            option(:value="null" disabled) Selecione o perfil
            option(v-for="role in rolesUsuario" :key="role.id" :value="role") {{ role.name }}
        div
          label.obrigatorio Bloco
          input(type="text" v-model="formMoradorCompleto.bloco" :disabled="formDesabilitado" autocomplete="off")
        div
          label.obrigatorio Apartamento
          input(type="text" v-model="formMoradorCompleto.apartamento" :disabled="formDesabilitado" autocomplete="off")
        div
          label Vaga de Carro
          input(type="text" v-model="formMoradorCompleto.vagaCarro" :disabled="formDesabilitado" autocomplete="off")
        div
          label Vaga de Moto
          input(type="text" v-model="formMoradorCompleto.vagaMoto" :disabled="formDesabilitado" autocomplete="off")

    //- FOOTER
    footer.modal-footer
      button.btn-outline.btn-cancelar(@click="close") Cancelar
      button.btn-outline.btn-principal(type="button" @click="salvarMoradorCompleto" :disabled="formDesabilitado") Salvar
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, watch, computed } from 'vue'
import { buscarEnderecoPorCep } from '@/services/cepService'
import { toast } from 'vue3-toastify'
import { buscarUsuarioPorCpf as buscarUsuarioApi } from '@/services/userService'
import { useLoadingStore } from '@/stores/loadingStore'
const store = useLoadingStore()
interface Empresa {
  name?: string
  cep?: string
  logradouro?: string
  numero?: string
  bairro?: string
  cidade?: string
  estado?: string
}

interface FormMoradorCompleto {
  username: string
  sobrenome: string
  password: string
  email: string
  cpf: string
  telefone: string
  genero: string
  alerta: boolean
  cep: string
  logradouro: string
  numero: string
  complemento: string
  bairro: string
  cidade: string
  estado: string
  tipo: any | null
  empresa: Empresa | null
  role: any | null
  bloco: string
  apartamento: string
  vagaCarro: string
  vagaMoto: string
}
const camposObrigatoriosValidos = computed(() => {
  const f = formMoradorCompleto
  return (
    f.username.trim() !== '' &&
    f.sobrenome.trim() !== '' &&
    f.email.trim() !== '' &&
    f.cpf.replace(/\D/g, '').length === 11 &&
    f.telefone.trim() !== '' &&
    f.cep.trim() !== '' &&
    f.logradouro.trim() !== '' &&
    f.cidade.trim() !== '' &&
    f.estado.trim() !== '' &&
    f.bairro.trim() !== '' &&
    f.numero.trim() !== '' &&
    f.genero.trim() !== ''
  )
})
const props = defineProps<{
  modelValue: any
  tiposUsuario: { id: number; name: string }[]
  rolesUsuario: { id: number; name: string }[]
}>()

const emit = defineEmits(['update:modelValue', 'salvar', 'close'])
const cpfError = ref('')
const validarCPF = () => {
  const cpf = formMoradorCompleto.cpf
  if (!isValidCPF(cpf)) {
    cpfError.value = 'CPF inválido'
  } else {
    cpfError.value = ''
  }
}

const formDesabilitado = ref(true)
const senhaGerada = ref(false)
const usarEnderecoEmpresa = ref(false)
const modalContent = ref<HTMLElement | null>(null)

const formMoradorCompleto = reactive<FormMoradorCompleto>({
  username: '',
  sobrenome: '',
  password: '',
  email: '',
  cpf: '',
  telefone: '',
  genero: '',
  alerta: false,
  cep: '',
  logradouro: '',
  numero: '',
  complemento: '',
  bairro: '',
  cidade: '',
  estado: '',
  tipo: null,
  empresa: null,
  role: null,
  bloco: '',
  apartamento: '',
  vagaCarro: '', // << adicione aqui
  vagaMoto: '', // << e aqui
})

function close() {
  emit('close')
}
function formatCPF(value: string): string {
  return value
    .replace(/\D/g, '')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d{1,2})$/, '$1-$2')
}

watch(
  () => formMoradorCompleto.cpf,
  (val) => {
    formMoradorCompleto.cpf = formatCPF(val)
  },
)

async function buscarUsuarioPorCpf() {
  // Remove tudo que não for dígito
  const cpf = formMoradorCompleto.cpf.replace(/\D/g, '').trim()

  if (!cpf || cpf.length < 11) {
    toast.warn('Preencha o CPF corretamente antes de continuar.')
    return
  }

  try {
    store.startLoading()

    const usuario = await buscarUsuarioApi(cpf)

    Object.assign(formMoradorCompleto, {
      username: usuario.username || '',
      email: usuario.email || '',
      bloco: usuario.bloco || '',
      apartamento: usuario.apartamento || '',
    })

    formDesabilitado.value = false
    toast.success('Dados encontrados e carregados com sucesso.')
  } catch (error) {
    formDesabilitado.value = false
    toast.info('CPF não encontrado. Preencha os dados manualmente.')
    console.warn('Usuário não encontrado:', error)
  } finally {
    store.stopLoading()
  }
}

function isValidCPF(cpf: string): boolean {
  cpf = cpf.replace(/[^\d]+/g, '')
  if (cpf.length !== 11 || /^(\d)\1+$/.test(cpf)) return false

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

watch(usarEnderecoEmpresa, (usar) => {
  if (usar && formMoradorCompleto.empresa) {
    Object.assign(formMoradorCompleto, {
      cep: formMoradorCompleto.empresa.cep || '',
      logradouro: formMoradorCompleto.empresa.logradouro || '',
      numero: formMoradorCompleto.empresa.numero || '',
      bairro: formMoradorCompleto.empresa.bairro || '',
      cidade: formMoradorCompleto.empresa.cidade || '',
      estado: formMoradorCompleto.empresa.estado || '',
    })
  } else {
    Object.assign(formMoradorCompleto, {
      cep: '',
      logradouro: '',
      numero: '',
      bairro: '',
      cidade: '',
      estado: '',
    })
  }
})

function formatarTelefoneMoradorCompleto(e: Event) {
  const input = e.target as HTMLInputElement
  input.value = input.value
    .replace(/\D/g, '')
    .replace(/(\d{2})(\d)/, '($1) $2')
    .replace(/(\d{5})(\d)/, '$1-$2')
    .replace(/(-\d{4})\d+?$/, '$1')

  formMoradorCompleto.telefone = input.value
}

async function buscarEnderecoViaCep() {
  if (usarEnderecoEmpresa.value) return

  const endereco = await buscarEnderecoPorCep(formMoradorCompleto.cep)

  if (endereco) {
    formMoradorCompleto.logradouro = endereco.logradouro
    formMoradorCompleto.bairro = endereco.bairro
    formMoradorCompleto.cidade = endereco.localidade
    formMoradorCompleto.estado = endereco.uf
  } else {
    toast.warn('Erro ao buscar CEP.')
    console.warn('CEP inválido ou não encontrado')
  }
}

function salvarMoradorCompleto() {
  console.warn('Chamou o salvar.')
  emit('salvar', { ...formMoradorCompleto })
}

onMounted(() => {
  modalContent.value?.focus()
  const empresaStorage = localStorage.getItem('userEmpresa')

  if (empresaStorage) {
    try {
      const empresa = JSON.parse(empresaStorage)
      formMoradorCompleto.empresa = empresa
    } catch (err) {
      toast.error('Erro ao parsear empresa do localStorage.')
      console.error('Erro ao parsear empresa do localStorage:', err)
    }
  }
})

function gerarSenha() {
  if (senhaGerada.value) return

  const chars = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%&*?'
  let senha = ''
  for (let i = 0; i < 8; i++) {
    senha += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  formMoradorCompleto.password = senha
  senhaGerada.value = true
}
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  backdrop-filter: blur(5px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease forwards;
}

.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  backdrop-filter: blur(5px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease forwards;
}

.modal-content {
  background: white;
  border-radius: 16px;
  max-width: 680px;
  width: 95%;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.25);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  outline: none;
  animation: slideIn 0.35s ease forwards;
  padding: 0;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideIn {
  from {
    transform: translateY(-25px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-header {
  padding: 1.25rem 2rem;
  background: #b38600;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 1.25rem;
}

.btn-close {
  background: transparent;
  border: none;
  font-size: 2rem;
  cursor: pointer;
  line-height: 1;
  color: white;
  transition: color 0.2s ease;
}

.btn-close:hover,
.btn-close:focus {
  color: #f0f0f0;
  outline: none;
}

.modal-body {
  padding: 2rem;
  color: #333;
  font-size: 1.05rem;
  overflow-y: auto;
  max-height: 70vh;
  line-height: 1.6;
}

.modal-footer {
  padding: 1.25rem 2rem;
  background: #f6f6f6;
  color: #333;
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  font-size: 1rem;
}

.btn-outline.btn-principal {
  background-color: transparent;
  border: 2px solid #b38600;
  color: #b38600;
  padding: 0.8rem 1.6rem;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  transition:
    background-color 0.25s ease,
    box-shadow 0.25s ease;
}

.btn-outline.btn-principal:hover,
.btn-outline.btn-principal:focus {
  background-color: #b38600;
  color: white;
  box-shadow: 0 0 10px rgba(66, 185, 131, 0.5);
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideIn {
  from {
    transform: translateY(-25px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

h4.secao-form {
  margin-top: 1.5rem;
  font-size: 1.2rem;
  color: #b38600;
  border-bottom: 1px solid #ddd;
  padding-bottom: 0.4rem;
  margin-bottom: 0.8rem;
}

.btn-group {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.btn-edit {
  background-color: transparent;
  border: 2px solid #f1c40f;
  color: #f1c40f;
  font-weight: bold;
  padding: 0.4rem 1rem;
  border-radius: 10px;
  cursor: pointer;
  transition: 0.3s;
}

.btn-edit:hover {
  background-color: #f1c40f;
  color: white;
}

.btn-secundario {
  border-color: #999;
  color: #333;
}

.btn-secundario:hover {
  background-color: #999;
  color: white;
}
input {
  padding: 0.6rem 1rem;
  font-size: 1rem;
  border: 2px solid #ccc;
  border-radius: 10px;
  width: 100%;
  margin-top: 0.3rem;
  margin-bottom: 0.8rem;
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease;
}

input:focus {
  outline: none;
  border-color: #b38600;
  box-shadow: 0 0 5px rgba(66, 185, 131, 0.3);
}

/* Botão Cancelar */
.btn-outline.btn-cancelar {
  background-color: transparent;
  border: 2px solid #e74c3c;
  color: #e74c3c;
  padding: 0.8rem 1.6rem;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  transition:
    background-color 0.25s ease,
    box-shadow 0.25s ease;
}

.btn-outline.btn-cancelar:hover,
.btn-outline.btn-cancelar:focus {
  background-color: #e74c3c;
  color: white;
  box-shadow: 0 0 10px rgba(231, 76, 60, 0.5);
}

select {
  padding: 0.6rem 1rem;
  font-size: 1rem;
  border: 2px solid #ccc;
  border-radius: 10px;
  width: 100%;
  margin-top: 0.3rem;
  margin-bottom: 0.8rem;
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease;
  background-color: white;
  color: #333;
  appearance: none;
  background-image: url('data:image/svg+xml;charset=US-ASCII,<svg fill="gray" height="16" viewBox="0 0 20 20" width="16" xmlns="http://www.w3.org/2000/svg"><path d="M7 7l5 5 5-5z"/></svg>');
  background-repeat: no-repeat;
  background-position: right 0.75rem center;
  background-size: 1rem;
}

select:focus {
  outline: none;
  border-color: #b38600;
  box-shadow: 0 0 5px rgba(66, 185, 131, 0.3);
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1.25rem;
  margin-top: 1rem;
}

label {
  display: block;
  font-weight: 600;
  margin-bottom: 0.25rem;
  color: #333;
}

.checkbox-header {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 1rem;
}

.checkbox-center {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 600;
  color: #333;
}

.checkbox-inline {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 600;
  color: #333;
  margin-top: 0.5rem;
}

.div-centralizado {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  margin: 1rem 0;
  width: fit-content; /* Faz a div ocupar só o espaço necessário */
  margin-left: auto; /* Centraliza horizontalmente no container pai */
  margin-right: auto;
}

.div-centralizado-alerta {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  margin-top: 1.5rem;
  gap: 0.5rem;
}
.nowrap {
  white-space: nowrap;
}

.btn-gerar-senha {
  background-color: transparent;
  border: 2px solid #b38600;
  color: #b38600;
  font-weight: 600;
  padding: 0.4rem 0.8rem;
  border-radius: 10px;
  cursor: pointer;
  transition:
    background-color 0.25s ease,
    box-shadow 0.25s ease;
  font-size: 0.9rem;
  user-select: none;
}

.btn-gerar-senha:disabled {
  border-color: #aaa;
  color: #aaa;
  cursor: not-allowed;
  background-color: #f0f0f0;
  box-shadow: none;
}

.btn-gerar-senha:hover:not(:disabled),
.btn-gerar-senha:focus:not(:disabled) {
  background-color: #b38600;
  color: white;
  box-shadow: 0 0 10px rgba(66, 185, 131, 0.5);
  outline: none;
}

.senha-sucesso {
  color: #2e7d32; /* verde sucesso */
  font-weight: 600;
  margin-top: 0.5rem;
  font-size: 0.9rem;
}
label.obrigatorio::after {
  content: ' *';
  color: red;
  margin-left: 2px;
  font-weight: 700;
}
</style>
