<script setup lang="ts">
import { Send } from "lucide-vue-next";
import { useChat, useSendMessage, type Message } from "../../composables";


interface Props {
  messages: Message[];
  isStreaming: boolean;
  currentStreamingMessage: string;
  avatarSrc: string;
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
  (e: "send-message", message: string): void;
}>();

const { processedMessages, processedStreamingMessage } = useChat(props.messages, props.currentStreamingMessage);
const { userMessage, sendMessage } = useSendMessage(emit);
</script>

<template>
   <div
    class="flex flex-col gap-4 p-3 sm:p-5 bg-muted/50 rounded-lg border-2 border-transparent shadow-md overflow-hidden h-full"
    style="
      border-image: linear-gradient(
          to bottom,
          rgb(56, 22, 83),
          rgb(146, 210, 221)
        )
        1;
    "
  >
    <div class="flex justify-center">
      <div class="relative w-[152px] h-[33px]">
        <p
          class="absolute top-[5px] left-[33px] font-normal text-muted-foreground text-base sm:text-lg"
        >
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

    <div class="flex-grow overflow-y-auto overflow-x-hidden rounded-lg">
      <div v-for="(message, index) in processedMessages" :key="index">
        <div
          v-if="message.sender === 'user'"
          class="flex justify-end py-2 sm:py-3"
        >
          <div
            class="inline-flex items-center p-2 bg-white rounded-lg max-w-[80%] sm:max-w-[70%]"
            style="
              word-break: break-word;
              overflow-wrap: anywhere;
              overflow: hidden;
            "
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
        <div
          v-if="message.sender === 'ai'"
          class="flex justify-center py-2 sm:py-3"
        >
          <Card class="w-full mx-auto">
            <CardContent class="overflow-x-auto">
              <MDCRenderer
                v-if="message.parsedContent"
                :data="message.parsedContent.data"
                :body="message.parsedContent.body"
                class="text-xs sm:text-sm text-foreground break-words py-5"
              />
            </CardContent>
          </Card>
        </div>
      </div>

      <div v-if="isStreaming" class="flex justify-center py-2 sm:py-3">
        <Card class="w-full mx-auto">
          <CardHeader>
            <span>{{ streamingMessage }}</span>
          </CardHeader>
          <CardContent class="overflow-x-auto">
            <MDCRenderer
              v-if="processedStreamingMessage"
              :data="processedStreamingMessage.data"
              :body="processedStreamingMessage.body"
              class="text-xs sm:text-sm text-foreground break-words py-5"
            />
          </CardContent>
        </Card>
      </div>
    </div>
    <div
      class="flex items-center justify-between px-3 sm:px-6 py-2 bg-primary rounded-full shadow mt-4"
    >
      <Textarea
        v-model="userMessage"
        placeholder="Escribe tu mensaje aquí..."
        class="w-full bg-transparent border-transparent text-xs sm:text-sm text-muted-foreground outline-none min-h-[24px] resize-none py-0"
        @keyup.enter="sendMessage"
      />
      <Button size="icon" variant="ghost">
        <Send
          class="w-2 h-2 sm:w-5 sm:h-5 cursor-pointer ml-2"
          @click="sendMessage"
        />
      </Button>
    </div>
  </div>
</template>

<style scoped>
/* Asegúrate de que el componente padre tenga una altura definida */
:deep(.h-full) {
  height: 100%;
}
</style>
