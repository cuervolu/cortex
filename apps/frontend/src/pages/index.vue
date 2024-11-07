<script setup lang="ts">
import LandingHero from "~/components/landing/LandingHero.vue";
import LearningInterface from "~/components/landing/LearningInterface.vue";
import CommonQuestions from "~/components/landing/CommonQuestions.vue";
import PricingSection from "~/components/landing/PricingSection.vue";

useHead({
  title: 'Inicio',
  meta: [
    {
      name: 'description',
      content: 'Aprenda a su propio ritmo con acceso ilimitado, disponible en cualquier momento tanto en el sitio web como en el escritorio. Revise el material siempre que lo necesite, sin restricciones de tiempo.'
    }
  ]
})


definePageMeta({
  auth: false
})
</script>

<template>
  <section>
    <landing-hero />
    <learning-interface />
    <common-questions />
    <pricing-section />
  </section>
</template>