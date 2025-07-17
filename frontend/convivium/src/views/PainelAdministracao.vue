<template lang="pug">
.layout-admin
  MenuLateral(:itemSelecionado="''")

  main.painel-container
    h1 Painel de Administração
    p.descricao
      | Bem-vindo ao painel de controle do condomínio. Escolha uma das opções abaixo para gerenciar as funcionalidades administrativas.

    .opcoes-grid
      // Opções ativas
      RouterLink.card(to="/moradores")
        span.emoji 🧍
        h3 Moradores
        p Gerencie o cadastro e os dados dos moradores do condomínio.

      RouterLink.card(to="/reclamacoes")
        span.emoji 📣
        h3 Reclamações
        p Acompanhe e resolva as reclamações dos moradores.

      // Opção Gestor de Licenças só para ADMIN
      template(v-if="perfilUsuario === 'ADMIN'")
        RouterLink.card(to="/licenca")
          span.emoji 🔑
          h3 Gestor de Licenças
          p Gerencie licenças de acesso e permissões do sistema.

      RouterLink.card(to="/relatorio")
        span.emoji 📊
        h3 Relatórios
        p Visualize indicadores e dados consolidados do condomínio.

      // Opções desativadas com cadeado
      .card.card-desativado
        span.cadeado 🔒
        span.emoji 💰
        h3 Financeiro
        p Controle financeiro, taxas e despesas do condomínio.

      .card.card-desativado
        span.cadeado 🔒
        span.emoji 📅
        h3 Reservas Áreas Comuns
        p Agendamento das áreas de lazer e espaços comuns.

      .card.card-desativado
        span.cadeado 🔒
        span.emoji 📢
        h3 Comunicação
        p Envio de avisos e comunicados para os moradores.

      .card.card-desativado
        span.cadeado 🔒
        span.emoji 📁
        h3 Documentos
        p Armazenamento de documentos do condomínio.

      .card.card-desativado
        span.cadeado 🔒
        span.emoji 🚪
        h3 Visitantes
        p Controle e registro de visitantes no condomínio.

      .card.card-desativado
        span.cadeado 🔒
        span.emoji 🔧
        h3 Manutenção
        p Solicitações e acompanhamento de manutenção.


</template>

<script setup lang="ts">
import MenuLateral from '@/components/Layout/MenuLateral.vue'
import { ref, onMounted } from 'vue'

const perfilUsuario = ref('PUBLICO')

onMounted(() => {
  perfilUsuario.value = (localStorage.getItem('userPerfil') || 'PUBLICO').toUpperCase().trim()
})

function onSelecionar(valor: string) {
  // Lógica opcional se quiser atualizar rota ou estado
}
</script>

<style scoped>
.layout-admin {
  display: flex;
  min-height: 100vh;
}

.painel-container {
  flex: 1;
  padding: 2rem;
  max-width: 1000px;
  margin: 0 auto;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

h1 {
  font-size: 2rem;
  margin-bottom: 1rem;
  color: #b38600;
}

.descricao {
  margin-bottom: 2rem;
  font-size: 1.1rem;
  color: #555;
}

.opcoes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
}

.card {
  background-color: #fffbe6;
  border: 1px solid #e1d78b;
  border-radius: 12px;
  padding: 1.5rem;
  text-decoration: none;
  color: inherit;
  box-shadow: 2px 2px 8px rgba(0, 0, 0, 0.05);
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 4px 4px 12px rgba(0, 0, 0, 0.1);
}

.card .emoji {
  font-size: 2rem;
  margin-bottom: 0.5rem;
  display: inline-block;
}

.card h3 {
  margin: 0.5rem 0;
  font-size: 1.2rem;
  color: #b38600;
}

.card p {
  font-size: 0.95rem;
  color: #555;
}

.card-desativado {
  position: relative;
  pointer-events: none;
  opacity: 0.6;
  cursor: default;
}

.card-desativado .cadeado {
  position: absolute;
  top: 12px;
  right: 12px;
  font-size: 1.2rem;
  color: #b38600;
}

.card-desativado .emoji {
  opacity: 0.6;
}

.card-desativado h3,
.card-desativado p {
  opacity: 0.6;
}
</style>
