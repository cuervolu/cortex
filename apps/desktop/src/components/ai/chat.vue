<script setup lang="ts">
import { Send, AlertCircle, BugOff, CircleHelp, Code, Footprints } from "lucide-vue-next";
import { VueMarkdownIt } from '@f3ve/vue-markdown-it';
import type { Message } from "~/types";
import logo from "~/assets/img/cortex_logo_dark_mode.svg";
interface Props {
  messages: Message[];
  isStreaming: boolean;
  currentStreamingMessage: string;
  isSending: boolean;
  error: string | null;
  avatarSrc?: string;
  explanation?: string;
  streamingMessage?: string;
  cortexLogo?: string;
}

const props = withDefaults(defineProps<Props>(), {
  avatarSrc: "https://placewaifu.com/image",
  explanation: "",
  streamingMessage: "CORTEX-IA está escribiendo...",
  cortexLogo: logo,
  error: null
});

const emit = defineEmits<{
  (e: 'send-message', message: string): void;
}>();

const userMessage = ref('');

const isChatBlocked = computed(() => {
  return props.error !== null || props.isSending || props.isStreaming;
});

const sendMessage = async () => {
  const message = userMessage.value.trim();
  if (!message || isChatBlocked.value) return;

  emit('send-message', message);
  userMessage.value = '';
};

const handleKeydown = (event: KeyboardEvent) => {
  if (event.key === 'Enter' && !event.shiftKey && !event.ctrlKey) {
    event.preventDefault();
    sendMessage();
  }
};

const { $markdown } = useNuxtApp();

const prompts = {
  debugging: [
    "Analiza las pruebas unitarias para identificar por qué mi solución no pasa todos los test",
    "Compara mi implementación con los requisitos originales del ejercicio"
  ],
  consultas: [
    "Explícame la estrategia de resolución más eficiente para este problema",
    "Dame recomendaciones para optimizar mi código actual"
  ],
  codigo: [
    "Muestra una implementación alternativa para resolver este ejercicio",
    "Genera código de ejemplo que demuestre los conceptos clave de este problema"
  ],
  tutoriales: [
    "Desglosa los conceptos de programación que se evalúan en este ejercicio",
    "Explícame paso a paso la lógica que se requiere para resolver este desafío"
  ]
}

const selectPrompt = (prompt: string) => {
  userMessage.value = prompt
}
</script>

