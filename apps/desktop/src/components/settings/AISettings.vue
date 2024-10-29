<script setup lang="ts">
import {debug} from "@tauri-apps/plugin-log";
import {CheckCircle2, Loader2, Lock, Trash2, Eye, EyeOff} from 'lucide-vue-next'
import {useAIProviderStore} from '~/stores/ai-provider.store'
import {useChatStore} from '~/stores/'

const apiKey = ref('')
const showApiKey = ref(false)
const currentProvider = ref('claude')

const keystore = useKeystore()
const providerStore = useAIProviderStore()
const chatStore = useChatStore()
const {createAppError} = useErrorHandler()

const isLoading = ref(true)
const isSaving = ref(false)
const isRemoving = ref(false)

const validateApiKey = (provider: string, key: string): boolean => {
  if (!key.trim()) {
    throw createAppError('API key cannot be empty', {
      statusCode: 400,
      data: { provider }
    })
  }

  if (provider === 'claude' && !key.startsWith('sk-')) {
    throw createAppError('Invalid API key format. Claude API keys should start with "sk-"', {
      statusCode: 400,
      data: { provider }
    })
  }

  return true
}

const loadCurrentApiKey = async () => {
  try {
    isLoading.value = true
    const key = await keystore.getApiKey(currentProvider.value)
    if (key) {
      apiKey.value = key
      await providerStore.setProviderConfigured(currentProvider.value, true)
    }
  } catch (error) {
    await debug(`Error loading API key: ${error}`)
    await debug(`No existing API key found for ${currentProvider.value}`)
  } finally {
    isLoading.value = false
  }
}

const handleProviderChange = async (providerName: string) => {
  try {
    currentProvider.value = providerName
    apiKey.value = ''
    showApiKey.value = false
    isLoading.value = true

    const provider = providerStore.getProvider(providerName)
    if (!provider) {
      throw createAppError(`Provider ${providerName} not found`, {
        statusCode: 404,
        data: { providerName }
      })
    }

    if (!provider.requiresApiKey) {
      isSaving.value = true
      await chatStore.setProvider(providerName)
      provider.isConfigured = true
    } else {
      await loadCurrentApiKey()
      await chatStore.setProvider(providerName)
    }
  } finally {
    isLoading.value = false
    isSaving.value = false
  }
}

const saveApiKey = async () => {
  const provider = providerStore.getProvider(currentProvider.value)
  if (!provider) return

  validateApiKey(currentProvider.value, apiKey.value)

  isSaving.value = true
  try {
    await chatStore.setProvider(currentProvider.value)
    await keystore.setApiKey(currentProvider.value, apiKey.value)
    await providerStore.setProviderConfigured(currentProvider.value, true)
  } finally {
    isSaving.value = false
  }
}

const removeApiKey = async () => {
  const provider = providerStore.getProvider(currentProvider.value)
  if (!provider) return

  isRemoving.value = true
  try {
    await keystore.removeApiKey()
    await providerStore.setProviderConfigured(currentProvider.value, false)
    apiKey.value = ''
  } finally {
    isRemoving.value = false
  }
}

const handleKeyDown = (event: KeyboardEvent) => {
  if (event.key === 'Enter' && !isSaving.value && !isRemoving.value) {
    saveApiKey()
  }
}

const currentProviderDetails = computed(() => {
  const provider = providerStore.getProvider(currentProvider.value)
  return {
    requiresApiKey: provider?.requiresApiKey || false,
    isConfigured: provider?.isConfigured || false,
    name: provider?.name || ''
  }
})

const isProviderConfigured = computed(() => currentProviderDetails.value.isConfigured)
const currentProviderName = computed(() => currentProviderDetails.value.name)

onMounted(async () => {
  const provider = providerStore.getProvider(currentProvider.value)
  if (provider) {
    await providerStore.checkProviderConfiguration(currentProvider.value)
    if (provider.requiresApiKey) {
      await loadCurrentApiKey()
    }
  }
})
</script>

