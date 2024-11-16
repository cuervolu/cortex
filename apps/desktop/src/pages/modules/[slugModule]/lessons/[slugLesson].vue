<script lang="ts" setup>

import HomeIcon from "@cortex/shared/components/icons/HomeIcon.vue";

const route = useRoute()
const slugModule = route.params.slugModule as string
const slug = route.params.slugLesson as string

const { handleError } = useDesktopErrorHandler()

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
    </section>

</template>

<style>

.prose code {
    @apply bg-secondary text-white px-2 py-1 rounded-sm hover:bg-secondary/70;
}

.pre code {
    @apply bg-transparent hover:bg-transparent
}

.prose code::before,
.prose code::after {
    content: none;
}

</style>