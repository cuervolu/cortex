<script setup lang="ts">
import {
  LogOut,
  Settings,
  User,
  BookOpen,
  Map,
} from 'lucide-vue-next'

const { data: session, signOut } = useAuth()

const initials = computed(() => {
  if (!session.value) return 'U'
  return `${session.value.first_name.charAt(0)}${session.value.last_name.charAt(0)}`
})

const avatarUrl = computed(() => {
  return session.value?.avatar_url || ''
})

const handleLogout = async () => {
  await signOut({ callbackUrl: '/auth/login' })
}
</script>

<template>
  <DropdownMenu>
    <DropdownMenuTrigger as-child>
      <Avatar class="cursor-pointer">
        <AvatarImage
            v-if="avatarUrl"
            :src="avatarUrl"
            :alt="session?.username || ''"
        />
        <AvatarFallback>{{ initials }}</AvatarFallback>
      </Avatar>
    </DropdownMenuTrigger>
    <DropdownMenuContent class="w-56">
      <DropdownMenuLabel>Mi Cuenta</DropdownMenuLabel>
      <DropdownMenuSeparator />
      <DropdownMenuGroup>
        <DropdownMenuItem>
          <User class="mr-2 h-4 w-4" />
          <span>Perfil</span>
        </DropdownMenuItem>
        <DropdownMenuItem>
          <Map class="mr-2 h-4 w-4" />
          <span>Mis Roadmaps</span>
        </DropdownMenuItem>
        <DropdownMenuItem>
          <BookOpen class="mr-2 h-4 w-4" />
          <span>Mis Cursos</span>
        </DropdownMenuItem>
        <DropdownMenuItem>
          <Settings class="mr-2 h-4 w-4" />
          <span>Configuración</span>
        </DropdownMenuItem>
      </DropdownMenuGroup>
      <DropdownMenuSeparator />
      <DropdownMenuItem @click="handleLogout">
        <LogOut class="mr-2 h-4 w-4" />
        <span>Cerrar Sesión</span>
      </DropdownMenuItem>
    </DropdownMenuContent>
  </DropdownMenu>
</template>