<script setup lang="ts">
import { format } from 'date-fns';
import { es } from 'date-fns/locale';
import { Loader, BookOpen } from "lucide-vue-next";
import { useToast } from "@cortex/shared/components/ui/toast";
import CourseActionsMenu from "~/components/courses/CourseActionsMenu.vue";

const route = useRoute();
const roadmapId = Number(route.params.id);
const { courses, loading, fetchRoadmapCourses } = useCourses();
const { toast } = useToast();

const currentPage = ref(0);
const pageSize = ref(10);
const sortBy = ref(['displayOrder:asc']);

const loadCourses = async () => {
  try {
    await fetchRoadmapCourses(roadmapId, {
      page: currentPage.value,
      size: pageSize.value,
      sort: sortBy.value
    });
  } catch (err) {
    toast({
      title: "Error",
      description: "Error al cargar los cursos",
      variant: "destructive"
    });
  }
};
const formatDate = (date: string) => {
  return format(new Date(date), 'PPP', { locale: es });
};

const handlePageChange = async (page: number) => {
  currentPage.value = page - 1;
  await loadCourses();
};
const handleViewLessons = async (courseId: number) => {
  await navigateTo(`/admin/courses/${courseId}/lessons`)
}

const handleViewModules = async (courseId: number) => {
  await navigateTo(`/admin/courses/${courseId}/modules`)
}

const handleCreateLesson = async (courseId: number) => {
  await navigateTo(`/admin/courses/${courseId}/lessons/create`)
}

const handleCreateModule = async (courseId: number) => {
  await navigateTo(`/admin/courses/${courseId}/modules/create`)
}

const handleTogglePublish = async (courseId: number) => {
  // Implementar lógica de publicación
}

const handleEdit = async (courseId: number) => {
  await navigateTo(`/admin/courses/${courseId}`)
}

const handleDelete = async (courseId: number) => {
  try {
    // Implementar lógica de eliminación
    await loadCourses()
    toast({
      title: "Éxito",
      description: "Curso eliminado correctamente"
    })
  } catch (err) {
    toast({
      title: "Error",
      description: "Error al eliminar el curso",
      variant: "destructive"
    })
  }
}

onMounted(loadCourses);
</script>

<template>
  <Card class="w-full">
    <CardHeader>
      <CardTitle class="text-2xl font-bold">Cursos</CardTitle>
      <CardDescription>
        Cursos asignados al roadmap
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
                  <span class="ml-2 text-sm text-muted-foreground">Cargando cursos...</span>
                </div>
              </TableCell>
            </TableRow>

            <template v-else-if="courses?.content?.length">
              <TableRow
                  v-for="course in courses.content"
                  :key="course.id"
                  class="hover:bg-muted/50 transition-colors"
              >
                <TableCell class="font-medium">
                  <div class="flex items-center gap-2">
                    <Avatar class="h-8 w-8">
                      <AvatarImage :src="course.image_url || ''"/>
                      <AvatarFallback><BookOpen class="h-4 w-4" /></AvatarFallback>
                    </Avatar>
                    <div>
                      <p class="font-semibold">{{ course.name }}</p>
                      <p class="text-sm text-muted-foreground md:hidden">
                        {{ course.description }}
                      </p>
                    </div>
                  </div>
                </TableCell>

                <TableCell class="hidden md:table-cell">
                  <p class="text-sm text-muted-foreground line-clamp-2">
                    {{ course.description }}
                  </p>
                </TableCell>

                <TableCell class="hidden sm:table-cell text-muted-foreground">
                  {{ formatDate(course.created_at) }}
                </TableCell>

                <TableCell class="text-center">
                  <Badge :variant="course.is_published ? 'default' : 'secondary'">
                    {{ course.is_published ? 'Publicado' : 'Borrador' }}
                  </Badge>
                </TableCell>

                <TableCell class="text-center">
                  {{ course.display_order || 0 }}
                </TableCell>
                <TableCell>
                  <CourseActionsMenu
                      :course-id="course.id"
                      :is-published="course.is_published"
                      @view-lessons="handleViewLessons"
                      @view-modules="handleViewModules"
                      @create-lesson="handleCreateLesson"
                      @create-module="handleCreateModule"
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
                  <p>No hay cursos disponibles</p>
                </div>
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </div>

      <!-- Pagination -->
      <div v-if="courses" class="flex flex-col gap-2 sm:flex-row items-center justify-between px-2 py-4">
       <span class="text-sm text-muted-foreground order-2 sm:order-1">
         Mostrando {{ courses.content?.length ?? 0 }} de {{ courses.total_elements ?? 0 }} resultados
       </span>

        <Pagination
            v-slot="{ page }"
            :total="courses.total_pages"
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