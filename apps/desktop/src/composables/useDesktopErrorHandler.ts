import { isPermissionGranted, requestPermission, sendNotification } from '@tauri-apps/plugin-notification'
import { error as logError, debug } from "@tauri-apps/plugin-log"
import type { ErrorHandler, ErrorOptions } from '@cortex/shared/types'
import { AppError } from '@cortex/shared/types'
import { useToast } from "@cortex/shared/components/ui/toast"

export const useDesktopErrorHandler = (): ErrorHandler => {
  const { toast } = useToast()

  const showSystemNotification = async (message: string, title: string) => {
    try {
      let permissionGranted = await isPermissionGranted()
      if (!permissionGranted) {
        const permission = await requestPermission()
        permissionGranted = permission === 'granted'
      }
      if (permissionGranted) {
        sendNotification({ title, body: message })
        await debug('System notification sent successfully')
      }
    } catch (e) {
      await logError(`Failed to send system notification: ${e}`)
    }
  }

  const handleError = async (err: unknown, options: ErrorOptions = {}) => {

    if (err instanceof AppError && err.isHandled) {
      return
    }
    
    const appError = err instanceof AppError
        ? err
        : new AppError(err instanceof Error ? err.message : String(err), options)
    appError.isHandled = true
    await logError(`Error details: ${appError.message}`, {
      keyValues: {
        statusCode: appError.statusCode.toString(),
        data: JSON.stringify(appError.data || {}),
        stack: appError.stack || 'No stack trace available'
      }
    })


    if (!options.silent) {
      if (!appError.fatal) {
        toast({
          title: `Error ${appError.statusCode}`,
          description: appError.message,
          variant: 'destructive',
        })
        return 
      }

      // Only for fatal errors
      await showSystemNotification(
          appError.message,
          `Error ${appError.statusCode}`
      )
      
      throw createError({
        statusCode: appError.statusCode,
        message: appError.message,
        fatal: true,
        data: appError.data,
        stack: appError.stack,
      })
    }
  }

  return {
    handleError,
    showSystemNotification
  }
}