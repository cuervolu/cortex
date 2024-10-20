import { ref, watch, nextTick } from "vue";
import { parseMarkdown } from "@nuxtjs/mdc/runtime";
import type { MDCParserResult } from "@nuxtjs/mdc";

export interface Message {
  sender: "ai" | "user";
  content: string;
}

export function useChat(messages: Message[], currentStreamingMessage: string) {
  const userMessage = ref("");
  const processedMessages = ref<(Message & { parsedContent?: MDCParserResult | null })[]>([]);
  const processedStreamingMessage = ref<MDCParserResult | null>(null);

  const processAIMessages = async () => {
    const aiMessages = messages.map(async (message) => {
      if (message.sender === "ai") {
        const parsedContent = await parseMarkdown(message.content);
        return { ...message, parsedContent };
      }
      return { ...message };
    });

    processedMessages.value = await Promise.all(aiMessages);
  };

  const processStreamingMessage = async () => {
    if (currentStreamingMessage) {
      processedStreamingMessage.value = await parseMarkdown(currentStreamingMessage);
    } else {
      processedStreamingMessage.value = null;
    }
  };

  watch(
    () => messages,
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
    () => currentStreamingMessage,
    async () => {
      await processStreamingMessage();
    },
    { immediate: true }
  );

  return {
    userMessage,
    processedMessages,
    processedStreamingMessage,
  };
}
