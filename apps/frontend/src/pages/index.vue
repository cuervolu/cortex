<script setup lang="ts">
import {storeToRefs} from 'pinia'
import {useAuthStore} from '~/stores/auth.store'


useHead({
  title: 'Inicio | Cortex',
  meta: [
    {
      name: 'description',
      content: 'Página de inicio de Cortex'
    }
  ]
})


definePageMeta({
  middleware: 'auth'
})
const authStore = useAuthStore()
const {user, isAuthenticated} = storeToRefs(authStore)
const router = useRouter()

const login = () => {
  router.push('/auth/login')
}

const logout = async () => {
  await authStore.logout()
  await router.push('/')
}

const loginWithProvider = (provider: 'github' | 'google') => {
  authStore.loginWithProvider(provider)
}
</script>

<template>
  <div>
    <h1 v-if="isAuthenticated">
      Hola, {{ user?.firstName || user?.username }}
    </h1>
    <h1 v-else>
      Hola, rarito
    </h1>
    <p v-if="isAuthenticated">
      Email: {{ user?.email }}
    </p>
    <div v-if="!isAuthenticated">
      <cortex-btn @click="login">Iniciar Sesión</cortex-btn>
      <cortex-btn @click="() => loginWithProvider('github')">
        Iniciar sesión con GitHub
      </cortex-btn>
      <cortex-btn @click="() => loginWithProvider('google')">
        Iniciar sesión con Google
      </cortex-btn>
    </div>
    <cortex-btn v-else @click="logout">Cerrar Sesión</cortex-btn>
  </div>
</template>