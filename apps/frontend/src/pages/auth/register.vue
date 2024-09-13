<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '~/stores'

const auth = useAuthStore()
const username = ref('')
const email = ref('')
const password = ref('')

const handleSubmit = async () => {
  try {
    await auth.register({ username: username.value, email: email.value, password: password.value })
    // Redirect or show success message
  } catch (error) {
    // Handle error
  }
}
</script>
<template>
  <form @submit.prevent="handleSubmit">
    <input v-model="username" type="text" placeholder="Username" required>
    <input v-model="email" type="email" placeholder="Email" required>
    <input v-model="password" type="password" placeholder="Password" required>
    <button type="submit">Register</button>
  </form>
</template>

