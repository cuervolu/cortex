<script setup lang="ts">
import ExecutionResults from './ExecutionResults.vue'
import type {CodeExecutionResult} from "../../types";

interface Props {
  result: CodeExecutionResult | null
  loading?: boolean
}

defineProps<Props>()
</script>

<template>
  <ScrollArea class="h-full">
    <ExecutionResults :result="result" :loading="loading" />
  </ScrollArea>
</template>
