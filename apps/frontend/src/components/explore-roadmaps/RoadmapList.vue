<script setup>
import { ref, computed } from 'vue'
import RoadmapCard from '~/components/explore-roadmaps/RoadmapCard.vue'
import PaginatedRoadmaps from '~/components/explore-roadmaps/RoadmapPagination.vue'

const roadmaps = ref([
    // Tus datos de roadmaps aquí...
    {
        id: 1,
        title: "Frontend Frameworks: React, Vue.js & Angular",
        image: "https://blog.facialix.com/wp-content/uploads/2024/06/curso_golang_programacion_lenguaje_udemy_gratis.jpg",
        tags: ["Rendimiento", "Concurrencia", "Go", "Backend Development", "Intermedio"],
        rating: 4.5,
        reviews: 17,
        courses: 4,
        instructor: {
            name: "Hamir Llanos",
            avatar: "https://d2zp5xs5cp8zlg.cloudfront.net/image-79322-800.jpg"
        }
    },
    {
        id: 2,
        title: "Frontend Frameworks: React, Vue.js & Angular",
        image: "https://blog.facialix.com/wp-content/uploads/2024/06/curso_golang_programacion_lenguaje_udemy_gratis.jpg",
        tags: ["Rendimiento", "Concurrencia", "Go", "Backend Development", "Intermedio"],
        rating: 4.5,
        reviews: 17,
        courses: 4,
        instructor: {
            name: "Hamir Llanos",
            avatar: "https://d2zp5xs5cp8zlg.cloudfront.net/image-79322-800.jpg"
        }
    },
    {
        id: 3,
        title: "Frontend Frameworks: React, Vue.js & Angular",
        image: "https://blog.facialix.com/wp-content/uploads/2024/06/curso_golang_programacion_lenguaje_udemy_gratis.jpg",
        tags: ["Rendimiento", "Concurrencia", "Go", "Backend Development", "Intermedio"],
        rating: 4.5,
        reviews: 17,
        courses: 4,
        instructor: {
            name: "Hamir Llanos",
            avatar: "https://d2zp5xs5cp8zlg.cloudfront.net/image-79322-800.jpg"
        }
    },
    {
        id: 4,
        title: "Frontend Frameworks: React, Vue.js & Angular",
        image: "https://blog.facialix.com/wp-content/uploads/2024/06/curso_golang_programacion_lenguaje_udemy_gratis.jpg",
        tags: ["Rendimiento", "Concurrencia", "Go", "Backend Development", "Intermedio"],
        rating: 4.5,
        reviews: 17,
        courses: 4,
        instructor: {
            name: "Hamir Llanos",
            avatar: "https://d2zp5xs5cp8zlg.cloudfront.net/image-79322-800.jpg"
        }
    },
    {
        id: 5,
        title: "Frontend Frameworks: React, Vue.js & Angular",
        image: "https://blog.facialix.com/wp-content/uploads/2024/06/curso_golang_programacion_lenguaje_udemy_gratis.jpg",
        tags: ["Rendimiento", "Concurrencia", "Go", "Backend Development", "Intermedio"],
        rating: 4.5,
        reviews: 17,
        courses: 4,
        instructor: {
            name: "Hamir Llanos",
            avatar: "https://d2zp5xs5cp8zlg.cloudfront.net/image-79322-800.jpg"
        }
    },
    {
        id: 6,
        title: "Frontend Frameworks: React, Vue.js & Angular",
        image: "https://blog.facialix.com/wp-content/uploads/2024/06/curso_golang_programacion_lenguaje_udemy_gratis.jpg",
        tags: ["Rendimiento", "Concurrencia", "Go", "Backend Development", "Intermedio"],
        rating: 4.5,
        reviews: 17,
        courses: 4,
        instructor: {
            name: "Hamir Llanos",
            avatar: "https://d2zp5xs5cp8zlg.cloudfront.net/image-79322-800.jpg"
        }
    },
    {
        id: 7,
        title: "Frontend Frameworks: React, Vue.js & Angular",
        image: "https://blog.facialix.com/wp-content/uploads/2024/06/curso_golang_programacion_lenguaje_udemy_gratis.jpg",
        tags: ["Rendimiento", "Concurrencia", "Go", "Backend Development", "Intermedio"],
        rating: 4.5,
        reviews: 17,
        courses: 4,
        instructor: {
            name: "Hamir Llanos",
            avatar: "https://d2zp5xs5cp8zlg.cloudfront.net/image-79322-800.jpg"
        }
    },
    {
        id: 8,
        title: "Frontend Frameworks: React, Vue.js & Angular",
        image: "https://blog.facialix.com/wp-content/uploads/2024/06/curso_golang_programacion_lenguaje_udemy_gratis.jpg",
        tags: ["Rendimiento", "Concurrencia", "Go", "Backend Development", "Intermedio"],
        rating: 4.5,
        reviews: 17,
        courses: 4,
        instructor: {
            name: "Hamir Llanos",
            avatar: "https://d2zp5xs5cp8zlg.cloudfront.net/image-79322-800.jpg"
        }
    },
    {
        id: 9,
        title: "Frontend Frameworks: React, Vue.js & Angular",
        image: "https://blog.facialix.com/wp-content/uploads/2024/06/curso_golang_programacion_lenguaje_udemy_gratis.jpg",
        tags: ["Rendimiento", "Concurrencia", "Go", "Backend Development", "Intermedio"],
        rating: 4.5,
        reviews: 17,
        courses: 4,
        instructor: {
            name: "Hamir Llanos",
            avatar: "https://d2zp5xs5cp8zlg.cloudfront.net/image-79322-800.jpg"
        }
    },
    {
        id: 10,
        title: "Frontend Frameworks: React, Vue.js & Angular",
        image: "https://blog.facialix.com/wp-content/uploads/2024/06/curso_golang_programacion_lenguaje_udemy_gratis.jpg",
        tags: ["Rendimiento", "Concurrencia", "Go", "Backend Development", "Intermedio"],
        rating: 4.5,
        reviews: 17,
        courses: 4,
        instructor: {
            name: "Hamir Llanos",
            avatar: "https://d2zp5xs5cp8zlg.cloudfront.net/image-79322-800.jpg"
        }
    },
    {
        id: 11,
        title: "Frontend Frameworks: React, Vue.js & Angular",
        image: "https://blog.facialix.com/wp-content/uploads/2024/06/curso_golang_programacion_lenguaje_udemy_gratis.jpg",
        tags: ["Rendimiento", "Concurrencia", "Go", "Backend Development", "Intermedio"],
        rating: 4.5,
        reviews: 17,
        courses: 4,
        instructor: {
            name: "Hamir Llanos",
            avatar: "https://d2zp5xs5cp8zlg.cloudfront.net/image-79322-800.jpg"
        }
    },
    {
        id: 12,
        title: "Frontend Frameworks: React, Vue.js & Angular",
        image: "https://blog.facialix.com/wp-content/uploads/2024/06/curso_golang_programacion_lenguaje_udemy_gratis.jpg",
        tags: ["Rendimiento", "Concurrencia", "Go", "Backend Development", "Intermedio"],
        rating: 4.5,
        reviews: 17,
        courses: 4,
        instructor: {
            name: "Hamir Llanos",
            avatar: "https://d2zp5xs5cp8zlg.cloudfront.net/image-79322-800.jpg"
        }
    },
    {
        id: 13,
        title: "Frontend Frameworks: React, Vue.js & Angular",
        image: "https://blog.facialix.com/wp-content/uploads/2024/06/curso_golang_programacion_lenguaje_udemy_gratis.jpg",
        tags: ["Rendimiento", "Concurrencia", "Go", "Backend Development", "Intermedio"],
        rating: 4.5,
        reviews: 17,
        courses: 4,
        instructor: {
            name: "Hamir Llanos",
            avatar: "https://d2zp5xs5cp8zlg.cloudfront.net/image-79322-800.jpg"
        }
    },
    {
        id: 14,
        title: "Frontend Frameworks: React, Vue.js & Angular",
        image: "https://blog.facialix.com/wp-content/uploads/2024/06/curso_golang_programacion_lenguaje_udemy_gratis.jpg",
        tags: ["Rendimiento", "Concurrencia", "Go", "Backend Development", "Intermedio"],
        rating: 4.5,
        reviews: 17,
        courses: 4,
        instructor: {
            name: "Hamir Llanos",
            avatar: "https://d2zp5xs5cp8zlg.cloudfront.net/image-79322-800.jpg"
        }
    },
    {
        id: 15,
        title: "Frontend Frameworks: React, Vue.js & Angular",
        image: "https://blog.facialix.com/wp-content/uploads/2024/06/curso_golang_programacion_lenguaje_udemy_gratis.jpg",
        tags: ["Rendimiento", "Concurrencia", "Go", "Backend Development", "Intermedio"],
        rating: 4.5,
        reviews: 17,
        courses: 4,
        instructor: {
            name: "Hamir Llanos",
            avatar: "https://d2zp5xs5cp8zlg.cloudfront.net/image-79322-800.jpg"
        }
    },

])

