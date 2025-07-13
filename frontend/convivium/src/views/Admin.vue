<template lang="pug">
.admin-condominio
  h2.titulo Administração do Condomínio

  .nav-listas
    button.btn-nav(
      :class="{ ativo: listaSelecionada === 'moradores' }"
      @click="listaSelecionada = 'moradores'"
    ) Lista de Moradores
    button.btn-nav(
      :class="{ ativo: listaSelecionada === 'reclamacoes' }"
      @click="listaSelecionada = 'reclamacoes'"
    ) Lista de Reclamações

  // Mostrar conteúdo conforme a lista selecionada
  div(v-if="listaSelecionada === 'moradores'")
    .link-condominio
      label Link para cadastro dos moradores:
      p
        a(:href="linkCadastroMoradores" target="_blank") {{ linkCadastroMoradores }}

    .filtro-acoes
      div.input-group
        input(
          type="text"
          placeholder="Buscar por nome, unidade ou email"
          v-model="filtroMorador"
          @input="filtrarMoradores"
          autocomplete="off"
        )
        span.icon 🔍
      button.btn-outline(@click="abrirModalNovoMoradorCompleto") + Novo Morador
    table.lista-moradores
      thead
        tr
          th Nome
          th Tipo
          th Unidade
          th Email
          th Contato
          th Ações
      tbody
        tr(v-for="morador in moradoresPagina" :key="morador.id")
          td {{ morador.nome }}
          td {{ morador.tipo }}
          td {{ morador.unidade }}
          td {{ morador.email }}
          td {{ morador.telefone }}
          td
            .dropdown
              button.btn-outline.btn-acao(@click="toggleDropdown(morador.id)")
                | Ações ▾
              ul.dropdown-menu(v-if="dropdownAberto === morador.id")
                li(@click="abrirModalDetalhes(morador); fecharDropdown()") Detalhes
                li(@click="confirmarRemoverMoradorWrapper(morador); fecharDropdown()") Remover

    .paginacao(v-if="totalPaginas > 1")
      button.btn-outline.pag-btn(
        :disabled="paginaAtual === 1"
        @click="paginaAtual--"
      ) ‹
      button.btn-outline.pag-btn(
        v-for="num in totalPaginas"
        :key="num"
        :class="{ ativo: num === paginaAtual }"
        @click="paginaAtual = num"
      ) {{ num }}
      button.btn-outline.pag-btn(
        :disabled="paginaAtual === totalPaginas"
        @click="paginaAtual++"
      ) ›

  // Placeholder da lista de reclamações
  // div(v-else)
  div(v-if="listaSelecionada === 'reclamacoes'")
    .filtro-acoes
      div.input-group
        input(
          type="text"
          placeholder="Buscar por nome do usuário"
          v-model="filtroReclamacao.nomeUsuario"
          @input="filtrarReclamacoes"
          autocomplete="off"
        )
      div.input-group
        input(
          type="text"
          placeholder="Buscar por apartamento"
          v-model="filtroReclamacao.apartamento"
          @input="filtrarReclamacoes"
          autocomplete="off"
        )
      div.input-group
        input(
          type="text"
          placeholder="Buscar por bloco"
          v-model="filtroReclamacao.bloco"
          @input="filtrarReclamacoes"
          autocomplete="off"
        )
      div.input-group
        label(for="dataInicio") Data início:
        input(
          type="date"
          id="dataInicio"
          v-model="filtroReclamacao.dataInicio"
          @change="filtrarReclamacoes"
        )
      div.input-group
        label(for="dataFim") Data fim:
        input(
          type="date"
          id="dataFim"
          v-model="filtroReclamacao.dataFim"
          @change="filtrarReclamacoes"
        )
      div.input-group
        input(
          type="text"
          placeholder="Buscar por tipo"
          v-model="filtroReclamacao.tipo"
          @input="filtrarReclamacoes"
          autocomplete="off"
        )
      div.input-group
        input(
          type="text"
          placeholder="Buscar por descrição"
          v-model="filtroReclamacao.descricao"
          @input="filtrarReclamacoes"
          autocomplete="off"
        )
      div.input-group
        select(
          id="status"
          v-model="filtroReclamacao.status"
          @change="filtrarReclamacoes"
        )
          option(value="" selected) -- Selecione o Status --
          option(value="EM_ANDAMENTO") Em andamento
          option(value="EM_ANALISE") Em análise
          option(value="EM_NOTIFICACAO") Em notificação
          option(value="SOLUCIONADA") Solucionada
          option(value="CANCELADA") Cancelada
      button.btn-outline.btn-reset(@click="limparFiltros") Limpar Filtros

    table.lista-moradores
      thead
        tr
          th Tipo
          th Descrição
          th Status
          th Data
          th Reclamante
          th Empresa
          th Anexos
          th Acoes
      tbody
        tr(v-for="reclamacao in reclamacoesPagina" :key="reclamacao.id")
          td {{ reclamacao.tipo }}
          td {{ reclamacao.detalhes }}
          td {{ formatarStatus(reclamacao.status) }}
          td {{ formatarData(reclamacao.dataCriacao) }}
          td
            | {{ reclamacao.usuario.username }} -
            | Bloco: {{ reclamacao.usuario.bloco }}, Apt: {{ reclamacao.usuario.apartamento }} -
            | {{ reclamacao.usuario.tipoPerfil }}
          td {{ reclamacao.empresa.name }}
          td
            span(v-for="anexo in reclamacao.anexos" :key="anexo.id")
              | {{ anexo.nomeArquivo }}
              br
          td
            .dropdown
              button.btn-outline.btn-acao(@click="toggleDropdown(reclamacao.id)") Ações ▾
              ul.dropdown-menu(v-if="dropdownAberto === reclamacao.id")
                li(@click="abrirModalDetalhesReclamacao(reclamacao); fecharDropdown()") Detalhes
                li(@click="abrirModalAcaoReclamacao(reclamacao); fecharDropdown()") Adicionar Ação
                li(@click="abrirModalSolucaoReclamacao(reclamacao); fecharDropdown()") Solucionar

    .paginacao(v-if="totalPaginasReclamacoes > 1")
      button.btn-outline.pag-btn(
        :disabled="paginaAtualReclamacoes === 1"
        @click="paginaAtualReclamacoes--"
      ) ‹
      button.btn-outline.pag-btn(
        v-for="num in totalPaginasReclamacoes"
        :key="num"
        :class="{ ativo: num === paginaAtualReclamacoes }"
        @click="paginaAtualReclamacoes = num"
      ) {{ num }}
      button.btn-outline.pag-btn(
        :disabled="paginaAtualReclamacoes === totalPaginasReclamacoes"
        @click="paginaAtualReclamacoes++"
      ) ›

