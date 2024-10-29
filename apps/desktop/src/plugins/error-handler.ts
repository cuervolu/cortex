export default defineNuxtPlugin((nuxtApp) => {
  nuxtApp.vueApp.config.errorHandler = async (error) => {
    const {handleError} = useErrorHandler()

    const isFatal = error instanceof Error &&
        (error.message.includes('invoke') || error.message.includes('IPC'))

    await handleError(error, {
      fatal: isFatal,
      notify: isFatal // If fatal, show system notification
    })
  }

  window.addEventListener('unhandledrejection', async (event) => {
    const {handleError} = useErrorHandler()
    await handleError(event.reason, {
      notify: true // Show system notification for unhandled rejections
    })
  })
})