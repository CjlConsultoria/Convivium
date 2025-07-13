<template lang="pug">
.modal-overlay(@click.self="$emit('close')")
  .modal
    header.modal-header
      h3.modal-title Detalhes da Reclamação
    section.modal-body
      p
        strong Tipo:
        |  {{ reclamacao.tipo }}
      p
        strong Descrição:
        |  {{ reclamacao.detalhes }}
      p
        strong Data de Criação:
        |  {{ formatarData(reclamacao.dataCriacao) }}
      p
        strong Reclamante:
        |  {{ reclamacao.usuario.username }} - Bloco {{ reclamacao.usuario.bloco }}, Apto {{ reclamacao.usuario.apartamento }} ({{ reclamacao.usuario.tipoPerfil }})
      p
        strong Condominio:
        |  {{ reclamacao.empresa.name }} ({{ reclamacao.empresa.cnpj }})
      div.anexos
        strong Anexos:
        ul
          li(v-for="anexo in reclamacao.anexos" :key="anexo.id") {{ anexo.nomeArquivo }}

      //- Seção histórico
      .historico
        strong.titulo-historico Histórico de Ações
        ul.lista-acoes
          li.sem-acoes(v-if="historico.length === 0") Nenhuma ação registrada.
          li.acao-item(v-for="acao in historico" :key="acao.id")
            .acao-header
              span.status(:class="`status-${acao.status.toLowerCase()}`") {{ formatarStatus(acao.status) }}
              span.data {{ formatarData(acao.dataCriacao) }}
            .acao-corpo
              p.usuario
                strong Usuário: 
                | {{ acao.usuario ? acao.usuario.username : reclamacao.usuario.username }}
                |  ({{ acao.usuario ? acao.usuario.tipoPerfil : reclamacao.usuario.tipoPerfil }})
              p.descricao
                strong Descrição: 
                | {{ acao.descricao }}
              p.solucao
                strong Solução: 
                | {{ reclamacao.descricaoSolucao || 'Não tem dados' }}

    footer.modal-footer
      button.btn.btn-primary(@click="$emit('close')") Fechar
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { listarAcoesReclamacao } from '@/services/denunciaService'

const props = defineProps<{ reclamacao: any }>()

const historico = ref<any[]>([])

onMounted(async () => {
  console.log('Reclamação recebida no modal:', props.reclamacao)
  await carregarHistorico()
})

async function carregarHistorico() {
  try {
    historico.value = await listarAcoesReclamacao(props.reclamacao.id)
  } catch (error) {
    console.error('Erro ao carregar histórico:', error)
  }
}

function formatarData(dataISO: string) {
  if (!dataISO) return ''
  const d = new Date(dataISO)
  return d.toLocaleDateString() + ' ' + d.toLocaleTimeString()
}

function formatarStatus(status: string) {
  const map: Record<string, string> = {
    EM_ANDAMENTO: 'Em andamento',
    EM_ANALISE: 'Em análise',
    EM_NOTIFICACAO: 'Em notificação',
    SOLUCIONADA: 'Solucionada',
    CANCELADA: 'Cancelada',
  }
  return map[status] || status
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.55);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1100;
  padding: 1rem;
  overflow-y: auto;
}

.modal {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
  max-width: 600px;
  width: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.modal-header {
  background: #b38600;
  padding: 1.25rem 1.5rem;
  color: white;
  font-weight: 700;
  font-size: 1.6rem;
  user-select: none;
  box-shadow: inset 0 -3px 10px rgba(0, 0, 0, 0.2);
}

.modal-body {
  padding: 1.75rem 2rem;
  color: #333;
  font-size: 1rem;
  line-height: 1.5;
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.modal-body p strong {
  min-width: 110px;
  display: inline-block;
  font-weight: 600;
  color: #b38600;
}

.anexos {
  margin-top: 1rem;
}

.anexos ul {
  list-style-type: disc;
  margin-left: 1.5rem;
  color: #555;
}

/* Seção histórico */
.historico {
  margin-top: 2rem;
  padding-top: 1rem;
  border-top: 2px solid #b38600;
}

.historico strong {
  font-size: 1.1rem;
  color: #b38600;
  margin-bottom: 0.5rem;
  display: block;
}

.historico ul {
  list-style-type: none;
  padding-left: 0;
  max-height: 220px;
  overflow-y: auto;
  border: 1px solid #ddd;
  border-radius: 8px;
  background: #fafafa;
}

.historico li.acao-item {
  flex-direction: column;
  align-items: flex-start;
  padding: 0.8rem 1rem;
  border-bottom: 1px solid #eee;
  gap: 0.4rem;
  font-size: 0.95rem;
  color: #444;
  background: #fff;
  box-shadow: 0 1px 3px rgb(0 0 0 / 0.05);
  border-radius: 6px;
  margin-bottom: 0.6rem;
  display: flex;
}

.historico li.acao-item:last-child {
  margin-bottom: 0;
  border-bottom: none;
  box-shadow: none;
}

.acao-header {
  width: 100%;
  display: flex;
  justify-content: space-between;
  font-weight: 700;
  font-size: 0.9rem;
  color: #b38600;
}

.status {
  text-transform: uppercase;
  padding: 0.2rem 0.6rem;
  border-radius: 12px;
  background-color: #b38600;
  color: #fff;
  user-select: none;
  flex-shrink: 0;
  min-width: 120px;
  text-align: center;
}

.data {
  color: #888;
  font-size: 0.85rem;
  flex-shrink: 0;
  min-width: 150px;
  text-align: right;
}

.acao-corpo {
  width: 100%;
  margin-top: 0.5rem;
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  color: #333;
}

.acao-corpo p {
  margin: 0;
  font-size: 0.9rem;
}

.acao-corpo strong {
  color: #b38600;
  min-width: 110px;
  display: inline-block;
}

/* Cores específicas para status */
.status-em_andamento {
  background-color: #3498db;
}

.status-em_analise {
  background-color: #f39c12;
}

.status-em_notificacao {
  background-color: #e67e22;
}

.status-solucionada {
  background-color: #27ae60;
}

.status-cancelada {
  background-color: #e74c3c;
}

.modal-footer {
  padding: 1rem 1.5rem;
  background: #f9f9f9;
  border-top: 1px solid #ddd;
  display: flex;
  justify-content: flex-end;
}

.btn {
  font-weight: 600;
  border-radius: 8px;
  padding: 0.6rem 1.6rem;
  cursor: pointer;
  border: none;
  transition: background-color 0.3s ease;
  box-shadow: 0 2px 6px rgb(0 0 0 / 0.1);
  user-select: none;
  background-color: #b38600;
  color: white;
}

.btn:hover {
  background-color: #b38600;
}

/* Responsividade */
@media (max-width: 480px) {
  .modal {
    max-width: 95vw;
  }
  .modal-header {
    font-size: 1.3rem;
  }
}
</style>
