<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { AlertCircle, Download, ChevronLeft, ChevronRight } from 'lucide-vue-next'
import { useOllamaStore } from '~/stores'

const ollamaStore = useOllamaStore()
const { models, isLoading, error, isOllamaInstalled, pullProgress } = storeToRefs(ollamaStore)

const downloadingModels = ref<Record<string, boolean>>({})
const currentPage = ref(1)
const itemsPerPage = 6

const sortedModels = computed(() =>
    [...models.value].sort((a, b) => a.name.localeCompare(b.name))
)

const totalPages = computed(() =>
    Math.ceil(sortedModels.value.length / itemsPerPage)
)

const paginatedModels = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return sortedModels.value.slice(start, end)
})

const handleModelDownload = async (modelName: string) => {
  downloadingModels.value[modelName] = true
  try {
    await ollamaStore.pullModel(modelName)
  } catch (err) {
    console.error('Failed to download model:', err)
  } finally {
    downloadingModels.value[modelName] = false
  }
}

const handleRefresh = async () => {
  await ollamaStore.fetchOllamaModels()
  currentPage.value = 1
}

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

onMounted(async () => {
  await ollamaStore.checkOllamaInstallation()
  if (ollamaStore.isOllamaInstalled) {
    await ollamaStore.fetchOllamaModels()
  }
})

const getProgressValue = computed(() => {
  const progressString = pullProgress.value
  const match = progressString.match(/(\d+(?:\.\d+)?)%/)
  return match ? parseFloat(match[1]) : 0
})
</script>

<template>
  <div class="space-y-4">
    <div class="flex justify-between items-center mb-4">
      <h2 class="text-xl font-semibold">Ollama Models</h2>
      <Button :disabled="isLoading" @click="handleRefresh">
        Refresh Models
      </Button>
    </div>

    <div v-if="!isOllamaInstalled" class="text-center text-yellow-500">
      <AlertCircle class="h-8 w-8 mx-auto mb-2"/>
      <p>Ollama is not installed. Please install Ollama to use this feature.</p>
    </div>

    <div v-else-if="isLoading" class="grid gap-4 md:grid-cols-2">
      <Card v-for="i in itemsPerPage" :key="i">
        <CardHeader>
          <CardTitle>
            <Skeleton class="h-6 w-3/4"/>
          </CardTitle>
          <CardDescription>
            <Skeleton class="h-4 w-full mt-2"/>
            <Skeleton class="h-4 w-5/6 mt-1"/>
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div class="flex gap-2 mb-2">
            <Skeleton class="h-6 w-16"/>
            <Skeleton class="h-6 w-16"/>
          </div>
          <Skeleton class="h-10 w-full"/>
        </CardContent>
      </Card>
    </div>

    <div v-else-if="error" class="text-center text-red-500">
      <AlertCircle class="h-8 w-8 mx-auto mb-2"/>
      <p>{{ error }}</p>
    </div>

    <div v-else>
      <div class="grid gap-4 md:grid-cols-2 mb-4">
        <Card v-for="model in paginatedModels" :key="model.name">
          <CardHeader>
            <CardTitle>{{ model.name }}</CardTitle>
            <CardDescription>{{ model.description }}</CardDescription>
          </CardHeader>
          <CardContent>
            <div class="flex justify-between items-center mb-2">
              <div class="flex gap-2">
                <Badge v-for="(tag, index) in model.tags" :key="index" variant="secondary">
                  {{ tag }}
                </Badge>
              </div>
            </div>
            <div v-if="downloadingModels[model.name]" class="space-y-2">
              <Progress :model-value="getProgressValue" />
              <p class="text-sm text-center">{{ pullProgress }}</p>
            </div>
            <Button
                v-else
                class="w-full"
                @click="handleModelDownload(model.name)"
            >
              <Download class="mr-2 h-4 w-4"/>
              Download Model
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
          <ChevronLeft class="h-4 w-4 mr-2"/>
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
          <ChevronRight class="h-4 w-4 ml-2"/>
        </Button>
      </div>
    </div>

    <div class="flex items-center space-x-2 text-yellow-600">
      <AlertCircle class="h-4 w-4"/>
      <span class="text-sm">
        Note: Ollama models depend on your system's memory and GPU capabilities.
      </span>
    </div>
  </div>
</template>