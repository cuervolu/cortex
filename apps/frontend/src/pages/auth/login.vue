<script setup lang="ts">
import {ref} from 'vue'
import {useAuthStore} from '~/stores'

definePageMeta({
  layout: 'auth-default',
})

const auth = useAuthStore()
const router = useRouter()
const error = ref('')

const handleSubmit = async (formData: { email: string, password: string }) => {
  console.log(formData)
  error.value = ''
  try {
    await auth.login({username: formData.email, password: formData.password})
    await router.push('/')
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'An error occurred during login'
    console.error(err)
  }
}

const handleLogin = (provider: 'github' | 'google') => {
  auth.loginWithProvider(provider)
}
</script>

<template>
  <AuthForm
      title="Sign In"
      subtitle="Log in with a provider or with email"
      submit-text="Sign in"
      :show-name-fields="false"
      @submit="handleSubmit"
      @login="handleLogin"
  >
    <template #footer>
      <div class="text-center mt-4">
        <a href="#" class="text-purple-900 hover:underline text-sm">
          Forgot password?
        </a>
        <p class="mt-2 text-sm text-gray-600">
          Don't have an account?
          <NuxtLink to="/auth/register" class="text-purple-700 hover:underline">
            Sign up
          </NuxtLink>
        </p>
      </div>
    </template>
  </AuthForm>
</template>