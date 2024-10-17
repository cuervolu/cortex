export interface SendPromptParams {
  message: string;
  userId: string;
  editorContent?: string;
  language?: string;
  selectedModel: string;
}

export interface OllamaModelInfo {
  name: string;
  id: string;
  size: string;
  modified: string;
}

export interface OllamaModelDetails {
  architecture: string;
  parameters: string;
  context_length: string;
  embedding_length: string;
  quantization: string;
  license: string;
}

export interface OllamaModel {
  name: string
  tags: string[]
  description: string
}