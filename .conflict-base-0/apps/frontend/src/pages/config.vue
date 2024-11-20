<script setup lang="ts">
import Setting from '~/components/usersetting/setting.vue'

definePageMeta({
  middleware: 'auth'
})

useHead({
  title: 'Configuración',
  meta: [
    {
      name: 'description',
      content: 'Configura la manera en la que interactúas con la aplicación'
    }
  ]
})
</script>

<template>
  <section class="min-h-screen">
    <div class="max-w-4xl mx-auto p-4 pt-8">
      <div class=" bg-muted/40 rounded-lg shadow-lg p-6">
        <Setting/>
      </div>
    </div>
  </section>
</template>