import { type Path, type PathValue, useForm } from 'vee-validate';
import { toTypedSchema } from '@vee-validate/zod';
import * as z from 'zod';
import type { PartialDeep } from 'type-fest';
import type {ContentType} from "~/types";

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
  description: string;
  tagNames: string[];
  is_published: boolean;
}

export interface RoadmapFormValues extends BaseFormValues {
  title: string;
  image_url?: string;
}

export interface CourseModuleFormValues extends BaseFormValues {
  name: string;
}

const createRoadmapSchema = () => ({
  ...createBaseSchema(),
  title: z.string()
  .min(3, 'El título debe tener al menos 3 caracteres')
  .max(255, 'El título no puede exceder 255 caracteres')
});

const createCourseModuleSchema = () => ({
  ...createBaseSchema(),
  name: z.string()
  .min(3, 'El nombre debe tener al menos 3 caracteres')
  .max(255, 'El nombre no puede exceder 255 caracteres')
});


export function useEducationalForm<T extends RoadmapFormValues | CourseModuleFormValues>(
    contentType: ContentType,
    additionalSchema: Record<string, never> = {},
    initialValues?: PartialDeep<T>
): {
  form: ReturnType<typeof useForm<T>>;
  handleEditorUpdate: (content: string) => void;
} {
  const baseInitialValues: PartialDeep<BaseFormValues> = {
    description: '',
    tagNames: [],
    is_published: false
  };

  const typeInitialValues = contentType === 'roadmap'
      ? { title: '' }
      : { name: '' };

  const combinedInitialValues = {
    ...baseInitialValues,
    ...typeInitialValues,
    ...(initialValues || {})
  } as PartialDeep<T>;

  const schema = toTypedSchema(z.object({
    ...createBaseSchema(),
    ...additionalSchema
  }));

  const form = useForm<T>({
    validationSchema: schema,
    initialValues: combinedInitialValues
  });

  const handleEditorUpdate = (content: string) => {
    const path = 'description' as Path<T>;
    form.setFieldValue(path, content as PathValue<T, typeof path>);
  };

  return {
    form,
    handleEditorUpdate
  };
}