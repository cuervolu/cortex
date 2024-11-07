export interface ErrorOptions {
  statusCode?: number
  fatal?: boolean
  data?: Record<string, unknown>
  silent?: boolean
  isHandled?: boolean
}

export class AppError extends Error {
  statusCode: number
  fatal: boolean
  data?: Record<string, unknown>
  isHandled: boolean

  constructor(message: string, options: ErrorOptions = {}) {
    super(message)
    this.name = 'AppError'
    this.statusCode = options.statusCode || 500
    this.fatal = options.fatal || false
    this.data = options.data
    this.isHandled = options.isHandled || false
  }
}

export interface ErrorHandler {
  handleError: (error: unknown, options?: ErrorOptions) => Promise<void>
  showSystemNotification?: (message: string, title: string) => Promise<void>
}

export interface ErrorHandlerOptions {
  toast: {
    title: string
    description: string
    variant?: 'default' | 'destructive'
  }
  notification?: {
    show: boolean
    title?: string
    message?: string
  }
}
