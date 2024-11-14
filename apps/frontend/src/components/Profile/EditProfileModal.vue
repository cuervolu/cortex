<script setup lang="ts">
import type { UpdateProfileRequest } from '@/interfaces/user.interface'
import { ref, computed } from 'vue'

const props = defineProps<{
  profileData: UpdateProfileRequest | null
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'save', data: UpdateProfileRequest): void
}>()

const formData = ref<UpdateProfileRequest>({
  firstName: props.profileData?.firstName || '',
  lastName: props.profileData?.lastName || '',
  username: props.profileData?.username || '',
  email: props.profileData?.email || '',
  dateOfBirth: props.profileData?.dateOfBirth,
  countryCode: props.profileData?.countryCode || '',
  gender: props.profileData?.gender || 'PREFER_NOT_TO_SAY',
})

const avatarPreview = ref<string | null>(null)

const getInitials = computed(() => {
  const name = `${formData.value.firstName} ${formData.value.lastName}`.trim()
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
    formData.value.avatar = input.files[0]
    avatarPreview.value = URL.createObjectURL(input.files[0])
  }
}

const handleSubmit = () => {
  emit('save', formData.value)
}
</script>

<template>
  <div class="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center p-4">
    <div class="bg-white rounded-lg max-w-2xl w-full max-h-[90vh] overflow-y-auto shadow-lg">
      <!-- Contenido del modal -->
      <div class="p-6">
        <div class="flex justify-between items-center mb-6">
          <h2 class="text-xl font-bold">Editar Perfil</h2>
          <button class="text-gray-500 hover:text-gray-700" @click="$emit('close')">
            <span class="sr-only">Cerrar</span>
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <form class="space-y-6" @submit.prevent="handleSubmit">
          <div class="flex items-center space-x-6">
            <div class="w-20 h-20 rounded-full overflow-hidden bg-gray-200">
              <img
                v-if="avatarPreview"
                :src="avatarPreview"
                alt="Preview avatar"
                class="w-full h-full object-cover"
              >
              </img>
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
              ></input>
            </label>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700">Nombre</label>
            <input
              type="text"
              v-model="formData.firstName"
              class="mt-1 block w-full p-2 border border-gray-300 rounded-md"
            >
            </input>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700">Apellido</label>
            <input
              type="text"
              v-model="formData.lastName"
              class="mt-1 block w-full p-2 border border-gray-300 rounded-md"
            ></input>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700">Nombre de Usuario</label>
            <input
              type="text"
              v-model="formData.username"
              class="mt-1 block w-full p-2 border border-gray-300 rounded-md"
            ></input>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700">Correo Electrónico</label>
            <input
              type="email"
              v-model="formData.email"
              class="mt-1 block w-full p-2 border border-gray-300 rounded-md"
            ></input>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700">Fecha de Nacimiento</label>
            <input
              type="date"
              v-model="formData.dateOfBirth"
              class="mt-1 block w-full p-2 border border-gray-300 rounded-md"
            ></input>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700">País</label>
            <input
              type="text"
              v-model="formData.countryCode"
              class="mt-1 block w-full p-2 border border-gray-300 rounded-md"
            >
            </input>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700">Género</label>
            <select v-model="formData.gender" class="mt-1 block w-full p-2 border border-gray-300 rounded-md">
              <option value="PREFER_NOT_TO_SAY">Prefiero no decir</option>
              <option value="MALE">Masculino</option>
              <option value="FEMALE">Femenino</option>
              <!-- Agregar otros géneros según sea necesario -->
            </select>
          </div>

          <div class="flex justify-end">
            <button
              type="button"
              class="px-4 py-2 bg-gray-300 rounded-md mr-2"
              @click="$emit('close')"
            >
              Cancelar
            </button>
            <button
              type="submit"
              class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
            >
              Guardar Cambios
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
