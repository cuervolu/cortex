import type { ErrorHandler, ErrorOptions } from '@cortex/shared/types'
import { AppError } from '@cortex/shared/types'
import { useToast } from "@cortex/shared/components/ui/toast"

export const useWebErrorHandler = (): ErrorHandler => {
  const { toast } = useToast()

  const handleError = async (err: unknown, options: ErrorOptions = {}) => {
    if (err instanceof AppError && err.isHandled) {
      return
    }

    const appError = err instanceof AppError
        ? err
        : new AppError(err instanceof Error ? err.message : String(err), options)

    appError.isHandled = true

    // Log error details to console in development
    if (import.meta.dev) {
      console.error(`Error details:`, {
        message: appError.message,
        statusCode: appError.statusCode,
        data: appError.data || {},
        stack: appError.stack || 'No stack trace available'
      })
    }

    if (!options.silent) {
      // Determine toast variant based on error type
      let toastVariant: 'destructive' | 'warning' | 'info' | 'unauthorized' = 'destructive'
      
      switch (true) {
        case appError.statusCode >= 400 && appError.statusCode < 500:
          toastVariant = appError.statusCode === 401 || appError.statusCode === 403 
            ? 'unauthorized' 
            : 'warning'
          break
        case appError.statusCode >= 500:
          toastVariant = 'destructive'
          break
        default:
          toastVariant = 'info'
      }

      // Always show toast for both fatal and non-fatal errors
      toast({
        title: `Error ${appError.statusCode}`,
        description: appError.message,
        variant: toastVariant,
      })
    }
  }

  return {
    handleError
  }
}