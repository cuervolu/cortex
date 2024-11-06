<script setup lang="ts">
import { type HTMLAttributes, computed } from 'vue'
import {
  AccordionHeader,
  AccordionTrigger,
  type AccordionTriggerProps,
} from 'radix-vue'
import { ChevronDown } from 'lucide-vue-next'
import { cn } from '../../../lib/utils'

const props = defineProps<AccordionTriggerProps & { class?: HTMLAttributes['class'], hiddeIcon?: boolean }>()

const hiddeIcon = computed(() => props.hiddeIcon !== false)

const delegatedProps = computed(() => {
  const { class: _, hiddeIcon: __, ...delegated } = props

  return delegated
})
</script>

<template>
  <AccordionHeader class="flex">
    <AccordionTrigger
      v-bind="delegatedProps"
      :class="
        cn(
          'flex flex-1 items-center justify-between py-4 font-medium transition-all hover:underline [&[data-state=open]>svg]:rotate-180',
          props.class,
        )
      "
    >
      <slot />
      <slot name="icon">
        <ChevronDown
          v-if="!hiddeIcon"
          class="h-4 w-4 shrink-0 transition-transform duration-200"
        />
      </slot>
    </AccordionTrigger>
  </AccordionHeader>
</template>
