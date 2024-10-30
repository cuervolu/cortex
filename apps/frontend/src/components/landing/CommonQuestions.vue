<script setup lang="ts">

</script>

<template>
  <section class="py-20 px-4 text-foreground">
    <h2 class="text-3xl font-bold mb-8 text-center">Preguntas Comunes</h2>
    <div class="max-w-3xl mx-auto">
      <Accordion type="single" collapsible>
      <AccordionItem value="item-1">
        <AccordionTrigger>¿Puedo acceder a los cursos desde cualquier dispositivo?</AccordionTrigger>
        <AccordionContent>
          Sí, puedes acceder a nuestros cursos desde cualquier dispositivo con conexión a internet.
        </AccordionContent>
      </AccordionItem>
      <AccordionItem value="item-2">
        <AccordionTrigger>¿Necesito experiencia previa en programación?</AccordionTrigger>
        <AccordionContent>
          No se requiere experiencia previa. Nuestros cursos están diseñados tanto para principiantes como para estudiantes avanzados.
        </AccordionContent>
      </AccordionItem>
      <AccordionItem value="item-3">
        <AccordionTrigger>¿Cómo se gestiona el progreso en los cursos?</AccordionTrigger>
        <AccordionContent>
          Tu progreso se rastrea automáticamente a medida que completas lecciones y ejercicios.
        </AccordionContent>
      </AccordionItem>
      <AccordionItem value="item-4">
        <AccordionTrigger>¿Cómo aseguran que el contenido esté actualizado?</AccordionTrigger>
        <AccordionContent>
          Nuestro equipo de expertos revisa y actualiza regularmente el contenido del curso para asegurar que se mantenga al día con los estándares de la industria.
        </AccordionContent>
      </AccordionItem>
      <AccordionItem value="item-5">
        <AccordionTrigger>¿Qué opciones de soporte o mentoría están disponibles?</AccordionTrigger>
        <AccordionContent>
          Ofrecemos foros comunitarios, sesiones de preguntas y respuestas en vivo, y mentoría individual opcional según tu plan de suscripción.
        </AccordionContent>
      </AccordionItem>
      <AccordionItem value="item-6">
        <AccordionTrigger>¿Qué hace a Cortex diferente de otras plataformas educativas?</AccordionTrigger>
        <AccordionContent>
          Cortex se enfoca en proporcionar una experiencia interactiva y personalizada, adaptándose a tu ritmo de aprendizaje. También ofrecemos contenido en español con una interfaz intuitiva tanto para usuarios jóvenes como adultos, todo sin necesidad de pagar hasta que decidas acceder a las funciones premium.
        </AccordionContent>
        </AccordionItem>
      </Accordion>
    </div>
  </section>
</template>

<style scoped>

</style>