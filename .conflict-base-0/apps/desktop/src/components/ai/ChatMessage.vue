<script lang="ts" setup>
import { parseMarkdown } from '@nuxtjs/mdc/runtime'
import type { MDCParserResult } from "@nuxtjs/mdc";
import { error as logError } from '@tauri-apps/plugin-log'

const props = defineProps<{
  sender: 'ai' | 'user'
  content: string
}>()

const parsedContent = ref<MDCParserResult | null>(null)

watch(() => props.content, async (newContent) => {
  if (props.sender === 'ai' && newContent.trim()) {
    try {
      parsedContent.value = await parseMarkdown(newContent)
    } catch (error) {
      await logError(`Failed to parse markdown: ${error}`)
      parsedContent.value = null
    }
  } else {
    parsedContent.value = null
  }
}, { immediate: true })
</script>

<template>
  <div :class="`mb-4 flex ${sender === 'ai' ? 'justify-start' : 'justify-end'}`">
    <div v-if="sender === 'ai'" class="max-w-[80%]">
      <MDCRenderer
          v-if="parsedContent && parsedContent.body"
          :body="parsedContent.body"
          :data="parsedContent.data || {}"
          class="mt-1 text-foreground prose dark:prose-invert max-w-none"
      />
      <p v-else class="mt-1 text-foreground">{{ content }}</p>
    </div>
    <div v-else class="flex items-start max-w-[80%]">
      <div class="mr-2">
        <div class="bg-primary text-primary-foreground rounded-lg px-4 py-2 inline-block">
          <p class="text-sm">{{ content }}</p>
        </div>
      </div>
      <Avatar class="w-8 h-8">
        <AvatarImage src="https://placewaifu.com/image/100" alt="User"/>
        <AvatarFallback>U</AvatarFallback>
      </Avatar>
    </div>
  </div>
</template>

<style scoped>
</style>