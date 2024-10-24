<script setup>
import { ref } from 'vue'
import RoadmapCard from '~/components/explore-roadmaps/RoadmapCard.vue'

const roadmaps = ref([
    {
        id: 1,
        title: "Frontend Frameworks: React, Vue.js & Angular",
        image: "https://cdn.builder.io/api/v1/image/assets/TEMP/ff83ff870f8381227c65d76797121024c18daeb515259e44609d5ecf3f2425a9?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245",
        tags: ["Frontend Development", "Principiante"],
        rating: 4.5,
        reviews: 17,
        courses: 4,
        instructor: {
            name: "Hamir Llanos",
            avatar: "https://cdn.builder.io/api/v1/image/assets/TEMP/f1fd81e83ad8e7cea01d95f6881e29e5af6fd6ca4c5530993d512d5ca68be1ea?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
        }
    },
    {
        id: 2,
        title: "Frontend Frameworks: React, Vue.js & Angular",
        image: "https://cdn.builder.io/api/v1/image/assets/TEMP/ff83ff870f8381227c65d76797121024c18daeb515259e44609d5ecf3f2425a9?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245",
        tags: ["Frontend Development", "Principiante"],
        rating: 4.5,
        reviews: 17,
        courses: 4,
        instructor: {
            name: "Hamir Llanos",
            avatar: "https://cdn.builder.io/api/v1/image/assets/TEMP/f1fd81e83ad8e7cea01d95f6881e29e5af6fd6ca4c5530993d512d5ca68be1ea?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
        }
    },
    {
        id: 3,
        title: "Frontend Frameworks: React, Vue.js & Angular",
        image: "https://cdn.builder.io/api/v1/image/assets/TEMP/ff83ff870f8381227c65d76797121024c18daeb515259e44609d5ecf3f2425a9?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245",
        tags: ["Frontend Development", "Principiante"],
        rating: 4.5,
        reviews: 17,
        courses: 4,
        instructor: {
            name: "Hamir Llanos",
            avatar: "https://cdn.builder.io/api/v1/image/assets/TEMP/f1fd81e83ad8e7cea01d95f6881e29e5af6fd6ca4c5530993d512d5ca68be1ea?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
        }
    },
    {
        id: 4,
        title: "Frontend Frameworks: React, Vue.js & Angular",
        image: "https://cdn.builder.io/api/v1/image/assets/TEMP/ff83ff870f8381227c65d76797121024c18daeb515259e44609d5ecf3f2425a9?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245",
        tags: ["Frontend Development", "Principiante"],
        rating: 4.5,
        reviews: 17,
        courses: 4,
        instructor: {
            name: "Hamir Llanos",
            avatar: "https://cdn.builder.io/api/v1/image/assets/TEMP/f1fd81e83ad8e7cea01d95f6881e29e5af6fd6ca4c5530993d512d5ca68be1ea?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
        }
    },
    // Additional roadmap objects following the same structure...
])
</script>

