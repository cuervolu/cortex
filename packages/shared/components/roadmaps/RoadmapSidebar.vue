<script setup lang="ts">
import { Dot, CirclePlay } from 'lucide-vue-next'
import type { RoadmapCourse } from "@cortex/shared/types";
import type { RoadmapModule } from "@cortex/shared/types";
import type { RoadmapLesson } from "@cortex/shared/types";
import type { Exercise } from "@cortex/shared/types";
import ModuleIcon from '@cortex/shared/components/icons/ModuleIcon.vue';

interface RoadmapSidebarProps {
  courses: RoadmapCourse[]
  tagNames: string[]
}

defineProps<RoadmapSidebarProps>()

function getExerciseCount(lessons: RoadmapLesson[]): number {
  return lessons.reduce((count, lesson) => count + lesson.exercises.length, 0);
}

</script>

<template>
  <div class="py-5 px-6 flex justify-between items-center border-b">
    <h2 class="text-2xl font-extrabold">Contenido Roadmap</h2>
    <EllipsisVertical :size=24 class="stroke-muted" />
  </div>
  <Accordion type="single" collapsible>
    <AccordionItem value="item-1">
      <AccordionTrigger class="py-4 px-6 text-lg font-bold">Roadmap Intro</AccordionTrigger>
      <AccordionContent class="border-t py-4 px-6 ">
        <div class="flex justify-between items-center">
          <div class="flex flex-col gap-1">
            <span class="font-bold text-base">01: Introducción</span>
            <span>20 Minutos</span>
          </div>
          <CirclePlay :size="26" />
        </div>
      </AccordionContent>
    </AccordionItem>
  </Accordion>
  <div class="py-5 px-6 flex justify-between items-center border-b">
    <div class="flex items-center">
      <h2 class="text-xl font-extrabold">Cursos Roadmap</h2>
      <Dot :size=24 :stroke-width="3" />
      <h2 class="text-xl font-semibold">{{ courses.length }}</h2>
    </div>
    <EllipsisVertical :size=24 class="stroke-muted" />
  </div>
  <Accordion type="multiple" collapsible>
    <AccordionItem
      v-for="(course, index) in [...courses]
      .sort((a, b) => (a.display_order ?? 0) - (b.display_order ?? 0))"
      :key="index"
      :value="`item-${index + 1}`"
    >
      <AccordionTrigger class="py-4 px-6">
        <div class="flex flex-col justify-start">
          <span class="text-lg font-bold text-start">{{ index + 1 }}. {{ course.name }}</span>
          <div class="flex">
            <span>{{ course.modules.length }} modulo</span>
            <Dot/>
            <span>50 min Total</span>
          </div>
        </div>
      </AccordionTrigger>
      <AccordionContent class="p-0">
        <div v-for="(module, moduleIndex) in course.modules.sort((a, b) => (a.display_order ?? 0) - (b.display_order ?? 0))"
          :key="moduleIndex"
          class="flex justify-between items-center border-t px-6 py-2">
          <div class="flex flex-col gap-1 py-2">
            <span class="font-bold text-base">{{ index + 1 }}.{{ moduleIndex + 1 }} {{ module.name }}</span>
            <div class="flex">
              <span>{{ module.lesson_count }} lección</span>
              <Dot />
              <span>{{ getExerciseCount(module.lessons) }} ejercicios</span>
            </div>
          </div>
          <ModuleIcon :width="28" class="fill-current flex-shrink-0" />
        </div>
        <div v-if="course.modules.length === 0" class="flex justify-center items-center px-6">
          <span class="font-bold text-lg">No hay módulos disponibles</span>
        </div>
      </AccordionContent>
    </AccordionItem>
    <div v-if="courses.length === 0" class="flex justify-center items-center px-6">
      <span class="font-bold text-lg">No hay cursos disponibles</span>
    </div>
  </Accordion>
  <div class="py-5 px-3 flex flex-col border-b gap-4">
    <h2 class="font-bold text-lg">Tópicos</h2>
    <div class="flex flex-wrap gap-2">
      <Badge v-for="tag in tagNames" :key="tag">{{ tag }}</Badge>
    </div>
    <span class="font-bold text-sm text-end">Ver todos los tópicos</span>
  </div>
</template>