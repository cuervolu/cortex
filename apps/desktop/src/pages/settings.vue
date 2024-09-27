<script setup lang="ts">

import GeneralSettings from "~/components/settings/GeneralSettings.vue";
import EditorSettings from "~/components/settings/EditorSettings.vue";
import AISettings from "~/components/settings/AISettings.vue";
</script>

<template>
  <div class="container mx-auto p-6 space-y-8">
    <h1 class="text-3xl font-bold mb-6">Settings</h1>

    <Tabs default-value="general" class="w-full">
      <TabsList>
        <TabsTrigger value="general">General</TabsTrigger>
        <TabsTrigger value="editor">Editor</TabsTrigger>
        <TabsTrigger value="ai">AI</TabsTrigger>
      </TabsList>

      <TabsContent value="general">
        <GeneralSettings/>
      </TabsContent>

      <TabsContent value="editor">
        <EditorSettings/>
      </TabsContent>

      <TabsContent value="ai">
        <AISettings/>
      </TabsContent>
    </Tabs>
  </div>
</template>
<style scoped>

</style>