<template>
    <main class="flex overflow-hidden flex-col flex-1 shrink p-2.5 basis-0 min-w-[240px] max-md:max-w-full">
        <header class="flex flex-wrap gap-10 justify-between items-center w-full max-md:max-w-full">
            <h1 class="self-stretch my-auto text-xl font-medium text-black">96 Roadmaps</h1>
            <form
                class="flex gap-10 items-center self-stretch py-2.5 pl-3 my-auto bg-white rounded-md border border-solid border-zinc-200 min-h-[40px] min-w-[300px]">
                <label for="sortSelect" class="sr-only">Sort by</label>
                <select id="sortSelect"
                    class="gap-2.5 self-stretch my-auto text-base text-zinc-950 bg-transparent border-none focus:outline-none">
                    <option value="popular">Más Popular</option>
                </select>
            </form>
        </header>

        <section class="flex flex-wrap gap-7 items-start mt-2.5 w-full max-md:max-w-full">
            <RoadmapCard v-for="roadmap in roadmaps" :key="roadmap.id" :roadmap="roadmap" />

            <Card
                class="grow shrink basis-0 p-[11px] bg-[#faf9f7] rounded-[18px] flex-col justify-start items-start gap-2.5 inline-flex">
                <CardHeader class="w-full p-2.5">
                    <img class="rounded-[16px] " src="https://via.placeholder.com/446x156" alt="">
                </CardHeader>
                <CardContent class="p-2.5 w-full">
                    <div class="flex items-center gap-1 justify-between">
                        <div class="gap-1.5 flex">
                            <Badge>Rendimiento</Badge>
                            <Badge>Concurrencia</Badge>
                            <Badge>Go</Badge>
                        </div>
                        <div class="gap-4 flex text-base font-medium whitespace-nowrap text-zinc-600">
                            <div class="flex gap-1.5 items-center self-stretch my-auto">
                                <span>4.2</span>
                                <img src="https://cdn.builder.io/api/v1/image/assets/TEMP/e1fbc9b605e0ec39c456cf7c0bab8bb44d1afbb592833d863afc385123c22e28?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                                    alt="" class="object-contain shrink-0 self-stretch my-auto aspect-square w-[18px]" />
                            </div>
                            <span>(17)</span>
                        </div>
                    </div>
                    <h2 class="text-xl font-medium text-black">Frontend Frameworks: React, Vue.js & Angular</h2>
                    <Separator/>
                </CardContent>
                <CardFooter class="w-full">
                    <div class="flex justify-between w-full">
                        <div class="flex items-center gap-1">
                            <img src="https://cdn.builder.io/api/v1/image/assets/TEMP/8df0ba3abe0734a95f222017195eea638ff7a139129c7058e229aa8f11a5afa2?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                                alt="" class="object-contain shrink-0 self-stretch my-auto w-5 aspect-square" />
                            <span>4 cursos</span>
                        </div>
                        <div class="flex items-center gap-1">
                            <Avatar></Avatar>
                            <span>by Hamir Llanos</span>
                        </div>
                    </div>
                </CardFooter>
            </Card>
        </section>

        <nav class="flex flex-wrap gap-1 justify-center items-center py-5 mt-2.5 w-full text-sm font-medium whitespace-nowrap text-zinc-950 max-md:max-w-full"
            aria-label="Pagination">
            <button
                class="flex gap-1 justify-center items-center self-stretch py-2 pr-4 pl-2.5 my-auto text-xl font-semibold rounded-md"
                aria-label="Previous page">
                <img src="https://cdn.builder.io/api/v1/image/assets/TEMP/0b702f294b59209c6027cb46aa2dd071cd453c69c7884b9eb681c6c2664c971f?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                    alt="" class="object-contain shrink-0 self-stretch my-auto w-4 aspect-square" />
                <span>Previous</span>
            </button>

            <button
                class="flex gap-2.5 justify-center items-center self-stretch px-2.5 py-3 my-auto w-10 rounded-md min-h-[40px]"
                aria-label="Page 1">1</button>
            <button
                class="flex gap-2.5 justify-center items-center self-stretch px-2.5 my-auto w-10 h-10 text-white rounded-md border border-solid bg-violet-950 border-zinc-200 min-h-[40px]"
                aria-current="page" aria-label="Current Page, Page 2">2</button>
            <button
                class="flex gap-2.5 justify-center items-center self-stretch px-2.5 py-3 my-auto w-10 rounded-md min-h-[40px]"
                aria-label="Page 3">3</button>
            <span
                class="flex gap-2.5 justify-center items-center self-stretch p-2.5 my-auto w-9 rounded-md min-h-[36px]"
                aria-hidden="true">...</span>

            <button
                class="flex gap-1 justify-center items-center self-stretch py-2 pr-4 pl-2.5 my-auto text-xl font-semibold rounded-md"
                aria-label="Next page">
                <span>Next</span>
                <img src="https://cdn.builder.io/api/v1/image/assets/TEMP/4013be0761472700690f7913162894cdd6d1096afef9164bf4f373ee5b77cd60?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                    alt="" class="object-contain shrink-0 self-stretch my-auto w-4 aspect-square" />
            </button>
        </nav>
    </main>
</template>