<script lang="ts" setup>

import { CircleCheck, CirclePlay } from "lucide-vue-next";
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import HomeIcon from "~/components/icons/HomeIcon.vue";

const route = useRoute()
const slugModule = route.params.slugModule as string
const slug = route.params.slugLesson as string

const { handleError } = useWebErrorHandler()

const { module, fetchModule } = useModules()

const { lesson, fetchLesson, loading } = useLessons()

onMounted(async () => {
    try {
        await fetchLesson(slug)
        await fetchModule(slugModule)
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

    <section v-else class="w-full self-stretch px-10 justify-start items-start gap-[30px] flex flex-col">
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
                            {{ module?.name }}
                        </NuxtLink>
                    </BreadcrumbLink>
                </BreadcrumbItem>
                <BreadcrumbSeparator />
                <BreadcrumbItem>
                    <BreadcrumbLink>
                        <NuxtLink to="javascript:history.back()">
                            Lessons
                        </NuxtLink>
                    </BreadcrumbLink>
                </BreadcrumbItem>
                <BreadcrumbSeparator />
                <BreadcrumbItem>
                    <BreadcrumbPage>{{ lesson?.name }}</BreadcrumbPage>
                </BreadcrumbItem>
            </BreadcrumbList>
        </Breadcrumb>

        <!-- Leccion -->

        <div class="w-full flex justify-center items-center">
            <div class="flex flex-col gap-5 w-fit">
                <article class="prose prose-lg dark:prose-invert prose-img:rounded-xl prose-img:object-cover prose-a:no-underline">
                    <MDC :value="lesson?.content || ''" class="prose-img:mx-auto"></MDC>            
                </article>
            </div>
        </div>

        <Sheet>
            <SheetTrigger class="fixed bottom-8 right-8 bg-primary/80 hover:bg-primary text-white text-lg font-bold py-2 px-4 rounded-full shadow-lg transition duration-300 ease-in-out transform hover:scale-105 focus:outline-none">
                Ejercicios
            </SheetTrigger>
            <SheetContent>
            <SheetHeader>
                <SheetTitle>Ejercicios</SheetTitle>
                <SheetDescription>
                    <NuxtLink v-for="(exercise, exerciseIndex) in [...(lesson?.exercises ?? [])]
                        .sort((a, b) => (a.display_order ?? 0) - (b.display_order ?? 0))"
                        :key="exerciseIndex" :class="[
                            'flex justify-between items-center py-3 gap-2 px-6 hover:bg-foreground/5',
                            exercise.is_completed ? 'border-l-4 border-primary bg-popover dark:bg-secondary' : 'border-b'
                        ]"
                        :to="`/exercises/${exercise.id}/solve`">
                        <div class="flex gap-3 items-center">
                            <LayoutList :size="28" class="stroke-current" />
                            <div class="flex flex-col">
                                <span class="font-bold text-lg">{{ exercise.title }}</span>
                                <span v-if="exercise.is_completed">Completado</span>
                                <span v-else>No Iniciado</span>
                            </div>
                        </div>
                        <CircleCheck v-if="exercise.is_completed" :size="28" class="stroke-current" />
                        <CirclePlay v-else :size="28" class="stroke-current" />
                    </NuxtLink>
                </SheetDescription>
            </SheetHeader>
            </SheetContent>
        </Sheet>
    </section>

</template>

<style>

.prose {
    @apply max-w-7xl;
}

.prose code {
    @apply bg-secondary text-white px-2 py-1 rounded-sm hover:bg-secondary/70;
}

.prose pre code {
    @apply bg-transparent hover:bg-transparent;
}

.prose code::before,
.prose code::after {
    content: none;
}

.prose img {
    @apply mx-auto rounded-xl object-cover;
}

.prose a {
    @apply no-underline;
}

.dark .prose {
    @apply prose-invert;
}

.prose {
    @apply prose-lg;
}

</style>

