<template lang="pug">
  .modal(v-if="showModal")
    .modal-content
      button.close-btn(@click="closeModal") ×
      h2 Redefinir Senha
      p.error(v-if="error") {{ error }}
      p.success(v-if="success") {{ success }}
      form(@submit.prevent="handleSubmit")
        .form-group
          label(for="newPassword") Nova Senha
          input(type="password", v-model="newPassword", required=true, placeholder="Digite sua nova senha")
        .form-group
          label(for="confirmPassword") Confirmar Senha
          input(type="password", v-model="confirmPassword", required=true, placeholder="Confirme sua senha")
        button(type="submit") Redefinir Senha
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { toast } from 'vue3-toastify'
import { resetPassword } from '@/services/authService'
import { useLoadingStore } from '@/stores/loadingStore'

const route = useRoute()
const router = useRouter()
const store = useLoadingStore()

// Reactive refs
const newPassword = ref('')
const confirmPassword = ref('')
const error = ref('')
const success = ref('')
const token = ref('')
const showModal = ref(false)

onMounted(() => {
  const queryToken = route.query.token
  if (typeof queryToken === 'string') {
    token.value = queryToken
    showModal.value = true
  } else {
    error.value = 'Token inválido ou não encontrado.'
  }
})

const handleSubmit = async () => {
  if (newPassword.value !== confirmPassword.value) {
    error.value = 'As senhas não coincidem.'
    return
  }

  try {
    store.startLoading()
    const user = JSON.parse(localStorage.getItem('user') || '{}')
    await resetPassword(token.value, newPassword.value, user.cpf)
    toast.success('Senha redefinida com sucesso!')
    store.stopLoading()
    setTimeout(() => {
      router.push('/login')
    }, 3000)
  } catch (err) {
    store.stopLoading()
    error.value = 'Erro ao redefinir a senha. Tente novamente mais tarde.'
  }
}

const closeModal = () => {
  showModal.value = false
  router.push('/login') // ou apenas router.replace('/') se preferir não voltar
}
</script>

<style scoped>
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
}

.modal-content {
  position: relative;
  background: #ffffff;
  padding: 30px 25px;
  border-radius: 10px;
  max-width: 420px;
  width: 100%;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.2);
}

.close-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  background: transparent;
  border: none;
  font-size: 22px;
  color: #888;
  cursor: pointer;
}

.close-btn:hover {
  color: #000;
}

h2 {
  text-align: center;
  margin-bottom: 25px;
  color: #b38600;
}

.form-group {
  margin-bottom: 20px;
  text-align: left;
}

label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
}

input {
  width: 100%;
  padding: 10px 12px;
  font-size: 15px;
  border-radius: 6px;
  border: 1px solid #ccc;
  transition: border-color 0.2s;
}

input:focus {
  outline: none;
  border-color: #b38600;
}

button[type='submit'] {
  width: 100%;
  padding: 12px;
  background-color: #b38600;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  margin-top: 10px;
}

button[type='submit']:hover {
  background-color: #c2a757;
}

.error {
  color: #d32f2f;
  font-size: 14px;
  margin-bottom: 15px;
}

.success {
  color: #388e3c;
  font-size: 14px;
  margin-bottom: 15px;
}
</style>
