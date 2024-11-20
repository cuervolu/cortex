export type ContentType = 'roadmap' | 'course' | 'module';

export interface ContentCommandConfig {
  createCommand: string;
  uploadImageCommand: string;
  redirectPath: string;
}

export const CONTENT_COMMANDS: Record<ContentType, ContentCommandConfig> = {
  roadmap: {
    createCommand: 'create_new_roadmap',
    uploadImageCommand: 'upload_roadmap_image_command',
    redirectPath: '/explore'
  },
  course: {
    createCommand: 'create_new_course',
    uploadImageCommand: 'upload_course_image_command',
    redirectPath: '/courses'
  },
  module: {
    createCommand: 'create_new_module',
    uploadImageCommand: 'upload_module_image_command',
    redirectPath: '/modules'
  }
};