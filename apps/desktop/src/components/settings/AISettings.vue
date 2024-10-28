<script setup lang="ts">
import { ref } from 'vue';
import { AlertCircle, CheckCircle2, Loader2 } from 'lucide-vue-next';
import { useStronghold } from '~/composables/useStronghold';
import { useAnthropicStore } from '~/stores/anthropic.store';

interface Notification {
  show: boolean;
  message: string;
  type: 'error' | 'success';
}

const claudeKey = ref('');
const notification = ref<Notification>({
  show: false,
  message: '',
  type: 'success'
});

const stronghold = useStronghold();
const anthropicStore = useAnthropicStore();

const showNotification = (message: string, type: 'success' | 'error') => {
  notification.value = {
    show: true,
    message,
    type
  };

  setTimeout(() => {
    if (notification.value.message === message) {
      notification.value.show = false;
    }
  }, 5000);
};

const saveClaudeKey = async () => {
  if (!claudeKey.value.trim()) {
    showNotification('API key cannot be empty', 'error');
    return;
  }

  if (!claudeKey.value.startsWith('sk-')) {
    showNotification('Invalid API key format. Claude API keys should start with "sk-"', 'error');
    return;
  }

  try {
    await stronghold.saveApiKey(claudeKey.value);
    claudeKey.value = '';
    await anthropicStore.checkKeyStatus();
    showNotification('Claude API key saved successfully', 'success');
  } catch (error) {
    showNotification(
        error instanceof Error ? error.message : 'Failed to save API key',
        'error'
    );
  }
};

const removeClaudeKey = async () => {
  try {
    await stronghold.removeApiKey();
    await anthropicStore.checkKeyStatus();
    showNotification('Claude API key removed successfully', 'success');
  } catch (error) {
    showNotification(
        error instanceof Error ? error.message : 'Failed to remove API key',
        'error'
    );
  }
};

// Manejar Enter en el input
const handleKeyDown = (event: KeyboardEvent) => {
  if (event.key === 'Enter' && !anthropicStore.isLoading) {
    saveClaudeKey();
  }
};
</script>

<template>
  <div class="w-full max-w-2xl mx-auto p-4 space-y-4">
    <Card>
      <CardHeader>
        <CardTitle>AI Model Settings</CardTitle>
        <CardDescription>
          Configure your Claude-3 API key for AI assistance.
        </CardDescription>
      </CardHeader>

      <CardContent>
        <Tabs default-value="claude" class="w-full">
          <TabsList class="grid w-full grid-cols-1">
            <TabsTrigger value="claude">
              Claude-3
              <Badge
                  v-if="anthropicStore.isConfigured"
                  variant="secondary"
                  class="ml-2"
              >
                Configured
              </Badge>
            </TabsTrigger>
          </TabsList>

          <TabsContent value="claude">
            <div class="space-y-4">
              <div class="space-y-2">
                <Label for="claude-api-key">Claude API Key</Label>
                <div class="space-y-1">
                  <Input
                      id="claude-api-key"
                      v-model="claudeKey"
                      type="password"
                      placeholder="Enter your Claude API key (starts with sk-)"
                      :disabled="anthropicStore.isLoading"
                      @keydown="handleKeyDown"
                  />
                  <p class="text-xs text-muted-foreground">
                    Your API key will be securely stored in the system keychain.
                  </p>
                </div>
              </div>

              <div class="flex space-x-2">
                <Button
                    @click="saveClaudeKey"
                    :disabled="anthropicStore.isLoading"
                >
                  <Loader2
                      v-if="anthropicStore.isLoading"
                      class="mr-2 h-4 w-4 animate-spin"
                  />
                  {{ anthropicStore.isLoading ? 'Saving...' : 'Save Claude API Key' }}
                </Button>

                <Button
                    variant="destructive"
                    @click="removeClaudeKey"
                    :disabled="!anthropicStore.isConfigured || anthropicStore.isLoading"
                >
                  <Loader2
                      v-if="anthropicStore.isLoading"
                      class="mr-2 h-4 w-4 animate-spin"
                  />
                  Remove Claude API Key
                </Button>
              </div>
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
                class="h-4 w-4"
            />
            <AlertTitle>
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
  transition: all 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>