<script lang="ts" setup>
import { computed } from 'vue';
import { useUserStore } from '~/stores';


defineProps<{
  isCollapsed: boolean;
}>();

const userStore = useUserStore();

const userFullName = computed(() => userStore.user?.fullName || 'Unknown User');
const userUsername = computed(() => userStore.user?.username ? `@${userStore.user.username}` : 'No username');
const userAvatar = computed(() => userStore.user?.avatarUrl || '');
const userInitials = computed(() => {
  const name = userStore.user?.fullName || '';
  return name.split(' ').map(n => n[0]).join('').toUpperCase();
});

</script>

<template>
  <div class="self-stretch h-[118px] p-2.5 flex-col justify-start items-start flex">
    <div class="w-[121px] h-[35px] text-[#f4f8f7]/80 text-xs font-semibold tracking-tight">USER ACCOUNT</div>
    <div class="self-stretch justify-start items-center inline-flex">
      <Avatar class="w-[49px] h-[49px]">
        <AvatarImage :src="userAvatar" :alt="userFullName" />
        <AvatarFallback>{{ userInitials }}</AvatarFallback>
      </Avatar>
      <div v-if="!isCollapsed" class="grow shrink basis-0 p-2.5 flex-col justify-start items-start inline-flex">
        <div class="self-stretch h-7 text-[#f4f8f7] text-lg font-semibold tracking-tight">{{ userFullName }}</div>
        <div class="w-[111px] h-[15px] text-[#f4f8f7]/60 text-sm font-normal tracking-tight">
          {{ userUsername }}
        </div>
      </div>
    </div>
  </div>
</template>