<script setup lang="ts">
import HomeIcon from '@cortex/shared/components/icons/HomeIcon.vue';

import { Flag, ThumbsDown, ThumbsUp } from 'lucide-vue-next';

const route = useRoute()
const slug = route.params.slug as string
const slugCourse = route.params.slugCourse as string

const { handleError } = useDesktopErrorHandler()

const { roadmap, fetchRoadmap, loading } = useRoadmaps()

const course = computed(() => {
    if (!roadmap.value) return null;
    return roadmap.value.courses.find((course) => course.slug === slugCourse);
});

onMounted(async () => {
    try {
        await fetchRoadmap(slug)
    } catch (err) {
        await handleError(err, {
            statusCode: 404,
            data: { slug },
        })
    }
})

</script>

<template>
    <section v-if="loading">
        <div>Loading...</div>
    </section>

    <section v-else class="w-full h-screen px-10 py-5 justify-start items-start gap-[30px] flex flex-col">
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
                            {{ roadmap?.title }}
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
                    <BreadcrumbPage>{{ course?.name }}</BreadcrumbPage>
                </BreadcrumbItem>
            </BreadcrumbList>
        </Breadcrumb>
        <div class="w-full flex gap-7">
            
            <div class="self-stretch bg-background w-full">
                <Tabs defaultValue="overview" orientation="vertical" class="w-full h-full flex gap-4">
                    <div class="border-r flex flex-col items-start">
                        <TabsList class="flex flex-col max-w-md h-full bg-transparent p-0 pr-3 space-y-2 justify-start">
                            <TabsTrigger value="overview" class="w-full justify-start text-base font-bold data-[state=active]:bg-secondary data-[state=active]:text-white">
                                {{ course?.name }}
                            </TabsTrigger>
                            <TabsTrigger v-for="module in course?.modules?.sort((a, b) => (a.display_order ?? 0) - (b.display_order ?? 0))" :key="module.id" :value="`module-${module.id}`"
                                class="w-full justify-start text-sm data-[state=active]:bg-secondary data-[state=active]:text-white">
                                {{ module.name }}
                            </TabsTrigger>
                        </TabsList>
                    </div>

                    <div class="w-full self-stretch overflow-y-auto">
                        <TabsContent value="overview" class="flex flex-col gap-7 m-0 w-full">
                            <h1 class="font-bold text-4xl">{{ course?.name }}</h1>

                            <Card class="rounded-3xl border-2 overflow-hidden">
                                <CardHeader class="p-0">
                                    <img :src="course?.image_url || 'https://placehold.co/600x400'" :alt="course?.name"
                                        class="relative h-72 object-cover" />
                                </CardHeader>
                                <CardFooter class="border-t-2 justify-end items-center px-4 py-5">
                                    <div class="flex gap-4">
                                        <div class="flex justify-start items-center gap-2">
                                            <Share :size="22" class="stroke-primary dark:stroke-current" />
                                            <span class="font-bold text-primary dark:text-current">Compartir</span>
                                        </div>
                                        <div class="flex gap-2">
                                            <Button variant="outline" size="icon"
                                                class="rounded-full w-11 h-11 hover:bg-foreground/20">
                                                <ThumbsUp class="stroke-foreground" />
                                            </Button>
                                            <Button variant="outline" size="icon"
                                                class="rounded-full w-11 h-11 hover:bg-foreground/20">
                                                <ThumbsDown class="stroke-foreground" />
                                            </Button>
                                            <Button variant="outline" size="icon"
                                                class="rounded-full w-11 h-11 hover:bg-foreground/20">
                                                <Flag class="stroke-foreground" />
                                            </Button>
                                        </div>
                                    </div>
                                </CardFooter>
                            </Card>

                            <div class="flex">
                                <span class="font-bold text-lg w-3/5">Descripción</span>
                                <div class="flex-col flex justify-between gap-3 text-sm w-3/4">
                                    <span>{{ course?.description }}</span>
                                </div>
                            </div>
                            <Separator />
                        </TabsContent>

                        <TabsContent v-for="module in course?.modules" :key="module.id" :value="`module-${module.id}`" class="m-0 w-full flex gap-4">
                            <div class="flex flex-col gap-6 w-full">
                                <div class="flex justify-between items-start">
                                    <div>
                                        <h2 class="text-3xl font-bold">{{ module.name }}</h2>
                                        <p class="text-muted-foreground text-left text- mt-2 w-full lg:w-4/6">{{ module.description }}</p>
                                    </div>
                                    <Badge variant="outline" class="text-sm text-nowrap">
                                        {{ module.lesson_count }} lecciones
                                    </Badge>
                                </div>

                                <Card >
                                    <CardHeader>
                                        <CardTitle>Contenido del módulo</CardTitle>
                                    </CardHeader>
                                    <CardContent class="p-0">
                                        <div v-for="(lesson, index) in module.lessons?.sort((a, b) => (a.display_order ?? 0) - (b.display_order ?? 0))" :key="lesson.id">
                                            <Separator v-if="index > 0" />
                                            <NuxtLink :to="`/modules/${module.slug}/lessons/${lesson.slug}`">
                                                <div class="p-4 flex items-center justify-between hover:bg-muted/50 transition-colors cursor-pointer">
                                                    <div>
                                                        <h4 class="font-medium">{{ lesson.name }}</h4>
                                                        <p class="text-sm text-muted-foreground">
                                                            Credits: {{ lesson.credits }}
                                                        </p>
                                                    </div>
                                                    <ChevronRight class="w-5 h-5 text-muted-foreground" />
                                                </div>
                                            </NuxtLink>
                                        </div>
                                    </CardContent>
                                </Card>
                            </div>
                        </TabsContent>
                    </div>
                </Tabs>
            </div>
        </div>

    </section>
</template>