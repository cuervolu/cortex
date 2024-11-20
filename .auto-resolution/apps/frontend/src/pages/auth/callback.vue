<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from "~/stores"
import {AppError} from "@cortex/shared/types";

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

    if(!token) {
      throw new AppError('Token no encontrado')
    }
    const redirectTo = await authStore.handleOAuthCallback(token!)
    message.value = 'Autenticación exitosa. Redirigiendo...'
    await nextTick()
    await router.replace(redirectTo)
  } catch (err) {
    message.value = 'Error en la autenticación. Redirigiendo al login...'
    await nextTick()
    await router.replace('/auth/login')
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