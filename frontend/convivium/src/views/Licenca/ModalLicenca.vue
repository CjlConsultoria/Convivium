<template lang="pug">
  .modal-overlay
    .modal-container
      .modal-header
        h3 {{ localLicenca?.id ? 'Editar Licença' : 'Adicionar Licença' }}

      .modal-body
        form(@submit.prevent="onSubmit")
          // Empresa (exibida de forma visual apenas, sem edição)
          label Empresa *
          .form-pesquisa-container
            input.form-input(
              type="text"
              v-model="nomeBusca"
              placeholder="Digite o nome da empresa"
              @input="carregarEmpresas(0)"
            )
          select(v-model="localLicenca.empresaId" required)
            option(value="" disabled) Selecione uma empresa
            option(v-for="empresa in empresas" :key="empresa.id" :value="empresa.id")
              | {{ empresa.name }} ({{ empresa.cnpj || 'sem CNPJ' }})


          label Tipo *
          select(v-model="localLicenca.tipo" required)
            option(value="BASICA") Básica
            option(value="PREMIUM") Premium
            option(value="ILIMITADA") Ilimitada
            option(value="FULL") Full


          label Limite de Usuários *
          input(type="number" min="1" v-model="localLicenca.limiteUsuarios" required)

          label Data Início *
          input(type="date" v-model="localLicenca.dataInicio" required)

          label Data Fim *
          input(type="date" v-model="localLicenca.dataFim" required)

      .modal-footer
        button.btn.btn-secondary(@click="$emit('close')") Cancelar
        button.btn.btn-primary(type="button" @click="onSubmit") Salvar
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { fetchEmpresas } from '@/services/empresaService'
import type { Empresa, PaginatedResponse } from '@/services/empresaService'
import Multiselect from '@vueform/multiselect'
import '@vueform/multiselect/themes/default.css'
import { salvarLicenca } from '@/services/licencaService'
import { toast } from 'vue3-toastify'
import { useLoadingStore } from '@/stores/loadingStore'
const store = useLoadingStore()

const empresas = ref<Empresa[]>([])
const totalPaginas = ref(0)
const paginaAtual = ref(0)

const nomeBusca = ref('')
const cnpjBusca = ref('')

async function carregarEmpresas(pagina = 0) {
  try {
    const resposta: PaginatedResponse<Empresa> = await fetchEmpresas(
      pagina,
      nomeBusca.value,
      cnpjBusca.value,
    )
    empresas.value = resposta.content
    totalPaginas.value = resposta.totalPages
    paginaAtual.value = resposta.number
  } catch (err) {
    console.error('Erro ao buscar empresas:', err)
  }
}

onMounted(() => {
  carregarEmpresas(0)
})

interface Licenca {
  id?: number
  empresaId: number
  empresaNome?: string
  empresaCnpj?: string
  tipo: string // pode ser "FULL", "BASICA", "PREMIUM", "ILIMITADA" etc
  dataInicio: string
  dataFim: string
  limiteUsuarios: number
  ativa?: boolean
}

const props = defineProps<{
  licenca: Partial<Licenca> | null
  empresaId?: number
  getEmpresaNome: (empresaId: number) => string
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'save', licenca: Partial<Licenca>): void
}>()

const localLicenca = ref<Partial<Licenca>>(
  props.licenca
    ? { ...props.licenca }
    : {
        empresaId: props.empresaId ?? 0,
        tipo: 'BASICA',
        dataInicio: '',
        dataFim: '',
        limiteUsuarios: 5,
        ativa: true,
      },
)

watch(
  () => props.licenca,
  (newVal) => {
    if (!newVal) {
      localLicenca.value = {
        empresaId: props.empresaId,
        tipo: 'BASICA',
        dataInicio: '',
        dataFim: '',
        limiteUsuarios: 5,
        ativa: true,
      }
    } else {
      localLicenca.value = {
        ...newVal,
        dataInicio: normalizarParaInputDate(newVal.dataInicio), // já retorna yyyy-MM-dd
        dataFim: normalizarParaInputDate(newVal.dataFim), // já retorna yyyy-MM-dd
      }
    }
  },
  { immediate: true },
)

