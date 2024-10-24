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
  username: z.string().min(5, "Username must be at least 5 characters"),
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
  <Card class="w-full p-[29px] gap-[15px] rounded-[14.5px] card-login-form dark:card-login-form flex flex-col flex-grow">
    <CardHeader class="self-stretch flex-col items-center justify-start flex p-0">
      <CardTitle class="grow shrink basis-0 text-[28.88px] font-semibold text-[#181b32] dark:text-[#E1E0E0] text-center">
        Sign In
      </CardTitle>
      <CardDescription class="grow shrink basis-0 inline-flex text-[16.85px] text-[#5e4a6e] dark:text-[#BEBEBE] text-center">
        Log in with a provider or with email
      </CardDescription>
    </CardHeader>
    <CardContent class="self-stretch flex-col items-start justify-start flex gap-[19.26px] p-0">
      <div class="self-stretch flex gap-6">
        <Button variant="outline" class="flex-1 bg-white hover:bg-gray-200 hover:text-black text-black"
          :disabled="props.loading" @click="handleLogin('github')">
          <GithubIcon class="mr-2 h-5 w-5 fill-[#222222]" />
          Github
        </Button>
        <Button variant="outline" class="flex-1 bg-white hover:bg-gray-200 hover:text-black text-black"
          :disabled="props.loading" @click="handleLogin('google')">
          <GoogleIcon class="mr-2 h-5 w-5" />
          Google
        </Button>
      </div>
      <div class="self-stretch justify-start items-center inline-flex">
        <Separator class="grow shrink basis-0 h-[1.20px] border-[#381653] dark:bg-[#F9CF87]"></Separator>
        <div class="px-[9.63px] flex-col justify-start items-start gap-3 inline-flex">
          <div class="self-stretch justify-center items-center gap-3 inline-flex">
            <div class="text-[#5e4a6e] dark:text-[#BEBEBE] text-sm font-normal">OR CONTINUE WITH</div>
          </div>
        </div>
        <Separator class="grow shrink basis-0 h-[1.20px] bg-[#381653] dark:bg-[#F9CF87]"></Separator>
      </div>

      <form class="self-stretch flex-col gap-[19.26px] inline-flex text-foreground" @submit.prevent="onSubmit">
        <FormField v-slot="{ componentField }" name="username">
          <FormItem>
            <FormLabel class="text-[#181B32] dark:text-[#E1E0E0] font-medium text-[16.85px]" >Username</FormLabel>
            <FormControl class="bg-[#FFFFFF] border-[#E4E4E7]">
              <Input type="text" placeholder="Username" autocomplete="username" v-bind="componentField" class="placeholder:text-[#A0A0A0] text-[#181B32]"/>
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>
        <FormField v-slot="{ componentField }" name="password">
          <FormItem>
            <FormLabel class="text-[#181B32] dark:text-[#E1E0E0] font-medium text-[16.85px]">Password</FormLabel>
            <FormControl class="bg-[#FFFFFF] border-[#E4E4E7]">
              <Input type="password" placeholder="Password" autocomplete="current-password" v-bind="componentField" class="placeholder:text-[#A0A0A0] text-[#181B32]"/>
            </FormControl>
            <FormMessage />
          </FormItem>
        </FormField>
        <Button type="submit" class="w-full text-white text-[16.85px] mt-[10px]" :disabled="props.loading">
          <Loader2 v-if="props.loading" class="mr-2 h-4 w-4 animate-spin" />
          Sign in
        </Button>
      </form>
    </CardContent>
    <CardFooter class="flex justify-center items-center w-full p-0">
      <a href="#" class="self-stretch text-[#5e4a6e] dark:text-[#BEBEBE] text-[16.85px] font-medium">
          Forgot password?
        </a>
    </CardFooter>
  </Card>
</template>

<style scoped>
.card-login-form {
  border: 1.203px solid #E4E4E7;
  background: linear-gradient(209deg, rgba(247, 255, 175, 0.38) 10.42%, rgba(244, 162, 255, 0.65) 92.04%);
  box-shadow: 0px 4px 4px 0px rgba(0, 0, 0, 0.25);
  backdrop-filter: blur(7.6px);
}

.dark .card-login-form {
  border: 1.203px solid #E4E4E7;
  background: linear-gradient(209deg, rgba(78, 205, 196, 0.38) 10.42%, rgba(244, 162, 255, 0.65) 92.04%);
  box-shadow: 0px 4px 4px 0px rgba(0, 0, 0, 0.25);
  backdrop-filter: blur(7.6px);
}

.separator-label{
  color:#f493bc;
  font-size: 1.125rem;
  background-color: transparent;
}
</style>