<script setup lang="ts">
import { ref, computed } from 'vue'
import { useAuthStore } from '~/stores'
import { z } from 'zod'
import {toZodDate} from "~/lib/utils";

definePageMeta({
  layout: 'auth-default',
})

const auth = useAuthStore()
const router = useRouter()
const error = ref('')
const loading = ref(false)
const showSuccessDialog = ref(false)
const currentStep = ref(1)
const formData = ref({} as z.infer<typeof formSchema>)

const steps = [
  {
    title: 'Account Information',
    fields: ['email', 'password', 'username']
  },
  {
    title: 'Personal Information',
    fields: ['firstName', 'lastName', 'dateOfBirth']
  },
  {
    title: 'Additional Details',
    fields: ['countryCode', 'gender']
  }
]

const stepSchemas = [
  z.object({
    email: z.string().email('Invalid email address'),
    password: z.string().min(8, 'Password must be at least 8 characters'),
    username: z.string().min(4).max(20, 'Username must be between 4 and 20 characters'),
  }),
  z.object({
    firstName: z.string().min(2, 'First name must be at least 2 characters'),
    lastName: z.string().min(2, 'Last name must be at least 2 characters'),
    dateOfBirth: toZodDate(z.date().max(new Date(), 'Date of birth must be in the past')),
  }),
  z.object({
    countryCode: z.string().min(2).max(3, 'Country code must be 2 or 3 characters'),
    gender: z.enum(['MALE', 'FEMALE', 'OTHER', 'NON_BINARY', 'PREFER_NOT_TO_SAY'], {errorMap: () => ({message: 'Invalid gender'})}),
  }),
]

const currentStepSchema = computed(() => stepSchemas[currentStep.value - 1])

const formSchema = z.object({
  email: z.string().email('Invalid email address'),
  password: z.string().min(8, 'Password must be at least 8 characters'),
  username: z.string().min(4).max(20, 'Username must be between 4 and 20 characters'),
  firstName: z.string().min(2, 'First name must be at least 2 characters'),
  lastName: z.string().min(2, 'Last name must be at least 2 characters'),
  dateOfBirth: toZodDate(z.date().max(new Date(), 'Date of birth must be in the past')),
  countryCode: z.string().min(2).max(3, 'Country code must be 2 or 3 characters'),
  gender: z.enum(['MALE', 'FEMALE', 'OTHER', 'NON_BINARY', 'PREFER_NOT_TO_SAY'], {errorMap: () => ({message: 'Invalid gender'})})
})

type FormValues = z.infer<typeof formSchema>

const currentStepFields = computed(() => steps[currentStep.value - 1].fields)

const handleSubmit = async (stepData: Partial<FormValues>) => {
  Object.assign(formData.value, stepData)

  if (currentStep.value < steps.length) {
    handleNext()
    return
  }

  error.value = ''
  loading.value = true
  try {
    await auth.register(formData.value)
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

const handleNext = () => {
  if (currentStep.value < steps.length) {
    currentStep.value++
  }
}

const handlePrevious = () => {
  if (currentStep.value > 1) {
    currentStep.value--
  }
}
</script>

<template>
  <div>
    <AuthForm
        :title="steps[currentStep - 1].title"
        :subtitle="`Step ${currentStep} of ${steps.length}`"
        :submit-text="currentStep === steps.length ? 'Create account' : 'Next'"
        :show-fields="currentStepFields"
        :loading="loading"
        :validation-schema="currentStepSchema"
        :current-step="currentStep"
        :total-steps="steps.length"
        @submit="handleSubmit"
        @login="handleLogin"
        @next="handleNext"
        @previous="handlePrevious"
    >
      <template #footer>
        <div class="flex justify-between items-center mt-4">
          <p v-if="currentStep === steps.length" class="text-sm text-gray-600">
            Already have an account?
            <NuxtLink to="/auth/login" class="text-purple-700 hover:underline">
              Sign in
            </NuxtLink>
          </p>
        </div>
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