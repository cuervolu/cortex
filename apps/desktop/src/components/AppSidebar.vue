<script lang="ts" setup>
import NavigationButton from '~/components/NavigationButton.vue'
import UserAccount from '~/components/UserAccount.vue'
import { useNavigation } from '~/composables/useNavigation'

const { isCollapsed } = defineProps<{
  isCollapsed: boolean
}>()

defineEmits(['toggle-sidebar'])
const { navigationItems } = useNavigation()
</script>

<template>
  <div
      :class="[
      'shrink basis self-stretch flex-col justify-between items-start inline-flex transition-all duration-300 ease-in-out',
      isCollapsed ? 'w-[90px]' : 'w-[270px]'
    ]"
  >
    <div class="self-stretch h-[459px] px-[7px] py-2.5 flex-col justify-start items-start gap-2.5 flex">
      <!-- Logo section -->
      <div class="self-stretch p-2.5 border-b border-[#f9cf87] items-center gap-2.5 inline-flex justify-between">
        <div class="w-[185.92px] flex-col justify-center items-start gap-[14.64px] inline-flex">
          <div class="justify-between items-center inline-flex">
            <img
                src="~/assets/img/Cortex Logo.svg"
                alt="Cortex Logo"
                class="min-w-[46.85px] min-h-[54px] w-[46.85px] h-[54px] relative"
            >
            <span
                :class="[
                'text-[#f4f8f7] text-logo transition-all duration-300 ease-in-out pl-2 uppercase',
                isCollapsed ? 'opacity-0 w-0 overflow-hidden' : 'opacity-100 w-auto'
              ]"
            >
              Cortex
            </span>
          </div>
        </div>
      </div>

      <!-- Navigation section -->
      <div class="self-stretch h-[355px] py-2.5 flex-col justify-start items-start gap-2.5 flex">
        <div class="w-fit h-[35px] text-[#f4f8f7]/80 text-xs font-semibold tracking-tight uppercase">
          Navegar
        </div>
        <div
            :class="[
            'h-[290px] flex-col justify-start items-start gap-2.5 flex transition-all duration-300 ease-in-out',
            isCollapsed ? 'w-fit' : 'w-full'
          ]"
        >
          <NavigationButton
              v-for="item in navigationItems"
              :key="item.name"
              :name="item.name"
              :icon="item.icon"
              :route="item.route"
              :is-collapsed="isCollapsed"
              :is-custom-icon="item.isCustomIcon"
          />
        </div>
      </div>
    </div>
    <UserAccount :is-collapsed="isCollapsed" />
  </div>
</template>

<style scoped>
.text-logo {
  font-size: 1.5rem;
  font-weight: bold;
}
</style>