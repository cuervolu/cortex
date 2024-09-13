import {defineStore} from 'pinia'
import type {User} from "~/interfaces"
import {API_ROUTES} from '~/config/api'

interface UserPayloadInterface {
  username: string;
  password: string;
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const loading = ref(false)
  const authenticated = ref(false)
  const router = useRouter()

  const isAuthenticated = computed(() => authenticated.value && !!user.value)

  const getToken = () => useCookie('token').value

  const setToken = (newToken: string) => {
    const tokenCookie = useCookie('token')
    tokenCookie.value = newToken
    authenticated.value = true
  }

  const fetchUser = async () => {
    const token = getToken()
    if (!token) return

    try {
      loading.value = true
      user.value = await $fetch<User>(API_ROUTES.USER_INFO, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      })
      authenticated.value = true
    } catch (error) {
      console.error('Error fetching user:', error)
      await logout()
    } finally {
      loading.value = false
    }
  }

  const verifyToken = async () => {
    const token = getToken()
    if (!token) return false

    try {
      await fetchUser()
      return true
    } catch (error) {
      console.error('Error verifying token:', error)
      return false
    }
  }

  const login = async ({username, password}: UserPayloadInterface) => {
    try {
      loading.value = true
      const data = await $fetch<{ token: string }>(API_ROUTES.LOGIN, {
        method: 'POST',
        body: {username, password}
      })
      if (data.token) {
        setToken(data.token)
        await fetchUser()
      }
    } catch (error) {
      console.error('Login error:', error)
      throw new Error('Login failed')
    } finally {
      loading.value = false
    }
  }

  const register = async (userData: User) => {
    try {
      loading.value = true
      await $fetch(API_ROUTES.REGISTER, {
        method: 'POST',
        body: userData
      })
    } catch (error) {
      console.error('Registration error:', error)
      throw new Error('Registration failed')
    } finally {
      loading.value = false
    }
  }

  const logout = async (): Promise<void> => {
    const tokenCookie = useCookie('token')
    tokenCookie.value = null
    authenticated.value = false
    user.value = null
    await router.push('/login')
  }

  const initAuth = async () => {
    const token = getToken()
    if (token) {
      await fetchUser()
    }
  }

  const loginWithProvider = (provider: 'github' | 'google') => {
    window.location.href = `${API_ROUTES.OAUTH_BASE}/${provider}`
  }

  return {
    user,
    loading,
    authenticated,
    isAuthenticated,
    setToken,
    login,
    register,
    logout,
    initAuth,
    loginWithProvider,
    fetchUser,
    verifyToken
  }
})