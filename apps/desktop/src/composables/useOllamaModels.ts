import { ref, onMounted } from 'vue'
import { invoke } from '@tauri-apps/api/core'
import { info, error as logError } from '@tauri-apps/plugin-log'

interface OllamaModel {
  name: string
  tags: string[]
  description: string
}

export function useOllamaModels() {
  const models = ref<OllamaModel[]>([])
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  async function fetchOllamaModels(retries = 3) {
    isLoading.value = true
    error.value = null

    try {
      models.value = await invoke<OllamaModel[]>('get_ollama_models')
      await info(`Successfully fetched ${models.value.length} Ollama models`)
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'An unknown error occurred'

      if (retries > 0) {
        await logError(`Failed to fetch Ollama models: ${errorMessage}. Retrying...`)
        await new Promise(resolve => setTimeout(resolve, 5000)) // Wait for 5 seconds before retrying
        return fetchOllamaModels(retries - 1)
      }

      error.value = errorMessage
      await logError(`Failed to fetch Ollama models after multiple attempts: ${errorMessage}`)
      await logError(`Error details: ${JSON.stringify(err)}`)
    } finally {
      isLoading.value = false
    }
  }

  onMounted(async () => {
    await fetchOllamaModels()
  })

  return {
    models,
    isLoading,
    error,
    fetchOllamaModels
  }
}