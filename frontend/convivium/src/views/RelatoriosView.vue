<template lang="pug">
.layout-admin
  MenuLateral(:itemSelecionado="''")
  section.relatorios-container
    h2.titulo-dashboard Relatórios e Métricas do Condomínio

    //- Filtros de período
    .filtros
      label(for="inicio") Início:
      input#inicio(type="date" v-model="dataInicio")

      label(for="fim") Fim:
      input#fim(type="date" v-model="dataFim")

      button.btn-filtrar(@click="filtrarReclamacoes") Filtrar

    //- Métricas principais
    .metricas-grid
      .card-metrica
        h3 Total de Reclamações
        p.valor {{ totalReclamacoes }}
      .card-metrica
        h3 Reclamações Pendentes
        p.valor {{ totalPendentes }}
      .card-metrica
        h3 Tempo Médio de Resolução (dias)
        p.valor {{ tempoMedioResolucao.toFixed(1) }}
      .card-metrica
        h3 Unidade com mais Reclamações
        p.valor {{ unidadeMaisReclama }}

    //- Botões exportar
    .botoes-relatorios
      ExportPdf(:dados="dadosParaPdf")
      button.btn-xls(title="Em desenvolvimento" disabled)
        span 🔒
        |  📊 Exportar Excel




    //- Gráficos
    .graficos
      .grafico
        h4 Reclamações por Tipo
        canvas#pieChart

      .grafico
        h4 Reclamações por Mês
        canvas#barChart

      .grafico
        h4 Reclamações por Status
        canvas#horizontalBarChart


    //- Histórico em cards separados
    .historicos-grid
      .card-historico
        h3 Últimas 10 Reclamações Abertas
        ul.lista-historico
          li(v-for="(r, i) in ultimasReclamacoes" :key="i")
            strong {{ r.unidade }}:
            |  {{ r.tipo }} - {{ formatarData(r.dataAbertura) }}


      .card-historico
        h3 Top 10 Unidades que Mais Reclamam
        ul.lista-historico
          li(v-for="(item, i) in topUnidadesQueMaisReclamam" :key="i")
            strong {{ item.unidade }}:
            |  {{ item.qtd }} reclamações

      .card-historico
        h3 Top 10 Unidades Mais Reclamadas
        ul.lista-historico
          li(v-for="(item, i) in topUnidadesMaisReclamadas" :key="i")
            strong {{ item.unidade }}:
            |  {{ item.qtd }} reclamações
</template>
<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import {
  Chart,
  PieController,
  BarController,
  CategoryScale,
  LinearScale,
  ArcElement,
  BarElement,
  Tooltip,
  Legend,
} from 'chart.js'
import { buscarRelatorioReclamacoes } from '@/services/relatorioService'
import ExportPdf from '@/views/components/ExportPdf.vue'
import MenuLateral from '@/components/Layout/MenuLateral.vue'

Chart.register(
  PieController,
  BarController,
  ArcElement,
  BarElement,
  CategoryScale,
  LinearScale,
  Tooltip,
  Legend,
)
let horizontalBarChart: Chart | null = null
const reclamacoesPorStatus = ref<Record<string, number>>({
  Pendentes: 0,
  Resolvidas: 0,
  'Em Análise': 0,
})

const dadosParaPdf = computed(() => ({
  totalReclamacoes: totalReclamacoes.value,
  totalPendentes: totalPendentes.value,
  tempoMedioResolucao: tempoMedioResolucao.value,
  unidadeMaisReclama: unidadeMaisReclama.value,
  ultimasReclamacoes: ultimasReclamacoes.value,
  topUnidadesQueMaisReclamam: topUnidadesQueMaisReclamam.value,
  topUnidadesMaisReclamadas: topUnidadesMaisReclamadas.value,
  formatarData,
}))

const dataInicio = ref('')
const dataFim = ref('')

const totalReclamacoes = ref(0)
const totalPendentes = ref(0)
const tempoMedioResolucao = ref(0)
const unidadeMaisReclama = ref('-')

const ultimasReclamacoes = ref<any[]>([])
const topUnidadesQueMaisReclamam = ref<any[]>([])
const topUnidadesMaisReclamadas = ref<any[]>([])
const reclamacoesPorTipo = ref<Record<string, number>>({})
const reclamacoesPorMes = ref<Record<string, number>>({})

let pieChart: Chart | null = null
let barChart: Chart | null = null

