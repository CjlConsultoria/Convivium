<template lang="pug">
div.modal-overlay(@click="$emit('close')")
  div.modal-container(@click.stop)
    header.modal-header
      h3 {{ localUsuario.id ? 'Editar Usuário' : 'Adicionar Usuário' }}

    main.modal-body
      form(@submit.prevent="onSubmit")

        label Nome *
        input(type="text" v-model="localUsuario.username" :disabled="!!localUsuario.id" placeholder="Nome")
        small.text-xs.text-gray-500(v-if="localUsuario.id") Nome não pode ser alterado após cadastro

        label Sobrenome *
        input(type="text" v-model="localUsuario.sobrenome" :disabled="!!localUsuario.id" placeholder="Sobrenome")
        small.text-xs.text-gray-500(v-if="localUsuario.id") Sobrenome não pode ser alterado após cadastro

        label Senha *
        input(type="password" v-model="localUsuario.senha" :disabled="!!localUsuario.id" placeholder="Senha")
        small.text-xs.text-gray-500(v-if="localUsuario.id") Senha padrão 123.

        label Email *
        input(type="email" v-model="localUsuario.email" :disabled="!!localUsuario.id" placeholder="email@exemplo.com")
        small.text-xs.text-gray-500(v-if="localUsuario.id") Email não pode ser alterado após cadastro

        label CPF *
        input(type="text"
              v-model="localUsuario.cpf"
              @input="formatarCpf"
              :disabled="!!localUsuario.id"
              maxlength="14"
              placeholder="CPF (somente números)")
        small.text-xs.text-gray-500(v-if="localUsuario.id") CPF não pode ser alterado após cadastro


        label Telefone *
        input(type="tel" v-model="localUsuario.telefone" required placeholder="Telefone")

        label CEP
        input(type="text" 
              v-model="localUsuario.cep" 
              @blur="onCepBlur" 
              maxlength="9" 
              placeholder="CEP"
              @input="formatarCep"
        )

        label Logradouro *
        input(type="text" v-model="localUsuario.logradouro" required placeholder="Rua, Avenida, etc.")

        label Cidade *
        input(type="text" v-model="localUsuario.cidade" required placeholder="Cidade")

        label Estado *
        input(type="text" v-model="localUsuario.estado" required placeholder="Estado")

        label Bairro *
        input(type="text" v-model="localUsuario.bairro" required placeholder="Bairro")

        label Número *
        input(type="text" v-model="localUsuario.numero" required placeholder="Número")

        label Complemento
        input(type="text" v-model="localUsuario.complemento" placeholder="Complemento (opcional)")

        label Gênero *
        select(v-model="localUsuario.genero" required)
          option(value="" disabled) Selecione o gênero
          option(value="Masculino") Masculino
          option(value="Feminino") Feminino
          option(value="Outro") Outro
          option(value="Prefiro não informar") Prefiro não informar

        label Alerta *
        input(type="checkbox" v-model="localUsuario.alerta")

        label Ativo *
        input(type="checkbox" v-model="localUsuario.ativo")

        label Bloco *
        input(type="text" v-model="localUsuario.bloco" required placeholder="Bloco")

        label Apartamento *
        input(type="text" v-model="localUsuario.apartamento" required placeholder="Apartamento")

        label Vaga Carro *
        input(type="text" v-model="localUsuario.vagaCarro" required placeholder="Vaga Carro")

        label Vaga Moto *
        input(type="text" v-model="localUsuario.vagaMoto" required placeholder="Vaga Moto")

        label Empresa *
        select(v-model="localUsuario.empresa" required)
          option(value="" disabled) Selecione uma empresa
          option(v-for="e in empresas" :key="e.id" :value="e.id") {{ e.name }}

        label Tipo *
        select(v-model="localUsuario.tipoUsuario" required)
          option(value="" disabled) Selecione o tipo
          option(v-for="t in tipos" :key="t.id" :value="t.id") {{ t.name }}

        label Role *
        select(v-model="localUsuario.role" required)
          option(value="" disabled) Selecione o role
          option(v-for="r in roles" :key="r.id" :value="r.id") {{ r.name }}


    footer.modal-footer
      button.btn.btn-secondary(type="button" @click="$emit('close')") Cancelar
      button.btn.btn-primary(type="button" @click="onSubmit") Salvar
</template>
<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { fetchEmpresas } from '@/services/empresaService'
import { fetchTiposUsuario, fetchRolesUsuario } from '@/services/userService'
import { salvarMoradorCompleto, atualizarUsuario } from '@/services/authService'
import { buscarEnderecoPorCep } from '@/services/cepService' // Supondo que tenha esta função
import type { MoradorPayload } from '@/services/authService'
import { toast } from 'vue3-toastify'
import { useLoadingStore } from '@/stores/loadingStore'
const store = useLoadingStore()

