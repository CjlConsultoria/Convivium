<template lang="pug">
section.presentation-container
  // HERO
  div.hero
    div.hero-content
      h1.hero-title Sistema de Gestão de Condomínios
      p.hero-description
        | Organize, controle e facilite a administração do seu condomínio de forma simples, segura e eficiente, em qualquer dispositivo.
      div.hero-actions
        RouterLink.btn-primary(to="/login") Começar Agora
        RouterLink.btn-secondary(to="/sobre") Saiba Mais
    div.hero-image
      img(src="https://via.placeholder.com/400x300?text=Mobile+Preview" alt="Visual do sistema em mobile")

  // FUNCIONALIDADES PRINCIPAIS
  section.features
    h2.section-title Funcionalidades Principais
    div.features-grid
      div.feature-card
        img(src="https://via.placeholder.com/60?text=Gestao" alt="Gestão")
        h3 feature-title Gestão Completa
        p feature-description Controle total de moradores, áreas e documentos.
      div.feature-card
        img(src="https://via.placeholder.com/60?text=Financeiro" alt="Financeiro")
        h3 feature-title Financeiro
        p feature-description Controle de taxas, cobranças e relatórios financeiros.
      div.feature-card
        img(src="https://via.placeholder.com/60?text=Seguranca" alt="Segurança")
        h3 feature-title Segurança
        p feature-description Monitoramento de acessos e relatórios de ocorrências.
      div.feature-card
        img(src="https://via.placeholder.com/60?text=Mobile" alt="Mobile")
        h3 feature-title Mobile Friendly
        p feature-description Acesse de qualquer lugar, no celular, tablet ou desktop.

  // ESTATÍSTICAS
  section.statistics
    h2.section-title Estatísticas
    div.stats-grid
      div.stat-card
        h3 120+
        p Condominios Gerenciados
      div.stat-card
        h3 5000+
        p Moradores Ativos
      div.stat-card
        h3 10000+
        p Ocorrências Registradas
      div.stat-card
        h3 98%
        p Satisfação dos Usuários

  // DIFERENCIAIS
  section.differentials
    h2.section-title Diferenciais
    div.differentials-grid
      div.diff-card
        img(src="https://via.placeholder.com/60?text=Rapido" alt="Rápido")
        h3 Rápido e Intuitivo
        p Plataforma leve e amigável, sem complicação.
      div.diff-card
        img(src="https://via.placeholder.com/60?text=Suporte" alt="Suporte")
        h3 Suporte Especializado
        p Equipe pronta para te atender rapidamente.
      div.diff-card
        img(src="https://via.placeholder.com/60?text=Atualizacoes" alt="Atualizações")
        h3 Atualizações Constantes
        p Funcionalidades sempre atualizadas e melhoradas.
      div.diff-card
        img(src="https://via.placeholder.com/60?text=Seguranca" alt="Segurança")
        h3 Segurança Total
        p Criptografia e backup diário de dados.

  // DEPOIMENTOS
  section.testimonials
    h2.section-title O que nossos usuários dizem
    div.testimonials-grid
      div.testimonial-card
        p "O sistema facilitou muito a gestão do nosso condomínio. Super intuitivo e confiável!"
        span - João S., Síndico
      div.testimonial-card
        p "Agora consigo controlar todas as taxas e comunicados sem estresse. Recomendo!"
        span - Maria L., Moradora
      div.testimonial-card
        p "Plataforma segura e sempre atualizada, excelente para qualquer condomínio."
        span - Carlos R., Administrador

  // PRÓS E CONTRAS
  section.pros-cons
    h2.section-title Prós e Contras
    div.pros-cons-container
      div.pros
        h3 Pros
        ul
          li Interface intuitiva
          li Mobile-friendly
          li Segurança avançada
          li Relatórios detalhados
          li Suporte rápido
      div.cons
        h3 Contras
        ul
          li Funcionalidades avançadas requerem treinamento
          li Integração com sistemas legados pode precisar de configuração

  // CALL TO ACTION FINAL
  section.call-to-action
    h2.section-title Pronto para transformar a gestão do seu condomínio?
    p.description
      | Experimente agora e descubra como nosso sistema facilita a vida de síndicos, moradores e administradores.
    div.cta-buttons
      RouterLink.btn-primary(to="/login") Começar Agora
      RouterLink.btn-secondary(to="/sobre") Saiba Mais
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

const logado = ref(false)
onMounted(() => {
  logado.value = !!localStorage.getItem('authToken')
})
</script>

<style scoped>
.presentation-container {
  width: 100%;
  color: #000;
  font-family: 'Segoe UI', sans-serif;
  background-color: #fff;
}

/* HERO */
.hero {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  padding: 4rem 2rem;
  gap: 2rem;
}

.hero-content {
  flex: 1 1 400px;
  max-width: 600px;
}