// Modal Confirmação Remoção
ModalConfirmarRemover(
  v-if="modalConfirmarRemoverAberto"
  :morador="moradorParaRemover"
  @close="modalConfirmarRemoverAberto = false"
  @confirmar="removerMoradorConfirmado"
)

// Modal Cadastro Completo Morador
ModalNovoMorador(
  v-if="modalNovoMoradorCompletoAberto"
  v-model="formMoradorCompleto"
  :tipos-usuario="tiposUsuario"
  :roles-usuario="rolesUsuario"
  @close="modalNovoMoradorCompletoAberto = false"
  @salvar="onSalvar"
)


// Modal Detalhes do Morador
ModalDetalhesMorador(
  v-if="modalDetalhesAberto"
  :tipos-usuario="tiposUsuario"
  :roles-usuario="rolesUsuario"
  :morador="moradorSelecionado"
  @close="modalDetalhesAberto = false"
)

ModalDetalhesReclamacao(
  v-if="modalDetalhesReclamacaoAberto"
  :reclamacao="reclamacaoSelecionada"
  @close="modalDetalhesReclamacaoAberto = false"
)

ModalAcaoReclamacao(
  v-if="modalAcaoReclamacaoAberto"
  :reclamacao="reclamacaoSelecionada"
  @close="fecharModalAcao"
)

