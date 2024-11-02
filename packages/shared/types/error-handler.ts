export interface ErrorOptions {
  statusCode?: number
  fatal?: boolean
  cause?: Error | unknown
  data?: Record<string, unknown>
  silent?: boolean
  groupKey?: string
}

export interface ErrorHandler {
  handleError(error: unknown, options?: ErrorOptions): Promise<void>;
  createAppError(message: string, options?: ErrorOptions): Error;
}
