<template lang="pug">
.modal-overlay(@click.self="$emit('close')")
  .modal
    header.modal-header
      h3.modal-title Solucionar Reclamação
      // Exibe dados básicos da reclamação
      .reclamacao-info
        p
          strong Tipo:
          |  {{ reclamacao.tipo }}
        p
          strong Detalhes:
          |  {{ reclamacao.detalhes }}
        p
          strong Data de Criação:
          |  {{ formatarData(reclamacao.dataCriacao) }}
    section.modal-body
      label(for="descricaoSolucao") Descrição da Solução
      textarea(
        id="descricaoSolucao"
        v-model="descricaoSolucao"
        rows="5"
        placeholder="Descreva a solução da reclamação..."
      )
    footer.modal-footer
      button.btn.btn-primary(
        @click="confirmarSolucao"
        :disabled="!descricaoSolucao.trim() || isLoading"
      ) {{ isLoading ? 'Enviando...' : 'Confirmar' }}
      button.btn.btn-cancel(@click="$emit('close')") Cancelar
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { solucionarReclamacao } from '@/services/denunciaService'

const props = defineProps<{ reclamacao: any }>()
const emit = defineEmits<{
  (e: 'close'): void
  (e: 'confirmar', payload: any): void
}>()

const descricaoSolucao = ref('')
const isLoading = ref(false)

function formatarData(dataISO: string) {
  if (!dataISO) return ''
  const d = new Date(dataISO)
  return d.toLocaleDateString() + ' ' + d.toLocaleTimeString()
}

async function confirmarSolucao() {
  if (!descricaoSolucao.value.trim()) {
    alert('Por favor, informe a descrição da solução.')
    return
  }
  isLoading.value = true
  try {
    // Chamada da API passando id da reclamação e objeto da solução
    const resposta = await solucionarReclamacao(props.reclamacao.id, {
      descricao: descricaoSolucao.value.trim(),
    })

    emit('confirmar', resposta)
    emit('close')
    descricaoSolucao.value = ''
  } catch (error) {
    console.error('Erro ao solucionar reclamação:', error)
    alert('Erro ao enviar solução. Tente novamente.')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background: #fff;
  border-radius: 12px;
  max-width: 600px;
  width: 90%;
  max-height: 90vh;
  padding: 2rem;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
}

.modal-header {
  margin-bottom: 1rem;
}

.reclamacao-info p {
  margin: 0.2rem 0;
  font-size: 0.9rem;
  color: #555;
}

.modal-body {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
}

label {
  font-weight: 600;
  margin-bottom: 0.3rem;
}

textarea {
  resize: vertical;
  padding: 0.5rem;
  font-size: 1rem;
  border-radius: 6px;
  border: 1px solid #ccc;
  min-height: 100px;
  margin-bottom: 1rem;
  font-family: inherit;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}

.btn {
  padding: 0.5rem 1.25rem;
  border-radius: 6px;
  border: none;
  cursor: pointer;
  font-weight: 600;
  font-size: 1rem;
  transition: background-color 0.2s ease;
}

.btn-primary {
  background-color: #b38600;
  color: white;
}

.btn-primary:disabled {
  background-color: #ddd0a7;
  cursor: not-allowed;
}

.btn-primary:hover:not(:disabled) {
  background-color: #b38600;
}

.btn-cancel {
  background-color: #ccc;
  color: #444;
}

.btn-cancel:hover {
  background-color: #999;
}
</style>
