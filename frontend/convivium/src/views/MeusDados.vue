<template lang="pug">
  .meus-dados
    h2.titulo Meus Dados

    form(@submit.prevent="salvarDados")
      .secao
        h3.secao-titulo Informações Pessoais
        .linha
          .campo
            label(for="username") Nome
            input(type="text" id="username" v-model="user.username" readonly)
          .campo
            label(for="sobrenome") Sobrenome
            input(type="text" id="sobrenome" v-model="user.sobrenome" readonly)
        .linha
          .campo
            label(for="cpf") CPF
            input(type="text" id="cpf" v-model="user.cpf" readonly)
          .campo
            label(for="email") E-mail
            input(type="email" id="email" v-model="user.email" readonly)
        .linha
          .campo-completo
            label(for="telefone") Telefone
            input(type="text" id="telefone" v-model="user.telefone")

      .secao
        h3.secao-titulo Endereço
        .linha
          .campo
            label(for="cep") CEP
            input(type="text" id="cep" v-model="user.cep" readonly)
          .campo
            label(for="logradouro") Logradouro
            input(type="text" id="logradouro" v-model="user.logradouro" readonly)
        .linha
          .campo
            label(for="numero") Número
            input(type="text" id="numero" v-model="user.numero" readonly)
          .campo
            label(for="complemento") Complemento
            input(type="text" id="complemento" v-model="user.complemento" readonly)
        .linha
          .campo
            label(for="bairro") Bairro
            input(type="text" id="bairro" v-model="user.bairro" readonly)
          .campo
            label(for="cidade") Cidade
            input(type="text" id="cidade" v-model="user.cidade" readonly)
          .campo
            label(for="estado") Estado
            input(type="text" id="estado" v-model="user.estado" readonly)

      .secao
        h3.secao-titulo Empresa
        .linha
          .campo
            label Nome da Empresa
            input(type="text" v-model="user.empresa.name" readonly)
          .campo
            label CNPJ
            input(type="text" v-model="user.empresa.cnpj" readonly)
          .campo
            label Código Público
            input(type="text" v-model="user.empresa.codigoPublico" readonly)

      .secao
        h3.secao-titulo Perfil e Acesso
        .linha
          .campo
            label Perfil
            input(type="text" v-model="user.role.name" readonly)
          .campo
            label Tipo
            input(type="text" v-model="user.tipo.name" readonly)
          .campo
            label Status
            input(type="text" v-model="user.status" readonly)
        .linha
          .campo
            label Gênero
            input(type="text" v-model="user.genero" readonly)
          .campo
            label Bloco
            input(type="text" v-model="user.bloco" readonly)
          .campo
            label Apartamento
            input(type="text" v-model="user.apartamento" readonly)
        .linha
          .campo
            label Vaga Carro
            input(type="text" v-model="user.vagaCarro" readonly)
          .campo
            label Vaga Moto
            input(type="text" v-model="user.vagaMoto" readonly)

      button(type="submit") Salvar Dados
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { toast } from 'vue3-toastify'
import { updateUserData } from '@/services/authService'

// Carregar usuário do localStorage
const storedUser = localStorage.getItem('user')
const user = ref(JSON.parse(storedUser || '{}'))

const salvarDados = async () => {
  try {
    await updateUserData(
      user.value.id,
      user.value.username,
      user.value.sobrenome,
      user.value.email,
      user.value.cpf,
      user.value.telefone,
      user.value.cep,
      user.value.logradouro,
      user.value.numero,
      user.value.complemento,
      user.value.bairro,
      user.value.cidade,
      user.value.estado,
      user.value.role.id,
      user.value.tipo.id,
      user.value.status,
      user.value.genero,
      user.value.bloco,
      user.value.apartamento,
      user.value.vagaCarro,
      user.value.vagaMoto,
    )
    toast.success('Dados atualizados com sucesso!')
  } catch (error) {
    toast.error('Erro ao atualizar dados.')
  }
}
</script>

<style scoped>
.meus-dados {
  padding: 2rem;
  max-width: 900px;
  margin: 0 auto;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.titulo {
  text-align: center;
  color: #222; /* preto mais forte */
  margin-bottom: 2rem;
  font-size: 2rem;
  font-weight: 600;
}

.secao {
  background-color: #fff;
  padding: 1.5rem 2rem;
  margin-bottom: 2rem;
  border-radius: 8px;
  border-left: 5px solid #b38600; /* amarelo médio */
  box-shadow: 0 2px 6px rgba(179, 134, 0, 0.1); /* sombra amarela suave */
}

.secao-titulo {
  font-size: 1.5rem;
  color: #222; /* preto forte */
  margin-bottom: 1.5rem;
  font-weight: 600;
}

.linha {
  display: flex;
  gap: 20px;
  margin-bottom: 1rem;
  flex-wrap: wrap;
}

.campo {
  flex: 1 1 250px;
  display: flex;
  flex-direction: column;
}

.campo-completo {
  flex: 1 1 100%;
  display: flex;
  flex-direction: column;
}

label {
  font-weight: 600;
  margin-bottom: 0.4rem;
  color: #665500; /* amarelo escuro para o texto */
}

input[type='text'],
input[type='email'] {
  padding: 10px;
  border-radius: 6px;
  border: 1px solid #d9c877; /* amarelo claro */
  font-size: 15px;
  transition: border-color 0.2s ease;
  color: #222; /* texto escuro dentro dos inputs */
  background-color: #fff;
}

input[readonly] {
  background-color: #f0e8b0; /* amarelo muito claro */
  cursor: not-allowed;
}

input[type='text']:focus,
input[type='email']:focus {
  border-color: #b38600; /* amarelo médio */
  outline: none;
  box-shadow: 0 0 6px rgba(179, 134, 0, 0.3);
}

button {
  display: block;
  margin: 2rem auto 0;
  padding: 12px 40px;
  background-color: #b38600; /* amarelo médio */
  color: white;
  font-weight: 700;
  font-size: 18px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #d9a500; /* amarelo claro mais vibrante */
}

@media (max-width: 720px) {
  .linha {
    flex-direction: column;
  }
  .campo {
    flex: 1 1 100%;
  }
}
</style>
