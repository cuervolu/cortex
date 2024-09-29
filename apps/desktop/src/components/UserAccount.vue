<script lang="ts" setup>

defineProps<{
  isCollapsed: boolean;
}>();
</script>

<template>
  <div class="self-stretch h-[118px] p-2.5 flex-col justify-start items-start flex">
    <div class="w-[121px] h-[35px] text-[#f4f8f7]/80 text-xs font-semibold tracking-tight">USER
      ACCOUNT
    </div>
    <div class="self-stretch justify-start items-center inline-flex">
      <div class="min-w-[49px] h-[49px] relative bg-[#d9d9d9] rounded-[37px]">
        <div class="w-[39.23px] h-[45px] left-[5px] top-[4px] absolute"/>
      </div>
      <div v-if="!isCollapsed" class="grow shrink basis-0 p-2.5 flex-col justify-start items-start inline-flex">
        <div class="self-stretch h-7 text-[#f4f8f7] text-lg font-semibold tracking-tight">Alex
          Williamson</div>
        <div class="w-[111px] h-[15px] text-[#f4f8f7]/60 text-sm font-normal tracking-tight">
          #alexwil227
        </div>
      </div>
    </div>
  </div>
</template>

