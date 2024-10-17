import { error as logError } from "@tauri-apps/plugin-log";
import { defineNuxtPlugin, useCookie } from '#app'
import { useUserStore } from '~/stores'

export default defineNuxtPlugin(async () => {
  const userStore = useUserStore()
  const { getSession, token, status } = useAuth()

  // Sync token
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

  // Sync user
  const syncUser = async () => {
    if (status.value === 'authenticated' && !userStore.user) {
      const session = await getSession()
      if (session) {
        await userStore.setUser(session)
      }
    }
  }

  // Init store
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

  // Exponer funciones útiles
  return {
    provide: {
      syncAuthState: async () => {
        await syncToken()
        await syncUser()
      }
    }
  }
})