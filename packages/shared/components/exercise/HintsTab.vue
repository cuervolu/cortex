<script setup lang="ts">
import type {Exercise} from "@cortex/shared/types";
import type {Options} from "markdown-it";
import {VueMarkdownIt} from "@f3ve/vue-markdown-it";

defineProps<{ exercise: Exercise, options: Options }>();
</script>

<template>
  <div class="p-4">
    <VueMarkdownIt
      :source="exercise.hints"
      :options="options"
      class="prose prose-slate dark:prose-invert max-w-none
             prose-headings:font-bold prose-headings:no-underline
             prose-h1:text-2xl prose-h1:no-underline
             prose-h2:text-xl prose-h2:no-underline
             prose-h3:text-lg prose-h3:no-underline
             prose-p:text-base prose-p:leading-7
             prose-li:text-base
             prose-code:text-sm prose-code:bg-muted prose-code:px-1 prose-code:rounded
             prose-pre:bg-muted/50
             prose-strong:font-semibold
             prose-a:text-foreground hover:prose-a:text-foreground/80"
    />
  </div>
</template>

<style scoped>
.prose h2{
  margin-top: 10px
}
</style>