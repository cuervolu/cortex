<script setup lang="ts">
import { format } from 'date-fns';
import { es } from 'date-fns/locale';
import { Loader, BookOpen } from "lucide-vue-next";
import { useToast } from "@cortex/shared/components/ui/toast";
import ModuleActionsMenu from "~/components/modules/ModuleActionsMenu.vue";

const route = useRoute();
const courseSlug = String(route.params.id);
const { modules, loading, fetchCourseModules } = useModules();
const { toast } = useToast();

const currentPage = ref(0);
const pageSize = ref(10);
const sortBy = ref(['displayOrder:asc']);

const loadModules = async () => {
    try {
        await fetchCourseModules(courseSlug, {
        page: currentPage.value,
        size: pageSize.value,
        sort: sortBy.value
        });
    } catch (err) {
        console.log(err)
        toast({
        title: "Error",
        description: "Error al cargar los modulos",
        variant: "destructive"
        });
    }
};
const formatDate = (date: string) => {
    return format(new Date(date), 'PPP', { locale: es });
};

const handlePageChange = async (page: number) => {
    currentPage.value = page - 1;
    await loadModules();
};
const handleViewLessons = async (moduleId: number) => {
    await navigateTo(`/admin/modules/${moduleId}/lessons`)
}

const handleCreateLesson = async (moduleId: number) => {
    await navigateTo({
        path: `/admin/lessons/create`,
        query: { moduleId }
    })
}

const handleTogglePublish = async (moduleId: number) => {
  // Implementar lógica de publicación
}

const handleEdit = async (moduleId: number) => {
    await navigateTo(`/admin/modules/${moduleId}`)
}

const handleDelete = async (moduleId: number) => {
    try {
        // Implementar lógica de eliminación
        await loadModules()
        toast({
        title: "Éxito",
        description: "Modulo eliminado correctamente"
        })
    } catch (err) {
        toast({
        title: "Error",
        description: "Error al eliminar el modulo",
        variant: "destructive"
        })
    }
}

onMounted(loadModules);
</script>

<template>
    <Card class="w-full">
        <CardHeader>
            <CardTitle class="text-2xl font-bold">Modulos</CardTitle>
            <CardDescription>
                Modulos asignados al curso
            </CardDescription>
        </CardHeader>

        <CardContent>
            <div class="rounded-md border">
                <Table>
                    <TableHeader>
                        <TableRow class="bg-muted/50">
                        <TableHead class="w-[250px]">Nombre</TableHead>
                        <TableHead class="hidden md:table-cell">Descripción</TableHead>
                        <TableHead class="hidden sm:table-cell w-[180px]">Fecha de creación</TableHead>
                        <TableHead class="w-[100px] text-center">Estado</TableHead>
                        <TableHead class="w-[100px]">Orden</TableHead>
                        <TableHead class="w-[50px]"></TableHead>
                        </TableRow>
                    </TableHeader>

                    <TableBody>
                        <TableRow v-if="loading">
                            <TableCell colspan="5" class="h-32">
                                <div class="flex h-full w-full items-center justify-center">
                                <Loader class="h-6 w-6"/>
                                <span class="ml-2 text-sm text-muted-foreground">Cargando modulos...</span>
                                </div>
                            </TableCell>
                        </TableRow>

                        <template v-else-if="modules?.content?.length">
                            <TableRow
                                v-for="module in modules.content"
                                :key="module.id"
                                class="hover:bg-muted/50 transition-colors"
                            >
                                <TableCell class="font-medium">
                                <div class="flex items-center gap-2">
                                    <Avatar class="h-8 w-8">
                                    <AvatarImage :src="module.imageUrl || ''"/>
                                    <AvatarFallback><BookOpen class="h-4 w-4" /></AvatarFallback>
                                    </Avatar>
                                    <div>
                                    <p class="font-semibold">{{ module.name }}</p>
                                    <p class="text-sm text-muted-foreground md:hidden">
                                        {{ module.description }}
                                    </p>
                                    </div>
                                </div>
                                </TableCell>

                                <TableCell class="hidden md:table-cell">
                                <p class="text-sm text-muted-foreground line-clamp-2">
                                    {{ module.description }}
                                </p>
                                </TableCell>


                                <TableCell class="hidden sm:table-cell text-muted-foreground">
                                {{ formatDate(module.created_at) }}
                                </TableCell>

                                <TableCell class="text-center">
                                <Badge :variant="module.is_published ? 'default' : 'secondary'">
                                    {{ module.is_published ? 'Publicado' : 'Borrador' }}
                                </Badge>
                                </TableCell>

                                <TableCell class="text-center">
                                    {{ module.display_order || 0 }}
                                </TableCell>
                                <TableCell>
                                    <ModuleActionsMenu
                                        :module-id="module.id"
                                        :is-published="module.is_published"
                                        @view-lessons="handleViewLessons"
                                        @create-lesson="handleCreateLesson"
                                        @toggle-publish="handleTogglePublish"
                                        @edit="handleEdit"
                                        @delete="handleDelete"
                                    />
                                </TableCell>
                            </TableRow>
                        </template>

                        <TableRow v-else>
                        <TableCell colspan="5" class="h-32 text-center">
                            <div class="flex flex-col items-center justify-center text-muted-foreground">
                            <BookOpen class="h-8 w-8 mb-2"/>
                            <p>No hay modulos disponibles</p>
                            </div>
                        </TableCell>
                        </TableRow>
                    </TableBody>
                </Table>
            </div>

        <!-- Pagination -->
        <div v-if="modules" class="flex flex-col gap-2 sm:flex-row items-center justify-between px-2 py-4">
        <span class="text-sm text-muted-foreground order-2 sm:order-1">
            Mostrando {{ modules.content?.length ?? 0 }} de {{ modules.total_elements ?? 0 }} resultados
        </span>

            <Pagination
                v-slot="{ page }"
                :total="modules.total_pages"
                :sibling-count="1"
                show-edges
                :default-page="currentPage + 1"
                @update:page="handlePageChange"
                class="order-1 sm:order-2"
            >
            <PaginationList v-slot="{ items }" class="flex items-center gap-1">
                <div class="hidden sm:flex items-center gap-1">
                <PaginationFirst/>
                <PaginationPrev/>
                </div>

                <template v-for="(item, index) in items">
                <PaginationListItem
                    v-if="item.type === 'page'"
                    :key="index"
                    :value="item.value"
                    as-child
                >
                    <Button
                        class="h-8 w-8 sm:h-10 sm:w-10 p-0"
                        :variant="item.value === page ? 'default' : 'outline'"
                    >
                    {{ item.value }}
                    </Button>
                </PaginationListItem>
                <PaginationEllipsis v-else :key="item.type" :index="index"/>
                </template>

                <div class="hidden sm:flex items-center gap-1">
                <PaginationNext/>
                <PaginationLast/>
                </div>
            </PaginationList>
            </Pagination>
        </div>
        </CardContent>
    </Card>
</template>