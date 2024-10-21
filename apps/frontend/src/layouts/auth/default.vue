<script setup>
import backgroundImage from '~/assets/img/auth-background.svg'
</script>

<template>
  <div class="w-screen h-screen inset-0 bg-cover bg-center bg-no-repeat flex-col justify-center items-center gap-[60px] inline-flex" :style="{ backgroundImage: `url(${backgroundImage})` }">

    <!-- Content overlay -->
    <div class="self-stretch px-[50px] my-8 justify-center items-center gap-[57px] inline-flex flex-wrap">
      <!-- Left side with text -->
      <div class="grow shrink basis-0 md:min-w-[400px] md:max-w-[700px] lg:min-w-[566px] lg:max-w-[793px] justify-center items-center gap-3 hidden sm:flex">
        <div class="grow shrink basis-0 text-center">
          <span class="text-[#381653] text-responsive lg:text-6xl font-black font-['Axiforma']" style="line-height: normal;">Start learning</span>
          <span class="text-[#f493bc] text-responsive lg:text-6xl font-['Axiforma']" style="line-height: normal;"> today</span>
          <span class="text-[#381653] text-responsive lg:text-6xl font-black font-['Axiforma']" style="line-height: normal;">. Knowledge has </span>
          <span class="text-[#92d2dd] text-responsive lg:text-6xl font-bold italic font-['Axiforma']" style="line-height: normal;">no limits</span>
          <span class="text-[#381653] text-responsive lg:text-6xl font-black font-['Axiforma']" style="line-height: normal;">, and neither does your </span>
          <span class="text-[#f9cf87] text-responsive lg:text-6xl font-black font-['Axiforma']" style="line-height: normal;">potential</span>
        </div>
      </div>

      <!-- Right side with form -->
      <slot />
    </div>
  </div>
</template>

<style>
@media (max-width: 1192px) {
  .text-responsive {
    font-size: 3rem; /* Cambia el tamaño aquí, por ejemplo 4xl */
    line-height: 1;
  }
}

@media (max-width: 1023px) {
  .text-responsive {
    font-size: 2.5rem; /* Cambia el tamaño aquí, por ejemplo 4xl */
    line-height: 2.25rem;
  }
}
</style>
