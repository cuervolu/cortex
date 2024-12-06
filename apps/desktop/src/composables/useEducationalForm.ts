import { type Path, type PathValue, useForm } from 'vee-validate';
import { toTypedSchema } from '@vee-validate/zod';
import * as z from 'zod';
import type { PartialDeep } from 'type-fest';
import type { ContentType } from "~/types";

// Base schema genérico con un método para personalizar tags
export const createBaseSchema = (allowTags = true) => {
  const baseSchema = {
    title: z.string()
      .min(3, 'El título debe tener al menos 3 caracteres')
      .max(255, 'El título no puede exceder 255 caracteres'),
    description: z.string()
      .min(10, 'La descripción debe tener al menos 10 caracteres')
      .max(5000, 'La descripción no puede exceder 5000 caracteres'),
    is_published: z.boolean()
  };

  // Agregamos tags condicionalmente
  return allowTags 
    ? {
        ...baseSchema,
        tagNames: z.array(z.string())
          .min(1, 'Debes agregar al menos una etiqueta')
          .max(10, 'No puedes agregar más de 10 etiquetas')
      }
    : baseSchema;
};

export interface BaseFormValues {
  description: string;
  is_published: boolean;
  tagNames?: string[];  // Opcional para manejar módulos
}

export interface RoadmapFormValues extends BaseFormValues {
  title: string;
  tagNames: string[];  // Requerido para roadmaps
  image_url?: string;
}

export interface CourseModuleFormValues extends BaseFormValues {
  name: string;
  display_order?: number;
}

export function useEducationalForm<T extends RoadmapFormValues | CourseModuleFormValues>(
  contentType: ContentType,
  additionalSchema: Record<string, never> = {},
  initialValues?: PartialDeep<T>
) {
  const baseInitialValues: PartialDeep<BaseFormValues> = {
    description: '',
    is_published: false,
    ...(contentType !== 'module' ? { tagNames: [] } : {})  // Solo agrega tagNames si no es módulo
  };

  const typeInitialValues = contentType === 'roadmap'
    ? { title: '' }
    : { name: '' };

  const combinedInitialValues = {
    ...baseInitialValues,
    ...typeInitialValues,
    ...(initialValues || {})
  } as PartialDeep<T>;

  // Crea el schema dinámicamente basado en el tipo de contenido
  const schema = toTypedSchema(z.object({
    ...createBaseSchema(contentType !== 'module'),  // No tags para módulos
    ...(contentType === 'roadmap' ? { title: createBaseSchema().title } : { name: z.string().min(3).max(255) }),
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