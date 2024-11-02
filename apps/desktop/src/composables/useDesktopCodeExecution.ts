
import { useCodeExecution } from '@cortex/shared/composables/useCodeExecution';
import { useErrorHandler } from '~/composables/useErrorHandler';
import {DesktopCodeExecutionService} from "~/services/desktop-code-execution.service";

export function useDesktopCodeExecution() {
  const service = new DesktopCodeExecutionService();
  const errorHandler = useErrorHandler();

  return useCodeExecution(service, {
    errorHandler,
    maxAttempts: 10,
    pollingInterval: 1000
  });
}