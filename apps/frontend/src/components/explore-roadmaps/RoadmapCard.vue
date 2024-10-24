<script setup>
defineProps({
    roadmap: {
        type: Object,
        required: true
    }
})
</script>

<template>
    <article
        class="flex overflow-hidden flex-col flex-1 shrink p-3 rounded-2xl basis-0 bg-stone-50 min-w-[382px] max-md:max-w-full">
        <img :src="roadmap.image" :alt="roadmap.title"
            class="object-contain gap-2.5 py-2.5 w-full rounded-3xl aspect-[2.86] max-md:max-w-full" />

        <div class="flex gap-10 justify-between items-center mt-2.5 w-full max-md:max-w-full">
            <div
                class="flex gap-1.5 items-center self-stretch my-auto text-xs font-semibold min-w-[240px] text-neutral-50">
                <span v-for="tag in roadmap.tags" :key="tag"
                    class="flex flex-col justify-center items-center self-stretch px-2.5 py-1 my-auto bg-purple-900 rounded-full min-h-[22px]">
                    {{ tag }}
                </span>
            </div>

            <div
                class="flex gap-4 items-center self-stretch my-auto text-base font-medium whitespace-nowrap text-zinc-600">
                <div class="flex gap-1.5 items-center self-stretch my-auto">
                    <span>{{ roadmap.rating }}</span>
                    <img src="https://cdn.builder.io/api/v1/image/assets/TEMP/e1fbc9b605e0ec39c456cf7c0bab8bb44d1afbb592833d863afc385123c22e28?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                        alt="" class="object-contain shrink-0 self-stretch my-auto aspect-square w-[18px]" />
                </div>
                <span>({{ roadmap.reviews }})</span>
            </div>
        </div>

        <h2 class="mt-2.5 text-xl font-medium text-black max-md:max-w-full">{{ roadmap.title }}</h2>

        <hr class="mt-2.5 w-full border-2 border-solid border-black border-opacity-30 min-h-[1px] max-md:max-w-full" />

        <footer
            class="flex gap-10 justify-between items-center mt-2.5 w-full text-base font-medium text-zinc-600 max-md:max-w-full">
            <div class="flex gap-2.5 items-center self-stretch my-auto">
                <img src="https://cdn.builder.io/api/v1/image/assets/TEMP/8df0ba3abe0734a95f222017195eea638ff7a139129c7058e229aa8f11a5afa2?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                    alt="" class="object-contain shrink-0 self-stretch my-auto w-5 aspect-square" />
                <span>{{ roadmap.courses }} cursos</span>
            </div>
            <div class="flex gap-2.5 items-center self-stretch my-auto">
                <img :src="roadmap.instructor.avatar" :alt="roadmap.instructor.name"
                    class="object-contain shrink-0 self-stretch my-auto w-9 aspect-square" />
                <span>by {{ roadmap.instructor.name }}</span>
            </div>
        </footer>
    </article>
</template>