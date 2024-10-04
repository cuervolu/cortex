<script setup lang="ts">
import { ref, watch, nextTick } from "vue";
import { Send } from "lucide-vue-next";
import { parseMarkdown } from "@nuxtjs/mdc/runtime";
import type { MDCParserResult } from "@nuxtjs/mdc";

interface Message {
  sender: "ai" | "user";
  content: string;
}

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

console.log(props);



const emit = defineEmits<{
  (e: "send-message", message: string): void;
}>();

const userMessage = ref("");
const processedMessages = ref<
  (Message & { parsedContent?: MDCParserResult | null })[]
>([]);

const processedStreamingMessage = ref<MDCParserResult | null>(null);

const processAIMessages = async () => {
  const aiMessages = props.messages.map(async (message) => {
    if (message.sender === "ai") {
      const parsedContent = await parseMarkdown(message.content);
      return { ...message, parsedContent };
    }
    return { ...message };
  });

  processedMessages.value = await Promise.all(aiMessages);
};

const processStreamingMessage = async () => {
  if (props.currentStreamingMessage) {
    processedStreamingMessage.value = await parseMarkdown(
      props.currentStreamingMessage
    );
  } else {
    processedStreamingMessage.value = null;
  }
};

watch(
  () => props.messages,
  async () => {
    await processAIMessages();
    nextTick(() => {
      const chatContainer = document.querySelector(".overflow-y-auto");
      if (chatContainer) {
        chatContainer.scrollTop = chatContainer.scrollHeight;
      }
    });
  },
  { deep: true, immediate: true }
);

watch(
  () => props.currentStreamingMessage,
  async () => {
    await processStreamingMessage();
  },
  { immediate: true }
);

const sendMessage = () => {
  if (userMessage.value.trim()) {
    emit("send-message", userMessage.value);
    userMessage.value = "";
  }
};
</script>

<template>
  <div
    class="flex flex-col gap-4 p-3 sm:p-5 bg-muted/50 rounded-lg border-2 border-transparent shadow-md overflow-hidden"
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
          class="absolute top-[5px] left-[33px] font-normal text-purple-900 text-base sm:text-lg"
        >
          <span class="tracking-wide">CORTEX-</span>
          <span class="tracking-tight">Ia</span>
        </p>
        <img
          class="absolute w-[29px] h-[33px] top-0 left-0"
          alt="Cortex logo"
          :src="cortexLogo"
        />
      </div>
    </div>

    <div
      class="flex-grow overflow-y-auto max-h-[60vh] sm:max-h-[50vh] rounded-lg"
    >
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
            <!-- Agregar el avatar del usuario aquí -->
            <Avatar class="ml-2">
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
            <CardContent>
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
          <CardContent>
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
        rows="1"
        type="text"
        placeholder="Escribe tu mensaje aquí..."
        class="w-full bg-transparent border-transparent text-xs sm:text-sm text-muted-foreground outline-none h-8"
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
