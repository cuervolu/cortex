<script setup lang="ts">
import { Send } from "lucide-vue-next";
import { VueMarkdownIt } from '@f3ve/vue-markdown-it';
import highlight from 'highlight.js/lib/core';
import typescript from 'highlight.js/lib/languages/typescript';
import python from 'highlight.js/lib/languages/python';
import go from 'highlight.js/lib/languages/go';
import java from 'highlight.js/lib/languages/java';
import rust from 'highlight.js/lib/languages/rust';
import 'highlight.js/styles/atom-one-dark.css';
import type { Message } from "~/types";

highlight.registerLanguage('typescript', typescript);
highlight.registerLanguage('python', python);
highlight.registerLanguage('go', go);
highlight.registerLanguage('java', java);
highlight.registerLanguage('rust', rust);

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

//* NOTE: Temporal fix for a bug in production
const markdownOptions = {
  html: true,
  linkify: true,
  typographer: true,
  breaks: true,
  highlight: function (str: string, lang: string) {
    if (lang && highlight.getLanguage(lang)) {
      try {
        const result = highlight.highlight(str, {
          language: lang,
          ignoreIllegals: true
        }).value;
        return `<pre class="hljs language-${lang}"><code>${result}</code></pre>`;
      } catch (error) {
        console.error('Error highlighting code:', error);
      }
    }
    // Fallback to plain text if the language is not supported
    return `<pre class="hljs"><code>${str}</code></pre>`;
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
      <div v-for="(message, index) in messages" :key="index">
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
              <div class="prose dark:prose-invert max-w-none py-5">
                <VueMarkdownIt
                    :source="message.content"
                    :options="markdownOptions"
                    class="text-xs sm:text-sm text-foreground break-words"
                />
              </div>
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
            <div class="prose dark:prose-invert max-w-none py-5">
              <VueMarkdownIt
                  :source="currentStreamingMessage"
                  :options="markdownOptions"
                  class="text-xs sm:text-sm text-foreground break-words"
              />
            </div>
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
  padding: 1rem;
  border-radius: 0.5rem;
  overflow-x: auto;
  margin: 1rem 0;
}

.prose code {
  color: inherit;
  padding: 0.2rem 0.4rem;
  border-radius: 0.25rem;
  font-size: 0.875em;
}

.prose pre code {
  padding: 0;
  border-radius: 0;
  background: none;
}

/* Estilos específicos para el tema oscuro */
.dark .prose .hljs {
  background: #1e1e1e;
  color: #d4d4d4;
}

/* Ajustes adicionales para highlight.js */
.hljs {
  background: #282c34;
  color: #abb2bf;
  padding: 1em;
  border-radius: 0.5em;
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