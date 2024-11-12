<script setup lang="ts">

import type { RoadmapCourse } from "@cortex/shared/types";

interface CoursesCardProps {
    courses: RoadmapCourse[]
    roadmapSlug: string
}

defineProps<CoursesCardProps>()

//Funcion para calcular el progreso de un curso
const calculateCourseProgress = (course: { modules: { lessons: { exercises: { completed: boolean; }[]; }[]; }[]; }) => {
    const totalExercises = course.modules.reduce((total, module) => total + module.lessons.reduce((lessonTotal, lesson) => lessonTotal + lesson.exercises.length, 0), 0);
    const completedExercises = course.modules.reduce((total, module) => total + module.lessons.reduce((lessonTotal, lesson) => lessonTotal + lesson.exercises.filter(exercise => exercise.completed).length, 0), 0);
    const progress = Math.round((completedExercises / totalExercises) * 100);
    return isNaN(progress) ? 0 : progress;
};

</script>

<template>
    <Card class="flex flex-col md:flex-row overflow-hidden rounded-2xl"
        v-for="(course, index) in [...courses]
        .sort((a, b) => (a.display_order ?? 0) - (b.display_order ?? 0))"
        :key="index"
        :value="`item-${index + 1}`">
        <img :src="course.image_url || 'https://placehold.co/500x400'" alt="Course Image" class="w-full md:w-[210px] h-[160px] md:h-auto object-cover md:border-b-0 md:border-r sm:border-b border-b"/>  
        
        <CardContent class="flex flex-col gap-3 p-5 w-full">
            <span class="text-lg sm:text-xl lg:text-xl xl:text-2xl font-bold text-start truncate">{{ index + 1 }}. {{ course.name }}</span>
            <div class="grid grid-cols-2 xl:flex xl:flex-wrap gap-4 xl:gap-8 justify-start">
                <div class="flex gap-2">
                    <ModuleIcon :width="18" class="stroke-current"/>
                    <span class="text-sm text-nowrap">Modulos <strong class="text-base">{{  course.modules.length }}</strong></span>
                </div>
                <div class="flex gap-2">
                    <BookOpen :width="18" class="stroke-current"/>
                    <span class="text-sm text-nowrap">Lecciones <strong class="text-base">{{ course.modules.reduce((total, module) => total + module.lessons.length, 0) }}</strong></span>
                </div>
                <div class="flex gap-2">
                    <LayoutList :width="18" class="stroke-current"/>
                    <span class="text-sm text-nowrap">Ejercicios <strong class="text-base">{{ course.modules.reduce((total, module) => total + module.lessons.reduce((lessonTotal, lesson) => lessonTotal + lesson.exercises.length, 0), 0) }}</strong></span>
                </div>
                <div class="flex gap-2">
                    <Clock3 :width="18" class="stroke-current"/>
                    <span class="text-sm text-nowrap">Ult. vez <strong class="text-base">Hoy</strong></span>
                </div>
            </div>
            <div class="flex flex-col sm:flex-row items-center justify-between gap-3">
                <div class="flex w-full items-center gap-4">
                    <Progress class="w-full sm:w-3/4" :model-value="calculateCourseProgress(course)" />
                    <span>{{ calculateCourseProgress(course) }}%</span>
                </div>
                <NuxtLink :to="`${roadmapSlug}/courses/${course.slug}`" class="w-full sm:w-auto">
                    <Button class="w-full gap-2 rounded-full whitespace-nowrap">
                        <span>Continuar</span>
                        <ArrowRight :size="18"/>
                    </Button>
                </NuxtLink>
            </div>
        </CardContent>
    </Card>
</template>