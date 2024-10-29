export default defineNuxtPlugin((nuxtApp) => {
  nuxtApp.vueApp.config.errorHandler = (error) => {
    const { handleError } = useErrorHandler()
    handleError(error)
  }

  window.addEventListener('unhandledrejection', (event) => {
    const { handleError } = useErrorHandler()
    handleError(event.reason)
  })
})