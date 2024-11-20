<script setup lang="ts">
import { Star, Clock3, BadgeCheck } from 'lucide-vue-next'
import StudentIcon from '@cortex/shared/components/icons/StudentIcon.vue';

interface RoadmapHeaderProps {
  title: string
  isPublished: boolean
  credits: number
}

defineProps<RoadmapHeaderProps>()
</script>

<template>

  <div class="flex flex-col gap-7">
    <div class="flex justify-between items-center gap-5">
      <div class="inline-flex justify-start items-center gap-4">
        <h1 class="font-bold text-4xl">{{ title }}</h1>
        <BadgeCheck :size="36" class="fill-[#689F39] stroke-[#FAF9F7] flex-shrink-0" />
      </div>
      <Badge class="text-md px-3 py-1 text-nowrap">{{ credits }} Credits</Badge>
    </div>
    <div class="flex gap-8">
      <div>
        <div class="flex items-center gap-1.5">
          <Star :width="20" class="stroke-[#F59E0B]" />
          <span class="font-bold text-base">4.2</span>
        </div>
        <span class="font-medium text-sm">14,715 Ratings</span>
      </div>
      <div>
        <div class="flex items-center gap-1.5">
          <StudentIcon :width="20" class="fill-current" />
          <span class="font-bold text-base">32,123</span>
        </div>
        <span class="font-medium text-sm">14,715 Ratings</span>
      </div>
      <div>
        <div class="flex items-center gap-1.5">
          <Clock3 :width="20" />
          <span class="font-bold text-base">1.8h</span>
        </div>
        <span class="font-medium text-sm">14,715 Ratings</span>
      </div>
    </div>
  </div>

</template>