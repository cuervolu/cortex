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
  <section class="relative py-20 px-4 mt-4 text-center overflow-hidden text-foreground">
    <div class="max-w-2xl mx-6 md:mx-auto card-hero card-hero-color rounded-3xl p-8 relative z-10">
      <h1 class="text-5xl mb-4 leading-snug dark:text-[#E1E0E0]">
        Un enfoque <span class="font-bold text-primary dark:text-[#F9CF87]">único</span> para aprender Ciencias de la Computación
      </h1>
      <p class="text-[#5F4B6F] dark:text-[#BEBEBE] mb-8 leading-relaxed font-8">
        Aprende a tu propio ritmo con acceso ilimitado, disponible en cualquier momento tanto en el sitio web como en el
        escritorio. Revisa el material cuando lo necesites, sin restricciones de tiempo.
      </p>
    </div>

    <div class="space-x-4 py-10 z-50">
      <NuxtLink to="/my-roadmaps">
        <Button class="bg-primary hover:bg-primary/90 text-white rounded-full text-sm py-6 shadow-xl md:text-base md:py-7 md:px-6 lg:text-lg lg:px-8">Empieza aquí</Button>
      </NuxtLink>
      <NuxtLink to="/explore">
        <Button variant="outline" class="bg-white border-none hover:bg-gray-50 rounded-full text-primary text-sm py-6 font-bold shadow-xl md:text-base md:py-7 md:px-6 lg:text-lg lg:px-8">Explorar Roadmaps</Button>
      </NuxtLink>
    </div>

    <vue-icon class="icon-float absolute top-20 left-[3%] w-16 h-16 " />
    <react-icon class="icon-float absolute top-36 left-[25%] w-20 h-20 md:left-[20%] " />
    <typescript-icon class="icon-float absolute top-72 left-[8%] w-14 h-14" />
    <html-icon class="icon-float absolute top-[24rem] left-[30%] w-18 h-16 md:left-[16%]" />
    <python-icon class="icon-float absolute top-36 right-[20%] w-32 md:right-[15%]" />
    <kotlin-icon class="icon-float absolute top-10 right-[3%] w-14 h-14" />
    <c-sharp-icon class="icon-float absolute top-[26rem] right-[35%] w-16 h-16 md:right-[25%]" />
    <java-icon class="icon-float absolute top-80 right-[8%] w-16 h-20" />
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
  filter: blur(1.149px);
}

section > *:not(.max-w-3xl) {
  animation: float 6s ease-in-out infinite;
}

.icon-float:hover {
  opacity: 0.6;
  filter: grayscale(0%);
}

.card-hero{
  border: 1px solid rgba(244, 244, 244, 0.60);
  box-shadow: 0px 4px 24px 0px rgba(0, 0, 0, 0.20);
  backdrop-filter: blur(4.800000190734863px);
}

.card-hero-color{
  background: linear-gradient(112deg, rgba(146, 210, 221, 0.12) 6.5%, rgba(146, 210, 221, 0.20) 93.45%);
}

.dark .card-hero-color{
  background: linear-gradient(112deg, rgba(249, 207, 135, 0.18) 6.5%, rgba(249, 207, 135, 0.33) 93.45%);
}
</style>