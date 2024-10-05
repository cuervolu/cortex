<script setup lang="ts">
import { toTypedSchema } from '@vee-validate/zod'
import type * as z from 'zod'
import { useForm } from 'vee-validate'
import { Loader2 } from 'lucide-vue-next'
import GoogleIcon from "~/components/icons/GoogleIcon.vue";
import GithubIcon from "~/components/icons/GithubIcon.vue";

import { watch } from 'vue'

interface Props {
  title: string
  submitText: string
  subtitle: string
  showFields: string[]
  loading?: boolean
  validationSchema: z.ZodType<any, any>
  currentStep: number
  totalSteps: number
}

const props = withDefaults(defineProps<Props>(), {
  title: 'Authentication',
  submitText: 'Submit',
  subtitle: 'Enter your details',
  loading: false,
  currentStep: 1,
  totalSteps: 1
})

type FormValues = z.infer<typeof props.validationSchema>

const emit = defineEmits<{
  (e: 'submit', formData: Partial<FormValues>): void
  (e: 'login', provider: 'github' | 'google'): void
  (e: 'next' | 'previous'): void
}>()

const form = useForm<FormValues>({
  validationSchema: toTypedSchema(props.validationSchema),
})

const onSubmit = form.handleSubmit((values) => {
  const visibleFieldValues = props.showFields.reduce((acc, field) => {
    acc[field] = values[field];
    return acc;
  }, {} as Partial<FormValues>);

  if (props.currentStep < props.totalSteps) {
    emit('next')
  } else {
    emit('submit', visibleFieldValues)
  }
})

// Watch for changes in the dateOfBirth field
watch(() => form.values.dateOfBirth, (newValue) => {
  if (newValue && props.showFields.includes('dateOfBirth')) {
    // Trigger form submission when a date is selected
    onSubmit()
  }
})

const handleLogin = (provider: 'github' | 'google') => {
  emit('login', provider)
}

const goToPreviousStep = () => {
  emit('previous')
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center">
    <Card class="w-full max-w-md bg-white bg-opacity-20 backdrop-blur-lg rounded-xl shadow-lg">
      <CardHeader>
        <CardTitle class="text-3xl font-bold text-purple-900 text-center">
          {{ props.title }}
        </CardTitle>
        <CardDescription class="text-lg text-purple-700 text-center">
          {{ props.subtitle }}
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div v-if="props.showFields.includes('email') && props.currentStep === 1" class="flex gap-4 mb-6">
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
        <div v-if="props.showFields.includes('email') && props.currentStep === 1" class="relative mb-6">
          <Separator/>
          <span
              class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 bg-white px-2 text-sm text-gray-500 uppercase"
          >
            Or continue with
          </span>
        </div>
        <form class="space-y-4 text-gray-900" @submit="onSubmit">
          <template v-for="field in props.showFields" :key="field">
            <FormField v-slot="{ componentField }" :name="field">
              <FormItem>
                <FormLabel>{{ field.charAt(0).toUpperCase() + field.slice(1) }}</FormLabel>
                <FormControl>
                  <template v-if="field === 'dateOfBirth'">
                    <Calendar v-bind="componentField" class="rounded-md border" />
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
            {{ props.submitText }}
          </Button>
        </form>
      </CardContent>
      <CardFooter>
        <div class="flex justify-between items-center w-full">
          <Button v-if="props.currentStep > 1" variant="outline" @click="goToPreviousStep">
            Previous
          </Button>
          <slot name="footer"/>
        </div>
      </CardFooter>
    </Card>
  </div>
</template>