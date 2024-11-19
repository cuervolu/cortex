<script lang="ts" setup>
import { ref, computed } from 'vue';

const isMonthly = ref(true);

const props = defineProps<{
  path: string;
}>();

const plans = [
  {
    title: 'Plan Básico',
    price: '0 CLP',
    period: 'Gratis',
    features: [
      { text: 'Acceso a cursos básicos' },
      { text: 'Proyectos limitados' },
      { text: 'Foro de discusión' },
      { text: 'Sin mentorías personalizadas' },
      { text: 'Recursos de aprendizaje básicos' },
      { text: 'Aprendizaje autodirigido' },
    ],
    buttonText: 'Plan Actual',
    enable: false,
  },
  {
    title: 'Plan Estándar',
    price: '25.000 CLP',
    period: 'por editor/mes',
    subPeriod: 'factura mensual',
    features: [
      { text: 'Acceso a todos los cursos' },
      { text: 'Mentorías limitadas' },
      { text: 'Acceso a proyectos colaborativos' },
      { text: 'Créditos extra opcionales' },
      { text: 'Acceso a foros avanzados' },
      { text: 'Aprendizaje guiado y personalizado' },
    ],
    buttonText: 'Elegir Plan',
    highlighted: true,
    tag: 'Más Popular',
    enable: true,
  },
  {
    title: 'Plan anual',
    price: '250.000 CLP',
    period: 'por editor/anual',
    subPeriod: 'factura anual',
    features: [
      { text: 'Acceso a todos los cursos' },
      { text: 'Mentorías limitadas' },
      { text: 'Acceso a proyectos colaborativos' },
      { text: 'Créditos extra opcionales' },
      { text: 'Acceso a foros avanzados' },
      { text: 'Aprendizaje guiado y personalizado' },
    ],
    buttonText: 'Elegir Plan',
    highlighted: true,
    enable: true,
  },
  {
    title: 'Plan empresarial',
    price: '50.000 CLP',
    period: 'por editor/mes',
    subPeriod: 'factura mensual',
    features: [
      { text: 'Acceso completo a todos los cursos y recursos exclusivos' },
      { text: 'Mentorías ilimitadas' },
      { text: 'Prioridad en proyectos colaborativos' },
      { text: 'Créditos adicionales' },
      { text: 'Acceso a contenido avanzado y herramientas premium' },
      { text: 'Soporte prioritario' },
    ],
    buttonText: 'Contactanos',
    highlighted: true,
    enable: true,
  },
];

// Filtra los planes según la opción seleccionada, siempre incluye el Plan Básico
const filteredPlans = computed(() => {
  const additionalPlans = isMonthly.value
    ? plans.filter(plan => plan.period.includes('mes'))
    : plans.filter(plan => plan.period.includes('anual'));
  return [plans[0], ...additionalPlans]; // Siempre incluye el Plan Básico
});
</script>

<template>
  <div class="flex justify-center w-full">
    <div class="flex flex-col items-center justify-center gap-8 px-16 py-14 relative max-w-[1200px] w-full">
      <div class="flex flex-col items-center gap-4 relative self-stretch w-full">
        <div class="relative w-fit font-bold text-[40px] text-center">
          Compra una suscripción
        </div>
        <p class="relative w-fit text-xl text-center">
          Escoje el plan que mejor te convenga
        </p>
      </div>
      <div class="flex max-w-[355px] items-center gap-[9px] p-[4.5px] bg-[#ebeff0] rounded-[112.45px] justify-center">
        <button 
          :class="['w-[168.67px] p-[11.24px] rounded-[112.45px]', isMonthly ? 'bg-[#1d2127] text-white' : 'text-[#1d2127]']" 
          @click="isMonthly = true">
          Mensual
        </button>
        <button 
          :class="['w-[168.67px] p-[11.24px] rounded-[112.45px]', !isMonthly ? 'bg-[#1d2127] text-white' : 'text-[#1d2127]']" 
          @click="isMonthly = false">
          Anual
        </button>
      </div>
      <div class="flex flex-col md:flex-row justify-center gap-[18px] p-[13.5px] w-full max-w-[1075px] rounded-[36px] shadow-[0px_16px_14px_#00000021]">
        <Carousel v-slot="{ canScrollNext }" class="w-full">
          <CarouselContent>
            <CarouselItem v-for="(plan, index) in filteredPlans" :key="index" class="max-w-fit">
              <PricingCard v-bind="plan" :path="path" />
            </CarouselItem>
          </CarouselContent>
          <CarouselPrevious />
          <CarouselNext v-if="canScrollNext" />
        </Carousel>
      </div>
    </div>
  </div>
</template>
