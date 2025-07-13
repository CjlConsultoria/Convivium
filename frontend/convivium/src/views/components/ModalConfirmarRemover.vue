<template lang="pug">
.modal-backdrop(@click.self="close" role="dialog" aria-modal="true")
  .modal-content(tabindex="-1" ref="modalContent")
    header.modal-header
      h3 Confirmação
      button.btn-close(aria-label="Fechar modal" @click="close") &times;

    section.modal-body
      p Tem certeza que deseja remover este item?

    footer.modal-footer
      button.btn-outline(@click="close") Cancelar
      button.btn-outline.btn-principal(@click="confirmar") Remover
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const emit = defineEmits(['close', 'confirmar'])
const modalContent = ref<HTMLElement | null>(null)

const close = () => emit('close')
const confirmar = () => emit('confirmar')

onMounted(() => {
  modalContent.value?.focus()
})
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  backdrop-filter: blur(5px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease forwards;
}

.modal-content {
  background: white;
  border-radius: 16px;
  max-width: 400px;
  width: 90%;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.25);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  outline: none;
  animation: slideIn 0.35s ease forwards;
  padding: 0;
}

.modal-header {
  padding: 1.25rem 2rem;
  background: #d32f2f;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 1.25rem;
}

.btn-close {
  background: transparent;
  border: none;
  font-size: 2rem;
  cursor: pointer;
  line-height: 1;
  color: white;
  transition: color 0.2s ease;
}

.btn-close:hover,
.btn-close:focus {
  color: #f0f0f0;
  outline: none;
}

.modal-body {
  padding: 2rem;
  color: #333;
  font-size: 1.05rem;
  line-height: 1.6;
}

.modal-footer {
  padding: 1.25rem 2rem;
  background: #f6f6f6;
  color: #333;
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  font-size: 1rem;
}

.btn-outline {
  background-color: transparent;
  border: 2px solid #d32f2f;
  color: #d32f2f;
  padding: 0.8rem 1.6rem;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  transition:
    background-color 0.25s ease,
    box-shadow 0.25s ease;
}

.btn-outline:hover,
.btn-outline:focus {
  background-color: #d32f2f;
  color: white;
  box-shadow: 0 0 10px rgba(211, 47, 47, 0.5);
}

.btn-principal {
  border-color: #b71c1c;
  background-color: #b71c1c;
  color: white;
}

.btn-principal:hover,
.btn-principal:focus {
  background-color: #7f0000;
  border-color: #7f0000;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideIn {
  from {
    transform: translateY(-25px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}
</style>
