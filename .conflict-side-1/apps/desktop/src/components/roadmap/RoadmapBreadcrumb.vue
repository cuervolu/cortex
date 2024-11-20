<script setup lang="ts">
import HomeIcon from '@cortex/shared/components/icons/HomeIcon.vue';

interface RoadmapBreadcrumbProps {
  slug: string
  title: string
}

defineProps<RoadmapBreadcrumbProps>()
</script>

<template>
  <Breadcrumb class="py-5">
    <BreadcrumbList>
      <BreadcrumbItem>
        <BreadcrumbLink>
          <NuxtLink to="/">
            <HomeIcon class="w-[18px] fill-current" />
          </NuxtLink>
        </BreadcrumbLink>
      </BreadcrumbItem>
      <BreadcrumbSeparator />
      <BreadcrumbItem>
        <BreadcrumbLink>
          <NuxtLink to="/roadmaps">
            Roadmaps
          </NuxtLink>
        </BreadcrumbLink>
      </BreadcrumbItem>
      <BreadcrumbSeparator />
      <BreadcrumbItem>
        <BreadcrumbLink>
          <NuxtLink to="/explore">
            Explore Roadmaps
          </NuxtLink>
        </BreadcrumbLink>
      </BreadcrumbItem>
      <BreadcrumbSeparator />
      <BreadcrumbItem>
        <BreadcrumbPage>{{ title }}</BreadcrumbPage>
      </BreadcrumbItem>
    </BreadcrumbList>
  </Breadcrumb>
</template>