<template lang="pug">
.modal-backdrop(@click.self="close" role="dialog" aria-modal="true")
  .modal-content(tabindex="-1" ref="modalContent")
    header.modal-header
      h3 Detalhes do Morador
      .btn-group
        button.btn-edit(@click="habilitarEdicao") Editar
        button.btn-close(aria-label="Fechar modal" @click="close") &times;

    section.modal-body(v-if="morador")
      h4.secao-form Dados Pessoais
      p
        strong Nome:
        span(v-if="!modoEdicao") {{ morador.username }} {{ morador.sobrenome }}
        input(v-else v-model="morador.username")
        input(v-else v-model="morador.sobrenome")
      p
        strong E-mail:
        span(v-if="!modoEdicao") {{ morador.email }}
        input(v-else type="email" v-model="morador.email")
      p
        strong CPF:
        |  {{ formatCpf(morador.cpf) }}
      p
        strong Telefone:
        span(v-if="!modoEdicao") {{ morador.telefone }}
        input(v-else v-model="morador.telefone")
      p
        strong Gênero:
        span(v-if="!modoEdicao") {{ morador.genero }}
        select(v-else v-model="morador.genero")
          option(value="Masculino") Masculino
          option(value="Feminino") Feminino
          option(value="Outro") Outro

      h4.secao-form Endereço
      p
        strong CEP:
        span(v-if="!modoEdicao") {{ morador.cep }}
        input(v-else v-model="morador.cep")
      p
        strong Logradouro:
        span(v-if="!modoEdicao") {{ morador.logradouro }}, {{ morador.numero }}
        input(v-else v-model="morador.logradouro")
        input(v-else v-model="morador.numero")
      p
        strong Complemento:
        span(v-if="!modoEdicao") {{ morador.complemento }}
        input(v-else v-model="morador.complemento")
      p
        strong Bairro:
        span(v-if="!modoEdicao") {{ morador.bairro }}
        input(v-else v-model="morador.bairro")
      p
        strong Cidade:
        span(v-if="!modoEdicao") {{ morador.cidade }}
        input(v-else v-model="morador.cidade")
      p
        strong Estado:
        span(v-if="!modoEdicao") {{ morador.estado }}
        input(v-else v-model="morador.estado")

      h4.secao-form Informações do Condomínio
      p
        strong Bloco:
        span(v-if="!modoEdicao") {{ morador.bloco }}
        input(v-else v-model="morador.bloco")
      p
        strong Apartamento:
        span(v-if="!modoEdicao") {{ morador.apartamento }}
        input(v-else v-model="morador.apartamento")
      p
        strong Vaga Carro:
        span(v-if="!modoEdicao") {{ morador.vagaCarro }}
        input(v-else v-model="morador.vagaCarro")
      p
        strong Vaga Moto:
        span(v-if="!modoEdicao") {{ morador.vagaMoto }}
        input(v-else v-model="morador.vagaMoto")

      h4.secao-form Empresa
      p
        strong Empresa:
        |  {{ morador.empresa?.name || '' }}
      p
        strong CNPJ da Empresa:
        |  {{ morador.empresa?.cnpj || '' }}
      p
        strong Endereço da Empresa:
        |  {{ morador.empresa?.logradouro }}, {{ morador.empresa?.numero }} - {{ morador.empresa?.bairro }}, {{ morador.empresa?.cidade }} - {{ morador.empresa?.estado }}

      h4.secao-form Status e Perfil
      .form-grid
        div
          label Tipo
          span(v-if="!modoEdicao") {{ morador.tipo?.name || morador.tipo }}
          select(v-else v-model="morador.tipo" required)
            option(:value="null" disabled) Selecione o tipo
            option(v-for="tipo in tiposUsuario" :key="tipo.id" :value="tipo") {{ tipo.name }}

        div
          label Perfil
          span(v-if="!modoEdicao") {{ morador.role?.name || morador.role }}
          select(v-else v-model="morador.role" required)
            option(:value="null" disabled) Selecione o perfil
            option(v-for="role in rolesUsuario" :key="role.id" :value="role") {{ role.name }}

        div
          label Alerta
          span(v-if="!modoEdicao") {{ morador.alerta ? 'Sim' : 'Não' }}
          select(v-else v-model="morador.alerta")
            option(:value="true") Sim
            option(:value="false") Não

        div
          label Ativo
          span(v-if="!modoEdicao") {{ morador.ativo ? 'Sim' : 'Não' }}
          select(v-else v-model="morador.ativo")
            option(:value="true") Sim
            option(:value="false") Não

        div
          label Data de Criação
          p {{ new Date(morador.dataCriacao).toLocaleString() }}

        div
          label Última Modificação
          p {{ new Date(morador.dataAtualizacao).toLocaleString() }}


    footer.modal-footer
      button.btn-outline.btn-principal(@click="close") Fechar
      button.btn-outline.btn-cancelar(v-if="modoEdicao" @click="cancelarEdicao") Cancelar
      button.btn-outline.btn-principal(v-if="modoEdicao" @click="salvarEdicao") Salvar
