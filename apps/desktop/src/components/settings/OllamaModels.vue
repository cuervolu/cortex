<script setup lang="ts">
import { ref, computed } from 'vue'
import { AlertCircle, Download, CheckCircle2, ChevronLeft, ChevronRight } from 'lucide-vue-next'
import { type OllamaModel, useOllamaModels } from '~/composables/useOllamaModels'

const { models, isLoading, error, fetchOllamaModels } = useOllamaModels()
const downloadedModels = ref<string[]>([])

// Pagination
const currentPage = ref(1)
const itemsPerPage = 6
const totalPages = computed(() => Math.ceil(sortedModels.value.length / itemsPerPage))

const isCompatible = (model: OllamaModel): boolean => {
  return true // For now, assume all models are compatible
}

const isDownloaded = (modelId: string): boolean => {
  return downloadedModels.value.includes(modelId)
}

const handleModelDownload = (modelId: string): void => {
  // This would typically initiate the actual download process
  downloadedModels.value.push(modelId)
}

const handleRefresh = async () => {
  await fetchOllamaModels()
  currentPage.value = 1 // Reset to first page after refresh
}

const sortedModels = computed(() => {
  return [...models.value].sort((a, b) => a.name.localeCompare(b.name))
})

const paginatedModels = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return sortedModels.value.slice(start, end)
})

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}
</script>

<template>
  <div class="space-y-4">
    <div class="flex justify-between items-center mb-4">
      <h2 class="text-xl font-semibold">Ollama Models</h2>
      <Button :disabled="isLoading" @click="handleRefresh">
        Refresh Models
      </Button>
    </div>

    <div v-if="isLoading" class="grid gap-4 md:grid-cols-2">
      <Card v-for="i in itemsPerPage" :key="i">
        <CardHeader>
          <CardTitle>
            <Skeleton class="h-6 w-3/4" />
          </CardTitle>
          <CardDescription>
            <Skeleton class="h-4 w-full mt-2" />
            <Skeleton class="h-4 w-5/6 mt-1" />
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div class="flex gap-2 mb-2">
            <Skeleton class="h-6 w-16" />
            <Skeleton class="h-6 w-16" />
          </div>
          <Skeleton class="h-10 w-full" />
        </CardContent>
      </Card>
    </div>

    <div v-else-if="error" class="text-center text-red-500">
      <AlertCircle class="h-8 w-8 mx-auto mb-2" />
      <p>{{ error }}</p>
    </div>

    <div v-else>
      <div class="grid gap-4 md:grid-cols-2 mb-4">
        <Card
            v-for="model in paginatedModels"
            :key="model.name"
            :class="{ 'opacity-50': !isCompatible(model) }"
        >
          <CardHeader>
            <CardTitle class="flex justify-between items-center">
              <span>{{ model.name }}</span>
              <Badge :variant="isCompatible(model) ? 'default' : 'destructive'">
                {{ isCompatible(model) ? 'Compatible' : 'Not Compatible' }}
              </Badge>
            </CardTitle>
            <CardDescription>{{ model.description }}</CardDescription>
          </CardHeader>
          <CardContent>
            <div class="flex justify-between items-center mb-2">
              <div class="flex gap-2">
                <Badge
                    v-for="(tag, index) in model.tags"
                    :key="index"
                    variant="secondary"
                >
                  {{ tag }}
                </Badge>
              </div>
            </div>
            <Button
                :disabled="!isCompatible(model) || isDownloaded(model.name)"
                class="w-full"
                @click="handleModelDownload(model.name)"
            >
              <template v-if="isDownloaded(model.name)">
                <CheckCircle2 class="mr-2 h-4 w-4" />
                Downloaded
              </template>
              <template v-else>
                <Download class="mr-2 h-4 w-4" />
                Download Model
              </template>
            </Button>
          </CardContent>
        </Card>
      </div>

      <div class="flex items-center justify-between">
        <Button
            variant="outline"
            size="sm"
            :disabled="currentPage === 1"
            @click="prevPage"
        >
          <ChevronLeft class="h-4 w-4 mr-2" />
          Previous
        </Button>
        <span>Page {{ currentPage }} of {{ totalPages }}</span>
        <Button
            variant="outline"
            size="sm"
            :disabled="currentPage === totalPages"
            @click="nextPage"
        >
          Next
          <ChevronRight class="h-4 w-4 ml-2" />
        </Button>
      </div>
    </div>

    <div class="flex items-center space-x-2 text-yellow-600">
      <AlertCircle class="h-4 w-4" />
      <span class="text-sm">
        Note: Ollama models depend on your system's memory and GPU capabilities.
      </span>
    </div>
  </div>
</template>