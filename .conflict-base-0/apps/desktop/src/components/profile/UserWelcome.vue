<script setup lang="ts">
import type {User} from '~/types';
import {computed} from "vue";

const {user} = defineProps<{
  user: User | null;
}>();

const userAvatar = computed(() => user?.avatar_url || '');
const userInitials = computed(() => {
  const name = user?.full_name || '';
  return name.split(' ').map(n => n[0]).join('').toUpperCase();
});

</script>

<template>
  <Card v-if="user" class="bg-purple-600 text-foreground">
    <CardContent class="flex items-center space-x-4 p-6">
      <Avatar class="w-20 h-20 border-4 border-purple-300">
        <AvatarImage :src="userAvatar" :alt="user.full_name"/>
        <AvatarFallback>{{ userInitials }}</AvatarFallback>
      </Avatar>
      <div>
        <h1 class="text-3xl font-bold mb-2">Bienvenido de vuelta, {{ user.first_name }}</h1>
        <p class="text-purple-200">
          Aprende a tu propio ritmo con acceso ilimitado, disponible en cualquier momento tanto en el sitio web
          como en el escritorio. Revisa el material cuando lo necesites, sin restricciones de tiempo.
        </p>
      </div>
    </CardContent>
  </Card>
</template>