ModalSolucaoReclamacao(
  v-if="modalSolucaoReclamacaoAberto"
  :reclamacao="reclamacaoSelecionada"
  @close="fecharModalSolucao"
)

</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, reactive } from 'vue'
import axios from 'axios'
import {
  fetchUsersByEmpresaRaw,
  fetchTiposUsuario,
  fetchRolesUsuario,
  buscarUsuarioPorId,
} from '@/services/userService'
import { salvarMoradorCompleto, deletarUsuario } from '@/services/authService'
import { toast } from 'vue3-toastify'
import ModalNovoMorador from '@/views/components/ModalNovoMorador.vue'
import ModalConfirmarRemover from '@/views/components/ModalConfirmarRemover.vue'
import ModalDetalhesMorador from '@/views/components/ModalDetalhesMorador.vue'
import ModalDetalhesReclamacao from '@/views/components/ModalDetalhesReclamacao.vue'
import ModalAcaoReclamacao from '@/views/components/ModalAcaoReclamacao.vue'
import ModalSolucaoReclamacao from '@/views/components/ModalSolucaoReclamacao.vue'

import { listarReclamacoes } from '@/services/denunciaService'
import { useLoadingStore } from '@/stores/loadingStore'
const store = useLoadingStore()
const modalDetalhesReclamacaoAberto = ref(false)
const modalAcaoReclamacaoAberto = ref(false)
const modalSolucaoReclamacaoAberto = ref(false)
const reclamacaoSelecionada = ref<ReclamacaoResumo | null>(null)

function abrirModalDetalhesReclamacao(reclamacao: ReclamacaoResumo) {
  reclamacaoSelecionada.value = reclamacao
  modalDetalhesReclamacaoAberto.value = true
}

function abrirModalAcaoReclamacao(reclamacao: ReclamacaoResumo) {
  reclamacaoSelecionada.value = reclamacao
  modalAcaoReclamacaoAberto.value = true
}

function abrirModalSolucaoReclamacao(reclamacao: ReclamacaoResumo) {
  reclamacaoSelecionada.value = reclamacao
  modalSolucaoReclamacaoAberto.value = true
}

const moradores = ref<any[]>([]) // lista de moradores carregada do backend
const paginaAtual = ref(1)
const totalPaginas = ref(1)
const totalElementos = ref(0)
const itensPorPagina = 10
const linkCadastroMoradores = ref('')
const filtroMorador = ref('')
const moradoresFiltrados = ref<any[]>([]) // usado para filtro local
const listaSelecionada = ref('moradores')
const tiposUsuario = ref<{ id: number; name: string }[]>([])
const rolesUsuario = ref<{ id: number; name: string }[]>([])
const modalConfirmarRemoverAberto = ref(false)
const moradorParaRemover = ref<any>(null)
const modalNovoMoradorCompletoAberto = ref(false)
const modalDetalhesAberto = ref(false)
const moradorSelecionado = ref<any>(null)
const dropdownAberto = ref<number | null>(null)
const reclamacoes = ref<ReclamacaoResumo[]>([])
const paginaAtualReclamacoes = ref(1)
const totalPaginasReclamacoes = ref(1)
interface FiltroReclamacao {
  nomeUsuario: string
  apartamento: string
  bloco: string
  tipo: string
  descricao: string
  status?: 'EM_ANDAMENTO' | 'EM_ANALISE' | 'EM_NOTIFICACAO' | 'SOLUCIONADA' | 'CANCELADA' | ''
  dataInicio?: string
  dataFim?: string
}

function limparFiltros(): void {
  filtroReclamacao.value = {
    nomeUsuario: '',
    apartamento: '',
    bloco: '',
    tipo: '',
    descricao: '',
    status: '',
    dataInicio: '',
    dataFim: '',
  }
  filtrarReclamacoes()
}

