<script setup lang="ts">
import FilterSiderbar from "~/components/explore-roadmaps/FilterSidebar.vue";
import RoadmapList from "~/components/explore-roadmaps/RoadmapList.vue";
</script>


<template>
    <section>
        <div class="w-full flex-col justify-start items-start gap-[25px] inline-flex">
        <div class="px-10 justify-start items-center gap-2 inline-flex">
            <div class="w-8 h-8 p-4 rounded-[123px] justify-center items-center gap-2.5 flex">
                <div class="w-[18.94px] h-5 relative"></div>
            </div>
            <div class="w-5 h-5 justify-center items-center flex">
                <div class="w-5 h-5 relative flex-col justify-start items-start flex"></div>
            </div>
            <div class="justify-start items-center gap-1 flex">
                <div class="text-center text-slate-600 text-sm font-bold leading-tight">Roadmaps
                </div>
            </div>
            <div class="w-5 h-5 justify-center items-center flex">
                <div class="w-5 h-5 relative flex-col justify-start items-start flex"></div>
            </div>
            <div class="justify-start items-center gap-1 flex">
                <div class="text-center text-[#381653] text-sm font-bold leading-tight">Explore Roadmaps</div>
            </div>
        </div>
        <div
            class="self-stretch h-[196px] px-[38px] py-[30px] flex-col justify-center items-center gap-[22px] flex">
            <div class="text-primary text-5xl font-semibold">Explore Roadmaps</div>
            <div class="self-stretch text-center text-neutral-700 text-xl font-normal">Descubre
                nuestros Roadmaps interactivos y empieza tu viaje de aprendizaje paso a paso. Desde los fundamentos
                hasta niveles avanzados, te guiamos por el camino hacia el dominio de las tecnologías más demandadas
            </div>
        </div>

        <div class="self-stretch h-[1504px] px-10 bg-[#f8efff] flex-col justify-start items-center gap-[30px] flex">
            <div class="self-stretch py-[19px] justify-start items-start gap-2.5 inline-flex">
                <!-- Seccion Filtros -->
                <FilterSiderbar/>
                <!-- Seccion Roadmaps -->
                <RoadmapList/>
            </div>
        </div>
    </div>
    </section>
</template>
