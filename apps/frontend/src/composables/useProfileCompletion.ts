import {API_ROUTES} from '@cortex/shared/config/api'
import {createFormDataFromProfile} from '~/utils/form'
import {AppError} from '@cortex/shared/types'
import {
  transformSessionToProfileRequest,
  type UpdateProfileRequest,
  type UserResponse
} from "~/interfaces"
import { useWebErrorHandler } from '@cortex/shared/composables/front/useWebErrorHandler'

interface PasswordUpdateData {
  password: string
  confirm_password: string
}

interface ApiError {
  response?: {
    status?: number
  }
}

export function useProfileCompletion() {
  const router = useRouter()
  const {data: session, getSession, token, signOut} = useAuth()
  const errorHandler = useWebErrorHandler()
  const loading = ref(false)

  const needsProfileCompletion = computed(() => {
    if (!session.value) return false
    return !session.value.username ||
        !session.value.first_name ||
        !session.value.last_name ||
        !session.value.date_of_birth ||
        !session.value.country_code ||
        !session.value.gender
  })

  const needsPassword = computed(() => {
    if (!session.value) return false
    return !session.value.has_password
  })

  const initialData = computed((): Partial<UpdateProfileRequest> => {
    return transformSessionToProfileRequest(session.value)
  })

  const updatePassword = async (
      formData: PasswordUpdateData,
      currentToken: string
  ) => {
    try {
      await $fetch(`${API_ROUTES.USER}/change-password`, {
        method: 'PATCH',
        headers: {
          'Authorization': currentToken
        },
        body: {
          current_password: null,
          new_password: formData.password,
          confirm_password: formData.confirm_password
        }
      })
    } catch (err) {
      const apiError = err as ApiError
      throw new AppError('Error al actualizar la contraseña', {
        statusCode: apiError.response?.status || 500,
        data: {
          context: 'updatePassword',
          originalError: err
        }
      })
    }
  }

  const updateProfile = async (formData: UpdateProfileRequest) => {
    loading.value = true

    try {
      const currentToken = token.value
      if (!currentToken) {
        throw new AppError('No se encontró token de autenticación', {
          statusCode: 401,
          data: {context: 'tokenValidation'}
        })
      }

      const headers: HeadersInit = {
        'Authorization': currentToken
      }

      // Update password if needed
      if (needsPassword.value && formData.password && formData.confirm_password) {
        const passwordData: PasswordUpdateData = {
          password: formData.password,
          confirm_password: formData.confirm_password
        }
        await updatePassword(passwordData, currentToken)
      }

      // Update profile
      const form = createFormDataFromProfile(formData)
      for (const pair of form.entries()) {
        console.log(`${pair[0]}: ${pair[1]}`)
      }
      const response = await $fetch<UserResponse>(API_ROUTES.USER_INFO, {
        method: 'PATCH',
        headers,
        body: form
      })

      if (!response?.id) {
        throw new AppError('No se recibió respuesta válida del servidor', {
          statusCode: 500,
          data: {context: 'profileUpdate', response}
        })
      }
      await getSession()
      await router.push('/')
      return response

    } catch (err) {
      const apiError = err as ApiError
      const isAuthError = apiError.response?.status === 401
      const errorOptions = {
        statusCode: apiError.response?.status || 500,
        fatal: false,
        data: {
          context: 'updateProfile',
          formData: {
            ...formData,
            password: undefined,
            confirm_password: undefined
          },
          originalError: err
        }
      }

      if (isAuthError) {
        await signOut()
        errorOptions.fatal = true
      }

      await errorHandler.handleError(
          err instanceof AppError ? err : new AppError(
              'Error actualizando el perfil',
              errorOptions
          )
      )

      throw err
    } finally {
      loading.value = false
    }
  }

  const checkSession = async () => {
    try {
      if (!session.value) {
        await getSession()
      }

      if (!needsProfileCompletion.value && !needsPassword.value) {
        await router.push('/')
      }
    } catch (err) {
      const apiError = err as ApiError
      await errorHandler.handleError(new AppError(
          'Error al verificar la sesión',
          {
            statusCode: apiError.response?.status || 500,
            fatal: true,
            data: {
              context: 'checkSession',
              originalError: err
            }
          }
      ))
      await signOut()
    }
  }

  return {
    loading,
    session,
    needsProfileCompletion,
    needsPassword,
    initialData,
    updateProfile,
    checkSession
  }
}