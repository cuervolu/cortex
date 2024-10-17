<script setup lang="ts">
import { ref, onMounted } from "vue";
import { error as logError, info } from "@tauri-apps/plugin-log";
import { useUserStore } from '~/stores'
import LoginForm from "~/components/auth/LoginForm.vue";
import { useCookie} from "#app";

definePageMeta({
  layout: 'auth-default',
})

const { signIn, getSession } = useAuth()
const userStore = useUserStore()
const error = ref('')
const loading = ref(false)

onMounted(async () => {
  await userStore.initStore()
  const user = await userStore.getUser()
  if (user) {
    await info(`User already logged in: ${user.username}`)
    
  }
})

const handleSubmit = async (credentials: { username: string; password: string }) => {
  error.value = ''
  loading.value = true
  try {
    const response = await signIn(credentials, { callbackUrl: '/' })
    if (response?.error) {
      throw new Error(response.error)
    }
    const session = await getSession()
    if (session) {
      await info(`User logged in: ${session.username}`)
      await userStore.setUser(session)

      // Obtener el token de la cookie
      const authToken = useCookie('auth.token')
      if (authToken.value) {
        await userStore.setToken({ token: authToken.value })
      } else {
        console.warn('No auth token found in cookie')
      }
    }
  } catch (err) {
    await logError(`Login error: ${err}`)
    error.value = err instanceof Error ? err.message : 'Invalid credentials'
  } finally {
    loading.value = false
  }
}

const handleLoginWithProvider = (provider: 'github' | 'google') => {
  //TODO: Implement login with provider
  console.log(`Login with provider: ${provider}`)
}
</script>


<template>
  <div>
    <LoginForm
        :loading="loading"
        @submit="handleSubmit"
        @login="handleLoginWithProvider"
    />

    <Alert v-if="error" variant="destructive" class="mt-4">
      <AlertTitle>Error</AlertTitle>
      <AlertDescription>{{ error }}</AlertDescription>
    </Alert>
  </div>
</template>