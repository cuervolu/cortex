import {invoke} from '@tauri-apps/api/core'
import {listen} from '@tauri-apps/api/event'
import {open} from '@tauri-apps/plugin-shell'
import {debug, info} from '@tauri-apps/plugin-log'
import {API_ROUTES} from "@cortex/shared/config/api";

interface OAuthConfig {
  provider: string
  code_verifier: string
  code_challenge: string
  port: number
}

export const useAuthStore = defineStore('auth', () => {
  const userStore = useUserStore()
  const router = useRouter()
  const {getSession} = useAuth()
  const {$syncAuthState} = useNuxtApp()
  const {handleError} = useErrorHandler()
  
  const isAuthCompleted = ref(false)

  const cleanup = async (port?: number, unlisten?: () => void) => {
    try {
      if (unlisten) {
        unlisten()
      }
      if (port && !isAuthCompleted.value) {
        await invoke('cancel_oauth_flow', {port})
      }
    } catch (error) {
      // Ignorar errores de limpieza
      await debug(`Cleanup error (can be ignored): ${error}`)
    }
  }

  const extractTokenFromUrl = (url: string): string | null => {
    try {
      debug(`Parsing callback URL: ${url}`)
      const urlParams = new URLSearchParams(new URL(url).search)
      return urlParams.get('token')
    } catch (error) {
      throw new Error(`Failed to parse callback URL: ${error}`)
    }
  }

  const loginWithProviderDesktop = async (provider: 'github' | 'google') => {
    let port: number | undefined
    let unlisten: (() => void) | undefined
    isAuthCompleted.value = false

    try {
      const config = await invoke<OAuthConfig>('start_oauth_flow', {
        provider
      })
      port = config.port
      await info('OAuth server started successfully')

      const authUrl = `${API_ROUTES.OAUTH_BASE}/${provider}?` +
          `code_challenge=${config.code_challenge}&` +
          `code_challenge_method=S256&` +
          `client_type=desktop&` +
          `redirect_uri=http://localhost:${config.port}/callback`

      await info(`Opening browser with URL: ${authUrl}`)
      await open(authUrl)

      unlisten = await listen('oauth://url', async (event) => {
        try {
          const callbackUrl = event.payload as string
          const token = extractTokenFromUrl(callbackUrl)

          if (!token) {
            throw new Error('No token received in callback URL')
          }

          await userStore.setToken({token})
          await info('Token stored successfully')

          await $syncAuthState()
          const session = await getSession()

          if (session) {
            await userStore.setUser(session)
            await info('User session updated')

            // Marcar como completado antes de la limpieza
            isAuthCompleted.value = true

            // Realizar limpieza antes de la redirección
            await cleanup(port, unlisten)

            // Redireccionar después de la limpieza
            await router.push('/')
          } else {
            throw new Error('Failed to get user session after authentication')
          }
        } catch (error) {
          await handleError(error, {
            statusCode: 401,
            data: {
              action: 'oauth_callback',
              provider,
              url: event.payload
            }
          })
          // Limpiar en caso de error
          await cleanup(port, unlisten)
        }
      })
    } catch (error) {
      // Limpiar en caso de error en la configuración inicial
      await cleanup(port, unlisten)
      await handleError(error, {
        statusCode: 401,
        data: {
          action: 'start_oauth_flow',
          provider
        }
      })
    }
  }

  return {
    loginWithProviderDesktop
  }
})