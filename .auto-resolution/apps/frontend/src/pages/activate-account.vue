<script setup lang="ts">

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const loading = ref(true)
const error = ref('')
const success = ref(false)

onMounted(async () => {
  const token = route.query.token as string

  if (!token) {
    error.value = 'No activation token provided'
    loading.value = false
    return
  }

  try {
    await auth.activateAccount(token)
    success.value = true
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'An error occurred during account activation'
    console.error(err)
  } finally {
    loading.value = false
  }
})

const goToLogin = () => {
  router.push('/auth/login')
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <div v-if="loading">
        <p class="text-center text-lg">Activating your account...</p>
      </div>
      <div v-else-if="error" class="text-center">
        <h2 class="mt-6 text-3xl font-extrabold text-gray-900">Account Activation Failed</h2>
        <p class="mt-2 text-sm text-red-600">{{ error }}</p>
      </div>
      <div v-else-if="success" class="text-center">
        <h2 class="mt-6 text-3xl font-extrabold text-gray-900">Account Activated Successfully</h2>
        <p class="mt-2 text-sm text-green-600">Your account has been successfully activated.</p>
        <div class="mt-6">
          <button
              class="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              @click="goToLogin">
            Go to Login
          </button>
        </div>
      </div>
    </div>
  </div>
</template>