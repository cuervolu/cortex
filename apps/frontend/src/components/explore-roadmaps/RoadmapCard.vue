<script setup>
defineProps({
    roadmap: {
        type: Object,
        required: true
    }
})
</script>

<template>    
    <Card class="grow shrink basis-0 p-[11px] w-screen min-w-[382px] rounded-[18px] flex-col justify-start items-start gap-2.5 inline-flex">
        <CardHeader class="w-full p-2.5">
            <img class="rounded-[16px] min-h-[190px] max-h-[200px] object-cover" :src="roadmap.image" :alt="roadmap.title">
        </CardHeader>
        <CardContent class="flex gap-2.5 flex-col px-2.5 pb-2.5 w-full">
            <div class="flex items-center gap-1 justify-between">
                <div class="gap-1.5 flex md:max-w-[580px]">
                    <Badge v-for="(tag, index) in roadmap.tags.slice(0, 3)" :key="tag" class="line-clamp-1">{{ tag }}</Badge>
                </div>
                <div class="gap-4 flex text-base font-medium whitespace-nowrap">
                    <div class="flex gap-1.5 items-center self-stretch my-auto">
                        <span>{{ roadmap.rating }}</span>
                        <img src="https://cdn.builder.io/api/v1/image/assets/TEMP/e1fbc9b605e0ec39c456cf7c0bab8bb44d1afbb592833d863afc385123c22e28?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                            alt="" class="object-contain shrink-0 self-stretch my-auto aspect-square w-[18px]" />
                    </div>
                    <span>({{ roadmap.reviews }})</span>
                </div>
            </div>
            <h2 class="text-xl font-medium line-clamp-1">{{ roadmap.title }}</h2>
        </CardContent>
        <Separator/>
        <CardFooter class="w-full p-2.5">
            <div class="flex justify-between w-full font-medium">
                <div class="flex items-center gap-1">
                    <img src="https://cdn.builder.io/api/v1/image/assets/TEMP/8df0ba3abe0734a95f222017195eea638ff7a139129c7058e229aa8f11a5afa2?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                        alt="" class="object-contain shrink-0 self-stretch my-auto w-5 aspect-square" />
                    <span>{{ roadmap.courses }} cursos</span>
                </div>
                <div class="flex items-center gap-2">
                    <Avatar>
                        <AvatarImage :src="roadmap.instructor.avatar" />
                    </Avatar>
                    <span>by {{ roadmap.instructor.name }}</span>
                </div>
            </div>
        </CardFooter>
    </Card>
</template>