<template lang="pug">
div.modal-overlay(@click.self="$emit('close')")
  div.modal-container
    h3 Confirmar Exclusão

    p.text-info
      | Tem certeza que deseja excluir
      span(style="margin-left: 0.25em; text-transform: capitalize") {{ tipo }}
      | ?


    // Exibição dos detalhes com nome e CNPJ/email/id estilizados
    div.detalhes(v-if="item && tipo === 'empresa'")
      span.nome {{ item.name }}
      br
      span.cnpj CNPJ: {{ item.cnpj }}

    div.detalhes(v-else-if="item && tipo === 'usuario'")
      span.nome {{ item.username }}
      br
      span.email Email: {{ item.email }}

    div.detalhes(v-else-if="item && tipo === 'licenca'")
      span.nome Nom: {{ item.empresaNome }}
      span.nome CNPJ: {{ item.empresaCnpj }}
      br
      span.tipo Tipo Licenca: {{ formatarTipo(item.tipo) }}

    div.detalhes(v-else)
      em (sem dados)

    div.actions
      button.btn.btn-secondary(@click="$emit('close')") Cancelar
      button.btn.btn-danger(@click="$emit('confirmar')") Sim, Excluir
</template>

<script setup lang="ts">
const props = defineProps<{
  tipo: string
  item: Record<string, any> | null
}>()
const emit = defineEmits(['close', 'confirmar'])

function formatarTipo(tipo: string): string {
  const mapTipos: Record<string, string> = {
    BASIC: 'Básica',
    PREMIUM: 'Premium',
    ENTERPRISE: 'Enterprise',
  }
  return mapTipos[tipo] || tipo || '—'
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-container {
  background: white;
  padding: 1.75rem 2rem;
  border-radius: 8px;
  max-width: 400px;
  width: 90%;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.3);
  position: relative;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

h3 {
  color: #dc3545;
  margin-bottom: 1rem;
  font-weight: 700;
}

.text-info {
  font-size: 1.1rem;
  margin-bottom: 1.5rem;
}

strong {
  color: #dc3545;
}

.detalhes {
  font-size: 1rem;
  margin-bottom: 2rem;
  line-height: 1.4;
  color: #333;
}

.detalhes .nome {
  font-weight: 600;
  font-size: 1.15rem;
  display: block;
  margin-bottom: 0.25rem;
}

.detalhes .cnpj,
.detalhes .email,
.detalhes .tipo {
  font-style: normal;
  color: #666;
  font-size: 0.95rem;
  display: block;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
}

.btn {
  cursor: pointer;
  padding: 0.5rem 1.2rem;
  border-radius: 4px;
  border: none;
  font-weight: 600;
  font-size: 1rem;
  transition:
    background-color 0.25s ease,
    color 0.25s ease;
  user-select: none;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background-color: #5a6268;
}

.btn-danger {
  background-color: #dc3545;
  color: white;
}

.btn-danger:hover {
  background-color: #a71d2a;
}
</style>
