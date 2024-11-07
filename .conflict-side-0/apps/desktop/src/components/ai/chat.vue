<script setup lang="ts">
import { Send } from "lucide-vue-next";
import type { Message } from "~/types";
import { parseMarkdown } from '@nuxtjs/mdc/runtime';

interface Props {
  messages: Message[];
  isStreaming: boolean;
  currentStreamingMessage: string;
  isSending: boolean;
  avatarSrc?: string;
  explanation?: string;
  streamingMessage?: string;
  cortexLogo?: string;
}

const props = withDefaults(defineProps<Props>(), {
  avatarSrc: "https://placewaifu.com/image",
  explanation: "",
  streamingMessage: "CORTEX-IA está escribiendo...",
  cortexLogo: "https://placewaifu.com/image",
});

const emit = defineEmits<{
  (e: 'send-message', message: string): void;
}>();


const userMessage = ref('');
const renderedMessages = ref<(Message & { parsedContent?: any })[]>([]);
const renderedStreamingContent = ref<any>(null);

// Procesar y renderizar mensajes existentes
watchEffect(async () => {
  renderedMessages.value = await Promise.all(props.messages.map(async (message) => {
    if (message.sender === 'ai') {
      try {
        const parsed = await parseMarkdown(message.content);
        return { ...message, parsedContent: parsed };
      } catch (error) {
        console.error('Failed to parse markdown:', error);
      }
    }
    return message;
  }));
});

watchEffect(async () => {
  if (props.isStreaming && props.currentStreamingMessage) {
    try {
      renderedStreamingContent.value = await parseMarkdown(props.currentStreamingMessage);
    } catch (error) {
      console.error('Failed to parse streaming markdown:', error);
      renderedStreamingContent.value = null;
    }
  } else {
    renderedStreamingContent.value = null;
  }
});

const sendMessage = async () => {
  const message = userMessage.value.trim();
  if (!message || props.isSending || props.isStreaming) return;

  emit('send-message', message);
  userMessage.value = '';
};

const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === 'Enter' && !event.shiftKey && !event.ctrlKey) {
    event.preventDefault();
    sendMessage();
  }
};
</script>

<template>
  <div
    class="flex flex-col gap-4 p-3 sm:p-5 bg-muted/50 rounded-lg border-2 border-transparent shadow-md overflow-hidden h-full"
    style="border-image: linear-gradient(to bottom, rgb(56, 22, 83), rgb(146, 210, 221)) 1;"
  >
    <!-- Header -->
    <div class="flex justify-center">
      <div class="relative w-[152px] h-[33px]">
        <p class="absolute top-[5px] left-[33px] font-normal text-muted-foreground text-base sm:text-lg">
          <span class="tracking-widest">CORTEX-</span>
          <span class="tracking-tight">Ia</span>
        </p>
        <img
          class="absolute w-[29px] h-[33px] top-0 left-0"
          alt="Cortex logo"
          :src="cortexLogo"
        >
      </div>
    </div>

    <!-- Messages Container -->
    <div class="flex-grow overflow-y-auto overflow-x-hidden rounded-lg">
      <!-- Rendered Messages -->
      <div v-for="(message, index) in renderedMessages" :key="index">
        <!-- User Message -->
        <div
          v-if="message.sender === 'user'"
          class="flex justify-end py-2 sm:py-3"
        >
          <div
            class="inline-flex items-center p-2 bg-white rounded-lg max-w-[80%] sm:max-w-[70%]"
          >
            <p class="text-xs sm:text-sm text-purple-900 break-words">
              {{ message.content }}
            </p>
            <Avatar class="ml-2 flex-shrink-0">
              <AvatarImage :src="avatarSrc" alt="User" />
              <AvatarFallback>UN</AvatarFallback>
            </Avatar>
          </div>
        </div>

        <!-- AI Message -->
        <div
          v-if="message.sender === 'ai'"
          class="flex justify-center py-2 sm:py-3"
        >
          <Card class="w-full mx-auto">
            <CardContent class="overflow-x-auto">
              <div
                v-if="message.parsedContent"
                class="prose dark:prose-invert max-w-none py-5"
              >
                <MDCRenderer
                  :data="message.parsedContent.data"
                  :body="message.parsedContent.body"
                  class="text-xs sm:text-sm text-foreground break-words"
                />
              </div>
              <p v-else class="text-xs sm:text-sm text-foreground break-words py-5">
                {{ message.content }}
              </p>
            </CardContent>
          </Card>
        </div>
      </div>


      <!-- Indicador de Escritura -->
      <div v-if="isStreaming && !currentStreamingMessage" class="flex justify-center py-2 sm:py-3">
        <Card class="w-full mx-auto">
          <CardContent class="flex items-center space-x-2 py-4">
            <span class="text-sm text-muted-foreground">{{ streamingMessage }}</span>
            <div class="flex space-x-1">
              <div class="w-2 h-2 bg-primary rounded-full animate-bounce [animation-delay:-0.3s]"/>
              <div class="w-2 h-2 bg-primary rounded-full animate-bounce [animation-delay:-0.15s]"/>
              <div class="w-2 h-2 bg-primary rounded-full animate-bounce"/>
            </div>
          </CardContent>
        </Card>
      </div>



      <!-- Streaming Message -->
      <div v-if="isStreaming && currentStreamingMessage" class="flex justify-center py-2 sm:py-3">
        <Card class="w-full mx-auto">
          <CardContent class="overflow-x-auto">
            <div
                v-if="renderedStreamingContent"
                class="prose dark:prose-invert max-w-none py-5"
            >
              <MDCRenderer
                  :data="renderedStreamingContent.data"
                  :body="renderedStreamingContent.body"
                  class="text-xs sm:text-sm text-foreground break-words"
              />
            </div>
            <p v-else class="text-xs sm:text-sm text-foreground break-words py-5">
              {{ currentStreamingMessage }}
            </p>
          </CardContent>
        </Card>
      </div>
    </div>


    <!-- Input Area -->
    <div class="flex items-center justify-between px-3 sm:px-6 py-2 bg-primary rounded-full shadow mt-4">
      <Textarea
        v-model="userMessage"
        placeholder="Escribe tu mensaje aquí..."
        class="w-full bg-transparent border-transparent text-xs sm:text-sm text-muted-foreground outline-none min-h-[24px] resize-none py-0"
        @keydown="handleKeydown"
      />
      <Button
        size="icon"
        variant="ghost"
        :disabled="isSending || isStreaming"
        @click="sendMessage"
      >
        <Send class="w-2 h-2 sm:w-5 sm:h-5 cursor-pointer ml-2" />
      </Button>
    </div>
  </div>
</template>

<style>
.prose {
  width: 100%;
  color: inherit;
}

.prose pre {
  background-color: rgb(40, 44, 52);
  padding: 1rem;
  border-radius: 0.5rem;
  overflow-x: auto;
}

.prose code {
  color: #e06c75;
  background-color: rgba(40, 44, 52, 0.1);
  padding: 0.2rem 0.4rem;
  border-radius: 0.25rem;
}

.prose pre code {
  color: inherit;
  padding: 0;
  background: none;
}

.dark .prose {
  color: inherit;
}

.dark .prose code {
  background-color: rgba(255, 255, 255, 0.1);
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-4px);
  }
}

.animate-bounce {
  animation: bounce 0.6s infinite;
}
</style>
