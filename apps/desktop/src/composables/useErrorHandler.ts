import {
  isPermissionGranted,
  requestPermission,
  sendNotification
} from '@tauri-apps/plugin-notification'
import { error as logError, debug } from "@tauri-apps/plugin-log"
import { useToast } from "@cortex/shared/components/ui/toast"

export interface ErrorOptions {
  statusCode?: number
  fatal?: boolean
  cause?: Error | unknown
  data?: Record<string, unknown>
  silent?: boolean
  groupKey?: string
}

export class AppError extends Error {
  statusCode: number
  fatal: boolean
  override cause?: Error | unknown
  data?: Record<string, unknown>
  groupKey?: string

  constructor(message: string, options: ErrorOptions = {}) {
    super(message)
    this.name = 'AppError'
    this.statusCode = options.statusCode || 500
    this.fatal = options.fatal || false
    this.cause = options.cause
    this.data = options.data
    this.groupKey = options.groupKey || `${this.name}-${this.statusCode}`
  }
}

export const useErrorHandler = () => {
  const { toast } = useToast()
  const router = useRouter()
  const isHandlingError = ref(false)

  const showToastNotification = (error: AppError) => {
    toast({
      title: `Error ${error.statusCode}`,
      description: error.message,
      variant: 'destructive',
    })
  }

  const showSystemNotification = async (error: AppError) => {
    try {
      let permissionGranted = await isPermissionGranted()
      if (!permissionGranted) {
        const permission = await requestPermission()
        permissionGranted = permission === 'granted'
      }

      if (permissionGranted) {
        sendNotification({
          title: `Fatal Error ${error.statusCode}`,
          body: error.message
        })
      }
    } catch (e) {
      await debug('Failed to send system notification: ' + e)
    }
  }

  const handleError = async (error: unknown, options: ErrorOptions = {}) => {
    if (isHandlingError.value) {
      await logError(`Additional error while handling error: ${error}`)
      return
    }

    try {
      isHandlingError.value = true

      const appError = error instanceof AppError
          ? error
          : new AppError(
              error instanceof Error ? error.message : String(error),
              options
          )

      // Log del error
      await logError('Error details: ' + JSON.stringify({
        message: appError.message,
        statusCode: appError.statusCode,
        groupKey: appError.groupKey,
        data: appError.data
      }, null, 2))

      // Solo mostramos notificaciones si no es silencioso
      if (!options.silent) {
        showToastNotification(appError)

        if (appError.fatal) {
          await showSystemNotification(appError)

          // Solo redirigimos si no estamos ya en la página de error
          if (!router.currentRoute.value.path.startsWith('/error')) {
            await router.push({
              path: '/error',
              query: {
                groupKey: appError.groupKey,
                message: appError.message,
                fatal: 'true',
                from: router.currentRoute.value.fullPath
              }
            })
          }
        }
      }
      
      if (!options.silent) {
        throw appError
      }
    } finally {
      isHandlingError.value = false
    }
  }

  return {
    handleError,
    createAppError: (message: string, options: ErrorOptions = {}) =>
        new AppError(message, options)
  }
}