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
            @input="carregarEmpresas(0)"
          )
        button.btn.btn-yellow.ml-4(@click="abrirModalEmpresa()")
          i.fa.fa-plus.mr-2
          | Adicionar Empresa

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
            td {{ empresa.name }}
            td {{ empresa.cnpj }}
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
            @input="carregarUsuariosCompletos(0)"
          )
        button.btn.btn-yellow.ml-4(@click="abrirModalUsuario()")
          i.fa.fa-plus.mr-2
          | Adicionar Usuário

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
            td {{ usuario.username }}
            td {{ usuario.email }}
            td {{ usuario.empresa }}
            td
              button.btn.btn-yellow.btn-sm(@click="abrirModalUsuario(usuario)")
                i.fa.fa-pencil-alt.mr-1
                | Editar
              button.btn.btn-danger.btn-sm(@click="confirmarExclusao('usuario', usuario)")
                i.fa.fa-trash.mr-1
                | Excluir

      .pagination
        button.btn.btn-sm(@click="carregarUsuariosCompletos(0)" :disabled="usuarios?.first") « Primeira
        button.btn.btn-sm(@click="carregarUsuariosCompletos(usuarios.number - 1)" :disabled="usuarios?.first") ‹
        span Página {{ usuarios.number + 1 }} de {{ usuarios.totalPages }}
        button.btn.btn-sm(@click="carregarUsuariosCompletos(usuarios.number + 1)" :disabled="usuarios?.last") ›
        button.btn.btn-sm(@click="carregarUsuariosCompletos(usuarios.totalPages - 1)" :disabled="usuarios?.last") Última »

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
            @input="carregarLicencas(0)"
          )
        button.btn.btn-yellow.ml-4(@click="abrirModalLicenca()")
          i.fa.fa-plus.mr-2
          | Adicionar Licença

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
            td {{ licenca.empresaNome }} ({{ formatarCnpj(licenca.empresaCnpj) }})
            td {{ formatarTipo(licenca.tipo) }}
            td {{ formatarData(licenca.dataInicio) }}
            td {{ formatarData(licenca.dataFim) }}
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
      :usuarios="usuarios?.content || []"
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
  usuarioId: number
  tipo: string
  dataInicio: string
  dataFim: string
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
  padding-bottom: 150px; /* reserva espaço para o footer */
  box-sizing: border-box;
  min-height: 100%;
  display: flex;
  flex-direction: column;
}

h2.text-xl.font-bold.mb-6 {
  text-align: center;
  margin-bottom: 3rem;
  color: #374151; /* cinza escuro */
  font-weight: 700;
  font-size: 2rem;
}

.card {
  background-color: #ede9db; /* tom creme mais neutro e ameno */
  padding: 2rem 2.5rem;
  border: 1px solid #e6dfb8; /* borda clara, tom pastel */
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(214, 185, 78, 0.15); /* sombra leve amarelada */
  margin-bottom: 3.5rem;
  color: #374151; /* cinza escuro para o texto */
  transition: box-shadow 0.3s ease;
}

.card:hover {
  box-shadow: 0 8px 20px rgba(214, 185, 78, 0.3);
}

button.btn {
  background: #d6b94e; /* amarelo pastel escuro */
  color: #1f2937; /* cinza escuro */
  padding: 0.55rem 1.4rem;
  border: none;
  border-radius: 0.45rem;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  transition:
    background-color 0.3s ease,
    box-shadow 0.3s ease;
  box-shadow: 0 2px 6px rgba(214, 185, 78, 0.3);
}

button.btn:hover:not(:disabled) {
  background-color: #b8952f; /* amarelo pastel mais escuro no hover */
  box-shadow: 0 4px 14px rgba(184, 149, 47, 0.6);
}

button.btn.btn-sm {
  padding: 0.35rem 0.9rem;
  font-size: 0.85rem;
  border-radius: 0.4rem;
}

button.btn.btn-danger {
  background: #dc2626; /* vermelho forte */
  color: white;
  padding: 0.35rem 0.85rem;
  border-radius: 0.4rem;
  font-weight: 600;
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  transition:
    background-color 0.3s ease,
    box-shadow 0.3s ease;
  box-shadow: 0 2px 6px rgba(220, 38, 38, 0.3);
}

button.btn.btn-danger:hover:not(:disabled) {
  background-color: #b91c1c;
  box-shadow: 0 4px 14px rgba(185, 28, 28, 0.6);
}

.submenu-container {
  position: relative;
}

