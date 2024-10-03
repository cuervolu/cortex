<script setup lang="ts">
import LandingHero from "~/components/landing/LandingHero.vue";
import LearningInterface from "~/components/landing/LearningInterface.vue";
import CommonQuestions from "~/components/landing/CommonQuestions.vue";
import PricingSection from "~/components/landing/PricingSection.vue";

useHead({
  title: 'Inicio | Cortex',
  meta: [
    {
      name: 'description',
      content: 'Página de inicio de Cortex'
    }
  ]
})


definePageMeta({
  middleware: 'auth'
})
</script>

<template>
  <div class="min-h-screen ">
    <app-header />
    <main>
      <landing-hero />
      <learning-interface />
      <common-questions />
      <pricing-section />
    </main>
    <app-footer />
  </div>
</template>