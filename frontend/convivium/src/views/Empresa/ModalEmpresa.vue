<template lang="pug">
.modal-overlay
  .modal-container
    .modal-header
      h3 {{ localEmpresa.id ? 'Editar Empresa' : 'Adicionar Empresa' }}

    .modal-body
      form(@submit.prevent="onSubmit")
        label Nome *
        input(
          type="text"
          v-model="localEmpresa.name"
          required
          placeholder="Nome da empresa"
          :disabled="!!localEmpresa.id"
          class="bg-gray-100"
        )
        label CNPJ *
        input(
          type="text"
          v-model="localEmpresa.cnpj"
          maxlength="18"
          placeholder="00.000.000/0000-00"
          @input="formatarCnpj"
          :disabled="!!localEmpresa.id"
          class="bg-gray-100"
        )
        small.text-gray-500.block.mt-1(v-if="localEmpresa.id != null") Nome e CNPJ não podem ser editados após a criação.

        label CEP
        input(type="text" 
              v-model="localEmpresa.cep" 
              @blur="onCepBlur" 
              maxlength="9" 
              placeholder="CEP"
              @input="formatarCep"
        )

        label Logradouro
        input(type="text" v-model="localEmpresa.logradouro" placeholder="Logradouro")

        label Número
        input(type="text" v-model="localEmpresa.numero" placeholder="Número")

        label Complemento
        input(type="text" v-model="localEmpresa.complemento" placeholder="Complemento")

        label Bairro
        input(type="text" v-model="localEmpresa.bairro" placeholder="Bairro")

        label Cidade
        input(type="text" v-model="localEmpresa.cidade" placeholder="Cidade")

        label Estado *
        input(type="text" v-model="localEmpresa.estado" maxlength="2" required placeholder="UF")

    .modal-footer
      button.btn.btn-secondary(type="button" @click="$emit('close')") Cancelar
      button.btn.btn-primary(type="button" @click="onSubmit") Salvar

</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import type { Endereco } from '@/services/cepService'
import { buscarEnderecoPorCep } from '@/services/cepService'
import { criarEmpresaService, atualizarEmpresaService } from '@/services/empresaService'

import { toast } from 'vue3-toastify'
import { useLoadingStore } from '@/stores/loadingStore'
const store = useLoadingStore()

interface Empresa {
  id?: number
  name: string
  cnpj: string
  cep?: string
  logradouro?: string
  numero?: string
  complemento?: string
  bairro?: string
  cidade?: string
  estado?: string
}

export interface EmpresaCreateDTO {
  name: string
  cnpj: string
  cep?: string
  logradouro?: string
  numero?: string
  complemento?: string
  bairro?: string
  cidade?: string
  estado?: string
}

