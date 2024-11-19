<script setup lang="ts">

const colorMode = useColorMode()

const themes = [
  { value: 'light', label: 'Claro' },
  { value: 'dark', label: 'Oscuro' },
  { value: 'system', label: 'Sistema' }
]

const currentTab = ref('general')
const fontSize = ref(14)
const showLineNumbers = ref(false)
const wordWrap = ref(false)




</script>

<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between border-b pb-4">
      <h1 class="text-2xl font-bold text-foreground">Configuración</h1>
    </div>
    
    <Tabs v-model="currentTab" class="w-full">
      <TabsList class="grid w-full grid-cols-2 max-w-[400px]">
        <TabsTrigger value="general">General</TabsTrigger>
        <TabsTrigger value="editor">Editor</TabsTrigger>
      </TabsList>
    </Tabs>

    <!-- General Tab -->
    <div v-if="currentTab === 'general'" class="space-y-8">
      <!-- Theme Section -->
      <div class="space-y-4">
        <h2 class="text-lg font-semibold text-foreground">Tema</h2>
        <RadioGroup v-model="colorMode.preference" class="flex space-x-4">
          <div v-for="theme in themes" :key="theme.value" class="flex items-center space-x-2">
            <RadioGroupItem :id="theme.value" :value="theme.value" class="fill-foreground text-foreground ring-offset-foreground border-foreground" />
            <Label :for="theme.value">{{ theme.label }}</Label>
          </div>
        </RadioGroup>
      </div>
    </div>

    <!-- Editor Tab -->
    <div v-if="currentTab === 'editor'" class="space-y-8">
      <!-- Font Size Section -->
      <div class="space-y-4">
        <h2 class="text-lg font-semibold text-foreground">Tamaño de Fuente</h2>
        <div class="flex items-center gap-4 max-w-[300px]">
          <Slider 
            v-model="fontSize"
            class="flex-1"
            :min="8"
            :max="32"
            :step="1"
          />
          <span class="text-sm text-foreground min-w-[45px]">{{ fontSize }}px</span>
        </div>
      </div>

      <!-- Editor Options -->
      <div class="space-y-6">
        <div class="flex items-center justify-between max-w-[300px]">
          <Label for="line-numbers" class="text-foreground0">Mostrar números de línea</Label>
          <Switch v-model="showLineNumbers" id="line-numbers" />
        </div>
        
        <div class="flex items-center justify-between max-w-[300px]">
          <Label for="word-wrap" class="text-foreground0">Ajuste de línea</Label>
          <Switch v-model="wordWrap" id="word-wrap" />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.space-y-8 > * + * {
  margin-top: 2rem;
}

.space-y-6 > * + * {
  margin-top: 1.5rem;
}

.space-y-4 > * + * {
  margin-top: 1rem;
}
</style>