const filtroReclamacao = ref<FiltroReclamacao>({
  nomeUsuario: '',
  apartamento: '',
  bloco: '',
  tipo: '',
  descricao: '',
  status: '', // começa vazio
  dataInicio: '',
  dataFim: '',
})

function fecharModalAcao() {
  modalAcaoReclamacaoAberto.value = false
  carregarReclamacoes()
}

function fecharModalSolucao() {
  modalSolucaoReclamacaoAberto.value = false
  carregarReclamacoes()
}

function limparFiltroLocal(filtro: Record<string, any>) {
  const filtroLimpo: Record<string, any> = {}
  for (const [key, value] of Object.entries(filtro)) {
    // Considera string vazia, null, undefined como "limpar"
    if (value !== '' && value !== null && value !== undefined) {
      filtroLimpo[key] = value
    }
  }
  return filtroLimpo
}

const carregarReclamacoes = async () => {
  try {
    // Ajusta datas para undefined se vazias
    const filtroAjustado = { ...filtroReclamacao.value }
    if (filtroAjustado.dataInicio === '') delete filtroAjustado.dataInicio
    if (filtroAjustado.dataFim === '') delete filtroAjustado.dataFim

    const filtroLimpo = limparFiltroLocal(filtroAjustado)

    const data = await listarReclamacoes(
      paginaAtualReclamacoes.value - 1,
      itensPorPagina,
      filtroLimpo,
    )
    reclamacoes.value = data.content
    totalPaginasReclamacoes.value = data.totalPages
  } catch (e) {
    console.error('Erro ao carregar reclamações:', e)
  }
}

function formatarStatus(status: string): string {
  const map: Record<string, string> = {
    EM_ANDAMENTO: 'Em andamento',
    EM_ANALISE: 'Em análise',
    EM_NOTIFICACAO: 'Em notificação',
    SOLUCIONADA: 'Solucionada',
    CANCELADA: 'Cancelada',
  }
  return map[status] || status
}

const reclamacoesPagina = computed(() => reclamacoes.value)

const filtrarReclamacoes = () => {
  paginaAtualReclamacoes.value = 1 // Resetar para a primeira página ao filtrar
  carregarReclamacoes() // Função que chama a API com filtro e página
}

const formatarData = (dataISO: string) => {
  const d = new Date(dataISO)
  return d.toLocaleDateString() + ' ' + d.toLocaleTimeString()
}

watch(paginaAtualReclamacoes, () => {
  carregarReclamacoes()
})

watch(listaSelecionada, (novoValor) => {
  if (novoValor === 'reclamacoes') {
    carregarReclamacoes()
  }
})
function toggleDropdown(id: number) {
  dropdownAberto.value = dropdownAberto.value === id ? null : id
}
function fecharDropdown() {
  dropdownAberto.value = null
}

const fetchMoradores = async (page = 0, size = itensPorPagina, filtro = '') => {
  try {
    const empresa = JSON.parse(localStorage.getItem('userEmpresa') || '{}')
    const idEmpresa = empresa.id
    if (!idEmpresa) {
      throw new Error('ID da empresa não encontrado no localStorage.')
    }

    // Chama a função que faz a requisição, passando idEmpresa, page, size
    const data = await fetchUsersByEmpresaRaw(idEmpresa, page, size)

    // Mapeia o conteúdo da página para o formato esperado pela UI
    moradores.value = data.content.map((m: any) => ({
      id: m.id,
      nome: m.username + (m.sobrenome ? ' ' + m.sobrenome : ''),
      unidade: (m.bloco ? 'Bloco ' + m.bloco + ' - ' : '') + (m.apartamento ?? ''),
      email: m.email,
      telefone: formatarTelefoneUI(m.telefone),
      tipo: m.tipo?.name ?? '',
      empresa: m.empresa?.name ?? '',
      role: m.role?.name ?? '',
      bloco: m.bloco,
      apartamento: m.apartamento,
      // demais campos que quiser usar
    }))

    paginaAtual.value = data.pageable.pageNumber + 1
    totalPaginas.value = data.totalPages
    totalElementos.value = data.totalElements

    moradoresFiltrados.value = moradores.value // reset filtro local
  } catch (error) {
    console.error('Erro ao buscar moradores:', error)
  }
}

