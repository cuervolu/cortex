<script setup lang="ts">
import Button from "@cortex/shared/components/ui/button/Button.vue";
import { BadgeCheck, Clock3, Share, ThumbsUp, ThumbsDown, Flag, BookOpen, LayoutList, ArrowRight, EllipsisVertical, CirclePlay, Dot, CircleCheck } from "lucide-vue-next";
import HomeIcon from "~/components/icons/HomeIcon.vue";
import ModuleIcon from "~/components/icons/ModuleIcon.vue";

import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";

import type { RoadmapDetails } from "@cortex/shared/types";

const route = useRoute();
const { getRoadmapDetails } = useRoadmaps();

const roadmapData = ref<RoadmapDetails | null>(null);
const error = ref<string | null>(null);
const isLoading = ref(false);

// Función para cargar los datos del roadmap
const loadRoadmapDetails = async () => {
    const slug = route.params.slug as string;
    if (!slug) return;

    const response = await getRoadmapDetails(slug);
    roadmapData.value = response.data.value;
    error.value = response.error.value;
    isLoading.value = response.isLoading.value;
};

// Cargar los datos cuando el componente se monta
onMounted(() => {
    loadRoadmapDetails();
});

// Opcional: Si necesitas recargar cuando cambie el slug
watch(
    () => route.params.slug,
    () => {
        loadRoadmapDetails();
    }
);

// Cantidad de Creditos por roadmap
const credits = computed(() => {
    if (!roadmapData.value) return 0;
    return roadmapData.value.courses.reduce((totalCredits: number, course: { modules: { lessons: { credits: number; }[]; }[]; }) => {
        return totalCredits + course.modules.reduce((moduleCredits: number, module: { lessons: { credits: number; }[]; }) => {
            return moduleCredits + module.lessons.reduce((lessonCredits: number, lesson: { credits: number; }) => {
                return lessonCredits + lesson.credits;
            }, 0);
        }, 0);
    }, 0);
});


//Obetener Courses
const courses = computed(() => {
    if (!roadmapData.value) return [];
    return roadmapData.value.courses;
});

//Funcion para calcular el progreso de un curso
const calculateCourseProgress = (course: { modules: { lessons: { exercises: { completed: boolean; }[]; }[]; }[]; }) => {
    const totalExercises = course.modules.reduce((total, module) => total + module.lessons.reduce((lessonTotal, lesson) => lessonTotal + lesson.exercises.length, 0), 0);
    const completedExercises = course.modules.reduce((total, module) => total + module.lessons.reduce((lessonTotal, lesson) => lessonTotal + lesson.exercises.filter(exercise => exercise.completed).length, 0), 0);
    const progress = Math.round((completedExercises / totalExercises) * 100);
    return isNaN(progress) ? 0 : progress;
};


</script>

