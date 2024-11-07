<script setup lang="ts">
import {useForm} from 'vee-validate'
import {toTypedSchema} from '@vee-validate/zod'
import {Loader2} from 'lucide-vue-next'
import {countries} from 'countries-list'
import * as z from 'zod'
import type {UpdateProfileRequest} from "~/interfaces"

useHead({
  title: 'Completa tu perfil',
  meta: [
    {
      name: 'description',
      content: 'Completa tu perfil para acceder a todos los beneficios de la plataforma'
    }
  ]
})

interface Props {
  loading?: boolean
  initialData?: Partial<UpdateProfileRequest>
  needsPassword?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  initialData: () => ({}),
  needsPassword: false
})

const emit = defineEmits<{
  (e: 'submit', formData: UpdateProfileRequest): void
}>()

const requiredFields = computed(() => {
  const fields: (keyof UpdateProfileRequest)[] = [
    'username',
    'firstName',
    'lastName',
    'dateOfBirth',
    'countryCode',
    'gender'
  ]

  if (props.needsPassword) {
    fields.push('password')
    fields.push('confirm_password')
  }

  return fields
})

const formSchema = toTypedSchema(z.object({
  username: z.string().min(3).max(50).optional(),
  firstName: z.string().min(2).max(50).optional(),
  lastName: z.string().min(2).max(50).optional(),
  dateOfBirth: z.date().optional(),
  countryCode: z.string().min(2).max(3).optional(),
  gender: z.enum(['MALE', 'FEMALE', 'OTHER', 'NON_BINARY', 'PREFER_NOT_TO_SAY'] as const).optional(),
  password: props.needsPassword
      ? z.string()
      .min(8, 'La contraseña debe tener al menos 8 caracteres')
      .regex(/[A-Z]/, 'Debe contener al menos una letra mayúscula')
      .regex(/[a-z]/, 'Debe contener al menos una letra minúscula')
      .regex(/[0-9]/, 'Debe contener al menos un número')
      : z.string().optional(),
  confirm_password: props.needsPassword
      ? z.string()
      : z.string().optional()
}).refine(data => {
  if (props.needsPassword) {
    return data.password === data.confirm_password
  }
  return true
}, {
  message: "Las contraseñas no coinciden",
  path: ["confirm_password"]
}))

const form = useForm({
  validationSchema: formSchema,
  initialValues: {
    username: props.initialData.username,
    firstName: props.initialData.firstName,
    lastName: props.initialData.lastName,
    dateOfBirth: props.initialData.dateOfBirth ? new Date(props.initialData.dateOfBirth) : undefined,
    countryCode: props.initialData.countryCode,
    gender: props.initialData.gender,
  }
})

const countryOptions = computed(() =>
    Object.entries(countries).map(([code, country]) => ({
      value: code,
      label: country.name,
      flag: `https://flagcdn.com/w20/${code.toLowerCase()}.png`
    }))
)

const genderOptions = [
  {value: 'MALE', label: 'Hombre'},
  {value: 'FEMALE', label: 'Mujer'},
  {value: 'OTHER', label: 'Otro'},
  {value: 'NON_BINARY', label: 'No binario'},
  {value: 'PREFER_NOT_TO_SAY', label: 'Prefiero no decirlo'}
] as const


const getFieldLabel = (field: string) => {
  const labels: Record<string, string> = {
    username: 'Nombre de usuario',
    firstName: 'Nombre',
    lastName: 'Apellidos',
    dateOfBirth: 'Fecha de nacimiento',
    countryCode: 'País',
    gender: 'Género',
    password: 'Contraseña',
    confirm_password: 'Confirmar contraseña'
  }
  return labels[field] || field.split('_').map(word => word.charAt(0).toUpperCase() + word.slice(1)).join(' ')
}

const onSubmit = form.handleSubmit((values) => {
  emit('submit', values)
})

