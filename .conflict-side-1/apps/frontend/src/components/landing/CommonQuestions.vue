<script setup lang="ts">

</script>

<template>
  <section class="py-20 px-4 text-foreground">
    <h2 class="text-3xl font-bold mb-8 text-center">Common questions</h2>
    <div class="max-w-3xl mx-auto">
      <Accordion type="single" collapsible>
        <AccordionItem value="item-1">
          <AccordionTrigger>Can I access the courses from any device?</AccordionTrigger>
          <AccordionContent>
            Yes, you can access our courses from any device with an internet connection.
          </AccordionContent>
        </AccordionItem>
        <AccordionItem value="item-2">
          <AccordionTrigger>Do I need prior programming experience?</AccordionTrigger>
          <AccordionContent>
            No prior experience is required. Our courses cater to beginners and advanced learners alike.
          </AccordionContent>
        </AccordionItem>
        <AccordionItem value="item-3">
          <AccordionTrigger>How is progress managed in the courses?</AccordionTrigger>
          <AccordionContent>
            Your progress is automatically tracked as you complete lessons and exercises.
          </AccordionContent>
        </AccordionItem>
        <AccordionItem value="item-4">
          <AccordionTrigger>How do you ensure the content is up to date?</AccordionTrigger>
          <AccordionContent>
            Our team of experts regularly reviews and updates the course content to ensure it remains current with industry standards.
          </AccordionContent>
        </AccordionItem>
        <AccordionItem value="item-5">
          <AccordionTrigger>What support or mentorship options are available?</AccordionTrigger>
          <AccordionContent>
            We offer community forums, live Q&A sessions, and optional one-on-one mentorship depending on your subscription plan.
          </AccordionContent>
        </AccordionItem>
        <AccordionItem value="item-6">
          <AccordionTrigger>What makes Cortex different from other educational platforms?</AccordionTrigger>
          <AccordionContent>
            Cortex focuses on providing an interactive and personalized experience, adapting to your learning pace. We also offer content in Spanish with an intuitive interface for both young users and adults, all without the need to pay until you decide to access premium features.
          </AccordionContent>
        </AccordionItem>
      </Accordion>
    </div>
  </section>
</template>

<style scoped>

</style>