function normalizarParaInputDate(data: string | undefined): string {
  if (!data) return ''
  if (data.includes('T')) return data.substring(0, 10) // cortar parte da hora
  if (/^\d{4}-\d{2}-\d{2}$/.test(data)) return data
  return ''
}

async function onSubmit() {
  const lic = localLicenca.value

  if (!lic.tipo || !lic.dataInicio || !lic.dataFim || !lic.empresaId || !lic.limiteUsuarios) {
    toast.warning('Preencha todos os campos obrigatórios.')
    return
  }

  try {
    store.startLoading()
    await salvarLicenca(lic as any)
    toast.success('Licença salva com sucesso!')

    emit('close') // Fecha o modal após salvar
  } catch (err) {
    console.error(err)
    toast.error('Erro ao salvar licença.')
  } finally {
    store.stopLoading()
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-container {
  background: #fffbea;
  border: 2px solid #f5c518;
  border-radius: 10px;
  padding: 2rem;
  width: 100%;
  max-width: 600px;
  box-shadow: 0 8px 20px rgba(245, 197, 24, 0.6);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #4a3200;
}

.modal-header h3 {
  margin-bottom: 1rem;
  color: #b37f00;
  font-weight: bold;
}

.modal-body form {
  display: flex;
  flex-direction: column;
}

label {
  margin-top: 1rem;
  font-weight: 600;
  color: #8c6d00;
}

select,
input[type='date'] {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 2px solid #f5c518;
  border-radius: 4px;
  background: #fffbea;
  color: #5a3e00;
  font-size: 1rem;
  margin-top: 0.2rem;
}

select:focus,
input[type='date']:focus {
  outline: none;
  border-color: #d4af37;
  background: #fff9db;
}

.modal-footer {
  margin-top: 1.5rem;
  text-align: right;
}

button.btn {
  padding: 0.6rem 1.5rem;
  border-radius: 5px;
  font-weight: 600;
  cursor: pointer;
}

button.btn-primary {
  background-color: #f5c518;
  border: none;
  color: #4a3200;
  margin-left: 0.5rem;
}

button.btn-primary:hover {
  background-color: #d4af37;
  color: #3a2700;
}

button.btn-secondary {
  background-color: #fffbea;
  border: 2px solid #f5c518;
  color: #b37f00;
}

button.btn-secondary:hover {
  background-color: #f5c518;
  color: #4a3200;
}

.modal-body form {
  display: flex;
  flex-direction: column;
}

.form-pesquisa-container {
  display: flex;
  gap: 0.5rem;
  margin-top: 1rem;
  align-items: center;
}

/* Input de busca */
.form-pesquisa-container input.form-input {
  flex: 1;
  padding: 0.5rem 0.75rem;
  border: 2px solid #f5c518;
  border-radius: 4px;
  background: #fffbea;
  color: #5a3e00;
  font-size: 1rem;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  outline-offset: 0;
  transition:
    border-color 0.2s,
    background-color 0.2s;
}

.form-pesquisa-container input.form-input:focus {
  outline: none;
  border-color: #d4af37;
  background: #fff9db;
}

/* Botão pesquisar */
.form-pesquisa-container button.btn-pesquisar {
  background-color: #f5c518;
  border: none;
  color: #4a3200;
  font-weight: 600;
  padding: 0.5rem 1rem;
  border-radius: 5px;
  cursor: pointer;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  transition: background-color 0.2s;
}

.form-pesquisa-container button.btn-pesquisar:hover {
  background-color: #d4af37;
  color: #3a2700;
}

/* Mantém os labels com margem superior */
label {
  margin-top: 1.5rem;
  font-weight: 600;
  color: #8c6d00;
}

/* Select e input date simplificado */
select,
input[type='date'],
input[type='number'] {
  width: 100%;
  padding: 0.5rem 0.75rem;
  border: 2px solid #f5c518;
  border-radius: 4px;
  background: #fffbea;
  color: #5a3e00;
  font-size: 1rem;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  margin-top: 0.2rem;
  transition:
    border-color 0.2s,
    background-color 0.2s;
}

select:focus,
input[type='date']:focus,
input[type='number']:focus {
  outline: none;
  border-color: #d4af37;
  background: #fff9db;
}
</style>
