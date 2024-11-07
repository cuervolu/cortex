import {debug} from "@tauri-apps/plugin-log"
import {useDesktopErrorHandler} from "~/composables/useDesktopErrorHandler"
import {AppError} from "@cortex/shared/types";

export default defineNuxtPlugin((nuxtApp) => {
  const errorHandler = useDesktopErrorHandler()

  /**
   * Determines if an error is a Tauri-specific fatal error
   * @param error - The error object to evaluate
   * @param info - Additional Vue error information
   * @returns boolean indicating if the error is fatal
   */
  const isFatalError = (error: unknown, info?: string): boolean => {
    if (error instanceof Error) {
      const conditions = [
        error.message.includes('invoke'),  // Tauri invocation errors
        error.message.includes('IPC'),     // IPC communication errors
        info?.includes('render'),          // Vue rendering errors
        info?.includes('setup'),           // Vue component setup errors
        error.message.includes('fatal'),   // Explicitly marked fatal errors
      ]
      return conditions.some(condition => condition)
    }
    return false
  }

  /**
   * Global Vue error handler
   */
  nuxtApp.vueApp.config.errorHandler = async (error, instance, info) => {
    if (error instanceof AppError && error.isHandled) {
      return
    }
    const errorMessage = error instanceof Error ? error.message : String(error)

    await debug(`Vue error intercepted: ${errorMessage}`, {
      keyValues: {
        component: instance?.$options?.name || 'unknown',
        info: info || 'no info provided',
        errorType: error instanceof Error ? error.constructor.name : typeof error
      }
    })

    const fatal = isFatalError(error, info)

    await errorHandler.handleError(error, {
      fatal,
      statusCode: fatal ? 500 : 400,
      data: {
        component: instance?.$options?.name,
        errorInfo: info,
        isTauriError: error instanceof Error && (
            error.message.includes('invoke') ||
            error.message.includes('IPC')
        ),
        vueInfo: {
          lifecycle: info,
          componentName: instance?.$options?.name,
          componentPath: instance?.$options?.__file,
        }
      }
    })
  }

  if (import.meta.client) {
    /**
     * Handle unhandled promise rejections, which includes Tauri invoke errors
     */
    window.addEventListener('unhandledrejection', async (event) => {
      if (event.reason instanceof AppError && event.reason.isHandled) {
        return
      }
      const errorMessage = event.reason?.message || 'Unknown rejection reason'

      await debug(`Unhandled rejection intercepted: ${errorMessage}`, {
        keyValues: {
          type: 'unhandledRejection',
          errorType: event.reason?.constructor.name || 'Unknown',
          stack: event.reason?.stack || 'No stack trace available'
        }
      })

      const isTauriError = event.reason?.message?.includes('invoke') ||
          event.reason?.message?.includes('IPC')

      await errorHandler.handleError(event.reason, {
        fatal: true,
        statusCode: 500,
        data: {
          type: 'unhandledRejection',
          isTauriError,
          promise: {
            state: 'rejected',
            reason: event.reason
          }
        }
      })
    })
  }

  // Provide the error handler to be used throughout the app
  return {
    provide: {
      errorHandler
    }
  }
})