<script setup lang="ts">
import { ref } from 'vue'

const isOpen = ref(false)
const loading = ref(false)
const email = ref('')

const handleSubmit = async (e: Event) => {
  e.preventDefault()
  loading.value = true
  // Agrega aquí tu lógica de reseteo de contraseña
  await new Promise(resolve => setTimeout(resolve, 1000)) // Simula llamada API
  loading.value = false
  isOpen.value = false
}
</script>

<template>
  <Button 
    variant="link" 
    class="text-[#5e4a6e] dark:text-[#BEBEBE] text-[16.85px] font-medium p-0"
    @click="isOpen = true"
  >
    Olvidaste tu contraseña?
  </Button>

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
          <label class="text-[#181B32] dark:text-[#E1E0E0] font-medium text-[16.85px]">
            Email
          </label>
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
</template>