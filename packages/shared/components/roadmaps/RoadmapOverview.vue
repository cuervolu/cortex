<script setup lang="ts">
import { AlarmClock, BookOpen, Clock, FileBadge, LayoutList, NotebookText, Search } from 'lucide-vue-next'
import type { Course } from "@cortex/shared/types";
import StudentIcon from '@cortex/shared/components/icons/StudentIcon.vue';
import InstagramIcon from '@cortex/shared/components/icons/InstagramIcon.vue';
import TwitterIcon from '@cortex/shared/components/icons/TwitterIcon.vue';
import LinkedinIcon from '@cortex/shared/components/icons/LinkedinIcon.vue';
import GithubIcon from '@cortex/shared/components/icons/GithubIcon.vue';

export interface RoadmapOverviewProps {
  description: string
  createdAt: string
  updatedAt: string | null
}

defineProps<RoadmapOverviewProps>()

const formatDate = (date: string) => new Date(date).toLocaleDateString()
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
            <ul>
            <li>
                  Fundamentos de Go: Aprende a diseñar y programar tus propias pistas de carreras utilizando el poderoso lenguaje Go. 
                  Domina desde los conceptos básicos, como estructuras y control de flujo, hasta técnicas avanzadas como concurrencia y 
                  manejo de errores, para desarrollar aplicaciones eficientes y de alto rendimiento.
            </li>
            <li>
                  Fundamentos de TypeScript: Descubre cómo TypeScript puede transformar tu experiencia de desarrollo web. Desde su tipado 
                  estático hasta el manejo avanzado de interfaces y genéricos, aprenderás a escribir código más confiable, escalable y 
                  legible. ¡Conviértete en un maestro de la creación de aplicaciones encantadoras y bien estructuradas!
            </li>
            <li>
                  Resolución de problemas: Pon a prueba tus habilidades enfrentándote a desafíos prácticos diseñados para mejorar tu lógica 
                  y capacidad analítica. Resuelve problemas como invertir cadenas de texto, calcular edades espaciales en diferentes 
                  planetas y desentrañar acertijos algorítmicos. Cada ejercicio te ayudará a perfeccionar tu pensamiento crítico y 
                  preparar soluciones efectivas.
            </li>
            <li>
                  Desarrollo práctico: Llévate el aprendizaje a otro nivel creando proyectos funcionales desde cero. Trabaja en un entorno 
                  interactivo que simula situaciones reales, permitiéndote aplicar tus conocimientos recién adquiridos de manera inmediata. 
                  Desde aplicaciones sencillas hasta soluciones más complejas, verás tu progreso en acción.
            </li>
          </ul>
          <span>
                  Este roadmap es ideal tanto para quienes están dando sus primeros pasos en el mundo de la programación como para aquellos 
                  que buscan llevar sus habilidades en Go y TypeScript al siguiente nivel. Prepárate para dominar conceptos esenciales y 
                  avanzar hacia el desarrollo de proyectos desafiantes. ¡Es tu oportunidad para brillar y destacarte en la pista de carreras 
                  tecnológicas!
          </span>

          </div>
        </div>
      </div>
      <Separator />
      <div class="flex">
        <span class="font-bold text-lg w-3/5">Creado por</span>
        <div class="flex-col flex justify-between gap-3 text-sm w-3/4">
          <div class="flex items-center gap-3">
            <Avatar class="w-12 h-12">
              <AvatarImage src="https://pbs.twimg.com/profile_images/994592419705274369/RLplF55e_400x400.jpg" />
            </Avatar>
            <div class="flex flex-col">
              <span class="font-bold">Nelson Garrido</span>
              <span>Desarrollador de Software</span>
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