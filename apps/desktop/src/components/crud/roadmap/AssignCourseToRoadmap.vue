<script setup lang="ts">
import draggable from 'vuedraggable'
import { Search } from 'lucide-vue-next'
import { useCourseAssignment } from '~/composables/useCourseAssignment'
import { useRoadmaps } from '~/composables/useRoadmaps'
import { useDesktopErrorHandler } from '~/composables/useDesktopErrorHandler'
import {useToast} from "@cortex/shared/components/ui/toast";

const route = useRoute();
const roadmapId = computed(() => Number(route.params.id));
const searchQueryAssigned = ref('');
const searchQueryAvailable = ref('');
const isDragging = ref(false);

const { handleError } = useDesktopErrorHandler()
const { paginatedRoadmaps, fetchRoadmaps } = useRoadmaps()
const {
  selectedRoadmap,
  assignedCourses,
  availableCourses,
  isLoading,
  dragOptions,
  setRoadmap,
  loadMoreCourses,
  saveCourseAssignments
} = useCourseAssignment()
const { toast } = useToast();
onMounted(async () => {
  try {
    await fetchRoadmaps({ isAdmin: true });
    if (roadmapId.value) {
      const roadmap = paginatedRoadmaps.value?.content.find(r => r.id === roadmapId.value);
      if (roadmap) {
        await setRoadmap(roadmap);
      }
    }
  } catch (err) {
    await handleError(err);
  }
});

const handleRoadmapSelect = async (roadmapId: number) => {
  try {
    const roadmap = paginatedRoadmaps.value?.content.find(r => r.id === roadmapId)
    if (roadmap) {
      await setRoadmap(roadmap)
    }
  } catch (err) {
    await handleError(err)
  }
}

const handleSave = async () => {
  try {
    await saveCourseAssignments()
    toast({
      title: 'Curso asignado correctamente',
      description: 'Los cursos se han asignado correctamente al roadmap',
    });
  } catch (err) {
    await handleError(err)
  }
}
const handleScroll = async (type: 'assigned' | 'available', event: Event) => {
  const element = event.target as HTMLElement
  const bottom = element.scrollHeight - element.scrollTop === element.clientHeight

  if (bottom && !isLoading.value) {
    await loadMoreCourses(type)
  }
}
</script>

