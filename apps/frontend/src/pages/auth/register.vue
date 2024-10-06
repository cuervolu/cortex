<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '~/stores'
import RegisterForm from "~/components/auth/RegisterForm.vue";
import {type RegisterSchemaType, stepSchemas} from "~/schemas/register.schema";

definePageMeta({
  layout: 'auth-default',
})

const { signUp } = useAuth()
const auth = useAuthStore()
const router = useRouter()
const error = ref('')
const loading = ref(false)
const showSuccessDialog = ref(false)

const handleSubmit = async (credentials: RegisterSchemaType) => {
  error.value = ''
  loading.value = true
  try {
    await signUp(credentials,{ preventLoginFlow: true, redirect: false })
    showSuccessDialog.value = true
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'An error occurred during registration'
  } finally {
    loading.value = false
  }
}

const handleLogin = (provider: 'github' | 'google') => {
  auth.loginWithProvider(provider)
}

const closeDialog = () => {
  showSuccessDialog.value = false
  router.push('/auth/login')
}
</script>

<template>
  <div>
    <RegisterForm
        :loading="loading"
        :step-schemas="stepSchemas"
        @submit="handleSubmit"
        @login="handleLogin"
    />

    <Alert v-if="error" variant="destructive" class="mt-4">
      <AlertTitle>Error</AlertTitle>
      <AlertDescription>{{ error }}</AlertDescription>
    </Alert>

    <Dialog :open="showSuccessDialog" @update:open="showSuccessDialog = $event">
      <DialogContent class="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Registration Successful</DialogTitle>
          <DialogDescription>
            Your account has been created successfully. Please check your email to activate your
            account.
          </DialogDescription>
        </DialogHeader>
        <DialogFooter>
          <Button @click="closeDialog">
            Go to Login
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  </div>
</template>