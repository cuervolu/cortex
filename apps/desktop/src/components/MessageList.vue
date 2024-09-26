<script setup lang="ts">
import MessageAvatar from "~/components/ai/MessageAvatar.vue";
import type {Message} from "~/types/message";

defineProps<{
  messages: Message[]
  isLoading: boolean
}>()
</script>

<template>
  <div>
    <div
        v-for="message in messages"
        :key="message.id"
        class="mb-4"
    >
      <div
          v-if="message.sender === 'ai' && isLoading && messages[messages.length - 1].id === message.id"
          class="text-xs italic text-muted-foreground mb-1 ml-10">
        La IA está escribiendo...
      </div>
      <div
          :class="`flex items-start ${
          message.sender === 'user' ? 'justify-end' : 'justify-start'
        }`">
        <MessageAvatar v-if="message.sender === 'ai'" :sender="message.sender" class="mr-2" />
        <div
            :class="`rounded-lg p-2 max-w-[70%] ${
            message.sender === 'user' ? 'bg-blue-500 text-white' : 'bg-muted/90'
          }`"
        >
          <MDC v-if="message.sender === 'ai'" :value="message.text" />
          <template v-else>{{ message.text }}</template>
        </div>
        <MessageAvatar v-if="message.sender === 'user'" :sender="message.sender" class="ml-2" />
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>