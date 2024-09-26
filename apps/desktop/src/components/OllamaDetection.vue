<script lang="ts" setup>
import { useOllamaInteraction, useOllamaDetection } from "~/composables";
import { ref, watch } from 'vue';
import { parseMarkdown } from '@nuxtjs/mdc/runtime';
import type { MDCParserResult } from '@nuxtjs/mdc';
import { AlertCircle, Loader2, Send } from 'lucide-vue-next';

const {
  isOllamaInstalled,
  isChecking,
  checkError,
  checkOllamaInstallation
} = useOllamaDetection();

const {
  prompt,
  response,
  isSending,
  promptError,
  sendPrompt
} = useOllamaInteraction();

const parsedResponse = ref<MDCParserResult | null>(null);
const hasCheckedInstallation = ref(false);

watch(response, async (newResponse) => {
  if (newResponse) {
    parsedResponse.value = await parseMarkdown(newResponse);
  } else {
    parsedResponse.value = null;
  }
});

const handleCheckInstallation = async () => {
  await checkOllamaInstallation();
  hasCheckedInstallation.value = true;
};
</script>

<template>
  <div class="min-h-screen w-full flex items-center justify-center bg-muted/40 p-4">
    <Card class="w-full max-w-[90vw] md:max-w-[80vw] lg:max-w-[70vw] xl:max-w-[60vw] 2xl:max-w-[50vw] max-h-[90vh] flex flex-col">
      <CardHeader>
        <CardTitle>Ollama Chat Interface</CardTitle>
        <CardDescription>Interact with Ollama AI model</CardDescription>
      </CardHeader>
      <CardContent class="space-y-4 flex-grow overflow-y-auto">
        <Alert v-if="!isOllamaInstalled && !isChecking" variant="destructive">
          <AlertCircle class="h-4 w-4"/>
          <AlertTitle>Installation Required</AlertTitle>
          <AlertDescription>
            Ollama is not installed. Please install it to continue.
          </AlertDescription>
        </Alert>
        <Alert v-if="checkError" variant="destructive">
          <AlertCircle class="h-4 w-4"/>
          <AlertTitle>Error</AlertTitle>
          <AlertDescription>{{ checkError }}</AlertDescription>
        </Alert>
        <Button
            v-if="!isOllamaInstalled && !hasCheckedInstallation"
            :disabled="isChecking"
            class="w-full"
            @click="handleCheckInstallation"
        >
          <Loader2 v-if="isChecking" class="mr-2 h-4 w-4 animate-spin"/>
          {{ isChecking ? 'Checking...' : 'Check Ollama Installation' }}
        </Button>
        <div v-if="isOllamaInstalled" class="space-y-2">
          <Textarea
              v-model="prompt"
              placeholder="Ask Ollama something..."
              class="min-h-[100px] resize-none"
              @keyup.enter="sendPrompt"
          />
          <Button
              :disabled="!prompt || isSending"
              class="w-full"
              @click="sendPrompt"
          >
            <Send v-if="!isSending" class="mr-2 h-4 w-4"/>
            <Loader2 v-else class="mr-2 h-4 w-4 animate-spin"/>
            {{ isSending ? 'Sending...' : 'Send to Ollama' }}
          </Button>
        </div>
        <Alert v-if="promptError" variant="destructive">
          <AlertCircle class="h-4 w-4"/>
          <AlertTitle>Error</AlertTitle>
          <AlertDescription>{{ promptError }}</AlertDescription>
        </Alert>
        <div v-if="parsedResponse" class="mt-4">
          <h3 class="text-lg font-semibold mb-2">Ollama's Response</h3>
          <div class="bg-muted p-4 rounded-md overflow-x-auto">
            <MDC
                v-if="parsedResponse"
                :value="parsedResponse"
            />
          </div>
        </div>
      </CardContent>
    </Card>
  </div>
</template>


<style scoped>
.overflow-y-auto {
  scrollbar-width: thin;
  scrollbar-color: rgba(155, 155, 155, 0.5) transparent;
}

.overflow-y-auto::-webkit-scrollbar {
  width: 6px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: transparent;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background-color: rgba(155, 155, 155, 0.5);
  border-radius: 20px;
  border: transparent;
}
</style>