<script setup lang="ts">
import { useForm } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import * as z from 'zod'
import { Loader2 } from 'lucide-vue-next'
import { countries } from 'countries-list'
import type { UpdateProfileRequest } from "~/interfaces"


interface Props {
  loading?: boolean
  profileData: UpdateProfileRequest | null
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  profileData: null
})

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'save', data: UpdateProfileRequest): void
}>()

const avatarPreview = ref<string | null>(null)

// Función auxiliar para formatear la fecha
const formatDate = (date: string | Date | undefined) => {
  if (!date) return undefined
  const d = new Date(date)
  if (isNaN(d.getTime())) return undefined
  return d
}

// Esquema de validación
const formSchema = toTypedSchema(z.object({
  username: z.string().min(3, 'El nombre de usuario debe tener al menos 3 caracteres').max(50),
  firstName: z.string().min(2, 'El nombre debe tener al menos 2 caracteres').max(50),
  lastName: z.string().min(2, 'El apellido debe tener al menos 2 caracteres').max(50),
  email: z.string().email('Correo electrónico inválido'),
  dateOfBirth: z.date().nullable().optional(),
  countryCode: z.string().min(2).max(3),
  gender: z.enum(['MALE', 'FEMALE', 'OTHER', 'NON_BINARY', 'PREFER_NOT_TO_SAY'] as const)
}))

// Estado reactivo para la fecha
const dateOfBirth = ref(formatDate(props.profileData?.dateOfBirth))

const form = useForm({
  validationSchema: formSchema,
  initialValues: {
    username: props.profileData?.username || '',
    firstName: props.profileData?.firstName || '',
    lastName: props.profileData?.lastName || '',
    email: props.profileData?.email || '',
    dateOfBirth: dateOfBirth.value,
    countryCode: props.profileData?.countryCode || '',
    gender: props.profileData?.gender || 'PREFER_NOT_TO_SAY',
  }
})

// Observador para actualizar la fecha cuando cambie
watch(dateOfBirth, (newDate) => {
  form.setFieldValue('dateOfBirth', newDate)
})

const countryOptions = computed(() =>
  Object.entries(countries).map(([code, country]) => ({
    value: code,
    label: country.name,
    flag: `https://flagcdn.com/w20/${code.toLowerCase()}.png`
  }))
)

const genderOptions = [
  { value: 'MALE', label: 'Hombre' },
  { value: 'FEMALE', label: 'Mujer' },
  { value: 'OTHER', label: 'Otro' },
  { value: 'NON_BINARY', label: 'No binario' },
  { value: 'PREFER_NOT_TO_SAY', label: 'Prefiero no decirlo' }
] as const

const getInitials = computed(() => {
  const name = `${form.values.firstName} ${form.values.lastName}`.trim()
  if (!name) return ''
  return name
    .split(' ')
    .map(word => word[0])
    .join('')
    .toUpperCase()
    .slice(0, 2)
})

const handleAvatarChange = (event: Event) => {
  const input = event.target as HTMLInputElement
  if (input.files && input.files[0]) {
    const file = input.files[0]
    // Verificar el tamaño del archivo (max 5MB)
    if (file.size > 5 * 1024 * 1024) {
      toast.error('El archivo es demasiado grande. Máximo 5MB.')
      return
    }
    
    // Verificar el tipo de archivo
    if (!file.type.startsWith('image/')) {
      toast.error('Por favor, selecciona una imagen válida.')
      return
    }
    
    avatarPreview.value = URL.createObjectURL(file)
    // Guardar el archivo para el envío
    form.setFieldValue('avatar', file)
  }
}

const handleDateChange = (date: Date | null) => {
  dateOfBirth.value = date || undefined
}

const onSubmit = form.handleSubmit((values) => {
  const formData: UpdateProfileRequest = {
    ...values,
    dateOfBirth: dateOfBirth.value,
    // Si hay un nuevo avatar, incluirlo en la solicitud
    ...(form.values.avatar && { avatar: form.values.avatar as File })
  }
  emit('save', formData)
})

// Limpiar URL del objeto al desmontar el componente
onBeforeUnmount(() => {
  if (avatarPreview.value) {
    URL.revokeObjectURL(avatarPreview.value)
  }
})
</script>

