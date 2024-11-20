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
    title: 'Información de cuenta',
    fields: ['email', 'password', 'username']
  },
  {
    title: 'Información personal',
    fields: ['firstname', 'lastname', 'date_of_birth']
  },
  {
    title: 'Detalles adicionales',
    fields: ['country_code', 'gender']
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
    <Card class="w-full max-w-md bg-white bg-opacity-20 backdrop-blur-lg rounded-xl shadow-lg">
      <CardHeader>
        <CardTitle class="text-3xl font-bold text-purple-900 text-center">
          {{ steps[currentStep - 1].title }}
        </CardTitle>
        <CardDescription class="text-lg text-purple-700 text-center">
          Paso {{ currentStep }} De {{ steps.length }}
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
            O continua con
          </span>
        </div>
        <form class="space-y-4 text-gray-900" @submit.prevent="onSubmit">
          <template v-for="field in currentStepFields" :key="field">
            <FormField v-slot="{ componentField }" :name="field">
              <FormItem>
                <FormLabel>{{ field.charAt(0).toUpperCase() + field.slice(1) }}</FormLabel>
                <FormControl>
                  <template v-if="field === 'date_of_birth'">
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
                  <template v-else-if="field === 'country_code'">
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
            Anterior
          </Button>
          <p v-if="currentStep === steps.length" class="text-sm text-gray-600">
            Ya tienes una cuenta?
            <NuxtLink to="/auth/login" class="text-purple-700 hover:underline">
              Ingresar
            </NuxtLink>
          </p>
        </div>
      </CardFooter>
    </Card>
  </div>
</template>