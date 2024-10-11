import {invoke} from '@tauri-apps/api/core'
import type {Store} from '@tauri-apps/plugin-store';
import {createStore} from '@tauri-apps/plugin-store'
import {defineStore} from 'pinia'
import {ref} from 'vue'
import type {User} from "~/types"

interface AuthResponse {
  token: string;
}

let store: Store;

async function initStore() {
  store = await createStore('store.bin', {
    autoSave: 1, //HACK: This is a workaround for the issue #1902 in Tauri, waiting for a fix in #1860.
                 // See https://github.com/tauri-apps/plugins-workspace/issues/1902
  });
}

await initStore();

export const useUserStore = defineStore('user', () => {
  const user = ref<User | null>(null)
  const token = ref<string | null>(null)

  const initStore = async () => {
    const storedUser = await store.get('user')
    const storedToken = await store.get('token')
    if (storedUser) user.value = storedUser as User
    if (storedToken) token.value = storedToken as string
    if (user.value && token.value) {
      await invoke('set_user', {user: user.value, token: token.value})
    }
  }

  const setToken = async (authResponse: AuthResponse) => {
    token.value = authResponse.token
    await store.set('token', authResponse.token)
    if (user.value) {
      await invoke('set_user', {user: user.value, token: authResponse.token})
    }
  }

  const setUser = async (userData: User) => {
    user.value = userData
    await store.set('user', userData)
    if (token.value) {
      await invoke('set_user', {user: userData, token: token.value})
    }
  }

  const getUser = async (): Promise<User | null> => {
    if (!user.value) {
      const storedUser = await store.get('user')
      if (storedUser) {
        user.value = storedUser as User
      } else {
        try {
          const fetchedUser = await invoke<User | null>('get_user')
          if (fetchedUser) {
            user.value = fetchedUser
            await store.set('user', fetchedUser)
          }
        } catch (error) {
          console.error('Failed to get user from Rust backend:', error)
        }
      }
    }
    return user.value
  }

  const getToken = async (): Promise<string | null> => {
    if (!token.value) {
      const storedToken = await store.get('token')
      if (storedToken) {
        token.value = storedToken as string
      } else {
        try {
          const fetchedToken = await invoke<string | null>('get_token')
          if (fetchedToken) {
            token.value = fetchedToken
            await store.set('token', fetchedToken)
          }
        } catch (error) {
          console.error('Failed to get token from Rust backend:', error)
        }
      }
    }
    return token.value
  }

  const clearUser = async () => {
    user.value = null
    token.value = null
    await store.delete('user')
    await store.delete('token')
    try {
      await invoke('clear_user')
    } catch (error) {
      console.error('Failed to clear user in Rust backend:', error)
    }
  }

  const getTokenFromCookie = () => {
    const authToken = useCookie('auth.token')
    return authToken.value
  }

  return {
    user,
    token,
    initStore,
    setToken,
    setUser,
    getUser,
    getToken,
    clearUser,
    getTokenFromCookie,
  }
})