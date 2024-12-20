<script setup lang="ts">
import {ref} from 'vue'
import {Check, ChevronsUpDown} from 'lucide-vue-next'

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
    value: "gemini",
    label: "Gemini",
    description: "Modelo de IA de Google, optimizado para respuestas rápidas y precisas con excelente comprensión del contexto.",
  },
  {
    value: "ollama",
    label: "Ollama",
    description: "Modelo local basado en Llama, eficiente para la mayoría de tareas.",
  }
]

const props = defineProps<{
  modelValue: string
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: string): void
  (e: 'change', model: AIModel): void
}>()

const open = ref(false)
const selectedModel = ref(aiModels.find(model => model.value === props.modelValue) || aiModels[0])
const providerStore = useAIProviderStore();
const chatStore = useChatStore();
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

const isModelOverloaded = (model: AIModel) => {
  return model.value === 'gemini' && chatStore.lastError?.includes('sobrecargado');
};
</script>

<template>
  <Card class="w-full max-w-2xl mx-auto">
    <CardHeader>
      <CardTitle>Selector de Modelo IA</CardTitle>
      <CardDescription>Elige el modelo de IA para tu tarea</CardDescription>
    </CardHeader>
    <CardContent class="space-y-4">
      <Popover v-model:open="open">
        <PopoverTrigger as-child>
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
                <span v-if="isModelOverloaded(model)" class="text-sm text-yellow-500 ml-2">
      (Temporalmente Sobrecargado)
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
    </CardContent>
  </Card>
</template>