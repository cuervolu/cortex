<script lang="ts" setup>
import {ref} from 'vue';
import {PanelRightOpen} from "lucide-vue-next";
import NavigationButton from "~/components/NavigationButton.vue";
import UserAccount from "~/components/UserAccount.vue";
import DashboardIcon from "~/components/icons/DashboardIcon.vue";
import CubeIcon from "~/components/icons/CubeIcon.vue";
import RoadmapIcon from "~/components/icons/RoadmapIcon.vue";
import CourseIcon from "~/components/icons/CourseIcon.vue";
import SupportIcon from "~/components/icons/SupportIcon.vue";

const {isCollapsed} = defineProps<{
  isCollapsed: boolean;
}>();

defineEmits(['toggle-sidebar']);

const selectedButton = ref('Dashboard');

const navigationButtons = [
  {name: 'Dashboard', icon: DashboardIcon, route: '/dashboard'},
  {name: 'Analytics', icon: CubeIcon, route: '/analytics'},
  {name: 'Roadmaps', icon: RoadmapIcon, route: '/roadmaps'},
  {name: 'Courses', icon: CourseIcon, route: '/courses'},
  {name: 'Support', icon: SupportIcon, route: '/settings'},
];
</script>

<template>
  <div
      :class="['shrink basis self-stretch flex-col justify-between items-start inline-flex transition-all duration-300', isCollapsed ? 'w-[115px]' : 'w-[270px]']">
    <div
        class="self-stretch h-[459px] px-[7px] py-2.5 flex-col justify-start items-start gap-2.5 flex">
      <div
          class="self-stretch p-2.5 border-b border-[#f9cf87] items-center gap-2.5 inline-flex justify-between">
        <div class="w-[185.92px] flex-col justify-center items-start gap-[14.64px] inline-flex">
          <div class="justify-between items-center inline-flex">
            <img
                src="~/assets/img/Cortex Logo.svg" alt="Cortex Logo"
                class="min-w[46.85px] min-h[54px] w-[46.85px] h-[54px] relative">
            <span
                :class="['text-[#f4f8f7] text-logo transition-all duration-300 pl-2 uppercase', isCollapsed ? 'hidden' : '']">Cortex</span>
          </div>
        </div>
        <Button
            size="icon" variant="ghost"
            @click="$emit('toggle-sidebar')">
          <PanelRightOpen
              :class="['transition-transform duration-300 w-6 text-[#f4f8f7]/80   dark:text-foreground', { 'rotate-180': !isCollapsed }]"/>
        </Button>
      </div>
      <div class="self-stretch h-[355px] py-2.5 flex-col justify-start items-start gap-2.5 flex">
        <div
            class="w-[121px] h-[35px] text-[#f4f8f7]/80 text-xs font-semibold tracking-tight uppercase">
          Navigation
        </div>
        <div
            :class="['h-[290px] flex-col justify-start items-start gap-2.5 flex', !isCollapsed ? 'self-stretch transition-all duration-300' : 'transition-all duration-300']">
          <NavigationButton
              v-for="button in navigationButtons" :key="button.name"
              :name="button.name"
              :icon="button.icon"
              :route="button.route"
              :is-selected="selectedButton === button.name"
              :is-collapsed="isCollapsed"
              @click="selectedButton = button.name"
          />
        </div>
      </div>
    </div>
    <UserAccount :is-collapsed="isCollapsed"/>
  </div>
</template>