const props = defineProps<{
  empresa: Partial<Empresa> | null
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()

const localEmpresa = ref<Partial<Empresa>>(
  props.empresa
    ? { ...props.empresa }
    : {
        name: '',
        cnpj: '',
        cep: '',
        logradouro: '',
        numero: '',
        complemento: '',
        bairro: '',
        cidade: '',
        estado: '',
      },
)

watch(
  () => props.empresa,
  (newVal) => {
    localEmpresa.value = newVal
      ? { ...newVal }
      : {
          name: '',
          cnpj: '',
          cep: '',
          logradouro: '',
          numero: '',
          complemento: '',
          bairro: '',
          cidade: '',
          estado: '',
        }
  },
)

function formatarCnpj() {
  if (!localEmpresa.value.cnpj) return

  let v = localEmpresa.value.cnpj.replace(/\D/g, '')

  if (v.length > 14) v = v.slice(0, 14)

  v = v.replace(/^(\d{2})(\d)/, '$1.$2')
  v = v.replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3')
  v = v.replace(/\.(\d{3})(\d)/, '.$1/$2')
  v = v.replace(/(\d{4})(\d)/, '$1-$2')

  localEmpresa.value.cnpj = v
}

function formatarCep() {
  if (!localEmpresa.value.cep) return

  let v = localEmpresa.value.cep.replace(/\D/g, '')

  if (v.length > 8) v = v.slice(0, 8)

  v = v.replace(/^(\d{5})(\d)/, '$1-$2')

  localEmpresa.value.cep = v
}

async function onCepBlur() {
  if (!localEmpresa.value.cep) return

  const endereco: Endereco | null = await buscarEnderecoPorCep(localEmpresa.value.cep)

  if (endereco) {
    localEmpresa.value.logradouro = endereco.logradouro || ''
    localEmpresa.value.bairro = endereco.bairro || ''
    localEmpresa.value.cidade = endereco.localidade || ''
    localEmpresa.value.estado = endereco.uf || ''
  } else {
    toast.error('CEP não encontrado.')
  }
}

function validarCnpj(cnpj: string): boolean {
  const cnpjClean = cnpj.replace(/[^\d]+/g, '')

  if (cnpjClean.length !== 14) return false

  if (/^(\d)\1+$/.test(cnpjClean)) return false

  let tamanho = cnpjClean.length - 2
  let numeros = cnpjClean.substring(0, tamanho)
  let digitos = cnpjClean.substring(tamanho)
  let soma = 0
  let pos = tamanho - 7
  for (let i = tamanho; i >= 1; i--) {
    soma += parseInt(numeros.charAt(tamanho - i)) * pos--
    if (pos < 2) pos = 9
  }
  let resultado = soma % 11 < 2 ? 0 : 11 - (soma % 11)
  if (resultado !== parseInt(digitos.charAt(0))) return false

  tamanho += 1
  numeros = cnpjClean.substring(0, tamanho)
  soma = 0
  pos = tamanho - 7
  for (let i = tamanho; i >= 1; i--) {
    soma += parseInt(numeros.charAt(tamanho - i)) * pos--
    if (pos < 2) pos = 9
  }
  resultado = soma % 11 < 2 ? 0 : 11 - (soma % 11)
  if (resultado !== parseInt(digitos.charAt(1))) return false

  return true
}

async function onSubmit() {
  if (!localEmpresa.value.name || !localEmpresa.value.cnpj || !localEmpresa.value.estado) {
    toast.warning('Preencha os campos obrigatórios: Nome, CNPJ e Estado.')
    return
  }

  if (!validarCnpj(localEmpresa.value.cnpj)) {
    toast.error('CNPJ inválido.')
    return
  }

  const empresaParaSalvar = {
    cep: localEmpresa.value.cep?.replace(/\D/g, '') || '',
    logradouro: localEmpresa.value.logradouro || '',
    numero: localEmpresa.value.numero || '',
    complemento: localEmpresa.value.complemento || '',
    bairro: localEmpresa.value.bairro || '',
    cidade: localEmpresa.value.cidade || '',
    estado: localEmpresa.value.estado || '',
  }

  store.startLoading()
  try {
    if (localEmpresa.value.id) {
      // ✅ Atualização
      await atualizarEmpresaService(localEmpresa.value.id, empresaParaSalvar)
      toast.success('Empresa atualizada com sucesso!')
    } else {
      // ✅ Criação
      await criarEmpresaService({
        name: localEmpresa.value.name,
        cnpj: localEmpresa.value.cnpj.replace(/\D/g, ''),
        ...empresaParaSalvar,
      })
      toast.success('Empresa criada com sucesso!')
    }

    emit('close')
  } catch (error: any) {
    if (error?.response?.data?.message) {
      toast.error(error.response.data.message)
    } else {
      toast.error('Erro ao salvar empresa. Tente novamente.')
    }
  } finally {
    store.stopLoading()
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.15); /* mais suave */
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}

.modal-container {
  background: #fff;
  border: 2px solid #f5c518;
  border-radius: 8px;
  padding: 1.5rem;
  width: 100%;
  max-width: 600px;
  box-shadow: 0 4px 10px rgba(245, 197, 24, 0.3);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #333;
}

.modal-header h3 {
  margin-bottom: 1rem;
  color: #b37f00;
  font-weight: 700;
}

.modal-body form {
  display: flex;
  flex-direction: column;
}

label {
  margin-top: 1rem;
  font-weight: 600;
  color: #b37f00;
}

input {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 2px solid #f5c518;
  border-radius: 5px;
  background: #fff;
  color: #333;
  font-size: 1rem;
  margin-top: 0.25rem;
  box-sizing: border-box;
  transition: border-color 0.3s ease;
}

input:focus {
  outline: none;
  border-color: #d4af37;
  background: #fff9db;
}

.modal-footer {
  margin-top: 1.5rem;
  text-align: right;
}

button.btn {
  padding: 0.6rem 1.3rem;
  border-radius: 5px;
  font-weight: 700;
  cursor: pointer;
  border: none;
  transition: background-color 0.3s ease;
}

button.btn-primary {
  background-color: #f5c518;
  color: #4a3200;
  margin-left: 0.5rem;
}

button.btn-primary:hover {
  background-color: #d4af37;
}

button.btn-secondary {
  background-color: transparent;
  border: 2px solid #f5c518;
  color: #b37f00;
}

button.btn-secondary:hover {
  background-color: #f5c518;
  color: #4a3200;
}
</style>
