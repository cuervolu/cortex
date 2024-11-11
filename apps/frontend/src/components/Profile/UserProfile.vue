<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { format, parseISO } from 'date-fns'
import { es } from 'date-fns/locale'
import { 
  type UserResponse,
  type Achievement,
  type UpdateProfileRequest,
  transformSessionToProfileRequest
} from '~/interfaces'
import EditProfileModal from '~/components/Profile/EditProfileModal.vue'

definePageMeta({
  middleware: 'auth'
})

const userProfile = ref<UserResponse | null>(null)
const achievements = ref<Achievement[]>([])
const showEditProfile = ref(false)
const loading = ref(true)
const error = ref<string | null>(null)

const profileRequest = computed(() => {
  if (!userProfile.value) return null
  return transformSessionToProfileRequest(userProfile.value)
})

// Obtener la sesión del usuario usando el composable de Nuxt Auth
const { data: session } = useAuth()

const getInitials = (name?: string) => {
  if (!name) return ''
  return name
    .split(' ')
    .map(word => word[0])
    .join('')
    .toUpperCase()
    .slice(0, 2)
}

const formatDate = (date?: string) => {
  if (!date) return 'No especificada'
  return format(parseISO(date), 'dd MMM yyyy', { locale: es })
}

const formatGender = (gender?: string) => {
  const genderMap: Record<string, string> = {
    'MALE': 'Masculino',
    'FEMALE': 'Femenino',
    'OTHER': 'Otro',
    'NON_BINARY': 'No binario',
    'PREFER_NOT_TO_SAY': 'Prefiero no decirlo'
  }
  return genderMap[gender || ''] || 'No especificado'
}

const handleProfileUpdate = async (updatedProfile: UpdateProfileRequest) => {
  try {
    // TODO: Implementar la actualización del perfil
    showEditProfile.value = false
    // Actualizar los datos locales después de la actualización exitosa
    if (session.value) {
      userProfile.value = session.value.user as UserResponse
    }
  } catch (error) {
    console.error('Error al actualizar el perfil:', error)
  }
}

// Cargar los datos del usuario cuando el componente se monta
onMounted(async () => {
  try {
    loading.value = true
    
    if (session.value) {
      userProfile.value = session.value.user as UserResponse
      // Aquí podrías cargar los logros si tienes un endpoint específico
      // achievements.value = await fetchAchievements()
    } else {
      throw new Error('No hay sesión de usuario')
    }
  } catch (error) {
    console.error('Error al cargar el perfil:', error)
    error.value = 'No se pudo cargar el perfil del usuario'
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="max-w-4xl mx-auto p-4">
    <!-- Estado de carga -->
    <div v-if="loading" class="flex justify-center items-center h-64">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
    </div>

    <!-- Estado de error -->
    <div v-else-if="error" class="bg-red-50 p-4 rounded-lg">
      <p class="text-red-600">{{ error }}</p>
    </div>

    <!-- Contenido principal -->
    <div v-else class="bg-white rounded-lg shadow">
      <!-- Header del perfil -->
      <div class="relative h-48 bg-gray-100 rounded-t-lg">
        <!-- Avatar -->
        <div class="absolute -bottom-16 left-8">
          <div class="w-32 h-32 rounded-full border-4 border-white overflow-hidden bg-gray-200">
            <img
              v-if="userProfile?.avatar_url"
              :src="userProfile.avatar_url"
              :alt="userProfile?.username"
              class="w-full h-full object-cover"
            />
            <div v-else class="w-full h-full flex items-center justify-center text-gray-500">
              <span class="text-4xl">{{ getInitials(userProfile?.full_name) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Información del perfil -->
      <div class="pt-20 px-8 pb-8">
        <div class="flex justify-between items-start">
          <div>
            <h1 class="text-2xl font-bold">{{ userProfile?.full_name }}</h1>
            <p class="text-gray-600">@{{ userProfile?.username }}</p>
          </div>
          <button
            @click="showEditProfile = true"
            class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700"
          >
            Editar Perfil
          </button>
        </div>

        <!-- Detalles del perfil -->
        <div class="mt-8 grid grid-cols-2 gap-6">
          <div>
            <h2 class="text-lg font-semibold mb-4">Información Personal</h2>
            <div class="space-y-3">
              <div>
                <span class="text-gray-600">Email:</span>
                <span class="ml-2">{{ userProfile?.email }}</span>
              </div>
              <div>
                <span class="text-gray-600">País:</span>
                <span class="ml-2">{{ userProfile?.country_code || 'No especificado' }}</span>
              </div>
              <div>
                <span class="text-gray-600">Género:</span>
                <span class="ml-2">{{ formatGender(userProfile?.gender) }}</span>
              </div>
              <div>
                <span class="text-gray-600">Fecha de nacimiento:</span>
                <span class="ml-2">{{ formatDate(userProfile?.date_of_birth) }}</span>
              </div>
            </div>
          </div>

          <div>
            <h2 class="text-lg font-semibold mb-4">Logros</h2>
            <div class="space-y-3">
              <div v-if="achievements?.length" class="grid gap-3">
                <div
                  v-for="achievement in achievements"
                  :key="achievement.id"
                  class="flex items-center p-3 bg-gray-50 rounded-lg"
                >
                  <img
                    v-if="achievement.type.iconUrl"
                    :src="achievement.type.iconUrl"
                    :alt="achievement.name"
                    class="w-8 h-8 mr-3"
                  />
                  <div>
                    <div class="font-medium">{{ achievement.name }}</div>
                    <div class="text-sm text-gray-600">{{ achievement.description }}</div>
                  </div>
                </div>
              </div>
              <div v-else class="text-gray-500">
                Aún no hay logros desbloqueados
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de edición de perfil -->
    <EditProfileModal
      v-if="showEditProfile"
      :profile-data="profileRequest"
      @close="showEditProfile = false"
      @save="handleProfileUpdate"
    />
  </div>
</template>