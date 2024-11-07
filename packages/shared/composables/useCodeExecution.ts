import type {
  CodeExecutionRequest,
  CodeExecutionResult,
  CodeExecutionService
} from '../types/code-execution';
import type {ErrorHandler} from '../types/error-handler';
import {useCodeExecutionStore} from '../stores/useCodeExecutionStore';

export interface UseCodeExecutionOptions {
  maxAttempts?: number;
  pollingInterval?: number;
  errorHandler?: ErrorHandler;
}

export function useCodeExecution(
  service: CodeExecutionService,
  options: UseCodeExecutionOptions = {}
) {
  const store = useCodeExecutionStore();
  const isPolling = ref(false);

  const {
    maxAttempts = 10,
    pollingInterval = 1000,
    errorHandler
  } = options;

  const executeCode = async (request: CodeExecutionRequest) => {
    try {
      store.isExecuting = true;
      store.error = null;

      const response = await service.executeCode(request);
      store.setTaskId(response.task_id);

      return await pollResult(response.task_id);
    } catch (error) {
      if (errorHandler) {
        await errorHandler.handleError(error, {
          statusCode: 500,
          groupKey: 'code-execution-error',
          data: {
            action: 'execute_code',
            request: {
              exercise_id: request.exercise_id,
              language: request.language
            }
          }
        });
      }
      store.setError(error as Error);
    } finally {
      store.isExecuting = false;
    }
  };

  const pollResult = async (taskId: string): Promise<CodeExecutionResult> => {
    isPolling.value = true;
    let attempts = 0;

    try {
      while (attempts < maxAttempts) {
        try {
          const result = await service.getExecutionResult(taskId);
          store.setResult(result);
          return result;
        } catch (error) {
          if (attempts === maxAttempts - 1) {
            throw errorHandler?.createAppError?.('Failed to get execution results', {
              statusCode: 408,
              groupKey: 'code-execution-timeout',
              data: {taskId, attempts, maxAttempts}
            }) || new Error('Failed to get execution results');
          }
          await new Promise(resolve => setTimeout(resolve, pollingInterval));
          attempts++;
        }
      }
    } catch (error) {
      if (errorHandler) {
        await errorHandler.handleError(error, {
          statusCode: 408,
          groupKey: 'code-execution-polling-error',
          data: { taskId, attempts }
        });
      }
      throw error;
    } finally {
      isPolling.value = false;
    }
  };

  return {
    executeCode,
    isExecuting: computed(() => store.isExecuting),
    isPolling,
    result: computed(() => store.result),
    error: computed(() => store.error),
    reset: () => store.reset()
  };
}
