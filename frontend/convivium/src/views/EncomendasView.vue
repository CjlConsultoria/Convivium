<template lang="pug">
.encomendas-view
  .layout-container
    MenuLateral(:itemSelecionado="'encomendas'")

    main.area-conteudo
      h2.titulo Encomendas

      //- Portaria/gestão: registrar e validar retirada
      .acoes-portaria(v-if="podeRegistrarOuValidar")
        .card-acao
          h3 Registrar nova encomenda
          form.form-registro(@submit.prevent="registrarEncomenda")
            .form-group
              label Morador
              select(v-model="formRegistro.moradorId" required)
                option(value="") Selecione o morador
                option(v-for="m in moradores" :key="m.id" :value="m.id") {{ m.nome || (m.username + ' ' + (m.sobrenome || '')) }} {{ m.bloco && m.apartamento ? ` - ${m.bloco}/${m.apartamento}` : '' }}
            .form-group
              label Descrição (opcional)
              input(v-model="formRegistro.descricao" type="text" placeholder="Ex: Caixa, envelope")
            button.btn-primary(type="submit") Registrar

        .card-acao
          h3 Validar retirada
          form.form-retirada(@submit.prevent="validarRetirada")
            .form-group
              label Código de retirada
              input(v-model="codigoRetirada" type="text" placeholder="Ex: ABC123" maxlength="20" required)
            button.btn-primary(type="submit") Confirmar retirada

      //- Lista (todos ou só do morador)
      .lista-encomendas
        table.lista-moradores(v-if="encomendas.length")
          thead
            tr
              th Código
              th Status
              th Morador
              th Descrição
              th Recebimento
              th Retirada
          tbody
            tr(v-for="e in encomendas" :key="e.id")
              td {{ e.codigoRetirada }}
              td {{ statusLabel(e.status) }}
              td {{ e.moradorNome }}
              td {{ e.descricao || '-' }}
              td {{ formatarData(e.dataRecebimento) }}
              td {{ e.dataRetirada ? formatarData(e.dataRetirada) : '-' }}
        .vazio(v-else)
          p Nenhuma encomenda encontrada.

      .paginacao(v-if="totalPaginas > 1")
        button.btn-outline.pag-btn(:disabled="paginaAtual === 0" @click="carregarPagina(paginaAtual - 1)") ‹
        span Página {{ paginaAtual + 1 }} de {{ totalPaginas }}
        button.btn-outline.pag-btn(:disabled="paginaAtual >= totalPaginas - 1" @click="carregarPagina(paginaAtual + 1)") ›
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import MenuLateral from '@/components/Layout/MenuLateral.vue'
import {
  listarEncomendas,
  registrarEncomenda as apiRegistrar,
  marcarRetirada,
  type EncomendaDTO,
  type EncomendaRegistroRequest,
} from '@/services/encomendaService'
import { fetchUsersByEmpresaRaw } from '@/services/userService'
import { toast } from 'vue3-toastify'

const ROLES_PORTARIA = ['ADMIN', 'ADMINISTRATIVO', 'SINDICO', 'SUB_SINDICO', 'PORTARIA', 'SEGURANCA', 'ZELADOR']
const perfil = ref(localStorage.getItem('userPerfil') || '')
const empresa = ref<{ id?: number }>({})
const encomendas = ref<EncomendaDTO[]>([])
const totalPaginas = ref(0)
const paginaAtual = ref(0)
const size = 10
const moradores = ref<any[]>([])
const formRegistro = ref<{ moradorId: string; empresaId: number; descricao: string }>({ moradorId: '', empresaId: 0, descricao: '' })
const codigoRetirada = ref('')

const podeRegistrarOuValidar = computed(() => ROLES_PORTARIA.includes(perfil.value))

function statusLabel(s: string) {
  const map: Record<string, string> = { AGUARDANDO: 'Aguardando', DISPONIVEL: 'Disponível', RETIRADA: 'Retirada' }
  return map[s] || s
}

function formatarData(d: string) {
  if (!d) return '-'
  try {
    return new Date(d).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' })
  } catch {
    return d
  }
}

