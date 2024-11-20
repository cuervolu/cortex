<script setup lang="ts">
import { Flag, Share, StickyNote, ThumbsDown, ThumbsUp } from 'lucide-vue-next';



defineProps<{
  imageUrl: string | null
  title: string
}>()
</script>

<template>
  <Card class="rounded-3xl border-2 overflow-hidden">
    <CardHeader class="p-0">
      <img :src="imageUrl || 'https://placehold.co/600x400'" :alt="title"
        class="relative h-[360px] object-cover" />
    </CardHeader>
    <CardFooter class="border-t-2 justify-between items-center px-4 py-5">
      <div class="flex justify-start items-center gap-2">
        <StickyNote :size="22" class="rotate-90" />
        <span class="font-bold">Guardar Roadmap</span>
      </div>
      <div class="flex gap-4">
        <div class="flex justify-start items-center gap-2">
          <Share :size="22" class="stroke-primary dark:stroke-current" />
          <span class="font-bold text-primary dark:text-current">Compartir</span>
        </div>
        <div class="flex gap-2">
          <Button
            class="rounded-full border-2 w-11 h-11 p-0 bg-transparent hover:bg-foreground/20">
            <ThumbsUp class="stroke-foreground" />
          </Button>
          <Button
            class="rounded-full border-2 w-11 h-11 p-0 bg-transparent hover:bg-foreground/20">
            <ThumbsDown class="stroke-foreground" />
          </Button>
          <Button
            class="rounded-full border-2 w-11 h-11 p-0 bg-transparent hover:bg-foreground/20">
            <Flag class="stroke-foreground" />
          </Button>
        </div>
      </div>
    </CardFooter>
  </Card>
</template>