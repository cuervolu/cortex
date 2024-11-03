import {API_ROUTES} from "@cortex/shared/config/api";

export const useAuthStore = defineStore('auth', () => {
  const { getSession } = useAuth()
  const { setToken } = useAuthState()
  const loading = ref(false)

  const handleOAuthCallback = async (token: string) => {
    try {
      loading.value = true
      setToken(token)
      const session = await getSession()

      if (session) {
        const needsPassword = !session.has_password
        

        if (needsPassword) {
          return '/auth/complete-profile'
        }
      }
      
      return '/roadmaps'
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