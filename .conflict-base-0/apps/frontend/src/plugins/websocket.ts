import { defineNuxtPlugin, useRuntimeConfig } from '#app'
import SockJS from 'sockjs-client'
import type {Client, ConnectionHeaders, Frame, Message} from 'webstomp-client';
import Stomp from 'webstomp-client'
import type {ChatMessageRequest} from "~/interfaces";

export default defineNuxtPlugin(() => {
  const config = useRuntimeConfig()
  const wsEndpoint = `${config.public.apiBaseUrl}ws`

  const stompClient = ref<Client | null>(null)
  const socket = ref<any>(null)
  const isConnected = ref(false)

  const getStompClient = async (): Promise<Client> => {
    if (stompClient.value && isConnected.value) {
      console.log('WebSocket connection is successful.')
      return stompClient.value
    } else {
      await new Promise(resolve => setTimeout(resolve, 1000)) // Wait for 1 second
      return await getStompClient()
    }
  }

  const connect = (callback: (response: { sessionId: string | null; status: string; message: string }) => void): void => {
    isConnected.value = false
    socket.value = new SockJS(wsEndpoint)
    stompClient.value = Stomp.over(socket.value)
    if (stompClient.value) {
      stompClient.value.debug = () => {}
      const headers: ConnectionHeaders = {}
      stompClient.value.connect(
          headers,
          (frame?: Frame) => {
            console.log("WebSocket Connection frame:", frame)
            isConnected.value = true
            callback({
              sessionId: frame?.headers['session-id'] || null,
              status: 'CONNECTED',
              message: 'WebSocket connection successful'
            })
          },
          (error: Frame | CloseEvent) => {
            console.log("WebSocket Connection error:", error)
            callback({ sessionId: null, status: 'CONNECTION_ERROR', message: 'WebSocket connection error' })
          }
      )
    }
  }


  const disconnect = (): void => {
    if (stompClient.value) {
      stompClient.value.disconnect(() => {
        console.log('WebSocket connection is closed successfully.')
        isConnected.value = false
      })
    }
  }

  const subscribeToTopic = async (callback: (response: any) => void): Promise<void> => {
    const websocketStompClient = await getStompClient()
    if (websocketStompClient) {
      websocketStompClient.subscribe('/user/queue/messages', (message: Message) => {
        if (!message || !message.body) {
          disconnect()
        } else {
          const messageBody = JSON.parse(message.body)
          if (messageBody.status === 'SUCCESS' || messageBody.status === 'ERROR') {
            disconnect()
          }
          callback(messageBody)
        }
      })
    }
  }

  const subscribe = async (callback: (response: any) => void): Promise<void> => {
    connect((response) => {
      if (response.status === 'CONNECTED') {
        subscribeToTopic(callback)
      } else {
        callback(response)
      }
    })
  }

  const sendMessage = async (msg: ChatMessageRequest): Promise<void> => {
    const client = await getStompClient()
    if (client) {
      client.send('/app/chat', JSON.stringify(msg), {})
    }
  }

  return {
    provide: {
      webSocket: {
        connect,
        disconnect,
        subscribe,
        sendMessage
      }
    }
  }
})