</template>

<script setup lang="ts">
import { defineEmits, toRefs, onMounted, ref } from 'vue'

const emit = defineEmits(['close'])
const props = defineProps<{
  morador: any
  tiposUsuario: any[]
  rolesUsuario: any[]
}>()

const { morador } = toRefs(props)

const modoEdicao = ref(false)
const backup = ref<any>(null)

const close = () => emit('close')

const habilitarEdicao = () => {
  backup.value = JSON.parse(JSON.stringify(morador.value)) // cria cópia de segurança
  modoEdicao.value = true
}

const cancelarEdicao = () => {
  Object.assign(morador.value, backup.value) // restaura original
  modoEdicao.value = false
}

const salvarEdicao = () => {
  // Aqui você pode emitir um evento com os dados editados
  console.log('Salvar edição:', morador.value)
  modoEdicao.value = false
}

onMounted(() => {})

const formatCpf = (cpf: string) => {
  return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4')
}
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
  max-width: 680px;
  width: 95%;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.25);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  outline: none;
  animation: slideIn 0.35s ease forwards;
  padding: 0;
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

.modal-header {
  padding: 1.25rem 2rem;
  background: #b38600;
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
  overflow-y: auto;
  max-height: 70vh;
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

.btn-outline.btn-principal {
  background-color: transparent;
  border: 2px solid #b38600;
  color: #b38600;
  padding: 0.8rem 1.6rem;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  transition:
    background-color 0.25s ease,
    box-shadow 0.25s ease;
}

.btn-outline.btn-principal:hover,
.btn-outline.btn-principal:focus {
  background-color: #b38600;
  color: white;
  box-shadow: 0 0 10px rgba(66, 185, 131, 0.5);
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

h4.secao-form {
  margin-top: 1.5rem;
  font-size: 1.2rem;
  color: #b38600;
  border-bottom: 1px solid #ddd;
  padding-bottom: 0.4rem;
  margin-bottom: 0.8rem;
}

.btn-group {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.btn-edit {
  background-color: transparent;
  border: 2px solid #f1c40f;
  color: #f1c40f;
  font-weight: bold;
  padding: 0.4rem 1rem;
  border-radius: 10px;
  cursor: pointer;
  transition: 0.3s;
}

.btn-edit:hover {
  background-color: #f1c40f;
  color: white;
}

.btn-secundario {
  border-color: #999;
  color: #333;
}

.btn-secundario:hover {
  background-color: #999;
  color: white;
}
input {
  padding: 0.6rem 1rem;
  font-size: 1rem;
  border: 2px solid #ccc;
  border-radius: 10px;
  width: 100%;
  margin-top: 0.3rem;
  margin-bottom: 0.8rem;
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease;
}

input:focus {
  outline: none;
  border-color: #b38600;
  box-shadow: 0 0 5px rgba(66, 185, 131, 0.3);
}

/* Botão Cancelar */
.btn-outline.btn-cancelar {
  background-color: transparent;
  border: 2px solid #e74c3c;
  color: #e74c3c;
  padding: 0.8rem 1.6rem;
  font-weight: 600;
  border-radius: 10px;
  cursor: pointer;
  transition:
    background-color 0.25s ease,
    box-shadow 0.25s ease;
}

.btn-outline.btn-cancelar:hover,
.btn-outline.btn-cancelar:focus {
  background-color: #e74c3c;
  color: white;
  box-shadow: 0 0 10px rgba(231, 76, 60, 0.5);
}

select {
  padding: 0.6rem 1rem;
  font-size: 1rem;
  border: 2px solid #ccc;
  border-radius: 10px;
  width: 100%;
  margin-top: 0.3rem;
  margin-bottom: 0.8rem;
  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease;
  background-color: white;
  color: #333;
  appearance: none;
  background-image: url('data:image/svg+xml;charset=US-ASCII,<svg fill="gray" height="16" viewBox="0 0 20 20" width="16" xmlns="http://www.w3.org/2000/svg"><path d="M7 7l5 5 5-5z"/></svg>');
  background-repeat: no-repeat;
  background-position: right 0.75rem center;
  background-size: 1rem;
}

select:focus {
  outline: none;
  border-color: #b38600;
  box-shadow: 0 0 5px rgba(66, 185, 131, 0.3);
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 1.25rem;
  margin-top: 1rem;
}

label {
  display: block;
  font-weight: 600;
  margin-bottom: 0.25rem;
  color: #333;
}
</style>
