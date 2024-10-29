<script setup lang="ts">
import {debug, error as logError} from "@tauri-apps/plugin-log";
import {AlertCircle, CheckCircle2, Loader2, Lock, Trash2} from 'lucide-vue-next'
import {useAIProviderStore} from '~/stores/ai-provider.store'
import {useChatStore} from '~/stores/'

interface Notification {
  show: boolean
  message: string
  type: 'error' | 'success'
}

const apiKey = ref('')
const currentProvider = ref('claude')
const notification = ref<Notification>({
  show: false,
  message: '',
  type: 'success'
})

const {data: authData} = useAuth();
const keystore = useKeystore();
const providerStore = useAIProviderStore()
const chatStore = useChatStore()
const isSaving = ref(false)
const isRemoving = ref(false)

const showNotification = (message: string, type: 'success' | 'error') => {
  notification.value = {
    show: true,
    message,
    type
  }

  setTimeout(() => {
    if (notification.value.message === message) {
      notification.value.show = false
    }
  }, 5000)
}

const validateApiKey = (provider: string, key: string): boolean => {
  if (!key.trim()) {
    showNotification('API key cannot be empty', 'error')
    return false
  }

  switch (provider) {
    case 'claude':
      if (!key.startsWith('sk-')) {
        showNotification('Invalid API key format. Claude API keys should start with "sk-"', 'error')
        return false
      }
      break
  }

  return true
}

const handleProviderChange = async (providerName: string) => {
  try {
    currentProvider.value = providerName
    const provider = providerStore.getProvider(providerName)

    if (!provider) return
    
    if (!provider.requiresApiKey) {
      isSaving.value = true
      await chatStore.setProvider(providerName)
      provider.isConfigured = true
      showNotification(`${provider.name} configured successfully`, 'success')
    } else {
      await chatStore.setProvider(providerName)
    }
  } catch (error) {
    showNotification(
        error instanceof Error ? error.message : `Failed to configure provider`,
        'error'
    )
  } finally {
    isSaving.value = false
  }
}

const saveApiKey = async () => {
  const provider = providerStore.getProvider(currentProvider.value);
  if (!provider) return;

  if (!validateApiKey(currentProvider.value, apiKey.value)) return;

  isSaving.value = true;
  try {
    // Primero configurar el proveedor
    await chatStore.setProvider(currentProvider.value);

    // Luego guardar la API key
    await keystore.setApiKey(currentProvider.value, apiKey.value);
    providerStore.setProviderConfigured(currentProvider.value, true);
    apiKey.value = '';
    showNotification('API key saved successfully', 'success');
  } catch (error) {
    providerStore.setProviderConfigured(currentProvider.value, false);
    showNotification('Failed to save API key', 'error');
  } finally {
    isSaving.value = false;
  }
};

const removeApiKey = async () => {
  const provider = providerStore.getProvider(currentProvider.value);
  if (!provider) return;

  isRemoving.value = true;
  try {
    await keystore.removeApiKey();
    providerStore.setProviderConfigured(currentProvider.value, false);
    showNotification(`${provider.name} removed successfully`, 'success');
  } catch (error) {
    showNotification('Failed to remove configuration', 'error');
  } finally {
    isRemoving.value = false;
  }
};

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
  const provider = providerStore.getProvider(currentProvider.value);
  if (provider) {
    await providerStore.checkProviderConfiguration(currentProvider.value);
  }
});
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
            <div class="space-y-6">
              <!-- Usar currentProviderDetails para determinar qué mostrar -->
              <template v-if="currentProviderDetails.requiresApiKey">
                <div class="space-y-4">
                  <!-- Input de API Key -->
                  <div class="space-y-2">
                    <Label :for="`${name}-api-key`" class="text-lg font-medium">
                      {{ currentProviderName }} API Key
                    </Label>
                    <Input
                        :id="`${name}-api-key`"
                        v-model="apiKey"
                        type="password"
                        class="font-mono"
                        :placeholder="`Enter your ${currentProviderName} API key`"
                        :disabled="isSaving || isRemoving"
                        @keydown="handleKeyDown"
                    />
                    <p class="text-sm text-muted-foreground flex items-center">
                      <Lock class="w-4 h-4 mr-2"/>
                      Your API key will be securely encrypted and stored locally
                    </p>
                  </div>

                  <!-- Botones -->
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

              <!-- Provider sin API Key -->
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

      <CardFooter>
        <TransitionGroup name="fade">
          <Alert
              v-if="notification.show"
              :variant="notification.type === 'error' ? 'destructive' : 'default'"
              class="w-full"
          >
            <component
                :is="notification.type === 'error' ? AlertCircle : CheckCircle2"
                class="h-5 w-5"
            />
            <AlertTitle class="font-medium">
              {{ notification.type === 'error' ? 'Error' : 'Success' }}
            </AlertTitle>
            <AlertDescription>{{ notification.message }}</AlertDescription>
          </Alert>
        </TransitionGroup>
      </CardFooter>
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