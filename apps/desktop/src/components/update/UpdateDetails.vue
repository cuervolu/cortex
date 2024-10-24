<script setup lang="ts">
import { Download } from "lucide-vue-next"
import { parseMarkdown } from '@nuxtjs/mdc/runtime'

const props = defineProps<{
  version: string
  date: string
  notes: string
  onBack: () => void
}>()

const formattedDate = computed(() => {
  return new Date(props.date).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
})

const { data: notesAst } = await useAsyncData(
    `changelog-${props.version}`,
    () => parseMarkdown(props.notes || '::alert{type="info"}\nNo release notes available\n::')
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
        <div class="text-white text-[17px]">New version available</div>
        <div class="flex gap-2 text-white text-[17px]">
          <span>{{ version }}</span>
          <span>{{ formattedDate }}</span>
        </div>
      </div>
    </div>

    <!-- Content -->
    <div class="flex-1 px-6 py-3 text-white overflow-y-auto">
      <MDCRenderer
          v-if="notesAst"
          :body="notesAst.body"
          :data="notesAst.data"
      />
    </div>

    <!-- Footer -->
    <div class="p-6 bg-white/10 border-t border-white/30">
      <div class="flex justify-end">
        <Button
            class="px-5 py-2.5 bg-[#d9d9d9] rounded-md border border-[#8c2a94] text-[#8c2a94] text-sm font-medium hover:bg-[#c9c9c9] transition-colors"
            @click="onBack"
        >
          Ok
        </Button>
      </div>
    </div>
  </div>
</template>

<style scoped>
:deep(.prose) {
  color: white;
}

:deep(.prose h1),
:deep(.prose h2),
:deep(.prose h3),
:deep(.prose h4),
:deep(.prose h5),
:deep(.prose h6) {
  color: white;
  margin-top: 1rem;
  margin-bottom: 0.5rem;
}

:deep(.prose a) {
  color: #d9d9d9;
  text-decoration: underline;
}

:deep(.prose strong) {
  color: white;
}

:deep(.prose ul),
:deep(.prose ol) {
  margin-top: 0.5rem;
  margin-bottom: 1rem;
}

:deep(.prose li) {
  margin-top: 0.25rem;
  margin-bottom: 0.25rem;
}

:deep(.prose blockquote) {
  border-left-color: rgba(255, 255, 255, 0.2);
  color: rgba(255, 255, 255, 0.7);
}

:deep(.prose code) {
  background: rgba(255, 255, 255, 0.1);
  padding: 0.2em 0.4em;
  border-radius: 0.25rem;
}

:deep(.prose pre) {
  background: rgba(0, 0, 0, 0.2);
}
</style>