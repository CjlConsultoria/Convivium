<!-- src/components/Chat.vue -->
<script setup lang="ts">
import { ref, nextTick, watch } from 'vue'
import { enviarMensagem, type ChatRequest } from '@/services/chatService'

// Tipo das mensagens
type Mensagem = {
  de: 'usuario' | 'sistema'
  texto: string
  temporaria?: boolean
}
const chatMessagesRef = ref<HTMLElement | null>(null)

const aberto = ref(false)
const mensagens = ref<Mensagem[]>([])
const novaMensagem = ref('')
const carregando = ref(false)

const authToken = localStorage.getItem('authToken') || ''
const usuarioId = Number(localStorage.getItem('userId') || 0)
const condominioId = JSON.parse(localStorage.getItem('userEmpresa') || '{}').id || 0

const toggleChat = () => {
  aberto.value = !aberto.value
}

const enviar = async () => {
  const texto = novaMensagem.value.trim()
  if (!texto) return

  // Adiciona mensagem do usuário imediatamente
  mensagens.value.push({ de: 'usuario', texto })
  novaMensagem.value = ''

  // Adiciona mensagem temporária "pensando"
  mensagens.value.push({ de: 'sistema', texto: '', temporaria: true })
  carregando.value = true

  try {
    const request: ChatRequest = { message: texto, condominioId, usuarioId }
    const response = await enviarMensagem(request, authToken)

    // Substitui a mensagem temporária pela resposta real
    const indexTemp = mensagens.value.findIndex((m) => m.temporaria)
    if (indexTemp !== -1) {
      mensagens.value[indexTemp] = { de: 'sistema', texto: response.message }
    } else {
      mensagens.value.push({ de: 'sistema', texto: response.message })
    }
  } catch (error) {
    const indexTemp = mensagens.value.findIndex((m) => m.temporaria)
    if (indexTemp !== -1) {
      mensagens.value[indexTemp] = { de: 'sistema', texto: 'Erro ao enviar mensagem.' }
    } else {
      mensagens.value.push({ de: 'sistema', texto: 'Erro ao enviar mensagem.' })
    }
    console.error(error)
  } finally {
    carregando.value = false
  }
}

// Função para rolar para a última mensagem
const scrollToBottom = () => {
  nextTick(() => {
    if (chatMessagesRef.value) {
      chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight
    }
  })
}

// Sempre que mensagens mudarem, rola para baixo
watch(
  mensagens,
  () => {
    scrollToBottom()
  },
  { deep: true },
)
</script>

<template lang="pug">
.chat-flutuante(:class="{ aberto: aberto }")
  .chat-header(@click="toggleChat")
    | Chat
    span(v-if="mensagens.length") ({{ mensagens.length }})
  .chat-body(v-show="aberto")
    .chat-messages(ref="chatMessagesRef")
      div.chat-message(
        v-for="(msg, idx) in mensagens" 
        :key="idx" 
        :class="[msg.de, msg.temporaria ? 'temporaria' : '']"
      )
        span {{ msg.texto }}

    .chat-input
      input(
        type="text"
        v-model="novaMensagem"
        @keyup.enter="enviar"
        placeholder="Digite sua mensagem..."
      )
      button(@click="enviar") Enviar
</template>

<style scoped>
.chat-flutuante {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 300px;
  max-width: 90%;
  z-index: 9999;
  font-family: sans-serif;
}

.chat-header {
  background-color: var(--color-primary);
  color: #000;
  padding: 0.5rem 0.8rem;
  border-radius: 8px 8px 0 0;
  cursor: pointer;
  font-weight: bold;
}

.chat-body {
  background-color: #1c1c1c;
  border: 1px solid #d4af37;
  border-top: none;
  border-radius: 0 0 8px 8px;
  display: flex;
  flex-direction: column;
  max-height: 400px;
  overflow: hidden;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 0.5rem;
}

.chat-message {
  margin-bottom: 0.5rem;
  padding: 0.4rem 0.6rem;
  border-radius: 4px;
  max-width: 80%;
}

.chat-message.usuario {
  align-self: flex-end;
  background-color: var(--color-primary);
  color: #000;
}

.chat-message.sistema {
  align-self: flex-start;
  background-color: #333;
  color: #fff;
}

/* Animação "pensando" */
.chat-message.temporaria::after {
  content: '';
  display: inline-block;
  width: 1em;
  animation: blink 1s steps(3, end) infinite;
}

@keyframes blink {
  0%,
  20% {
    content: '.';
  }
  40% {
    content: '..';
  }
  60% {
    content: '...';
  }
  80%,
  100% {
    content: '';
  }
}

.chat-input {
  display: flex;
  gap: 0.5rem;
  padding: 0.5rem;
}

.chat-input input {
  flex: 1;
  padding: 0.4rem 0.6rem;
  border-radius: 4px;
  border: 1px solid #d4af37;
  background-color: #222;
  color: #fff;
}

.chat-input button {
  padding: 0.4rem 1rem;
  border: none;
  border-radius: 4px;
  background-color: #d4af37;
  color: #000;
  cursor: pointer;
  font-weight: bold;
}

.chat-input button:hover {
  background-color: #e5c94f;
}
</style>
