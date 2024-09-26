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

watch(response, async (newResponse) => {
  if (newResponse) {
    parsedResponse.value = await parseMarkdown(newResponse);
  } else {
    parsedResponse.value = null;
  }
});
</script>

<template>
  <div class="h-screen flex items-center justify-center bg-muted/40 p-4">
    <Card class="w-full max-w-2xl">
      <CardHeader>
        <CardTitle>Ollama Chat Interface</CardTitle>
        <CardDescription>Interact with Ollama AI model</CardDescription>
      </CardHeader>
      <CardContent class="space-y-4">
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
            :disabled="isChecking"
            class="w-full"
            @click="checkOllamaInstallation"
        >
          <Loader2 v-if="isChecking" class="mr-2 h-4 w-4 animate-spin"/>
          {{ isChecking ? 'Checking...' : 'Check Ollama Installation' }}
        </Button>
        <div v-if="isOllamaInstalled" class="space-y-2">
          <Textarea
              v-model="prompt"
              placeholder="Ask Ollama something..."
              class="min-h-[100px]"
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
        <Card v-if="parsedResponse" class="mt-4">
          <CardHeader>
            <CardTitle>Ollama's Response</CardTitle>
          </CardHeader>
          <CardContent>
            <MDCRenderer
                v-if="parsedResponse.body && parsedResponse.data"
                :body="parsedResponse.body"
                :data="parsedResponse.data"
            />
          </CardContent>
        </Card>
      </CardContent>
    </Card>
  </div>
</template>