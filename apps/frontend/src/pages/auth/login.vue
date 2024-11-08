<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '~/stores'
import LoginForm from "~/components/auth/LoginForm.vue";

import { useToast } from '@cortex/shared/components/ui/toast';

const { toast } = useToast()

definePageMeta({
  layout: 'auth-default',
  auth: {
    unauthenticatedOnly: true,
    navigateAuthenticatedTo: '/roadmaps'
  },
})

const auth = useAuthStore()
const { signIn } = useAuth()
const error = ref('')
const loading = ref(false)
const errorStatus = ref<number | null>(null)

const handleSubmit = async (credentials: { username: string; password: string }) => {
  error.value = ''
  loading.value = true
  try {
    await signIn(credentials, { callbackUrl: '/' })
  } catch (err: any) {
    errorStatus.value = err?.response?.status
    console.error('Error de inicio de sesión:', err)
    
    let errorMessage = 'Ocurrió un error inesperado'
    
    switch (errorStatus.value) {
      case 401:
        errorMessage = 'Usuario o contraseña inválidos'
        break
      case 403:
        errorMessage = 'Cuenta bloqueada. Por favor contacte a soporte'
        break
      case 404:
        errorMessage = 'Usuario no encontrado'
        break
      case 429:
        errorMessage = 'Demasiados intentos de inicio de sesión. Por favor intente más tarde'
        break
      case 500:
        errorMessage = 'Error del servidor. Por favor intente más tarde'
        break
    }
    
    error.value = errorMessage
    toast({
      title: 'Error',
      description: errorMessage,
    })
  } finally {
    loading.value = false
  }
}

const handleLogin = (provider: 'github' | 'google') => {
  auth.loginWithProvider(provider)
}
</script>

<template>
  <div class="flex w-full min-w-[364px] max-w-[470px] self-start justify-center items-center">
    <LoginForm :loading="loading" @submit="handleSubmit" @login="handleLogin" />

    <Toaster />

  </div>
</template>