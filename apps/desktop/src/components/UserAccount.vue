<script lang="ts" setup>
import {error as logError} from "@tauri-apps/plugin-log";
import {LogOut} from "lucide-vue-next";
import {useUserStore} from '~/stores';

defineProps<{
  isCollapsed: boolean;
}>();

const userStore = useUserStore();
const isLoading = ref(false);

const userFullName = computed(() => userStore.user?.full_name || 'Unknown User');
const userUsername = computed(() => userStore.user?.username ? `@${userStore.user.username}` : 'No username');
const userAvatar = computed(() => userStore.user?.avatar_url || '');
const userInitials = computed(() => {
  const name = userStore.user?.full_name || '';
  return name.split(' ').map(n => n[0]).join('').toUpperCase();
});

const {clearUser} = useUserStore();
const {signOut} = useAuth();

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

onMounted(async () => {
  await userStore.initStore();
  await userStore.getUser();
});
</script>

<template>
  <div class="self-stretch h-[128px] p-2.5 flex-col justify-start items-start flex">
    <div class="w-[121px] h-[25px] text-[#f4f8f7]/80 text-xs font-semibold tracking-tight">Cuenta de usuario
    </div>
    <div class="self-stretch justify-between items-center inline-flex">
      <div class="flex items-center">
        <DropdownMenu>
          <DropdownMenuTrigger class="focus:outline-none">
            <Avatar class="w-[49px] h-[49px] cursor-pointer hover:opacity-90 transition-opacity">
              <AvatarImage :src="userAvatar" :alt="userFullName"/>
              <AvatarFallback>{{ userInitials }}</AvatarFallback>
            </Avatar>
          </DropdownMenuTrigger>

          <DropdownMenuContent class="w-56 bg-[#1e1e1e] border border-[#f4f8f7]/10">
            <div class="px-2 py-1.5 border-b border-[#f4f8f7]/10">
              <p class="text-sm font-medium text-[#f4f8f7]">{{ userFullName }}</p>
              <p class="text-xs text-[#f4f8f7]/60">{{ userUsername }}</p>
            </div>

            <DropdownMenuItem
                :disabled="isLoading"
                class="flex items-center px-2 py-1.5 hover:bg-[#f4f8f7]/10 focus:bg-[#f4f8f7]/10 cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
                @click="handleSignOut"
            >
              <template v-if="isLoading">
                <div
                    class="w-4 h-4 mr-2 border-2 border-[#f4f8f7]/80 border-t-transparent rounded-full animate-spin"/>
                <span class="text-sm text-[#f4f8f7]/80">Cerrando sesión...</span>
              </template>
              <template v-else>
                <LogOut class="w-4 h-4 mr-2 text-[#f4f8f7]/80"/>
                <span class="text-sm text-[#f4f8f7]/80">Cerrar sesión</span>
              </template>
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>

        <div
            v-if="!isCollapsed"
            class="grow shrink basis-0 p-2.5 flex-col justify-start items-start inline-flex">
          <div class="self-stretch h-7 text-[#f4f8f7] text-lg font-semibold tracking-tight">
            {{ userFullName }}
          </div>
          <div class="w-[111px] h-[33px] text-[#f4f8f7]/60 text-sm font-normal tracking-tight">
            {{ userUsername }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
:deep(.dropdown-menu-content) {
  z-index: 50;
}
</style>