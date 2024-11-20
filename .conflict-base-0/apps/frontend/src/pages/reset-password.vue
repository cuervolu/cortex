<script setup lang="ts">
import { toTypedSchema } from '@vee-validate/zod'
import * as z from 'zod'
import { Loader2 } from 'lucide-vue-next'
import { API_ROUTES } from "@cortex/shared/config/api"

definePageMeta({
  layout: 'auth-default',
  auth: {
    unauthenticatedOnly: true,
    navigateAuthenticatedTo: '/explore'
  },
})

const route = useRoute()
const router = useRouter()
const { handleError } = useWebErrorHandler()
const isSuccessDialogOpen = ref(false)
const isSubmitting = ref(false)

const token = route.query.token as string
if (!token) {
  router.push('/login')
}

const resetPasswordSchema = toTypedSchema(
    z.object({
      new_password: z
      .string()
      .min(8, 'La contraseña debe tener al menos 8 caracteres')
      .regex(
          /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]/,
          'La contraseña debe contener al menos una letra mayúscula, una minúscula, un número y un carácter especial'
      ),
      confirm_password: z.string().min(1, 'Confirma tu contraseña'),
    }).refine((data) => data.new_password === data.confirm_password, {
      message: "Las contraseñas no coinciden",
      path: ["confirm_password"],
    })
)

const form = useForm({
  validationSchema: resetPasswordSchema,
})

const onSubmit = form.handleSubmit(async (values) => {
  try {
    isSubmitting.value = true
    await $fetch(API_ROUTES.RESET_PASSWORD, {
      method: 'POST',
      body: {
        token,
        new_password: values.new_password
      }
    })

    isSuccessDialogOpen.value = true
    setTimeout(() => {
      router.push('/auth/login')
    }, 3000)

  } catch (error) {
    await handleError(error)
  } finally {
    isSubmitting.value = false
  }
})
</script>

<template>
  <Card class="mx-auto max-w-md space-y-6 p-6 w-full bg-white bg-opacity-20 backdrop-blur-lg rounded-xl shadow-lg">
    <div class="space-y-2 text-center">
      <h1 class="text-2xl font-bold">Resetear Contraseña</h1>
      <p class="text-gray-500 dark:text-gray-400">
        Ingresa tu nueva contraseña
      </p>
    </div>

    <form @submit="onSubmit" class="space-y-4">
      <FormField
          v-slot="{ componentField }"
          name="new_password"
      >
        <FormItem>
          <FormLabel>Nueva Contraseña</FormLabel>
          <FormControl>
            <Input
                type="password"
                placeholder="********"
                v-bind="componentField"
            />
          </FormControl>
          <FormMessage />
        </FormItem>
      </FormField>

      <FormField
          v-slot="{ componentField }"
          name="confirm_password"
      >
        <FormItem>
          <FormLabel>Confirmar Contraseña</FormLabel>
          <FormControl>
            <Input
                type="password"
                placeholder="********"
                v-bind="componentField"
            />
          </FormControl>
          <FormMessage />
        </FormItem>
      </FormField>

      <Button
          type="submit"
          class="w-full"
          :disabled="isSubmitting"
      >
        <Loader2
            v-if="isSubmitting"
            class="mr-2 h-4 w-4 animate-spin"
        />
        Cambiar Contraseña
      </Button>
    </form>
  </Card>

  <!-- Diálogo de éxito -->
  <Dialog :open="isSuccessDialogOpen" @update:open="isSuccessDialogOpen = $event">
    <DialogContent>
      <DialogHeader>
        <DialogTitle>¡Contraseña actualizada!</DialogTitle>
        <DialogDescription>
          Tu contraseña ha sido actualizada exitosamente. Serás redirigido al inicio de sesión en unos segundos.
        </DialogDescription>
      </DialogHeader>
    </DialogContent>
  </Dialog>
</template>