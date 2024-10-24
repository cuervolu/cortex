<script setup></script>

<template>
  <div class="w-screen h-screen inset-0 bg-cover bg-center bg-no-repeat flex-col justify-center items-center gap-[60px] inline-flex bg-auth-background dark:bg-auth-background-dark">

    <!-- Content overlay -->
    <div class="self-stretch px-[50px] my-8 justify-center items-center gap-[57px] inline-flex flex-wrap">
      <!-- Left side with text -->
      <div class="grow shrink basis-0 md:min-w-[400px] md:max-w-[700px] lg:min-w-[566px] lg:max-w-[814px] justify-center items-center gap-3 hidden sm:flex">
        <div class="grow shrink basis-0 text-center">
          <span class="text-[#381653] dark:text-[#E1E0E0] text-responsive lg:text-6xl font-extrabold lg:leading-relaxed">Start learning</span>
          <span class="text-[#f493bc] text-responsive lg:text-6xl lg:leading-relaxed"> today</span>
          <span class="text-[#381653] dark:text-[#E1E0E0] text-responsive lg:text-6xl font-extrabold lg:leading-relaxed">. Knowledge has </span>
          <span class="text-[#92d2dd] text-responsive lg:text-6xl font-bold italic lg:leading-relaxed">no limits</span>
          <span class="text-[#381653] dark:text-[#E1E0E0] text-responsive lg:text-6xl font-extrabold lg:leading-relaxed">, and neither does your </span>
          <span class="text-[#f9cf87] text-responsive lg:text-6xl font-extrabold lg:leading-relaxed">potential</span>
        </div>
      </div>

      <!-- Right side with form -->
      <slot />
    </div>
  </div>
</template>

<style scoped>
@media (max-width: 1192px) {
  .text-responsive {
    font-size: 3rem; /* Cambia el tamaño aquí, por ejemplo 4xl */
    line-height: 1.5;
  }
}

@media (max-width: 1023px) {
  .text-responsive {
    font-size: 2.5rem; /* Cambia el tamaño aquí, por ejemplo 4xl */
    line-height: 3.25rem;
  }
}
</style>
