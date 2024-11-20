import { ref, watch } from 'vue'
import type { ViewUpdate } from "@codemirror/view"
import type { Statistics } from "#build/nuxt-codemirror"

type EmitFn = {
  (e: 'update:code', value: string): void
  (e: 'change', value: string, viewUpdate: ViewUpdate): void
  (e: 'update', viewUpdate: ViewUpdate): void
}

export function useEditorCode(props: {
  initialCode: string
}, emit: EmitFn) {
  const code = ref(props.initialCode)

  const handleChange = (value: string, viewUpdate: ViewUpdate) => {
    emit('update:code', value)
    emit('change', value, viewUpdate)
  }

  const handleUpdate = (viewUpdate: ViewUpdate) => {
    emit('update', viewUpdate)
  }

  const handleStatistics = (stats: Statistics) => {
    console.log("Statistics:", stats)
  }

  watch(
    () => code.value,
    (newCode) => {
      emit('update:code', newCode)
    }
  )

  return {
    code,
    handleChange,
    handleUpdate,
    handleStatistics
  }
}
