<script setup lang="ts">
import { ref, computed } from 'vue'

const props = defineProps<{
  profileData: any
}>()

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'save', data: any): void
}>()

const formData = ref({
  firstName: props.profileData?.firstName || '',
  lastName: props.profileData?.lastName || '',
  username: props.profileData?.username || '',
  email: props.profileData?.email || '',
  dateOfBirth: props.profileData?.dateOfBirth || null,
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
  <div class="fixed inset-0 bg-[#1a1625]/90 flex items-center justify-center p-4 z-50">
    <div class="bg-[#1E1A2E] rounded-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto border border-purple-500/20">
      <div class="p-6">
        <div class="flex justify-between items-center mb-6">
          <h2 class="text-xl font-bold text-white">Editar Perfil</h2>
          <button @click="$emit('close')" class="text-gray-400 hover:text-white transition-colors">
            <span class="sr-only">Cerrar</span>
            <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        <form @submit.prevent="handleSubmit" class="space-y-6">
          <!-- Avatar -->
          <div class="flex items-center space-x-6">
            <div class="w-20 h-20 rounded-full overflow-hidden bg-[#2A2438] border border-purple-500/20">
              <img
                v-if="avatarPreview"
                :src="avatarPreview"
                alt="Avatar preview"
                class="w-full h-full object-cover"
              />
              <div v-else class="w-full h-full flex items-center justify-center text-gray-400">
                <span class="text-2xl">{{ getInitials }}</span>
              </div>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-200">Foto de perfil</label>
              <input
                type="file"
                accept="image/*"
                @change="handleAvatarChange"
                class="mt-1 block w-full text-sm text-gray-300
                      file:mr-4 file:py-2 file:px-4
                      file:rounded-full file:border-0
                      file:text-sm file:font-semibold
                      file:bg-purple-900/50 file:text-purple-300
                      hover:file:bg-purple-800/50
                      cursor-pointer"
              />
            </div>
          </div>

          <!-- Form fields -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div>
              <label class="block text-sm font-medium text-gray-200">Nombre</label>
              <input
                v-model="formData.firstName"
                type="text"
                class="mt-1 block w-full rounded-lg bg-[#2A2438] border-purple-500/20 text-white
                       shadow-sm focus:border-purple-500 focus:ring-purple-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-200">Apellido</label>
              <input
                v-model="formData.lastName"
                type="text"
                class="mt-1 block w-full rounded-lg bg-[#2A2438] border-purple-500/20 text-white
                       shadow-sm focus:border-purple-500 focus:ring-purple-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-200">Nombre de usuario</label>
              <input
                v-model="formData.username"
                type="text"
                class="mt-1 block w-full rounded-lg bg-[#2A2438] border-purple-500/20 text-white
                       shadow-sm focus:border-purple-500 focus:ring-purple-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-200">Email</label>
              <input
                v-model="formData.email"
                type="email"
                class="mt-1 block w-full rounded-lg bg-[#2A2438] border-purple-500/20 text-white
                       shadow-sm focus:border-purple-500 focus:ring-purple-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-200">Fecha de nacimiento</label>
              <input
                v-model="formData.dateOfBirth"
                type="date"
                class="mt-1 block w-full rounded-lg bg-[#2A2438] border-purple-500/20 text-white
                       shadow-sm focus:border-purple-500 focus:ring-purple-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-200">País</label>
              <input
                v-model="formData.countryCode"
                type="text"
                class="mt-1 block w-full rounded-lg bg-[#2A2438] border-purple-500/20 text-white
                       shadow-sm focus:border-purple-500 focus:ring-purple-500"
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-200">Género</label>
              <select
                v-model="formData.gender"
                class="mt-1 block w-full rounded-lg bg-[#2A2438] border-purple-500/20 text-white
                       shadow-sm focus:border-purple-500 focus:ring-purple-500"
              >
                <option value="MALE">Masculino</option>
                <option value="FEMALE">Femenino</option>
                <option value="OTHER">Otro</option>
                <option value="NON_BINARY">No binario</option>
                <option value="PREFER_NOT_TO_SAY">Prefiero no decirlo</option>
              </select>
            </div>
          </div>

          <div class="flex justify-end space-x-4 pt-4">
            <button
              type="button"
              @click="$emit('close')"
              class="px-4 py-2 border border-purple-500/20 rounded-lg text-gray-200 
                     hover:bg-purple-900/20 transition-colors"
            >
              Cancelar
            </button>
            <button
              type="submit"
              class="px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 
                     transition-colors"
            >
              Guardar cambios
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>