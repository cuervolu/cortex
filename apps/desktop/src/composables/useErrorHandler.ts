import {useToast} from "@cortex/shared/components/ui/toast";

export const useErrorHandler = () => {
  const { toast } = useToast()

  const handleError = (error: unknown) => {
    const message = error instanceof Error ? error.message : String(error)

    toast({
      title: 'Error',
      description: message,
      variant: 'destructive',
    })

    // Opcional: log del error
    console.error('Error:', error)
  }

  return {
    handleError
  }
}