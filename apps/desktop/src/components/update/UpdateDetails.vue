<script setup lang="ts">
import { Download } from "lucide-vue-next"
import { parseMarkdown } from '@nuxtjs/mdc/runtime'
import { format, isValid, parseISO } from 'date-fns'
import { es } from 'date-fns/locale'

// Props del componente
const props = defineProps<{
  version: string
  date: string
  notes: string
  onBack: () => void
}>()

// Detect if the app is running in development mode
const isDev = import.meta.dev

// Fake development changelog
const devChangelog = `
# Notas de la versión ${props.version}

## 🚀 Nuevas características

- Añadido nuevo sistema de temas personalizables
- Integración con ChatGPT para ayuda en ejercicios
- Soporte para más lenguajes de programación

## 🔧 Mejoras

- Optimización del rendimiento en el editor de código
- Mejora en la sincronización de datos
- Interface más intuitiva para la sección de ejercicios

## 🐛 Correcciones

- Solucionado problema con la carga de ejercicios
- Corregido error en la validación de código
- Arreglado problema de memoria en sesiones largas

## 📝 Otros cambios

- Actualización de dependencias
- Mejoras en la documentación
- Optimizaciones internas
`

const formattedDate = computed(() => {
  if (!props.date) return 'Fecha no disponible'

  try {
    const date = parseISO(props.date)
    
    if (!isValid(date)) return 'Fecha no disponible'
    
    return format(date, "d 'de' MMM 'de' yyyy", {
      locale: es
    })
  } catch {
    return 'Fecha no disponible'
  }
})
const { data: notesAst } = await useAsyncData(
    `changelog-${props.version}`,
    () => parseMarkdown(isDev ? devChangelog : (props.notes || '::alert{type="info"}\nNo hay notas de versión disponibles\n::'))
)
</script>

<template>
  <div class="flex flex-col h-full">
    <!-- Header -->
    <div class="px-6 py-3 bg-white/10 border-b border-white/30 flex items-center">
      <div class="w-[72px] h-[72px] p-5 bg-[#d9d9d9] rounded-xl flex items-center justify-center">
        <Download class="h-8 w-8 text-[#8c2a94]" />
      </div>
      <div class="ml-3.5">
        <div class="text-white text-[17px]">Nueva versión disponible</div>
        <div class="flex gap-2 text-white text-[17px]">
          <span>{{ version }}</span>
          <span>{{ formattedDate }}</span>
        </div>
      </div>
    </div>
    <!-- Content -->
    <div class="flex-1 px-6 py-3 text-white overflow-y-auto">
      <div class="prose prose-invert prose-pre:bg-white/5 prose-headings:text-white prose-headings:no-underline prose-p:text-white/90 prose-strong:text-white max-w-none">
        <MDCRenderer
            v-if="notesAst"
            :body="notesAst.body"
            :data="notesAst.data"
        />
      </div>
    </div>
    <!-- Footer -->
    <div class="p-6 bg-white/10 border-t border-white/30">
      <div class="flex justify-end">
        <Button
            class="px-5 py-2.5 bg-[#d9d9d9] rounded-md border border-[#8c2a94] text-[#8c2a94] text-sm font-medium hover:bg-[#c9c9c9] transition-colors"
            @click="onBack"
        >
          Aceptar
        </Button>
      </div>
    </div>
  </div>
</template>

<style scoped>
:deep(.prose) {
  color: white;
  max-width: none;
}

:deep(.prose h1) {
  color: white;
  font-size: 1.8rem;
  margin-top: 0;
  margin-bottom: 1rem;
}

:deep(.prose h2) {
  color: white;
  font-size: 1.4rem;
  margin-top: 2rem;
  margin-bottom: 1rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  padding-bottom: 0.5rem;
}

:deep(.prose h3) {
  color: white;
  font-size: 1.2rem;
  margin-top: 1.5rem;
  margin-bottom: 0.75rem;
}

:deep(.prose ul) {
  margin-top: 0.5rem;
  margin-bottom: 1.5rem;
  list-style-type: none;
  padding-left: 1rem;
}

:deep(.prose li) {
  margin-top: 0.25rem;
  margin-bottom: 0.25rem;
  position: relative;
}

:deep(.prose li::before) {
  content: "•";
  position: absolute;
  left: -1rem;
  color: rgba(255, 255, 255, 0.5);
}

:deep(.prose code) {
  background: rgba(255, 255, 255, 0.1);
  padding: 0.2em 0.4em;
  border-radius: 0.25rem;
  font-size: 0.875em;
}

:deep(.prose pre) {
  background: rgba(0, 0, 0, 0.2);
  padding: 1rem;
  border-radius: 0.5rem;
  margin: 1rem 0;
}

:deep(.prose a) {
  color: #d9d9d9;
  text-decoration: underline;
  text-decoration-thickness: 1px;
  text-underline-offset: 2px;
}

:deep(.prose strong) {
  color: white;
  font-weight: 600;
}

:deep(.prose blockquote) {
  border-left: 4px solid rgba(255, 255, 255, 0.2);
  padding-left: 1rem;
  color: rgba(255, 255, 255, 0.7);
  font-style: italic;
  margin: 1rem 0;
}
</style>