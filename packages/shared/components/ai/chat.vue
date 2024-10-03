<script setup lang="ts">
import { Send } from 'lucide-vue-next'

interface Message {
  sender: 'ai' | 'user'
  content: string
}

interface Props {
  messages: Message[]
  isStreaming: boolean
  currentStreamingMessage: string
  avatarSrc: string
  explanation?: string
  streamingMessage?: string
  cortexLogo?: string
}

const props = withDefaults(defineProps<Props>(), {
  avatarSrc: 'path_to_avatar_image',
  explanation: '',
  streamingMessage: 'CORTEX-IA está escribiendo...',
  cortexLogo: 'https://placewaifu.com/image'
})

const emit = defineEmits<{
  (e: 'send-message', message: string): void
}>()

const userMessage = ref('')

const sendMessage = () => {
  if (userMessage.value.trim()) {
    emit('send-message', userMessage.value)
    userMessage.value = ''
  }
}

watch(() => props.messages, (newMessages) => {
  // Scroll to bottom when new messages arrive
  console.log('Scrolling to bottom, new messages:', newMessages)
  nextTick(() => {
    const chatContainer = document.querySelector('.overflow-y-auto')
    if (chatContainer) {
      chatContainer.scrollTop = chatContainer.scrollHeight
    }
  })
}, { deep: true })
</script>

<template>
  <div class="flex flex-col gap-4 p-3 sm:p-5 bg-gray-50 rounded-lg border-2 border-transparent shadow-md" style="border-image: linear-gradient(to bottom, rgb(56,22,83), rgb(146,210,221)) 1;">
    <div class="flex justify-center">
      <div class="relative w-[152px] h-[33px]">
        <p class="absolute top-[5px] left-[33px] font-normal text-purple-900 text-base sm:text-lg">
          <span class="tracking-wide">CORTEX-</span>
          <span class="tracking-tight">Ia</span>
        </p>
        <img class="absolute w-[29px] h-[33px] top-0 left-0" alt="Cortex logo" :src="cortexLogo">
      </div>
    </div>

    <div class="flex-grow overflow-y-auto max-h-[60vh] sm:max-h-[50vh]">
      <div v-for="(message, index) in messages" :key="index" class="flex items-center gap-2 py-2 sm:py-3" :class="message.sender === 'user' ? 'justify-end' : 'justify-start'">
        <div v-if="message.sender === 'user'" class="inline-flex items-center p-2 bg-white rounded-lg max-w-[80%] sm:max-w-[70%]">
          <p class="text-xs sm:text-sm text-purple-900 break-words">{{ message.content }}</p>
        </div>
        <div class="w-5 h-5 flex-shrink-0" v-if="message.sender === 'user'">
          <img :src="avatarSrc" alt="Avatar" class="w-full h-full rounded-full" >
        </div>
        <div v-if="message.sender === 'ai'" class="inline-flex items-center p-2 bg-white rounded-lg max-w-[80%] sm:max-w-[70%]">
          <p class="text-xs sm:text-sm text-gray-800 break-words">{{ message.content }}</p>
        </div>
      </div>
    </div>

    <div v-if="isStreaming" class="text-xs sm:text-sm text-gray-600">{{ streamingMessage }}</div>

    <div v-if="currentStreamingMessage" class="bg-white rounded-lg p-2">
      <p class="text-xs sm:text-sm text-gray-800">{{ currentStreamingMessage }}</p>
    </div>

    <div v-if="explanation" class="text-xs sm:text-sm" v-html="explanation"/>

    <div class="flex items-center justify-between px-3 sm:px-6 py-2 bg-gray-50 rounded-full shadow mt-4">
      <Input
        v-model="userMessage"
        type="text"
        placeholder="Escribe tu mensaje aquí..."
        class="w-full bg-transparent border-transparent text-xs sm:text-sm text-gray-600 outline-none"
        @keyup.enter="sendMessage"
      />
      <Send
        class="w-7 h-7 sm:w-9 sm:h-9 cursor-pointer ml-2"
        :size="4"
        @click="sendMessage"
      />
    </div>
  </div>
</template>

<style scoped>
.overflow-y-auto {
  scrollbar-width: thin;
  scrollbar-color: rgba(156, 163, 175, 0.5) transparent;
}

.overflow-y-auto::-webkit-scrollbar {
  width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: transparent;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background-color: rgba(156, 163, 175, 0.5);
  border-radius: 20px;
  border: transparent;
}
</style>
