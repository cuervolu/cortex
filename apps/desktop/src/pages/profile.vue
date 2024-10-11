<script setup lang="ts">
import UserProgress from "~/components/profile/UserProgress.vue";
import UserRoadmaps from "~/components/profile/UserRoadmaps.vue";
import UserWelcome from "~/components/profile/UserWelcome.vue";

const userStore = useUserStore();

onMounted(async () => {
  await userStore.initStore();
  await userStore.getUser();
});
</script>

<template>
  <div class="min-h-screen bg-background p-8 mb-5">
    <div class="max-w-6xl mx-auto space-y-8">
      <UserWelcome :user="userStore.user" />

      <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
        <UserProgress />
        <UserRoadmaps />
      </div>
    </div>
  </div>
</template>