.submenu {
  position: absolute;
  left: 100%;
  top: 0;
  background-color: #f9f7e8;
  z-index: 9999;
  border: 1px solid #d9c877;
  padding: 0.5rem 0.75rem;
  border-radius: 0.5rem;
  min-width: 180px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

button.btn.btn-yellow {
  background-color: #d6b94e;
  color: #1f2937;
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  font-weight: 600;
  box-shadow: 0 2px 6px rgba(214, 185, 78, 0.3);
  transition:
    background-color 0.3s ease,
    box-shadow 0.3s ease;
  margin-right: 0.6rem; /* Espaço para separar do botão Excluir */
}

button.btn.btn-yellow:hover {
  background-color: #b8952f;
  box-shadow: 0 4px 14px rgba(184, 149, 47, 0.6);
}

.card > h3.text-lg.font-semibold.mb-2 {
  font-size: 1.4rem;
  font-weight: 700;
  color: #374151;
  text-align: center;
  margin: 0 auto 1.5rem auto;
  max-width: fit-content;
  display: block;
}

button.btn.btn-yellow.btn-sm {
  padding: 0.25rem 0.7rem;
  font-size: 0.85rem;
}

table.table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0 8px;
  font-size: 0.95rem;
  color: #374151; /* texto cinza escuro */
  background-color: transparent;
}

table.table th,
table.table td {
  padding: 0.8rem 1rem;
  text-align: left;
  vertical-align: middle;
  background-color: #f4f1e7; /* fundo creme mais claro que o card */
  border: 1px solid #e6dfb8; /* borda leve no mesmo tom da borda do card */
  border-radius: 8px;
  transition: background-color 0.3s ease;
}

table.table th {
  background-color: #e9e5d8; /* tom creme claro para header */
  font-weight: 700;
  color: #5a5500; /* amarelo escuro para combinar com o tom dourado */
}

table.table tr:hover td {
  background-color: #f0ecd9; /* fundo hover suave amarelo claro */
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

.pagination {
  display: flex;
  justify-content: center;
  gap: 0.4rem;
  margin-top: 1.5rem;
  margin-bottom: 3rem;
  font-size: 0.9rem;
  font-weight: 600;
  color: #374151;
}

.pagination button.btn-sm {
  padding: 0.3rem 0.7rem;
  font-size: 0.85rem;
  border-radius: 0.35rem;
  background-color: #d6b94e;
  color: #1f2937;
  border: none;
  cursor: pointer;
  box-shadow: 0 2px 6px rgba(214, 185, 78, 0.3);
  transition:
    background-color 0.25s ease,
    box-shadow 0.25s ease;
}

.pagination button.btn-sm:disabled {
  background-color: #f3f4f6;
  color: #9ca3af;
  cursor: not-allowed;
  box-shadow: none;
}

.pagination button.btn-sm:not(:disabled):hover {
  background-color: #b8952f;
  box-shadow: 0 4px 14px rgba(184, 149, 47, 0.6);
}

.pagination span {
  display: flex;
  align-items: center;
  padding: 0 0.6rem;
  user-select: none;
  color: #374151;
}

.input-container {
  position: relative;
  max-width: 450px;
  width: 100%;
  margin-bottom: 1.2rem;
}

.input-container i.fa-search {
  position: absolute;
  top: 50%;
  left: 14px;
  transform: translateY(-50%);
  color: #9ca3af;
  font-size: 1.1rem;
  pointer-events: none;
}

.input-pesquisa {
  width: 100%;
  padding: 11px 14px 11px 40px;
  border: 1.5px solid #d1d5db;
  border-radius: 10px;
  font-size: 1.05rem;
  color: #374151;
  box-shadow: none;
  transition:
    border-color 0.3s ease,
    box-shadow 0.3s ease;
}

.input-pesquisa:focus {
  border-color: #d6b94e;
  box-shadow: 0 0 6px #d6b94eaa;
  outline: none;
}

.app-container {
  min-height: 100vh; /* garante altura mínima, mas permite crescer */
  display: flex;
  flex-direction: row;
}

.menu-lateral {
  height: 100vh;
  background-color: #f9f7e8;
  border-right: 2px solid #d9c877;
  padding: 1.5rem 1rem;
  min-width: 180px;
  max-width: 240px;
  position: sticky;
  top: 0;
  z-index: 1000;

  /* Remova ou comente esta linha ↓ */
  /* overflow-y: auto; */
  overflow: visible; /* permite que o submenu "escape" da lateral */
}

.licenca-container,
.app-container {
  overflow: visible !important;
  position: relative;
  z-index: 1;
}
</style>
