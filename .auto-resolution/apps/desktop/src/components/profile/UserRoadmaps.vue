<script setup lang="ts">
import { ref } from 'vue';
import { Coffee, Languages, Monitor, LogOut, UserCog } from 'lucide-vue-next';
import { useUserStore } from '~/stores';
import { error as logError } from "@tauri-apps/plugin-log";
import UserEdit from './UserEdit.vue'; // Asegúrate de que la ruta sea correcta

const roadmaps = ref([
  {
    title: 'Introduction to the programming world',
    icon: Monitor,
    badges: ['Introduction', 'Software', 'Algoritms', 'Python'],
  },
  {
    title: 'Compiled coffee',
    icon: Coffee,
    badges: ['Introduction', 'Software', 'Algoritms', 'Java'],
  },
  {
    title: 'Code Alchemy',
    icon: Languages,
    badges: ['Advanced', 'Compilers', 'Algoritms', 'C', 'Rust'],
  },
]);

// User store and logout functionality
const userStore = useUserStore();
const isLoading = ref(false);
const showEditProfile = ref(false);
const { clearUser } = useUserStore();
const { signOut } = useAuth();

const handleSignOut = async () => {
  if (isLoading.value) return;

  isLoading.value = true;
  try {
    await signOut({ callbackUrl: '/auth/login' });
    await clearUser();
  } catch (error) {
    await logError(`Error signing out: ${error}`);
  } finally {
    isLoading.value = false;
  }
};

const handleProfileUpdate = async (data) => {
  try {
    // Aquí implementarías la lógica para actualizar el perfil
    console.log('Actualizando perfil:', data);
    showEditProfile.value = false;

    await userStore.getUser();
  } catch (error) {
    console.error('Error al actualizar el perfil:', error);
  }
};


watch(showEditProfile, (newValue) => {
  console.log('showEditProfile changed:', newValue);
});
</script>

<template>
  <div class="space-y-8">
    <h2 class="text-2xl font-bold">Mis Roadmaps</h2>
    <Card v-for="roadmap in roadmaps" :key="roadmap.title">
      <CardHeader class="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle class="text-sm font-medium">
          {{ roadmap.title }}
        </CardTitle>
        <component :is="roadmap.icon" class="h-4 w-4 text-muted-foreground"/>
      </CardHeader>
      <CardContent>
        <div class="flex flex-wrap gap-2">
          <Badge v-for="badge in roadmap.badges" :key="badge" variant="secondary">{{ badge }}</Badge>
        </div>
      </CardContent>
    </Card>
    <Button class="w-full" as-child>
      <NuxtLink to="/my-roadmaps">
        Ver todos los roadmaps
      </NuxtLink>
    </Button>
    

    <Button 
      class="w-full flex items-center justify-center gap-2"
      variant="outline"
      @click="showEditProfile = true"
    >
      <UserCog class="w-4 h-4"/>
      <span>Editar Perfil</span>
    </Button>


    <Button 
      variant="destructive" 
      class="w-full flex items-center justify-center gap-2"
      :disabled="isLoading"
      @click="handleSignOut"
    >
      <template v-if="isLoading">
        <div class="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"/>
        <span>Cerrando sesión...</span>
      </template>
      <template v-else>
        <LogOut class="w-4 h-4"/>
        <span>Cerrar sesión</span>
      </template>
    </Button>


    <Teleport to="body">
      <UserEdit
        v-if="showEditProfile"
        :profile-data="userStore.user"
        @close="showEditProfile = false"
        @save="handleProfileUpdate"
      />
    </Teleport>
  </div>
</template>