<template>
    <section v-if="isLoading">Cargando detalles del roadmap...</section>

    <section v-else-if="error">
        <div>Error: {{ error }}</div>
    </section>

    <section v-else class="w-full self-stretch px-10 justify-start items-start gap-[30px] inline-flex">
        <div class="md:w-full lg:w-4/6 flex flex-col gap-7">
            <Breadcrumb class="py-5">
                <BreadcrumbList>
                    <BreadcrumbItem>
                        <BreadcrumbLink>
                            <NuxtLink to="/">
                                <HomeIcon class="w-[18px] fill-current" />
                            </NuxtLink>
                        </BreadcrumbLink>
                    </BreadcrumbItem>
                    <BreadcrumbSeparator />
                    <BreadcrumbItem>
                        <BreadcrumbLink>
                            <NuxtLink to="/roadmaps">
                                Roadmaps
                            </NuxtLink>
                        </BreadcrumbLink>
                    </BreadcrumbItem>
                    <BreadcrumbSeparator />
                    <BreadcrumbItem>
                        <BreadcrumbLink>
                            <NuxtLink to="/my-roadmaps">
                                Mis Roadmaps
                            </NuxtLink>
                        </BreadcrumbLink>
                    </BreadcrumbItem>
                    <BreadcrumbSeparator />
                    <BreadcrumbItem>
                        <BreadcrumbPage>{{ roadmapData?.title }}</BreadcrumbPage>
                    </BreadcrumbItem>
                </BreadcrumbList>
            </Breadcrumb>

            <div class="flex flex-col gap-7">
                <div class="flex justify-between items-center gap-5">
                    <div class="inline-flex justify-start items-center gap-4">
                        <h1 class="font-bold text-4xl" >{{ roadmapData?.title }}</h1>
                        <BadgeCheck :size="36" class="fill-[#689F39] stroke-[#FAF9F7] flex-shrink-0"/>
                    </div>
                    <Badge class="text-md px-3 py-1 text-nowrap">{{ credits }} Credits</Badge>
                </div>
            </div>

            <Card class="rounded-3xl border-2 overflow-hidden">
                <CardHeader class="p-0">
                    <img :src="roadmapData?.image_url ?? undefined" alt="Roadmap Image" class="relative h-[360px] object-cover"/>
                </CardHeader>
                <CardFooter class="border-t-2 justify-end items-center px-4 py-5">
                    <div class="flex gap-4">
                        <div class="flex justify-start items-center gap-2">
                            <Share :size="22" class="stroke-primary dark:stroke-current"/>
                            <span class="font-bold text-primary dark:text-current">Compartir</span>
                        </div>
                        <div class="flex gap-2">
                            <Button class="rounded-full border-2 w-11 h-11 p-0 bg-transparent hover:bg-[#00000020] dark:hover:bg-[#ffffff20]">
                                <ThumbsUp class="stroke-foreground"/>
                            </Button>
                            <Button class="rounded-full border-2 w-11 h-11 p-0 bg-transparent hover:bg-[#00000020] dark:hover:bg-[#ffffff20]">
                                <ThumbsDown class="stroke-foreground"/>
                            </Button>
                            <Button class="rounded-full border-2 w-11 h-11 p-0 bg-transparent hover:bg-[#00000020] dark:hover:bg-[#ffffff20]">
                                <Flag class="stroke-foreground"/>
                            </Button>
                        </div>
                    </div>
                </CardFooter>
            </Card>
            
            <Card class="flex flex-col md:flex-row overflow-hidden rounded-2xl"
                v-for="(course, index) in courses"
                    :key="index"
                    :value="`item-${index + 1}`">
                <img :src="course.image_url ?? undefined" alt="Course Image" class="w-full md:w-[210px] h-[160px] md:h-auto object-cover md:border-b-0 md:border-r sm:border-b border-b"/>  
                
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
                        <NuxtLink :to="`${roadmapData?.slug}/courses/${course.slug}`" class="w-full sm:w-auto">
                            <Button class="w-full gap-2 rounded-full whitespace-nowrap">
                                <span>Continuar</span>
                                <ArrowRight :size="18"/>
                            </Button>
                        </NuxtLink>
                    </div>
                </CardContent>
            </Card>
            
        </div>
        <div class="w-2/6 self-stretch bg-background border-l px-3 min-w-[322px] hidden lg:block">
            <div class="py-5 px-6 flex justify-between items-center border-b">
                <h2 class="text-2xl font-extrabold">Contenido Roadmap</h2>
                <EllipsisVertical :size=24 class="stroke-muted"/>
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
                            <CirclePlay :size="26"/>
                        </div>
                    </AccordionContent>
                </AccordionItem>
            </Accordion>
            <div class="py-5 px-6 flex justify-between items-center border-b">
                <div class="flex items-center">
                    <h2 class="text-xl font-extrabold">Cursos Roadmap</h2>
                    <Dot :size=24 :stroke-width="3"/>
                    <h2 class="text-xl font-semibold">{{ courses.length }}</h2>
                </div>
                <EllipsisVertical :size=24 class="stroke-muted" />
            </div>
            <Accordion type="multiple" collapsible>
                <AccordionItem
                    v-for="(course, index) in courses"
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
                    <AccordionContent class="py-4 ">
                        <Accordion 
                            v-for="(module, moduleIndex) in course.modules"
                            type="single" collapsible class="w-full border-t">
                            <AccordionItem :value="`module-${moduleIndex + 1}`" class="border-0">
                                <AccordionTrigger 
                                    :key="moduleIndex"
                                    :value="`module-${moduleIndex + 1}`"
                                    class="py-2 px-6 hover:bg-[#92d2dd]/30 dark:hover:bg-[#F9CF87]/30 hover:no-underline" :hidde-icon="true">
                                    <div class="flex justify-between items-center w-full">
                                        <div class="flex flex-col gap-1">
                                            <span class="font-bold text-base text-start">{{ index + 1 }}.{{ moduleIndex + 1 }} {{ module.name }}</span>
                                            <div class="flex">
                                                <span>{{ module.lesson_count }} lección</span>
                                                <Dot/>
                                                <span>{{ module.lessons.reduce((total, lesson) => total + lesson.exercises.length, 0) }} ejercicios</span>
                                            </div>
                                        </div>
                                        <ModuleIcon :width="28" class="fill-current flex-shrink-0"/>
                                    </div>
                                </AccordionTrigger>
                                <AccordionContent class="p-0 border-t">
                                    <Accordion 
                                        v-for="(lesson, lessonIndex) in module.lessons"
                                        type="single" collapsible class="w-full">
                                        <AccordionItem :value="`lesson-${lessonIndex + 1}`" class="border-0">
                                            <AccordionTrigger 
                                                :key="lessonIndex"
                                                :value="`lesson-${lessonIndex + 1}`"
                                                class="py-3 px-6 border-b-2 hover:bg-[#F9CF87]/30 dark:hover:bg-[#92d2dd]/30 hover:no-underline" :hidde-icon="true">
                                                <div class="flex justify-between items-center w-full">
                                                    <div class="flex flex-col gap-1">
                                                        <span class="font-bold text-base text-start">{{ index + 1 }}.{{ moduleIndex + 1 }}.{{ lessonIndex + 1 }} {{ lesson.name }}</span>
                                                        <div class="flex">
                                                            <span>{{ lesson.exercises.length }} ejercicios</span>
                                                            <Dot/>
                                                            <span>10 min</span>
                                                        </div>
                                                    </div>
                                                    <BookOpen class="flex-shrink-0" :width="28"/>
                                                </div>
                                            </AccordionTrigger>
                                            <AccordionContent class="p-0 border-b">
                                                <div 
                                                    v-for="(exercise, exerciseIndex) in lesson.exercises"
                                                    :key="exerciseIndex"
                                                    :class="[
                                                        'flex justify-between items-center py-3 gap-2 px-6',
                                                        exercise.completed ? 'border-l-4 border-primary bg-popover dark:bg-secondary' : 'border-b'
                                                    ]">
                                                    <div class="flex gap-3 items-center">
                                                        <LayoutList :size="28" class="stroke-current"/>
                                                        <div class="flex flex-col">
                                                            <span class="font-bold text-lg">{{ exercise.title }}</span>
                                                            <span v-if="exercise.completed">Completado</span>
                                                            <span v-else>No Iniciado</span>
                                                        </div>
                                                    </div>
                                                    <CircleCheck v-if="exercise.completed" :size="28" class="stroke-current"/>
                                                    <CirclePlay v-else :size="28" class="stroke-current"/>
                                                </div>
                                                <div v-if="lesson.exercises.length === 0" class="flex justify-center items-center py-3 gap-2 px-6 border-t">
                                                    <span class="font-bold text-lg">No hay ejercicios disponibles</span>
                                                </div>
                                            </AccordionContent>
                                        </AccordionItem>
                                    </Accordion>
                                </AccordionContent>
                            </AccordionItem>
                        </Accordion>
                        <div v-if="course.modules.length === 0" class="flex justify-center items-center py-3 gap-2 px-6 border-t">
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
                    <Badge v-for="tag in roadmapData?.tag_names" :key="tag">{{ tag }}</Badge>
                </div>
                <span class="font-bold text-sm text-end">Ver todos los tópicos</span>
            </div>
        </div>
    </section>
</template>