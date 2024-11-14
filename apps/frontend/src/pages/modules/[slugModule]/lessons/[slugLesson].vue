<script lang="ts" setup>

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
                <article class="prose prose-lg dark:prose-invert prose-img:rounded-xl prose-img:object-cover prose-a:no-underline prose-">
                    <MDC :value="lesson?.content || ''"></MDC>                
                </article>
                <!-- <Card class="bg-popover">
                    <CardHeader>
                        <div class="flex justify-between items-center">
                            <h1 class="text-3xl font-bold w-4/6 text-start">{{ lesson?.name }}</h1>
                            <Badge v-if="lesson?.credits" class="bg-primary text-base text-nowrap h-fit">{{ lesson?.credits }} créditos</Badge>
                        </div>
                    </CardHeader>
                    <CardContent class="p-6 prose prose-lg dark:prose-invert prose-img:rounded-xl prose-img:min-w-full prose-img:object-cover">
                        
                    </CardContent>
                </Card> -->
            </div>
        </div>
    </section>

</template>

