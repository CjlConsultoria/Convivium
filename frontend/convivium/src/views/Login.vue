<template lang="pug">
  section.container
    div.login-container
      h2 Login
      form(@submit.prevent="handleLogin")
        div.input-group
          label(for="loginCpf") CPF
          input(
            type="text"
            id="loginCpf"
            v-model="loginCpf"
            maxlength="14"
            required
            autocomplete="off"
          )
          span.error-message(v-if="cpfError") {{ cpfError }}

        div.input-group.password-group
          label(for="loginPassword") Senha
          div.password-wrapper
            input(
              :type="showPassword ? 'text' : 'password'"
              id="loginPassword"
              v-model="loginPassword"
              required
            )
            span.toggle-icon(@click="showPassword = !showPassword")
              i(:class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'")

        button(type="submit") Entrar
      span.forgot-password(@click="openForgotPasswordModal") Esqueci a senha

    ForgotPasswordModal(ref="forgotPasswordModal")
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { login as loginApi, fetchUserData as fetchUserData } from '@/services/authService'
import { toast } from 'vue3-toastify'
import ForgotPasswordModal from '@/views/components/ForgotPasswordModal.vue'
import { useLoadingStore } from '@/stores/loadingStore'
const store = useLoadingStore()

const loginCpf = ref('')
const loginPassword = ref('')
const showPassword = ref(false)
const cpfError = ref('')
const router = useRouter()
const forgotPasswordModal = ref<InstanceType<typeof ForgotPasswordModal> | null>(null)

onMounted(() => {
  const msg = localStorage.getItem('loginMessage')
  if (msg) {
    toast.error(msg)
    localStorage.removeItem('loginMessage')
  }
})

const openForgotPasswordModal = () => {
  if (forgotPasswordModal.value) {
    forgotPasswordModal.value.openModal() // Acessando o método openModal do componente filho
  }
}
// Função para formatar CPF
function formatCPF(value: string): string {
  return value
    .replace(/\D/g, '')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d{1,2})$/, '$1-$2')
}

// Watcher para validar CPF
watch(loginCpf, (val) => {
  loginCpf.value = formatCPF(val)
  cpfError.value = !isValidCPF(val) ? 'CPF inválido' : '' // Exibir erro caso CPF seja inválido
})

function isValidCPF(cpf: string): boolean {
  cpf = cpf.replace(/[^\d]+/g, '')
  // if (cpf.length !== 11 || /^(\d)\1+$/.test(cpf)) return false

  let sum = 0
  for (let i = 0; i < 9; i++) sum += parseInt(cpf.charAt(i)) * (10 - i)
  let rev = 11 - (sum % 11)
  if (rev === 10 || rev === 11) rev = 0
  if (rev !== parseInt(cpf.charAt(9))) return false

  sum = 0
  for (let i = 0; i < 10; i++) sum += parseInt(cpf.charAt(i)) * (11 - i)
  rev = 11 - (sum % 11)
  if (rev === 10 || rev === 11) rev = 0
  return rev === parseInt(cpf.charAt(10))
}

const handleLogin = async () => {
  if (!loginCpf.value || !loginPassword.value) {
    toast.warning('Preencha todos os campos obrigatórios para login.')
    return
  }

  if (cpfError.value) {
    toast.error('Por favor, verifique o CPF inserido.')
    return
  }

  try {
    loginCpf.value = loginCpf.value.replace(/\D/g, '')
    store.startLoading()

    const response = await loginApi(loginCpf.value, loginPassword.value)
    if (response && response.token) {
      localStorage.setItem('authToken', response.token)

      const userData = await fetchUserData()

      // Armazenamento
      localStorage.setItem('userName', userData.username)
      localStorage.setItem('user', JSON.stringify(userData))
      localStorage.setItem('userPerfil', userData.role.name)
      localStorage.setItem('userId', userData.id)
      localStorage.setItem('userEmpresa', JSON.stringify(userData.empresa || {}))
      window.dispatchEvent(new Event('storage'))

      const empresaCodigo = userData.empresa?.codigoPublico
      if (!empresaCodigo) {
        toast.error('Empresa não encontrada.')
        return
      }

      if (!userData.ativo) {
        // Usuário INATIVO: redireciona para verificação de CPF
        await router.push({
          path: `/empresa/${empresaCodigo}/cadastro-condominio/${userData.id}`,
        })
        return
      }

      // Usuário ATIVO: redireciona para admin
      await router.push({
        path: `/empresa/${empresaCodigo}/admin`,
      })

      window.location.reload()
    } else {
      toast.error('Erro ao fazer login. Verifique seu CPF e senha.')
    }
  } catch (err: any) {
    if (err.response) {
      const status = err.response.status
      const message = err.response.data?.message || 'Erro ao realizar login.'

      if (status === 401) {
        toast.error(message) // Ex: CPF ou senha inválidos.
      } else {
        toast.error(`Erro ${status}: ${message}`)
      }
    } else {
      toast.error('Erro de conexão com o servidor.')
      console.error(err)
    }
  } finally {
    store.stopLoading()
  }
}
</script>