const carregarTiposERoles = async () => {
  try {
    tiposUsuario.value = await fetchTiposUsuario()
    rolesUsuario.value = await fetchRolesUsuario()
  } catch (error) {
    console.error('Erro ao buscar tipos e roles:', error)
  }
}
const formatarTelefoneUI = (telefone: string) => {
  if (!telefone) return ''
  let val = telefone.replace(/\D/g, '')
  if (val.length > 10) {
    val = val.replace(/^(\d{2})(\d{5})(\d{4}).*/, '($1) $2-$3')
  } else if (val.length > 5) {
    val = val.replace(/^(\d{2})(\d{4})(\d{0,4}).*/, '($1) $2-$3')
  }
  return val
}

interface MoradorPayload {
  username: string
  email: string
  cpf: string
  tipoUsuario: number
  tipo: number
  sobrenome?: string
  telefone?: string
  cep?: string
  logradouro?: string
  cidade?: string
  estado?: string
  bairro?: string
  numero?: string
  complemento?: string
  genero?: string
  alerta?: boolean
  bloco?: string
  apartamento?: string
  vagaCarro?: string
  vagaMoto?: string
  role: number
  empresa: number
  password: string
  ativo?: boolean
}

function getId(valor: number | { id: number } | null | undefined): number {
  if (valor == null) throw new Error('Campo obrigatório')
  if (typeof valor === 'number') return valor
  return valor.id
}

const onSalvar = async (dadosMorador: any) => {
  const empresa = JSON.parse(localStorage.getItem('userEmpresa') || '{}')
  const idEmpresa = empresa.id

  try {
    const dados: MoradorPayload = {
      username: dadosMorador.username,
      email: dadosMorador.email,
      cpf: dadosMorador.cpf,
      tipoUsuario: getId(dadosMorador.tipo),
      tipo: getId(dadosMorador.tipo),
      sobrenome: dadosMorador.sobrenome ?? '',
      telefone: dadosMorador.telefone ?? '',
      cep: dadosMorador.cep ?? '',
      logradouro: dadosMorador.logradouro ?? '',
      cidade: dadosMorador.cidade ?? '',
      estado: dadosMorador.estado ?? '',
      bairro: dadosMorador.bairro ?? '',
      numero: dadosMorador.numero ?? '',
      complemento: dadosMorador.complemento ?? '',
      genero: dadosMorador.genero ?? '',
      alerta: dadosMorador.alerta ?? false,
      bloco: dadosMorador.bloco ?? '',
      apartamento: dadosMorador.apartamento ?? '',
      vagaCarro: dadosMorador.vagaCarro ?? '',
      vagaMoto: dadosMorador.vagaMoto ?? '',
      role: dadosMorador.role ? getId(dadosMorador.role) : 0,
      empresa: idEmpresa,
      password: dadosMorador.password ?? '',
      ativo: false, // caso queira já enviar ativo false aqui
    }
    store.startLoading()

    await salvarMoradorCompleto(dados)

    toast.success('Morador salvo com sucesso!')
    modalNovoMoradorCompletoAberto.value = false
    carregarMoradores()
    store.stopLoading()
  } catch (error: any) {
    const resposta = error?.response?.data
    const mensagem = resposta?.message

    if (mensagem === 'Usuário reativado com sucesso.') {
      toast.success(mensagem)
      modalNovoMoradorCompletoAberto.value = false
      carregarMoradores()
      store.stopLoading()
    } else if (mensagem && resposta?.error) {
      toast.error(mensagem)
      store.stopLoading()
    } else {
      toast.error('Erro inesperado ao salvar morador.')
      store.stopLoading()
    }

    console.error('Erro ao salvar morador', error)
    store.stopLoading()
  }
}

