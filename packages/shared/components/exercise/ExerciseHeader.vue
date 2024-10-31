<script setup lang="ts">
import { ref, computed } from 'vue'
import { Settings, ChevronLeft, Check, ChevronsUpDown } from "lucide-vue-next"
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from '@/components/ui/sheet'

interface Props {
  lesson: string;
  exerciseName: string;
  fileName: string;
  onBackClick: () => void;
  onSettingsClick: () => void;
  modelValue: string;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'change', model: AIModel): void
}>()

interface AIModel {
  value: string
  label: string
  description: string
}

const aiModels = [
  {
    value: "claude",
    label: "Claude",
    description: "Modelo avanzado de Anthropic con excelentes capacidades de razonamiento y comprensión contextual.",
  },
  {
    value: "ollama",
    label: "Ollama",
    description: "Modelo local basado en Llama, eficiente para la mayoría de tareas.",
  }
]

const open = ref(false)
const selectedModel = ref(aiModels.find(model => model.value === props.modelValue) || aiModels[0])
const providerStore = useAIProviderStore();

const handleSelect = (value: string) => {
  const model = aiModels.find(m => m.value === value)
  if (model) {
    selectedModel.value = model
    open.value = false
    emit('update:modelValue', model.value)
    emit('change', model)
  }
}

const searchQuery = ref('')
const filteredModels = computed(() => {
  if (!searchQuery.value) return aiModels
  const query = searchQuery.value.toLowerCase()
  return aiModels.filter(model =>
    model.label.toLowerCase().includes(query) ||
    model.description.toLowerCase().includes(query)
  )
})

const isModelDisabled = (model: AIModel) => {
  const provider = providerStore.getProvider(model.value);
  return provider?.requiresApiKey && !provider?.isConfigured;
};
</script>

<template>
  <div class="exercise-header">
    <div
      class="flex items-start justify-between p-2 sm:p-4 bg-muted/50 border-b-2 border-gradient-purple"
    >
      <div
        class="flex items-center gap-1 sm:gap-2 cursor-pointer"
        @click="props.onBackClick"
      >
        <ChevronLeft class="w-4 h-4 sm:w-5 sm:h-5"/>
        <div class="text-xs sm:text-sm text-foreground truncate max-w-[80px] sm:max-w-none">
          Back to Exercise
        </div>
      </div>
      <div class="flex items-center gap-1 sm:gap-2">
        <p class="text-xs sm:text-sm truncate max-w-[120px] sm:max-w-none">
          <span class="text-muted-foreground">{{ props.lesson }}</span>
          <span class="text-muted-foreground"> /</span>
          <span class="text-foreground"> {{ props.exerciseName }}</span>
        </p>
      </div>
      <Sheet>
        <SheetTrigger asChild>
          <Button variant="ghost" size="icon">
            <Settings class="w-4 h-4"/>
          </Button>
        </SheetTrigger>
        <SheetContent>
          <SheetHeader>
            <SheetTitle>Selector de Modelo IA</SheetTitle>
          </SheetHeader>
          <div class="space-y-4 mt-4">
            <Popover v-model:open="open">
              <PopoverTrigger asChild>
                <Button
                  variant="outline"
                  role="combobox"
                  :aria-expanded="open"
                  class="w-full justify-between"
                >
                  {{ selectedModel.label }}
                  <ChevronsUpDown class="ml-2 h-4 w-4 shrink-0 opacity-50"/>
                </Button>
              </PopoverTrigger>

              <PopoverContent class="w-[--radix-popover-trigger-width] p-0">
                <Command>
                  <CommandInput
                    v-model="searchQuery"
                    placeholder="Buscar modelos de IA..."
                  />
                  <CommandEmpty>No se encontraron modelos.</CommandEmpty>
                  <CommandGroup>
                    <CommandItem
                      v-for="model in filteredModels"
                      :key="model.value"
                      :value="model.value"
                      :disabled="isModelDisabled(model)"
                      @select="handleSelect(model.value)"
                    >
                      <Check
                        class="mr-2 h-4 w-4"
                        :class="{
                          'opacity-100': selectedModel.value === model.value,
                          'opacity-0': selectedModel.value !== model.value
                        }"
                      />
                      {{ model.label }}
                      <span v-if="isModelDisabled(model)" class="text-sm text-muted-foreground ml-2">
                        (API Key Required)
                      </span>
                    </CommandItem>
                  </CommandGroup>
                </Command>
              </PopoverContent>
            </Popover>

            <div class="mt-4">
              <h3 class="text-lg font-semibold">
                Modelo Seleccionado: {{ selectedModel.label }}
              </h3>
              <p class="text-sm text-muted-foreground">
                {{ selectedModel.description }}
              </p>
            </div>
          </div>
        </SheetContent>
      </Sheet>
    </div>
    <!-- Tab -->
    <div
      class="inline-flex items-center px-2 sm:px-4 py-1 sm:py-1.5 rounded-br-2xl sm:rounded-br-3xl border-r-2 border-b-2 overflow-hidden bg-muted/50"
    >
      <div class="font-medium text-xs sm:text-sm text-foreground truncate max-w-[120px] sm:max-w-none">
        {{ props.fileName }}
      </div>
    </div>
  </div>
</template>

<style scoped>
.border-gradient-purple {
  border-image: linear-gradient(to bottom, rgb(56, 22, 83), rgb(135.66, 56.29, 198.69)) 1;
}
</style>