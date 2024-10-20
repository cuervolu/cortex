<script setup lang="ts">
import {ref, onMounted} from 'vue'
import {useRouter} from 'vue-router'
import {useAuthStore} from "~/stores";

const router = useRouter()
const authStore = useAuthStore()
const message = ref('Procesando autenticación...')

onMounted(async () => {
  const urlParams = new URLSearchParams(window.location.search)
  const token = urlParams.get('token')

  try {
    if (token) {
      await authStore.setToken(token)
      message.value = 'Autenticación exitosa. Redirigiendo...'
      setTimeout(() => router.push('/'), 1500)
    } else {
      throw new Error('No se recibió token de autenticación')
    }
  } catch (error) {
    console.error('Error en la autenticación:', error)
    message.value = 'Error en la autenticación. Redirigiendo al login...'
    setTimeout(() => router.push('/auth/login'), 1500)
  }
})
</script>
<template>
  <div>
    <p>{{ message }}</p>
  </div>
</template>

