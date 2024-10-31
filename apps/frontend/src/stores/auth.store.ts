import { defineStore } from 'pinia'
import {API_ROUTES} from "~/config/api";

export const useAuthStore = defineStore('auth', () => {
  const { getSession } = useAuth()
  const { setToken } = useAuthState()
  const loading = ref(false)

  const handleOAuthCallback = async (token: string) => {
    try {
      loading.value = true

      // We need to set the token in the store
      setToken(token)

      // Update the session
      await getSession()

      return true
    } catch (error) {
      console.error('OAuth callback error:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const loginWithProvider = (provider: 'github' | 'google') => {
    window.location.href = `${API_ROUTES.OAUTH_BASE}/${provider}`
  }

  const activateAccount = async (token: string) => {
    try {
      loading.value = true
      await $fetch(`${API_ROUTES.ACTIVATE_ACCOUNT}/auth/activate-account`, {
        method: 'GET',
        params: { token }
      })
    } catch (error) {
      console.error('Account activation error:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  return {
    loading,
    handleOAuthCallback,
    loginWithProvider,
    activateAccount
  }
})