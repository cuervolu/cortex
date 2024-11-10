import { useForm } from 'vee-validate';
import { toTypedSchema } from '@vee-validate/zod';
import * as z from 'zod';

export const createBaseSchema = () => ({
  title: z.string()
  .min(3, 'El título debe tener al menos 3 caracteres')
  .max(255, 'El título no puede exceder 255 caracteres'),
  description: z.string()
  .min(10, 'La descripción debe tener al menos 10 caracteres')
  .max(5000, 'La descripción no puede exceder 5000 caracteres'),
  tagNames: z.array(z.string())
  .min(1, 'Debes agregar al menos una etiqueta')
  .max(10, 'No puedes agregar más de 10 etiquetas'),
  is_published: z.boolean()
});

export interface BaseFormValues {
  title: string;
  description: string;
  tagNames: string[];
  is_published: boolean;
}

export function useEducationalForm<T extends BaseFormValues>(
    additionalSchema = {},
    initialValues?: Partial<T>
) {
  const schema = toTypedSchema(z.object({
    ...createBaseSchema(),
    ...additionalSchema
  }));

  const form = useForm<T>({
    validationSchema: schema,
    initialValues: {
      title: '',
      description: '',
      tagNames: [],
      is_published: false,
      ...initialValues
    } as T
  });

  const handleEditorUpdate = (content: string) => {
    form.setFieldValue('description', content);
  };

  return {
    form,
    handleEditorUpdate
  };
}