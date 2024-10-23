<script setup lang="ts">
import {BookOpen, Rocket, User} from 'lucide-vue-next'
import {useUserStore} from "~/stores";
import {computed} from "vue";

const userStore = useUserStore();
const userUsername = computed(() => userStore.user?.username ? `${userStore.user.username}` : 'No username')
</script>

<template>
  <div
      class="self-stretch grow shrink basis-0 p-[30px] justify-between items-start flex flex-wrap h-full">
    <!-- Sección izquierda -->
    <div
        class="grow shrink basis-0 self-stretch min-w-[600px] w-full md:w-auto px-[43px] flex-col justify-center items-start gap-[27px] inline-flex">
      <div
          class="self-stretch text-foreground text-[58px] font-semibold font-['Axiforma'] leading-[62px]">
        Bienvenido, {{ userUsername }}
      </div>
      <div
          class="self-stretch text-foreground text-2xl font-light font-['Axiforma'] leading-[35px]">
        Tu plataforma de aprendizaje personalizado. Explora, aprende y crece con nuestros roadmaps
        educativos.
      </div>
      <div class="self-stretch justify-start items-center gap-[18px] inline-flex w-full">
        <!-- Botón 'Explorar Roadmaps' -->
        <NuxtLink to="/roadmap" class="w-full">
          <Button
              class="w-full h-[61px] px-5 py-[13px] bg-primary rounded-[14px] justify-center items-center gap-2.5 flex"
              size="lg">
            <BookOpen class="mr-2 h-5 w-5"/>
            <div class="text-white text-lg font-medium font-['Axiforma'] leading-[35px]">Explorar
              Roadmaps
            </div>
          </Button>
        </NuxtLink>
        <!-- Botón 'Mi Perfil' -->
        <NuxtLink to="/profile" class="w-full">
          <Button
              variant="outline"
              class="w-full h-[61px] px-5 py-[13px] rounded-[14px] border border-border-color justify-center items-center gap-2.5 flex"
              size="lg">
            <User class="mr-2 h-5 w-5"/>
            <div class="text-foreground text-lg font-medium font-['Axiforma'] leading-[35px]">Mi
              Perfil
            </div>
          </Button>
        </NuxtLink>
      </div>
      <div
          class="self-stretch text-muted-foreground text-base font-light font-['Axiforma'] leading-snug">
        ¿Listo para comenzar tu viaje? Elige un roadmap o personaliza tu perfil para obtener
        recomendaciones adaptadas a
        ti.
      </div>
      <NuxtLink to="/exercises">
        <Button
            variant="outline"
            class="w-full h-[61px] px-5 py-[13px] rounded-[14px] border border-border-color justify-center items-center gap-2.5 flex"
            size="lg">
          <User class="mr-2 h-5 w-5"/>
          <div class="text-foreground text-lg font-medium font-['Axiforma'] leading-[35px]">
            Ejercicios (DEBUG)
          </div>
        </Button>
      </NuxtLink>
    </div>

    <!-- Sección derecha (imagen) -->
    <div
        class="grow shrink basis-0 self-stretch min-w-[310px] max-w-[908px] w-full md:w-auto px-[42px] py-[38px] bg-gradient-to-b from-background to-background-muted rounded-[35px] justify-center items-end gap-2.5 flex relative">
      <img
          src="https://picsum.photos/800/600" alt="Ilustración de aprendizaje"
          class="absolute inset-0 w-full h-full object-cover rounded-3xl">
      <div class="absolute inset-0 bg-gradient-to-t from-background to-transparent"/>
      <div
          class="grow shrink basis-0 px-[35px] py-[25px] bg-background rounded-[22px] flex-col justify-start items-start gap-[18px] inline-flex z-10 backdrop-blur-sm bg-opacity-80">
        <div class="self-stretch justify-start items-center gap-5 inline-flex">
          <Rocket class="mr-2 h-6 w-6 text-primary"/>
          <div
              class="grow shrink basis-0 text-foreground text-2xl font-semibold font-['Axiforma'] leading-[30px]">
            Comienza tu aventura
          </div>
        </div>
        <div
            class="self-stretch text-muted-foreground text-base font-light font-['Axiforma'] leading-snug">
          Cortex te guía paso a paso en tu camino de aprendizaje. Descubre nuevos temas, domina
          habilidades y alcanza
          tus metas educativas.
        </div>
      </div>
    </div>
  </div>

</template>