<template lang="pug">
section.app-container
  MenuLateral(:itemSelecionado="itemSelecionado" @selecao="onMenuSelecionado")
  section.licenca-container
    h2.text-xl.font-bold.mb-6 Gestão de Licenças

    // Empresas
    .card.mb-8
      h3.text-lg.font-semibold.mb-4 Empresas
      .flex.items-center.mb-4
        .input-container.flex.items-center.flex-grow
          i.fa.fa-search.mr-2
          input.input-pesquisa(
            type="text"
            placeholder="Buscar por nome ou CNPJ"
            v-model="buscaEmpresa"
            @input="debounceEmpresa()"
          )
        button.btn.btn-yellow.ml-4(@click="abrirModalEmpresa()")
          i.fa.fa-plus.mr-2
          | Adicionar Empresa
      .table-wrapper-empresa
        table.table
          thead
            tr
              th Nome
              th CNPJ
              th Ações
          tbody
            tr(v-if="!empresas?.content || empresas.content.length === 0")
              td(colspan="3") Nenhuma empresa cadastrada.
            tr(v-for="empresa in empresas.content" :key="empresa.id")
              td(data-label="Nome") {{ empresa.name }}
              td(data-label="CNPJ") {{ empresa.cnpj }}
              td
                button.btn.btn-yellow.btn-sm(@click="abrirModalEmpresa(empresa)")
                  i.fa.fa-pencil-alt.mr-1
                  | Editar
                button.btn.btn-danger.btn-sm(@click="confirmarExclusao('empresa', empresa)")
                  i.fa.fa-trash.mr-1
                  | Excluir

      .pagination
        button.btn.btn-sm(@click="carregarEmpresas(0)" :disabled="empresas?.first") « Primeira
        button.btn.btn-sm(@click="carregarEmpresas(empresas.number - 1)" :disabled="empresas?.first") ‹
        span Página {{ empresas.number + 1 }} de {{ empresas.totalPages }}
        button.btn.btn-sm(@click="carregarEmpresas(empresas.number + 1)" :disabled="empresas?.last") ›
        button.btn.btn-sm(@click="carregarEmpresas(empresas.totalPages - 1)" :disabled="empresas?.last") Última »

    // Usuários
    .card.mb-8
      h3.text-lg.font-semibold.mb-4 Usuários
      .flex.items-center.mb-4
        .input-container.flex.items-center.flex-grow
          i.fa.fa-search.mr-2
          input.input-pesquisa(
            type="text"
            placeholder="Buscar por nome ou CPF"
            v-model="buscaUsuario"
            @input="debounceUsuario()"
          )
        button.btn.btn-yellow.ml-4(@click="abrirModalUsuario()")
          i.fa.fa-plus.mr-2
          | Adicionar Usuário

      .table-wrapper-usuario
        table.table
          thead
            tr
              th Nome
              th Email
              th Empresa
              th Ações
          tbody
            tr(v-if="usuariosCompletos?.content?.length === 0")
              td(colspan="4") Nenhum usuário cadastrado.
            tr(v-for="usuario in usuariosCompletos.content" :key="usuario.id")
              td(data-label="Nome") {{ usuario.username }}
              td(data-label="Email") {{ usuario.email }}
              td(data-label="Empresa") {{ usuario.empresa }}
              td(data-label="Ações")
                button.btn.btn-yellow.btn-sm(@click="abrirModalUsuario(usuario)")
                  i.fa.fa-pencil-alt.mr-1
                  | Editar
                button.btn.btn-danger.btn-sm(@click="confirmarExclusao('usuario', usuario)")
                  i.fa.fa-trash.mr-1
                  | Excluir

      .pagination
        button.btn.btn-sm(@click="carregarUsuariosCompletos(0)" :disabled="usuariosCompletos?.first") « Primeira
        button.btn.btn-sm(@click="carregarUsuariosCompletos(usuariosCompletos.number - 1)" :disabled="usuariosCompletos?.first") ‹
        span Página {{ (usuariosCompletos?.number ?? 0) + 1 }} de {{ usuariosCompletos?.totalPages ?? 0 }}
        button.btn.btn-sm(@click="carregarUsuariosCompletos(usuariosCompletos.number + 1)" :disabled="usuariosCompletos?.last") ›
        button.btn.btn-sm(@click="carregarUsuariosCompletos((usuariosCompletos?.totalPages ?? 1) - 1)" :disabled="usuariosCompletos?.last") Última »

    // Licenças
    .card
      h3.text-lg.font-semibold.mb-4 Licenças
      .flex.items-center.mb-4
        .input-container.flex.items-center.flex-grow
          i.fa.fa-search.mr-2
          input.input-pesquisa(
            type="text"
            placeholder="Buscar por nome da Empresa"
            v-model="buscalicenca"
            @input="debounceLicenca()"
          )
        button.btn.btn-yellow.ml-4(@click="abrirModalLicenca()")
          i.fa.fa-plus.mr-2
          | Adicionar Licença
      .table-wrapper-licencas
        table.table
          thead
            tr
              th Empresa
              th Tipo
              th Data Início
              th Data Fim
              th Ações
          tbody
            tr(v-if="licencas?.content?.length === 0")
              td(colspan="5") Nenhuma licença cadastrada.
            tr(v-for="licenca in licencas.content" :key="licenca.id")
              td(data-label="Licenca") {{ licenca.empresaNome }} ({{ formatarCnpj(licenca.empresaCnpj) }})
              td(data-label="Tipo") {{ formatarTipo(licenca.tipo) }}
              td(data-label="Data Inicio") {{ formatarData(licenca.dataInicio) }}
              td(data-label="Data Fim") {{ formatarData(licenca.dataFim) }}
              td
                button.btn.btn-yellow.btn-sm(@click="abrirModalLicenca(licenca)")
                  i.fa.fa-pencil-alt.mr-1
                  | Editar
                button.btn.btn-danger.btn-sm(@click="confirmarExclusao('licenca', licenca)")
                  i.fa.fa-trash.mr-1
                  | Excluir

      .pagination
        button.btn.btn-sm(@click="carregarLicencas(0)" :disabled="licencas?.first") « Primeira
        button.btn.btn-sm(@click="carregarLicencas(licencas.number - 1)" :disabled="licencas?.first") ‹
        span Página {{ licencas.number + 1 }} de {{ licencas.totalPages }}
        button.btn.btn-sm(@click="carregarLicencas(licencas.number + 1)" :disabled="licencas?.last") ›
        button.btn.btn-sm(@click="carregarLicencas(licencas.totalPages - 1)" :disabled="licencas?.last") Última »

    // Modais (sem alterações, mantidos íntegros)
    ModalEmpresa(
      v-if="modalEmpresaAberto"
      :empresa="empresaSelecionada"
      @close="fecharModalEmpresa"
    )

    ModalLicenca(
      v-if="modalLicencaAberto"
      :licenca="licencaSelecionada"
      :getEmpresaNome="getEmpresaNome"
      @close="fecharModalLicenca"
      @salvo="() => carregarLicencas(licencas.number)"
    )

    ModalConfirmacao(
      v-if="modalConfirmacaoAberto"
      :item="itemParaExcluir"
      :tipo="tipoParaExcluir"
      @close="fecharModalConfirmacao"
      @confirmar="excluirItem"
    )

    ModalUsuario(
      v-if="modalUsuarioAberto"
      :usuario="usuarioSelecionado"
      :empresas="empresas?.content || []"
      @close="fecharModalUsuario"
      @salvo="() => carregarUsuariosCompletos(usuarios.number)"
    )
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'

