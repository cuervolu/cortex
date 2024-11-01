export default defineNuxtPlugin((nuxtApp) => {
  const errorHandler = useErrorHandler()

  // Global Vue error handler
  nuxtApp.vueApp.config.errorHandler = async (error, instance, info) => {
    // Determine if the error is fatal based on the error type and context
    const isFatal = error instanceof Error && (
        error.message.includes('invoke') ||
        error.message.includes('IPC') ||
        info?.includes('render') || // Error durante el renderizado
        info?.includes('lifecycle') // Error en lifecycle hooks
    )

    // Group similar errors by adding a groupKey
    const groupKey = `${instance?.$options?.name || 'anonymous'}-${info || 'unknown'}`

    await errorHandler.handleError(error, {
      fatal: isFatal,
      groupKey,
      data: {
        component: instance?.$options?.name,
        errorInfo: info
      }
    })
  }

  // Global promise error handler
  window.addEventListener('unhandledrejection', async (event) => {
    await errorHandler.handleError(event.reason, {
      groupKey: 'unhandled-promise-rejection'
    })
  })
})