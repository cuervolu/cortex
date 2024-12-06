import { useWebErrorHandler } from "@cortex/shared/composables/front/useWebErrorHandler"
import { AppError } from "@cortex/shared/types"

export default defineNuxtPlugin((nuxtApp) => {
  const errorHandler = useWebErrorHandler()

  /**
   * Determines if an error is a fatal error in the web context
   * @param error - The error object to evaluate
   * @param info - Additional Vue error information
   * @returns boolean indicating if the error is fatal
   */
  const isFatalError = (error: unknown, info?: string): boolean => {
    if (error instanceof Error) {
      const conditions = [
        error.message.includes('fetch'),   // Network/API errors
        error.message.includes('auth'),    // Authentication errors
        info?.includes('render'),          // Vue rendering errors
        info?.includes('setup'),           // Vue component setup errors
        error.message.includes('fatal'),   // Explicitly marked fatal errors
        true,        // Type errors that could break the app
        error.message.includes('undefined is not a function'), // Common fatal JS errors
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

    if (import.meta.dev) {
      console.debug(`Vue error intercepted: ${errorMessage}`, {
        component: instance?.$options?.name || 'unknown',
        info: info || 'no info provided',
        errorType: error instanceof Error ? error.constructor.name : typeof error
      })
    }

    const fatal = isFatalError(error, info)

    await errorHandler.handleError(error, {
      fatal,
      statusCode: fatal ? 500 : 400,
      data: {
        component: instance?.$options?.name,
        errorInfo: info,
        isNetworkError: error instanceof Error && (
            error.message.includes('fetch') ||
            error.message.includes('network')
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
     * Handle unhandled promise rejections
     */
    window.addEventListener('unhandledrejection', async (event) => {
      if (event.reason instanceof AppError && event.reason.isHandled) {
        return
      }

      const errorMessage = event.reason?.message || 'Unknown rejection reason'

      if (import.meta.dev) {
        console.debug(`Unhandled rejection intercepted: ${errorMessage}`, {
          type: 'unhandledRejection',
          errorType: event.reason?.constructor.name || 'Unknown',
          stack: event.reason?.stack || 'No stack trace available'
        })
      }

      await errorHandler.handleError(event.reason, {
        fatal: true,
        statusCode: 500,
        data: {
          type: 'unhandledRejection',
          promise: {
            state: 'rejected',
            reason: event.reason
          }
        }
      })
    })

    /**
     * Handle general JavaScript errors
     */
    window.addEventListener('error', async (event) => {
      if (event.error instanceof AppError && event.error.isHandled) {
        return
      }

      if (import.meta.dev) {
        console.debug(`Global error intercepted:`, {
          message: event.message,
          filename: event.filename,
          lineno: event.lineno,
          colno: event.colno,
          error: event.error
        })
      }

      await errorHandler.handleError(event.error || event.message, {
        fatal: true,
        statusCode: 500,
        data: {
          type: 'globalError',
          source: {
            filename: event.filename,
            line: event.lineno,
            column: event.colno
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