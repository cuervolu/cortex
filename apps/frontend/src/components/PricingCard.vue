<script lang="ts" setup>
import {Check} from 'lucide-vue-next'
import LemonSqueezyButton from "~/components/LemonSqueezyButton.vue";

interface Feature {
  text: string;
}

interface Props {
  title: string;
  price: string;
  period: string;
  subPeriod?: string;
  features: Feature[];
  buttonText: string;
  highlighted?: boolean;
  tag?: string;
  path: string;
  enable?: boolean;
  checkoutUrl?: string;
}

const props = withDefaults(defineProps<Props>(), {
  highlighted: false,
});

const bgClass = computed(() => props.highlighted ? 'bg-gradient-to-b from-[#F493BC] to-[#7C4BA4] dark:from-[#92D2DD] dark:to-[#67219E]' : 'bg-white dark:bg-primary');
const textClass = computed(() => props.highlighted ? 'text-white dark:text-primary' : 'text-[#1d2127] dark:text-[#E1E0E0]');
const iconBgClass = computed(() => props.highlighted ? 'bg-[#f08cb6] dark:bg-[#92D2DD]' : 'bg-[#ebeff0] dark:bg-[#1A2123]');
const iconColor = computed(() => props.highlighted ? 'text-white dark:text-[#381653]' : 'text-[#B9BEC1]');
const buttonClass = computed(() => props.highlighted ? 'bg-[#f296bd] text-white dark:bg-[#92D2DD] dark:text-primary' : 'bg-[#fff7eb] text-[#ead7b5] dark:bg-[#66249B] dark:text-[#BEBEBE]');
</script>

<template>
  <div
      :class="[
    'flex flex-col w-[337.34px] h-[591.47px] items-center relative rounded-[33.73px]',
    bgClass,
    { 'shadow-[0px_3.37px_15.74px_#2d2f331f]': highlighted }
  ]">
    <div
        class="flex flex-col items-start gap-[17.99px] px-[26.99px] py-[35.98px] relative self-stretch w-full flex-[0_0_auto]">
      <div
          :class="['relative w-fit mt-[-1.12px] font-semibold text-lg tracking-[0.36px] leading-normal', textClass]">
        {{ title }}
      </div>
      <div class="flex items-center gap-[9px] relative self-stretch w-full flex-[0_0_auto]">
        <div
            :class="['relative w-fit mt-[-1.12px] font-extrabold text-[40.5px] tracking-[0.81px] leading-normal', textClass]">
          {{ price }}
        </div>
        <div
            class="inline-flex flex-col items-start gap-[2.25px] justify-center relative flex-[0_0_auto]">
          <div
              :class="['relative w-fit mt-[-1.12px] font-normal text-[13.5px] tracking-0 leading-normal', highlighted ? 'text-white dark:text-primary dark:font-medium' : 'text-[#b9bec1]']">
            {{ period }}
          </div>
          <div
              v-if="subPeriod"
              :class="['relative w-fit font-normal text-[13.5px] tracking-0 leading-normal', highlighted ? 'text-white dark:text-primary dark:font-medium' : 'text-[#b9bec1]']">
            {{ subPeriod }}
          </div>
        </div>
      </div>
    </div>
    <div
        class="flex flex-col items-start gap-[13.49px] pt-0 pb-[62.97px] px-[26.99px] relative flex-1 self-stretch w-full grow">
      <div
          v-for="(feature, index) in features" :key="index"
          class="flex items-start gap-[9px] relative self-stretch w-full flex-[0_0_auto]">
        <div :class="['relative w-[17.99px] h-[17.99px] rounded-[9px]', iconBgClass]">
          <Check class="!absolute !w-[9px] !h-[9px] !top-1 !left-1" :class="iconColor"/>
        </div>
        <div
            :class="['relative flex-1 mt-[-1.12px] font-medium text-[15.7px] tracking-0 leading-[18px]', textClass]">
          {{ feature.text }}
        </div>
      </div>
    </div>
    <div
        v-if="tag"
        class="inline-flex items-center justify-center gap-[4.5px] px-[9px] py-[6.75px] absolute top-[27px] right-[27px] bg-white dark:bg-primary rounded-[9px]">
      <div
          class="relative w-fit mt-[-1.12px] font-medium text-[#ff72ae] dark:text-[#E1E0E0] text-[13.5px] text-center tracking-[0.27px] leading-normal">
        {{ tag }}
      </div>
    </div>
    <div
        class="flex flex-col items-center gap-[11.24px] pt-0 pb-[35.98px] px-[26.99px] relative self-stretch w-full flex-[0_0_auto]">
      <template v-if="checkoutUrl">
        <LemonSqueezyButton
            :checkout-url="checkoutUrl"
            :button-text="buttonText"
            :disabled="!enable"
            :class="buttonClass"
        />
      </template>
      <template v-else>
        <Button v-if="path === '/'"
                @click="$router.push('/plans')"
                :disabled="!enable"
                :class="['relative self-stretch w-full h-[44.98px] rounded-[9px] overflow-hidden', buttonClass, { 'opacity-50 cursor-not-allowed': !enable }]"
        >
          <div
              class="absolute top-[7px] left-[50%] transform -translate-x-1/2 font-normal text-lg text-center tracking-0 leading-[27px] whitespace-nowrap">
            {{ buttonText }}
          </div>
        </Button>
        <Button v-else
                :disabled="!enable"
                :class="['relative self-stretch w-full h-[44.98px] rounded-[9px] overflow-hidden', buttonClass, { 'opacity-50 cursor-not-allowed': !enable }]"
        >
          <div
              class="absolute top-[7px] left-[50%] transform -translate-x-1/2 font-normal text-lg text-center tracking-0 leading-[27px] whitespace-nowrap">
            {{ buttonText }}
          </div>
        </Button>
      </template>
    </div>
  </div>
</template>
