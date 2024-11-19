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
  <ScriptLemonSqueezy @lemon-squeezy-event="handleLemonEvent" @ready="ready = true">
    <Button
        :class="[
        'relative self-stretch w-full h-[44.98px] rounded-[9px] overflow-hidden',
        'bg-[#f296bd] text-white',
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