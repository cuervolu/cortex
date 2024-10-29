<script setup lang="ts">
import FilterSiderbar from "~/components/explore-roadmaps/FilterSidebar.vue";
import RoadmapList from "~/components/explore-roadmaps/RoadmapList.vue";
import HomeIcon from "~/components/icons/HomeIcon.vue";
</script>


<template>
    <section>
        <div class="w-full flex-col justify-start items-start gap-[25px] inline-flex">
            <Breadcrumb class="px-10 py-5">
                <BreadcrumbList>
                <BreadcrumbItem>
                    <BreadcrumbLink>
                    <a href="/">
                        <HomeIcon class="w-[18px] fill-current"/>
                    </a>
                    </BreadcrumbLink>
                </BreadcrumbItem>
                <BreadcrumbSeparator />
                <BreadcrumbItem>
                    <BreadcrumbLink>
                    <a href="/docs/components/accordion.html">
                        Roadmaps
                    </a>
                    </BreadcrumbLink>
                </BreadcrumbItem>
                <BreadcrumbSeparator />
                <BreadcrumbItem>
                    <BreadcrumbPage>Explore Roadmaps</BreadcrumbPage>
                </BreadcrumbItem>
                </BreadcrumbList>
            </Breadcrumb>

            <div
                class="self-stretch w-full px-[38px] py-[30px] flex-col justify-center md:items-center items-start gap-[22px] flex">
                <div class="text-primary dark:text-[#E1E0E0] text-5xl font-semibold">Explore Roadmaps</div>
                <div>
                    <div class="self-stretch md:text-center max-w-[900px] text-left text-neutral-700 dark:text-[#BEBEBE] text-xl font-normal">Descubre
                        nuestros Roadmaps interactivos y empieza tu viaje de aprendizaje paso a paso. Desde los fundamentos
                        hasta niveles avanzados, te guiamos por el camino hacia el dominio de las tecnologías más demandadas
                    </div>
                </div>
            </div>

            <div class="self-stretch p-5 rounded-3xl mx-10 bg-[#f8efff] dark:bg-[#361C4A] flex-col justify-start items-center gap-[30px] flex">
                <div class="self-stretch py-[19px] justify-start items-start gap-2.5 inline-flex">
                    <!-- Seccion Filtros -->
                    <FilterSiderbar class="hidden lg:flex"/>
                    <!-- Seccion Roadmaps -->
                    <RoadmapList/>
                </div>
            </div>
        </div>
    </section>
</template>
