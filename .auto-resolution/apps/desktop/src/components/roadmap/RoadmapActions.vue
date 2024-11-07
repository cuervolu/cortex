<script setup lang="ts">
import { Bookmark, Share2, ThumbsUp, ThumbsDown } from 'lucide-vue-next'
</script>

<template>
  <div class="mb-6 flex gap-4">
    <Button variant="outline" class="flex gap-2">
      <Bookmark class="h-4 w-4" />
      Guardar Roadmap
    </Button>
    <Button variant="outline" class="flex gap-2">
      <Share2 class="h-4 w-4" />
      Compartir
    </Button>
    <Button variant="outline" size="icon">
      <ThumbsUp class="h-4 w-4" />
    </Button>
    <Button variant="outline" size="icon">
      <ThumbsDown class="h-4 w-4" />
    </Button>
  </div>
</template>