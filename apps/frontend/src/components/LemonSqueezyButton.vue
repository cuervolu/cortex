<script setup lang="ts">
const props = defineProps<{
  checkoutUrl: string
  buttonText: string
  className?: string
  disabled?: boolean
}>()

const ready = ref(false)
const events = ref([])

const handleLemonEvent = (event: any) => {
  events.value.push(event)
  if (event.event === 'Checkout.Success') {
    console.log('Checkout successful!', event.data)
  }
}
</script>

<template>
  <ScriptLemonSqueezy @lemon-squeezy-event="handleLemonEvent" @ready="ready = true" class="rounded-[9px] w-full">
    <Button
        :class="[
        'relative self-stretch w-full h-[44.98px] text-lg rounded-[9px] overflow-hidden',
        'bg-[#f296bd] dark:bg-[#92D2DD] text-white dark:text-primary',
        { 'opacity-50 cursor-not-allowed': disabled },
        className
      ]"
        :disabled="disabled" as-child
    >
      <NuxtLink
          :to="checkoutUrl + '?embed=1'"
      >
        {{ buttonText }}
      </NuxtLink>
    </Button>
  </ScriptLemonSqueezy>
</template>