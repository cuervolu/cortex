<script setup lang="ts">
import type { Exercise } from "@cortex/shared/types";

defineProps<{ exercise: Exercise }>();
</script>

<template>
  <div class="p-4">
    <MDC
        :value="exercise.hints || 'No hay pistas disponibles para este ejercicio.'"
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
        tag="article"
    />
  </div>
</template>