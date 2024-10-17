import { defineStore } from 'pinia'
import {API_ROUTES} from "~/config/api";

export interface AuthenticationPayload {
  username: string;
  password: string;
}

export const useAuthStore = defineStore('auth', () => {
  const loading = ref(false)
  

  const activateAccount = async (token: string) => {
    try {
      loading.value = true
      await $fetch(API_ROUTES.ACTIVATE_ACCOUNT, {
        method: 'GET',
        params: { token }
      })
    } catch (error) {
      console.error('Account activation error:', error)
      throw new Error('Account activation failed')
    } finally {
      loading.value = false
    }
  }
  
  const loginWithProvider = (provider: 'github' | 'google') => {
    window.location.href = `${API_ROUTES.OAUTH_BASE}/${provider}`
  }

  return {
    activateAccount,
    loginWithProvider,
  }
})