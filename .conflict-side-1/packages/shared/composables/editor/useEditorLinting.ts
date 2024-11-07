import { linter, lintGutter } from "@codemirror/lint"
import type { Diagnostic } from "@codemirror/lint"
import { syntaxTree } from "@codemirror/language"
import type { EditorView } from "@codemirror/view"
import type {Extension} from "@codemirror/state";

interface LinterRule {
  test: (line: string) => boolean
  message: string
  severity: "error" | "warning" | "info"
}

interface LanguageRules {
  lineRules?: LinterRule[]
  globalRules?: Array<(text: string) => Diagnostic[]>
  customRules?: Array<(view: EditorView) => Diagnostic[]>
}

// Reglas específicas por lenguaje
const languageRules: Record<string, LanguageRules> = {
  python: {
    lineRules: [
      {
        test: (line) => /^(def|class|if|for|while|with|try|elif|else)\s+.*[^:]$/.test(line.trim()),
        message: "Falta dos puntos (:) al final de la declaración",
        severity: "error"
      },
      {
        test: (line) => /^[^#]*\s+$/.test(line),
        message: "Línea con espacios en blanco al final",
        severity: "warning"
      },
      {
        test: (line) => line.length > 79,
        message: "La línea excede los 79 caracteres (PEP 8)",
        severity: "warning"
      }
    ],
    globalRules: [
      (text) => text.includes("\t") ? [{
        from: 0,
        to: text.length,
        severity: "warning",
        message: "Usa espacios en lugar de tabulaciones para la indentación"
      }] : []
    ]
  },

  javascript: {
    lineRules: [
      {
        test: (line) => /^[^/]*[\w)\]"']\s*[^;{})\]"'\s]$/.test(line.trim()),
        message: "Posible punto y coma faltante",
        severity: "warning"
      },
      {
        test: (line) => /var\s+/.test(line),
        message: "Considera usar 'let' o 'const' en lugar de 'var'",
        severity: "warning"
      },
      {
        test: (line) => /==[^=]/.test(line),
        message: "Considera usar === para comparaciones estrictas",
        severity: "warning"
      }
    ],
    globalRules: [
      (text) => /console\.(log|debug|info|warn|error)/.test(text) ? [{
        from: 0,
        to: text.length,
        severity: "info",
        message: "Considera remover los console.log antes de producción"
      }] : []
    ]
  },

  csharp: {
    lineRules: [
      {
        test: (line) => /^[^/]*[\w)\]"']\s*[^;{})\]"'\s]$/.test(line.trim()),
        message: "Falta punto y coma (;) al final de la línea",
        severity: "error"
      },
      {
        test: (line) => /^(class|interface|struct)\s+\w+\s*[^:{]$/.test(line.trim()),
        message: "Las declaraciones de clase deben tener llaves",
        severity: "error"
      },
      {
        test: (line) => /^\s*catch\s*\(\s*Exception\s*\)/.test(line),
        message: "Evita atrapar Exception genérica",
        severity: "warning"
      }
    ],
    customRules: [
      (view) => {
        const diagnostics: Diagnostic[] = []
        const cursor = syntaxTree(view.state).cursor()

        while (cursor.next()) {
          if (cursor.name === "MethodDeclaration") {
            const methodText = view.state.doc.sliceString(cursor.from, cursor.to)
            if (!/\bpublic\b|\bprivate\b|\bprotected\b|\binternal\b/.test(methodText)) {
              diagnostics.push({
                from: cursor.from,
                to: cursor.to,
                severity: "warning",
                message: "Especifica un modificador de acceso explícito"
              })
            }
          }
        }
        return diagnostics
      }
    ]
  },

  go: {
    lineRules: [
      {
        test: (line: string): boolean => /^func.*\)\s*{/.test(line.trim()) && !/^func.*\)\s*\w+\s*{/.test(line.trim()),
        message: "Considera especificar el tipo de retorno",
        severity: "info"
      },
      {
        test: (line: string): boolean => /if\s+err\s*!=\s*nil\s*{[\s\S]*return[^}]*}/.test(line),
        message: "Considera manejar el error antes de retornar",
        severity: "warning"
      },
    ],
    customRules: [
      (view: EditorView): Diagnostic[] => {
        const diagnostics: Diagnostic[] = []
        const text = view.state.doc.toString()
        const lines = view.state.doc.toJSON()
        let braceBalance = 0
        let lastOpenBracePos = -1

        // Verifica si hay package declarado
        if (!text.includes("package")) {
          diagnostics.push({
            from: 0,
            to: 0,
            severity: "error",
            message: "Archivo Go debe comenzar con una declaración de package"
          })
        }

        lines.forEach((line: string, lineIndex: number) => {
          const fromPos = view.state.doc.line(lineIndex + 1).from
          // eslint-disable-next-line @typescript-eslint/no-unused-vars
          const toPos = view.state.doc.line(lineIndex + 1).to

          for (let i = 0; i < line.length; i++) {
            if (line[i] === '{') {
              braceBalance++
              lastOpenBracePos = fromPos + i
            } else if (line[i] === '}') {
              braceBalance--
            }

            if (braceBalance < 0) {
              diagnostics.push({
                from: fromPos + i,
                to: fromPos + i + 1,
                severity: "error",
                message: "Llave de cierre sin llave de apertura correspondiente"
              })
              braceBalance = 0
            }
          }
        })

        if (braceBalance > 0) {
          diagnostics.push({
            from: lastOpenBracePos,
            to: lastOpenBracePos + 1,
            severity: "error",
            message: `Llave(s) sin cerrar: ${braceBalance} llave(s) abierta(s) sin cerrar`
          })
        }

        const importMatches = text.match(/import\s+\([^)]+\)/g)
        if (importMatches) {
          diagnostics.push({
            from: 0,
            to: text.length,
            severity: "warning",
            message: "Verifica que todos los imports estén siendo utilizados"
          })
        }
        return diagnostics
      }
    ]
  },

  java: {
    lineRules: [
      {
        test: (line: string): boolean => {
          const trimmed = line.trim()
          return Boolean(trimmed &&
            !trimmed.endsWith(";") &&
            !trimmed.endsWith("{") &&
            !trimmed.endsWith("}") &&
            !trimmed.startsWith("//") &&
            !trimmed.startsWith("package") &&
            !trimmed.startsWith("import") &&
            !/^(public|private|protected)\s+(class|interface|enum)/.test(trimmed))
        },
        message: "Falta punto y coma (;) al final de la línea",
        severity: "error"
      },
      {
        test: (line) => /catch\s*\(\s*Exception\s*\)/.test(line),
        message: "Evita capturar Exception genérica",
        severity: "warning"
      },
      {
        test: (line) => /^(?!.*@Override).*public\s+void\s+toString\s*\(/.test(line),
        message: "Método toString() debería tener @Override",
        severity: "warning"
      }
    ],
    customRules: [
      (view) => {
        const diagnostics: Diagnostic[] = []
        const cursor = syntaxTree(view.state).cursor()

        while (cursor.next()) {
          if (cursor.name === "MethodDeclaration") {
            const methodText = view.state.doc.sliceString(cursor.from, cursor.to)
            if (!/^@|\bprivate\b|\bprotected\b|\bpublic\b/.test(methodText)) {
              diagnostics.push({
                from: cursor.from,
                to: cursor.to,
                severity: "warning",
                message: "Los métodos deberían tener un modificador de acceso explícito"
              })
            }
          }
        }
        return diagnostics
      }
    ]
  },

  rust: {
    lineRules: [
      {
        test: (line) => /^\s*fn\s+\w+\s*\([^)]*\)\s*{/.test(line.trim()),
        message: "Considera añadir un tipo de retorno explícito",
        severity: "warning"
      },
      {
        test: (line) => /mut\s+\w+\s*:\s*String\s*=\s*String::new\(\)/.test(line),
        message: "Considera usar String::with_capacity si conoces el tamaño aproximado",
        severity: "info"
      }
    ],
    customRules: [
      (view) => {
        const diagnostics: Diagnostic[] = []
        const cursor = syntaxTree(view.state).cursor()

        while (cursor.next()) {
          if (cursor.name === "FunctionDef") {
            const fnText = view.state.doc.sliceString(cursor.from, cursor.to)
            if (!/->/.test(fnText)) {
              diagnostics.push({
                from: cursor.from,
                to: cursor.to,
                severity: "warning",
                message: "Considera añadir un tipo de retorno explícito",
                actions: [{
                  name: "Añadir -> ()",
                  apply(view, from, to) {
                    const pos = view.state.doc.toString().lastIndexOf(")", to)
                    if (pos > -1) {
                      view.dispatch({
                        changes: {from: pos + 1, insert: " -> ()"}
                      })
                    }
                  }
                }]
              })
            }

            // Verifica el uso de unwrap
            if (/\bunwrap\(\)/.test(fnText)) {
              diagnostics.push({
                from: cursor.from,
                to: cursor.to,
                severity: "warning",
                message: "Evita usar unwrap() directamente, maneja el Result/Option apropiadamente"
              })
            }
          }
        }
        return diagnostics
      }
    ]
  }
}

