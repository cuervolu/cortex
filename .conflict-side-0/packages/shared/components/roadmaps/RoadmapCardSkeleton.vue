<template>
  <Card class="grow shrink basis-0 p-[11px] w-screen min-w-[382px] rounded-[18px] flex-col justify-start items-start gap-2.5 inline-flex">
    <CardHeader class="w-full p-2.5">
      <Skeleton class="w-full h-[200px] rounded-[16px]" />
    </CardHeader>
    <CardContent class="flex gap-2.5 flex-col px-2.5 pb-2.5 w-full">
      <div class="flex items-center gap-1.5">
        <Skeleton class="h-5 w-16 rounded-full" />
        <Skeleton class="h-5 w-16 rounded-full" />
        <Skeleton class="h-5 w-16 rounded-full" />
      </div>
      <Skeleton class="h-7 w-3/4 rounded-lg" />
      <Skeleton class="h-4 w-full rounded-lg" />
      <Skeleton class="h-4 w-2/3 rounded-lg" />
    </CardContent>
    <Separator />
    <CardFooter class="w-full p-2.5">
      <div class="flex justify-between w-full">
        <Skeleton class="h-5 w-24 rounded-lg" />
        <Skeleton class="h-5 w-32 rounded-lg" />
      </div>
    </CardFooter>
  </Card>
</template>