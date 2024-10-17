import { ref } from "vue";

export function useSendMessage(emit: (event: 'send-message', message: string) => void) {
  const userMessage = ref("");

  const sendMessage = () => {
    if (userMessage.value.trim()) {
      emit("send-message", userMessage.value);
      userMessage.value = "";
    }
  };

  return {
    userMessage,
    sendMessage,
  };
}