interface Empresa {
  id: number
  name: string
}

interface Usuario {
  id?: number
  username: string
  sobrenome: string
  senha: string
  email: string
  cpf: string
  telefone: string
  cep: string
  logradouro: string
  cidade: string
  estado: string
  bairro: string
  numero: string
  complemento?: string
  genero: string
  alerta: boolean
  ativo: boolean
  status: string
  bloco: string
  apartamento: string
  vagaCarro: string
  vagaMoto: string
  tipoUsuario: number
  role: number
  empresa: number
}

interface Props {
  usuario: Partial<Usuario> | null
}

const props = defineProps<Props>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'save', usuario: Partial<Usuario>): void
}>()

// Listas para selects
const empresas = ref<Empresa[]>([])
const tipos = ref<{ id: number; name: string }[]>([])
const roles = ref<{ id: number; name: string }[]>([])

async function carregarListas() {
  try {
    const empresasResponse = await fetchEmpresas(0)
    empresas.value = empresasResponse.content
    tipos.value = await fetchTiposUsuario()
    roles.value = await fetchRolesUsuario()
  } catch {
    toast.error('Erro ao carregar listas para o modal')
  }
}

const localUsuario = ref<Partial<Usuario>>(
  props.usuario
    ? { ...props.usuario }
    : {
        username: '',
        sobrenome: '',
        senha: '',
        email: '',
        cpf: '',
        telefone: '',
        cep: '',
        logradouro: '',
        cidade: '',
        estado: '',
        bairro: '',
        numero: '',
        complemento: '',
        genero: '',
        alerta: true,
        ativo: true,
        status: '',
        bloco: '',
        apartamento: '',
        vagaCarro: '',
        vagaMoto: '',
        tipoUsuario: 0,
        role: 0,
        empresa: 0,
      },
)

interface IdName {
  id: number
  name: string
}

interface UsuarioPossivel {
  empresa?: number | IdName | null
  tipoUsuario?: number | IdName | null
  role?: number | IdName | null
  [key: string]: any
}

interface UsuarioComTipo extends Partial<Usuario> {
  tipo?: string
}

async function prepararModal() {
  try {
    await carregarListas() // espera as listas carregarem
    if (props.usuario) {
      const newVal = props.usuario as UsuarioComTipo

      function buscarIdPorNome(
        lista: IdName[],
        nome: string | undefined | null,
      ): number | undefined {
        if (!nome) return undefined
        const item = lista.find((i) => i.name === nome)
        return item ? item.id : undefined
      }

      localUsuario.value = {
        ...newVal,
        empresa:
          typeof newVal.empresa === 'object' && newVal.empresa !== null
            ? (newVal.empresa as IdName).id
            : typeof newVal.empresa === 'string'
              ? buscarIdPorNome(empresas.value, newVal.empresa)
              : typeof newVal.empresa === 'number'
                ? newVal.empresa
                : undefined,

        tipoUsuario: (() => {
          const val = newVal.tipoUsuario ?? newVal.tipo // tenta tipoUsuario, se não tiver pega tipo
          if (typeof val === 'object' && val !== null) {
            return (val as IdName).id
          }
          if (typeof val === 'string') {
            const id = buscarIdPorNome(tipos.value, val)
            return id
          }
          if (typeof val === 'number') {
            return val
          }
          return undefined
        })(),

        role:
          typeof newVal.role === 'object' && newVal.role !== null
            ? (newVal.role as IdName).id
            : typeof newVal.role === 'string'
              ? buscarIdPorNome(roles.value, newVal.role)
              : typeof newVal.role === 'number'
                ? newVal.role
                : undefined,
      }
    } else {
      // Novo usuário, limpar campos
      localUsuario.value = {
        username: '',
        sobrenome: '',
        senha: '',
        email: '',
        cpf: '',
        telefone: '',
        cep: '',
        logradouro: '',
        cidade: '',
        estado: '',
        bairro: '',
        numero: '',
        complemento: '',
        genero: '',
        alerta: true,
        ativo: true,
        status: '',
        bloco: '',
        apartamento: '',
        vagaCarro: '',
        vagaMoto: '',
        tipoUsuario: 0,
        role: 0,
        empresa: 0,
      }
    }
  } catch (error) {
    toast.error('Erro ao preparar modal')
  }
}

onMounted(() => {
  prepararModal()
})