// Função para buscar moradores ao mudar a página
const mudarPagina = (pagina: number) => {
  if (pagina < 1 || pagina > totalPaginas.value) return
  paginaAtual.value = pagina
  carregarMoradores()
}

// Buscar moradores considerando filtro e pagina atual
const carregarMoradores = () => {
  // Sua API espera página zero-based
  fetchMoradores(paginaAtual.value - 1, itensPorPagina, filtroMorador.value)
}

// Filtrar moradores localmente (opcional, ou pode refazer a busca com filtro no backend)
const filtrarMoradores = () => {
  const termo = filtroMorador.value.trim().toLowerCase()

  if (!termo) {
    moradoresFiltrados.value = moradores.value
  } else {
    moradoresFiltrados.value = moradores.value.filter((morador) =>
      Object.values(morador).some((valor) =>
        String(valor ?? '')
          .toLowerCase()
          .includes(termo),
      ),
    )
  }

  // Recalcula a paginação local
  totalPaginas.value = Math.ceil(moradoresFiltrados.value.length / itensPorPagina)
  paginaAtual.value = 1
}

const moradoresPagina = computed(() => {
  const lista =
    moradoresFiltrados.value.length > 0 || filtroMorador.value
      ? moradoresFiltrados.value
      : moradores.value

  const start = (paginaAtual.value - 1) * itensPorPagina
  const end = start + itensPorPagina
  return lista.slice(start, end)
})

watch(paginaAtual, () => {
  carregarMoradores()
})
watch(filtroMorador, () => {
  filtrarMoradores()
})

onMounted(() => {
  carregarMoradores()
  carregarTiposERoles()
  const empresa = JSON.parse(localStorage.getItem('userEmpresa') || '{}')
  const codigoPublico = empresa.codigoPublico || 'defaultCodigo'
  const urlBase = window.location.origin
  const idCondominio = empresa.id

  linkCadastroMoradores.value = `${urlBase}/empresa/${codigoPublico}/cadastro-condominio/${idCondominio}`
})

const confirmarRemoverMoradorWrapper = (morador: any) => {
  moradorParaRemover.value = morador
  modalConfirmarRemoverAberto.value = true
}
const removerMoradorConfirmado = async () => {
  if (!moradorParaRemover.value) return

  try {
    await deletarUsuario(moradorParaRemover.value.id)
    toast.success('Usuário removido com sucesso!')

    // Remove localmente após sucesso
    moradores.value = moradores.value.filter((m) => m.id !== moradorParaRemover.value.id)

    // Atualiza listagem completa, se for o caso
    await carregarMoradores()
  } catch (error) {
    toast.error('Erro ao remover usuário.')
  } finally {
    modalConfirmarRemoverAberto.value = false
    moradorParaRemover.value = null
  }
}

const abrirModalDetalhes = async (moradorResumido: any) => {
  const response = await buscarUsuarioPorId(moradorResumido.id)
  moradorSelecionado.value = response
  modalDetalhesAberto.value = true
}

interface Empresa {
  id: number
  name: string
}
interface Usuario {
  id: number
  username: string
  email: string
  bloco: string
  apartamento: string
  tipoPerfil: string
}
interface Anexo {
  id: number
  nomeArquivo: string
}
interface ReclamacaoResumo {
  id: number
  tipo: string
  detalhes: string
  dataCriacao: string
  usuario: Usuario
  empresa: Empresa
  anexos: Anexo[]
}
interface MoradorForm {
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
  tipo: number | { id: number } | null
  empresa: Empresa
  role: number | { id: number } | null
}

const formMoradorCompleto = ref<MoradorForm>({
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
  empresa: { id: 0, name: '' }, // Corrigido aqui
  role: null,
})

function resetFormMoradorCompleto() {
  const empresa = JSON.parse(localStorage.getItem('userEmpresa') || '{}')

  formMoradorCompleto.value = {
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
    empresa: {
      id: empresa.id || 0,
      name: empresa.name || '',
    },
    role: null,
  }
}