const DEBOUNCE_MS = 350
function debounce(fn: () => void, ms: number) {
  let t: ReturnType<typeof setTimeout>
  return () => {
    clearTimeout(t)
    t = setTimeout(fn, ms)
  }
}
import { fetchUsuarios, deletarUsuario } from '@/services/authService' // ajuste o caminho se necessário
import { listarLicencas, excluirLicenca } from '@/services/licencaService' // já está importado no seu código

import ModalEmpresa from '@/views/Empresa/ModalEmpresa.vue'
import ModalUsuario from '@/views/Usuario/ModalUsuario.vue'
import ModalLicenca from '@/views/Licenca/ModalLicenca.vue'
import ModalConfirmacao from '@/views/components/ModalConfirmacao.vue'
import { fetchEmpresas, excluirEmpresaService } from '@/services/empresaService'
import { toast } from 'vue3-toastify'
import { useLoadingStore } from '@/stores/loadingStore'
import MenuLateral from '@/components/Layout/MenuLateral.vue'

const store = useLoadingStore()
const buscaEmpresa = ref('')
const buscaUsuario = ref('')
const buscalicenca = ref('')
const itemSelecionado = ref('licencas') // ou o item padrão que quiser marcar ativo

interface UsuarioCompleto {
  id: number
  username: string
  email: string
  cpf: string
  telefone: string
  ativo: boolean
  sobrenome: string
  genero: string
  cep: string
  logradouro: string
  cidade: string
  estado: string
  bairro: string
  numero: string
  complemento: string
  alerta: boolean
  bloco: string
  apartamento: string
  vagaCarro: string
  vagaMoto: string
  role: string
  tipo: string
  empresa: string
}