async function filtrarReclamacoes() {
  if (!dataInicio.value || !dataFim.value) return alert('Selecione um período válido.')

  const empresa = JSON.parse(localStorage.getItem('userEmpresa') || '{}')
  const idEmpresa = empresa.id || null

  try {
    const resposta = await buscarRelatorioReclamacoes(idEmpresa, dataInicio.value, dataFim.value)

    totalReclamacoes.value = resposta.totalReclamacoes
    totalPendentes.value = resposta.totalPendentes
    tempoMedioResolucao.value = resposta.tempoMedioResolucao
    unidadeMaisReclama.value = resposta.unidadeMaisReclama

    ultimasReclamacoes.value = resposta.ultimasReclamacoes || []
    topUnidadesQueMaisReclamam.value = resposta.topUnidadesQueMaisReclamam || []
    topUnidadesMaisReclamadas.value = resposta.topUnidadesMaisReclamadas || []
    reclamacoesPorStatus.value['Pendentes'] = resposta.totalPendentes || 0
    reclamacoesPorStatus.value['Resolvidas'] =
      (resposta.totalReclamacoes || 0) - (resposta.totalPendentes || 0)
    reclamacoesPorStatus.value['Em Análise'] = resposta.totalEmAnalise || 0 // se tiver esse dado, senão pode omitir ou ajustar

    // Se os dados não vierem da API, calcular manualmente
    const tipos: Record<string, number> = {}
    const meses: Record<string, number> = {}

    for (const r of ultimasReclamacoes.value) {
      // Agrupa por tipo
      if (r.tipo) {
        tipos[r.tipo] = (tipos[r.tipo] || 0) + 1
      }

      // Agrupa por mês (formato MM/YYYY)
      if (r.dataAbertura) {
        const data = new Date(r.dataAbertura)
        const mesAno = `${(data.getMonth() + 1).toString().padStart(2, '0')}/${data.getFullYear()}`
        meses[mesAno] = (meses[mesAno] || 0) + 1
      }
    }

    reclamacoesPorTipo.value = tipos
    reclamacoesPorMes.value = meses
    reclamacoesPorStatus.value['Pendentes'] = resposta.totalPendentes || 0
    reclamacoesPorStatus.value['Resolvidas'] =
      (resposta.totalReclamacoes || 0) - (resposta.totalPendentes || 0)
    reclamacoesPorStatus.value['Em Análise'] = resposta.totalEmAnalise || 0 // se tiver esse dado, senão pode omitir ou ajustar

    atualizarGraficos()
  } catch (e) {
    console.error('Erro ao buscar relatório:', e)
    alert('Erro ao buscar dados do relatório.')
  }
}

