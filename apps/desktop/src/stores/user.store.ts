import {invoke} from '@tauri-apps/api/core'
import type {Store} from '@tauri-apps/plugin-store'
import {load} from '@tauri-apps/plugin-store'
import {info, debug} from "@tauri-apps/plugin-log"

import type {User} from "~/types"

interface AuthResponse {
  token: string
}

let store: Store

function createUserStore() {
  return defineStore('user', () => {
    const {handleError} = useErrorHandler()
    const user = ref<User | null>(null)
    const token = ref<string | null>(null)

    const initStore = async () => {
      try {
        store = await load('store.bin', {autoSave: true})
        await debug('Store loaded successfully')

        const storedUser = await store.get('user')
        const storedToken = await store.get('token')

        if (storedUser) user.value = storedUser as User
        if (storedToken) token.value = storedToken as string

        if (user.value && token.value) {
          await invoke('set_user', {user: user.value, token: token.value})
          await debug('User and token restored from store')
        }
      } catch (error) {
        await handleError(error, {
          statusCode: 500,
          data: {action: 'init_store'},
          fatal: true, // Fatal error, we can't continue without the store
          notify: true
        })
      }
    }

    const setToken = async (authResponse: AuthResponse) => {
      try {
        token.value = authResponse.token
        await store.set('token', authResponse.token)
        await debug('Token stored successfully')

        if (user.value) {
          await invoke('set_user', {user: user.value, token: authResponse.token})
          await debug('User context updated with new token')
        }
      } catch (error) {
        await handleError(error, {
          statusCode: 500,
          data: {action: 'set_token'}
        })
      }
    }

    const setUser = async (userData: User) => {
      try {
        user.value = userData
        await store.set('user', userData)
        await debug('User stored successfully')

        if (token.value) {
          await invoke('set_user', {user: userData, token: token.value})
          await debug('User context updated in backend')
        }
      } catch (error) {
        await handleError(error, {
          statusCode: 500,
          data: {action: 'set_user', userId: userData.id}
        })
      }
    }

    const getUser = async (): Promise<User | null> => {
      try {
        if (!user.value) {
          const storedUser = await store.get('user')
          if (storedUser) {
            user.value = storedUser as User
            await debug('User retrieved from store')
          } else {
            const fetchedUser = await invoke<User | null>('get_user')
            if (fetchedUser) {
              user.value = fetchedUser
              await store.set('user', fetchedUser)
              await debug('User fetched from backend and stored')
            }
          }
        }
        return user.value
      } catch (error) {
        await handleError(error, {
          statusCode: 401,
          data: {action: 'get_user'}
        })
        return null
      }
    }

    const getToken = async (): Promise<string | null> => {
      try {
        if (!token.value) {
          const storedToken = await store.get('token')
          if (storedToken) {
            token.value = storedToken as string
            await debug('Token retrieved from store')
          } else {
            const fetchedToken = await invoke<string | null>('get_token')
            if (fetchedToken) {
              token.value = fetchedToken
              await store.set('token', fetchedToken)
              await debug('Token fetched from backend and stored')
            }
          }
        }
        return token.value
      } catch (error) {
        await handleError(error, {
          statusCode: 401,
          data: {action: 'get_token'}
        })
        return null
      }
    }

    const clearUser = async () => {
      try {
        await info('Clearing user data')
        user.value = null
        token.value = null
        await store.clear()
        await invoke('clear_user')
        await debug('User data cleared successfully')
      } catch (error) {
        await handleError(error, {
          statusCode: 500,
          data: {action: 'clear_user'}
        })
      }
    }

    const getTokenFromCookie = () => {
      try {
        const authToken = useCookie('auth.token')
        return authToken.value
      } catch (error) {
        handleError(error, {
          statusCode: 401,
          data: {action: 'get_token_from_cookie'}
        })
        return null
      }
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
}

export const useUserStore = createUserStore()