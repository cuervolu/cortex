<script setup lang="ts">
interface Props {
  currentPage: number
  totalPages: number
  siblingCount?: number
  showEdges?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  siblingCount: 1,
  showEdges: true
})

const emit = defineEmits(['pageChange'])

const onPageChange = (page: number) => {
  emit('pageChange', page)
}
</script>

<template>
  <Pagination
      v-slot="{ page }"
      :total="totalPages"
      :sibling-count="siblingCount"
      :show-edges="showEdges"
      :default-page="currentPage"
      @update:page="onPageChange"
  >
    <PaginationList v-slot="{ items }" class="flex items-center gap-1">
      <PaginationFirst v-if="showEdges" />
      <PaginationPrev />
      <template v-for="(item, index) in items">
        <PaginationListItem v-if="item.type === 'page'" :key="index" :value="item.value" as-child>
          <Button class="w-10 h-10 p-0" :variant="item.value === page ? 'default' : 'outline'">
            {{ item.value }}
          </Button>
        </PaginationListItem>
        <PaginationEllipsis v-else :key="item.type" :index="index" />
      </template>
      <PaginationNext />
      <PaginationLast v-if="showEdges" />
    </PaginationList>
  </Pagination>
</template>