<script setup lang="ts">
import { BookOpen, Rocket, User } from 'lucide-vue-next'
import { useUserStore } from "~/stores";
import { computed } from "vue";

const userStore = useUserStore();
const userUsername = computed(() => userStore.user?.username ? `${userStore.user.username}` : 'No username')
</script>

<template>
  <div class="flex flex-col md:flex-row min-h-screen">
    <div class="flex-1 flex flex-col justify-center p-10 md:p-20">
      <h1 class="text-4xl md:text-6xl font-bold text-foreground mb-6">Bienvenido, {{ userUsername }}</h1>
      <p class="text-xl md:text-2xl text-foreground mb-8">
        Tu plataforma de aprendizaje personalizado. Explora, aprende y crece con nuestros roadmaps
        educativos.
      </p>
      <div class="grid gap-4 md:grid-cols-2 mb-8">
        <NuxtLink to="/roadmap">
          <Button class="w-full text-lg py-6" size="lg">
            <BookOpen class="mr-2 h-5 w-5"/>
            Explorar Roadmaps
          </Button>
        </NuxtLink>
        <NuxtLink to="/profile">
          <Button variant="outline" class="w-full text-lg py-6" size="lg">
            <User class="mr-2 h-5 w-5"/>
            Mi Perfil
          </Button>
        </NuxtLink>
      </div>
      <p class="text-muted-foreground">
        ¿Listo para comenzar tu viaje? Elige un roadmap o personaliza tu perfil para obtener
        recomendaciones adaptadas a ti.
      </p>
    </div>
    <div class="flex-1 relative hidden md:block">
      <img
          src="https://picsum.photos/800/600"
          alt="Ilustración de aprendizaje"
          class="absolute inset-0 w-full h-full object-cover rounded-l-3xl"
      >
      <div class="absolute inset-0 bg-gradient-to-t from-background to-transparent"/>
      <div
          class="absolute bottom-10 left-10 right-10 bg-background/80 backdrop-blur-sm rounded-lg p-6">
        <h2 class="text-2xl font-semibold mb-2 flex items-center">
          <Rocket class="mr-2 h-6 w-6 text-primary"/>
          Comienza tu aventura
        </h2>
        <p class="text-muted-foreground">
          Cortex te guía paso a paso en tu camino de aprendizaje. Descubre nuevos temas, domina
          habilidades y alcanza tus metas educativas.
        </p>
      </div>
    </div>
  </div>
</template>