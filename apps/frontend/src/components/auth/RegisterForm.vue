<script setup lang="ts">
import { ref, computed } from 'vue'
import { useForm } from 'vee-validate'
import { Loader2 } from 'lucide-vue-next'
import { countries } from 'countries-list'
import GoogleIcon from "~/components/icons/GoogleIcon.vue";
import GithubIcon from "~/components/icons/GithubIcon.vue";
import { type RegisterSchemaType, stepSchemas } from "~/schemas/register.schema";

interface Props {
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  loading: false
})

const countryOptions = computed(() =>
    Object.entries(countries).map(([code, country]) => ({
      value: code,
      label: country.name,
      flag: `https://flagcdn.com/w20/${code.toLowerCase()}.png`
    }))
)

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

</script>

<template>
  <div class="min-h-screen flex items-center justify-center">
    <Card class="w-[450px] p-[29px] gap-[15px] rounded-[14.5px] card-register-form dark:card-register-form flex flex-col flex-grow">
      <CardHeader class="self-stretch flex-col items-center justify-start flex p-0">
        <CardTitle class="grow shrink basis-0 text-[28.88px] font-semibold text-[#181b32] dark:text-[#E1E0E0] text-center">
          {{ steps[currentStep - 1].title }}
        </CardTitle>
        <CardDescription class="grow shrink basis-0 inline-flex text-[16.85px] text-[#5e4a6e] dark:text-[#BEBEBE] text-center">
          Step {{ currentStep }} of {{ steps.length }}
        </CardDescription>
      </CardHeader>
      <CardContent class="self-stretch flex-col items-start justify-start flex gap-[19.26px] p-0">
        <div v-if="currentStep === 1" class="self-stretch flex gap-6">
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
        <div v-if="currentStep === 1" class="self-stretch justify-start items-center inline-flex">
          <Separator class="grow shrink basis-0 h-[1.20px] bg-primary dark:bg-[#F9CF87]"/>
          <div class="px-[9.63px] flex-col justify-start items-start gap-3 inline-flex">
            <div class="self-stretch justify-center items-center gap-3 inline-flex">
              <div class="text-[#5e4a6e] dark:text-[#BEBEBE] text-sm font-normal">O CONTINUA CON</div>
            </div>
          </div>
          <Separator class="grow shrink basis-0 h-[1.20px] bg-primary dark:bg-[#F9CF87]"/>
        </div>
        <form class="space-y-4 text-gray-900 w-full" @submit.prevent="onSubmit">
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
                          <img :src="option.flag" :alt="option.label" class="w-5 h-3 mr-2 inline-block" >
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
              class="w-full text-white text-[16.85px] mt-[10px]"
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
          <NuxtLink to="/auth/login">
            <Button variant="link" class="text-[#5e4a6e] dark:text-[#BEBEBE] text-sm font-medium p-0">
              Ya tienes cuenta? Inicia sesión
            </Button>
          </NuxtLink>
        </div>
      </CardFooter>
    </Card>
  </div>
</template>

<style scoped>
.card-register-form {
  border: 1.203px solid #E4E4E7;
  background: linear-gradient(209deg, rgba(247, 255, 175, 0.38) 10.42%, rgba(244, 162, 255, 0.65) 92.04%);
  box-shadow: 0px 4px 4px 0px rgba(0, 0, 0, 0.25);
  backdrop-filter: blur(7.6px);
}

.dark .card-register-form {
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