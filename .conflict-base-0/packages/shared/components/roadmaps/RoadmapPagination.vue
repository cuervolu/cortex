<script setup lang="ts">
defineProps<{
  currentPage: number
  totalPages: number
  isLoading: boolean
  onPageChange: (page: number) => void
}>()
</script>

<template>
  <div
    class="flex flex-wrap gap-1 justify-center items-center py-5 mt-2.5 w-full text-sm font-medium max-md:max-w-full"
    aria-label="Pagination"
  >
    <Pagination
      v-slot="{ page }"
      :total="totalPages"
      :sibling-count="1"
      show-edges
      :default-page="currentPage"
      :disabled="isLoading"
      @update:page="onPageChange"
    >
      <PaginationList v-slot="{ items }" class="flex items-center gap-1">
        <PaginationFirst/>
        <PaginationPrev/>

        <template v-for="(item, index) in items">
          <PaginationListItem
            v-if="item.type === 'page'"
            :key="index"
            :value="item.value"
            as-child
          >
            <Button
              class="w-10 h-10 p-0 dark:bg-[#5e2b85]"
              :variant="item.value === page ? 'default' : 'outline'"
              :disabled="isLoading"
            >
              {{ item.value }}
            </Button>
          </PaginationListItem>
          <PaginationEllipsis
            v-else
            :key="item.type"
            :index="index"
          />
        </template>

        <PaginationNext/>
        <PaginationLast/>
      </PaginationList>
    </Pagination>
  </div>
</template>
