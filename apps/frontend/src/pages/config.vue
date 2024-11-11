<script setup lang="ts">
import Setting from '~/components/usersetting/setting.vue'

definePageMeta({
  middleware: 'auth'
})
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-4xl mx-auto p-4 pt-8">
      <div class="bg-white rounded-lg shadow-lg p-6">
        <Setting />
      </div>
    </div>
  </div>
</template>