interface Empresa {
  id: number
  name: string
  cnpj: string
}

interface Usuario {
  id: number
  nome: string
  email: string
  empresaId: number
}

interface Licenca {
  id: number
  empresaId: number
  empresaNome?: string
  empresaCnpj?: string
  tipo: string
  dataInicio: string
  dataFim: string
  ativa?: boolean
}

interface PaginatedResponse<T> {
  content: T[]
  totalPages: number
  number: number
  first: boolean
  last: boolean
}

const empresas = reactive<PaginatedResponse<Empresa>>({
  content: [],
  totalPages: 0,
  number: 0,
  first: true,
  last: true,
})

const usuarios = reactive<PaginatedResponse<Usuario>>({
  content: [],
  totalPages: 0,
  number: 0,
  first: true,
  last: true,
})

const licencas = reactive<PaginatedResponse<Licenca>>({
  content: [],
  totalPages: 0,
  number: 0,
  first: true,
  last: true,
})

const usuariosCompletos = reactive<PaginatedResponse<UsuarioCompleto>>({
  content: [],
  totalPages: 0,
  number: 0,
  first: true,
  last: true,
})

const modalEmpresaAberto = ref(false)
const modalUsuarioAberto = ref(false)
const modalLicencaAberto = ref(false)
const modalConfirmacaoAberto = ref(false)

const empresaSelecionada = ref<Empresa | null>(null)
const usuarioSelecionado = ref<Usuario | null>(null)
const licencaSelecionada = ref<Licenca | null>(null)
const itemParaExcluir = ref<any>(null)
const tipoParaExcluir = ref<string>('')

const mapaEmpresasPorId = reactive<Record<number, string>>({})
const mapaUsuariosPorId = reactive<Record<number, string>>({})

function mapaEmpresasPorIdClear() {
  Object.keys(mapaEmpresasPorId).forEach((key) => delete mapaEmpresasPorId[+key])
}

function mapaUsuariosPorIdClear() {
  Object.keys(mapaUsuariosPorId).forEach((key) => delete mapaUsuariosPorId[+key])
}

async function carregarEmpresas(pagina: number) {
  try {
    const termo = buscaEmpresa.value.trim()
    const response = await fetchEmpresas(pagina, termo, termo)
    Object.assign(empresas, response)

    mapaEmpresasPorIdClear()
    response.content.forEach((e) => (mapaEmpresasPorId[e.id] = e.name))
  } catch (error) {
    console.error('Erro ao carregar empresas:', error)
  }
}

