<script setup lang="ts">
import {type UpdateProfileRequest, useGender} from '~/interfaces/user.interface'
import EditProfileModal from '@/components/Profile/EditProfileModal.vue'
import {
  LogOut, Trash2, Lock, UserCheck, Calendar, MapPin,
  Mail, Trophy, ShieldCheck, ShieldAlert
} from 'lucide-vue-next'
import {AppError} from "@cortex/shared/types";
import DeleteConfirmModal from "~/components/Profile/DeleteConfirmModal.vue";
import Input from '@cortex/shared/components/ui/input/Input.vue';

const router = useRouter()
const loading = ref(true)
const error = ref<string | null>(null)
const showEditProfile = ref(false)
const showDeleteConfirm = ref(false)
const profileRequest = ref<UpdateProfileRequest | null>(null)
const deleteLoading = ref(false)
const {translateGender} = useGender()

const {data: session} = useAuth()

const accountStatus = computed(() => {
  if (session.value?.account_locked) return {label: 'Bloqueada', class: 'bg-red-500'}
  if (!session.value?.enabled) return {label: 'Desactivada', class: 'bg-yellow-500'}
  return {label: 'Activa', class: 'bg-green-500'}
})

const formattedCreationDate = computed(() => {
  if (!session.value?.created_at) return ''
  return new Date(session.value.created_at).toLocaleDateString('es', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
})

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

const handleDeleteAccount = async () => {

}

async function handleProfileUpdate(updatedProfile: UpdateProfileRequest) {
  // Actualizar el perfil
}

onMounted(() => {
  loading.value = true
  try {
    if (!session.value) {
      throw new AppError("No se ha iniciado sesión", {
        data: {session: session.value}
      })
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
    <div v-if="loading" class="bg-white rounded-lg shadow-lg">
      <!-- Header del perfil con skeleton -->
      <div class="relative h-48 bg-gray-50 rounded-t-lg">
        <div class="absolute -bottom-16 left-8 flex items-end gap-4">
          <!-- Avatar skeleton -->
          <Skeleton class="w-32 h-32 rounded-full"/>

          <!-- Botones skeleton -->
          <div class="mb-4 flex gap-4">
            <Skeleton class="w-32 h-10 rounded-md"/>
            <Skeleton class="w-40 h-10 rounded-md"/>
            <Skeleton class="w-40 h-10 rounded-md"/>
          </div>
        </div>
      </div>

      <!-- Información del perfil skeleton -->
      <div class="pt-20 px-8 pb-8">
        <div>
          <Skeleton class="w-64 h-8 mb-2"/> <!-- Nombre -->
          <Skeleton class="w-48 h-6 mb-2"/> <!-- Username -->
          <Skeleton class="w-56 h-6"/> <!-- Email -->
        </div>

        <!-- Detalles del perfil skeleton -->
        <div class="mt-8">
          <Skeleton class="w-48 h-7 mb-4"/> <!-- Título sección -->
          <div class="space-y-4">
            <Skeleton class="w-72 h-6"/> <!-- Género -->
            <Skeleton class="w-80 h-6"/> <!-- Fecha de nacimiento -->
            <Skeleton class="w-64 h-6"/> <!-- País -->
          </div>
        </div>
      </div>
    </div>

    <!-- Estado de error -->
    <div v-else-if="error" class="bg-red-50 p-4 rounded-lg">
      <p class="text-red-600">{{ error }}</p>
    </div>

    <!-- Contenido principal -->
    <div v-else class="bg-white rounded-lg shadow-lg p-3">
      <!-- Header Card -->
      <Card class="mb-6">
        <CardContent class="pt-6">
          <div class="flex flex-wrap md:flex-nowrap md:gap-4 gap-7 items-start justify-between">
            <!-- Información básica y avatar -->
            <div class="flex gap-6">
              <div class="relative">
                <div class="relative">
                  <div
                    class="w-32 h-32 rounded-full border-4 border-white overflow-hidden bg-gray-100 shadow-lg">
                  <img
                    v-if="userAvatar"
                    :src="userAvatar"
                    :alt="userFullName"
                    class="w-full h-full object-cover"
                  >
                  <div v-else
                    class="w-full h-full flex items-center justify-center text-gray-600 text-4xl">
                    {{ getInitials }}
                  </div>
                  </div>
                  <Badge :class="accountStatus.class" class="absolute -bottom-2 left-3/4 transform -translate-x-1/2 px-3 py-1">
                  {{ accountStatus.label }}
                  </Badge>
                </div>
              </div>

              <div class="space-y-2">
                <div class="flex items-center gap-3">
                  <h1 class="text-2xl font-bold text-gray-800">{{ userFullName }}</h1>
                  <TooltipProvider>
                    <Tooltip>
                      <TooltipTrigger>
                        <ShieldCheck v-if="session?.has_password" class="h-6 w-6 text-green-500 flex-shrink-0"/>
                        <ShieldAlert v-else class="h-6 w-6 text-yellow-500 flex-shrink-0"/>
                      </TooltipTrigger>
                      <TooltipContent>
                        {{
                          session?.has_password ? 'Cuenta protegida con contraseña' : 'Cuenta sin contraseña'
                        }}
                      </TooltipContent>
                    </Tooltip>
                  </TooltipProvider>
                </div>
                <p class="text-gray-600 flex items-center gap-2">
                  <span>{{ userUsername }}</span>
                </p>
                <p class="text-gray-600 flex items-center gap-2">
                  <Mail class="h-4 w-4 flex-shrink-0"/>
                    <span class="break-all md:text-nowrap">{{ userEmail }}</span>
                </p>
                <div class="flex gap-2 mt-2">
                  <Badge v-for="role in session?.roles" :key="role" variant="secondary">
                    {{ role }}
                  </Badge>
                </div>
              </div>
            </div>

            <!-- Acciones -->
            <div class="flex gap-3 gap w-full md:w-fit">
              <Button variant="outline" @click="showEditProfile = true" class="w-full md:w-fit">
                Editar Perfil
              </Button>
              <Button variant="destructive" :disabled="deleteLoading" class="w-full md:w-fit"
                      @click="showDeleteConfirm = true">
                <Trash2 class="h-4 w-4 mr-2"/>
                Eliminar cuenta
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>

      <!-- Grid de información -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <!-- Información personal -->
        <Card>
          <CardContent class="pt-6">
            <h2 class="text-lg font-semibold mb-4 flex items-center gap-2">
              <UserCheck class="h-5 w-5"/>
              Información Personal
            </h2>
            <div class="space-y-4">
              <div v-if="session?.gender" class="flex items-center gap-2">
                <Badge variant="outline">Género</Badge>
                <span>{{ translateGender(session.gender) }}</span>
              </div>
              <div v-if="session?.date_of_birth" class="flex items-center gap-2">
                <Badge variant="outline">Nacimiento</Badge>
                <span>{{ new Date(session.date_of_birth).toLocaleDateString() }}</span>
              </div>
              <div v-if="session?.country_code" class="flex items-center gap-2">
                <Badge variant="outline">País</Badge>
                <span>{{ session.country_code }}</span>
              </div>
            </div>
          </CardContent>
        </Card>

        <!-- Información de la cuenta -->
        <Card>
          <CardContent class="pt-6">
            <h2 class="text-lg font-semibold mb-4 flex items-center gap-2">
              <Lock class="h-5 w-5"/>
              Información de la Cuenta
            </h2>
            <div class="space-y-4">
              <div class="flex items-center gap-2">
                <Badge variant="outline">Miembro desde</Badge>
                <span>{{ formattedCreationDate }}</span>
              </div>
              <div class="flex items-center gap-2">
                <Badge variant="outline">Estado</Badge>
                <Badge :class="accountStatus.class">
                  {{ accountStatus.label }}
                </Badge>
              </div>
              <div class="flex items-center gap-2">
                <Badge variant="outline">Seguridad</Badge>
                <span>{{
                    session?.has_password ? 'Cuenta con contraseña' : 'Sin contraseña'
                  }}</span>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      <!-- Activar Suscripcion -->
      <Card class="mt-6">
        <CardContent class="pt-6">
          <h2 class="text-lg font-semibold mb-4 flex items-center gap-2">
            <Trophy class="h-5 w-5"/>
            Activar Suscripción Premium
          </h2>
          <p class="text-gray-600">
            ¡Activa tu suscripción para acceder a contenido exclusivo y mejorar tu experiencia de aprendizaje!
          </p>
          <div class="flex gap-3">
            <Button class="mt-4">
              Activar Suscripción
            </Button>
            <Input class="mt-4" placeholder="Código de activación"/>
          </div>
        </CardContent>
      </Card>
    </div>

    <EditProfileModal
        v-model:open="showEditProfile"
        :loading="loading"
        :profile-data="profileRequest"
        @save="handleProfileUpdate"
    />

    <DeleteConfirmModal
        v-model:open="showDeleteConfirm"
        :loading="deleteLoading"
        @confirm="handleDeleteAccount"
    />
  </div>
</template>