async function carregarPagina(page: number) {
  const eid = empresa.value?.id
  if (!eid) return
  const userId = localStorage.getItem('userId')
  const onlyMorador = !podeRegistrarOuValidar.value && userId ? Number(userId) : undefined
  const res = await listarEncomendas({
    empresaId: eid,
    moradorId: onlyMorador,
    page,
    size,
  })
  encomendas.value = res.content || []
  totalPaginas.value = res.totalPages ?? 0
  paginaAtual.value = page
}

async function carregarMoradores() {
  const eid = empresa.value?.id
  if (!eid) return
  const res = await fetchUsersByEmpresaRaw(eid, 0, 500)
  const content = res?.content ?? []
  moradores.value = content.map((u: any) => ({
    id: u.id,
    nome: (u.username || '') + ' ' + (u.sobrenome || ''),
    username: u.username,
    sobrenome: u.sobrenome,
    bloco: u.bloco,
    apartamento: u.apartamento,
  }))
}

async function registrarEncomenda() {
  const eid = empresa.value?.id
  const moradorId = Number(formRegistro.value.moradorId)
  if (!eid || !moradorId) {
    toast.error('Selecione o morador.')
    return
  }
  try {
    await apiRegistrar({
      moradorId,
      empresaId: eid,
      descricao: formRegistro.value.descricao || undefined,
    })
    toast.success('Encomenda registrada. O morador pode retirar com o código gerado.')
    formRegistro.value = { moradorId: '', empresaId: eid, descricao: '' }
    carregarPagina(paginaAtual.value)
  } catch (err: any) {
    toast.error(err?.response?.data?.message || 'Erro ao registrar encomenda.')
  }
}

async function validarRetirada() {
  const eid = empresa.value?.id
  const cod = codigoRetirada.value?.trim()
  if (!eid || !cod) {
    toast.error('Informe o código de retirada.')
    return
  }
  try {
    await marcarRetirada(cod, eid)
    toast.success('Retirada confirmada.')
    codigoRetirada.value = ''
    carregarPagina(paginaAtual.value)
  } catch (err: any) {
    toast.error(err?.response?.data?.message || 'Código não encontrado ou encomenda já retirada.')
  }
}

onMounted(() => {
  try {
    const emp = localStorage.getItem('userEmpresa')
    empresa.value = emp ? JSON.parse(emp) : {}
  } catch {
    empresa.value = {}
  }
  perfil.value = localStorage.getItem('userPerfil') || ''
  carregarPagina(0)
  if (podeRegistrarOuValidar.value) {
    carregarMoradores()
  }
})

watch(empresa, () => {
  if (empresa.value?.id) carregarPagina(0)
}, { deep: true })
</script>

<style scoped>
.encomendas-view .area-conteudo { padding: 1rem; }
.titulo { margin-bottom: 1rem; color: var(--color-primary); }
.acoes-portaria { display: flex; flex-wrap: wrap; gap: 1rem; margin-bottom: 1.5rem; }
.card-acao { background: var(--color-surface); border: 1px solid var(--color-primary-border); border-radius: 8px; padding: 1rem; min-width: 280px; }
.card-acao h3 { margin: 0 0 0.75rem; font-size: 1rem; color: var(--color-text); }
.form-group { margin-bottom: 0.75rem; }
.form-group label { display: block; margin-bottom: 0.25rem; font-weight: 600; }
.form-group input, .form-group select { width: 100%; padding: 0.5rem; border: 1px solid var(--color-border); border-radius: 6px; }
.lista-moradores { width: 100%; border-collapse: collapse; margin-top: 1rem; }
.lista-moradores th, .lista-moradores td { padding: 0.5rem; text-align: left; border-bottom: 1px solid var(--color-border); }
.lista-moradores th { background: var(--color-surface-alt); color: var(--color-text); }
.vazio { padding: 2rem; text-align: center; color: var(--color-text-muted); }
.paginacao { margin-top: 1rem; display: flex; align-items: center; gap: 0.5rem; }
.pag-btn { padding: 0.35rem 0.75rem; border-radius: 8px; cursor: pointer; }
.pag-btn:disabled { opacity: 0.5; cursor: not-allowed; }
</style>
