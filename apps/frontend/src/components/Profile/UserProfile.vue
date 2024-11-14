<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import type { UserResponse, UpdateProfileRequest } from '~/interfaces/user.interface'
import EditProfileModal from '@/components/Profile/EditProfileModal.vue'
import { useRouter } from 'vue-router'
import {
  LogOut,
} from 'lucide-vue-next'

const router = useRouter()
const loading = ref(true)
const error = ref<string | null>(null)
const showEditProfile = ref(false)
const profileRequest = ref<UpdateProfileRequest | null>(null)

// Obtenemos la sesión y el signOut correctamente
const { data: session, signOut } = useAuth()

// Watch para debug
watch(() => session.value, (newSession) => {
  console.log('Session changed:', newSession)
}, { immediate: true })

const userFullName = computed(() => {
  return session.value?.full_name || 'Unknown User'
})

const userUsername = computed(() => {
  return session.value?.username ? `@${session.value.username}` : 'No username'
})

const userAvatar = computed(() => {
  return session.value?.avatar_url || ''
})

const userEmail = computed(() => {
  return session.value?.email || 'No email'
})

const getInitials = computed(() => {
  const firstName = session.value?.first_name || ''
  const lastName = session.value?.last_name || ''
  return `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase()
})

const handleLogout = async () => {
  try {
    loading.value = true
    await signOut()
    // Primero navegamos a la página principal
    await router.push('/')
    // Opcional: Recargar la página para asegurar que se limpie el estado
    window.location.reload()
  } catch (error) {
    console.error('Error al cerrar sesión:', error)
  } finally {
    loading.value = false
  }
}

async function handleProfileUpdate(_updatedProfile: UpdateProfileRequest) {
  try {
    // TODO: Implementar la actualización del perfil
    showEditProfile.value = false;
    // La sesión se actualizará automáticamente si el backend responde con los nuevos datos
  } catch (error) {
    console.error('Error al actualizar el perfil:', error);
  }
}

onMounted(() => {
  loading.value = true
  try {
    // Verificamos directamente session.value en lugar de session.value?.user
    if (!session.value) {
      throw new Error('No hay sesión de usuario')
    }
    loading.value = false
  } catch (err) {
    console.error('Error al cargar el perfil:', err)
    error.value = 'No se pudo cargar el perfil del usuario'
    loading.value = false
  }
})
</script>

<template>
  <div class="relative max-w-4xl mx-auto p-4">
    <!-- Estado de carga -->
    <div v-if="loading" class="flex justify-center items-center h-64">
      <span class="animate-spin rounded-full h-12 w-12 border-b-2 border-gray-600" />
    </div>

    <!-- Estado de error -->
    <div v-else-if="error" class="bg-red-50 p-4 rounded-lg">
      <p class="text-red-600">{{ error }}</p>
    </div>

    <!-- Contenido principal -->
    <div v-else class="bg-white rounded-lg shadow-lg">
      <!-- Header del perfil -->
      <div class="relative h-48 bg-gray-50 rounded-t-lg">
        <!-- Avatar y datos básicos -->
        <div class="absolute -bottom-16 left-8 flex items-end gap-4">
          <div class="w-32 h-32 rounded-full border-4 border-white overflow-hidden bg-gray-100 shadow-md">
            <img
              v-if="userAvatar"
              :src="userAvatar"
              :alt="userFullName"
              class="w-full h-full object-cover"
            >
            <div 
              v-else 
              class="w-full h-full flex items-center justify-center text-gray-600 text-4xl"
            >
              {{ getInitials }}
            </div>
          </div>
          
          <div class="mb-4 flex gap-4">
            <button
              class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 shadow-sm"
              @click="showEditProfile = true"
            >
              Editar Perfil
            </button>
            <button
              class="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 shadow-sm flex items-center gap-2"
              :disabled="loading"
              @click="handleLogout"
            >
              <LogOut class="h-4 w-4" />
              <span>{{ loading ? 'Cerrando sesión...' : 'Cerrar sesión' }}</span>
            </button>
          </div>
        </div>
      </div>

      <!-- Información del perfil -->
      <div class="pt-20 px-8 pb-8">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">{{ userFullName }}</h1>
          <p class="text-gray-600">{{ userUsername }}</p>
          <p class="text-gray-600 mt-2">{{ userEmail }}</p>
        </div>

        <!-- Detalles del perfil -->
        <div class="mt-8">
          <div>
            <h2 class="text-lg font-semibold mb-4 text-gray-800">Información Personal</h2>
            <div class="space-y-3">
              <div v-if="session?.gender">
                <span class="text-gray-600">Género:</span>
                <span class="ml-2 text-gray-800">{{ session.gender }}</span>
              </div>
              <div v-if="session?.date_of_birth">
                <span class="text-gray-600">Fecha de nacimiento:</span>
                <span class="ml-2 text-gray-800">
                  {{ new Date(session.date_of_birth).toLocaleDateString() }}
                </span>
              </div>
              <div v-if="session?.country_code">
                <span class="text-gray-600">País:</span>
                <span class="ml-2 text-gray-800">{{ session.country_code }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de edición de perfil -->
    <Teleport to="body">
      <div v-if="showEditProfile" class="fixed inset-0 bg-black bg-opacity-50 z-50 flex items-center justify-center">
        <div class="bg-white rounded-lg shadow-xl max-w-2xl w-full mx-4">
          <EditProfileModal
            :profile-data="profileRequest"
            @close="showEditProfile = false"
            @save="handleProfileUpdate"
          />
        </div>
      </div>
    </Teleport>
  </div>
</template>