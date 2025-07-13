<template lang="pug">
.modal-overlay(@click.self="$emit('close')")
  .modal
    header.modal-header
      h3.modal-title Adicionar Ação à Reclamação

    section.modal-contexto
      strong Reclamação:
      p {{ reclamacao.tipo }} - {{ reclamacao.detalhes }}
      p Data: {{ formatarData(reclamacao.dataCriacao) }}

    section.modal-body
      label(for="tipo") Tipo da Ação
      select(v-model="tipo" id="tipo")
        option(value="" disabled selected) -- Selecione --
        option(value="EM_ANDAMENTO") Em andamento
        option(value="EM_NOTIFICACAO") Em notificação
        option(value="EM_ANALISE") Em análise
        option(value="SOLUCIONADA") Solucionada
        option(value="CANCELADA") Cancelada
      label(for="descricao") Descrição
      textarea(v-model="descricao" id="descricao" rows="5" placeholder="Descreva a ação...")

    footer.modal-footer
      button.btn.btn-primary(
        :disabled="!tipo || !descricao.trim() || loading"
        @click="salvar"
      ) {{ loading ? 'Salvando...' : 'Salvar' }}
      button.btn.btn-cancel(@click="$emit('close')") Cancelar
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { adicionarAcaoReclamacao } from '@/services/denunciaService'

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'salvo', acao: any): void
}>()

const props = defineProps<{ reclamacao: any }>()

const tipo = ref('')
const descricao = ref('')
const loading = ref(false)

function formatarData(dataISO: string) {
  const d = new Date(dataISO)
  return d.toLocaleDateString() + ' ' + d.toLocaleTimeString()
}

async function salvar() {
  if (!tipo.value || !descricao.value.trim()) return

  loading.value = true
  try {
    const acao = await adicionarAcaoReclamacao(props.reclamacao.id, {
      tipo: tipo.value,
      descricao: descricao.value.trim(),
    })

    console.log('Ação adicionada com sucesso:', acao)
    emit('salvo', acao)
    emit('close')
  } catch (error) {
    console.error('Erro ao adicionar ação:', error)
    alert('Erro ao adicionar ação. Tente novamente.')
  } finally {
    loading.value = false
  }
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
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  max-width: 520px;
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

.modal-contexto {
  padding: 0.75rem 1.75rem;
  background-color: #e8f5e9;
  border-left: 6px solid #b38600;
  margin: 1rem 1.75rem 0;
  color: #b38600;
  font-weight: 600;
  border-radius: 6px;
}

.modal-body {
  padding: 1.5rem 1.75rem;
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
  color: #333;
  font-size: 1rem;
}

.modal-body label {
  font-weight: 600;
  margin-bottom: 0.25rem;
  user-select: none;
}

.modal-body select,
.modal-body textarea {
  border: 1.8px solid #ccc;
  border-radius: 8px;
  padding: 0.55rem 0.75rem;
  font-size: 1rem;
  font-family: inherit;
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease;
  outline-offset: 2px;
}

.modal-body select {
  height: 2.8rem;
  cursor: pointer;
}

.modal-body select:focus,
.modal-body textarea:focus {
  border-color: #b38600;
  box-shadow: 0 0 6px #b38600aa;
  outline: none;
}

.modal-body textarea {
  resize: vertical;
  min-height: 100px;
  font-family: inherit;
  line-height: 1.4;
  color: #222;
}

.modal-footer {
  padding: 1rem 1.5rem;
  background: #f9f9f9;
  border-top: 1px solid #ddd;
  display: flex;
  justify-content: flex-end;
  gap: 0.8rem;
}

.btn {
  font-weight: 600;
  border-radius: 8px;
  padding: 0.6rem 1.4rem;
  cursor: pointer;
  border: none;
  transition: background-color 0.3s ease;
  box-shadow: 0 2px 6px rgb(0 0 0 / 0.1);
  user-select: none;
}

.btn-primary {
  background-color: #b38600;
  color: #fff;
}

.btn-primary:hover:enabled {
  background-color: #b38600;
}

.btn-primary:disabled {
  background-color: #c0b492;
  cursor: not-allowed;
}

.btn-cancel {
  background-color: #e0e0e0;
  color: #444;
}

.btn-cancel:hover {
  background-color: #c6c6c6;
}

/* Responsividade simples */
@media (max-width: 480px) {
  .modal {
    max-width: 95vw;
  }
  .modal-header {
    font-size: 1.3rem;
  }
}
</style>
