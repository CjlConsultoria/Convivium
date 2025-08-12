<template lang="pug">
  section.politica-privacidade
    h1 Política de Privacidade

    p Última atualização: 12 de agosto de 2025

    h2 1. Introdução
    p
      | Esta Política de Privacidade explica como coletamos, usamos, armazenamos e protegemos suas
      | informações pessoais quando você utiliza nosso site e serviços. Ao acessar ou usar nossos
      | serviços, você concorda com esta política.

    h2 2. Informações que coletamos
    ul
      li
        strong Informações fornecidas por você:
        |  nome, email, telefone, dados de cadastro, etc.
      li
        strong Informações coletadas automaticamente:
        |  endereço IP, tipo de navegador, páginas acessadas, cookies, dados de localização, entre outros.

    h2 3. Como usamos suas informações
    p
      | Utilizamos seus dados para fornecer, personalizar e melhorar nossos serviços, responder suas
      | solicitações, enviar comunicações importantes, cumprir obrigações legais e proteger nossos
      | direitos.

    h2 4. Compartilhamento de informações
    p
      | Não vendemos seus dados pessoais. Podemos compartilhar informações com parceiros e
      | fornecedores confiáveis para prestação de serviços, desde que respeitem a confidencialidade e
      | a segurança dos dados.

    h2 5. Cookies e tecnologias semelhantes
    p
      | Utilizamos cookies para melhorar sua experiência, analisar o uso do site e personalizar
      | conteúdos. Você pode controlar as preferências de cookies no seu navegador.

    h2 6. Seus direitos
    p
      | Você tem o direito de acessar, corrigir, excluir seus dados pessoais, além de outras garantias
      | previstas na legislação aplicável, como a LGPD. Para exercer seus direitos, entre em contato
      | conosco através do email:
      a(href="mailto:cjlconsultoria.consultor@gmail.com") cjlconsultoria.consultor@gmail.com

    h2 7. Segurança dos dados
    p
      | Implementamos medidas técnicas e administrativas para proteger suas informações contra acesso
      | não autorizado, alteração, divulgação ou destruição.

    h2 8. Retenção de dados
    p
      | Mantemos seus dados apenas pelo tempo necessário para cumprir as finalidades descritas nesta
      | política ou conforme exigido por lei.

    h2 9. Alterações nesta política
    p
      | Podemos atualizar esta política periodicamente. Recomendamos revisar esta página regularmente
      | para se manter informado sobre quaisquer mudanças.

    h2 10. Contato
    p
      | Se tiver dúvidas ou solicitações sobre esta Política de Privacidade, entre em contato conosco
      | pelo email:
      a(href="mailto:cjlconsultoria.consultor@gmail.com") cjlconsultoria.consultor@gmail.com
</template>

<script setup lang="ts">
import { defineEmits } from 'vue'

const emit = defineEmits<{
  (e: 'aceitar'): void
  (e: 'recusar'): void
}>()

function aceitar() {
  emit('aceitar')
}

function recusar() {
  emit('recusar')
}
</script>

<style scoped>
.politica-privacidade {
  max-width: 900px;
  margin: 2rem auto;
  padding: 0 1rem;
  font-family: Arial, sans-serif;
  color: #333;
}

h1 {
  color: #b37f00;
  margin-bottom: 1.5rem;
  font-weight: 700;
}

h2 {
  color: #4b4b0e;
  margin-top: 2rem;
  margin-bottom: 1rem;
  font-weight: 600;
}

p,
ul {
  font-size: 1rem;
  line-height: 1.6;
  margin-bottom: 1rem;
}

ul {
  list-style-type: disc;
  padding-left: 1.5rem;
}

a {
  color: #f5c518;
  text-decoration: underline;
}

a:hover {
  color: #d4af37;
}
</style>
