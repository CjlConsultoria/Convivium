<template>
  <div v-if="isLoading" class="loading-overlay">
    <div class="spinner"></div>
  </div>
</template>

<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { useLoadingStore } from '@/stores/loadingStore'

const { loading: isLoading } = storeToRefs(useLoadingStore())
</script>

<style scoped>
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(255, 255, 255, 0.6); /* fundo semi-transparente */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.spinner {
  width: 48px;
  height: 48px;
  border: 5px solid #ccc;
  border-top-color: #42b983;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
