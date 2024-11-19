import { API_ROUTES } from '@cortex/shared/config/api'
import { createFormDataFromProfile } from '~/utils/form'
import { AppError } from '@cortex/shared/types'
import type { UpdateProfileRequest, UserResponse } from "~/interfaces"
import { useWebErrorHandler } from "~/composables/useWebErrorHandler"

interface ApiError {
  response?: {
    status?: number
  }
}

export function useUser() {
  const { data: session, getSession, token } = useAuth()
  const errorHandler = useWebErrorHandler()
  const loading = ref(false)
  const user = ref<UserResponse | null>(null)

  // Computed to transform session data to profile request format
  const currentUserData = computed((): Partial<UpdateProfileRequest> => {
    return session.value ? {
      first_name: session.value.first_name,
      last_name: session.value.last_name,
      username: session.value.username,
      email: session.value.email,
      date_of_birth: session.value.date_of_birth,
      country_code: session.value.country_code,
      gender: session.value.gender
    } : {}
  })

  // Get current user data
  const getCurrentUser = async () => {
    try {
      const currentToken = token.value
      if (!currentToken) {
        throw new AppError('No se encontró token de autenticación', {
          statusCode: 401,
          data: { context: 'getCurrentUser' }
        })
      }

      const response = await $fetch<UserResponse>(API_ROUTES.USER_INFO, {
        headers: {
          'Authorization': currentToken
        }
      })

      if (!response?.id) {
        throw new AppError('No se recibió respuesta válida del servidor', {
          statusCode: 500,
          data: { context: 'getCurrentUser', response }
        })
      }

      user.value = response
      return response

    } catch (err) {
      const apiError = err as ApiError
      await errorHandler.handleError(
          err instanceof AppError ? err : new AppError(
              'Error al obtener datos del usuario',
              {
                statusCode: apiError.response?.status || 500,
                data: {
                  context: 'getCurrentUser',
                  originalError: err
                }
              }
          )
      )
      throw err
    }
  }

  // Update user profile
  const updateProfile = async (formData: UpdateProfileRequest) => {
    loading.value = true

    try {
      const currentToken = token.value
      if (!currentToken) {
        throw new AppError('No se encontró token de autenticación', {
          statusCode: 401,
          data: { context: 'updateProfile' }
        })
      }

      // Create and validate form data
      const form = createFormDataFromProfile(formData)

      // Send update request
      const response = await $fetch<UserResponse>(API_ROUTES.USER_INFO, {
        method: 'PATCH',
        headers: {
          'Authorization': currentToken
        },
        body: form
      })

      if (!response?.id) {
        throw new AppError('No se recibió respuesta válida del servidor', {
          statusCode: 500,
          data: { context: 'updateProfile', response }
        })
      }

      // Update local session data
      await getSession()
      user.value = response
      return response

    } catch (err) {
      const apiError = err as ApiError
      await errorHandler.handleError(
          err instanceof AppError ? err : new AppError(
              'Error actualizando el perfil',
              {
                statusCode: apiError.response?.status || 500,
                data: {
                  context: 'updateProfile',
                  formData,
                  originalError: err
                }
              }
          )
      )
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    loading,
    user,
    currentUserData,
    getCurrentUser,
    updateProfile
  }
}