<script lang="ts" setup>
import {ArrowLeft} from "lucide-vue-next";
import PricingSection from "~/components/landing/PricingSection.vue";

const router = useRouter();

const fullPath = ref(router.currentRoute.value.fullPath);

definePageMeta({
  auth: false
});

useHead({
  title: 'Planes',
  meta: [
    {
      name: 'description',
      content: 'Elige el plan que mejor se adapte a tus necesidades y comienza a aprender con Cortex.'
    }
  ]
})

useScript('https://cdn.jsdelivr.net/npm/js-confetti@0.12.0/dist/js-confetti.browser.js')


</script>

<template>
  <section>
    <div class="w-full flex items-center gap-4 px-8 pt-8 relative">
      <!-- Botón de regreso -->
      <Button class="w-12 h-12 hover:bg-primary/80 back-button flex-shrink-0" variant="ghost"
              as-child>
        <NuxtLink to="/">
          <ArrowLeft class="flex-shrink-0 text-primary dark:text-white back-icon"/>
        </NuxtLink>
      </Button>
      <!-- Logo Cortex -->
      <div class="absolute left-1/2 transform -translate-x-1/2 flex items-center justify-center">
        <img src="~/assets/img/Cortex%20Logo.svg" alt="Cortex Logo" class="h-14">
        <span
            class="ml-2 text-2xl font-bold text-primary dark:text-[#FAF9F7] leading-7 tracking-[4px]">CORTEX</span>
      </div>
    </div>

    <PricingSection
        :path="fullPath"
    />
  </section>
</template>

<style scoped>

.back-button:hover {
  .back-icon {
    color: white;
  }
}

</style>