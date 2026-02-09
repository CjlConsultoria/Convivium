<template lang="pug">
.admin-condominio
  .layout-container
    MenuLateral(itemSelecionado="info-condominio")
    main.area-conteudo
      h2.titulo Informações do Condomínio
      template(v-if="carregando")
        p Carregando...
      template(v-else-if="!info")
        p.info-vazio Nenhuma informação cadastrada para este condomínio.
      template(v-else)
        .info-condominio
          h3(v-if="info.nomeCondominio") {{ info.nomeCondominio }}

          .bloco(v-if="temHorarios")
            h4 Horários das áreas comuns
            ul
              li(v-if="info.horarioPiscina") Piscina: {{ info.horarioPiscina }}
              li(v-if="info.horarioAcademia") Academia: {{ info.horarioAcademia }}
              li(v-if="info.horarioChurrasco") Churrasqueira: {{ info.horarioChurrasco }}
              li(v-if="info.horarioSalaoFestas") Salão de festas: {{ info.horarioSalaoFestas }}
              li(v-if="info.horarioQuadra") Quadra: {{ info.horarioQuadra }}
              li(v-if="info.horarioElevador") Elevador: {{ info.horarioElevador }}
              li(v-if="info.horarioPlayground") Playground: {{ info.horarioPlayground }}

          .bloco(v-if="temContatos")
            h4 Contatos
            ul
              li(v-if="info.telefonePortaria") Portaria: {{ info.telefonePortaria }}
              li(v-if="info.contatoSindico") Síndico: {{ info.contatoSindico }}
              li(v-if="info.contatoAdministradora") Administradora: {{ info.contatoAdministradora }}

          .bloco(v-if="temRegras")
            h4 Regras e políticas
            ul
              li(v-if="info.horarioBarulho") Horário de silêncio: {{ info.horarioBarulho }}
              li(v-if="info.regrasGerais") Regras gerais: {{ info.regrasGerais }}
              li(v-if="info.regrasGaragem") Garagem: {{ info.regrasGaragem }}
              li(v-if="info.regrasPiscina") Piscina: {{ info.regrasPiscina }}
              li(v-if="info.regrasChurrasqueira") Churrasqueira: {{ info.regrasChurrasqueira }}
              li(v-if="info.regrasSalaoFestas") Salão de festas: {{ info.regrasSalaoFestas }}
              li(v-if="info.regrasQuadra") Quadra: {{ info.regrasQuadra }}

          .bloco(v-if="temServicos")
            h4 Serviços e manutenção
            ul
              li(v-if="info.limpeza") Limpeza: {{ info.limpeza }}
              li(v-if="info.manutencao") Manutenção: {{ info.manutencao }}
              li(v-if="info.iluminacao") Iluminação: {{ info.iluminacao }}
              li(v-if="info.agua") Água: {{ info.agua }}
              li(v-if="info.coletaLixo") Coleta de lixo: {{ info.coletaLixo }}

          .bloco(v-if="temFinanceiro")
            h4 Financeiro
            ul
              li(v-if="info.taxaCondominio") Taxa condominial: {{ info.taxaCondominio }}
              li(v-if="info.formaPagamento") Forma de pagamento: {{ info.formaPagamento }}
              li(v-if="info.boletoLink")
                a(:href="info.boletoLink" target="_blank" rel="noopener") Segunda via do boleto

          .bloco(v-if="temReservas")
            h4 Reservas
            ul
              li(v-if="info.politicaReservas") {{ info.politicaReservas }}
              li(v-if="info.reservaLink")
                a(:href="info.reservaLink" target="_blank" rel="noopener") Portal de reservas

          .bloco(v-if="temOutros")
            h4 Outras informações
            ul
              li(v-if="info.estacionamentoInfo") Estacionamento: {{ info.estacionamentoInfo }}
              li(v-if="info.visitantesInfo") Visitantes: {{ info.visitantesInfo }}
              li(v-if="info.animaisInfo") Animais: {{ info.animaisInfo }}
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import MenuLateral from '@/components/Layout/MenuLateral.vue'
import { buscarInfoCondominioPorCodigo, type CondominioInfoDTO } from '@/services/condominioService'

const info = ref<CondominioInfoDTO | null>(null)
const carregando = ref(true)

const temHorarios = computed(() => {
  const i = info.value
  return !!(i?.horarioPiscina || i?.horarioAcademia || i?.horarioChurrasco || i?.horarioSalaoFestas || i?.horarioQuadra || i?.horarioElevador || i?.horarioPlayground)
})
const temContatos = computed(() => !!(info.value?.telefonePortaria || info.value?.contatoSindico || info.value?.contatoAdministradora))
const temRegras = computed(() => {
  const i = info.value
  return !!(i?.horarioBarulho || i?.regrasGerais || i?.regrasGaragem || i?.regrasPiscina || i?.regrasChurrasqueira || i?.regrasSalaoFestas || i?.regrasQuadra)
})
const temServicos = computed(() => !!(info.value?.limpeza || info.value?.manutencao || info.value?.iluminacao || info.value?.agua || info.value?.coletaLixo))
const temFinanceiro = computed(() => !!(info.value?.taxaCondominio || info.value?.formaPagamento || info.value?.boletoLink))
const temReservas = computed(() => !!(info.value?.politicaReservas || info.value?.reservaLink))
const temOutros = computed(() => !!(info.value?.estacionamentoInfo || info.value?.visitantesInfo || info.value?.animaisInfo))

onMounted(async () => {
  try {
    const empresa = JSON.parse(localStorage.getItem('userEmpresa') || '{}')
    const codigo = empresa?.codigoPublico
    if (!codigo) {
      info.value = null
      return
    }
    info.value = await buscarInfoCondominioPorCodigo(codigo)
  } catch (e) {
    console.error(e)
    info.value = null
  } finally {
    carregando.value = false
  }
})
</script>

<style scoped>
.info-condominio {
  max-width: 720px;
}
.info-condominio h3 {
  margin-bottom: 1rem;
  color: var(--color-text);
}
.bloco {
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: var(--color-surface-input);
  border-radius: 8px;
}
.bloco h4 {
  margin: 0 0 0.5rem 0;
  font-size: 1rem;
  color: var(--color-text);
}
.bloco ul {
  margin: 0;
  padding-left: 1.25rem;
}
.bloco li {
  margin: 0.25rem 0;
}
.bloco a {
  color: var(--color-primary);
}
.info-vazio {
  color: #666;
}
</style>
