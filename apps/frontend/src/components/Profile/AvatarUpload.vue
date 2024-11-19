<script setup lang="ts">
import { ImagePlus, User } from 'lucide-vue-next'

const props = defineProps<{
  modelValue?: File | null
  currentAvatarUrl?: string | null
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', file: File | null): void
}>()

const fileInput = ref<HTMLInputElement | null>(null)
const previewUrl = ref<string | null>(null)

const handleFileChange = (event: Event) => {
  const input = event.target as HTMLInputElement
  if (input.files && input.files[0]) {
    const file = input.files[0]
    emit('update:modelValue', file)
    if (previewUrl.value) {
      URL.revokeObjectURL(previewUrl.value)
    }
    previewUrl.value = URL.createObjectURL(file)
  }
}

const handleClick = () => {
  fileInput.value?.click()
}

onBeforeUnmount(() => {
  if (previewUrl.value) {
    URL.revokeObjectURL(previewUrl.value)
  }
})

const getDisplayImage = computed(() => {
  if (previewUrl.value) return previewUrl.value
  if (props.currentAvatarUrl) return props.currentAvatarUrl
  return null
})
</script>

<template>
  <div class="flex flex-col items-center gap-4">
    <div
        class="relative group cursor-pointer"
        @click="handleClick"
    >
      <Avatar class="h-24 w-24">
        <AvatarImage
            v-if="getDisplayImage"
            :src="getDisplayImage"
            alt="Avatar"
            class="object-cover"
        />
        <AvatarFallback class="bg-muted">
          <User class="h-12 w-12" />
        </AvatarFallback>
      </Avatar>
      <div class="absolute inset-0 bg-black/60 flex items-center justify-center rounded-full opacity-0 group-hover:opacity-100 transition-opacity">
        <ImagePlus class="h-8 w-8 text-white" />
      </div>
    </div>
    <input
        ref="fileInput"
        type="file"
        class="hidden"
        accept="image/*"
        @change="handleFileChange"
    />
    <Button
        variant="outline"
        type="button"
        size="sm"
        @click="handleClick"
    >
      Cambiar avatar
    </Button>
  </div>
</template>