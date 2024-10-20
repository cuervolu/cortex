<script setup lang="ts">
import GeneralSettings from "~/components/settings/GeneralSettings.vue";
import EditorSettings from "~/components/settings/EditorSettings.vue";
import AISettings from "~/components/settings/AISettings.vue";
</script>

<template>
  <div class="h-full overflow-auto text-foreground m-[30px]">
    <h1 class="text-3xl font-bold mb-6">Settings</h1>
    <Tabs default-value="general" class="w-full">
      <TabsList class="mb-4">
        <TabsTrigger value="general">General</TabsTrigger>
        <TabsTrigger value="editor">Editor</TabsTrigger>
        <TabsTrigger value="ai">AI</TabsTrigger>
      </TabsList>
      <TabsContent value="general">
        <GeneralSettings />
      </TabsContent>
      <TabsContent value="editor">
        <EditorSettings />
      </TabsContent>
      <TabsContent value="ai">
        <AISettings />
      </TabsContent>
    </Tabs>
  </div>
</template>

<style scoped>
:deep(.tabs-list) {
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
}

:deep(.tabs-trigger) {
  color: #ffffff;
  padding: 0.5rem 1rem;
}

:deep(.tabs-trigger[data-state="active"]) {
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 6px;
}

:deep(.tabs-content) {
  background-color: #1a1a2e;
  border-radius: 8px;
  padding: 1rem;
}
</style>