<script setup lang="ts">
import { API_ROUTES } from '~/config/api'
import OAuthProfileForm from "~/components/auth/OAuthProfileForm.vue"
import { createFormDataFromProfile } from '~/utils/form'
import {
  transformSessionToProfileRequest,
  type UpdateProfileRequest,
  type UserResponse
} from "~/interfaces"

const router = useRouter()
const { data: session, getSession, token, signOut } = useAuth()
const loading = ref(false)
const error = ref('')

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

const handleSubmit = async (formData: UpdateProfileRequest) => {
  loading.value = true
  error.value = ''

  try {
    const currentToken = token.value
    if (!currentToken) {
      throw new Error('No se encontró token de autenticación')
    }

    const headers: HeadersInit = {
      'Authorization': currentToken
    }

    if (needsPassword.value && formData.password) {
      await $fetch(`${API_ROUTES.USER}/change-password`, {
        method: 'PATCH',
        headers,
        body: {
          current_password: null,
          new_password: formData.password,
          confirm_password: formData.confirm_password
        }
      })
    }

    const form = createFormDataFromProfile(formData)

    const response = await $fetch<UserResponse>(API_ROUTES.USER_INFO, {
      method: 'PATCH',
      headers,
      body: form
    })

    console.log('Profile updated:', response)
    await getSession()

    if (response?.id) {
      await router.push('/')
    } else {
      throw new Error('No se recibió respuesta válida del servidor')
    }
  } catch (err) {
    console.error('Error updating profile:', err)
    error.value = err instanceof Error ? err.message : 'Error actualizando el perfil'

    if (err.response?.status === 401) {
      await signOut()
    }
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try {
    if (!session.value) {
      await getSession()
    }

    if (!needsProfileCompletion.value && !needsPassword.value) {
      await router.push('/')
    }
  } catch (err) {
    console.error('Session error:', err)
    await signOut()
  }
})
</script>

<template>
  <div class="min-h-screen flex items-center justify-center p-4">
    <template v-if="needsProfileCompletion || needsPassword">
      <OAuthProfileForm
          :loading="loading"
          :initial-data="initialData"
          :needs-password="needsPassword"
          @submit="handleSubmit"
      />
    </template>
    <Alert v-if="error" variant="destructive" class="mt-4">
      <AlertTitle>Error</AlertTitle>
      <AlertDescription>{{ error }}</AlertDescription>
    </Alert>
  </div>
</template>