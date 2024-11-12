<script setup lang="ts">
import { BookOpen, LayoutList, EllipsisVertical, CirclePlay, Dot, CircleCheck } from "lucide-vue-next";
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
        <AccordionItem v-for="(course, index) in [...courses]
            .sort((a, b) => (a.display_order ?? 0) - (b.display_order ?? 0))" :key="index"
            :value="`item-${index + 1}`">
            <AccordionTrigger class="py-4 px-6">
                <div class="flex flex-col justify-start">
                    <span class="text-lg font-bold text-start">{{ index + 1 }}. {{ course.name }}</span>
                    <div class="flex">
                        <span>{{ course.modules.length }} modulo</span>
                        <Dot />
                        <span>50 min Total</span>
                    </div>
                </div>
            </AccordionTrigger>
            <AccordionContent class="p-0">
                <Accordion v-for="(module, moduleIndex) in [...course.modules]
                    .sort((a, b) => (a.display_order ?? 0) - (b.display_order ?? 0))" type="single" collapsible
                    class="w-full border-t">
                    <AccordionItem :value="`module-${moduleIndex + 1}`" class="border-0">
                        <AccordionTrigger :key="moduleIndex" :value="`module-${moduleIndex + 1}`"
                            class="py-4 px-6 hover:bg-[#92d2dd]/30 dark:hover:bg-[#F9CF87]/30 hover:no-underline"
                            :hidde-icon="true">
                            <div class="flex justify-between items-center w-full">
                                <div class="flex flex-col gap-1">
                                    <span class="font-bold text-base text-start">{{ index + 1 }}.{{ moduleIndex + 1 }}
                                        {{ module.name }}</span>
                                    <div class="flex">
                                        <span>{{ module.lesson_count }} lección</span>
                                        <Dot />
                                        <span>{{ module.lessons.reduce((total, lesson) => total +
                                            lesson.exercises.length, 0) }} ejercicios</span>
                                    </div>
                                </div>
                                <ModuleIcon :width="28" class="fill-current flex-shrink-0" />
                            </div>
                        </AccordionTrigger>
                        <AccordionContent class="p-0 border-t">
                            <Accordion v-for="(lesson, lessonIndex) in [...module.lessons]
                                .sort((a, b) => (a.display_order ?? 0) - (b.display_order ?? 0))" type="single"
                                collapsible class="w-full">
                                <AccordionItem :value="`lesson-${lessonIndex + 1}`" class="border-0">
                                    <AccordionTrigger :key="lessonIndex" :value="`lesson-${lessonIndex + 1}`"
                                        class="py-3 px-6 border-b-2 hover:bg-[#F9CF87]/30 dark:hover:bg-[#92d2dd]/30 hover:no-underline"
                                        :hidde-icon="true">
                                        <div class="flex justify-between items-center w-full">
                                            <div class="flex flex-col gap-1">
                                                <span class="font-bold text-base text-start">{{ index + 1 }}.{{
                                                    moduleIndex + 1 }}.{{ lessonIndex + 1 }} {{ lesson.name }}</span>
                                                <div class="flex">
                                                    <span>{{ lesson.exercises.length }} ejercicios</span>
                                                    <Dot />
                                                    <span>10 min</span>
                                                </div>
                                            </div>
                                            <BookOpen class="flex-shrink-0" :width="28" />
                                        </div>
                                    </AccordionTrigger>
                                    <AccordionContent class="p-0 border-b">
                                        <div v-for="(exercise, exerciseIndex) in [...lesson.exercises]
                                            .sort((a, b) => (a.display_order ?? 0) - (b.display_order ?? 0))"
                                            :key="exerciseIndex" :class="[
                                                'flex justify-between items-center py-3 gap-2 px-6',
                                                exercise.completed ? 'border-l-4 border-primary bg-popover dark:bg-secondary' : 'border-b'
                                            ]">
                                            <div class="flex gap-3 items-center">
                                                <LayoutList :size="28" class="stroke-current" />
                                                <div class="flex flex-col">
                                                    <span class="font-bold text-lg">{{ exercise.title }}</span>
                                                    <span v-if="exercise.completed">Completado</span>
                                                    <span v-else>No Iniciado</span>
                                                </div>
                                            </div>
                                            <CircleCheck v-if="exercise.completed" :size="28" class="stroke-current" />
                                            <CirclePlay v-else :size="28" class="stroke-current" />
                                        </div>
                                        <div v-if="lesson.exercises.length === 0"
                                            class="flex justify-center items-center py-3 gap-2 px-6 border-t">
                                            <span class="font-bold text-lg">No hay ejercicios disponibles</span>
                                        </div>
                                    </AccordionContent>
                                </AccordionItem>
                            </Accordion>
                            <div v-if="module.lessons.length === 0"
                                class="flex justify-center items-center py-3 gap-2 px-6 border-t">
                                <span class="font-bold text-lg">No hay lecciones disponibles</span>
                            </div>
                        </AccordionContent>
                    </AccordionItem>
                </Accordion>
                <div v-if="course.modules.length === 0"
                    class="flex justify-center items-center py-3 gap-2 px-6 border-t">
                    <span class="font-bold text-lg">No hay módulos disponibles</span>
                </div>
            </AccordionContent>
        </AccordionItem>
        <div v-if="courses.length === 0" class="flex justify-center items-center py-3 gap-2 px-6 border-t">
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