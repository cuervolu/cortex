import {storeToRefs} from 'pinia'
import {useAuthStore} from '~/stores/'

export default defineNuxtRouteMiddleware(async (to) => {
  const authStore = useAuthStore()
  const {authenticated, user} = storeToRefs(authStore)
  const token = useCookie('token')

  if (token.value) {
    authenticated.value = true
    if (!user.value) {
      // Fetch user info if we have a token but no user data
      await authStore.fetchUser()
    }
  }

  // If token exists and user is trying to access login page, redirect to home
  if (token.value && to?.name === 'login') {
    return navigateTo('/')
  }

  // If no token and trying to access a protected route, redirect to log in
  if (!token.value && to?.name !== 'login') {
    abortNavigation()
    return navigateTo('/auth/login')
  }

  // If token exists, but we're not sure if it's valid, we can optionally verify it here
  if (token.value && !authenticated.value) {
    // This situation should be rare now that we're fetching the user above
    await authStore.fetchUser()
    if (!authenticated.value) {
      // If fetching user failed, clear token and redirect to log in
      await authStore.logout()
      abortNavigation()
      return navigateTo('/auth/login')
    }
  }
})