async function carregarUsuariosCompletos(pagina: number) {
  try {
    const termo = buscaUsuario.value.trim()
    const response = await fetchUsuarios({
      page: pagina,
      size: 10,
      nome: termo,
      cpf: termo,
    })

    Object.assign(usuariosCompletos, response)
  } catch (error) {
    console.error('Erro ao carregar usuários completos:', error)
  }
}

async function carregarLicencas(pagina: number) {
  try {
    const empresaNomeFiltro = buscalicenca.value.trim() || undefined

    const response = await listarLicencas(pagina, 10, empresaNomeFiltro)

    Object.assign(licencas, response)
  } catch (error) {
    console.error('Erro ao carregar licenças:', error)
  }
}

function abrirModalEmpresa(empresa: Empresa | null = null) {
  empresaSelecionada.value = empresa ? { ...empresa } : null
  modalEmpresaAberto.value = true
}

function fecharModalEmpresa() {
  modalEmpresaAberto.value = false
  empresaSelecionada.value = null
}

function abrirModalUsuario(usuario: Usuario | null = null) {
  usuarioSelecionado.value = usuario ? { ...usuario } : null
  modalUsuarioAberto.value = true
}

function fecharModalUsuario() {
  modalUsuarioAberto.value = false
  usuarioSelecionado.value = null
}

function abrirModalLicenca(licenca: Licenca | null = null) {
  licencaSelecionada.value = licenca ? { ...licenca } : null
  modalLicencaAberto.value = true
}

function fecharModalLicenca() {
  modalLicencaAberto.value = false
  licencaSelecionada.value = null
}

function abrirModalConfirmacao(tipo: string, item: any) {
  tipoParaExcluir.value = tipo
  itemParaExcluir.value = item
  modalConfirmacaoAberto.value = true
}

function fecharModalConfirmacao() {
  modalConfirmacaoAberto.value = false
  tipoParaExcluir.value = ''
  itemParaExcluir.value = null
}

function confirmarExclusao(tipo: string, item: any) {
  abrirModalConfirmacao(tipo, item)
}

async function excluirItem() {
  if (!tipoParaExcluir.value || !itemParaExcluir.value) return

  try {
    store.startLoading()

    if (tipoParaExcluir.value === 'empresa') {
      await excluirEmpresaService(itemParaExcluir.value.id)
      empresas.content = empresas.content.filter((e) => e.id !== itemParaExcluir.value.id)
    } else if (tipoParaExcluir.value === 'usuario') {
      await deletarUsuario(itemParaExcluir.value.id)
      usuarios.content = usuarios.content.filter((u) => u.id !== itemParaExcluir.value.id)
    } else if (tipoParaExcluir.value === 'licenca') {
      await excluirLicenca(itemParaExcluir.value.id) // Chamada para API
      licencas.content = licencas.content.filter((l) => l.id !== itemParaExcluir.value.id)
    }

    fecharModalConfirmacao()
    inicializarDados()
  } catch (error: any) {
    console.error('Erro ao excluir:', error)

    const mensagemErro =
      typeof error?.response?.data === 'string'
        ? error.response.data
        : error?.response?.data?.mensagem ||
          error?.response?.data?.message ||
          error?.message ||
          'Erro ao excluir item. Tente novamente.'

    toast.error(mensagemErro)
  } finally {
    store.stopLoading()
  }
}

const debounceEmpresa = debounce(() => carregarEmpresas(0), DEBOUNCE_MS)
const debounceUsuario = debounce(() => carregarUsuariosCompletos(0), DEBOUNCE_MS)
const debounceLicenca = debounce(() => carregarLicencas(0), DEBOUNCE_MS)

function getEmpresaNome(id: number): string {
  return mapaEmpresasPorId[id] || '—'
}

function getUsuarioNome(id: number): string {
  return mapaUsuariosPorId[id] || '—'
}

