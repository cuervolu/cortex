<script setup lang="ts">
import type {NuxtError} from '#app'
import {Home} from 'lucide-vue-next'

const props = defineProps({
  error: {
    type: Object as () => NuxtError,
    required: true
  }
})

const route = useRoute()
const isModalOpen = ref(false)

const additionalInfo = computed(() => {
  return {
    name: route.query.name || props.error.name || 'Unknown Error',
    statusCode: route.query.statusCode || props.error.statusCode || 500,
    message: route.query.message || props.error.message || 'An unexpected error occurred',
    stack: route.query.stack || props.error.stack || 'No stack trace available',
    fatal: route.query.fatal === 'true'
  }
})

const handleError = () => {
  clearError({redirect: '/'})
}
</script>

<template>
  <div class="min-h-screen bg-yellow-100 flex items-center justify-center p-4">
    <div class="max-w-md w-full text-center">
      <!-- Emoji Circle Animation -->
      <div class="mb-8">
        <div class="w-32 h-32 mx-auto bg-primary rounded-full flex items-center justify-center">
          <div class="w-24 h-24 bg-white rounded-full flex items-center justify-center">
            <div class="text-6xl animate-bounce">🙃</div>
          </div>
        </div>
      </div>

      <!-- Main Error Message -->
      <h1 class="text-4xl font-bold text-primary mb-4">
        Oops! Something went wrong
      </h1>
      <p class="text-xl text-gray-700 mb-8">
        Don't worry! Even computers make mistakes sometimes. Let's try going back to the home page.
      </p>

      <!-- Action Button -->
      <Button
          class="bg-green-500 hover:bg-green-600 text-white font-bold py-3 px-6 rounded-full text-lg transition-colors duration-200"
          @click="handleError"
      >
        <Home class="mr-2 h-5 w-5"/>
        Go Back Home
      </Button>

      <!-- Developer Details Dialog -->
      <div class="mt-8">
        <Dialog v-model:open="isModalOpen">
          <DialogTrigger as-child>
            <Button
                variant="link"
                class="text-sm text-gray-500 hover:text-gray-700"
            >
              Details (for developers)
            </Button>
          </DialogTrigger>

          <DialogContent>
            <DialogHeader>
              <DialogTitle>Error Details</DialogTitle>
              <DialogDescription>
                Technical information about the error:
              </DialogDescription>
            </DialogHeader>

            <div class="mt-4 text-left">
              <!-- Error Type -->
              <div class="mb-4">
                <h3 class="font-semibold text-primary">Error Type:</h3>
                <p class="text-sm">
                  {{ additionalInfo.name }}
                  <Badge
                      v-if="additionalInfo.fatal"
                      variant="destructive"
                      class="ml-2"
                  >
                    Fatal
                  </Badge>
                </p>
              </div>

              <!-- Status Code -->
              <div class="mb-4">
                <h3 class="font-semibold text-primary">Status Code:</h3>
                <p class="text-sm">{{ additionalInfo.statusCode }}</p>
              </div>

              <!-- Error Message -->
              <div class="mb-4">
                <h3 class="font-semibold text-primary">Error Message:</h3>
                <p class="text-sm">{{ additionalInfo.message }}</p>
              </div>

              <!-- Stack Trace -->
              <div>
                <h3 class="font-semibold text-primary">Stack Trace:</h3>
                <ScrollArea class="h-[200px] w-full rounded-md border p-4">
                  <pre class="text-xs font-mono">{{ additionalInfo.stack }}</pre>
                </ScrollArea>
              </div>
            </div>
          </DialogContent>
        </Dialog>
      </div>
    </div>
  </div>
</template>

<style scoped>
.animate-bounce {
  animation: bounce 1s infinite;
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(-25%);
    animation-timing-function: cubic-bezier(0.8, 0, 1, 1);
  }
  50% {
    transform: translateY(0);
    animation-timing-function: cubic-bezier(0, 0, 0.2, 1);
  }
}
</style>