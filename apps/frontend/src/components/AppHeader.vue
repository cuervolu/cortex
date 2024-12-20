<script setup lang="ts">
import { ref } from 'vue'
import RoadmapIcon from "~/components/icons/RoadmapIcon.vue";
import { navigationMenuTriggerStyle } from "@cortex/shared/components/ui/navigation-menu";
import { Menu, X } from 'lucide-vue-next'

const { status } = useAuth()
const isAuthenticated = computed(() => status.value === 'authenticated')
const isMenuOpen = ref(false)

const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value
}
</script>

<template>
  <div class="w-full px-10 py-5 flex-col justify-start items-center gap-2.5 inline-flex">
    <header class="relative self-stretch px-[39px] py-5 bg-[#fae7ef] dark:bg-[#361C4A] rounded-[30px] justify-between items-center inline-flex">
      <NuxtLink to="/">
        <div class="w-[127px] h-[36.89px] justify-between items-center inline-flex">
          <img src="~/assets/img/Cortex%20Logo.svg" alt="Cortex Logo" class="w-8 h-[36.89px]">
          <span class="w-[90px] h-[25px] font-bold text-primary dark:text-[#FAF9F7] leading-7 tracking-[4px]">CORTEX</span>
        </div>
      </NuxtLink>

      <!-- Desktop Navigation -->
      <NavigationMenu class="hidden min-[913px]:flex z-50">
        <NavigationMenuList class="gap-3">
          <NavigationMenuItem>
            <NavigationMenuTrigger class="bg-transparent font-medium text-base">Roadmaps</NavigationMenuTrigger>
            <NavigationMenuContent>
              <ul class="grid gap-3 p-6 md:w-[400px] lg:w-[500px] lg:grid-cols-[minmax(0,.75fr)_minmax(0,1fr)]">
                <li class="row-span-3">
                  <NavigationMenuLink as-child>
                    <a
                      class="flex h-full w-full select-none flex-col justify-end rounded-md bg-gradient-to-b from-muted/50 to-muted p-6 no-underline outline-none focus:shadow-md"
                      href="/roadmaps"
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
                      href="/explore"
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

      <div class="flex items-center gap-4">
        <template v-if="isAuthenticated">
          <UserMenu />
        </template>
        <template v-else>
          <Button as-child variant="default" class="hidden min-[913px]:flex bg-white text-center text-sm font-bold leading-7 rounded-[73px] px-8 py-3 h-[52px] text-primary hover:bg-gray-100">
            <NuxtLink to="/auth/login">Comenzar</NuxtLink>
          </Button>
        </template>

        <!-- Mobile Menu Button -->
        <button 
          @click="toggleMenu"
          class="min-[913px]:hidden p-2 text-primary dark:text-[#FAF9F7] relative z-50"
          aria-label="Toggle menu"
        >
          <Menu v-if="!isMenuOpen" class="w-6 h-6" />
          <X v-else class="w-6 h-6 text-[#FAF9F7]" />
        </button>
      </div>
    </header>

    <!-- Mobile Menu -->
    <Transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="opacity-0"
      enter-to-class="opacity-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="opacity-100"
      leave-to-class="opacity-0"
    >
      <div v-if="isMenuOpen" class="fixed inset-0 z-40 min-[913px]:hidden">
        <!-- Backdrop with opacity -->
        <div class="absolute inset-0 bg-primary/80 backdrop-blur-sm"></div>
        
        <!-- Menu content -->
        <div class="relative h-full w-full flex flex-col pt-24 pl-10">
          <div class="space-y-6 pr-6">
            <a 
              v-for="item in [
              { text: 'Explorar Roadmaps', url: '/explore' },
              { text: 'Mis Roadmaps', url: '/my-roadmaps' },
              { text: 'Acerca', url: '/about' },
              { text: 'Mentores', url: '/mentors' },
              { text: 'Planes', url: '/plans' },
              { text: 'Blog', url: '/blog' }
              ]" 
              :key="item.text"
              :href="item.url"
              class="block py-2 text-xl font-medium text-white hover:text-[#fae7ef] transition-colors duration-200"
              @click="isMenuOpen = false"
            >
              {{ item.text }}
            </a>
            
            <template v-if="!isAuthenticated">
              <Button as-child variant="default" class="mt-8 w-full bg-white text-center text-sm font-bold leading-7 rounded-[73px] px-8 py-3 h-[52px] text-primary hover:bg-gray-100">
                <NuxtLink to="/auth/login" @click="isMenuOpen = false">Comenzar</NuxtLink>
              </Button>
            </template>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>