<script setup lang="ts">
import { ref } from 'vue'
import type { Component } from 'vue'

interface Tab {
  value: string;
  label: string;
  iconSrc?: string;
  customIcon?: string;
  className?: string;
  labelClassName?: string;
  contentClassName?: string;
  component: Component;
  props?: Record<string, any>;
}

interface Props {
  tabs: Tab[]
  defaultTab: string
  isOpen: boolean
}

const props = withDefaults(defineProps<Props>(), {
  isOpen: false
})

const emit = defineEmits(['send-message', 'update:isOpen'])

const localIsOpen = ref(props.isOpen)

watch(() => props.isOpen, (newValue) => {
  localIsOpen.value = newValue
})

const updateIsOpen = (value: boolean) => {
  localIsOpen.value = value
  emit('update:isOpen', value)
}
</script>
<template>
  <div>
    <!-- Panel for desktop -->
    <ScrollArea class="hidden sm:flex flex-col w-full h-full bg-white shadow-lg">
      <Tabs :default-value="defaultTab" class="w-full h-full flex flex-col">
        <TabsList class="flex items-start px-6 py-2 bg-neutral-50 border-b-2 border-purple-600 shrink-0">
          <TabsTrigger
            v-for="tab in tabs"
            :key="tab.value"
            :value="tab.value"
            class="flex items-center gap-2 px-2 py-1"
            :class="tab.className"
          >
            <img v-if="tab.iconSrc" :src="tab.iconSrc" :alt="tab.label">
            <div v-if="tab.customIcon" v-html="tab.customIcon" />
            <span class="text-sm" :class="tab.labelClassName">{{ tab.label }}</span>
          </TabsTrigger>
        </TabsList>
        <div class="flex-grow overflow-auto">
          <TabsContent
            v-for="tab in tabs"
            :key="tab.value"
            :value="tab.value"
            class="h-full"
            :class="tab.contentClassName"
          >
            <component
              :is="tab.component"
              v-bind="tab.props"
              @send-message="$emit('send-message', $event)"
            />
          </TabsContent>
        </div>
      </Tabs>
    </ScrollArea>

    <!-- Sheet for mobile -->
    <Sheet v-model:open="localIsOpen" @update:open="updateIsOpen">
      <SheetContent class="sm:hidden w-full max-w-none">
        <SheetHeader>
          <SheetTitle>Exercise Panel</SheetTitle>
        </SheetHeader>
        <Tabs :default-value="defaultTab" class="w-full mt-4">
          <TabsList class="flex items-start px-4 py-2 bg-neutral-50 border-b-2 border-purple-600">
            <TabsTrigger
              v-for="tab in tabs"
              :key="tab.value"
              :value="tab.value"
              class="flex items-center gap-2 px-2 py-1"
              :class="tab.className"
            >
              <img v-if="tab.iconSrc" :src="tab.iconSrc" :alt="tab.label">
              <div v-if="tab.customIcon" v-html="tab.customIcon" />
              <span class="text-sm" :class="tab.labelClassName">{{ tab.label }}</span>
            </TabsTrigger>
          </TabsList>
          <TabsContent
            v-for="tab in tabs"
            :key="tab.value"
            :value="tab.value"
            :class="tab.contentClassName"
          >
            <component
              :is="tab.component"
              v-bind="tab.props"
              @send-message="$emit('send-message', $event)"
            />
          </TabsContent>
        </Tabs>
      </SheetContent>
    </Sheet>
  </div>
</template>
