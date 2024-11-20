<script setup lang="ts">
import {genderLabels, transformSessionToProfileRequest} from '~/interfaces'
import { format } from 'date-fns'
import { es } from 'date-fns/locale'
import { Calendar as CalendarIcon } from 'lucide-vue-next'
import AvatarUpload from "~/components/Profile/AvatarUpload.vue";
import {useProfileForm} from "~/composables/useProfileForm";

const props = defineProps<{
  open: boolean
}>()

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void
}>()

const { data: session } = useAuth()
const { isSubmitting, onSubmit, resetForm, countryOptions } = useProfileForm()
const profileData = computed(() =>
    session.value ? transformSessionToProfileRequest(session.value) : null
)


watch(
    () => props.open,
    (newOpen) => {
      if (newOpen && profileData.value) {
        resetForm(profileData.value)
      }
    }
)
const onDialogClose = () => {
  emit('update:open', false)
}
const handleSubmit = async () => {
  try {
    await onSubmit()
    onDialogClose()
  } catch (error) {
    console.error('Error al actualizar el perfil:', error)
  }
}
</script>

<template>
  <Dialog :open="open" @update:open="onDialogClose">
    <DialogContent class="sm:max-w-[600px]">
    <DialogHeader>
        <DialogTitle>Editar perfil</DialogTitle>
        <DialogDescription>
          Realiza cambios en tu perfil aquí. Haz clic en guardar cuando termines.
        </DialogDescription>
      </DialogHeader>

      <form @submit.prevent="handleSubmit" class="space-y-4">
        <div class="flex justify-center mb-6">
          <FormField v-slot="{ field }" name="avatar">
            <FormItem>
              <FormControl>
                <AvatarUpload
                    :model-value="field.value"
                    @update:model-value="field.onChange"
                    :current-avatar-url="session?.avatar_url"
                />
              </FormControl>
            </FormItem>
          </FormField>

        </div>

        <FormField v-slot="{ field }" name="username">
          <FormItem>
            <FormLabel>Nombre de usuario</FormLabel>
            <FormControl>
              <Input placeholder="alan.turing" v-bind="field" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ field }" name="email">
          <FormItem>
            <FormLabel>Correo electrónico</FormLabel>
            <FormControl>
              <Input type="email" placeholder="albert.einstein@gmail.com" v-bind="field" />
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>

        <div class="grid grid-cols-2 gap-4">
          <FormField v-slot="{ field }" name="first_name">
            <FormItem>
              <FormLabel>Nombre</FormLabel>
              <FormControl>
                <Input placeholder="Alan" v-bind="field" />
              </FormControl>
              <FormMessage />
            </FormItem>
          </FormField>

          <FormField v-slot="{ field }" name="last_name">
            <FormItem>
              <FormLabel>Apellido</FormLabel>
              <FormControl>
                <Input placeholder="Turing" v-bind="field" />
              </FormControl>
              <FormMessage />
            </FormItem>
          </FormField>
        </div>

        <FormField v-slot="{ field }" name="gender">
          <FormItem>
            <FormLabel>Género</FormLabel>
            <Select :model-value="field.value" @update:model-value="field.onChange">
              <FormControl>
                <SelectTrigger>
                  <SelectValue placeholder="Selecciona un género" />
                </SelectTrigger>
              </FormControl>
              <SelectContent>
                <SelectItem v-for="(label, value) in genderLabels" :key="value" :value="value">
                  {{ label }}
                </SelectItem>
              </SelectContent>
            </Select>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ field }" name="date_of_birth">
          <FormItem class="flex flex-col">
            <FormLabel>Fecha de nacimiento</FormLabel>
            <Popover>
              <PopoverTrigger asChild>
                <FormControl>
                  <Button
                      variant="outline"
                      class="w-full pl-3 text-left font-normal"
                      :class="{ 'text-muted-foreground': !field.value }"
                  >
                    <CalendarIcon class="mr-2 h-4 w-4" />
                    {{ field.value ? format(field.value, 'PP', { locale: es }) : 'Selecciona una fecha' }}
                  </Button>
                </FormControl>
              </PopoverTrigger>
              <PopoverContent class="w-auto p-0">
                <Calendar
                    :model-value="field.value"
                    @update:model-value="field.onChange"
                    :locale="es"
                    initialFocus
                />
              </PopoverContent>
            </Popover>
            <FormMessage />
          </FormItem>
        </FormField>

        <FormField v-slot="{ field }" name="country_code">
          <FormItem>
            <FormLabel>País</FormLabel>
            <Select :model-value="field.value" @update:model-value="field.onChange">
              <FormControl>
                <SelectTrigger>
                  <SelectValue placeholder="Selecciona un país" />
                </SelectTrigger>
              </FormControl>
              <SelectContent>
                <SelectItem
                    v-for="option in countryOptions"
                    :key="option.value"
                    :value="option.value"
                >
                  <div class="flex items-center gap-2">
                    <img :src="option.flag" :alt="option.label" class="w-5 h-auto" />
                    <span>{{ option.label }}</span>
                  </div>
                </SelectItem>
              </SelectContent>
            </Select>
            <FormMessage />
          </FormItem>
        </FormField>

        <DialogFooter>
          <Button type="submit" :disabled="isSubmitting">
            {{ isSubmitting ? 'Guardando...' : 'Guardar cambios' }}
          </Button>
        </DialogFooter>
      </form>
    </DialogContent>
  </Dialog>
</template>