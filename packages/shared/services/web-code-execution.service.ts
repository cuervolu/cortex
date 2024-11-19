import type {
  CodeExecutionRequest,
  CodeExecutionService,
  CodeExecutionResponse,
  CodeExecutionResult
} from '@cortex/shared/types/code-execution';
import { API_ROUTES } from '../config/api';

export class WebCodeExecutionService implements CodeExecutionService {
  private encodeToBase64(str: string): string {
    return btoa(str);
  }

  async executeCode(request: CodeExecutionRequest): Promise<CodeExecutionResponse> {
    const { code, language, exercise_id } = request;
    const encodedCode = this.encodeToBase64(code);

    const response = await $fetch(API_ROUTES.EXECUTE_CODE, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        code: encodedCode,
        language,
        exerciseId: exercise_id,
      }),
    });

    if (!response.ok) {
      throw new Error(`Error al ejecutar código: ${response.statusText}`);
    }

    return await response.json();
  }

  async getExecutionResult(taskId: string): Promise<CodeExecutionResult> {
    const response = await $fetch(`${API_ROUTES.GET_EXECUTION_RESULT}${taskId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      throw new Error(`Error al obtener el resultado: ${response.statusText}`);
    }

    return await response.json();
  }
}
