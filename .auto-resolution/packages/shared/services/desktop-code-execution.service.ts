import type {
    CodeExecutionRequest,
    CodeExecutionService,
    CodeExecutionResponse,
    CodeExecutionResult
  } from '@cortex/shared/types/code-execution';
  import { invoke } from '@tauri-apps/api/core';
  
  export class DesktopCodeExecutionService implements CodeExecutionService {
    private encodeToBase64(str: string): string {
      return btoa(str);
    }
  
    async executeCode(request: CodeExecutionRequest): Promise<CodeExecutionResponse> {
      const { code, language, exercise_id } = request;
      
      const encodedCode = this.encodeToBase64(code);
  
      return await invoke<CodeExecutionResponse>('execute_code', {
        code: encodedCode,
        language,
        exerciseId: exercise_id 
      });
    }
  
    async getExecutionResult(taskId: string): Promise<CodeExecutionResult> {
      return await invoke<CodeExecutionResult>('get_code_execution_result', {
        taskId 
      });
    }
  }