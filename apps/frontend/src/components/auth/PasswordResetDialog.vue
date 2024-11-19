<script setup lang="ts">
import { X, Loader2 } from 'lucide-vue-next'
import {API_ROUTES} from "@cortex/shared/config/api";

const isOpen = ref(false)
const isSuccessDialogOpen = ref(false)
const loading = ref(false)
const email = ref('')
const { handleError } = useWebErrorHandler()

const handleSubmit = async (e: Event) => {
  e.preventDefault()
  try {
    loading.value = true

    await $fetch(API_ROUTES.FORGOT_PASSWORD, {
      method: 'POST',
      body: {
        email: email.value
      }
    })

    isOpen.value = false
    isSuccessDialogOpen.value = true
    email.value = '' // Limpiar el campo después del éxito

  } catch (error) {
    await handleError(error)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <!-- Botón inicial -->
  <Button
      variant="link"
      class="text-[#5e4a6e] dark:text-[#BEBEBE] text-[16.85px] font-medium p-0"
      @click="isOpen = true"
  >
    Olvidaste tu contraseña?
  </Button>

  <!-- Diálogo principal de ingreso de email -->
  <Dialog :open="isOpen" @update:open="isOpen = $event">
    <DialogContent class="card-login-form w-full max-w-md p-[29px] gap-[15px]">
      <DialogHeader>
        <DialogTitle class="text-[28.88px] font-semibold text-[#181b32] dark:text-[#E1E0E0] text-center">
          Reiniciar contraseña
        </DialogTitle>
        <DialogDescription class="text-[16.85px] text-[#5e4a6e] dark:text-[#BEBEBE] text-center">
          Ingresa el correo electrónico con el que te registraste y te enviaremos un código de verificación.
        </DialogDescription>
      </DialogHeader>

      <form @submit="handleSubmit" class="space-y-4">
        <div class="space-y-2">
          <Label class="text-[#181B32] dark:text-[#E1E0E0] font-medium text-[16.85px]">
            Email
          </Label>
          <Input
              type="email"
              placeholder="m@example.com"
              class="bg-[#FFFFFF] border-[#E4E4E7] placeholder:text-[#A0A0A0] text-[#181B32]"
              v-model="email"
              required
          />
        </div>
        <Button
            type="submit"
            class="w-full text-white text-[16.85px]"
            :disabled="loading"
        >
          <Loader2 v-if="loading" class="mr-2 h-4 w-4 animate-spin" />
          Enviar código de verificación
        </Button>
      </form>

      <Button
          variant="ghost"
          class="absolute right-4 top-4 rounded-sm opacity-70 ring-offset-background transition-opacity hover:opacity-100"
          @click="isOpen = false"
      >
        <X class="h-4 w-4" />
      </Button>
    </DialogContent>
  </Dialog>

  <!-- Diálogo de éxito -->
  <Dialog :open="isSuccessDialogOpen" @update:open="isSuccessDialogOpen = $event">
    <DialogContent>
      <DialogHeader>
        <DialogTitle>Correo enviado</DialogTitle>
        <DialogDescription>
          Se ha enviado un correo con las instrucciones para resetear tu contraseña. Por favor, revisa tu bandeja de entrada.
        </DialogDescription>
      </DialogHeader>
      <DialogFooter>
        <Button @click="isSuccessDialogOpen = false">
          Entendido
        </Button>
      </DialogFooter>
    </DialogContent>
  </Dialog>
</template>