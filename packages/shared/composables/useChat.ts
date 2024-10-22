import {computed} from 'vue'
import {parseMarkdown} from '@nuxtjs/mdc/runtime'
import type {MDCParserResult} from '@nuxtjs/mdc'

export interface Message {
  sender: 'ai' | 'user'
  content: string
  parsedContent?: MDCParserResult
}

export function useChat(messages: Message[], currentStreamingMessage: string) {
  const processMarkdown = async (content: string): Promise<MDCParserResult | null> => {
    try {
      if (!content.trim()) return null
      return await parseMarkdown(content)
    } catch (error) {
      console.error('Failed to parse markdown:', error)
      return null
    }
  }

  // Process all existing messages
  const processedMessages = computed(async () => {
    return await Promise.all(
      messages.map(async (message) => {
        if (message.sender === 'ai' && !message.parsedContent) {
          const parsed = await processMarkdown(message.content)
          return {
            ...message,
            parsedContent: parsed
          }
        }
        return message
      })
    )
  })

  // Process streaming message separately
  const processedStreamingMessage = computed(async () => {
    if (!currentStreamingMessage) return null
    return await processMarkdown(currentStreamingMessage)
  })

  return {
    processedMessages,
    processedStreamingMessage
  }
}
