<script setup lang="ts">
import { ref } from 'vue'


const currentTab = ref('general')
const theme = ref('dark')
const fontSize = ref(14)
const showLineNumbers = ref(false)
const wordWrap = ref(false)
const selectedFont = ref('System Default')
</script>

<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between border-b pb-4">
      <h1 class="text-2xl font-bold text-gray-900">Configuración</h1>
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
        <h2 class="text-lg font-semibold text-gray-900">Tema</h2>
        <div class="flex gap-4">
          <button 
            class="px-4 py-2 rounded-md transition-colors"
            :class="theme === 'light' ? 'bg-blue-600 text-white' : 'bg-gray-100 hover:bg-gray-200'"
            @click="theme = 'light'"
          >
            Light
          </button>
          <button 
            class="px-4 py-2 rounded-md transition-colors"
            :class="theme === 'dark' ? 'bg-blue-600 text-white' : 'bg-gray-100 hover:bg-gray-200'"
            @click="theme = 'dark'"
          >
            Dark
          </button>
          <button 
            class="px-4 py-2 rounded-md transition-colors"
            :class="theme === 'system' ? 'bg-blue-600 text-white' : 'bg-gray-100 hover:bg-gray-200'"
            @click="theme = 'system'"
          >
            System
          </button>
        </div>
      </div>

      <!-- Font Section -->
      <div class="space-y-4">
        <h2 class="text-lg font-semibold text-gray-900">Fuente</h2>
        <Select v-model="selectedFont">
          <SelectTrigger class="w-[200px]">
            <SelectValue :placeholder="selectedFont" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="system">System Default</SelectItem>
            <SelectItem value="arial">Arial</SelectItem>
            <SelectItem value="helvetica">Helvetica</SelectItem>
            <SelectItem value="roboto">Roboto</SelectItem>
          </SelectContent>
        </Select>
      </div>

      <!-- App Version -->
      <div class="space-y-2">
        <h2 class="text-lg font-semibold text-gray-900">Versión de la Aplicación</h2>
        <p class="text-sm text-gray-500">0.1.5</p>
      </div>
    </div>

    <!-- Editor Tab -->
    <div v-if="currentTab === 'editor'" class="space-y-8">
      <!-- Font Size Section -->
      <div class="space-y-4">
        <h2 class="text-lg font-semibold text-gray-900">Tamaño de Fuente</h2>
        <div class="flex items-center gap-4 max-w-[300px]">
          <Slider 
            v-model="fontSize"
            class="flex-1"
            :min="8"
            :max="32"
            :step="1"
          />
          <span class="text-sm text-gray-600 min-w-[45px]">{{ fontSize }}px</span>
        </div>
      </div>

      <!-- Editor Options -->
      <div class="space-y-6">
        <div class="flex items-center justify-between max-w-[300px]">
          <Label for="line-numbers" class="text-gray-700">Mostrar números de línea</Label>
          <Switch v-model="showLineNumbers" id="line-numbers" />
        </div>
        
        <div class="flex items-center justify-between max-w-[300px]">
          <Label for="word-wrap" class="text-gray-700">Ajuste de línea</Label>
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