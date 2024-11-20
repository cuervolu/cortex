import {EditorView} from '@codemirror/view'
import type {Extension} from '@codemirror/state'
import {HighlightStyle, syntaxHighlighting} from '@codemirror/language'
import {tags as t} from '@lezer/highlight'

export const materialDarkConfig = {
  name: 'materialDark',
  dark: true,
  background: '#1B0D26',
  foreground: '#EEFFFF',
  selection: '#80CBC420',
  cursor: '#FFCC00',
  dropdownBackground: '#263238',
  dropdownBorder: '#FFFFFF10',
  activeLine: '#4c616c22',
  matchingBracket: '#263238',
  keyword: '#89DDFF',
  storage: '#89DDFF',
  variable: '#EEFFFF',
  parameter: '#EEFFFF',
  function: '#82AAFF',
  string: '#C3E88D',
  constant: '#89DDFF',
  type: '#FFCB6B',
  class: '#FFCB6B',
  number: '#F78C6C',
  comment: '#546E7A',
  heading: '#89DDFF',
  invalid: '#f0717870',
  regexp: '#C3E88D',
}

export const materialDarkTheme = EditorView.theme({
  '&': {
    color: materialDarkConfig.foreground,
    backgroundColor: materialDarkConfig.background,
  },

  '.cm-content': {caretColor: materialDarkConfig.cursor},

  '.cm-cursor, .cm-dropCursor': {borderLeftColor: materialDarkConfig.cursor},
  '&.cm-focused > .cm-scroller > .cm-selectionLayer .cm-selectionBackground, .cm-selectionBackground, .cm-content ::selection': {backgroundColor: materialDarkConfig.selection},

  '.cm-panels': {backgroundColor: materialDarkConfig.dropdownBackground, color: materialDarkConfig.foreground},
  '.cm-panels.cm-panels-top': {borderBottom: '2px solid black'},
  '.cm-panels.cm-panels-bottom': {borderTop: '2px solid black'},

  '.cm-searchMatch': {
    backgroundColor: materialDarkConfig.dropdownBackground,
    outline: `1px solid ${materialDarkConfig.dropdownBorder}`
  },
  '.cm-searchMatch.cm-searchMatch-selected': {
    backgroundColor: materialDarkConfig.selection
  },

  '.cm-activeLine': {backgroundColor: materialDarkConfig.activeLine},
  '.cm-selectionMatch': {backgroundColor: materialDarkConfig.selection},

  '&.cm-focused .cm-matchingBracket, &.cm-focused .cm-nonmatchingBracket': {
    backgroundColor: materialDarkConfig.matchingBracket,
    outline: 'none'
  },

  '.cm-gutters': {
    backgroundColor: materialDarkConfig.dropdownBorder,
    color: materialDarkConfig.foreground,
    border: 'none'
  },
  '.cm-activeLineGutter': {backgroundColor: materialDarkConfig.background},

  '.cm-foldPlaceholder': {
    backgroundColor: 'transparent',
    border: 'none',
    color: materialDarkConfig.foreground
  },
  '.cm-tooltip': {
    border: `1px solid ${materialDarkConfig.dropdownBorder}`,
    backgroundColor: materialDarkConfig.dropdownBackground,
    color: materialDarkConfig.foreground,
  },
  '.cm-tooltip .cm-tooltip-arrow:before': {
    borderTopColor: 'transparent',
    borderBottomColor: 'transparent'
  },
  '.cm-tooltip .cm-tooltip-arrow:after': {
    borderTopColor: materialDarkConfig.foreground,
    borderBottomColor: materialDarkConfig.foreground,
  },
  '.cm-tooltip-autocomplete': {
    '& > ul > li[aria-selected]': {
      background: materialDarkConfig.selection,
      color: materialDarkConfig.foreground,
    }
  }
}, {dark: materialDarkConfig.dark})

export const materialDarkHighlightStyle = HighlightStyle.define([
  {tag: t.keyword, color: materialDarkConfig.keyword},
  {tag: [t.name, t.deleted, t.character, t.macroName], color: materialDarkConfig.variable},
  {tag: [t.propertyName], color: materialDarkConfig.function},
  {tag: [t.processingInstruction, t.string, t.inserted, t.special(t.string)], color: materialDarkConfig.string},
  {tag: [t.function(t.variableName), t.labelName], color: materialDarkConfig.function},
  {tag: [t.color, t.constant(t.name), t.standard(t.name)], color: materialDarkConfig.constant},
  {tag: [t.definition(t.name), t.separator], color: materialDarkConfig.variable},
  {tag: [t.className], color: materialDarkConfig.class},
  {tag: [t.number, t.changed, t.annotation, t.modifier, t.self, t.namespace], color: materialDarkConfig.number},
  {tag: [t.typeName], color: materialDarkConfig.type, fontStyle: materialDarkConfig.type},
  {tag: [t.operator, t.operatorKeyword], color: materialDarkConfig.keyword},
  {tag: [t.url, t.escape, t.regexp, t.link], color: materialDarkConfig.regexp},
  {tag: [t.meta, t.comment], color: materialDarkConfig.comment},
  {tag: t.strong, fontWeight: 'bold'},
  {tag: t.emphasis, fontStyle: 'italic'},
  {tag: t.link, textDecoration: 'underline'},
  {tag: t.heading, fontWeight: 'bold', color: materialDarkConfig.heading},
  {tag: [t.atom, t.bool, t.special(t.variableName)], color: materialDarkConfig.variable},
  {tag: t.invalid, color: materialDarkConfig.invalid},
  {tag: t.strikethrough, textDecoration: 'line-through'},
])

export const materialDark: Extension = [
  materialDarkTheme,
  syntaxHighlighting(materialDarkHighlightStyle),
]
