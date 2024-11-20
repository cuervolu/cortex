export interface ChatMessageRequest {
  mentorship_id: number;
  sender_id: number;
  recipient_id: number;
  content: string;
}

export interface FrontendChatMessage extends ChatMessageRequest {
  id: number;
}