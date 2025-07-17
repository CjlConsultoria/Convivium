<script setup lang="ts">
import jsPDF from 'jspdf'
import autoTable from 'jspdf-autotable'

interface RelatorioData {
  totalReclamacoes: number
  totalPendentes: number
  tempoMedioResolucao: number
  unidadeMaisReclama: string
  ultimasReclamacoes: Array<{ unidade: string; tipo: string; dataAbertura: string }>
  topUnidadesQueMaisReclamam: Array<{ unidade: string; qtd: number }>
  topUnidadesMaisReclamadas: Array<{ unidade: string; qtd: number }>
  formatarData: (dataIso: string) => string
}

const props = defineProps<{
  dados: RelatorioData
}>()

function gerarPDF() {
  const doc = new jsPDF()

  doc.setFontSize(18)
  doc.text('Relatórios e Métricas do Condomínio', 14, 22)

  let y = 35
  doc.setFontSize(12)

  doc.text(`Total de Reclamações: ${props.dados.totalReclamacoes}`, 14, y)
  y += 8
  doc.text(`Reclamações Pendentes: ${props.dados.totalPendentes}`, 14, y)
  y += 8
  doc.text(`Tempo Médio de Resolução (dias): ${props.dados.tempoMedioResolucao.toFixed(1)}`, 14, y)
  y += 8
  doc.text(`Unidade com mais Reclamações: ${props.dados.unidadeMaisReclama}`, 14, y)
  y += 15

  // Últimas reclamações
  doc.setFontSize(14)
  doc.text('Últimas 10 Reclamações Abertas', 14, y)
  y += 8

  const ultimasRows = props.dados.ultimasReclamacoes.map((r) => [
    r.unidade || '-',
    r.tipo || '-',
    props.dados.formatarData(r.dataAbertura),
  ])

  autoTable(doc, {
    head: [['Unidade', 'Tipo', 'Data de Abertura']],
    body: ultimasRows,
    startY: y,
    margin: { left: 14, right: 14 },
  })

  y = (doc as any).lastAutoTable.finalY + 10

  // Top Unidades que mais reclamam
  doc.setFontSize(14)
  doc.text('Top 10 Unidades que Mais Reclamam', 14, y)
  y += 8

  const topMaisRows = props.dados.topUnidadesQueMaisReclamam.map((item) => [
    item.unidade || '-',
    item.qtd?.toString() || '0',
  ])

  autoTable(doc, {
    head: [['Unidade', 'Quantidade']],
    body: topMaisRows,
    startY: y,
    margin: { left: 14, right: 14 },
  })

  y = (doc as any).lastAutoTable.finalY + 10

  // Top Unidades mais reclamadas
  doc.setFontSize(14)
  doc.text('Top 10 Unidades Mais Reclamadas', 14, y)
  y += 8

  const topMaisReclamadasRows = props.dados.topUnidadesMaisReclamadas.map((item) => [
    item.unidade || '-',
    item.qtd?.toString() || '0',
  ])

  autoTable(doc, {
    head: [['Unidade', 'Quantidade']],
    body: topMaisReclamadasRows,
    startY: y,
    margin: { left: 14, right: 14 },
  })

  doc.save('relatorio_condominio.pdf')
}
</script>

<template>
  <button @click="gerarPDF" class="btn-pdf">📄 Exportar PDF</button>
</template>

<style scoped>
.btn-pdf {
  padding: 0.6rem 1.2rem;
  background-color: #facc15;
  border: none;
  font-weight: bold;
  cursor: pointer;
  border-radius: 6px;
  transition: background 0.3s;
}
.btn-pdf:hover {
  background-color: #e0b300;
}
</style>
