<script setup lang="ts">
import { ref, computed } from 'vue'
import { useForm } from 'vee-validate'
import { Loader2 } from 'lucide-vue-next'
import GoogleIcon from "~/components/icons/GoogleIcon.vue";
import GithubIcon from "~/components/icons/GithubIcon.vue";
import { type RegisterSchemaType, stepSchemas } from "~/schemas/register.schema";

interface Props {
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  loading: false
})

const emit = defineEmits<{
  (e: 'submit', formData: RegisterSchemaType): void
  (e: 'login', provider: 'github' | 'google'): void
}>()

const currentStep = ref(1)
const formData = ref({} as RegisterSchemaType)

const steps = [
  {
    title: 'Account Information',
    fields: ['email', 'password', 'username']
  },
  {
    title: 'Personal Information',
    fields: ['firstname', 'lastname', 'dateOfBirth']
  },
  {
    title: 'Additional Details',
    fields: ['countryCode', 'gender']
  }
]

const currentStepSchema = computed(() => stepSchemas[currentStep.value - 1])
const currentStepFields = computed(() => steps[currentStep.value - 1].fields)

const form = useForm({
  validationSchema: currentStepSchema,
})

const onSubmit = form.handleSubmit((values) => {
  Object.assign(formData.value, values)

  if (currentStep.value < steps.length) {
    currentStep.value++
    form.resetForm()
  } else {
    emit('submit', formData.value as RegisterSchemaType)
  }
})

const handleLogin = (provider: 'github' | 'google') => {
  emit('login', provider)
}

const handlePrevious = () => {
  if (currentStep.value > 1) {
    currentStep.value--
  }
}

const genderOptions = [
  { value: 'MALE', label: 'Male' },
  { value: 'FEMALE', label: 'Female' },
  { value: 'OTHER', label: 'Other' },
  { value: 'NON_BINARY', label: 'Non-binary' },
  { value: 'PREFER_NOT_TO_SAY', label: 'Prefer not to say' }
]

const countryOptions = [
  { value: 'US', label: 'United States' },
  { value: 'CA', label: 'Canada' },
  { value: 'UK', label: 'United Kingdom' },
  // Add more countries as needed
]
</script>

<template>
  <div class="min-h-screen flex items-center justify-center">
    <Card class="w-full max-w-md bg-white bg-opacity-20 backdrop-blur-lg rounded-xl shadow-lg">
      <CardHeader>
        <CardTitle class="text-3xl font-bold text-purple-900 text-center">
          {{ steps[currentStep - 1].title }}
        </CardTitle>
        <CardDescription class="text-lg text-purple-700 text-center">
          Step {{ currentStep }} of {{ steps.length }}
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div v-if="currentStep === 1" class="flex gap-4 mb-6">
          <Button
              variant="outline"
              class="flex-1 bg-white hover:bg-gray-200 hover:text-black text-black"
              :disabled="props.loading"
              @click="handleLogin('github')"
          >
            <GithubIcon class="mr-2 h-5 w-5"/>
            Github
          </Button>
          <Button
              variant="outline"
              class="flex-1 bg-white hover:bg-gray-200 hover:text-black text-black"
              :disabled="props.loading"
              @click="handleLogin('google')"
          >
            <GoogleIcon class="mr-2 h-5 w-5"/>
            Google
          </Button>
        </div>
        <div v-if="currentStep === 1" class="relative mb-6">
          <Separator/>
          <span
              class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 bg-white px-2 text-sm text-gray-500 uppercase"
          >
            Or continue with
          </span>
        </div>
        <form class="space-y-4 text-gray-900" @submit.prevent="onSubmit">
          <template v-for="field in currentStepFields" :key="field">
            <FormField v-slot="{ componentField }" :name="field">
              <FormItem>
                <FormLabel>{{ field.charAt(0).toUpperCase() + field.slice(1) }}</FormLabel>
                <FormControl>
                  <template v-if="field === 'dateOfBirth'">
                    <Calendar v-bind="componentField" class="rounded-md border" />
                  </template>
                  <template v-else-if="field === 'gender'">
                    <Select v-bind="componentField">
                      <SelectTrigger>
                        <SelectValue placeholder="Select gender" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem v-for="option in genderOptions" :key="option.value" :value="option.value">
                          {{ option.label }}
                        </SelectItem>
                      </SelectContent>
                    </Select>
                  </template>
                  <template v-else-if="field === 'countryCode'">
                    <Select v-bind="componentField">
                      <SelectTrigger>
                        <SelectValue placeholder="Select country" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem v-for="option in countryOptions" :key="option.value" :value="option.value">
                          {{ option.label }}
                        </SelectItem>
                      </SelectContent>
                    </Select>
                  </template>
                  <template v-else>
                    <Input
                        :type="field === 'password' ? 'password' : field === 'email' ? 'email' : 'text'"
                        :placeholder="field.charAt(0).toUpperCase() + field.slice(1)"
                        v-bind="componentField"
                    />
                  </template>
                </FormControl>
                <FormMessage/>
              </FormItem>
            </FormField>
          </template>
          <Button
              type="submit"
              class="w-full bg-purple-700 hover:bg-purple-800 text-white"
              :disabled="props.loading"
          >
            <Loader2 v-if="props.loading" class="mr-2 h-4 w-4 animate-spin"/>
            {{ currentStep === steps.length ? 'Create account' : 'Next' }}
          </Button>
        </form>
      </CardContent>
      <CardFooter>
        <div class="flex justify-between items-center w-full">
          <Button v-if="currentStep > 1" variant="outline" @click="handlePrevious">
            Previous
          </Button>
          <p v-if="currentStep === steps.length" class="text-sm text-gray-600">
            Already have an account?
            <NuxtLink to="/auth/login" class="text-purple-700 hover:underline">
              Sign in
            </NuxtLink>
          </p>
        </div>
      </CardFooter>
    </Card>
  </div>
</template>