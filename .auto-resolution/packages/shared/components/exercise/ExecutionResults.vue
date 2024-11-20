<script setup lang="ts">
import { Terminal, CheckCircle2, XCircle, Loader2 } from 'lucide-vue-next'
import type { CodeExecutionResult } from "@cortex/shared/types/code-execution";

interface Props {
  result: CodeExecutionResult | null
  loading?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  result: null,
  loading: false
})

const getStatusColor = computed(() => {
  if (!props.result) return 'bg-muted'
  // Usamos success en lugar de test_case_results para el estado general
  return props.result.success
    ? 'bg-green-500/10 border-green-500/20'
    : 'bg-destructive/10 border-destructive/20'
})

const getStatusIcon = computed(() => {
  if (!props.result) return Terminal
  // Usamos success en lugar de test_case_results
  return props.result.success ? CheckCircle2 : XCircle
})

// Computed para el mensaje de estado general
const getStatusMessage = computed(() => {
  if (!props.result) return 'No hay resultados'
  return props.result.success
    ? 'Todas las pruebas pasaron exitosamente'
    : 'Algunas pruebas fallaron'
})
</script>

<template>
  <div class="p-4 space-y-4">
    <!-- Estado de carga -->
    <Alert v-if="loading" class="bg-blue-500/10 border-blue-500/20">
      <Loader2 class="h-4 w-4 animate-spin" />
      <AlertTitle>Ejecutando pruebas...</AlertTitle>
      <AlertDescription>
        Esto puede tomar unos segundos...
      </AlertDescription>
    </Alert>

    <!-- Resultados -->
    <Alert v-else-if="result" :class="getStatusColor">
      <component :is="getStatusIcon" class="h-4 w-4" />
      <AlertTitle>
        Resultados de las pruebas
      </AlertTitle>
      <AlertDescription>
        {{ getStatusMessage }}
      </AlertDescription>
    </Alert>

    <!-- Sin resultados -->
    <Alert v-else class="bg-muted">
      <Terminal class="h-4 w-4" />
      <AlertTitle>No hay resultados</AlertTitle>
      <AlertDescription>
        Ejecuta el código para ver los resultados de las pruebas
      </AlertDescription>
    </Alert>

    <!-- Detalles de la ejecución -->
    <Accordion v-if="result" type="single" collapsible class="w-full">
      <AccordionItem
        v-for="(testCase, index) in result.test_case_results"
        :key="index"
        :value="'test-' + index">
        <AccordionTrigger>
          Prueba {{ index + 1 }}
          <Badge :variant="testCase.passed ? 'success' : 'destructive'" class="ml-2">
            {{ testCase.passed ? 'Pasó' : 'Falló' }}
          </Badge>
        </AccordionTrigger>
        <AccordionContent>
          <div class="space-y-2 text-sm">
            <div class="grid grid-cols-2 gap-2">
              <div class="font-semibold">Entrada:</div>
              <div class="font-mono bg-muted p-1 rounded">{{ testCase.input || 'N/A' }}</div>

              <div class="font-semibold">Salida esperada:</div>
              <div class="font-mono bg-muted p-1 rounded">{{ testCase.expected_output }}</div>

              <div class="font-semibold">Salida actual:</div>
              <div class="font-mono bg-muted p-1 rounded">{{ testCase.actual_output }}</div>
            </div>

            <div v-if="testCase.message" class="mt-2 text-sm text-muted-foreground">
              {{ testCase.message }}
            </div>
          </div>
        </AccordionContent>
      </AccordionItem>

      <AccordionItem value="details">
        <AccordionTrigger>Detalles técnicos</AccordionTrigger>
        <AccordionContent>
          <div class="space-y-2 text-sm">
            <!-- Output (both standard and error) -->
            <div v-if="result.stderr" class="mb-4">
              <div class="text-sm font-medium mb-2">Output:</div>
              <div class="font-mono text-sm whitespace-pre-wrap bg-muted/50 p-4 rounded-lg border">
              {{ result.stderr }}
              </div>
            </div>
            <div v-if="result.stdout" class="mb-4">
              <div class="text-sm font-medium mb-2">Output:</div>
              <div class="font-mono text-sm whitespace-pre-wrap bg-muted/50 p-4 rounded-lg border">
              {{ result.stdout }}
              </div>
            </div>

            <!-- Performance metrics -->
            <div class="grid grid-cols-2 gap-4 mt-4">
              <div class="flex items-center space-x-2 p-3 rounded-lg bg-muted/30">
              <span class="text-xs font-medium">Execution Time:</span>
              <span class="text-xs font-mono">{{ result.execution_time }}ms</span>
              </div>
              <div class="flex items-center space-x-2 p-3 rounded-lg bg-muted/30">
              <span class="text-xs font-medium">Memory Usage:</span>
              <span class="text-xs font-mono">{{ result.memory_used }}KB</span>
              </div>
            </div>
            </div>
        </AccordionContent>
      </AccordionItem>
    </Accordion>
  </div>
</template>

