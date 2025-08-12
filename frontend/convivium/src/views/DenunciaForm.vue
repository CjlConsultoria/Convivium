<script setup lang="ts">
import { ref } from 'vue'
import { enviarDenuncia } from '@/services/denunciaService'
import { toast } from 'vue3-toastify'
import { useLoadingStore } from '@/stores/loadingStore'

const store = useLoadingStore()
const etapa = ref(1)

const user = JSON.parse(localStorage.getItem('user') || '{}')

// Dados do denunciante para mostrar no formulário
const denunciante = {
  nome: user.username || '',
  tipo: user.tipo?.name || '',
  bloco: user.bloco || '',
  apartamento: user.apartamento || '',
  empresaNome: user.empresa?.name || '',
  perfil: user.role?.name || '',
}

// Campos da denúncia
const tipos = ['Barulho', 'Agressão', 'Uso indevido de área comum', 'Vazamento', 'Outro']
const tipoSelecionado = ref<string>('')
const detalhes = ref<string>('')
const anexos = ref<File[]>([])

// Próxima etapa
const proximaEtapa = () => {
  if (etapa.value < 3) etapa.value++
}

// Voltar etapa
const etapaAnterior = () => {
  if (etapa.value > 1) etapa.value--
}

const enviarDenunciaForm = async () => {
  try {
    store.startLoading()

    await enviarDenuncia({
      tipo: tipoSelecionado.value,
      detalhes: detalhes.value,
      usuarioId: user.id,
      empresaId: user.empresa?.id,
      arquivos: anexos.value,
    })

    toast.success('Denúncia enviada com sucesso!')
    etapa.value = 1
    tipoSelecionado.value = ''
    detalhes.value = ''
    anexos.value = []
  } catch (error) {
    console.error(error)
    toast.error('Erro ao enviar denúncia.')
  } finally {
    store.stopLoading()
  }
}

function onFileChange(event: Event) {
  const target = event.target as HTMLInputElement
  if (target.files) {
    const novos = Array.from(target.files)
    anexos.value = [...anexos.value, ...novos]
  }
  console.log('Arquivos a enviar:', anexos.value)
}
</script>

<template lang="pug">
section.container
  div.denuncia-container
    h2 Registrar Denúncia / Reclamação

    // Etapas com ícones
    div.etapas-indicador
      div.etapa(:class="{ ativa: etapa >= 1 }")
        i.fas(:class="etapa >= 1 ? 'fa-circle-check' : 'fa-circle'")
        span Etapa 1
      div.etapa(:class="{ ativa: etapa >= 2 }")
        i.fas(:class="etapa >= 2 ? 'fa-circle-check' : 'fa-circle'")
        span Etapa 2
      div.etapa(:class="{ ativa: etapa === 3 }")
        i.fas(:class="etapa === 3 ? 'fa-circle-check' : 'fa-circle'")
        span Etapa 3

    div.etapa-content
      div(v-if="etapa === 1")
        label Tipo de Denúncia
        select(v-model="tipoSelecionado" required class="input")
          option(disabled value="") Selecione...
          option(v-for="tipo in tipos" :key="tipo") {{ tipo }}

      div(v-else-if="etapa === 2")
        label Detalhes
        textarea(v-model="detalhes" placeholder="Descreva o ocorrido..." class="input")

        label.mt-4 Anexos (opcional)
        input(type="file" multiple @change="onFileChange")

      div(v-else-if="etapa === 3")
        h3 Confirmação dos Dados

        p
          strong Nome: 
          | {{ denunciante.nome }}

        p
          strong Cargo: 
          | {{ denunciante.tipo }}

        p
          strong Perfil: 
          | {{ denunciante.perfil }}

        p
          strong Condomínio: 
          | {{ denunciante.empresaNome }}

        p
          strong Bloco: 
          | {{ denunciante.bloco }}

        p
          strong Apartamento: 
          | {{ denunciante.apartamento }}

        p
          strong Tipo de Denúncia: 
          | {{ tipoSelecionado }}

        p
          strong Detalhes: 
          | {{ detalhes }}

        p
          strong Anexos: 
          | {{ anexos.length }} arquivo(s)

    // Botões
    div.button-group
      button.voltar(v-if="etapa > 1" @click="etapaAnterior") Voltar
      button.proximo(v-if="etapa < 3" :disabled="!tipoSelecionado" @click="proximaEtapa") Próximo
      button.enviar(v-if="etapa === 3" @click="enviarDenunciaForm") Enviar
</template>

<style scoped>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css');

.container {
  display: flex;
  justify-content: center;
  padding: 2rem;
}

.denuncia-container {
  background-color: white;
  border: 1px solid #d9c877; /* amarelo suave */
  border-radius: 8px;
  padding: 2rem;
  width: 100%;
  max-width: 600px;
  box-shadow: 0 2px 8px rgba(179, 134, 0, 0.1); /* sombra amarela leve */
}

h2 {
  text-align: center;
  color: #222; /* texto preto escuro */
  margin-bottom: 2rem;
}

/* Etapas com ícones */
.etapas-indicador {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1.5rem;
}

.etapa {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #cfc89a; /* amarelo claro sutil */
  flex: 1;
  font-size: 0.85rem;
}

.etapa i {
  font-size: 1.2rem;
  margin-bottom: 0.25rem;
}

.etapa.ativa {
  color: #b38600; /* amarelo médio */
}

.etapa.ativa i {
  color: #b38600;
}

/* Etapa content */
.etapa-content {
  margin-bottom: 2rem;
}

label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #444;
}

select,
textarea,
input[type='file'] {
  width: 100%;
  padding: 0.75rem;
  font-size: 1rem;
  border: 1px solid #d9c877; /* amarelo suave */
  border-radius: 4px;
  margin-bottom: 1.5rem;
  transition: border-color 0.3s ease;
}

select:focus,
textarea:focus,
input[type='file']:focus {
  border-color: #b38600; /* amarelo médio */
  outline: none;
  box-shadow: 0 0 8px rgba(179, 134, 0, 0.25);
}

textarea {
  resize: vertical;
  min-height: 100px;
}

h3 {
  font-size: 1.125rem;
  margin-bottom: 1rem;
  color: #222;
}

p {
  margin-bottom: 0.5rem;
  font-size: 0.95rem;
}

strong {
  font-weight: 600;
}

/* Botões */
.button-group {
  display: flex;
  justify-content: space-between;
  gap: 1rem;
}

button {
  flex: 1;
  padding: 0.8rem;
  font-size: 1rem;
  border-radius: 4px;
  font-weight: 500;
  border: none;
  cursor: pointer;
  transition: background-color 0.3s;
}

button.voltar {
  background-color: #ccc;
  color: #333;
}

button.voltar:hover {
  background-color: #bbb;
}

button.proximo {
  background-color: #b38600; /* amarelo médio */
  color: white;
}

button.proximo:hover {
  background-color: #d9a500; /* amarelo mais claro */
}

button.enviar {
  background-color: #8c6f00; /* amarelo escuro */
  color: white;
}

button.enviar:hover {
  background-color: #6b5300;
}

button:disabled {
  background-color: #e0e0e0;
  cursor: not-allowed;
}

/* Responsivo */
@media (max-width: 640px) {
  .button-group {
    flex-direction: column;
  }

  button {
    width: 100%;
  }

  h2 {
    font-size: 1.25rem;
  }
}
</style>
