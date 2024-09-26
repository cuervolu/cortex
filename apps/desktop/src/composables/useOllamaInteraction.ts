import { ref, onMounted, onUnmounted } from 'vue'
import { invoke } from '@tauri-apps/api/core'
import { listen, type UnlistenFn } from '@tauri-apps/api/event'
import { info, error } from '@tauri-apps/plugin-log'

export function useOllamaInteraction() {
  const prompt = ref('')
  const response = ref('')
  const isSending = ref(false)
  const isStreaming = ref(false)
  const promptError = ref<string | null>(null)
  let unlisten: UnlistenFn | null = null

  onMounted(async () => {
    unlisten = await listen<string>('ollama-response', (event) => {
      response.value += event.payload
      isStreaming.value = true
    })
  })

  onUnmounted(() => {
    if (unlisten) unlisten()
  })

  async function sendPrompt(userId: string) {
    if (!prompt.value) return
    isSending.value = true
    isStreaming.value = true
    promptError.value = null
    response.value = ''
    try {
      await invoke('send_prompt_to_ollama', {
        model: "llama3:8b",
        prompt: prompt.value,
        userId: userId
      })
      await info(`Prompt sent successfully: ${prompt.value}`)
      prompt.value = '' // Clear the input after sending
    } catch (err) {
      if (err instanceof Error) {
        await error(`Failed to send prompt to Ollama: ${err.message}`)
        promptError.value = err.message
      } else {
        await error('An unknown error occurred while sending prompt to Ollama')
        promptError.value = 'An unknown error occurred'
      }
    } finally {
      isSending.value = false
      isStreaming.value = false
    }
  }

  return {
    prompt,
    response,
    isSending,
    isStreaming,
    promptError,
    sendPrompt
  }
}