// Formata o CEP (XXXXX-XXX)
function formatarCep() {
  if (!localUsuario.value.cep) return
  let v = localUsuario.value.cep.replace(/\D/g, '')
  if (v.length > 8) v = v.slice(0, 8)
  v = v.replace(/^(\d{5})(\d)/, '$1-$2')
  localUsuario.value.cep = v
}

// Busca endereço via CEP na API e preenche campos
async function onCepBlur() {
  if (!localUsuario.value.cep) return

  const endereco = await buscarEnderecoPorCep(localUsuario.value.cep)
  if (endereco) {
    localUsuario.value.logradouro = endereco.logradouro || ''
    localUsuario.value.bairro = endereco.bairro || ''
    localUsuario.value.cidade = endereco.localidade || ''
    localUsuario.value.estado = endereco.uf || ''
  } else {
    toast.error('CEP não encontrado.')
  }
}

// Função simples para validar CPF (apenas formato e dígitos, não algoritmo)
function validarCpf(cpf: string): boolean {
  const cpfLimpo = cpf.replace(/\D/g, '')
  if (cpfLimpo.length !== 11) return false
  // Aqui pode colocar validação mais robusta se quiser
  return true
}

// Formata CPF como XXX.XXX.XXX-XX
function formatarCpf() {
  if (!localUsuario.value.cpf) return
  let v = localUsuario.value.cpf.replace(/\D/g, '')
  if (v.length > 11) v = v.slice(0, 11)
  v = v
    .replace(/^(\d{3})(\d)/, '$1.$2')
    .replace(/^(\d{3})\.(\d{3})(\d)/, '$1.$2.$3')
    .replace(/^(\d{3})\.(\d{3})\.(\d{3})(\d)/, '$1.$2.$3-$4')
  localUsuario.value.cpf = v
}

async function onSubmit() {
  // Validação campos obrigatórios (ajustar conforme seu modelo)
  const obrigatorios = [
    'username',
    'sobrenome',
    'email',
    'cpf',
    'telefone',
    'cep',
    'logradouro',
    'cidade',
    'estado',
    'bairro',
    'numero',
    'genero',
    'tipoUsuario',
    'role',
    'empresa',
  ]

  for (const campo of obrigatorios) {
    const valor = localUsuario.value[campo as keyof typeof localUsuario.value]
    if (
      valor === undefined ||
      valor === null ||
      (typeof valor === 'string' && valor.trim() === '')
    ) {
      toast.warning(`Campo obrigatório "${campo}" não preenchido.`)
      return
    }
  }

  // Validar CPF
  if (!validarCpf(localUsuario.value.cpf || '')) {
    toast.warning('CPF inválido.')
    return
  }

  const payload: MoradorPayload = {
    username: localUsuario.value.username || '',
    password: '123',
    email: localUsuario.value.email || '',
    cpf: localUsuario.value.cpf?.replace(/\D/g, '') || '', // envia só números
    tipoUsuario: Number(localUsuario.value.tipoUsuario),
    ativo: localUsuario.value.ativo || false,
    sobrenome: localUsuario.value.sobrenome || '',
    telefone: localUsuario.value.telefone || '',
    cep: localUsuario.value.cep || '',
    logradouro: localUsuario.value.logradouro || '',
    cidade: localUsuario.value.cidade || '',
    estado: localUsuario.value.estado || '',
    bairro: localUsuario.value.bairro || '',
    numero: localUsuario.value.numero || '',
    complemento: localUsuario.value.complemento || '',
    genero: localUsuario.value.genero || '',
    alerta: localUsuario.value.alerta || false,
    bloco: localUsuario.value.bloco || '',
    apartamento: localUsuario.value.apartamento || '',
    vagaCarro: localUsuario.value.vagaCarro || '',
    vagaMoto: localUsuario.value.vagaMoto || '',
    role: Number(localUsuario.value.role),
    empresa: Number(localUsuario.value.empresa),
  }

  try {
    store.startLoading()
    if (localUsuario.value.id) {
      await atualizarUsuario(localUsuario.value.id.toString(), payload)
    } else {
      await salvarMoradorCompleto(payload)
    }
    store.stopLoading()

    toast.success('Usuário salvo com sucesso!')

    emit('close')
  } catch (error: any) {
    store.stopLoading()

    toast.error(
      'Erro ao salvar usuário: ' +
        (error?.response?.data?.message || error?.message || 'Erro desconhecido'),
    )
  } finally {
    store.stopLoading()
  }
}
</script>

<style scoped>
/* Use seu estilo usual do modal para manter padrão */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.15);
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
  max-width: 700px;
  box-shadow: 0 4px 10px rgba(245, 197, 24, 0.3);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #333;
  max-height: 90vh;
  overflow-y: auto;
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

input,
select {
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

input:focus,
select:focus {
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
