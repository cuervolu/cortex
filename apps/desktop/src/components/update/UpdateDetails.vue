<script setup lang="ts">
import { Download } from "lucide-vue-next"
import { parseMarkdown } from '@nuxtjs/mdc/runtime'
import { format, isValid, parseISO } from 'date-fns'
import { es } from 'date-fns/locale'

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
  if (!props.date) return ''

  try {
    let date = parseISO(props.date)
    if (!isValid(date)) {
      date = new Date(props.date)
    }
    if (!isValid(date)) {
      return ''
    }

    return format(date, "d 'de' MMM 'de' yyyy", {
      locale: es
    })
  } catch {
    return ''
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
          <span v-if="formattedDate">{{ formattedDate }}</span>
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