.hero-title {
  font-size: 3rem;
  font-weight: bold;
  color: #f2c200;
  margin-bottom: 1rem;
}

.hero-description {
  font-size: 1.25rem;
  margin-bottom: 2rem;
  line-height: 1.5;
}

.hero-actions {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.hero-image {
  flex: 1 1 300px;
  display: flex;
  justify-content: center;
}

.hero-image img {
  width: 100%;
  max-width: 400px;
  border-radius: 12px;
  box-shadow: 0 0 20px rgba(242, 194, 0, 0.4);
}

/* FEATURES */
.features {
  padding: 4rem 2rem;
  text-align: center;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 2rem;
}

.feature-card {
  background-color: #000;
  color: #fff;
  border-radius: 12px;
  padding: 2rem 1rem;
  box-shadow: 0 0 15px rgba(242, 194, 0, 0.3);
  transition:
    transform 0.3s ease,
    box-shadow 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 0 25px rgba(242, 194, 0, 0.5);
}

.feature-card img {
  width: 60px;
  margin-bottom: 1rem;
}

.feature-card h3 {
  font-size: 1.25rem;
  color: #f2c200;
  margin-bottom: 0.5rem;
}

.feature-card p {
  font-size: 1rem;
  line-height: 1.4;
}

/* ESTATÍSTICAS */
.statistics {
  padding: 4rem 2rem;
  background-color: #121212;
  color: #fff;
  text-align: center;
}

.stats-grid {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 2rem;
}

.stat-card {
  flex: 1 1 150px;
  background-color: #000;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 0 15px rgba(242, 194, 0, 0.3);
}

.stat-card h3 {
  font-size: 2rem;
  color: #f2c200;
  margin-bottom: 0.5rem;
}

/* DIFERENCIAIS */
.differentials {
  padding: 4rem 2rem;
  text-align: center;
}

.differentials-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 2rem;
}

.diff-card {
  background-color: #000;
  color: #fff;
  border-radius: 12px;
  padding: 2rem 1rem;
  box-shadow: 0 0 15px rgba(242, 194, 0, 0.3);
}

.diff-card img {
  width: 60px;
  margin-bottom: 1rem;
}

.diff-card h3 {
  font-size: 1.25rem;
  color: #f2c200;
  margin-bottom: 0.5rem;
}

.diff-card p {
  font-size: 1rem;
  line-height: 1.4;
}

/* DEPOIMENTOS */
.testimonials {
  padding: 4rem 2rem;
  background-color: #121212;
  color: #fff;
  text-align: center;
}

.testimonials-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.testimonial-card {
  background-color: #000;
  padding: 2rem 1rem;
  border-radius: 12px;
  box-shadow: 0 0 15px rgba(242, 194, 0, 0.3);
  font-style: italic;
}

.testimonial-card span {
  display: block;
  margin-top: 1rem;
  font-weight: bold;
  color: #f2c200;
}

/* PRÓS E CONTRAS */
.pros-cons {
  padding: 4rem 2rem;
  text-align: center;
}

.pros-cons-container {
  display: flex;
  flex-wrap: wrap;
  gap: 2rem;
  justify-content: center;
}

.pros,
.cons {
  flex: 1 1 250px;
  background-color: #000;
  color: #fff;
  border-radius: 12px;
  padding: 2rem;
  box-shadow: 0 0 15px rgba(242, 194, 0, 0.3);
}

.pros ul,
.cons ul {
  list-style: disc;
  padding-left: 1.5rem;
  line-height: 1.5;
}

/* CALL TO ACTION */
.call-to-action {
  padding: 4rem 2rem;
  text-align: center;
}

.cta-buttons {
  display: flex;
  justify-content: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.btn-primary,
.btn-secondary {
  padding: 0.75rem 2rem;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
  display: inline-block;
  transition: background-color 0.3s ease;
}

.btn-primary {
  background-color: #f2c200;
  color: #000;
}

.btn-primary:hover {
  background-color: #d9a500;
}

.btn-secondary {
  border: 2px solid #f2c200;
  color: #f2c200;
  background-color: transparent;
}

.btn-secondary:hover {
  background-color: rgba(242, 194, 0, 0.1);
}

/* RESPONSIVO */
@media (max-width: 1024px) {
  .hero {
    flex-direction: column;
    text-align: center;
  }

  .hero-image img {
    max-width: 300px;
  }

  .features-grid,
  .differentials-grid,
  .testimonials-grid {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  }

  .pros-cons-container {
    flex-direction: column;
    align-items: center;
  }
}

@media (max-width: 480px) {
  .hero-title {
    font-size: 2rem;
  }

  .hero-description {
    font-size: 1rem;
  }

  .section-title {
    font-size: 1.5rem;
  }

  .feature-card,
  .diff-card,
  .stat-card,
  .testimonial-card,
  .pros,
  .cons {
    padding: 1.5rem 1rem;
  }
}
</style>
