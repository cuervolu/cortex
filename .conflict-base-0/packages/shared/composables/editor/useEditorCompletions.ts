import {
  autocompletion,
  type CompletionContext,
  type CompletionResult,
  type Completion
} from "@codemirror/autocomplete"
import { snippetCompletion } from "@codemirror/autocomplete"
import type { Extension } from "@codemirror/state"

interface CompletionItem {
  label: string
  type: string
  info?: string
  detail?: string
  apply?: string
}

interface SnippetDefinition {
  label: string
  detail: string
  type: string
  info: string
  template: string
}

export function useEditorCompletions() {
  const completionDefinitions: Record<string, CompletionItem[]> = {
    python: [
      { label: "print", type: "function", info: "Imprime en consola" },
      { label: "len", type: "function", info: "Obtiene la longitud de una secuencia" },
      { label: "range", type: "function", info: "Genera una secuencia de números" },
      { label: "list", type: "type", info: "Crea una nueva lista" },
      { label: "dict", type: "type", info: "Crea un nuevo diccionario" }
    ],
    java: [
      { label: "public", type: "keyword" },
      { label: "private", type: "keyword" },
      { label: "protected", type: "keyword" },
      { label: "class", type: "keyword" },
      { label: "interface", type: "keyword" }
    ],
    rust: [
      { label: "let", type: "keyword" },
      { label: "mut", type: "keyword" },
      { label: "fn", type: "keyword" },
      { label: "struct", type: "keyword" },
      { label: "impl", type: "keyword" }
    ],
    javascript: [
      { label: "const", type: "keyword", info: "Declara una constante" },
      { label: "let", type: "keyword", info: "Declara una variable" },
      { label: "function", type: "keyword", info: "Define una función" },
      { label: "async", type: "keyword", info: "Define una función asíncrona" },
      { label: "await", type: "keyword", info: "Espera una promesa" },
      { label: "class", type: "keyword", info: "Define una clase" },
      { label: "import", type: "keyword", info: "Importa módulos" },
      { label: "export", type: "keyword", info: "Exporta módulos" },
      { label: "console.log", type: "function", info: "Imprime en la consola" },
      { label: "Promise", type: "class", info: "Crea una nueva promesa" }
    ],
    go: [
      { label: "func", type: "keyword", info: "Define una función" },
      { label: "package", type: "keyword", info: "Declara el paquete" },
      { label: "import", type: "keyword", info: "Importa paquetes" },
      { label: "var", type: "keyword", info: "Declara una variable" },
      { label: "const", type: "keyword", info: "Declara una constante" },
      { label: "type", type: "keyword", info: "Define un nuevo tipo" },
      { label: "struct", type: "keyword", info: "Define una estructura" },
      { label: "interface", type: "keyword", info: "Define una interfaz" },
      { label: "defer", type: "keyword", info: "Difiere la ejecución" },
      { label: "go", type: "keyword", info: "Inicia una goroutine" }
    ],
    csharp: [
      { label: "public", type: "keyword", info: "Modificador de acceso público" },
      { label: "private", type: "keyword", info: "Modificador de acceso privado" },
      { label: "class", type: "keyword", info: "Define una clase" },
      { label: "interface", type: "keyword", info: "Define una interfaz" },
      { label: "using", type: "keyword", info: "Importa namespaces" },
      { label: "namespace", type: "keyword", info: "Define un namespace" },
      { label: "async", type: "keyword", info: "Define un método asíncrono" },
      { label: "await", type: "keyword", info: "Espera una tarea asíncrona" },
      { label: "var", type: "keyword", info: "Declaración implícita de tipo" },
      { label: "Console.WriteLine", type: "function", info: "Imprime en la consola" }
    ]
  }

  const snippetDefinitions: Record<string, SnippetDefinition[]> = {
    python: [
      {
        label: "def",
        detail: "Define una función",
        type: "keyword",
        info: "Define una nueva función",
        template: "def ${1:name}(${2:params}):\n\t${3}"
      },
      {
        label: "class",
        detail: "Define una clase",
        type: "keyword",
        info: "Define una nueva clase",
        template: "class ${1:ClassName}:\n\tdef __init__(self):\n\t\t${2:pass}"
      },
      {
        label: "if",
        detail: "Declaración if",
        type: "keyword",
        info: "Crea una declaración condicional",
        template: "if ${1:condition}:\n\t${2}"
      }
    ],
    java: [
      {
        label: "sout",
        detail: "System.out.println",
        type: "function",
        info: "Imprime en consola",
        template: 'System.out.println(${1:"Hello"});'
      },
      {
        label: "psvm",
        detail: "public static void main",
        type: "function",
        info: "Método principal",
        template: 'public static void main(String[] args) {\n\t${1}\n}'
      }
    ],
    rust: [
      {
        label: "fn",
        detail: "Define una función",
        type: "keyword",
        info: "Define una nueva función",
        template: "fn ${1:name}(${2:params}) ${3:-> ReturnType} {\n\t${4}\n}"
      },
      {
        label: "struct",
        detail: "Define una estructura",
        type: "keyword",
        info: "Define una nueva estructura",
        template: "struct ${1:Name} {\n\t${2}\n}"
      }
    ],
    javascript: [
      {
        label: "fn",
        detail: "Función anónima",
        type: "function",
        info: "Crea una función anónima",
        template: "function(${1:params}) {\n\t${2}\n}"
      },
      {
        label: "afn",
        detail: "Función flecha",
        type: "function",
        info: "Crea una función flecha",
        template: "(${1:params}) => {\n\t${2}\n}"
      },
      {
        label: "cls",
        detail: "Clase",
        type: "class",
        info: "Define una nueva clase",
        template: "class ${1:Name} {\n\tconstructor(${2:params}) {\n\t\t${3}\n\t}\n}"
      },
      {
        label: "prom",
        detail: "Promise",
        type: "class",
        info: "Crea una nueva promesa",
        template: "new Promise((resolve, reject) => {\n\t${1}\n})"
      },
      {
        label: "imp",
        detail: "Import",
        type: "keyword",
        info: "Importa un módulo",
        template: "import { ${1:module} } from '${2:package}'"
      }
    ],
    go: [
      {
        label: "func",
        detail: "Function",
        type: "keyword",
        info: "Define una nueva función",
        template: "func ${1:name}(${2:params}) ${3:returnType} {\n\t${4}\n}"
      },
      {
        label: "main",
        detail: "Main function",
        type: "function",
        info: "Función principal",
        template: "func main() {\n\t${1}\n}"
      },
      {
        label: "str",
        detail: "Struct",
        type: "keyword",
        info: "Define una estructura",
        template: "type ${1:Name} struct {\n\t${2:field} ${3:type}\n}"
      },
      {
        label: "if",
        detail: "If with error check",
        type: "keyword",
        info: "If con comprobación de error",
        template: "if err != nil {\n\treturn ${1:err}\n}"
      },
      {
        label: "pkg",
        detail: "Package declaration",
        type: "keyword",
        info: "Declara un paquete",
        template: "package ${1:main}"
      }
    ],
    csharp: [
      {
        label: "class",
        detail: "Class",
        type: "class",
        info: "Define una nueva clase",
        template: "public class ${1:Name}\n{\n\t${2}\n}"
      },
      {
        label: "prop",
        detail: "Property",
        type: "property",
        info: "Define una propiedad",
        template: "public ${1:type} ${2:Name} { get; set; }"
      },
      {
        label: "ctor",
        detail: "Constructor",
        type: "function",
        info: "Define un constructor",
        template: "public ${1:ClassName}(${2:params})\n{\n\t${3}\n}"
      },
      {
        label: "mth",
        detail: "Method",
        type: "function",
        info: "Define un método",
        template: "public ${1:ReturnType} ${2:MethodName}(${3:params})\n{\n\t${4}\n}"
      },
      {
        label: "amth",
        detail: "Async Method",
        type: "function",
        info: "Define un método asíncrono",
        template: "public async Task<${1:ReturnType}> ${2:MethodName}Async(${3:params})\n{\n\t${4}\n}"
      }
    ]
  }

  const createCompletionOptions = (language: string): Completion[] => {
    const lang = language.toLowerCase()
    const standardCompletions = (completionDefinitions[lang] || []).map(item => ({
      label: item.label,
      type: item.type,
      info: item.info,
      detail: item.detail,
      apply: item.apply
    }))

    const snippetCompletions = (snippetDefinitions[lang] || []).map(snippet =>
      snippetCompletion(snippet.template, {
        label: snippet.label,
        detail: snippet.detail,
        type: snippet.type,
        info: snippet.info
      })
    )

    return [...standardCompletions, ...snippetCompletions]
  }

  const getLanguageCompletions = (language: string): Extension => {
    return autocompletion({
      override: [
        (context: CompletionContext): CompletionResult | null => {
          const word = context.matchBefore(/\w*/)
          if (!word || (word.from === word.to && !context.explicit)) return null

          return {
            from: word.from,
            options: createCompletionOptions(language),
            validFor: /^\w*$/
          }
        }
      ]
    })
  }

  return {
    getLanguageCompletions
  }
}
