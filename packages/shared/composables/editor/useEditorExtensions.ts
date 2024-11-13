import { computed } from 'vue'
import { EditorView } from "@codemirror/view"
import { javascript } from "@codemirror/lang-javascript"
import { loadLanguage } from "@uiw/codemirror-extensions-langs"
import { indentationMarkers } from "@replit/codemirror-indentation-markers"
import { lineNumbersRelative } from "@uiw/codemirror-extensions-line-numbers-relative"
import interact from "@replit/codemirror-interact"
import type { Extension as CodeMirrorExtension } from "@codemirror/state"
import type { LanguageSupport } from "@codemirror/language"
import { useEditorLinting, useEditorCompletions } from './'

export function useEditorExtensions(props: {
  language: string
  activeExtensions: string[],
  lineWrapping?: boolean
}) {
  const { getLanguageCompletions } = useEditorCompletions()
  const { getLanguageLinter } = useEditorLinting()

  const getLanguageExtension = (lang: string): CodeMirrorExtension => {
    let extension: LanguageSupport | CodeMirrorExtension
    switch (lang.toLowerCase()) {
      case "javascript":
      case "typescript":
        extension = javascript({ jsx: true, typescript: true })
        break
      case "java":
        extension = loadLanguage("java") || javascript()
        break
      case "rust":
        extension = loadLanguage("rust") || javascript()
        break
      case "python":
        extension = loadLanguage("python") || javascript()
        break
      case "csharp":
        extension = loadLanguage("csharp") || javascript()
        break
      case "go":
        extension = loadLanguage("go") || javascript()
        break
      default:
        extension = javascript()
    }
    return extension
  }

  const lineWrappingExtension = computed((): CodeMirrorExtension => {
    return EditorView.lineWrapping
  })



  const extensions = computed((): CodeMirrorExtension[] => {
    const exts: CodeMirrorExtension[] = [
      getLanguageExtension(props.language),
      getLanguageLinter(props.language),
      getLanguageCompletions(props.language)
    ]

    if (props.lineWrapping) {
      exts.push(lineWrappingExtension.value)
    }

    if (props.activeExtensions.includes("lineNumbersRelative")) {
      exts.push(lineNumbersRelative)
    }
    if (props.activeExtensions.includes("indentationMarkers")) {
      exts.push(indentationMarkers())
    }
    if (props.activeExtensions.includes("interact")) {
      exts.push(interact())
    }

    return exts
  })

  return {
    extensions
  }
}
