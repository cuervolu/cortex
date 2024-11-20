import {invoke} from "@tauri-apps/api/core";
import {debug} from "@tauri-apps/plugin-log";
import type {
  RoadmapCreateRequest,
  CourseCreateRequest,
  ModuleCreateRequest,
  Roadmap,
  Course,
  Module
  , AppError
} from '@cortex/shared/types';
import {CONTENT_COMMANDS, type ContentType} from "~/types";

type ContentCreateRequest = RoadmapCreateRequest | CourseCreateRequest | ModuleCreateRequest;
type ContentResponse = Roadmap | Course | Module;

export function useEducationalContent(contentType: ContentType) {
  const router = useRouter();
  const isLoading = ref(false);
  const error = ref<AppError | null>(null);
  const commands = CONTENT_COMMANDS[contentType];

  const createWithImage = async (
      formData: ContentCreateRequest,
      imagePath: string | null
  ) => {
    isLoading.value = true;
    error.value = null;

    try {
      // First we create the content
      await debug(`Creating ${contentType}...`);
      const content = await invoke<ContentResponse>(commands.createCommand, {
        request: formData
      });

      // If we have an image, upload it
      if (imagePath && content.id) {
        await debug(`Uploading image for ${contentType} ${content.id}...`);
        await invoke(commands.uploadImageCommand, {
          roadmapId: content.id,
          imagePath,
          altText: 'title' in formData ? formData.title : formData.name
        });
      }

      await debug(`${contentType} creation completed successfully`);

      // Redirect to the appropriate view based on content type
      const slug = 'slug' in content ? content.slug : null;
      await router.push(`${commands.redirectPath}/${slug || content.id}`);

      return content;
    } catch (e) {
      error.value = e as AppError;
      await debug(`Error in ${contentType} creation: ${e}`);
      throw e;
    } finally {
      isLoading.value = false;
    }
  };

  return {
    isLoading,
    error,
    createWithImage
  };
}