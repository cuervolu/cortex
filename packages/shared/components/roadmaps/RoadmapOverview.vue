<script setup lang="ts">
import { AlarmClock, BookOpen, Clock, FileBadge, LayoutList, NotebookText, Search } from 'lucide-vue-next'
import { computed } from 'vue'
import type { Course } from "@cortex/shared/types";
import StudentIcon from '@cortex/shared/components/icons/StudentIcon.vue';
import InstagramIcon from '@cortex/shared/components/icons/InstagramIcon.vue';
import TwitterIcon from '@cortex/shared/components/icons/TwitterIcon.vue';
import LinkedinIcon from '@cortex/shared/components/icons/LinkedinIcon.vue';
import GithubIcon from '@cortex/shared/components/icons/GithubIcon.vue';
import type { RoadmapMentor } from '@cortex/shared/types';

export interface RoadmapOverviewProps {
  description: string
  createdAt: string
  updatedAt: string | null
  mentor: RoadmapMentor
}
const props = defineProps<RoadmapOverviewProps>()

const formatDate = (date: string) => new Date(date).toLocaleDateString()

// obtener iniciales mentor con mentor.full_name
const initials = computed(() => {
  const [firstName, lastName] = props.mentor.full_name.split(' ')
  return `${firstName.charAt(0)}${lastName.charAt(0)}`
})

</script>

<template>
  <Tabs defaultValue="panorama" class="relative mr-auto w-full">
    <TabsList class="w-full justify-start rounded-none border-b bg-transparent p-0">
      <div class="mx-3">
        <Search />
      </div>
      <TabsTrigger value="panorama"
        class="relative rounded-none border-b-2 border-b-transparent bg-transparent px-4 pb-3 pt-2 font-semibold text-muted-foreground shadow-none transition-none focus-visible:ring-0 data-[state=active]:border-b-primary data-[state=active]:text-foreground data-[state=active]:shadow-none ">
        Panorama
      </TabsTrigger>
      <TabsTrigger value="notas"
        class="relative rounded-none border-b-2 border-b-transparent bg-transparent px-4 pb-3 pt-2 font-semibold text-muted-foreground shadow-none transition-none focus-visible:ring-0 data-[state=active]:border-b-primary data-[state=active]:text-foreground data-[state=active]:shadow-none ">
        Notas
      </TabsTrigger>
      <TabsTrigger value="comunicados"
        class="relative rounded-none border-b-2 border-b-transparent bg-transparent px-4 pb-3 pt-2 font-semibold text-muted-foreground shadow-none transition-none focus-visible:ring-0 data-[state=active]:border-b-primary data-[state=active]:text-foreground data-[state=active]:shadow-none ">
        Comunicados
      </TabsTrigger>
      <TabsTrigger value="reseñas"
        class="relative rounded-none border-b-2 border-b-transparent bg-transparent px-4 pb-3 pt-2 font-semibold text-muted-foreground shadow-none transition-none focus-visible:ring-0 data-[state=active]:border-b-primary data-[state=active]:text-foreground data-[state=active]:shadow-none ">
        Reseñas
      </TabsTrigger>
    </TabsList>
    <TabsContent value="panorama" class="flex flex-col p-8 gap-6">
      <div class="flex">
        <span class="font-bold text-lg w-3/5">Resumen</span>
        <div class="flex-wrap flex justify-between gap-3 text-sm w-3/4">
          <div class="flex gap-2 sm:min-w-[240px]">
            <BookOpen />
            <span>Nivel de Destreza: Cualquiera</span>
          </div>
          <div class="flex gap-2 sm:min-w-[240px]">
            <StudentIcon :width="24" class="fill-current" />
            <span>Estudiantes: 215,118</span>
          </div>
          <div class="flex gap-2 sm:min-w-[240px]">
            <NotebookText />
            <span>Lecturas: 4</span>
          </div>
          <div class="flex gap-2 sm:min-w-[240px]">
            <LayoutList />
            <span>Ejercicios: 10</span>
          </div>
          <div class="flex gap-2 sm:min-w-[240px]">
            <AlarmClock />
            <span>Duración: 1.8h</span>
          </div>
          <div class="flex gap-2 sm:min-w-[240px]">
            <FileBadge />
            <span>Certifiación: Si</span>
          </div>
        </div>
      </div>
      <Separator />
      <div class="flex">
        <span class="font-bold text-lg w-3/5">Descripcion</span>
        <div class="flex-col flex justify-between gap-3 text-sm w-3/4">
          <span>{{ description }}</span>
          <div class="flex flex-col gap-2">
            <span class="font-bold">¿Que vas a aprender?</span>
            <ul class="list-disc list-inside">
              <li>Fundamentos de Go: Aprende a diseñar y programar tus propias pistas de carreras con Go.</li>
              <li>Fundamentos de TypeScript: Domina los encantos de TypeScript y crea aplicaciones web encantadoras.
              </li>
              <li>Resolución de problemas: Enfréntate a ejercicios prácticos como invertir cadenas, calcular edades
                espaciales y mucho más.</li>
              <li>Desarrollo práctico: Crea proyectos de código funcional en un entorno interactivo para aplicar tus
                nuevas habilidades.</li>
            </ul>
            <span>Este roadmap es perfecto tanto para principiantes como para aquellos que buscan perfeccionar sus
              conocimientos en Go y TypeScript. ¡Es tu turno de brillar en la pista de carreras!</span>
          </div>
        </div>
      </div>
      <Separator />
      <div class="flex">
        <span class="font-bold text-lg w-3/5">Creado por</span>
        <div class="flex-col flex justify-between gap-3 text-sm w-3/4">
          <div class="flex items-center gap-3">
            <Avatar class="w-12 h-12">
              <AvatarImage
                v-if="mentor.avatar_url"
                :src="mentor.avatar_url"
                :alt="mentor.username || ''"
              />
              <AvatarFallback>{{ initials }}</AvatarFallback>
            </Avatar>
            <div class="flex flex-col">
              <span class="font-bold">{{ mentor.full_name }}</span>
              <span>@{{ mentor.username }}</span>
            </div>
          </div>
          <!-- Social Media Buttons -->
          <div class="flex gap-2">
            <!--Instagram -->
            <Button class="rounded-full w-8 h-8 p-0">
              <InstagramIcon class="fill-white w-4" />
            </Button>
            <!--Twitter -->
            <Button class="rounded-full w-8 h-8 p-0">
              <TwitterIcon class="fill-white w-4" />
            </Button>
            <!-- Linkedin -->
            <Button class="rounded-full w-8 h-8 p-0">
              <LinkedinIcon class="fill-white w-4" />
            </Button>
            <!-- Github -->
            <Button class="rounded-full w-8 h-8 p-0">
              <GithubIcon class="fill-white w-4" />
            </Button>
          </div>
        </div>
      </div>
    </TabsContent>
    <TabsContent value="notas">Change your notas here.</TabsContent>
    <TabsContent value="comunicados">Change your comunicados here.</TabsContent>
    <TabsContent value="reseñas">Change your reseñas here.</TabsContent>
  </Tabs>
</template>
