<script setup lang="ts">
import RoadmapIcon from "~/components/icons/RoadmapIcon.vue";
import { navigationMenuTriggerStyle } from "@cortex/shared/components/ui/navigation-menu";

const { status } = useAuth()
const isAuthenticated = computed(() => status.value === 'authenticated')
</script>

<template>
  <div class="w-full px-10 py-5 flex-col justify-start items-center gap-2.5 inline-flex">
    <header class="self-stretch px-[39px] py-5 bg-[#fae7ef] dark:bg-[#361C4A] rounded-[30px] justify-between items-center inline-flex">
    <NuxtLink to="/">
      <div class="w-[127px] h-[36.89px] justify-between items-center inline-flex">
      <img src="~/assets/img/Cortex%20Logo.svg" alt="Cortex Logo" class="w-8 h-[36.89px]">
      <span class="w-[90px] h-[25px] font-bold text-[#381653] dark:text-[#FAF9F7] leading-7 tracking-[4px]">CORTEX</span>
    </div>
    </NuxtLink>

    <NavigationMenu>
      <NavigationMenuList class="gap-3">
        <NavigationMenuItem>
          <NavigationMenuTrigger class="bg-transparent font-medium text-base">Roadmaps</NavigationMenuTrigger>
          <NavigationMenuContent>
            <ul class="grid gap-3 p-6 md:w-[400px] lg:w-[500px] lg:grid-cols-[minmax(0,.75fr)_minmax(0,1fr)]">
              <li class="row-span-3">
                <NavigationMenuLink as-child>
                  <a
                    class="flex h-full w-full select-none flex-col justify-end rounded-md bg-gradient-to-b from-muted/50 to-muted p-6 no-underline outline-none focus:shadow-md"
                    href="/"
                  >
                    <RoadmapIcon class="w-[32px] fill-primary dark:fill-current" />
                    <div class="mb-2 mt-4 text-lg font-medium">
                      Roadmaps
                    </div>
                    <p class="text-sm leading-tight text-muted-foreground">
                      Descubre nuestros Roadmaps interactivos y empieza tu viaje de aprendizaje paso a paso.
                    </p>
                  </a>
                </NavigationMenuLink>
              </li>

              <li>
                <NavigationMenuLink as-child>
                  <a
                    href="/roadmaps"
                    class="block select-none space-y-1 rounded-md p-3 leading-none no-underline outline-none transition-colors hover:bg-accent hover:text-accent-foreground focus:bg-accent focus:text-accent-foreground"
                  >
                    <div class="text-sm font-medium leading-none">Explorar Roadmaps</div>
                    <p class="line-clamp-2 text-sm leading-snug text-muted-foreground">
                      Explora nuestros Roadmaps más populares para ti.
                    </p>
                  </a>
                </NavigationMenuLink>
              </li>
              <li>
                <NavigationMenuLink as-child>
                  <a
                    href="/my-roadmaps"
                    class="block select-none space-y-1 rounded-md p-3 leading-none no-underline outline-none transition-colors hover:bg-accent hover:text-accent-foreground focus:bg-accent focus:text-accent-foreground"
                  >
                    <div class="text-sm font-medium leading-none">Mis Roadmaps</div>
                    <p class="line-clamp-2 text-sm leading-snug text-muted-foreground">
                      Explora tus Roadmaps guardados y en progreso.
                    </p>
                  </a>
                </NavigationMenuLink>
              </li>
              <li>
                <NavigationMenuLink as-child>
                  <a
                    href="/docs/typography"
                    class="block select-none space-y-1 rounded-md p-3 leading-none no-underline outline-none transition-colors hover:bg-accent hover:text-accent-foreground focus:bg-accent focus:text-accent-foreground"
                  >
                    <div class="text-sm font-medium leading-none">Typography</div>
                    <p class="line-clamp-2 text-sm leading-snug text-muted-foreground">
                      Styles for headings, paragraphs, lists...etc
                    </p>
                  </a>
                </NavigationMenuLink>
              </li>
            </ul>
          </NavigationMenuContent>
        </NavigationMenuItem>
        <NavigationMenuItem>
          <LazyNavigationMenuLink as-child :class="navigationMenuTriggerStyle()" class="bg-transparent font-medium text-[16px]">
            <a href="/about">Acerca </a>
          </LazyNavigationMenuLink>
        </NavigationMenuItem>
        <NavigationMenuItem>
          <LazyNavigationMenuLink as-child :class="navigationMenuTriggerStyle()" class="bg-transparent font-medium text-[16px]">
            <a href="/mentors">Mentores</a>
          </LazyNavigationMenuLink>
        </NavigationMenuItem>
        <NavigationMenuItem>
          <LazyNavigationMenuLink as-child :class="navigationMenuTriggerStyle()" class="bg-transparent font-medium text-[16px]">
            <a href="/plans">Planes</a>
          </LazyNavigationMenuLink>
        </NavigationMenuItem>
        <NavigationMenuItem>
          <LazyNavigationMenuLink as-child :class="navigationMenuTriggerStyle()" class="bg-transparent font-medium text-[16px]">
            <a href="/blog">Blog</a>
          </LazyNavigationMenuLink>
        </NavigationMenuItem>
      </NavigationMenuList>
    </NavigationMenu>

      <template v-if="isAuthenticated">
        <UserMenu />
      </template>
      <template v-else>
        <Button as-child variant="default" class="bg-white text-center text-sm font-bold leading-7 rounded-[73px] px-8 py-3 h-[52px] text-[#381653] hover:bg-gray-100">
          <NuxtLink to="/auth/login">Comenzar</NuxtLink>
        </Button>
      </template>
  </header>
  </div>

</template>

<style scoped>

</style>