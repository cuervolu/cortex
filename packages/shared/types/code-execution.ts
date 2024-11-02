export interface CodeExecutionRequest {
  code: string;
  language: string;
  exercise_id: number;
}

export interface CodeExecutionResponse {
  task_id: string;
  status: string;
  message: string;
  submission_time: string;
}

export interface TestCaseResult {
  passed: boolean;
  input: string;
  expected_output: string;
  actual_output: string;
  message: string;
}

export interface CodeExecutionResult {
  success: boolean;
  stdout: string;
  stderr: string;
  execution_time: number;
  language: string;
  exercise_id: number;
  memory_used: number;
  test_case_results: TestCaseResult[];
}

export interface CodeExecutionService {
  executeCode(request: CodeExecutionRequest): Promise<CodeExecutionResponse>;
  getExecutionResult(taskId: string): Promise<CodeExecutionResult>;
}