function formatarCnpj(cnpj: string | null | undefined): string {
  if (!cnpj) return '—'
  // Remove tudo que não for número
  const numeros = cnpj.replace(/\D/g, '')
  if (numeros.length !== 14) return cnpj // retorna original se não for 14 dígitos
  return numeros.replace(/^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})$/, '$1.$2.$3/$4-$5')
}

function getEmpresaNomePorUsuarioId(usuarioId: number): string {
  const usuario = usuarios.content.find((u) => u.id === usuarioId)
  if (!usuario) return '—'

  const empresa = empresas.content.find((e) => e.id === usuario.empresaId)
  if (!empresa) return '—'

  return empresa.cnpj ? `${empresa.name} (${empresa.cnpj})` : empresa.name
}

function formatarTipo(tipo: string): string {
  const mapTipos: Record<string, string> = {
    BASIC: 'Básica',
    PREMIUM: 'Premium',
    ENTERPRISE: 'Enterprise',
  }
  return mapTipos[tipo] || tipo || '—'
}

function formatarData(dataStr: string): string {
  if (!dataStr) return '—'
  // pega só a parte da data (yyyy-MM-dd)
  const [ano, mes, dia] = dataStr.substring(0, 10).split('-')
  return `${dia}/${mes}/${ano}`
}

function inicializarDados() {
  carregarEmpresas(0)
  carregarUsuariosCompletos(0)
  carregarLicencas(0)
}

onMounted(() => {
  inicializarDados()
})
</script>
<style scoped>
.licenca-container {
  flex-grow: 1;
  padding: 2rem;
  min-height: 100vh;
  /* Se menu fixo, add margin-left igual à largura do menu */
  /* margin-left: 250px; */
  box-sizing: border-box;
  overflow: auto;
}
.app-container {
  display: flex;
  min-height: 100vh; /* garante que ocupe a altura total da tela */
}

/* Título */
h2.text-xl.font-bold.mb-6 {
  text-align: center;
  margin-bottom: 3rem;
  color: var(--color-text);
  font-weight: 700;
  font-size: 2rem;
}

/* Cards */
.card {
  background-color: var(--color-surface-card);
  padding: 2rem 2.5rem;
  border: 1px solid var(--color-primary-border);
  border-radius: 12px;
  box-shadow: var(--shadow-amber-sm);
  margin-bottom: 3.5rem;
  color: var(--color-text);
  transition: box-shadow 0.3s ease;
}

.card:hover {
  box-shadow: var(--shadow-amber);
}

/* Botões padrão */
button.btn {
  background: var(--color-primary);
  color: var(--color-primary-text-on);
  padding: 0.55rem 1.4rem;
  border: none;
  border-radius: 0.45rem;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  transition: background-color 0.3s ease, box-shadow 0.3s ease;
  box-shadow: var(--shadow-amber-sm);
  margin-right: 0.6rem;
}

button.btn:hover:not(:disabled) {
  background-color: var(--color-primary-hover);
  box-shadow: 0 4px 14px rgba(184, 149, 47, 0.6);
}

/* Botões pequenos */
button.btn.btn-sm {
  padding: 0.35rem 0.9rem;
  font-size: 0.85rem;
  border-radius: 0.4rem;
}

/* Botões de exclusão */
button.btn.btn-danger {
  background: var(--color-danger);
  color: var(--color-text-light);
  padding: 0.35rem 0.85rem;
  border-radius: 0.4rem;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  transition: background-color 0.3s ease, box-shadow 0.3s ease;
  margin-right: 0;
}

button.btn.btn-danger:hover:not(:disabled) {
  background-color: var(--color-danger-hover);
}

/* Container do submenu */
.submenu-container {
  position: relative;
}

