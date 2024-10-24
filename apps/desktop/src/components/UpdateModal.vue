<script setup lang="ts">
import updateRocket from '~/assets/img/update-rocket.svg'

const showDetails = ref(false)

const handleViewDetails = () => {
  showDetails.value = true
}

const handleBackToMain = () => {
  showDetails.value = false
}
</script>

<template>
  <Dialog>
    <DialogTrigger as-child>
      <Button class="px-4 py-2 bg-[#8b2a93] text-white rounded-md">
        Show Update Modal
      </Button>
    </DialogTrigger>
    <DialogContent class="p-0 border-none bg-transparent w-[654px]">
      <Card class="w-[654px] h-[580px] rounded-[14.442px] border-[1.203px] border-zinc-200 bg-gradient-to-r from-[rgba(80,23,106,0.68)] to-[rgba(168,61,173,0.84)] shadow-[0px_4px_68.8px_0px_rgba(0,0,0,0.25)] backdrop-blur-[16.9px] relative overflow-hidden flex flex-col">
        <!-- Vista Principal -->
        <template v-if="!showDetails">
          <CardHeader class="text-center space-y-1 pb-5">
            <CardTitle class="text-4xl font-semibold text-white">
              New Update is Available
            </CardTitle>
            <CardDescription>
              <button
                  class="text-sm text-[#d9d9d9] underline hover:text-white transition-colors"
                  @click="handleViewDetails"
              >
                View Details...
              </button>
            </CardDescription>
          </CardHeader>
          <div class="relative flex-1 flex flex-col">
            <div class="absolute inset-0">
              <img
                  class="w-full h-full object-cover"
                  :src="updateRocket"
                  alt="Update rocket illustration"
              >
            </div>
            <div class="relative z-10 flex flex-col h-full justify-end">
              <p class="text-[#5e4a6e] text-center px-4 mb-8">
                A new version is released, please update to get new features
              </p>
              <CardFooter class="flex gap-3 justify-center px-8 pb-8">
                <Button
                    class="flex-1 py-2.5 px-5 rounded-md bg-[#8b2a93] text-white text-sm font-medium hover:bg-[#7a2580] transition-colors uppercase"
                >
                  Update now!
                </Button>
                <DialogClose as-child>
                  <Button
                      class="flex-1 py-2.5 px-5 rounded-md bg-[#d9d9d9] border border-[#8c2a94] text-[#8c2a94] text-sm font-medium hover:bg-[#c9c9c9] transition-colors"
                  >
                    Remind me later
                  </Button>
                </DialogClose>
              </CardFooter>
            </div>
          </div>
        </template>

        <!-- Vista de Detalles -->
        <template v-else>
          <div class="flex flex-col h-full">
            <!-- Header -->
            <div class="px-6 py-3 bg-white/10 border-b border-white/30 flex items-center">
              <div class="w-[72px] h-[72px] p-5 bg-[#d9d9d9] rounded-xl flex items-center justify-center">
                <div class="w-[30px] h-8"/>
              </div>
              <div class="ml-3.5">
                <div class="text-white text-[17px]">New version available</div>
                <div class="flex gap-2 text-white text-[17px]">
                  <span>1.2.0</span>
                  <span>Oct 22, 2024</span>
                </div>
              </div>
            </div>

            <!-- Content -->
            <div class="flex-1 px-6 py-3 text-white overflow-y-auto">
              <h2 class="text-2xl font-bold mb-2">Changelog</h2>
              <p class="text-[17px] mb-6">
                A list of the latest improvements, new features, and fixes for the Cortex platform.
              </p>

              <h3 class="text-xl font-semibold mb-2">Features:</h3>
              <ul class="text-[17px] mb-6 space-y-2">
                <li>Added new UI components built with Shadcn for an enhanced user experience.</li>
                <li>Improved mobile responsiveness, making the platform more accessible across different devices.</li>
              </ul>

              <h3 class="text-xl font-semibold mb-2">Bug Fixes:</h3>
              <ul class="text-[17px] mb-6 space-y-2">
                <li>Fixed an issue with the sidebar collapsing incorrectly on smaller screen sizes.</li>
                <li>Resolved a bug where navigation icons were misaligned when the sidebar was minimized.</li>
              </ul>

              <h3 class="text-[17px] font-semibold mb-2">Improvements:</h3>
              <ul class="text-[17px] space-y-2">
                <li>Enhanced overall performance, resulting in faster page load times and smoother navigation.</li>
                <li>Adjusted layout for a more intuitive interaction with the homepage content.</li>
              </ul>
            </div>

            <!-- Footer -->
            <div class="p-6 bg-white/10 border-t bordegr-white/30">
              <div class="flex justify-end">
                <Button
                    class="px-5 py-2.5 bg-[#d9d9d9] rounded-md border border-[#8c2a94] text-[#8c2a94] text-sm font-medium hover:bg-[#c9c9c9] transition-colors"
                    @click="handleBackToMain"
                >
                  Ok
                </Button>
              </div>
            </div>
          </div>
        </template>
      </Card>
    </DialogContent>
  </Dialog>
</template>