<script lang="ts" setup>

const isMonthly = ref(true);

const plans = [
  {
    title: 'Basic Plan',
    price: '$0',
    period: 'Free per month',
    features: [
      { text: 'Access to basic courses' },
      { text: 'Limited projects' },
      { text: 'Discussion forum' },
      { text: 'No personalized mentorships' },
      { text: 'Basic learning resources' },
      { text: 'Self-directed learning' },
    ],
    buttonText: 'Current Plan',
  },
  {
    title: 'Standard Plan',
    price: '$30',
    period: 'per editor/month',
    subPeriod: 'billed monthly',
    features: [
      { text: 'Access to all courses' },
      { text: 'Limited mentorships' },
      { text: 'Access to collaborative projects' },
      { text: 'Optional extra credits' },
      { text: 'Access to advanced forums' },
      { text: 'Guided and personalized learning' },
    ],
    buttonText: 'Choose Plan',
    highlighted: true,
    tag: 'Most Popular',
  },
  {
    title: 'Pro Plan',
    price: '$60',
    period: 'per editor/month',
    subPeriod: 'billed monthly',
    features: [
      { text: 'Full access to all courses and exclusive resources' },
      { text: 'Unlimited mentorships' },
      { text: 'Priority in collaborative projects' },
      { text: 'Additional credits' },
      { text: 'Access to advanced content and premium tools' },
      { text: 'Priority support' },
    ],
    buttonText: 'Choose Plan',
  },
];
</script>

<template>
  <div class="flex justify-center w-full">
    <div class="flex flex-col items-center justify-center gap-[97px] px-[65px] py-[53px] relative max-w-[1200px] w-full">
      <div class="flex flex-col items-center gap-4 relative self-stretch w-full flex-[0_0_auto]">
        <div class="relative w-fit mt-[-1.00px] font-bold  text-[40px] text-center tracking-[0.80px] leading-normal">
          Purchase a subscription
        </div>
        <p class="relative w-fit font-normal  text-xl text-center tracking-[0.40px] leading-normal">
          Choose the plan that works for you.
        </p>
      </div>
      <div class="flex max-w-[355px] items-center gap-[9px] p-[4.5px] self-stretch w-full bg-[#ebeff0] rounded-[112.45px] justify-center relative flex-[0_0_auto]">
        <button :class="['flex w-[168.67px] items-center justify-center gap-[4.5px] p-[11.24px] relative ml-[-0.17px] rounded-[112.45px]', isMonthly ? 'bg-[#1d2127] text-white' : 'text-[#1d2127]']" @click="isMonthly = true">
          <div class="relative flex-1 mt-[-1.12px] font-bold text-lg text-center tracking-[0.36px] leading-normal">
            Monthly
          </div>
        </button>
        <button :class="['flex w-[168.67px] items-baseline justify-center gap-[4.5px] p-[11.24px] relative mr-[-0.17px] rounded-[112.45px]', !isMonthly ? 'bg-[#1d2127] text-white' : 'text-[#1d2127]']" @click="isMonthly = false">
          <div class="relative w-fit mt-[-1.12px] font-normal text-lg text-center tracking-[0.36px] leading-normal">
            Yearly
          </div>
          <div class="relative w-fit font-normal text-[13.5px] text-center tracking-[0.27px] leading-normal">
            -20% off
          </div>
        </button>
      </div>
      <div class="flex flex-col md:flex-row justify-center items-center md:items-end gap-[17.99px] p-[13.49px] relative w-full max-w-[1075px] rounded-[35.98px] shadow-[0px_16.03px_14.52px_#00000021]">
        <PricingCard v-for="(plan, index) in plans" :key="index" v-bind="plan" />
      </div>
    </div>
  </div>
</template>