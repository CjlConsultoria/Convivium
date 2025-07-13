<template>
  <div v-if="loading" class="overlay-loading">
    <div class="spinner"></div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useLoadingStore } from '@/stores/loadingStore' // Importe a store correta

const store = useLoadingStore() // Acesse a store corretamente

const loading = computed(() => store.loading) // Bind ao estado de loading
</script>

<style scoped>
s .overlay-loading {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.8);
  z-index: 9999;
  display: flex;
  justify-content: center;
  align-items: center;
}

.spinner {
  border: 8px solid #f3f3f3;
  border-top: 8px solid #42b983;
  border-radius: 50%;
  width: 60px;
  height: 60px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
