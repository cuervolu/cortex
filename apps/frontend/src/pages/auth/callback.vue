<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from "~/stores"

definePageMeta({
  auth: {
    unauthenticatedOnly: true,
  },
})

const router = useRouter()
const authStore = useAuthStore()
const message = ref('Procesando autenticación...')

onMounted(async () => {
  const urlParams = new URLSearchParams(window.location.search)
  const token = urlParams.get('token')

  try {
    if (token) {
      const redirectTo = await authStore.handleOAuthCallback(token)
      message.value = 'Autenticación exitosa. Redirigiendo...'
      setTimeout(() => router.push(redirectTo), 1500) 
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
  <div class="flex min-h-screen items-center justify-center">
    <Card class="w-full max-w-md">
      <CardHeader>
        <CardTitle>Autenticación OAuth</CardTitle>
      </CardHeader>
      <CardContent>
        <p class="text-center">{{ message }}</p>
      </CardContent>
    </Card>
  </div>
</template>