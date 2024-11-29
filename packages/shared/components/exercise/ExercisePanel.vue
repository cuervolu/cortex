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
    <div
      class="hidden lg:flex flex-col w-full h-full bg-muted/40"
    >
      <Tabs
        :default-value="defaultTab"
        :value="activeTabComputed"
        class="h-full flex flex-col"
        @update:value="activeTabComputed = $event"
      >
      <div class="border-b-4 border-primary">
        <ScrollArea class="py-1">
          <div class="w-max relative h-11 px-5">
            <TabsList class="rounded-none bg-background/80 flex absolute gap-2">
              <TabsTrigger
                v-for="tab in tabs"
                :key="tab.value"
                :value="tab.value"
                class="flex flex-col items-center justify-start gap-2 px-4 py-2 data-[state=active]:bg-[#EAD4FC] dark:data-[state=active]:bg-secondary rounded-full"
                :class="tab.className"
              >
                <template v-if="tab.iconSrc">
                  <div class="flex items-center gap-2">
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
                    <span class="text-sm" :class="tab.labelClassName">{{ tab.label }}</span>
                  </div>
                </template>
                <div v-if="tab.customIcon"/>
              </TabsTrigger>
            </TabsList>
          </div>
          <ScrollBar orientation="horizontal" thumbColor="bg-secondary" />
        </ScrollArea>
      </div>
        <ScrollArea class="flex-grow overflow-auto h-screen py-2">
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
        </ScrollArea>
      </Tabs>
    </div>

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