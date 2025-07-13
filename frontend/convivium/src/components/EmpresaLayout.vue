<template>
  <div>
    <header class="bg-blue-700 text-white p-4">
      <h1 class="text-xl font-bold">
        {{ empresa?.name || 'Condomínio' }}
      </h1>
    </header>

    <main class="p-4">
      <slot />
    </main>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { buscarEmpresaPorCodigo } from '@/services/empresaService'

const route = useRoute()
const empresa = ref<any>(null)

onMounted(async () => {
  const codigo = route.params.codigo as string
  try {
    empresa.value = await buscarEmpresaPorCodigo(codigo)
  } catch (error) {
    console.error('Erro ao carregar empresa:', error)
  }
})
</script>
