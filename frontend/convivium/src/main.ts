import './assets/main.css'
import 'vue3-toastify/dist/index.css'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Vue3Toastify from 'vue3-toastify'
import App from './App.vue'
import router from './router'
import 'vue3-toastify/dist/index.css' // Importe o CSS do Toastify
import Loader from './components/Loader.vue'

// Importando Font Awesome

// Adicionando o ícone `faCar` à biblioteca

const app = createApp(App)

// Usando o Vue3Toastify globalmente
app.use(Vue3Toastify, {
  autoClose: 4000,
  position: 'top-right',
  theme: 'light',
  pauseOnFocusLoss: false,
  pauseOnHover: false,
})
// Configuração do Pinia
app.use(createPinia())

// Registro global do componente Loader
app.component('Loader', Loader)
app.use(createPinia())
app.use(router)
app.mount('#app')
