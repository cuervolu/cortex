<script setup lang="ts">
import { ref } from 'vue'

const activeItem = ref('')

const faqs = [
  {
    id: 'item-1',
    question: '¿Puedo acceder a los cursos desde cualquier dispositivo?',
    answer: 'Sí, puedes acceder a nuestros cursos desde cualquier dispositivo con conexión a internet.'
  },
  {
    id: 'item-2',
    question: '¿Necesito experiencia previa en programación?',
    answer: 'No se requiere experiencia previa. Nuestros cursos están diseñados tanto para principiantes como para estudiantes avanzados.'
  },
  {
    id: 'item-3',
    question: '¿Cómo se gestiona el progreso en los cursos?',
    answer: 'Tu progreso se rastrea automáticamente a medida que completas lecciones y ejercicios.'
  },
  {
    id: 'item-4',
    question: '¿Cómo aseguran que el contenido esté actualizado?',
    answer: 'Nuestro equipo de expertos revisa y actualiza regularmente el contenido del curso para asegurar que se mantenga al día con los estándares de la industria.'
  },
  {
    id: 'item-5',
    question: '¿Qué opciones de soporte o mentoría están disponibles?',
    answer: 'Ofrecemos foros comunitarios, sesiones de preguntas y respuestas en vivo, y mentoría individual opcional según tu plan de suscripción.'
  },
  {
    id: 'item-6',
    question: '¿Qué hace a Cortex diferente de otras plataformas educativas?',
    answer: 'Cortex se enfoca en proporcionar una experiencia interactiva y personalizada, adaptándose a tu ritmo de aprendizaje. También ofrecemos contenido en español con una interfaz intuitiva tanto para usuarios jóvenes como adultos, todo sin necesidad de pagar hasta que decidas acceder a las funciones premium.'
  }
]
</script>

<template>
  <section class="py-20 px-4 text-foreground bg-background">
    <div class="max-w-7xl mx-auto">
      <h2 class="text-3xl font-bold mb-12 text-center">Preguntas Comunes</h2>
      
      <div class="grid md:grid-cols-2 gap-8">
        <!-- Columna izquierda -->
        <div class="space-y-6">
          <Accordion type="single" collapsible v-model="activeItem" class="space-y-6">
            <div 
              v-for="faq in faqs.slice(0, 3)" 
              :key="faq.id"
              class="rounded-lg border border-border overflow-hidden shadow-sm hover:shadow-md transition-shadow duration-200"
              :class="{'bg-primary text-primary-foreground': activeItem === faq.id}"
            >
              <AccordionItem :value="faq.id">
                <AccordionTrigger class="px-6 py-4">
                  {{ faq.question }}
                </AccordionTrigger>
                <AccordionContent class="px-6 pb-4">
                  {{ faq.answer }}
                </AccordionContent>
              </AccordionItem>
            </div>
          </Accordion>
        </div>

        <!-- Columna derecha -->
        <div class="space-y-6">
          <Accordion type="single" collapsible v-model="activeItem" class="space-y-6">
            <div 
              v-for="faq in faqs.slice(3, 6)" 
              :key="faq.id"
              class="rounded-lg border border-border overflow-hidden shadow-sm hover:shadow-md transition-shadow duration-200"
              :class="{'bg-primary text-primary-foreground': activeItem === faq.id}"
            >
              <AccordionItem :value="faq.id">
                <AccordionTrigger class="px-6 py-4">
                  {{ faq.question }}
                </AccordionTrigger>
                <AccordionContent class="px-6 pb-4">
                  {{ faq.answer }}
                </AccordionContent>
              </AccordionItem>
            </div>
          </Accordion>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
:deep(.accordion-trigger) {
  transition: all 0.2s ease-out;
}

:deep(.accordion-trigger:hover) {
  background-color: rgb(0 0 0 / 0.05);
}

:deep([data-state=open]) {
  background-color: hsl(var(--primary));
  color: hsl(var(--primary-foreground));
}
</style>