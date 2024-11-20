import {invoke} from '@tauri-apps/api/core'
import {listen} from '@tauri-apps/api/event'
import {confirm} from '@tauri-apps/plugin-dialog';
import {open} from '@tauri-apps/plugin-shell'
import {debug, info, warn} from '@tauri-apps/plugin-log'
import {API_ROUTES} from "@cortex/shared/config/api"
import {AppError} from '@cortex/shared/types'

interface OAuthConfig {
  provider: string
  code_verifier: string
  code_challenge: string
  port: number
}

const AUTH_TIMEOUT = 5 * 60 * 1000 // 5 minutes

export const useAuthStore = defineStore('auth', () => {
  const userStore = useUserStore()
  const router = useRouter()
  const {getSession} = useAuth()
  const {$syncAuthState} = useNuxtApp()
  const errorHandler = useDesktopErrorHandler()

  const isAuthCompleted = ref(false)
  const loading = ref(false)

  const cleanup = async (port?: number, unlisten?: () => void) => {
    try {
      if (unlisten) {
        unlisten()
      }
      if (port && !isAuthCompleted.value) {
        await invoke('cancel_oauth_flow', {port})
      }
    } catch (error) {
      await debug(`Cleanup error (can be ignored): ${error}`)
    }
  }

  const extractTokenFromUrl =  (url: string): { token: string | null, requiresProfile: boolean } => {
    try {
      debug(`Parsing callback URL: ${url}`)
      const urlParams = new URLSearchParams(new URL(url).search)
      return {
        token: urlParams.get('token'),
        requiresProfile: urlParams.get('requiresProfile') === 'true'
      }
    } catch (error) {
      throw new Error(`Failed to parse callback URL: ${error}`)
    }
  }

  const loginWithProviderDesktop = async (provider: 'github' | 'google') => {
    let port: number | undefined
    let unlisten: (() => void) | undefined
    let timeoutId: NodeJS.Timeout | undefined

    isAuthCompleted.value = false
    loading.value = true

    try {
      // Configurar timeout para el flujo OAuth
      timeoutId = setTimeout(() => {
        throw new AppError('La autenticación ha expirado. Por favor, intenta de nuevo.', {
          statusCode: 408,
          data: {
            provider,
            action: 'oauth_timeout'
          }
        })
      }, AUTH_TIMEOUT)

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
          const {token, requiresProfile} = extractTokenFromUrl(callbackUrl)

          if (!token) {
            throw new AppError('No token received in callback URL', {
              statusCode: 401,
              data: {callbackUrl}
            })
          }

          if (requiresProfile) {
            const confirmation = await confirm(
                'Necesitas completar tu perfil, ¿quieres hacerlo ahora? De lo contrario, no podrás continuar.',
                {title: '¡Espera!', kind: 'warning'}
            );

            if (!confirmation) {
              await warn('User declined to complete profile')
              await cleanup(port, unlisten)
              return
            }

            await info('User needs to complete profile, redirecting to web app...')
            await userStore.setToken({token})
            await open('http://localhost:3000/auth/complete-profile')
            return
          }

          await userStore.setToken({token})
          await info('Token stored successfully')
          await $syncAuthState()

          const session = await getSession()
          if (session) {
            await userStore.setUser(session)
            await info('User session updated')
            isAuthCompleted.value = true
            await cleanup(port, unlisten)
            await router.push('/')
          } else {
            throw new AppError('Failed to get user session after authentication', {
              statusCode: 401,
              data: {
                provider,
                hasToken: !!token
              }
            })
          }
        } catch (error) {
          await errorHandler.handleError(error, {
            statusCode: 401,
            data: {
              action: 'oauth_callback',
              provider,
              url: event.payload
            }
          })
          await cleanup(port, unlisten)
        } finally {
          if (timeoutId) clearTimeout(timeoutId)
        }
      })
    } catch (error) {
      await cleanup(port, unlisten)
      await errorHandler.handleError(error, {
        statusCode: 401,
        data: {
          action: 'start_oauth_flow',
          provider
        }
      })
    } finally {
      if (timeoutId) clearTimeout(timeoutId)
      loading.value = false
    }
  }

  return {
    loginWithProviderDesktop,
    loading,
    isAuthCompleted
  }
})