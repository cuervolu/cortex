<script setup lang="ts">
import { SendIcon, UserIcon, PhoneIcon } from 'lucide-vue-next'
import type {ChatMessageRequest, FrontendChatMessage} from "~/interfaces";

const { $webSocket } = useNuxtApp()
const { status, data: userData } = useAuth()

const messages = ref<(FrontendChatMessage)[]>([])
const newMessage = ref('')
const recipientId = ref('')
const mentorshipId = ref<number | null>(null)

const isAuthenticated = computed(() => status.value === 'authenticated')
const userId = computed(() => userData.value?.id)

const generateUniqueId = (() => {
  let id = 0;
  return () => id++;
})();

onMounted(() => {
  if (isAuthenticated.value) {
    startChat()
  }
})

const startChat = async () => {
  if (!isAuthenticated.value) {
    console.error('User is not authenticated')
    return
  }

  await $webSocket.subscribe((message: ChatMessageRequest) => {
    if ('status' in message) {
      if (message.status === 'SUCCESS') {
        console.info('Chat connection successful', message)
      } else if (message.status === 'ERROR') {
        console.error('Chat connection error', message)
      }
    } else {
      const frontendMessage: FrontendChatMessage = {
        ...message,
        id: generateUniqueId()
      }
      messages.value.push(frontendMessage)
    }
  })
}

const sendMessage = () => {
  if (!isAuthenticated.value || userId.value == null || mentorshipId.value === null) {
    console.error('User is not authenticated, user ID is not available, or mentorship ID is not set')
    return
  }

  const backendMessage: ChatMessageRequest = {
    content: newMessage.value,
    sender_id: Number(userId.value),
    recipient_id: parseInt(recipientId.value),
    mentorship_id: mentorshipId.value
  }

  $webSocket.sendMessage(backendMessage)

  const frontendMessage: FrontendChatMessage = {
    ...backendMessage,
    id: generateUniqueId()
  }

  messages.value.push(frontendMessage)
  newMessage.value = ''
}


const isOwnMessage = (message: ChatMessageRequest) => message.sender_id === userId.value
</script>

<template>
  <div class="flex flex-col h-screen bg-gray-900 text-white">
    <div class="bg-gray-800 p-4 flex items-center justify-between">
      <div class="flex items-center">
        <UserIcon class="w-8 h-8 text-gray-400 mr-3"/>
        <div>
          <h2 class="text-lg font-semibold">Chat</h2>
          <p class="text-sm text-gray-400">{{ isAuthenticated ? 'Connected' : 'Disconnected' }}</p>
        </div>
      </div>
      <button
          :disabled="!isAuthenticated"
          class="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded-full transition duration-300 ease-in-out flex items-center"
          @click="startChat"
      >
        <PhoneIcon class="w-4 h-4 mr-2"/>
        Start Chat
      </button>
    </div>

    <div class="flex-grow overflow-y-auto p-4 space-y-4">
      <transition-group name="message">
        <div
            v-for="message in messages"
            :key="message.id"
            :class="[
            'max-w-xs rounded-lg p-3 shadow-md',
            isOwnMessage(message) ? 'bg-blue-600 ml-auto' : 'bg-gray-700'
          ]"
        >
          {{ message.content }}
        </div>
      </transition-group>
    </div>

    <div class="bg-gray-800 p-4">
      <div class="flex space-x-2">
        <input
            v-model="recipientId"
            placeholder="Recipient ID"
            type="number"
            :disabled="!isAuthenticated"
            class="flex-grow bg-gray-700 text-white rounded-full px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-600"
        >
        <input
            v-model="mentorshipId"
            placeholder="Mentorship ID"
            type="number"
            :disabled="!isAuthenticated"
            class="flex-grow bg-gray-700 text-white rounded-full px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-600"
        >
        <input
            v-model="newMessage"
            :disabled="!isAuthenticated"
            placeholder="Type a message..."
            class="flex-grow bg-gray-700 text-white rounded-full px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-600"
            @keyup.enter="sendMessage"
        >
        <button
            :disabled="!isAuthenticated || !newMessage.trim() || !recipientId || !mentorshipId"
            class="bg-blue-600 hover:bg-blue-700 text-white rounded-full p-2 transition duration-300 ease-in-out"
            @click="sendMessage"
        >
          <SendIcon class="w-6 h-6"/>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.message-enter-active,
.message-leave-active {
  transition: all 0.5s ease;
}

.message-enter-from,
.message-leave-to {
  opacity: 0;
  transform: translateY(20px);
}
</style>