const abrirModalNovoMoradorCompleto = () => {
  resetFormMoradorCompleto()
  modalNovoMoradorCompletoAberto.value = true
}
</script>

<style scoped>
/* Reset básico */
* {
  box-sizing: border-box;
  font-family: 'Inter', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  margin: 0;
  padding: 0;
}

/* Container principal */
.admin-condominio {
  max-width: 1500px;
  margin: 2rem auto;
  padding: 1rem 1.5rem 3rem;
  color: #222; /* texto preto escuro */
}

/* Título */
.titulo {
  font-weight: 700;
  font-size: 1.8rem;
  margin-bottom: 1rem;
  text-align: center;
  color: #222; /* preto */
}

/* Caixa de link condomínio */
.link-condominio {
  background: #f9fafb;
  border: 1px solid #d9c877; /* amarelo suave */
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 1.5rem;
  font-weight: 600;
  color: #555;
  word-break: break-word;
}

.link-condominio a {
  color: #b38600; /* amarelo mais escuro */
  text-decoration: none;
  transition: color 0.3s;
}

.link-condominio a:hover {
  color: #8c6500; /* amarelo escuro hover */
  text-decoration: underline;
}

/* Filtro e ações */
.filtro-acoes {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 1rem;
  align-items: center;
}

.filtro-acoes .input-group {
  flex: 1 1 250px;
  min-width: 200px;
  max-width: 100%;
  position: relative;
}

.input-group input,
.input-group select {
  width: 100%;
  padding: 0.5rem 2.2rem 0.5rem 0.9rem;
  border: 1.5px solid #d9c877; /* amarelo suave */
  border-radius: 8px;
  font-size: 0.9rem;
  color: #333;
  background-color: white;
  transition: border-color 0.3s;
  appearance: none;
}

.input-group input::placeholder {
  color: #999;
  font-weight: 500;
}

.input-group input:focus,
.input-group select:focus {
  outline: none;
  border-color: #b38600; /* amarelo médio */
  box-shadow: 0 0 8px rgba(179, 134, 0, 0.3);
}

.input-group .icon {
  position: absolute;
  right: 1rem;
  top: 50%;
  transform: translateY(-50%);
  color: #b38600;
  pointer-events: none;
  font-size: 1.1rem;
}

/* Botões outline amarelo */
.btn-outline {
  padding: 0.4rem 1rem;
  font-weight: 600;
  font-size: 0.95rem;
  color: #b38600;
  background: transparent;
  border: 2px solid #b38600;
  border-radius: 8px;
  cursor: pointer;
  user-select: none;
  transition: all 0.25s ease;
  min-width: 90px;
  text-align: center;
}

.btn-outline:hover,
.btn-outline:focus {
  background-color: #b38600;
  color: white;
  outline: none;
}

.btn-principal {
  border-color: #8c6500;
  color: #8c6500;
}

.btn-principal:hover,
.btn-principal:focus {
  background-color: #8c6500;
  color: white;
}

/* Barra de navegação das listas */
.nav-listas {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.8rem;
  justify-content: center;
}

.btn-nav {
  padding: 0.45rem 1.3rem;
  font-weight: 700;
  font-size: 1rem;
  border: 2px solid #b38600;
  background: transparent;
  color: #b38600;
  border-radius: 30px;
  cursor: pointer;
  transition: all 0.25s ease;
  user-select: none;
  min-width: 140px;
  text-align: center;
}

.btn-nav:hover,
.btn-nav:focus {
  background-color: #b38600;
  color: white;
  outline: none;
}

.btn-nav.ativo {
  background-color: #b38600;
  color: white;
  cursor: default;
  pointer-events: none;
  box-shadow: 0 0 8px rgba(179, 134, 0, 0.6);
}