<template>
  <div class="w-full max-w-4xl mx-auto p-6">
    <Card class="border-primary/20">
      <CardHeader class="space-y-2">
        <CardTitle
            class="text-2xl font-bold bg-gradient-to-r from-primary to-primary-foreground bg-clip-text text-transparent">
          AI Configuration
        </CardTitle>
        <CardDescription class="text-base">
          Select and configure your preferred AI assistant
        </CardDescription>
      </CardHeader>

      <CardContent class="p-6">
        <Tabs :default-value="currentProvider" class="w-full">
          <TabsList class="grid w-full grid-cols-2 mb-8">
            <TabsTrigger
                v-for="(provider, name) in providerStore.providers"
                :key="name"
                :value="name"
                class="data-[state=active]:bg-primary data-[state=active]:text-primary-foreground"
                @click="handleProviderChange(name)"
            >
              <div class="flex items-center space-x-2">
                <span class="font-medium">{{ provider.name }}</span>
                <Badge
                    v-if="provider.isConfigured"
                    variant="secondary"
                    class="bg-success/20 text-success"
                >
                  Active
                </Badge>
              </div>
            </TabsTrigger>
          </TabsList>

          <TabsContent
              v-for="(provider, name) in providerStore.providers"
              :key="name"
              :value="name"
              class="border rounded-lg p-6 shadow-sm"
          >
            <div v-if="isLoading" class="flex justify-center p-4">
              <Loader2 class="h-6 w-6 animate-spin"/>
            </div>
            <div v-else class="space-y-6">
              <template v-if="currentProviderDetails.requiresApiKey">
                <div class="space-y-4">
                  <div class="space-y-2">
                    <Label :for="`${name}-api-key`" class="text-lg font-medium">
                      {{ currentProviderName }} API Key
                    </Label>
                    <div class="relative">
                      <Input
                          :id="`${name}-api-key`"
                          v-model="apiKey"
                          :type="showApiKey ? 'text' : 'password'"
                          class="font-mono pr-10"
                          :placeholder="`Enter your ${currentProviderName} API key`"
                          :disabled="isSaving || isRemoving"
                          @keydown="handleKeyDown"
                      />
                      <Button
                          variant="ghost"
                          size="icon"
                          class="absolute right-0 top-0 h-full px-3 hover:bg-transparent"
                          @click="showApiKey = !showApiKey"
                      >
                        <component :is="showApiKey ? EyeOff : Eye" class="h-4 w-4"/>
                      </Button>
                    </div>
                    <p class="text-sm text-muted-foreground flex items-center">
                      <Lock class="w-4 h-4 mr-2"/>
                      Your API key will be securely encrypted and stored locally
                    </p>
                  </div>

                  <div class="flex space-x-4">
                    <Button
                        class="min-w-[120px]"
                        :disabled="isSaving || isRemoving || !apiKey.trim()"
                        @click="saveApiKey"
                    >
                      <Loader2
                          v-if="isSaving"
                          class="mr-2 h-4 w-4 animate-spin"
                      />
                      {{ isSaving ? 'Saving...' : 'Save Key' }}
                    </Button>

                    <Button
                        variant="destructive"
                        class="min-w-[120px]"
                        :disabled="!isProviderConfigured || isSaving || isRemoving"
                        @click="removeApiKey"
                    >
                      <template v-if="!isRemoving">
                        <Trash2 class="mr-2 h-4 w-4"/>
                        Remove
                      </template>
                      <template v-else>
                        <Loader2 class="mr-2 h-4 w-4 animate-spin"/>
                        Removing...
                      </template>
                    </Button>
                  </div>
                </div>
              </template>

              <template v-else>
                <Alert class="bg-success/10 border-success/20">
                  <CheckCircle2 class="h-5 w-5 text-success"/>
                  <AlertTitle class="text-success font-medium">
                    Ready to use
                  </AlertTitle>
                  <AlertDescription class="text-success/80">
                    {{ currentProviderName }} is configured and ready to assist you
                  </AlertDescription>
                </Alert>
              </template>
            </div>
          </TabsContent>
        </Tabs>
      </CardContent>
    </Card>
  </div>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}

:deep(.tabs-trigger[data-state="active"]) {
  background: linear-gradient(to right, var(--primary), var(--primary-foreground));
}
</style>