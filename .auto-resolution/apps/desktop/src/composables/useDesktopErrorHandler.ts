import {
  isPermissionGranted,
  requestPermission,
  sendNotification
} from '@tauri-apps/plugin-notification'
import {error as logError, debug, error} from "@tauri-apps/plugin-log"
import type {ErrorHandler, ErrorOptions} from '@cortex/shared/types'
import {AppError} from '@cortex/shared/types'
import {useToast} from "@cortex/shared/components/ui/toast"

export const useDesktopErrorHandler = (): ErrorHandler => {
  const {toast} = useToast()

  const showSystemNotification = async (message: string, title: string) => {
    try {
      let permissionGranted = await isPermissionGranted()
      if (!permissionGranted) {
        const permission = await requestPermission()
        permissionGranted = permission === 'granted'
      }
      if (permissionGranted) {
        sendNotification({title, body: message})
        await debug('System notification sent successfully')
      }
    } catch (e) {
      await logError(`Failed to send system notification: ${e}`)
    }
  }

  const handleGeminiError = (error: AppError) => {
    const isGeminiError = error.message.includes('modelo está temporalmente sobrecargado') ||
        error.message.includes('límite de solicitudes');

    if (isGeminiError) {
      const chatStore = useChatStore();
      toast({
        title: "Error de Gemini",
        description: error.message,
        variant: 'destructive',
        action: {
          label: "Usar Ollama",
          onClick: async () => {
            try {
              const ollamaModel = {
                value: 'ollama',
                label: 'Ollama',
                description: 'Modelo local basado en Llama, eficiente para la mayoría de tareas.'
              };

              chatStore.clearChat();

              await chatStore.setProvider('ollama');

              const selectedModel = ref('ollama');

              toast({
                title: "Modelo Cambiado",
                description: "Se ha cambiado automáticamente a Ollama",
                variant: 'default'
              });

              const {handleModelChange} = useAIChat();
              await handleModelChange(ollamaModel);

            } catch (e) {
              const errorHandler = useDesktopErrorHandler();
              await errorHandler.handleError(e, {
                statusCode: 500,
                data: {
                  action: 'switch_to_ollama',
                  error: e instanceof Error ? e.message : 'Unknown error'
                },
                silent: true
              });
            }
          }
        },
        duration: 10000
      });
      return true;
    }
    return false;
  };

  const handleError = async (err: unknown, options: ErrorOptions = {}) => {

    if (err instanceof AppError && err.isHandled) {
      return
    }

    const appError = err instanceof AppError
        ? err
        : new AppError(err instanceof Error ? err.message : String(err), options)
    appError.isHandled = true
    await logError(`Error details: ${appError.message}`, {
      keyValues: {
        statusCode: appError.statusCode.toString(),
        data: JSON.stringify(appError.data || {}),
        stack: appError.stack || 'No stack trace available'
      }
    })


    if (!options.silent) {
      if (handleGeminiError(appError)) {
        return;
      }
      if (!appError.fatal) {
        toast({
          title: `Error ${appError.statusCode}`,
          description: appError.message,
          variant: 'destructive',
        })
        return
      }

      // Only for fatal errors
      await showSystemNotification(
          appError.message,
          `¡Oops! Error ${appError.statusCode}`
      )
      await error(`Fatal error occurred: ${appError.message}, ${appError.stack}`)
      return
      // throw createError({
      //   statusCode: appError.statusCode,
      //   message: appError.message,
      //   fatal: true,
      //   data: appError.data,
      //   stack: appError.stack,
      // })
    }
  }

  return {
    handleError,
    showSystemNotification
  }
}