<template>
  <div
      class="flex flex-col gap-4 p-3 sm:p-5 bg-muted/50 rounded-lg shadow-lg shadow-current/30 dark:shadow-sm overflow-hidden h-full my-4 mx-7 rounded-corners-gradient-borders">
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

    <!-- Error Alert -->
    <Alert v-if="error" variant="destructive" class="mb-4">
      <AlertCircle class="h-4 w-4" />
      <AlertTitle>Error</AlertTitle>
      <AlertDescription>
        {{ error }}
      </AlertDescription>
    </Alert>

    <!-- Messages Container -->
    <div class="flex-grow overflow-y-auto overflow-x-hidden rounded-lg">
      <!-- Placeholder para chat de Cortex -->
      <div v-if="messages.length === 0" class="flex flex-col items-center justify-center h-full text-center py-10 px-4">
        <div class="p-8 max-w-md mx-auto">
          <div class="mb-6 flex justify-center items-center space-x-3">
            <img
              class="w-12"
              alt="Cortex logo"
              :src="cortexLogo"
            >
            <h2 class="text-2xl font-bold text-foreground tracking-tight">Tu Guía de Código</h2>
          </div>
          
          <p class="text-base text-muted-foreground mb-6 leading-relaxed">
            Estás listo para aprender, resolver dudas y mejorar tus habilidades de programación. 
            ¿Qué quieres explorar hoy?
          </p>
          
          <div class="grid grid-cols-2 gap-3 mb-6">
            <Popover v-for="(categoryPrompts, category) in prompts" :key="category">
              <PopoverTrigger as-child>
                <div class="bg-primary/10 rounded-lg p-3 text-sm text-foreground hover:bg-primary/20 cursor-pointer transition-colors group">
                  <div class="flex items-center space-x-2">
                    <BugOff v-if="category === 'debugging'" class="text-primary"/>
                    <CircleHelp v-else-if="category === 'consultas'" class="text-primary"/>
                    <Code v-else-if="category === 'codigo'" class="text-primary"/>
                    <Footprints v-else class="text-primary"/>
                    <span>{{ 
                      category === 'debugging' ? 'Debugging' : 
                      category === 'consultas' ? 'Consultas' : 
                      category === 'codigo' ? 'Código' : 
                      'Tutoriales' 
                    }}</span>
                  </div>
                </div>
              </PopoverTrigger>
              <PopoverContent class="w-80">
                <div class="grid gap-4">
                  <div class="space-y-2">
                    <h4 class="font-medium leading-none capitalize">{{ category }}</h4>
                    <p class="text-sm text-muted-foreground">Selecciona un prompt para tu consulta</p>
                  </div>
                  <div class="grid gap-2">
                    <Button 
                      v-for="prompt in categoryPrompts" 
                      :key="prompt"
                      variant="outline"
                      class="w-full justify-start text-left whitespace-normal h-auto"
                      @click="selectPrompt(prompt)"
                    >
                      {{ prompt }}
                    </Button>
                  </div>
                </div>
              </PopoverContent>
            </Popover>
          </div>
          <div class="border-t border-primary/20 pt-4">
            <p class="text-xs text-muted-foreground">
              Comienza escribiendo tu primera consulta de programación
            </p>
          </div>
        </div>
      </div>
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
                    :options="$markdown.options"
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
                  :options="$markdown.options"
                  class="text-xs sm:text-sm text-foreground break-words"
              />
            </div>
          </CardContent>
        </Card>
      </div>
    </div>

    <!-- Error Hint -->
    <p v-if="error" class="text-xs text-destructive text-center mt-2">
      Chat bloqueado debido a un error. Por favor, recarga la página o cambia el modelo de IA.
    </p>
  </div>
  <!-- Input Area -->
    <div
      class="flex items-start justify-between px-3 sm:px-6 py-2 shadow my-4 mx-7 transition-all duration-200"
      :class="[
      error ? 'bg-destructive/20' : 'bg-background',
      { 'opacity-50': isChatBlocked },
      userMessage.split('\n').length > 2 || userMessage.length > 50 ? 'rounded-3xl' : 'rounded-full'
      ]"
    >
      <Textarea
      v-model="userMessage"
      placeholder="Escribe tu mensaje aquí..."
      class="w-full bg-transparent border-none text-sm sm:text-base text-muted-foreground outline-none min-h-[24px] max-h-[200px] h-fit resize-none py-0 focus:ring-transparent focus-visible:ring-transparent overflow-y-auto"
      :disabled="isChatBlocked"
      @keydown="handleKeydown"
      @input="$event.target.style.height = 'auto'; $event.target.style.height = $event.target.scrollHeight + 'px'"
      />
      <Button
      size="icon"
      class="flex bg-background shadow-inherit shadow-lg hover:bg-foreground/20 mt-1"
      :disabled="isChatBlocked"
      @click="sendMessage"
      >
      <Send class="h-5 cursor-pointer text-foreground flex-shrink-0" :class="{ 'opacity-50': isChatBlocked }" />
      </Button>
    </div>
</template>

<style scoped>
.rounded-corners-gradient-borders {
  border: double 4px transparent;
  border-radius: 20px;
  background-image: linear-gradient(hsl(var(--background)), hsl(var(--background))), radial-gradient(circle at top left, #92D2DD,hsl(var(--primary)));
  background-origin: border-box;
  background-clip: padding-box, border-box;
}
</style>