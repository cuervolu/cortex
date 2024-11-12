<script setup lang="ts">
import {ref, onMounted, computed} from "vue";
import {error as logError, info, warn} from "@tauri-apps/plugin-log";
import {useUserStore} from '~/stores'
import LoginForm from "~/components/auth/LoginForm.vue";
import {useAuthStore} from "~/stores/auth.store";

definePageMeta({
  layout: 'auth-default',
})

const {signIn, getSession} = useAuth()
const authStore = useAuthStore();
const userStore = useUserStore()

const error = ref('')
const formLoading = ref(false)

const isLoading = computed(() => formLoading.value || authStore.loading)

onMounted(async () => {
  await userStore.initStore()
  const user = await userStore.getUser()
  if (user) {
    const authToken = await userStore.getToken();
    if (authToken) {
      await userStore.setToken({token: authToken})
    } else {
      await warn('No auth token found in store')
    }
    await info(`User already logged in: ${user.username}`)
  }
})

const handleSubmit = async (credentials: { username: string; password: string }) => {
  error.value = ''
  formLoading.value = true

  try {
    const response = await signIn(credentials, {callbackUrl: '/'})
    if (response?.error) {
      throw new Error(response.error)
    }

    const session = await getSession()
    if (session) {
      await info(`User logged in: ${session.username}`)
      await userStore.setUser(session)

      // We obtain the auth token from the cookie
      const authToken = useCookie('auth.token')
      if (authToken.value) {
        await userStore.setToken({token: authToken.value})
      } else {
        await warn('No auth token found in cookie')
      }
    }
  } catch (err) {
    await logError(`Login error: ${err}`)
    error.value = err instanceof Error ? err.message : 'Invalid credentials'
  } finally {
    formLoading.value = false
  }
}

const handleLoginWithProvider = (provider: 'github' | 'google') => {
  authStore.loginWithProviderDesktop(provider)
}
</script>

<template>
  <div class="flex w-full min-w-[364px] max-w-[470px] self-start justify-center items-center">
    <LoginForm
        :loading="isLoading"
        @submit="handleSubmit"
        @login="handleLoginWithProvider"
    />
  </div>
</template>