function atualizarGraficos() {
  // Gráfico de pizza (por tipo)
  const tipos = Object.keys(reclamacoesPorTipo.value)
  const valoresTipo = Object.values(reclamacoesPorTipo.value)

  if (pieChart) {
    pieChart.data.labels = tipos
    pieChart.data.datasets[0].data = valoresTipo
    pieChart.update()
  }

  // Gráfico de barras (por mês)
  const meses = Object.keys(reclamacoesPorMes.value)
  const valoresMes = Object.values(reclamacoesPorMes.value)

  if (barChart) {
    barChart.data.labels = meses
    barChart.data.datasets[0].data = valoresMes
    barChart.update()
  }
  if (horizontalBarChart) {
    const labels = Object.keys(reclamacoesPorStatus.value)
    const data = Object.values(reclamacoesPorStatus.value)

    horizontalBarChart.data.labels = labels
    horizontalBarChart.data.datasets[0].data = data
    horizontalBarChart.update()
  }
}
function formatarData(dataIso: string): string {
  const d = new Date(dataIso)
  return d.toLocaleDateString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

onMounted(() => {
  // Inicializa os gráficos
  const pieCtx = document.getElementById('pieChart') as HTMLCanvasElement
  pieChart = new Chart(pieCtx, {
    type: 'bar',
    data: {
      labels: [],
      datasets: [
        {
          data: [],
          backgroundColor: ['#f87171', '#60a5fa', '#34d399', '#fbbf24'],
        },
      ],
    },
    options: {
      responsive: true,
      plugins: { legend: { position: 'bottom' } },
    },
  })

  const barCtx = document.getElementById('barChart') as HTMLCanvasElement
  barChart = new Chart(barCtx, {
    type: 'bar',
    data: {
      labels: [],
      datasets: [
        {
          label: 'Reclamações',
          data: [],
          backgroundColor: '#4ade80',
        },
      ],
    },
    options: {
      responsive: true,
      scales: { y: { beginAtZero: true } },
      plugins: { legend: { display: false } },
    },
  })

  const horizontalCtx = document.getElementById('horizontalBarChart') as HTMLCanvasElement
  horizontalBarChart = new Chart(horizontalCtx, {
    type: 'bar',
    data: {
      labels: [],
      datasets: [
        {
          label: 'Quantidade',
          data: [],
          backgroundColor: ['#fbbf24', '#60a5fa', '#34d399'], // cores diferentes
        },
      ],
    },
    options: {
      indexAxis: 'y', // importante para horizontal
      responsive: true,
      scales: {
        x: {
          beginAtZero: true,
        },
        y: {
          ticks: {
            autoSkip: false,
          },
        },
      },
      plugins: {
        legend: { display: false },
        tooltip: { enabled: true },
      },
    },
  })

  // Define período padrão (últimos 30 dias)
  const hoje = new Date()
  const trintaDiasAtras = new Date()
  trintaDiasAtras.setDate(hoje.getDate() - 30)

  dataFim.value = hoje.toISOString().split('T')[0]
  dataInicio.value = trintaDiasAtras.toISOString().split('T')[0]

  // Busca inicial
  filtrarReclamacoes()
})

function exportarXLS() {
  alert('Exportar Excel - ainda não implementado.')
}
</script>

<style scoped>
.layout-admin {
  display: flex;
  min-height: 100vh;
  /* Em telas pequenas o menu pode virar topo, mas se não quiser mudar estrutura, só ajustar scroll */
  overflow-x: hidden;
}

.relatorios-container {
  padding: 2rem;
  background: #fffde8;
  min-height: 100vh;
  font-family: Arial, sans-serif;
  flex-grow: 1;
  box-sizing: border-box;
}

/* Título */
.titulo-dashboard {
  font-size: 1.8rem;
  font-weight: bold;
  margin-bottom: 2rem;
  color: var(--color-primary-text-on);
}

/* Filtros */
.filtros {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.filtros label {
  font-weight: 600;
  white-space: nowrap;
}

.filtros input {
  padding: 0.3rem 0.5rem;
  border-radius: 5px;
  border: 1px solid #ccc;
  min-width: 140px;
}

.btn-filtrar {
  padding: 0.5rem 1rem;
  background: var(--color-primary);
  border: none;
  border-radius: 6px;
  font-weight: bold;
  cursor: pointer;
  transition: background 0.3s;
  white-space: nowrap;
}

.btn-filtrar:hover {
  background: var(--color-primary-hover);
}

/* Métricas */
.metricas-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.card-metrica {
  background: #fff;
  padding: 1.2rem;
  border-left: 5px solid #d9c400;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.05);
}

.card-metrica h3 {
  font-size: 1.1rem;
  margin-bottom: 0.5rem;
}

.valor {
  font-size: 2rem;
  font-weight: bold;
  color: #3e3e10;
}

/* Botões exportar */
.botoes-relatorios {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.btn-xls {
  padding: 0.6rem 1.2rem;
  background-color: #e5e7eb;
  border: none;
  font-weight: bold;
  cursor: not-allowed;
  border-radius: 6px;
  opacity: 0.7;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  position: relative;
}

.btn-xls:hover::after {
  content: 'Em desenvolvimento';
  position: absolute;
  background: #333;
  color: #fff;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.75rem;
  white-space: nowrap;
  transform: translateY(-120%);
}

/* Gráficos */
.graficos {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.grafico {
  flex: 1 1 32%;
  max-width: 32%;
  min-width: 280px;
  background: #fff;
  padding: 1rem;
  border-radius: 12px;
  box-shadow: 0 0 6px rgba(0, 0, 0, 0.08);
  text-align: center;
}

.grafico canvas {
  width: 100% !important;
  height: 200px !important;
}

@media (min-width: 1200px) {
  .grafico {
    min-width: 350px;
    max-width: 32%;
  }
  .grafico canvas {
    height: 280px !important;
  }
}

.grafico h4 {
  font-weight: 600;
  margin-bottom: 1rem;
}

/* Histórico */
.historicos-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
  margin-top: 2rem;
}

.card-historico {
  background: #fff;
  padding: 1rem;
  border-left: 5px solid #d9c400;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.05);
  max-height: 300px;
  overflow-y: auto;
  border-radius: 6px;
}

.card-historico h3 {
  margin-bottom: 1rem;
  font-size: 1.2rem;
  color: var(--color-primary-text-on);
}

.lista-historico {
  list-style: none;
  padding-left: 0;
  font-size: 0.95rem;
}

.lista-historico li {
  margin-bottom: 0.5rem;
  line-height: 1.3;
}

/* ========================= */
/* RESPONSIVE */

/* Ajusta layout para telas pequenas */
@media (max-width: 900px) {
  .layout-admin {
    flex-direction: column;
  }

  .relatorios-container {
    padding: 1rem;
    min-height: auto;
  }

  /* Filtros empilhados */
  .filtros {
    flex-direction: column;
    align-items: stretch;
    gap: 0.75rem;
  }

  .filtros label {
    margin-bottom: 0.25rem;
  }

  .filtros input,
  .btn-filtrar {
    width: 100%;
    min-width: unset;
  }

  /* Métricas em 1 ou 2 colunas */
  .metricas-grid {
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
    gap: 1rem;
  }

  /* Gráficos em 1 coluna */
  .graficos {
    flex-direction: column;
  }

  .grafico {
    max-width: 100%;
    min-width: auto;
  }

  /* Histórico em 1 coluna */
  .historicos-grid {
    grid-template-columns: 1fr;
  }

  /* Botões exportar centralizados */
  .botoes-relatorios {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .titulo-dashboard {
    font-size: 1.4rem;
    margin-bottom: 1rem;
  }

  .card-metrica h3 {
    font-size: 1rem;
  }

  .valor {
    font-size: 1.5rem;
  }

  .card-historico h3 {
    font-size: 1.1rem;
  }

  .lista-historico {
    font-size: 0.9rem;
  }
}
</style>
