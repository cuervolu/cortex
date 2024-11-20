export interface Message {
  sender: 'ai' | 'user'
  content: string
  timestamp?: string
}

export interface AIProvider {
  name: string
  requiresApiKey: boolean
  isConfigured?: boolean
}

export interface ChatState {
  provider: AIProvider
  messages: Message[]
  currentStreamingMessage: string
  isStreaming: boolean
  isSending: boolean
  error: string | null
}

export interface ExerciseContext {
  exercise_id: string
  username: string
  exercise_slug: string
  exercise_title: string
  exercise_instructions: string
  exercise_hints?: string
  editor_content?: string
  editor_language?: string
}

export function createExerciseContext(params: {
  exercise_id: string
  exercise_slug: string
  username: string
  exercise_title: string
  exercise_instructions: string
  exercise_hints?: string
  editor_content?: string
  editor_language?: string
}): ExerciseContext {
  return {
    exercise_id: params.exercise_id,
    exercise_slug: params.exercise_slug,
    username: params.username,
    exercise_title: params.exercise_title,
    exercise_instructions: params.exercise_instructions,
    exercise_hints: params.exercise_hints,
    editor_content: params.editor_content,
    editor_language: params.editor_language
  }
}