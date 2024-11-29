<script setup lang="ts">
import type {Exercise} from "@cortex/shared/types";
import { Check, ChevronsUpDown, Send } from "lucide-vue-next";
import { h, ref } from "vue";
import MentorieImage from "../icons/MentorieImage.vue";
import { toTypedSchema } from "@vee-validate/zod";
import { useForm } from "vee-validate";
import { toast } from "../ui/toast";
import * as z from 'zod'
import { cn } from "../../lib/utils";

defineProps<{ exercise: Exercise }>();

const userMessage = ref('');

const areas = [
  { label: 'Java', value: 'java' },
  { label: 'Python', value: 'py' },
  { label: 'Typescripts', value: 'ts' },
  { label: 'Machine Learning', value: 'ml' },
  { label: 'Quality Assurance', value: 'qa' },
]

const formSchema = toTypedSchema(z.object({
  area: z.string({
    required_error: 'Porfavor seleccione un área.',
  }),

  razon: z.string()
    .min(10, {
      message: 'La razón debe tener al menos 10 caracteres.',
        })
        .max(160, {
      message: 'La razón no debe exceder los 160 caracteres.',
    }),
}))

const { handleSubmit, setFieldValue, values } = useForm({
  validationSchema: formSchema,
})

const onSubmit = handleSubmit((values) => {
  toast({
    title: 'You submitted the following values:',
    description: h('pre', { class: 'mt-2 w-[340px] rounded-md bg-slate-950 p-4' }, h('code', { class: 'text-white' }, JSON.stringify(values, null, 2))),
  })
})

</script>

<template>
  <div class="flex flex-col gap-4 p-3 sm:p-5 bg-muted/50 rounded-lg shadow-lg shadow-current/30 dark:shadow-sm overflow-hidden h-full my-4 mx-7 rounded-corners-gradient-borders">
    <h2 class="text-xl font-semibold">Conéctate con un Mentor</h2>
    <div class="w-full flex justify-center">
      <MentorieImage class="max-w-48"/>
    </div>
    <form class="space-y-6" @submit="onSubmit">
    <FormField v-slot="{ componentField }" name="area">
      <FormItem class="flex flex-col">
        <FormLabel>Área</FormLabel>
        <Popover>
          <PopoverTrigger as-child>
            <FormControl>
              <Button
                variant="outline"
                role="combobox"
                :class="cn('w-full h-fit justify-between', !values.area && 'text-muted-foreground text-wrap text-start')"
                v-bind="componentField"
              >
                {{ values.area ? areas.find(
                  (area) => area.value === values.area,
                )?.label : 'Selecciona la área solicitada...' }}
                <ChevronsUpDown class="ml-2 h-4 w-4 shrink-0 opacity-50" />
              </Button>
            </FormControl>
          </PopoverTrigger>
          <PopoverContent class="max-w-72 p-0">
            <Command>
              <CommandInput placeholder="Buscar área..." />
              <CommandEmpty>Nothing found.</CommandEmpty>
              <CommandList>
                <CommandGroup>
                  <CommandItem
                    v-for="area in areas"
                    :key="area.value"
                    :value="area.label"
                    @select="() => {
                      setFieldValue('area', area.value)
                    }"
                  >
                    <Check
                      :class="cn('mr-2 h-4 w-4', area.value === values.area ? 'opacity-100' : 'opacity-0')"
                    />
                    {{ area.label }}
                  </CommandItem>
                </CommandGroup>
              </CommandList>
            </Command>
          </PopoverContent>
        </Popover>
        <FormDescription class="text-xs">
          Selecciona el área en la que necesitas ayuda. Esto nos permitirá conectarte con el mentor más adecuado para resolver tus dudas.
        </FormDescription>
        <FormMessage />
      </FormItem>
    </FormField>

    <FormField v-slot="{ componentField }" name="razon">
      <FormItem>
        <FormLabel>Razón de la Solicitud</FormLabel>
        <FormControl>
          <Textarea
            placeholder="Explícanos cómo podemos apoyarte..."
            class="resize-none"
            v-bind="componentField"
          />
        </FormControl>
        <FormDescription class="text-xs">
          Esto ayuda al mentor a prepararse y brindarte el mejor apoyo posible.
        </FormDescription>
        <FormMessage />
      </FormItem>
    </FormField>

    <div class="flex w-full justify-end">
      <Button type="submit">
        Solicitar Mentoría
      </Button>
    </div>
  </form>
  </div>

  <div
      class="flex items-start justify-between px-3 sm:px-6 py-2 shadow my-4 mx-7 transition-all duration-200 bg-background"
      :class="[userMessage.split('\n').length > 2 || userMessage.length > 50 ? 'rounded-3xl' : 'rounded-full']"
    >
      <Textarea
      v-model="userMessage"
      placeholder="Escribe tu mensaje aquí..."
      class="w-full bg-transparent border-none text-sm sm:text-base text-muted-foreground outline-none min-h-[24px] max-h-[200px] h-fit resize-none py-0 focus:ring-transparent focus-visible:ring-transparent overflow-y-auto"
      @input="$event.target.style.height = 'auto'; $event.target.style.height = $event.target.scrollHeight + 'px'"
      />
      <Button
      size="icon"
      class="flex bg-background shadow-inherit shadow-lg hover:bg-foreground/20 mt-1"
      >
      <Send class="h-5 cursor-pointer text-foreground flex-shrink-0" />
      </Button>
    </div>
</template>

<style scoped>
.rounded-corners-gradient-borders {
  border: double 4px transparent;
  border-radius: 20px;
  background-image: linear-gradient(hsl(var(--background)), hsl(var(--background))), radial-gradient(circle at top left, #92D2DD,hsl(var(--primary)));
  background-origin: border-box;
  background-clip: padding-box, border-box;
}
</style>