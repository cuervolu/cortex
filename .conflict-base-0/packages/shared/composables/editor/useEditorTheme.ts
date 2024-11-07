import { computed, watch } from 'vue'
import type { Extension } from "@codemirror/state"
import { StateEffect } from "@codemirror/state"
import type { CodeMirrorRef } from "#build/nuxt-codemirror"
import { materialLight, materialDark } from "../../themes"
import { noctisLilac } from "thememirror"
import { okaidia } from "@uiw/codemirror-theme-okaidia"

export type ThemeKey = 'noctisLilac' | 'okaidia' | 'materialLight' | 'materialDark'

export type ThemeRecord = Record<ThemeKey, Extension>

export function useEditorTheme(props: {
                                 activeTheme: ThemeKey
                                 availableThemes?: ThemeRecord
                               },
                               codemirror: Ref<CodeMirrorRef | undefined>) {

  const defaultThemes: ThemeRecord = {
    noctisLilac,
    okaidia,
    materialLight,
    materialDark,
  }

  const themes = props.availableThemes || defaultThemes

  const activeTheme = computed(() => {
    const theme = themes[props.activeTheme]
    return theme || materialLight
  })

  watch(
    () => props.activeTheme,
    () => {
      if (codemirror.value?.view) {
        const view = codemirror.value.view
        view.dispatch({
          effects: StateEffect.reconfigure.of(activeTheme.value)
        })
      }
    }
  )

  return {
    activeTheme,
    availableThemes: themes
  }
}
