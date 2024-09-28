<template>
  <div class="flex h-screen bg-gradient-to-br from-purple-900 via-indigo-800 to-pink-700">
    <AppSidebar />
    <main class="flex-grow flex">
      <div class="flex-grow bg-white rounded-3xl m-6 p-8 overflow-auto">
        <div class="flex justify-between items-center mb-6">
          <h2 class="text-2xl font-bold">Dashboard</h2>
          <Button variant="secondary">Add Custom Widget</Button>
        </div>
<!--      <slot />-->
      </div>
    </main>
  </div>
</template>