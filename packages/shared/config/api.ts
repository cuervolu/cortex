export const API_BASE_URL = process.env.NUXT_PUBLIC_API_BASE_URL || 'http://localhost:8088/api/v1'

export const API_ROUTES = {
  USER_INFO: `${API_BASE_URL}/user/me`,
  LOGIN: `${API_BASE_URL}/auth/authenticate`,
  REGISTER: `${API_BASE_URL}/auth/register`,
  OAUTH_BASE: `${API_BASE_URL}/oauth2/authorization`,
  ACTIVATE_ACCOUNT: `${API_BASE_URL}/auth/activate-account`,
  ROADMAPS: `${API_BASE_URL}/education/roadmap`,
  CORUSES: `${API_BASE_URL}/education/course`,
  MODULES: `${API_BASE_URL}/education/module`,
  LESSONS: `${API_BASE_URL}/education/lesson`,
  EXERCISES: `${API_BASE_URL}/exercises`,
  USER: `${API_BASE_URL}/user`,
  CHANGE_PASSWORD: `${API_BASE_URL}/user/change-password`,
  FORGOT_PASSWORD: `${API_BASE_URL}/auth/forgot-password`,
  RESET_PASSWORD: `${API_BASE_URL}/auth/reset-password`,
}
