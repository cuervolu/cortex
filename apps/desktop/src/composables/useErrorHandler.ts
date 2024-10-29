import {
  isPermissionGranted,
  requestPermission,
  sendNotification
} from '@tauri-apps/plugin-notification'
import {error as logError, debug} from '@tauri-apps/plugin-log'
import {useToast} from "@cortex/shared/components/ui/toast";

export interface ErrorOptions {
  statusCode?: number
  fatal?: boolean
  cause?: Error | unknown
  data?: Record<string, unknown>
  silent?: boolean
}

export class AppError extends Error {
  statusCode: number
  fatal: boolean
  override cause?: Error | unknown
  data?: Record<string, unknown>

  constructor(message: string, options: ErrorOptions = {}) {
    super(message)
    this.name = 'AppError'
    this.statusCode = options.statusCode || 500
    this.fatal = options.fatal || false
    this.cause = options.cause
    this.data = options.data
  }
}

export const useErrorHandler = () => {
  const {toast} = useToast()
  
  let lastFatalNotification = {
    message: '',
    timestamp: 0
  }
  
  const errorNotifications = new Map<string, {
    message: string,
    timestamp: number,
    count: number
  }>()

  const shouldShowToast = (error: AppError) => {
    const now = Date.now();
    const errorKey = `${error.name}-${error.statusCode}-${error.message}`;
    const lastError = errorNotifications.get(errorKey);

    if (lastError) {
      if (now - lastError.timestamp < 5000) { 
        lastError.count++;
        errorNotifications.set(errorKey, lastError);
        return false;
      }
    }

    errorNotifications.set(errorKey, {
      message: error.message,
      timestamp: now,
      count: 1
    });
    return true;
  };


  const shouldShowSystemNotification = (message: string) => {
    const now = Date.now()
    if (message === lastFatalNotification.message &&
        now - lastFatalNotification.timestamp < 300000) { 
      return false
    }
    lastFatalNotification = {message, timestamp: now}
    return true
  }

  const showSystemNotification = async (error: AppError) => {
    try {
      if (!shouldShowSystemNotification(error.message)) {
        return
      }

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
    const appError = error instanceof AppError
        ? error
        : new AppError(error instanceof Error ? error.message : String(error), options)

    const errorDetails = {
      message: appError.message,
      statusCode: appError.statusCode,
      cause: appError.cause,
      data: appError.data,
      stack: appError.stack
    }
    await logError('Error details: ' + JSON.stringify(errorDetails, null, 2))

    if (!options.silent) {
      if (appError.fatal) {
        await showSystemNotification(appError)
      } else if (shouldShowToast(appError)) {
        toast({
          title: `Error ${appError.statusCode}`,
          description: appError.message,
          variant: 'destructive',
        })
      }
    }
    
    if (appError.fatal) {
      const router = useRouter()
      await router.push({
        path: '/error',
        query: {
          name: appError.name,
          statusCode: appError.statusCode,
          message: appError.message,
          stack: appError.stack,
          fatal: 'true',
          from: router.currentRoute.value.fullPath
        }
      })
      return
    }

    throw appError
  }
  
  const createAppError = (message: string, options: ErrorOptions = {}) => {
    return new AppError(message, options)
  }

  return {
    handleError,
    createAppError
  }
}