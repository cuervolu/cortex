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
}

defineProps<Props>();

defineEmits(["send-message"]);
</script>

<template>
  <div>
    <!-- Panel for desktop (visible on screens >= 800px) -->
    <ScrollArea
      class="hidden lg:flex flex-col w-full h-full bg-muted/40 shadow-lg rounded-lg"
    >
      <Tabs :default-value="defaultTab" class="h-full flex flex-col">
        <TabsList
          class="flex items-start px-6 py-2 bg-muted/40 border-b-2 border-purple-600 shrink-0"
        >
          <TabsTrigger
            v-for="tab in tabs"
            :key="tab.value"
            :value="tab.value"
            class="flex items-center gap-2 px-2 py-1"
            :class="tab.className"
          >
            <template v-if="tab.iconSrc">
              <img
                v-if="typeof tab.iconSrc === 'string'"
                :src="tab.iconSrc"
                :alt="tab.label"
              >
              <component :is="tab.iconSrc" v-else :alt="tab.label"/>
            </template>
            <div v-if="tab.customIcon"/>
            <span class="text-sm" :class="tab.labelClassName">{{ tab.label }}</span>
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
        <Tabs :default-value="defaultTab" class="w-full mt-4">
          <TabsList class="flex items-start px-4 py-2 bg-neutral-50 border-b-2 border-purple-600">
            <TabsTrigger
              v-for="tab in tabs"
              :key="tab.value"
              :value="tab.value"
              class="flex items-center gap-2 px-2 py-1"
              :class="tab.className"
            >
              <template v-if="tab.iconSrc">
                <img
                  v-if="typeof tab.iconSrc === 'string'"
                  :src="tab.iconSrc"
                  :alt="tab.label"
                >
                <component :is="tab.iconSrc" v-else :alt="tab.label"/>
              </template>
              <div v-if="tab.customIcon"/>
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