<template>
  <div class="flex flex-col w-full p-8">
    <div class="pb-5">
      <h2 class="text-3xl font-semibold">Asignar Cursos al Roadmap</h2>
    </div>

    <!-- Roadmap Selection -->
    <Card v-if="!roadmapId" class="mb-6">
      <CardHeader>
        <CardTitle>Seleccionar Roadmap</CardTitle>
      </CardHeader>
      <CardContent>
        <Select
            :disabled="isLoading"
            @update:model-value="(value) => handleRoadmapSelect(Number(value))"
        >
          <SelectTrigger>
            <SelectValue placeholder="Selecciona un roadmap" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem
                v-for="roadmap in paginatedRoadmaps?.content"
                :key="roadmap.id"
                :value="roadmap.id.toString()"
            >
              {{ roadmap.title }}
            </SelectItem>
          </SelectContent>
        </Select>
      </CardContent>
    </Card>

    <div class="flex flex-wrap gap-3">
      <!-- Selected Roadmap Info -->
      <Card v-if="selectedRoadmap" class="flex-1 min-w-[300px] xl:min-w-[370px] h-fit">
        <CardHeader>
          <h2 class="text-2xl font-semibold">{{ selectedRoadmap.title }}</h2>
        </CardHeader>
        <CardContent>
          <div class="flex flex-col gap-4">
            <img
                v-if="selectedRoadmap.image_url"
                :src="selectedRoadmap.image_url"
                :alt="selectedRoadmap.title"
                class="w-full h-[230px] object-cover rounded-lg"
            >
            <div class="flex gap-2 flex-wrap">
              <Badge
                  v-for="tag in selectedRoadmap.tag_names"
                  :key="tag"
                  class="text-nowrap"
              >
                {{ tag }}
              </Badge>
            </div>
          </div>
        </CardContent>
        <CardFooter class="flex flex-col">
          <div class="w-full">
            <span class="text-lg font-semibold text-start">
              Cursos Asignados: {{ assignedCourses.length }}
            </span>
          </div>
          <div class="w-full">
            <Button
                class="w-full mt-4"
                :disabled="isLoading || !assignedCourses.length"
                @click="handleSave"
            >
              Guardar
            </Button>
          </div>
        </CardFooter>
      </Card>

      <!-- Drag and Drop Area -->
      <div class="flex flex-wrap gap-5 justify-between w-full xl:w-3/5">
        <!-- Assigned Courses -->
        <div class="basis-[350px] bg-popover/80 border-secondary-foreground dark:border-border dark:bg-card grow flex flex-col gap-4 border-2 border-dashed rounded-2xl p-4">
          <h2 class="text-lg font-bold">Cursos Asignados</h2>
          <div class="relative w-full items-center">
            <Input
                v-model="searchQueryAssigned"
                type="text"
                placeholder="Buscar..."
                class="pl-10"
            />
            <span class="absolute start-0 inset-y-0 flex items-center justify-center px-2">
              <Search class="size-6 text-muted-foreground" />
            </span>
          </div>
          <ScrollArea class="flex max-h-[400px] pr-3" @scroll="(e) => handleScroll('assigned', e)">
            <draggable
                v-model="assignedCourses"
                :component-data="{
                type: 'transition-group',
                name: !isDragging ? 'flip-list' : null
              }"
                v-bind="dragOptions"
                item-key="id"
                class="w-full h-full min-h-[200px]"
                @start="isDragging = true"
                @end="isDragging = false"
            >
              <template #item="{ element }">
                <Card
                    v-if="element.name.toLowerCase().includes(searchQueryAssigned.toLowerCase())"
                    class="bg-white dark:bg-secondary rounded-lg shadow-sm p-4 mb-2 flex items-center justify-between hover:bg-gray-50 dark:hover:bg-secondary/40 transition-colors card-content"
                >
                  <div class="flex items-center gap-3">
                    <span>{{ element.name }}</span>
                  </div>
                </Card>
              </template>
            </draggable>
          </ScrollArea>
        </div>

        <!-- Available Courses -->
        <div class="basis-[350px] bg-popover/80 border-secondary-foreground dark:border-border dark:bg-card grow flex flex-col gap-4 border-2 border-dashed rounded-2xl p-4">
          <h2 class="text-lg font-bold">Cursos Disponibles</h2>
          <div class="relative w-full items-center">
            <Input
                v-model="searchQueryAvailable"
                type="text"
                placeholder="Buscar..."
                class="pl-10"
            />
            <span class="absolute start-0 inset-y-0 flex items-center justify-center px-2">
              <Search class="size-6 text-muted-foreground" />
            </span>
          </div>
          <ScrollArea class="flex max-h-[400px] pr-3" @scroll="(e) => handleScroll('assigned', e)">
            <draggable
                v-model="availableCourses"
                :component-data="{
                type: 'transition-group',
                name: !isDragging ? 'flip-list' : null
              }"
                v-bind="dragOptions"
                item-key="id"
                class="w-full h-full min-h-[200px]"
                @start="isDragging = true"
                @end="isDragging = false"
            >
              <template #item="{ element }">
                <Card
                    v-if="element.name.toLowerCase().includes(searchQueryAvailable.toLowerCase())"
                    class="bg-white dark:bg-secondary rounded-lg shadow-sm p-4 mb-2 flex items-center justify-between hover:bg-gray-50 dark:hover:bg-secondary/40 transition-colors card-content"
                >
                  <div class="flex items-center gap-3">
                    <span>{{ element.name }}</span>
                  </div>
                </Card>
              </template>
            </draggable>
          </ScrollArea>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.flip-list-move {
  transition: transform 0.5s;
}
.no-move {
  transition: transform 0s;
}
.card-content {
  user-select: none;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  cursor: grab;
}
.card-content:active {
  cursor: grabbing;
}
</style>