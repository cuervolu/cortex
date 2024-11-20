export default defineNuxtRouteMiddleware(async (to) => {
  const { status, data } = useAuth()

  if (status.value !== 'authenticated') {
    return navigateTo('/auth/login')
  }

  const isAdminRoute = to.path.startsWith('/admin')

  if (isAdminRoute) {
    const hasAdminAccess = data.value?.roles.some(
        role => role === 'ADMIN' || role === 'MODERATOR'
    )

    if (!hasAdminAccess) {
      return navigateTo('/')
    }
  }
})