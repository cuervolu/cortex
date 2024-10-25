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
  <section class="relative py-20 px-4 text-center min-h-screen overflow-hidden bg-gradient-to-b from-blue-50/50 to-transparent">
    <div class="max-w-3xl mx-auto bg-teal-50 rounded-3xl shadow-md p-8 relative z-10 border-x-emerald-300">
      <h1 class="text-4xl font-light mb-6">
        A <span class="text-purple-900 font-medium">unique</span> approach to learning Computer Science
      </h1>
      <p class="text-gray-600 mb-8 leading-relaxed font-8">
        Learn at your own pace with unlimited access, available anytime on both the website and
        desktop. Revisit the material whenever you need, with no time restrictions.
      </p>
    </div>
    
          <div class="space-x-4 py-10" >
        <Button class="bg-purple-900 hover:bg-purple-800 text-white px-6 rounded-full">Get started</Button>
        <Button variant="outline" class="bg-white border-gray-200 hover:bg-gray-50 rounded-full text-purple-900">Explore Roadmaps</Button>
      </div>

    <vue-icon class="icon-float absolute top-20 left-10 w-16 h-16 opacity-40" />
    <react-icon class="icon-float absolute top-1/4 left-1/4 w-20 h-20 opacity-40" />
    <typescript-icon class="icon-float absolute bottom-1/4 left-16 w-14 h-14 opacity-40" />
    <html-icon class="icon-float absolute bottom-20 left-1/3 w-16 h-16 opacity-40" />
    <python-icon class="icon-float absolute top-32 right-1/4 w-16 h-16 opacity-40" />
    <kotlin-icon class="icon-float absolute top-1/3 right-16 w-14 h-14 opacity-40" />
    <c-sharp-icon class="icon-float absolute bottom-1/3 right-1/4 w-16 h-16 opacity-40" />
    <java-icon class="icon-float absolute bottom-24 right-20 w-16 h-16 opacity-40" />
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