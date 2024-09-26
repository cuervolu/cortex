import { ref } from 'vue'
import { invoke } from '@tauri-apps/api/core'
import { warn, debug, info, error } from '@tauri-apps/plugin-log'

const isOllamaInstalled = ref<boolean | null>(null)
const isChecking = ref(false)
const checkError = ref<string | null>(null)

export function useOllamaDetection() {
  async function checkOllamaInstallation() {
    if (isChecking.value || isOllamaInstalled.value !== null) return

    isChecking.value = true
    checkError.value = null

    try {
      const result = await invoke<boolean>('is_ollama_installed')
      isOllamaInstalled.value = result
      if (result) {
        await info('Ollama is installed')
      } else {
        await warn('Ollama is not installed')
      }
    } catch (err) {
      isOllamaInstalled.value = false
      if (err instanceof Error) {
        switch (err.message) {
          case 'Ollama not found':
            await warn('Ollama is not installed. Please install it to continue.')
            checkError.value = 'Ollama not found. Please install it to continue.'
            break
          case 'Unsupported operating system':
            await error('Your operating system is not supported for Ollama detection.')
            checkError.value = 'Your operating system is not supported for Ollama detection.'
            break
          case 'Failed to execute system command':
            await error('An error occurred while checking for Ollama installation.')
            checkError.value = 'An error occurred while checking for Ollama installation.'
            break
          default:
            await error(`An unexpected error occurred: ${err.message}`)
            checkError.value = `An unexpected error occurred: ${err.message}`
        }
      } else {
        await error('An unknown error occurred')
        checkError.value = 'An unknown error occurred'
      }
    } finally {
      isChecking.value = false
      await debug(`Ollama installation check completed. Installed: ${isOllamaInstalled.value}`)
    }
  }

  return {
    isOllamaInstalled,
    isChecking,
    checkError,
    checkOllamaInstallation
  }
}