<template lang="pug">
.modal-backdrop(@click.self="close" role="dialog" aria-modal="true")
  .modal-content(tabindex="-1" ref="modalContent")
    header.modal-header
      slot(name="header")
      button.btn-close(aria-label="Fechar modal" @click="close") &times;
    section.modal-body
      slot(name="body")
    footer.modal-footer
      slot(name="footer")
</template>

<script setup>
import { defineEmits, onMounted, ref } from 'vue'
const emit = defineEmits(['close'])

const close = () => emit('close')

const modalContent = ref(null)
onMounted(() => {
  modalContent.value?.focus()
})
</script>

<style>
/* Modal Backdrop */
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

/* Modal Content */
.modal-content {
  background: white;
  border-radius: 16px;
  max-width: 680px;
  width: 95%;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.25);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  outline: none;
  animation: slideIn 0.35s ease forwards;
  padding: 0; /* remove padding externo */
}

/* Header */
.modal-header {
  padding: 1.25rem 2rem;
  background: #42b983;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  font-size: 1.25rem;
}

/* Close Button */
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

/* Body */
.modal-body {
  padding: 2rem;
  color: #333;
  font-size: 1.05rem;
  overflow-y: auto;
  max-height: 70vh;
  line-height: 1.6;
}

/* Footer */
.modal-footer {
  padding: 1.25rem 2rem;
  background: #f6f6f6;
  color: #333;
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  font-size: 1rem;
}

/* Form */
form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  font-size: 1.05rem;
  width: 100%;
}

/* Labels */
label {
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #222;
  font-size: 1rem;
}

/* Inputs, Selects, Textareas */
input,
select,
textarea {
  padding: 0.75rem 1rem;
  border: 2px solid #ccc;
  border-radius: 10px;
  font-size: 1.05rem;
  transition:
    border-color 0.3s ease,
    box-shadow 0.3s ease;
  width: 100%;
  box-sizing: border-box;
}

input:focus,
select:focus,
textarea:focus {
  border-color: #42b983;
  box-shadow: 0 0 8px rgba(66, 185, 131, 0.3);
}

/* Textarea */
textarea {
  min-height: 140px;
  resize: vertical;
}

/* Botões */
button {
  background-color: #42b983;
  color: white;
  border: none;
  padding: 0.8rem 1.6rem;
  font-size: 1.05rem;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 600;
  transition:
    background-color 0.25s ease,
    box-shadow 0.25s ease;
  box-shadow: none;
  min-width: 120px;
}

button:hover,
button:focus {
  background-color: #369b6f;
  box-shadow: 0 0 10px rgba(54, 155, 111, 0.5);
}

/* Checkbox & Radio */
input[type='checkbox'],
input[type='radio'] {
  width: 20px;
  height: 20px;
  margin-right: 0.6rem;
  vertical-align: middle;
}

.checkbox-label,
.radio-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

/* Error Message */
.error-message {
  color: #d93025;
  font-size: 0.9rem;
  margin-top: -0.5rem;
  font-weight: 600;
}

/* Titles */
h1,
h2,
h3,
h4,
h5,
h6 {
  margin-bottom: 1rem;
  font-weight: 700;
  color: #222;
}

/* Links */
a {
  color: #42b983;
  text-decoration: none;
  transition: color 0.2s ease;
}

a:hover,
a:focus {
  color: #369b6f;
}

/* Animações */
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

@media (max-width: 600px) {
  .modal-content {
    max-width: 95%;
    padding: 1rem;
  }

  .modal-body {
    padding: 1.25rem;
  }

  .modal-footer {
    flex-direction: column;
    align-items: stretch;
  }

  button {
    width: 100%;
  }
}

input,
select,
textarea {
  height: 40px;
  padding: 0 1.25rem;
  font-size: 1.15rem;
  border: 2px solid #ccc;
  border-radius: 10px;
  box-sizing: border-box;
}
</style>
