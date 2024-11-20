<script setup lang="ts">
import VueIcon from "~/components/icons/VueIcon.vue";
import ReactIcon from "~/components/icons/ReactIcon.vue";
import TypescriptIcon from "~/components/icons/TypescriptIcon.vue";
import HtmlIcon from "~/components/icons/HtmlIcon.vue";
import PythonIcon from "~/components/icons/PythonIcon.vue";
import KotlinIcon from "~/components/icons/KotlinIcon.vue";
import CSharpIcon from "~/components/icons/CSharpIcon.vue";
import JavaIcon from "~/components/icons/JavaIcon.vue";
import { ref } from 'vue'
</script>

<template>
  <section class="relative py-20 px-4 text-center min-h-screen overflow-hidden text-foreground">
    <div class="max-w-3xl bg-cyan-50 mx-auto bg-muted rounded-lg shadow-lg p-8 relative z-2">
      <h1 class="text-5xl mb-4">
        Un enfoque <span class="font-bold">único</span> para aprender Ciencias de la Computación
      </h1>
      <p class="text-gray-600 mb-8 leading-relaxed font-8">
        Aprende a tu propio ritmo con acceso ilimitado, disponible en cualquier momento tanto en el sitio web como en el escritorio. Revisa el material cuando lo necesites, sin restricciones de tiempo.
      </p>
    </div>
    
          <div class="space-x-4 py-10" >
        <NuxtLink to="/my-roadmaps">
          <Button class="bg-purple-900 hover:bg-purple-800 text-white px-6 rounded-full">Empieza aqui</Button>
        </NuxtLink>
        <NuxtLink to="/explore">
          <Button variant="outline" class="bg-white border-gray-200 hover:bg-gray-50 rounded-full text-purple-900">Explorar Roadmaps</Button>
        </NuxtLink>
      </div>

    <vue-icon class="icon-float absolute top-20 left-10 w-16 h-16 " />
    <react-icon class="icon-float absolute top-1/4 left-1/4 w-20 h-20 " />
    <typescript-icon class="icon-float absolute bottom-1/4 left-16 w-14 h-14 " />
    <html-icon class="icon-float absolute bottom-29 left-1/3 w-18 h-16 " />
    <python-icon class="icon-float absolute top-32 right-1/4 w-16 h-18 " />
    <kotlin-icon class="icon-float absolute top-1/3 right-16 w-14 h-14 " />
    <c-sharp-icon class="icon-float absolute bottom-1/3 right-1/4 w-16 h-16 " />
    <java-icon class="icon-float absolute bottom-29 right-20 w-16 h-20 " />
  </section>
  
</template>

<style scoped>
@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.icon-float {
  animation: float 5s ease-in-out infinite;
  filter: grayscale(20%);
  transition: all 0.3s ease;
}

section > *:not(.max-w-3xl) {
  animation: float 6s ease-in-out infinite;
}

.icon-float:hover {
  opacity: 0.6;
  filter: grayscale(0%);
}
</style>