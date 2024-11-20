import {useForm} from 'vee-validate'
import {toTypedSchema} from '@vee-validate/zod'
import * as z from 'zod'
import type {UpdateProfileRequest} from '~/interfaces'
import {useToast} from "@cortex/shared/components/ui/toast"
import type {UserResponse} from "~/interfaces";
import {countries} from 'countries-list'
import {API_ROUTES} from "@cortex/shared/config/api";

export const useProfileForm = () => {
  const {handleError} = useWebErrorHandler()
  const {toast} = useToast()
  const isSubmitting = ref(false)

  const formSchema = toTypedSchema(z.object({
    username: z.string().min(3).max(50).optional(),
    first_name: z.string().min(2).max(50).optional(),
    last_name: z.string().min(2).max(50).optional(),
    email: z.string().email().optional(),
    date_of_birth: z.date().optional().nullable(),
    country_code: z.string().min(2).max(3).optional(),
    gender: z.enum(['MALE', 'FEMALE', 'OTHER', 'NON_BINARY', 'PREFER_NOT_TO_SAY']).optional(),
    avatar: z.any().optional()
  }))

  const form = useForm({
    validationSchema: formSchema,
    initialValues: {
      username: '',
      first_name: '',
      last_name: '',
      email: '',
      date_of_birth: null,
      country_code: '',
      gender: 'PREFER_NOT_TO_SAY',
      avatar: null
    }
  })

  const resetForm = (data: UpdateProfileRequest) => {
    console.log('Resetting form with data:', data) // Para debug
    form.resetForm({
      values: {
        username: data.username || '',
        first_name: data.first_name || '',
        last_name: data.last_name || '',
        email: data.email || '',
        date_of_birth: data.date_of_birth ? new Date(data.date_of_birth) : null,
        country_code: data.country_code || '',
        gender: data.gender || 'PREFER_NOT_TO_SAY',
        avatar: null
      }
    })
  }

  const onSubmit = form.handleSubmit(async (values) => {
    try {
      isSubmitting.value = true
      const formData = new FormData()

      Object.entries(values).forEach(([key, value]) => {
        if (value !== undefined && value !== null) {
          if (key === 'date_of_birth' && value instanceof Date) {
            formData.append(key, value.toISOString().split('T')[0])
          } else if (key === 'avatar' && value instanceof File) {
            formData.append('avatar', value, value.name)
          } else if (key !== 'avatar') {
            formData.append(key, String(value))
          }
        }
      })

      const { token} = useAuth();
      console.log('Form data entries:', Array.from(formData.entries()))
      const response = await $fetch<UserResponse>(API_ROUTES.USER_INFO, {
        method: 'PATCH',
       headers: {
          'Authorization': token.value
       },
        body: formData,
      })

      toast({
        title: "Perfil actualizado",
        description: "Los cambios han sido guardados correctamente",
      })

      return response
    } catch (error) {
      await handleError(error)
      throw error
    } finally {
      isSubmitting.value = false
    }
  })


  const countryOptions = computed(() =>
      Object.entries(countries).map(([code, country]) => ({
        value: code,
        label: country.name,
        flag: `https://flagcdn.com/w20/${code.toLowerCase()}.png`
      }))
  )

  return {
    form,
    onSubmit,
    resetForm,
    countryOptions,
    isSubmitting
  }
}