<style scoped>
/* Fundo geral preto */
body,
html {
  height: 100%;
  margin: 0;
  background-color: #000000;
  font-family: 'Segoe UI', sans-serif;
}

/* Container principal do login */
.container {
  display: flex;
  justify-content: center;
  padding: 3rem 2rem 2rem;
  min-height: calc(100vh - 60px);
  box-sizing: border-box;
}

/* Caixa do login */
.login-container {
  background-color: #121212;
  border-radius: 12px;
  padding: 2.5rem 3rem;
  width: 100%;
  max-width: 500px;
  box-shadow: 0 0 15px rgba(179, 134, 0, 0.6); /* sombra amarela mais escura */
  color: #b38600; /* amarelo escuro */
}

/* Títulos e textos */
h2 {
  text-align: center;
  color: #b38600; /* amarelo escuro */
  margin-bottom: 1.5rem;
}

/* Inputs e botões */
input,
button {
  width: 100%;
  padding: 0.8rem;
  margin-top: 0.5rem;
  font-size: 1rem;
  border-radius: 6px;
  border: 1px solid #333;
  background-color: #222;
  color: #b38600;
}

input::placeholder {
  color: #888;
}

button {
  background-color: #b38600;
  border: none;
  cursor: pointer;
  font-weight: 600;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #d9a500;
  color: #000;
}

/* Links */
RouterLink {
  display: block;
  text-align: center;
  color: #b38600;
  margin-top: 1rem;
  cursor: pointer;
}

.center-link {
  display: block;
  text-align: center;
  color: #b38600;
  margin-top: 1rem;
}

/* Password toggle icon */
.password-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.password-wrapper input {
  flex-grow: 1;
  padding-right: 2.5rem;
  background-color: #222;
  color: #b38600;
  border-radius: 6px;
  border: 1px solid #333;
  font-size: 1rem;
}

.toggle-icon {
  position: absolute;
  right: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  color: #b38600;
  font-size: 1.1rem;
  user-select: none;
}

/* Erros */
.error-message {
  color: #ff6b6b;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

/* Link esqueci senha */
.forgot-password {
  display: block;
  text-align: right;
  margin-top: 2rem;
  margin-bottom: 1rem;
  color: #b38600;
  font-size: 0.9rem;
  cursor: pointer;
  text-decoration: underline;
}

.forgot-password:hover {
  color: #d9a500;
}

/* Labels mais destacados */
label {
  display: block;
  font-weight: 700;
  color: #d9a500; /* amarelo vivo para destaque */
  margin-bottom: 0.5rem; /* espaço maior do label para o input */
  text-shadow: 0 0 3px rgba(217, 165, 0, 0.8); /* leve brilho amarelo */
}

/* Inputs com destaque ao redor */
input {
  width: 100%;
  padding: 0.8rem;
  margin-top: 0.25rem; /* espaçamento menor pois já tem do label */
  font-size: 1rem;
  border-radius: 6px;
  border: 2px solid transparent;
  background-color: #222;
  color: #b38600;
  transition:
    border-color 0.3s ease,
    box-shadow 0.3s ease;
  box-shadow: inset 0 0 5px #b38600; /* brilho interno suave */
}

input:focus {
  outline: none;
  border-color: #d9a500; /* contorno amarelo vivo ao focar */
  box-shadow: 0 0 8px #d9a500; /* brilho externo amarelo */
}

/* Mantém o espaçamento dos grupos */
.input-group {
  margin-bottom: 1.5rem;
}

/* Botão Entrar com texto mais visível */
button {
  background-color: #b38600;
  border: none;
  cursor: pointer;
  font-weight: 700;
  font-size: 1.1rem;
  color: #fff9d1; /* amarelo clarinho para texto */
  text-shadow: 0 0 4px rgba(255, 255, 224, 0.8);
  padding: 0.9rem;
  border-radius: 8px;
  transition:
    background-color 0.3s ease,
    color 0.3s ease;
}

button:hover {
  background-color: #d9a500;
  color: #000;
}
</style>
