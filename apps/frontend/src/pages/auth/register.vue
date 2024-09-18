<script setup lang="ts">
import {ref} from 'vue'
import {useAuthStore} from '~/stores'

definePageMeta({
  layout: 'auth-default',
})

const auth = useAuthStore()
const router = useRouter()
const error = ref('')
const loading = ref(false)
const showSuccessDialog = ref(false)

const handleSubmit = async (formData: {
  email: string,
  firstName: string,
  lastName: string,
  username: string,
  password: string
}) => {
  error.value = ''
  loading.value = true
  try {
    await auth.register({
      email: formData.email,
      firstname: formData.firstName,
      lastname: formData.lastName,
      username: formData.username,
      password: formData.password
    })
    showSuccessDialog.value = true
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'An error occurred during registration'
    console.error(err)
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
    <AuthForm
        title="Create an Account"
        subtitle="Enter your details below to create your account"
        submit-text="Create account"
        :show-name-fields="true"
        :show-username-field="true"
        :loading="loading"
        @submit="handleSubmit"
        @login="handleLogin"
    >
      <template #footer>
        <p class="text-center mt-4 text-sm text-gray-600">
          Already have an account?
          <NuxtLink to="/auth/login" class="text-purple-700 hover:underline">
            Sign in
          </NuxtLink>
        </p>
      </template>
    </AuthForm>

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