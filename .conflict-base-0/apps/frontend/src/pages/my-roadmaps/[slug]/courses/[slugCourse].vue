<script setup lang="ts">
import HomeIcon from '~/components/icons/HomeIcon.vue';

import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";

import type { CourseDetails } from "@cortex/shared/types";
import type { RoadmapDetails } from "@cortex/shared/types";
import { Flag, ThumbsDown, ThumbsUp } from 'lucide-vue-next';

const route = useRoute();
const { getCourseDetails } = useCourses();
const { getRoadmapDetails } = useRoadmaps();

const courseData = ref<CourseDetails | null>(null);
const error = ref<string | null>(null);
const isLoading = ref(false);

// Función para cargar los datos del curso
const loadCourseDetails = async () => {
    const slugCourse = route.params.slugCourse as string;
    if (!slugCourse) return;

    const response = await getCourseDetails(slugCourse);
    courseData.value = response.data.value;
    error.value = response.error.value;
    isLoading.value = response.isLoading.value;
};

const roadmapData = ref<RoadmapDetails | null>(null);

// Funcion para obtener los datos del roadmap
const loadRoadmapDetails = async () => {
    const slug = route.params.slug as string;
    if (!slug) return;

    const response = await getRoadmapDetails(slug);
    roadmapData.value = response.data.value;
    error.value = response.error.value;
    isLoading.value = response.isLoading.value;

    // Añadir estos logs para debug
    console.log('Response completa:', response);
    console.log('Course Data:', courseData.value);
    console.log('Modules:', courseData.value?.modules);
};



// Cargar los datos cuando el componente se monta
onMounted(() => {
    loadCourseDetails();
    loadRoadmapDetails();
});

// Opcional: Si necesitas recargar cuando cambie el slug
watch(
    () => route.params.slugCourse,
    () => {
        loadCourseDetails();
    }
);

</script>

<template>
    <section v-if="isLoading">
        <div>Loading...</div>
    </section>

    <section v-else-if="error">
        <div>Error: {{ error }}</div>
    </section>
    

    <section v-else class="w-full self-stretch px-10">
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
                    <DropdownMenu>
                    <DropdownMenuTrigger class="flex items-center gap-1">
                        <BreadcrumbEllipsis class="h-4 w-4" />
                        <span class="sr-only">Toggle menu</span>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="start">
                        <DropdownMenuItem>
                            <NuxtLink to="/my-roadmaps">
                                Mis Roadmaps
                            </NuxtLink>
                        </DropdownMenuItem>
                        <DropdownMenuItem>Roadmaps</DropdownMenuItem>
                        <DropdownMenuItem>GitHub</DropdownMenuItem>
                    </DropdownMenuContent>
                    </DropdownMenu>
                </BreadcrumbItem>
                <BreadcrumbSeparator />
                <BreadcrumbItem>
                    <BreadcrumbLink>
                        <NuxtLink to="javascript:history.back()">
                            {{ roadmapData?.title }}
                        </NuxtLink>
                    </BreadcrumbLink>
                </BreadcrumbItem>
                <BreadcrumbSeparator />
                <BreadcrumbItem>
                    <BreadcrumbLink>
                        <NuxtLink to="javascript:history.back()">
                            Courses
                        </NuxtLink>
                    </BreadcrumbLink>
                </BreadcrumbItem>
                <BreadcrumbSeparator />
                <BreadcrumbItem>
                    <BreadcrumbPage>{{ courseData?.name }}</BreadcrumbPage>
                </BreadcrumbItem>
            </BreadcrumbList>
        </Breadcrumb>

        <Card class="rounded-3xl border-2 overflow-hidden">
            <CardHeader class="p-0">
                <img :src="courseData?.image_url ?? undefined" alt="Course Image" class="relative h-[360px] object-cover"/>
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

    </section>
</template>