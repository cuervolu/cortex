<script setup lang="ts">
import { toTypedSchema } from '@vee-validate/zod'
import * as z from 'zod'
import { useForm } from 'vee-validate'
import {
  FormControl,
  FormField,
  FormItem,
  FormMessage
} from '@/components/ui/form'
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
  CardFooter
} from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Separator } from '@/components/ui/separator'
import GithubIcon from "~/components/icons/GithubIcon.vue"
import GoogleIcon from "~/components/icons/GoogleIcon.vue"
import { Loader2 } from 'lucide-vue-next'

interface Props {
  title: string
  submitText: string
  subtitle: string
  showNameFields?: boolean
  showUsernameField?: boolean
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  title: 'Authentication',
  submitText: 'Submit',
  subtitle: 'Enter your details',
  showNameFields: false,
  showUsernameField: false,
  loading: false
})

interface FormValues {
  email: string
  password: string
  firstName: string
  lastName: string
  username: string
}

const emit = defineEmits<{
  (e: 'submit', formData: FormValues): void
  (e: 'login', provider: 'github' | 'google'): void
}>()

const formSchema = toTypedSchema(z.object({
  email: z.string().email('Invalid email address'),
  password: z.string().min(8, 'Password must be at least 8 characters'),
  firstName: z.string().min(2, 'First name must be at least 2 characters'),
  lastName: z.string().min(2, 'Last name must be at least 2 characters'),
  username: z.string().min(4).max(20, 'Username must be between 4 and 20 characters')
}))

const form = useForm<FormValues>({
  validationSchema: formSchema,
})

const onSubmit = form.handleSubmit((values) => {
  emit('submit', values)
})

const handleLogin = (provider: 'github' | 'google') => {
  emit('login', provider)
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center">
    <Card class="w-full max-w-md bg-white bg-opacity-20 backdrop-blur-lg rounded-xl shadow-lg">
      <CardHeader>
        <CardTitle class="text-3xl font-bold text-purple-900 text-center">{{ props.title }}</CardTitle>
        <CardDescription class="text-lg text-purple-700 text-center">{{ props.subtitle }}</CardDescription>
      </CardHeader>
      <CardContent>
        <div class="flex gap-4 mb-6">
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
        <div class="relative mb-6">
          <Separator/>
          <span
              class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 bg-white px-2 text-sm text-gray-500 uppercase"
          >
            Or continue with
          </span>
        </div>
        <form class="space-y-4 text-gray-900" @submit="onSubmit">
          <div v-if="props.showNameFields" class="grid grid-cols-2 gap-4">
            <FormField v-slot="{ componentField }" name="firstName">
              <FormItem>
                <FormControl>
                  <Input placeholder="First Name" v-bind="componentField"/>
                </FormControl>
                <FormMessage/>
              </FormItem>
            </FormField>
            <FormField v-slot="{ componentField }" name="lastName">
              <FormItem>
                <FormControl>
                  <Input placeholder="Last Name" v-bind="componentField"/>
                </FormControl>
                <FormMessage/>
              </FormItem>
            </FormField>
          </div>
          <FormField v-if="props.showUsernameField" v-slot="{ componentField }" name="username">
            <FormItem>
              <FormControl>
                <Input placeholder="Username" v-bind="componentField"/>
              </FormControl>
              <FormMessage/>
            </FormItem>
          </FormField>
          <FormField v-slot="{ componentField }" name="email">
            <FormItem>
              <FormControl>
                <Input type="email" placeholder="Email" v-bind="componentField"/>
              </FormControl>
              <FormMessage/>
            </FormItem>
          </FormField>
          <FormField v-slot="{ componentField }" name="password">
            <FormItem>
              <FormControl>
                <Input type="password" placeholder="Password" v-bind="componentField"/>
              </FormControl>
              <FormMessage/>
            </FormItem>
          </FormField>
          <Button type="submit" class="w-full bg-purple-700 hover:bg-purple-800 text-white" :disabled="props.loading">
            <Loader2 v-if="props.loading" class="mr-2 h-4 w-4 animate-spin" />
            {{ props.submitText }}
          </Button>
        </form>
      </CardContent>
      <CardFooter>
        <slot name="footer"/>
      </CardFooter>
    </Card>
  </div>
</template>

<style scoped>
:deep(.bg-purple-700) {
  background-color: #6b21a8;
}

:deep(.hover\:bg-purple-800:hover) {
  background-color: #581c87;
}

:deep(.text-purple-900) {
  color: #4c1d95;
}

:deep(.text-purple-700) {
  color: #6b21a8;
}
</style>