const itemsPerPage = 10
const currentPage = ref(1)


const paginatedRoadmaps = computed(() => {
    const start = (currentPage.value - 1) * itemsPerPage
    const end = start + itemsPerPage
    return roadmaps.value.slice(start, end)
})


</script>

<template>
    <main class="flex overflow-hidden flex-col flex-1 shrink p-2.5 basis-0 min-w-[240px] max-md:max-w-full">
        <header class="flex flex-wrap gap-10 justify-between items-center w-full max-md:max-w-full">
            <h1 class="self-stretch my-auto text-xl font-medium">{{ roadmaps.length }} Roadmaps</h1>
            <Select>
                <SelectTrigger class="w-[180px]">
                <SelectValue placeholder="Ordenar por:" />
                </SelectTrigger>
                <SelectContent>
                <SelectGroup>
                    <SelectLabel>Ordenar</SelectLabel>
                    <SelectItem value="recent">
                        Más reciente                 
                    </SelectItem>
                    <SelectItem value="oldest">
                        Más antiguo
                    </SelectItem>
                    <SelectItem value="popular">
                        Más popular
                    </SelectItem>
                    <SelectItem value="highestRated">
                        Mejor valorado
                    </SelectItem>
                </SelectGroup>
                </SelectContent>
            </Select>
        </header>

        <section class="flex flex-wrap gap-7 items-start mt-2.5 w-full max-md:max-w-full">
            <RoadmapCard v-for="roadmap in paginatedRoadmaps" :key="roadmap.id" :roadmap="roadmap" />
        </section>

        <PaginatedRoadmaps />
    </main>
</template>