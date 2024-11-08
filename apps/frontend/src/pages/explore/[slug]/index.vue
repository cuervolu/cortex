<script setup lang="ts">
import Button from "@cortex/shared/components/ui/button/Button.vue";
import { BadgeCheck, Clock3, Star, StickyNote, Share, ThumbsUp, ThumbsDown, Flag, Search, BookOpen, NotebookText, AlarmClock, FileBadge, LayoutList, Facebook, Rocket, ArrowRight, EllipsisVertical, CirclePlay, Dot } from "lucide-vue-next";
import HomeIcon from "~/components/icons/HomeIcon.vue";
import TwitterIcon from "~/components/icons/TwitterIcon.vue";
import GithubIcon from "~/components/icons/GithubIcon.vue";
import InstagramIcon from "~/components/icons/InstagramIcon.vue";
import LinkedinIcon from "~/components/icons/LinkedinIcon.vue";
import StudentIcon from "~/components/icons/StudentIcon.vue";
import AvatarImage from "@cortex/shared/components/ui/avatar/AvatarImage.vue";
import Card from "@cortex/shared/components/ui/card/Card.vue";
import CardContent from "@cortex/shared/components/ui/card/CardContent.vue";
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

</script>

<template>
    <section v-if="!roadmapData">Cargando detalles del roadmap...</section>
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
                            <NuxtLink to="/explore">
                                Explore Roadmaps
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
                <div class="flex gap-8">
                    <div>
                        <div class="flex items-center gap-1.5">
                            <Star :width="20" class="stroke-[#F59E0B]"/>
                            <span class="font-bold text-base">4.2</span>
                        </div>
                        <span class="font-medium text-sm">14,715 Ratings</span>
                    </div>
                    <div>
                        <div class="flex items-center gap-1.5">
                            <StudentIcon :width="20" class="fill-current"/>
                            <span class="font-bold text-base">32,123</span>
                        </div>
                        <span class="font-medium text-sm">14,715 Ratings</span>
                    </div>
                    <div>
                        <div class="flex items-center gap-1.5">
                            <Clock3 :width="20"/>
                            <span class="font-bold text-base">1.8h</span>
                        </div>
                        <span class="font-medium text-sm">14,715 Ratings</span>
                    </div>
                </div>
            </div>
            <Card class="rounded-3xl border-2 overflow-hidden">
                <CardHeader class="p-0">
                    <img :src="roadmapData?.image_url" alt="Roadmap Image" class="relative h-[360px] object-cover"/>
                </CardHeader>
                <CardFooter class="border-t-2 justify-between items-center px-4 py-5">
                    <div class="flex justify-start items-center gap-2">
                        <StickyNote :size="22" class="rotate-90" />
                        <span class="font-bold">Guardar Roadmap</span>
                    </div>
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

            <Tabs defaultValue="panorama" class="relative mr-auto w-full">
                <TabsList class="w-full justify-start rounded-none border-b bg-transparent p-0">      
                    <div class="mx-3">
                        <Search/>
                    </div>              
                    <TabsTrigger
                        value="panorama"
                        class="relative rounded-none border-b-2 border-b-transparent bg-transparent px-4 pb-3 pt-2 font-semibold text-muted-foreground shadow-none transition-none focus-visible:ring-0 data-[state=active]:border-b-primary data-[state=active]:text-foreground data-[state=active]:shadow-none "
                    >
                        Panorama
                    </TabsTrigger>
                    <TabsTrigger
                        value="notas"
                        class="relative rounded-none border-b-2 border-b-transparent bg-transparent px-4 pb-3 pt-2 font-semibold text-muted-foreground shadow-none transition-none focus-visible:ring-0 data-[state=active]:border-b-primary data-[state=active]:text-foreground data-[state=active]:shadow-none "
                    >
                        Notas
                    </TabsTrigger>
                    <TabsTrigger
                        value="comunicados"
                        class="relative rounded-none border-b-2 border-b-transparent bg-transparent px-4 pb-3 pt-2 font-semibold text-muted-foreground shadow-none transition-none focus-visible:ring-0 data-[state=active]:border-b-primary data-[state=active]:text-foreground data-[state=active]:shadow-none "
                    >
                        Comunicados
                    </TabsTrigger>
                    <TabsTrigger
                        value="reseñas"
                        class="relative rounded-none border-b-2 border-b-transparent bg-transparent px-4 pb-3 pt-2 font-semibold text-muted-foreground shadow-none transition-none focus-visible:ring-0 data-[state=active]:border-b-primary data-[state=active]:text-foreground data-[state=active]:shadow-none "
                    >
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
                                <StudentIcon :width="24" class="fill-current"/>
                                <span>Estudiantes: 215,118</span>
                            </div>
                            <div class="flex gap-2 sm:min-w-[240px]">
                                <NotebookText />
                                <span>Lecturas: 4</span>
                            </div>
                            <div class="flex gap-2 sm:min-w-[240px]">
                                <LayoutList/>
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
                            <span>{{ roadmapData?.description }}</span>
                            <div class="flex flex-col gap-2">
                                <span class="font-bold">¿Que vas a aprender?</span>
                                <ul class="list-disc list-inside">
                                    <li>Fundamentos de Go: Aprende a diseñar y programar tus propias pistas de carreras con Go.</li>
                                    <li>Fundamentos de TypeScript: Domina los encantos de TypeScript y crea aplicaciones web encantadoras.</li>
                                    <li>Resolución de problemas: Enfréntate a ejercicios prácticos como invertir cadenas, calcular edades espaciales y mucho más.</li>
                                    <li>Desarrollo práctico: Crea proyectos de código funcional en un entorno interactivo para aplicar tus nuevas habilidades.</li>
                                </ul>
                                <span>Este roadmap es perfecto tanto para principiantes como para aquellos que buscan perfeccionar sus conocimientos en Go y TypeScript. ¡Es tu turno de brillar en la pista de carreras!</span>
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
                                    <InstagramIcon class="fill-white w-4"/>
                                </Button>
                                <!--Twitter -->
                                <Button class="rounded-full w-8 h-8 p-0">
                                    <TwitterIcon class="fill-white w-4"/>
                                </Button>
                                <!-- Linkedin -->
                                <Button class="rounded-full w-8 h-8 p-0">
                                    <LinkedinIcon class="fill-white w-4"/>
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

            <Card class="bg-popover dark:bg-background border-[#E6C7FF] dark:border-border rounded-3xl">
                <CardContent class="p-6 inline-flex items-center justify-between w-full">
                    <div class="flex gap-3 w-9/12">
                        <Avatar class="bg-[#E7C9FF] dark:bg-secondary w-12 h-12">
                            <Rocket/>
                        </Avatar>
                        <div>
                            <h2 class="font-bold text-lg" >Comienza tu Aventura</h2>
                            <span class="text-sm" >En Cortex, te guiamos en cada paso para que aprendas de forma práctica y efectiva. ¡Comienza ahora y acelera tu camino hacia el dominio del código!</span>
                        </div>
                    </div>
                    <Button class="gap-2 rounded-full">
                        <span>Get Started</span>
                        <ArrowRight :size="18"/>
                    </Button>
                </CardContent>
            </Card>
        </div>
        <div class="w-2/6 self-stretch bg-background border-l px-3 min-w-[322px] hidden lg:block">
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
                            <CirclePlay :size="26"/>
                        </div>
                    </AccordionContent>
                </AccordionItem>
            </Accordion>
            <div class="py-5 px-6 flex justify-between items-center border-b">
                <div class="flex items-center">
                    <h2 class="text-xl font-extrabold">Cursos Roadmap</h2>
                    <Dot :size=24 :stroke-width="3"/>
                    <h2 class="text-xl font-semibold">{{ roadmapData?.courses.length }}</h2>
                </div>
                <EllipsisVertical :size=24 class="stroke-muted" />
            </div>
            <Accordion type="multiple" collapsible>
                <AccordionItem
                    v-for="(course, index) in roadmapData?.courses"
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
                    <AccordionContent class="border-t py-4 px-6 ">
                        <div v-for="(module, moduleIndex) in course.modules" class="flex justify-between items-center">
                            <div class="flex flex-col gap-1">
                                <span class="font-bold text-base">{{ index + 1 }}.{{ moduleIndex + 1 }} {{ module.name }}</span>
                                <div class="flex">
                                    <span>{{ module.lesson_count }} lección</span>
                                    <Dot/>
                                    <span>{{ module.lessons.reduce((total, lesson) => total + lesson.exercises.length, 0) }} ejercicios</span>
                                </div>
                            </div>
                            <ModuleIcon :width="28" class="fill-current"/>
                        </div>
                        <div v-if="course.modules.length === 0" class="flex justify-center items-center px-6">
                            <span class="font-bold text-lg">No hay módulos disponibles</span>
                        </div>
                    </AccordionContent>
                </AccordionItem>
                <div v-if="roadmapData?.courses.length === 0" class="flex justify-center items-center px-6">
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