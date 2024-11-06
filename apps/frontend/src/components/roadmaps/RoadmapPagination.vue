<script setup lang="ts">
const route = useRoute()
const router = useRouter()

const props = defineProps<{
  currentPage: number
  totalPages: number
  isLoading: boolean
}>()

const handlePageChange = async (page: number) => {
  await router.push({
    query: {
      ...route.query,
      page
    }
  })
}
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
        @update:page="handlePageChange"
    >
      <PaginationList v-slot="{ items }" class="flex items-center gap-1">
        <PaginationFirst :disabled="currentPage === 1 || isLoading" />
        <PaginationPrev :disabled="currentPage === 1 || isLoading" />

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
              {{ currentPage }}
            </Button>
          </PaginationListItem>
          <PaginationEllipsis
              v-else
              :key="item.type"
              :index="index"
          />
        </template>

        <PaginationNext :disabled="currentPage === totalPages || isLoading"/>
        <PaginationLast :disabled="currentPage === totalPages || isLoading" @click="handlePageChange(totalPages)"/>
      </PaginationList>
    </Pagination>
  </div>
</template>