export function useEditorLinting() {
  const getLanguageLinter = (language: string): Extension[] => {
    const lint = linter((view: EditorView) => {
      const diagnostics: Diagnostic[] = []
      const doc = view.state.doc
      const text = doc.toString()
      const rules = languageRules[language.toLowerCase()]

      if (!rules) return []

      // Aplicar reglas por línea
      if (rules.lineRules) {
        const lines = doc.toJSON()
        lines.forEach((line: string, lineIndex: number) => {
          rules.lineRules?.forEach(rule => {
            if (rule.test(line)) {
              const fromPos = doc.line(lineIndex + 1).from
              const toPos = doc.line(lineIndex + 1).to
              diagnostics.push({
                from: fromPos,
                to: toPos,
                severity: rule.severity,
                message: rule.message
              })
            }
          })
        })
      }

      // Aplicar reglas globales y personalizadas
      if (rules.globalRules) {
        rules.globalRules.forEach(rule => {
          diagnostics.push(...rule(text))
        })
      }

      if (rules.customRules) {
        rules.customRules.forEach(rule => {
          diagnostics.push(...rule(view))
        })
      }

      return diagnostics
    })

    const gutter = lintGutter({
      hoverTime: 300,
      markerFilter: (diagnostics: readonly Diagnostic[]) =>
        Array.from(diagnostics.filter(d => d.severity !== "info")),
      tooltipFilter: (diagnostics: readonly Diagnostic[]) =>
        Array.from(diagnostics)
    })

    return [lint, gutter]
  }

  return {
    getLanguageLinter
  }
}
