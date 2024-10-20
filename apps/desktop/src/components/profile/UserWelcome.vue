<script setup lang="ts">
import type {User} from '~/types';
import {computed} from "vue";

const {user} = defineProps<{
  user: User | null;
}>();

const userAvatar = computed(() => user?.avatarUrl || '');
const userInitials = computed(() => {
  const name = user?.fullName || '';
  return name.split(' ').map(n => n[0]).join('').toUpperCase();
});

</script>

<template>
  <Card v-if="user" class="bg-purple-600 text-foreground">
    <CardContent class="flex items-center space-x-4 p-6">
      <Avatar class="w-20 h-20 border-4 border-purple-300">
        <AvatarImage :src="userAvatar" :alt="user.fullName"/>
        <AvatarFallback>{{ userInitials }}</AvatarFallback>
      </Avatar>
      <div>
        <h1 class="text-3xl font-bold mb-2">Welcome back, {{ user.firstName }}</h1>
        <p class="text-purple-200">
          Learn at your own pace with unlimited access, available anytime on both the website
          and desktop. Revisit the material whenever you need, with no time restrictions.
        </p>
      </div>
    </CardContent>
  </Card>
</template>