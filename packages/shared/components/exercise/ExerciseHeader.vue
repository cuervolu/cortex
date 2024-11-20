<script setup lang="ts">
import { Settings, ChevronLeft, Menu } from "lucide-vue-next";

interface Props {
  lesson: string;
  exerciseName: string;
  fileName: string;
  onBackClick: () => void;
  onSettingsClick: () => void;
}

const props = defineProps<Props>();
</script>

<template>
  <div class="exercise-header">
    <div class="flex items-start justify-between p-2 sm:p-4 bg-muted/50 border-b-4 border-gradient-purple">
      <RouterLink to="/my-roadmaps" class="flex items-center gap-1 sm:gap-2">
      <ChevronLeft class="w-4 h-4 sm:w-5 sm:h-5" />
      <div class="text-xs sm:text-sm text-foreground truncate max-w-[80px] sm:max-w-none">
        Back to Exercise
      </div>
      </RouterLink>
      <div class="flex items-center gap-1 sm:gap-2">
        <p class="text-xs sm:text-sm truncate max-w-[120px] sm:max-w-none">
          <span class="text-muted-foreground">{{ props.lesson }}</span>
          <span class="text-muted-foreground"> /</span>
          <span class="text-foreground"> {{ props.exerciseName }}</span>
        </p>
      </div>
      <Settings class="hidden sm:block cursor-pointer w-4 h-4 sm:w-5 sm:h-5" @click="props.onSettingsClick" />
      <Menu class="sm:hidden cursor-pointer w-4 h-4" @click="props.onSettingsClick" />
    </div>
    <!-- Tab -->
  </div>
</template>

<style scoped>
.border-gradient-purple {
  border-image: linear-gradient(to right, hsl(var(--primary)), hsl(var(--secondary))) 1;
}
</style>
