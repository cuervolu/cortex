<script setup lang="ts">
import { toTypedSchema } from '@vee-validate/zod'
import * as z from 'zod'
import { useForm } from 'vee-validate'
import { Loader2 } from 'lucide-vue-next'
import GoogleIcon from "~/components/icons/GoogleIcon.vue";
import GithubIcon from "~/components/icons/GithubIcon.vue";

interface Props {
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  loading: false
})

const emit = defineEmits<{
  (e: 'submit', formData: { username: string; password: string }): void
  (e: 'login', provider: 'github' | 'google'): void
}>()

const formSchema = toTypedSchema(z.object({
  username: z.string().min(3, "Username must be at least 5 characters"),
  password: z.string().min(8, 'Password must be at least 8 characters'),
}))

const form = useForm({
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
        <CardTitle class="text-3xl font-bold text-purple-900 text-center">
          Sign In
        </CardTitle>
        <CardDescription class="text-lg text-purple-700 text-center">
          Log in with a provider or with email
        </CardDescription>
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
        <form class="space-y-4 text-foreground" @submit="onSubmit">
          <FormField v-slot="{ componentField }" name="username">
            <FormItem>
              <FormLabel>Username</FormLabel>
              <FormControl>
                <Input
                    type="text"
                    placeholder="Username"
                    autocomplete="username"
                    v-bind="componentField"
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          </FormField>
          <FormField v-slot="{ componentField }" name="password">
            <FormItem>
              <FormLabel>Password</FormLabel>
              <FormControl>
                <Input
                    type="password"
                    placeholder="Password"
                    autocomplete="current-password"
                    v-bind="componentField"
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          </FormField>
          <Button
              type="submit"
              class="w-full bg-purple-700 hover:bg-purple-800 text-white"
              :disabled="props.loading"
          >
            <Loader2 v-if="props.loading" class="mr-2 h-4 w-4 animate-spin"/>
            Sign in
          </Button>
        </form>
      </CardContent>
      <CardFooter>
        <div class="flex justify-between items-center w-full">
          <a href="#" class="text-purple-900 hover:underline text-sm">
            Forgot password?
          </a>
        </div>
      </CardFooter>
    </Card>
  </div>
</template>