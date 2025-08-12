import './assets/main.css'
import 'vue3-toastify/dist/index.css'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Vue3Toastify from 'vue3-toastify'
import App from './App.vue'
import router from './router'
import Loader from './components/Loader.vue'

const app = createApp(App)

app.use(createPinia()) // Registra Pinia 1 vez
app.use(router)

app.use(Vue3Toastify, {
  autoClose: 4000,
  position: 'top-right',
  theme: 'light',
  pauseOnFocusLoss: false,
  pauseOnHover: false,
})

app.component('Loader', Loader)

app.mount('#app')
