<script setup lang="ts">
import {ref} from 'vue'
import {useAuthStore} from '~/stores'
import {z} from 'zod'

definePageMeta({
  layout: 'auth-default',
})

const auth = useAuthStore()
const router = useRouter()
const error = ref('')
const loading = ref(false)

const formSchema = z.object({
  email: z.string().email('Invalid email address'),
  password: z.string().min(8, 'Password must be at least 8 characters'),
})

type FormValues = z.infer<typeof formSchema>

const handleSubmit = async (formData: FormValues) => {
  console.log(formData)
  error.value = ''
  loading.value = true
  try {
    await auth.login({username: formData.email, password: formData.password})
    await router.push('/')
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'An error occurred during login'
    console.error(err)
  } finally {
    loading.value = false
  }
}

const handleLogin = (provider: 'github' | 'google') => {
  auth.loginWithProvider(provider)
}
</script>

<template>
  <div>
    <AuthForm
        title="Sign In"
        subtitle="Log in with a provider or with email"
        submit-text="Sign in"
        :show-fields="['email', 'password']"
        :loading="loading"
        :validation-schema="formSchema"
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

    <Alert v-if="error" variant="destructive" class="mt-4">
      <AlertTitle>Error</AlertTitle>
      <AlertDescription>{{ error }}</AlertDescription>
    </Alert>
  </div>
</template>