<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center p-4">
    <Card class="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto shadow-lg">
      <CardHeader>
        <div class="flex justify-between items-center">
          <CardTitle class="text-xl font-bold text-purple-900">
            Editar Perfil
          </CardTitle>
          <button class="text-gray-500 hover:text-gray-700" @click="$emit('close')">
            <span class="sr-only">Cerrar</span>
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>
      </CardHeader>

      <CardContent>
        <form @submit.prevent="onSubmit" class="space-y-6">
          <!-- Avatar Section -->
          <div class="flex items-center space-x-6">
            <div class="w-20 h-20 rounded-full overflow-hidden bg-gray-200">
              <img
                v-if="avatarPreview"
                :src="avatarPreview"
                alt="Preview avatar"
                class="w-full h-full object-cover"
              />
              <div v-else class="w-full h-full flex items-center justify-center text-gray-600 text-4xl">
                {{ getInitials }}
              </div>
            </div>
            <label class="cursor-pointer text-blue-600">
              Cambiar Avatar
              <input
                type="file"
                class="hidden"
                @change="handleAvatarChange"
                accept="image/*"
              />
            </label>
          </div>

          <!-- Form Fields -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- Personal Information -->
            <div class="space-y-4">
              <FormField
                v-slot="{ componentField, errors }"
                name="firstName"
              >
                <FormItem>
                  <FormLabel>Nombre</FormLabel>
                  <FormControl>
                    <Input
                      v-bind="componentField"
                      :class="{'border-red-500': errors.length}"
                      placeholder="Nombre"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              </FormField>

              <FormField
                v-slot="{ componentField, errors }"
                name="lastName"
              >
                <FormItem>
                  <FormLabel>Apellidos</FormLabel>
                  <FormControl>
                    <Input
                      v-bind="componentField"
                      :class="{'border-red-500': errors.length}"
                      placeholder="Apellidos"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              </FormField>

              <FormField
                v-slot="{ componentField, errors }"
                name="username"
              >
                <FormItem>
                  <FormLabel>Nombre de usuario</FormLabel>
                  <FormControl>
                    <Input
                      v-bind="componentField"
                      :class="{'border-red-500': errors.length}"
                      placeholder="Nombre de usuario"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              </FormField>
            </div>

            <!-- Additional Information -->
            <div class="space-y-4">
              <FormField
                v-slot="{ componentField, errors }"
                name="email"
              >
                <FormItem>
                  <FormLabel>Correo electrónico</FormLabel>
                  <FormControl>
                    <Input
                      type="email"
                      v-bind="componentField"
                      :class="{'border-red-500': errors.length}"
                      placeholder="Correo electrónico"
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              </FormField>

              <FormItem>
                <FormLabel>Fecha de nacimiento</FormLabel>
                <FormControl>
                  <Popover>
                    <PopoverTrigger asChild>
                      <Button
                        variant="outline"
                        class="w-full justify-start text-left font-normal"
                        :class="{'border-red-500': form.errors.dateOfBirth}"
                      >
                        <CalendarIcon class="mr-2 h-4 w-4" />
                        {{ dateOfBirth ? format(dateOfBirth, 'PP') : 'Selecciona una fecha' }}
                      </Button>
                    </PopoverTrigger>
                    <PopoverContent class="w-auto p-0">
                      <Calendar
                        mode="single"
                        :selected="dateOfBirth"
                        @update:model-value="handleDateChange"
                        :disabled-dates="{ after: new Date() }"
                      />
                    </PopoverContent>
                  </Popover>
                </FormControl>
                <FormMessage :name="'dateOfBirth'" />
              </FormItem>

              <FormField
                v-slot="{ componentField, errors }"
                name="countryCode"
              >
                <FormItem>
                  <FormLabel>País</FormLabel>
                  <Select v-bind="componentField">
                    <SelectTrigger :class="{'border-red-500': errors.length}">
                      <SelectValue placeholder="Selecciona un país" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem
                        v-for="option in countryOptions"
                        :key="option.value"
                        :value="option.value"
                      >
                        <img
                          :src="option.flag"
                          :alt="option.label"
                          class="w-5 h-3 mr-2 inline-block"
                        />
                        {{ option.label }}
                      </SelectItem>
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              </FormField>

              <FormField
                v-slot="{ componentField, errors }"
                name="gender"
              >
                <FormItem>
                  <FormLabel>Género</FormLabel>
                  <Select v-bind="componentField">
                    <SelectTrigger :class="{'border-red-500': errors.length}">
                      <SelectValue placeholder="Selecciona género" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem
                        v-for="option in genderOptions"
                        :key="option.value"
                        :value="option.value"
                      >
                        {{ option.label }}
                      </SelectItem>
                    </SelectContent>
                  </Select>
                  <FormMessage />
                </FormItem>
              </FormField>
            </div>
          </div>

          <!-- Form Actions -->
          <div class="flex justify-end space-x-4 mt-6">
            <Button
              type="button"
              variant="outline"
              @click="$emit('close')"
            >
              Cancelar
            </Button>
            <Button
              type="submit"
              class="bg-purple-700 hover:bg-purple-800 text-white"
              :disabled="props.loading"
            >
              <Loader2
                v-if="props.loading"
                class="mr-2 h-4 w-4 animate-spin"
              />
              Guardar Cambios
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  </div>
</template>