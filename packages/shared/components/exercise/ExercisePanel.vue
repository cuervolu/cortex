<script setup lang="ts">
import type {Component} from "vue";

export interface Tab {
  value: string;
  label: string;
  iconSrc?: string | Component;
  customIcon?: string;
  className?: string;
  labelClassName?: string;
  contentClassName?: string;
  component: Component;
  props?: Record<string, any>;
}

interface Props {
  tabs: Tab[];
  defaultTab: string;
  modelValue?: string;
  activeTab: string;
}

const props = defineProps<Props>();
const emit = defineEmits(['update:modelValue', 'send-message', 'update:activeTab']);

const activeTabComputed = computed({
  get: () => props.activeTab,
  set: (value) => emit('update:activeTab', value)
});
</script>

<template>
  <div>
    <!-- Panel for desktop (visible on screens >= 800px) -->
    <ScrollArea
      class="hidden lg:flex flex-col w-full h-full bg-muted/40 shadow-lg rounded-lg"
    >
      <Tabs
        :default-value="defaultTab"
        :value="activeTabComputed"
        class="h-full flex flex-col"
        @update:value="activeTabComputed = $event"
      >
        <TabsList
          class="flex justify-center px-6 py-2 bg-muted/40 border-b-2 border-purple-600 shrink-0"
        >
          <TabsTrigger
            v-for="tab in tabs"
            :key="tab.value"
            :value="tab.value"
            class="flex flex-col items-center justify-center gap-2 px-4 py-2"
            :class="tab.className"
          >
            <template v-if="tab.iconSrc">
              <div class="flex justify-center w-full">
                <img
                  v-if="typeof tab.iconSrc === 'string'"
                  :src="tab.iconSrc"
                  :alt="tab.label"
                  class="w-5 h-5"
                >
                <component
                  :is="tab.iconSrc"
                  v-else
                  :alt="tab.label"
                  class="w-5 h-5"
                />
              </div>
            </template>
            <div v-if="tab.customIcon"/>
            <span class="text-sm text-center" :class="tab.labelClassName">{{ tab.label }}</span>
          </TabsTrigger>
        </TabsList>
        <div class="flex-grow overflow-auto h-full">
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

    <!-- Sheet for mobile (visible on screens < 800px) -->
    <Sheet>
      <SheetTrigger class="lg:hidden block">Open Panel</SheetTrigger>
      <SheetContent side="right" class="w-[90%] sm:w-[540px]">
        <SheetHeader>
          <SheetTitle>Exercise Panel</SheetTitle>
        </SheetHeader>
        <Tabs
          :default-value="defaultTab"
          :value="activeTabComputed"
          class="h-full flex flex-col"
          @update:value="activeTabComputed = $event"
        >
          <TabsList
            class="flex justify-center px-4 py-2 bg-neutral-50 border-b-2 border-purple-600">
            <TabsTrigger
              v-for="tab in tabs"
              :key="tab.value"
              :value="tab.value"
              class="flex flex-col items-center justify-center gap-2 px-4 py-2"
              :class="tab.className"
            >
              <template v-if="tab.iconSrc">
                <div class="flex justify-center w-full">
                  <img
                    v-if="typeof tab.iconSrc === 'string'"
                    :src="tab.iconSrc"
                    :alt="tab.label"
                    class="w-5 h-5"
                  >
                  <component
                    :is="tab.iconSrc"
                    v-else
                    :alt="tab.label"
                    class="w-5 h-5"
                  />
                </div>
              </template>
              <div v-if="tab.customIcon"/>
              <span class="text-sm text-center" :class="tab.labelClassName">{{ tab.label }}</span>
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
