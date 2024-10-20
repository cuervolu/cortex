<script setup lang="ts">
import CompanyInfo from "~/components/CompanyInfo.vue";
import FooterSection from "~/components/FooterSection.vue";
import SocialIcons from "~/components/icons/SocialIcons.vue";
</script>

<template>
  <footer class="flex flex-col justify-center py-4 bg-[#B5E2E8] rounded-[40px]">
    <div
        class="flex overflow-hidden flex-col justify-center px-16 py-14 w-full max-md:px-5 max-md:max-w-full">
      <div class="flex overflow-hidden flex-wrap gap-28 items-start w-full max-md:max-w-full">
        <CompanyInfo/>
        <nav
            class="flex flex-wrap flex-1 shrink gap-40 justify-between items-start basis-0 min-w-[240px] max-md:max-w-full">
          <FooterSection title="Company" :items="['Careers', 'Teachers', 'Support', 'Contact']"/>
          <FooterSection title="Product" :items="['Courses', 'Pricing', 'Blog']"/>
          <FooterSection title="Legal" :items="['Terms & Conditions', 'Privacy policy']"/>
          <FooterSection title="Legal" :items="['Terms & Conditions', 'Privacy policy']"/>
          <SocialIcons/>
        </nav>
      </div>
    </div>
  </footer>
</template>

<style scoped>

</style>