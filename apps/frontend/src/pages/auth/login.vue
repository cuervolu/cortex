<script setup lang="ts">
import {ref} from 'vue'
import {useAuthStore} from '~/stores'

const auth = useAuthStore()
const router = useRouter()

const username = ref('')
const password = ref('')
const error = ref('')

const handleSubmit = async () => {
  error.value = ''
  try {
    await auth.login({username: username.value, password: password.value})
    await router.push('/')
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'An error occurred during login'
    console.error(err)
  }
}

const loginWithGithub = () => auth.loginWithProvider('github')
const loginWithGoogle = () => auth.loginWithProvider('google')
</script>

<template>
  <form @submit.prevent="handleSubmit">
    <div v-if="error" class="error">{{ error }}</div>
    <input v-model="username" type="text" placeholder="Username" required>
    <input v-model="password" type="password" placeholder="Password" required>
    <button type="submit" :disabled="auth.loading">
      {{ auth.loading ? 'Logging in...' : 'Login' }}
    </button>
    <Button type="button" :disabled="auth.loading" @click="loginWithGithub">
      Login with GitHub
    </Button>
    <Button type="button" :disabled="auth.loading" @click="loginWithGoogle">
      Login with Google
    </Button>
  </form>
</template>

<style scoped>
.error {
  color: red;
  margin-bottom: 10px;
}
</style>
