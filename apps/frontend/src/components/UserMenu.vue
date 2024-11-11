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
  if (!session.value) return 'UN'
  const firstName = session.value.first_name
  const lastName = session.value.last_name

  if (!firstName || !lastName) return 'UN'
  return `${firstName.charAt(0)}${lastName.charAt(0)}`
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
          <RouterLink to="/profile" class="flex items-center">
          <User class="mr-2 h-4 w-4" />
          <span>Perfil</span>
        </RouterLink>
        </DropdownMenuItem>
        <DropdownMenuItem>
          <RouterLink to="/my-roadmaps" class="flex items-center">
          <Map class="mr-2 h-4 w-4" />
          <span>Mis Roadmaps</span>
        </RouterLink>
        </DropdownMenuItem>
        <DropdownMenuItem>
          <RouterLink to="/courses" class="flex items-center">
          <BookOpen class="mr-2 h-4 w-4" />
          <span>Mis Cursos</span>
          </RouterLink>
        </DropdownMenuItem>
        <DropdownMenuItem>
          <RouterLink to="/config" class="flex items-center ">
          <Settings class="mr-2 h-4 w-4" />
          <span>Configuración</span>
          </RouterLink>
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