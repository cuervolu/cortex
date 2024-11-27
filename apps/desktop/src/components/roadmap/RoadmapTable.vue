<script setup lang="ts">
import { useToast } from "@cortex/shared/components/ui/toast";
import {Loader, FolderIcon} from "lucide-vue-next";
import RoadmapActionsMenu from "~/components/roadmap/RoadmapActionsMenu.vue";

const {
  paginatedRoadmaps,
  fetchRoadmaps,
  deleteRoadmap,
  loading
} = useRoadmaps()

// Pagination state
const currentPage = ref(0)
const pageSize = ref(10)
const sortBy = ref(['createdAt:desc'])
const showDeleteDialog = ref(false)
const deletingRoadmapId = ref<number | null>(null)
  const { toast } = useToast()
// Load initial data
onMounted(async () => {
  await loadRoadmaps()
})

// Watch for pagination changes
watch([currentPage, pageSize, sortBy], async () => {
  await loadRoadmaps()
})

const loadRoadmaps = async () => {
  await fetchRoadmaps({
    page: currentPage.value,
    size: pageSize.value,
    sort: sortBy.value,
    isAdmin: true
  })
}

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('es-ES', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// Handle page change
const handlePageChange = async (page: number) => {
  currentPage.value = page - 1
  await loadRoadmaps()
}

// Handlers para las acciones del menú
const handleCreateCourse = async (roadmapId: number) => {
  await navigateTo({
    path: '/admin/courses/create',
    query: { roadmapId }
  })
}

const handleCreateModule = async (roadmapId: number) => {
  // Implementar lógica
  console.log('Crear módulo para roadmap:', roadmapId)
}

const handleCreateLesson = async (roadmapId: number) => {
  // Implementar lógica
  console.log('Crear lección para roadmap:', roadmapId)
}

const handleTogglePublish = async (roadmapId: number) => {
  // Implementar lógica
  console.log('Toggle publicación para roadmap:', roadmapId)
}

const handleAssignCourses = async (roadmapId: number) => {
  await navigateTo(`/admin/roadmaps/assign-courses/${roadmapId}`);
}

const handleEdit = async (roadmapSlug: string) => {
  await navigateTo(`/admin/roadmaps/${roadmapSlug}`)
}

const handleDelete = async (roadmapId: number) => {
  const success = await deleteRoadmap(roadmapId)
  
  if (success) {
    toast({
      title: 'Éxito',
      description: 'Roadmap eliminado correctamente'
    })
  }
}
</script>

<template>
  <Card class="w-full">
    <CardHeader>
      <CardTitle class="text-2xl font-bold">Roadmaps</CardTitle>
      <CardDescription>
        Gestiona los roadmaps disponibles en la plataforma
      </CardDescription>
    </CardHeader>
    <CardContent>
      <div class="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow class="bg-muted/50">
              <TableHead class="w-[250px]">Título</TableHead>
              <TableHead class="hidden md:table-cell">Descripción</TableHead>
              <TableHead class="hidden sm:table-cell w-[180px]">Fecha de creación</TableHead>
              <TableHead class="w-[100px] text-center">Estado</TableHead>
              <TableHead class="w-[50px]"></TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            <TableRow v-if="loading">
              <TableCell colspan="5" class="h-32">
                <div class="flex h-full w-full items-center justify-center">
                  <Loader class="h-6 w-6"/>
                  <span class="ml-2 text-sm text-muted-foreground">Cargando roadmaps...</span>
                </div>
              </TableCell>
            </TableRow>
            <template v-else-if="paginatedRoadmaps?.content?.length">
              <TableRow
                  v-for="roadmap in paginatedRoadmaps.content"
                  :key="roadmap.id"
                  class="hover:bg-muted/50 transition-colors"
              >
                <TableCell class="font-medium">
                  <div class="flex items-center gap-2">
                    <Avatar class="h-8 w-8">
                      <AvatarImage :src="roadmap.image_url || ''"/>
                      <AvatarFallback>{{ roadmap.title.charAt(0) }}</AvatarFallback>
                    </Avatar>
                    <div>
                      <p class="font-semibold">{{ roadmap.title }}</p>
                      <p class="text-sm text-muted-foreground md:hidden">
                        {{ roadmap.description }}
                      </p>
                    </div>
                  </div>
                </TableCell>
                <TableCell class="hidden md:table-cell">
                  <p class="text-sm text-muted-foreground line-clamp-2">
                    {{ roadmap.description }}
                  </p>
                </TableCell>
                <TableCell class="hidden sm:table-cell text-muted-foreground">
                  {{ formatDate(roadmap.created_at) }}
                </TableCell>
                <TableCell class="text-center">
                  <Badge :variant="roadmap.is_published ? 'success' : 'secondary'">
                    {{ roadmap.is_published ? 'Publicado' : 'Borrador' }}
                  </Badge>
                </TableCell>
                <TableCell>
                  <RoadmapActionsMenu
                      :roadmap-id="roadmap.id"
                      :is-published="roadmap.is_published"
                      @create-course="handleCreateCourse"
                      @create-module="handleCreateModule"
                      @create-lesson="handleCreateLesson"
                      @toggle-publish="handleTogglePublish"
                      @assign-courses="handleAssignCourses"
                      @edit="handleEdit(roadmap.slug)"
                      @delete="handleDelete"
                  />
                </TableCell>
              </TableRow>
            </template>
            <TableRow v-else>
              <TableCell colspan="5" class="h-32 text-center">
                <div class="flex flex-col items-center justify-center text-muted-foreground">
                  <FolderIcon class="h-8 w-8 mb-2"/>
                  <p>No hay roadmaps disponibles</p>
                </div>
              </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </div>

      <!-- Pagination -->
      <div class="flex flex-col gap-2 sm:flex-row items-center justify-between px-2 py-4">
        <span class="text-sm text-muted-foreground order-2 sm:order-1">
          Mostrando {{
            paginatedRoadmaps?.content?.length ?? 0
          }} de {{ paginatedRoadmaps?.total_elements ?? 0 }} resultados
        </span>

        <Pagination
            v-if="paginatedRoadmaps"
            v-slot="{ page }"
            :total="paginatedRoadmaps.total_pages"
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