import type {
  CodeExecutionResult,
  CodeExecutionResponse
} from '../types';

export const useCodeExecutionStore = defineStore('code-execution', () => {
  const currentTaskId = ref<string | null>(null);
  const isExecuting = ref(false);
  const result = ref<CodeExecutionResult | null>(null);
  const error = ref<Error | null>(null);
  const activeTab = ref<string>('instructions');

  const setTaskId = (taskId: string) => {
    currentTaskId.value = taskId;
  };

  const setResult = (executionResult: CodeExecutionResult) => {
    result.value = executionResult;
  };

  const setError = (err: Error) => {
    error.value = err;
  };

  const setActiveTab = (tab: string) => {
    activeTab.value = tab;
  };

  const reset = () => {
    currentTaskId.value = null;
    isExecuting.value = false;
    result.value = null;
    error.value = null;
    activeTab.value = 'instructions';
  };



  return {
    // state
    currentTaskId,
    isExecuting,
    result,
    error,
    activeTab,
    // actions
    setTaskId,
    setResult,
    setError,
    setActiveTab,
    reset
  };
});
