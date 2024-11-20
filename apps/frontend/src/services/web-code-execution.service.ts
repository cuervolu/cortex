import type {
  CodeExecutionRequest,
  CodeExecutionService,
  CodeExecutionResponse,
  CodeExecutionResult
} from '@cortex/shared/types/code-execution';
import { API_ROUTES } from "@cortex/shared/config/api";
import { AppError } from "@cortex/shared/types";



export class WebCodeExecutionService implements CodeExecutionService {
  private encodeToBase64(str: string): string {
    return btoa(str);
  }

  async executeCode(request: CodeExecutionRequest): Promise<CodeExecutionResponse> {
    const { token } = useAuth();
    try {
      const { code, language, exercise_id } = request;
      const encodedCode = this.encodeToBase64(code);

      return await $fetch<CodeExecutionResponse>(API_ROUTES.EXECUTE_CODE, {
        method: 'POST',
        body: {
          code: encodedCode,
          language,
          exercise_id: exercise_id,
        },
        headers: {
          'Authorization': token.value
        }
      });
    } catch (error) {
      // Manejo específico de errores de $fetch
      if (error.response) {
        // Error con respuesta del servidor
        throw new AppError(`Error al ejecutar código: ${error.response._data?.message || 'Error desconocido'}`, {
          statusCode: error.response.status,
          data: error.response._data
        });
      }
      // Error de red u otro tipo
      throw new AppError('Error al conectar con el servidor', {
        statusCode: 500,
        cause: error
      });
    }
  }

  async getExecutionResult(taskId: string): Promise<CodeExecutionResult> {
    try {
      const { token } = useAuth();
      return await $fetch<CodeExecutionResult>(`${API_ROUTES.GET_EXECUTION_RESULT}${taskId}`, {
        method: 'GET',
        headers: {
          'Authorization': token.value
        }
      });
    } catch (error) {
      if (error.response?.status === 404) {
        // Resultado aún no disponible
        throw new Error('Resultado no disponible');
      }

      if (error.response) {
        throw new AppError(`Error al obtener el resultado: ${error.response._data?.message || 'Error desconocido'}`, {
          statusCode: error.response.status,
          data: error.response._data
        });
      }

      throw new AppError('Error al conectar con el servidor', {
        statusCode: 500,
        cause: error
      });
    }
  }
}