/* Tabela com bordas suaves e linhas diferenciadas */
.lista-moradores {
  width: 100%;
  max-width: 100%;
  margin: 0 auto;
  border-collapse: separate;
  border-spacing: 0;
  border: 1px solid #d9c877;
  border-radius: 12px;
  overflow: hidden;
  font-size: 1rem;
  color: #333333;
  background-color: transparent;
  box-shadow: 0 2px 8px rgba(217, 165, 0, 0.1);
  table-layout: fixed; /* Ajuda a forçar colunas proporcionais */
}

.lista-moradores th,
.lista-moradores td {
  padding: 1rem 1.2rem;
  border-bottom: 1px solid #f0eec9;
  border-right: 1px solid #f0eec9;
  vertical-align: middle;
  word-break: break-word;
  white-space: normal;
}

.lista-moradores th:last-child,
.lista-moradores td:last-child {
  border-right: none;
}

.lista-moradores thead th {
  background-color: #f9f7e8;
  color: #222;
  font-weight: 700;
  text-align: left;
  user-select: none;
  letter-spacing: 0.03em;
}

.lista-moradores tbody tr:nth-child(even) {
  background-color: #fffef7;
}

.lista-moradores tbody tr:hover {
  background-color: #fff8d1;
  cursor: pointer;
}

/* Dropdown ações */
.dropdown-menu {
  position: absolute;
  background: white;
  border: 1px solid #d9c877;
  border-radius: 8px;
  margin-top: 6px;
  min-width: 140px;
  box-shadow: 0 4px 12px rgba(217, 165, 0, 0.15);
  z-index: 20;
  padding: 0.3rem 0;
  user-select: none;
  color: #333333;
  font-weight: 600;
}

.dropdown-menu li {
  padding: 0.5rem 1.2rem;
  list-style: none;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.dropdown-menu li:hover {
  background-color: #b38600;
  color: #fff;
}

/* Paginação */
.paginacao {
  margin-top: 1.8rem;
  display: flex;
  justify-content: center;
  gap: 8px;
}

.pag-btn {
  border-color: #d9c877;
  color: #b38600;
  border-radius: 8px;
  padding: 0.5rem 0.8rem;
  min-width: 32px;
  font-size: 1rem;
  font-weight: 600;
  transition: all 0.3s ease;
  cursor: pointer;
  user-select: none;
  background-color: transparent;
}

.pag-btn:hover:not(:disabled),
.pag-btn.ativo {
  background-color: #b38600;
  color: #fff;
  box-shadow: 0 0 10px rgba(179, 134, 0, 0.6);
}

.pag-btn:disabled {
  color: #d9c877;
  border-color: #d9c877;
  cursor: default;
}

/* Modal básico estilizado */
modal {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  user-select: none;
}

modal > div {
  background: white;
  border-radius: 10px;
  max-width: 400px;
  width: 95%;
  padding: 1.8rem 2.5rem;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  user-select: auto;
}

modal h3 {
  margin-top: 0;
  font-weight: 700;
  font-size: 1.5rem;
  margin-bottom: 1rem;
  color: #222;
}

modal label {
  display: block;
  margin-bottom: 0.3rem;
  font-weight: 600;
  color: #444;
}

modal input {
  width: 100%;
  padding: 0.45rem 0.8rem;
  margin-bottom: 1.1rem;
  border-radius: 8px;
  border: 1.5px solid #ccc;
  font-size: 1rem;
  color: #333;
  transition: border-color 0.3s;
}

modal input:focus {
  border-color: #b38600;
  outline: none;
  box-shadow: 0 0 6px rgba(179, 134, 0, 0.3);
}

/* Botões no modal alinhados */
.botoes {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 0.5rem;
}

.secao-form {
  margin-top: 2rem;
  font-size: 1.1rem;
  font-weight: 700;
  color: #b38600;
  border-bottom: 2px solid #b3860033;
  padding-bottom: 0.4rem;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem 2rem;
  margin-top: 1.2rem;
}

.form-grid > div {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.btn-reset {
  border-color: #b38600;
  color: #b38600;
}

.btn-reset:hover {
  background-color: #b38600;
  color: white;
}
</style>
