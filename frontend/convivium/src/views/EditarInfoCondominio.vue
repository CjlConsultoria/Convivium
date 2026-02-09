<template lang="pug">
.admin-condominio
  .layout-container
    MenuLateral(itemSelecionado="editar-info-condominio")
    main.area-conteudo
      h2.titulo Editar Informações do Condomínio
      p.subtitulo(v-if="nomeCondominio") {{ nomeCondominio }}

      template(v-if="carregando")
        p Carregando...
      template(v-else)
        form.form-info-condominio(@submit.prevent="salvar")
          .bloco
            h4 Horários das áreas comuns
            .grid-campos
              .campo
                label Horário Piscina
                input(v-model="form.horarioPiscina" placeholder="Ex: 08h às 18h")
              .campo
                label Horário Academia
                input(v-model="form.horarioAcademia")
              .campo
                label Horário Churrasqueira
                input(v-model="form.horarioChurrasco")
              .campo
                label Horário Salão de Festas
                input(v-model="form.horarioSalaoFestas")
              .campo
                label Horário Quadra
                input(v-model="form.horarioQuadra")
              .campo
                label Horário Elevador
                input(v-model="form.horarioElevador")
              .campo
                label Horário Playground
                input(v-model="form.horarioPlayground")

          .bloco
            h4 Contatos
            .grid-campos
              .campo
                label Telefone Portaria
                input(v-model="form.telefonePortaria" placeholder="(11) 1234-5678")
              .campo
                label Contato Síndico
                input(v-model="form.contatoSindico")
              .campo
                label Contato Administradora
                input(v-model="form.contatoAdministradora")

          .bloco
            h4 Regras e políticas
            .grid-campos
              .campo
                label Horário de silêncio
                input(v-model="form.horarioBarulho" placeholder="Ex: 22h às 7h")
              .campo.campo-largo
                label Regras gerais
                textarea(v-model="form.regrasGerais" rows="2")
              .campo
                label Regras Garagem
                input(v-model="form.regrasGaragem")
              .campo
                label Regras Piscina
                input(v-model="form.regrasPiscina")
              .campo
                label Regras Churrasqueira
                input(v-model="form.regrasChurrasqueira")
              .campo
                label Regras Salão de Festas
                input(v-model="form.regrasSalaoFestas")
              .campo
                label Regras Quadra
                input(v-model="form.regrasQuadra")

          .bloco
            h4 Serviços e manutenção
            .grid-campos
              .campo
                label Limpeza
                input(v-model="form.limpeza" placeholder="Ex: Diariamente 08h às 17h")
              .campo
                label Manutenção
                input(v-model="form.manutencao")
              .campo
                label Iluminação
                input(v-model="form.iluminacao")
              .campo
                label Água
                input(v-model="form.agua")
              .campo
                label Coleta de lixo
                input(v-model="form.coletaLixo")

          .bloco
            h4 Financeiro
            .grid-campos
              .campo
                label Taxa condominial
                input(v-model="form.taxaCondominio" placeholder="Ex: R$ 450,00")
              .campo
                label Forma de pagamento
                input(v-model="form.formaPagamento" placeholder="Ex: Pix, boleto")
              .campo.campo-largo
                label Link 2ª via boleto
                input(v-model="form.boletoLink" type="url" placeholder="https://...")

          .bloco
            h4 Reservas
            .grid-campos
              .campo.campo-largo
                label Política de reservas
                input(v-model="form.politicaReservas")
              .campo.campo-largo
                label Link portal de reservas
                input(v-model="form.reservaLink" type="url")

          .bloco
            h4 Outras informações
            .grid-campos
              .campo
                label Estacionamento
                input(v-model="form.estacionamentoInfo")
              .campo
                label Visitantes
                input(v-model="form.visitantesInfo")
              .campo
                label Animais
                input(v-model="form.animaisInfo")

          .acoes-form
            button.btn-primary(type="submit" :disabled="salvando") {{ salvando ? 'Salvando...' : 'Salvar' }}
            RouterLink.btn-outline(to="/informacoes-condominio") Ver informações (somente leitura)
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { toast } from 'vue3-toastify'
import MenuLateral from '@/components/Layout/MenuLateral.vue'
import {
  buscarInfoCondominioPorEmpresaId,
  atualizarInfoCondominio,
  type CondominioInfoDTO,
} from '@/services/condominioService'

const router = useRouter()
const carregando = ref(true)
const salvando = ref(false)
const nomeCondominio = ref('')
const empresaId = ref<number | null>(null)

const form = reactive<Record<string, string>>({
  horarioPiscina: '',
  horarioAcademia: '',
  horarioChurrasco: '',
  horarioSalaoFestas: '',
  horarioQuadra: '',
  horarioElevador: '',
  horarioPlayground: '',
  telefonePortaria: '',
  contatoSindico: '',
  contatoAdministradora: '',
  horarioBarulho: '',
  regrasGerais: '',
  regrasGaragem: '',
  regrasPiscina: '',
  regrasChurrasqueira: '',
  regrasSalaoFestas: '',
  regrasQuadra: '',
  limpeza: '',
  manutencao: '',
  iluminacao: '',
  agua: '',
  coletaLixo: '',
  boletoLink: '',
  formaPagamento: '',
  taxaCondominio: '',
  reservaLink: '',
  politicaReservas: '',
  estacionamentoInfo: '',
  visitantesInfo: '',
  animaisInfo: '',
})

function preencherForm(dto: CondominioInfoDTO | null) {
  if (!dto) return
  nomeCondominio.value = dto.nomeCondominio || ''
  const keys = Object.keys(form) as (keyof typeof form)[]
  keys.forEach((k) => {
    if (dto[k as keyof CondominioInfoDTO] != null) {
      form[k] = String(dto[k as keyof CondominioInfoDTO])
    }
  })
}

async function carregar() {
  const empresa = JSON.parse(localStorage.getItem('userEmpresa') || '{}')
  const id = empresa?.id
  if (!id) {
    toast.error('Condomínio não identificado.')
    return
  }
  empresaId.value = id
  try {
    const info = await buscarInfoCondominioPorEmpresaId(id)
    preencherForm(info)
  } catch (e) {
    console.error(e)
    toast.error('Erro ao carregar informações.')
  } finally {
    carregando.value = false
  }
}

async function salvar() {
  if (empresaId.value == null) return
  salvando.value = true
  try {
    await atualizarInfoCondominio(empresaId.value, form)
    toast.success('Informações salvas com sucesso.')
  } catch (e) {
    console.error(e)
    toast.error('Erro ao salvar.')
  } finally {
    salvando.value = false
  }
}

onMounted(carregar)
</script>

<style scoped>
.form-info-condominio {
  max-width: 900px;
}
.subtitulo {
  margin: -0.5rem 0 1rem 0;
  color: #666;
  font-size: 0.95rem;
}
.bloco {
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: var(--surface, #f8f9fa);
  border-radius: 8px;
}
.bloco h4 {
  margin: 0 0 0.75rem 0;
  font-size: 1rem;
  color: var(--color-heading, #333);
}
.grid-campos {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 0.75rem 1rem;
}
.campo {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}
.campo-largo {
  grid-column: 1 / -1;
}
.campo label {
  font-size: 0.85rem;
  color: #555;
}
.campo input,
.campo textarea {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
}
.acoes-form {
  margin-top: 1.5rem;
  display: flex;
  gap: 1rem;
  align-items: center;
}
</style>
