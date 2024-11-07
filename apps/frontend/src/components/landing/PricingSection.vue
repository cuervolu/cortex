<script lang="ts" setup>

const isMonthly = ref(true);

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
  },
];
</script>

<template>
  <div class="flex justify-center w-full">
    <div class="flex flex-col items-center justify-center gap-[97px] px-[65px] py-[53px] relative max-w-[1200px] w-full">
      <div class="flex flex-col items-center gap-4 relative self-stretch w-full flex-[0_0_auto]">
        <div class="relative w-fit mt-[-1.00px] font-bold  text-[40px] text-center tracking-[0.80px] leading-normal">
          Compra una suscripción
        </div>
        <p class="relative w-fit font-normal  text-xl text-center tracking-[0.40px] leading-normal">
          Escoje el plan que mejor te convenga
        </p>
      </div>
      <div class="flex max-w-[355px] items-center gap-[9px] p-[4.5px] self-stretch w-full bg-[#ebeff0] rounded-[112.45px] justify-center relative flex-[0_0_auto]">
        <button :class="['flex w-[168.67px] items-center justify-center gap-[4.5px] p-[11.24px] relative ml-[-0.17px] rounded-[112.45px]', isMonthly ? 'bg-[#1d2127] text-white' : 'text-[#1d2127]']" @click="isMonthly = true">
          <div class="relative flex-1 mt-[-1.12px] font-bold text-lg text-center tracking-[0.36px] leading-normal">
            Mensual
          </div>
        </button>
        <button :class="['flex w-[168.67px] items-baseline justify-center gap-[4.5px] p-[11.24px] relative mr-[-0.17px] rounded-[112.45px]', !isMonthly ? 'bg-[#1d2127] text-white' : 'text-[#1d2127]']" @click="isMonthly = false">
          <div class="relative w-fit mt-[-1.12px] font-normal text-lg text-center tracking-[0.36px] leading-normal">
            Anual
          </div>
          <div class="relative w-fit font-normal text-[13.5px] text-center tracking-[0.27px] leading-normal">
            -20% off
          </div>
        </button>
      </div>
      <div class="flex flex-col md:flex-row justify-center items-center md:items-end gap-[17.99px] p-[13.49px] relative w-full max-w-[1075px] rounded-[35.98px] shadow-[0px_16.03px_14.52px_#00000021]">
        <PricingCard v-for="(plan, index) in plans" :key="index" v-bind="plan" />
      </div>
    </div>
  </div>
</template>