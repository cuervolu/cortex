import { invoke } from '@tauri-apps/api/core';
import { debug, error } from '@tauri-apps/plugin-log';
import type { Module, ModuleCreateRequest, ModuleUpdateRequest, PaginatedModules, RoadmapModule } from '@cortex/shared/types';
import { AppError } from '@cortex/shared/types';
import TurndownService from 'turndown';
import type { CourseModuleFormValues } from './useEducationalForm';

export function useModules() {
    const modules = ref<PaginatedModules | null>(null)
    const module = ref<Module | null>(null);
    const loading = ref(true);
    const errorHandler = useDesktopErrorHandler();

    const turndownService = new TurndownService({
        codeBlockStyle: 'fenced',
        headingStyle: 'atx'
    })

    const fetchCourseModules = async (
        courseSlug: string,
        params: { page?: number; size?: number; sort?: string[] } = {}
    ) => {
        try {
            loading.value = true
            const response = await invoke<PaginatedModules>('get_course_modules', {
                slug: courseSlug,
                page: params.page,
                size: params.size,
                sort: params.sort
            })
            modules.value = response
            return response
        } catch (err) {
            await error(`Failed to fetch course modules: ${err}`)
            throw err
        } finally {
            loading.value = false
        }
    }

    const fetchModule = async (slug: string) => {
        try {
            loading.value = true;
            await debug(`Fetching module: ${slug}`);

            const response = await invoke<Module>('get_module', { slug });

            if (!response) {
                throw new AppError('Module not found', {
                    statusCode: 404,
                    data: { slug },
                });
            }

            module.value = response;
            await debug(`Successfully fetched module: ${response.name}`);
            return response;
        } catch (err) {
            const isInvokeError = err instanceof Error && err.message.includes('invoke');
            await errorHandler.handleError(err, {
                statusCode: isInvokeError ? 500 : 404,
                fatal: isInvokeError,
                data: { slug },
            });
        } finally {
            loading.value = false;
        }
    };

    const fetchModuleById = async (id: number) => {
        try {
            loading.value = true
            await debug(`Fetching module by ID: ${id}`)
            const response = await invoke<Module>('get_module_by_id', { id })

            if (!response) {
                throw new Error('Module not found')
            }

            module.value = response
            return response
        } catch (err) {
            await error(`Failed to fetch module by ID: ${err}`)
            throw err
        } finally {
            loading.value = false
        }
    }

    const createModule = async (request: ModuleCreateRequest, imagePath: string | null, courseId: number) => {
        try {
            loading.value = true
            await debug(`Creating module: ${request.name}. Publish: ${request.is_published}`)

            const createdModule = await invoke<RoadmapModule>('create_new_module', {
                request: {
                    course_id: courseId,
                    name: request.name,
                    description: request.description,
                    is_published: request.is_published,
                    display_order: 1
                }
            })

            if (imagePath && createdModule.id) {
                await debug(`Uploading module image: ${imagePath}`)
                await invoke('upload_module_image_command', {
                    moduleId: createdModule.id,
                    imagePath,
                    altText: request.name
                })
            }

            await debug(`Assigning module ${createdModule.id} to course ${courseId}`)
            // Note: You might need to update this command based on your actual implementation
            await invoke('update_course_command', {
                id: courseId,
                request: {
                    module_ids: [createdModule.id]
                }
            })

            await debug(`Successfully created and assigned module: ${createdModule.id}`)
            return createdModule
        } catch (err) {
            await error(`Failed to create/assign module: ${err}`)
            throw err
        } finally {
            loading.value = false
        }
    }

    const updateModule = async (id: number, request: ModuleUpdateRequest, imagePath: string | null) => {
        try {
            loading.value = true
            await debug(`Updating module: ${id}. Publish: ${request.is_published}`)

            const updatedModule = await invoke<RoadmapModule>('update_module_command', {
                id,
                request
            })

            if (imagePath) {
                await invoke('upload_module_image_command', {
                    moduleId: id,
                    imagePath,
                    altText: request.name
                })
            }

            await debug(`Successfully updated module: ${id}`)
            return updatedModule
        } catch (err) {
            await error(`Failed to update module: ${err}`)
            throw err
        } finally {
            loading.value = false
        }
    }

    const transformFormToRequest = (formData: CourseModuleFormValues, courseId?: number): ModuleCreateRequest => {
        if (!formData.name && !formData.title) {
            throw new AppError('Name is required', {
                statusCode: 400,
                data: { formData }
            })
        }

        return {
            course_id: courseId,
            name: formData.name || formData.title || '', // Handle both name and title
            description: turndownService.turndown(formData.description),
            is_published: formData.is_published,
            display_order: formData.display_order || 0
        }
    }

    return {
        modules,
        module,
        loading,
        fetchCourseModules,
        fetchModule,
        fetchModuleById,
        createModule,
        updateModule,
        transformFormToRequest
    };
}