.submenu {
  position: absolute;
  left: 100%;
  top: 0;
  background-color: var(--color-surface-card);
  z-index: 9999;
  border: 1px solid var(--color-primary-border);
  padding: 0.5rem 0.75rem;
  border-radius: 0.5rem;
  min-width: 180px;
  box-shadow: var(--shadow-amber-sm);
}

/* Botão amarelo */
button.btn.btn-yellow {
  background-color: var(--color-primary);
  color: var(--color-primary-text-on);
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  font-weight: 600;
  box-shadow: var(--shadow-amber-sm);
  transition: background-color 0.3s ease, box-shadow 0.3s ease;
  margin-right: 0.6rem;
}

button.btn.btn-yellow:hover {
  background-color: var(--color-primary-hover);
  box-shadow: 0 4px 14px rgba(184, 149, 47, 0.6);
}

.card > h3.text-lg.font-semibold.mb-2 {
  font-size: 1.4rem;
  font-weight: 700;
  color: var(--color-text);
  text-align: center;
  margin: 0 auto 1.5rem auto;
  max-width: fit-content;
  display: block;
}

button.btn.btn-yellow.btn-sm {
  padding: 0.25rem 0.7rem;
  font-size: 0.85rem;
}

/* Wrappers para tabelas com scroll horizontal */
.table-wrapper-empresa,
.table-wrapper-usuario,
.table-wrapper-licencas {
  width: 100%;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
  margin-bottom: 1.5rem;
}

/* Estilos comuns para todas as tabelas */
table.table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0 8px;
  font-size: 0.95rem;
  color: var(--color-text);
  background-color: transparent;
  table-layout: auto;
}

/* Cabeçalho */
table.table thead {
  display: table-header-group;
}

table.table th {
  background-color: var(--color-surface-alt);
  font-weight: 700;
  color: var(--color-primary-text-on);
  white-space: nowrap;
  padding: 0.8rem 1rem;
  border-radius: 8px;
  border: 1px solid var(--color-primary-border);
}

table.table tr:hover td {
  background-color: var(--color-primary-light);
}

table.table th:first-child,
table.table td:first-child {
  border-top-left-radius: 8px;
  border-bottom-left-radius: 8px;
}

table.table th:last-child,
table.table td:last-child {
  border-top-right-radius: 8px;
  border-bottom-right-radius: 8px;
}

table.table th,
table.table td {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding: 0.8rem 1rem;
  background-color: var(--color-surface-card);
  border: 1px solid var(--color-primary-border);
  border-radius: 8px;
}

/* Remove o padding-left extra da célula "Ações" para o mobile */
@media (max-width: 768px) {
  table.table td[data-label='Ações'] {
    padding-left: 1rem; /* menor padding para caber botões */
    position: relative;
  }

  /* Label “Ações” acima dos botões, como um título */
  table.table td[data-label='Ações']::before {
    content: attr(data-label);
    position: absolute;
    top: 0.5rem;
    left: 1rem;
    font-weight: 700;
    color: var(--color-primary-text-on);
    white-space: nowrap;
    width: auto;
    transform: none;
  }

  /* Espaçamento para o grupo de botões (empilhados verticalmente) */
  table.table td[data-label='Ações'] > button {
    display: block; /* empilhar */
    width: 100%; /* largura total */
    margin: 2rem 0 0 0; /* distância de 2rem do label */
  }

  /* Remove margem superior do primeiro botão para não empurrar muito */
  table.table td[data-label='Ações'] > button:first-child {
    margin-top: 1.5rem; /* menor que os outros */
  }

  /* Remove margem inferior do último botão */
  table.table td[data-label='Ações'] > button:last-child {
    margin-bottom: 0;
  }
}

