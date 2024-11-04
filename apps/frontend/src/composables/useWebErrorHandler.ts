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
      // Always show toast for both fatal and non-fatal errors
      toast({
        title: `Error ${appError.statusCode}`,
        description: appError.message,
        variant: 'destructive',
      })
    }
  }

  return {
    handleError
  }
}