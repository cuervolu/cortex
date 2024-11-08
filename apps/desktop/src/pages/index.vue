<script setup lang="ts">
import { BookOpen, Rocket, User, Plus, BookmarkPlus } from 'lucide-vue-next'
import { useUserStore } from "~/stores"
import { useNavigation } from '~/composables/useNavigation'

const userStore = useUserStore()
const { hasRole } = useNavigation()

const userUsername = computed(() => userStore.user?.username ? `${userStore.user.username}` : 'No username')
const isAdmin = computed(() => hasRole(['ADMIN']))
const isModerator = computed(() => hasRole(['MODERATOR']))

const commonActions = [
  {
    name: 'Explorar Roadmaps',
    icon: BookOpen,
    route: '/roadmaps',
    variant: 'default'
  },
  {
    name: 'Mi Perfil',
    icon: User,
    route: '/profile',
    variant: 'outline'
  },
  {
    name: 'Explorar Ejercicios',
    icon: BookOpen,
    route: '/exercises',
    variant: 'default'
  }
]

const adminActions = [
  {
    name: 'Crear Roadmap',
    icon: Plus,
    route: { name: 'roadmaps-create' },
    variant: 'default'
  },
  {
    name: 'Asignar Cursos',
    icon: BookmarkPlus,
    route: { name: 'roadmaps-asign-courses' },
    variant: 'default'
  }
]

const moderatorActions = [
  {
    name: 'Moderar Contenido',
    icon: BookmarkPlus,
    route: '/moderation',
    variant: 'default'
  }
]
</script>

<template>
  <div class="self-stretch grow shrink basis-0 p-[30px] justify-between items-start flex flex-wrap h-full">
    <!-- Sección izquierda -->
    <div class="grow shrink basis-0 self-stretch min-w-[600px] w-full md:w-auto px-[43px] flex-col justify-center items-start gap-[27px] inline-flex">
      <!-- Encabezado -->
      <div class="self-stretch text-foreground text-[58px] font-semibold leading-[62px]">
        Bienvenido, {{ userUsername }}
      </div>

      <div class="self-stretch text-foreground text-2xl font-light leading-[35px]">
        Tu plataforma de aprendizaje personalizado. Explora, aprende y crece con nuestros roadmaps
        educativos.
      </div>

      <!-- Acciones Comunes -->
      <div class="self-stretch space-y-4">
        <div v-for="action in commonActions" :key="action.name" class="w-full">
          <NuxtLink :to="action.route" class="block w-full">
            <Button
                :variant="'default'"
                class="w-full h-[61px] px-5 py-[13px] rounded-[14px] justify-center items-center gap-2.5 flex"
                size="lg"
            >
              <component :is="action.icon" class="mr-2 h-5 w-5"/>
              <div class="text-lg font-medium leading-[35px]">{{ action.name }}</div>
            </Button>
          </NuxtLink>
        </div>
      </div>

      <!-- Texto informativo -->
      <div class="self-stretch text-muted-foreground text-base font-light leading-snug">
        ¿Listo para comenzar tu viaje? Elige un roadmap o personaliza tu perfil para obtener
        recomendaciones adaptadas a ti.
      </div>

      <!-- Acciones de Administrador -->
      <div v-if="isAdmin" class="self-stretch space-y-4">
        <div v-for="action in adminActions" :key="action.name" class="w-full">
          <NuxtLink :to="action.route" class="block w-full">
            <Button
                :variant="action.variant"
                class="w-full h-[61px] px-5 py-[13px] bg-primary rounded-[14px] justify-center items-center gap-2.5 flex"
                size="lg"
            >
              <component :is="action.icon" class="mr-2 h-5 w-5"/>
              <div class="text-white text-lg font-medium leading-[35px]">{{ action.name }}</div>
            </Button>
          </NuxtLink>
        </div>
      </div>

      <!-- Acciones de Moderador -->
      <div v-if="isModerator" class="self-stretch space-y-4">
        <div v-for="action in moderatorActions" :key="action.name" class="w-full">
          <NuxtLink :to="action.route" class="block w-full">
            <Button
                :variant="action.variant"
                class="w-full h-[61px] px-5 py-[13px] bg-primary rounded-[14px] justify-center items-center gap-2.5 flex"
                size="lg"
            >
              <component :is="action.icon" class="mr-2 h-5 w-5"/>
              <div class="text-white text-lg font-medium leading-[35px]">{{ action.name }}</div>
            </Button>
          </NuxtLink>
        </div>
      </div>
    </div>

    <!-- Sección derecha (imagen) -->
    <div class="grow shrink basis-0 self-stretch min-w-[310px] max-w-[908px] w-full md:w-auto px-[42px] py-[38px] bg-gradient-to-b from-background to-background-muted rounded-[35px] justify-center items-end gap-2.5 flex relative">
      <img
          src="https://res.cloudinary.com/dukgkrpft/image/upload/v1731019914/modules/curso-de-parques-y-heroes-de-java/vkachhklsl3tdolsjjr1.jpg"
          alt="Ilustración de aprendizaje"
          class="absolute inset-0 w-full h-full object-cover rounded-3xl"
      >
      <div class="absolute inset-0 bg-gradient-to-t from-background to-transparent"/>
      <div class="grow shrink basis-0 px-[35px] py-[25px] bg-background rounded-[22px] flex-col justify-start items-start gap-[18px] inline-flex z-10 backdrop-blur-sm bg-opacity-80">
        <div class="self-stretch justify-start items-center gap-5 inline-flex">
          <Rocket class="mr-2 h-6 w-6 text-primary"/>
          <div class="grow shrink basis-0 text-foreground text-2xl font-semibold leading-[30px]">
            Comienza tu aventura
          </div>
        </div>
        <div class="self-stretch text-muted-foreground text-base font-light leading-snug">
          Cortex te guía paso a paso en tu camino de aprendizaje. Descubre nuevos temas, domina
          habilidades y alcanza tus metas educativas.
        </div>
      </div>
    </div>
  </div>
</template>