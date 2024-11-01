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
  groupKey?: string // Para agrupar errores similares
}

export class AppError extends Error {
  statusCode: number
  fatal: boolean
  override cause?: Error | unknown
  data?: Record<string, unknown>
  groupKey?: string
  options: ErrorOptions

  constructor(message: string, options: ErrorOptions = {}) {
    super(message)
    this.name = 'AppError'
    this.statusCode = options.statusCode || 500
    this.fatal = options.fatal || false
    this.cause = options.cause
    this.data = options.data
    this.groupKey = options.groupKey || `${this.name}-${this.statusCode}`
    this.options = options // Guardamos las opciones completas
  }
}
interface ErrorGroup {
  count: number
  firstSeen: number
  lastSeen: number
  message: string
  errors: AppError[]
}

export const useErrorHandler = () => {
  const { toast } = useToast()
  const errorGroups = new Map<string, ErrorGroup>()
  const notificationQueue = ref<AppError[]>([])
  const isProcessingQueue = ref(false)

  // Rate limiting configuration
  const RATE_LIMIT = {
    toast: 5000,      // 5 seconds between similar toasts
    system: 300000,   // 5 minutes between similar system notifications
    log: 1000         // 1 second between similar logs
  }

  const groupError = (error: AppError): ErrorGroup => {
    const group = errorGroups.get(error.groupKey!) || {
      count: 0,
      firstSeen: Date.now(),
      lastSeen: Date.now(),
      message: error.message,
      errors: []
    }

    group.count++
    group.lastSeen = Date.now()
    group.errors.push(error)

    // Limit the number of stored errors to prevent memory issues
    if (group.errors.length > 10) {
      group.errors.shift()
    }

    errorGroups.set(error.groupKey!, group)
    return group
  }

  const shouldNotify = (error: AppError, type: 'toast' | 'system' | 'log'): boolean => {
    const group = errorGroups.get(error.groupKey!)
    if (!group) return true

    const timeSinceLastNotification = Date.now() - group.lastSeen
    return timeSinceLastNotification > RATE_LIMIT[type]
  }

  const processNotificationQueue = async () => {
    if (isProcessingQueue.value || notificationQueue.value.length === 0) return

    isProcessingQueue.value = true

    try {
      const error = notificationQueue.value[0]
      const group = groupError(error)

      // Log with rate limiting
      if (shouldNotify(error, 'log')) {
        const errorDetails = {
          message: error.message,
          statusCode: error.statusCode,
          groupKey: error.groupKey,
          occurrences: group.count,
          data: error.data
        }
        await logError('Error details: ' + JSON.stringify(errorDetails, null, 2))
      }

      // Show notifications based on error type and rate limiting
      if (!error.options?.silent) {
        if (error.fatal && shouldNotify(error, 'system')) {
          await showSystemNotification(error, group)
        } else if (shouldNotify(error, 'toast')) {
          showToastNotification(error, group)
        }
      }

      // Handle fatal errors
      if (error.fatal) {
        const router = useRouter()
        await router.push({
          path: '/error',
          query: {
            groupKey: error.groupKey,
            count: group.count.toString(),
            message: error.message,
            fatal: 'true',
            from: router.currentRoute.value.fullPath
          }
        })
      }

    } finally {
      notificationQueue.value.shift()
      isProcessingQueue.value = false

      // Process next error in queue
      if (notificationQueue.value.length > 0) {
        setTimeout(() => processNotificationQueue(), 100)
      }
    }
  }

  const showToastNotification = (error: AppError, group: ErrorGroup) => {
    const count = group.count > 1 ? ` (${group.count}×)` : ''
    toast({
      title: `Error ${error.statusCode}${count}`,
      description: error.message,
      variant: 'destructive',
    })
  }

  const showSystemNotification = async (error: AppError, group: ErrorGroup) => {
    try {
      let permissionGranted = await isPermissionGranted()
      if (!permissionGranted) {
        const permission = await requestPermission()
        permissionGranted = permission === 'granted'
      }

      if (permissionGranted) {
        const count = group.count > 1 ? ` (${group.count}×)` : ''
        sendNotification({
          title: `Fatal Error ${error.statusCode}${count}`,
          body: error.message
        })
      }
    } catch (e) {
      await debug('Failed to send system notification: ' + e)
    }
  }

  const handleError = async (error: unknown, options: ErrorOptions = {}) => {
    const appError = error instanceof AppError
        ? error
        : new AppError(
            error instanceof Error ? error.message : String(error),
            options
        )

    // Add to notification queue
    notificationQueue.value.push(appError)
    await processNotificationQueue()

    if (!options.silent) {
      throw appError // Re-throw for component error boundaries
    }
  }

  return {
    handleError,
    createAppError: (message: string, options: ErrorOptions = {}) => new AppError(message, options)
  }
}