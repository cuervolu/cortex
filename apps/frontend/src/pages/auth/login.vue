<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '~/stores'
import LoginForm from "~/components/auth/LoginForm.vue";

definePageMeta({
  layout: 'auth-default',
})

const auth = useAuthStore()
const { signIn } = useAuth()
const error = ref('')
const loading = ref(false)

const handleSubmit = async (credentials: { username: string; password: string }) => {
  error.value = ''
  loading.value = true
  try {
    await signIn(credentials, {callbackUrl: '/'})
    // Redirect or handle successful login
  } catch (err) {
    console.error('Login error:', err)
    error.value = err instanceof Error ? err.message : 'Invalid credentials'
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
    <LoginForm
        :loading="loading"
        @submit="handleSubmit"
        @login="handleLogin"
    />

    <Alert v-if="error" variant="destructive" class="mt-4">
      <AlertTitle>Error</AlertTitle>
      <AlertDescription>{{ error }}</AlertDescription>
    </Alert>
  </div>
</template>