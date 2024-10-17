<script setup lang="ts">
import VueIcon from "~/components/icons/VueIcon.vue";
import ReactIcon from "~/components/icons/ReactIcon.vue";
import TypescriptIcon from "~/components/icons/TypescriptIcon.vue";
import HtmlIcon from "~/components/icons/HtmlIcon.vue";
import PythonIcon from "~/components/icons/PythonIcon.vue";
import KotlinIcon from "~/components/icons/KotlinIcon.vue";
import CSharpIcon from "~/components/icons/CSharpIcon.vue";
import JavaIcon from "~/components/icons/JavaIcon.vue";
</script>

<template>
  <section class="relative py-20 px-4 text-center min-h-screen overflow-hidden text-foreground">
    <div class="max-w-3xl mx-auto bg-muted rounded-lg shadow-lg p-8 relative z-10">
      <h1 class="text-4xl mb-4">
        A <span class="font-bold">unique</span> approach to learning Computer Science
      </h1>
      <p class="mb-8">
        Learn at your own pace with unlimited access, available anytime on both the website and
        desktop. Revisit the material whenever you need, with no time restrictions.
      </p>
      <div class="space-x-4">
        <Button variant="default">Get started</Button>
        <Button variant="outline">Explore Roadmaps</Button>
      </div>
    </div>

    <vue-icon class="icon-float absolute top-20 left-10 w-16 h-16 opacity-50" />
    <react-icon class="icon-float absolute top-1/4 left-1/4 w-20 h-20 opacity-50" />
    <typescript-icon class="icon-float absolute bottom-1/4 left-16 w-14 h-14 opacity-50" />
    <html-icon class="icon-float absolute bottom-20 left-1/3 w-16 h-16 opacity-50" />
    <python-icon class="icon-float absolute top-32 right-1/4 w-16 h-16 opacity-50" />
    <kotlin-icon class="icon-float absolute top-1/3 right-16 w-14 h-14 opacity-50" />
    <c-sharp-icon class="icon-float absolute bottom-1/3 right-1/4 w-16 h-16 opacity-50" />
    <java-icon class="icon-float absolute bottom-24 right-20 w-16 h-16 opacity-50" />
  </section>
</template>

<style scoped>
@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.icon-float {
  animation: float 5s ease-in-out infinite;
}

section > *:not(.max-w-3xl) {
  animation: float 6s ease-in-out infinite;
  opacity: 0.85;
}

section > *:hover {
  opacity: 1;
}
</style>