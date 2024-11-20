import { error as logError } from "@tauri-apps/plugin-log";
import { useUserStore } from '~/stores'

export default defineNuxtPlugin(async () => {
  const userStore = useUserStore()
  const { getSession, token, status } = useAuth()
  
  const syncToken = async () => {
    const storeToken = await userStore.getToken()
    if (storeToken) {
      if (!token.value || token.value !== storeToken) {
        useCookie('auth.token').value = storeToken
      }
    } else if (token.value) {
      await userStore.setToken({ token: token.value })
    }
  }
  
  const syncUser = async () => {
    if (status.value === 'authenticated' && !userStore.user) {
      const session = await getSession()
      if (session) {
        await userStore.setUser(session)
      }
    }
  }


  await userStore.initStore()
  await syncToken()

  if (await userStore.getToken()) {
    try {
      await getSession()
      await syncUser()
    } catch (error) {
      await logError(`Error getting session: ${error}`)
      await userStore.clearUser()
      useCookie('auth.token').value = null
    }
  }
  
  return {
    provide: {
      syncAuthState: async () => {
        await syncToken()
        await syncUser()
      }
    }
  }
})