// Group fields for layout
const fieldGroups = computed(() => {
  const fields = requiredFields.value
  const groups = []

  // Personal info group
  groups.push({
    title: 'Información Personal',
    fields: fields.filter(f => ['firstName', 'lastName', 'username'].includes(f))
  })

  // Additional info group  
  groups.push({
    title: 'Información Adicional',
    fields: fields.filter(f => ['dateOfBirth', 'countryCode', 'gender'].includes(f))
  })

  // Password group (if needed)
  if (props.needsPassword) {
    groups.push({
      title: 'Contraseña',
      fields: fields.filter(f => ['password', 'confirm_password'].includes(f))
    })
  }

  return groups
})
</script>

<template>
  <Card class="w-full max-w-4xl bg-white bg-opacity-20 backdrop-blur-lg rounded-xl shadow-lg">
    <CardHeader>
      <CardTitle class="text-3xl font-bold text-purple-900 text-center">
        {{ props.initialData.username ? 'Editar perfil' : 'Completar perfil' }}
      </CardTitle>
      <CardDescription class="text-lg text-purple-700 text-center">
        {{
          props.initialData.username
              ? 'Puedes modificar la información de tu perfil'
              : 'Necesitamos algunos datos para completar su perfil'
        }}
      </CardDescription>
    </CardHeader>
    <CardContent>
      <form class="space-y-6" @submit.prevent="onSubmit">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div
              v-for="group in fieldGroups" :key="group.title"
              class="bg-white bg-opacity-50 rounded-lg p-4 space-y-4">
            <h3 class="text-lg font-semibold text-purple-900 mb-4">{{ group.title }}</h3>
            <div
                class="grid gap-4"
                :class="group.fields.length > 2 ? 'grid-cols-1' : 'grid-cols-2'">
              <FormField
                  v-for="field in group.fields"
                  v-slot="{ componentField, errors }"
                  :key="field"
                  :name="field">
                <FormItem :class="{'col-span-2': field === 'username'}">
                  <FormLabel class="text-sm font-medium text-gray-700">
                    {{ getFieldLabel(field) }}
                  </FormLabel>
                  <FormControl>
                    <template v-if="field === 'dateOfBirth'">
                      <Calendar
                          v-bind="componentField"
                          class="w-full rounded-md border"/>
                    </template>
                    <template v-else-if="field === 'gender'">
                      <Select v-bind="componentField">
                        <SelectTrigger :class="{'border-red-500': errors.length}">
                          <SelectValue placeholder="Select gender"/>
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem
                              v-for="option in genderOptions"
                              :key="option.value"
                              :value="option.value">
                            {{ option.label }}
                          </SelectItem>
                        </SelectContent>
                      </Select>
                    </template>
                    <template v-else-if="field === 'countryCode'">
                      <Select v-bind="componentField">
                        <SelectTrigger :class="{'border-red-500': errors.length}">
                          <SelectValue placeholder="Select country"/>
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem
                              v-for="option in countryOptions"
                              :key="option.value"
                              :value="option.value">
                            <img
                                :src="option.flag"
                                :alt="option.label"
                                class="w-5 h-3 mr-2 inline-block">
                            {{ option.label }}
                          </SelectItem>
                        </SelectContent>
                      </Select>
                    </template>
                    <template v-else>
                      <Input
                          :type="field.includes('password') ? 'password' : 'text'"
                          :placeholder="getFieldLabel(field)"
                          v-bind="componentField"
                          autocomplete="new-password"
                          :class="{'border-red-500': errors.length}"
                      />
                    </template>
                  </FormControl>
                  <FormMessage class="text-xs text-red-500 mt-1"/>
                </FormItem>
              </FormField>
            </div>
          </div>
        </div>

        <div class="flex justify-end mt-6">
          <Button
              type="submit"
              class="bg-purple-700 hover:bg-purple-800 text-white px-8"
              :disabled="props.loading"
          >
            <Loader2 v-if="props.loading" class="mr-2 h-4 w-4 animate-spin"/>
            {{ props.initialData.username ? 'Actualizar perfil' : 'Completar perfil' }}
          </Button>
        </div>
      </form>
    </CardContent>
  </Card>
</template>