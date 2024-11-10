<script setup lang="ts">
interface Props {
  width?: number;
  height?: number;
}

const { width, height } = defineProps<Props>();

</script>

<template>
  <svg :width :height viewBox="0 0 16 14" fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M12.2175 0.49585H14.4665L9.55317 6.11151L15.3334 13.7532H10.8075L7.26271 9.11854L3.20665 13.7532H0.956308L6.21164 7.74658L0.666687 0.49585H5.30743L8.51162 4.73207L12.2175 0.49585ZM11.4282 12.407H12.6744L4.63028 1.77127H3.29299L11.4282 12.407Z"/>
  </svg>
</template>