/* Responsividade: tabela vira cards no mobile */
@media (max-width: 768px) {
  table.table,
  thead,
  tbody,
  th,
  td,
  tr {
    display: block;
  }

  /* Esconde cabeçalho */
  table.table thead tr {
    position: absolute !important;
    top: -9999px;
    left: -9999px;
    height: 1px;
    overflow: hidden;
  }

  /* Cada linha vira card */
  table.table tr {
    margin-bottom: 1.5rem;
    border: 1px solid var(--color-primary-border);
    border-radius: 12px;
    padding: 1rem 1.25rem;
    background: var(--color-surface-card);
    box-shadow: var(--shadow-amber-sm);
  }

  /* Células vira linhas com label */
  table.table td {
    position: relative;
    padding-left: 45%;
    padding-top: 0.7rem;
    padding-bottom: 0.7rem;
    border: none;
    border-bottom: 1px solid var(--color-primary-border);
    white-space: normal;
    text-align: left;
  }

  table.table td:last-child {
    border-bottom: 0;
  }

  /* Label antes do conteúdo */
  table.table td::before {
    content: attr(data-label);
    position: absolute;
    left: 1rem;
    top: 50%;
    transform: translateY(-50%);
    font-weight: 700;
    color: var(--color-primary-text-on);
    white-space: nowrap;
    width: 40%;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  /* Ajuste para a coluna ações com botões */
  table.table td[data-label='Ações'] {
    padding-left: 1rem;
  }

  /* Botões na coluna ações ficam em bloco, espaçados e largura total */
  table.table td[data-label='Ações'] > button {
    margin-bottom: 0.5rem;
    width: 100%;
    justify-content: center;
  }

  /* Remove margem direita dos botões para não quebrar layout */
  table.table td[data-label='Ações'] > button:last-child {
    margin-bottom: 0;
  }
}

/* Ajuste para input de busca */
.input-container {
  position: relative;
  max-width: 450px;
  width: 100%;
  margin-bottom: 1.5rem;
}

.input-container i.fa-search {
  position: absolute;
  top: 50%;
  left: 14px;
  transform: translateY(-50%);
  color: var(--color-text-muted);
  font-size: 1.2rem;
  pointer-events: none;
}

.input-pesquisa {
  width: 100%;
  padding: 11px 14px 11px 42px;
  border: 1.5px solid var(--color-primary-border);
  border-radius: 10px;
  font-size: 1.05rem;
  color: var(--color-text);
  box-shadow: none;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

.input-pesquisa:focus {
  border-color: var(--color-primary);
  box-shadow: 0 0 6px var(--color-primary)aa;
  outline: none;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 0.5rem;
  margin-top: 1.5rem;
  margin-bottom: 3rem;
  font-size: 0.95rem;
  font-weight: 600;
  color: var(--color-text);
  user-select: none;
}

.pagination button.btn-sm {
  padding: 0.4rem 1rem;
  font-size: 0.9rem;
  border-radius: 0.4rem;
  background-color: var(--color-primary);
  color: var(--color-primary-text-on);
  border: none;
  cursor: pointer;
  box-shadow: var(--shadow-amber-sm);
  transition: background-color 0.25s ease, box-shadow 0.25s ease, transform 0.15s ease;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 40px;
  user-select: none;
}

.pagination button.btn-sm:disabled {
  background-color: var(--color-disabled-bg);
  color: var(--color-disabled-text);
  cursor: not-allowed;
  box-shadow: none;
  transform: none;
}

.pagination button.btn-sm:not(:disabled):hover {
  background-color: var(--color-primary-hover);
  box-shadow: var(--shadow-amber);
  transform: scale(1.05);
}

.pagination span {
  padding: 0 0.8rem;
  color: var(--color-text);
  display: flex;
  align-items: center;
  min-width: 50px;
  justify-content: center;
  user-select: none;
}

/* Responsividade para mobile: botões maiores e mais espaçamento */
@media (max-width: 480px) {
  .pagination {
    gap: 0.8rem;
    font-size: 0.9rem;
  }

  .pagination button.btn-sm {
    padding: 0.5rem 1.2rem;
    min-width: 45px;
    font-size: 1rem;
  }

  .pagination span {
    min-width: 55px;
    padding: 0 1rem;
  }
}
</style>
