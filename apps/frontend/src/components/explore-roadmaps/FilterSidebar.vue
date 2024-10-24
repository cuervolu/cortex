<template>
    <aside class="flex overflow-hidden flex-col min-w-[240px] w-[368px]">
        <section
            class="flex overflow-hidden flex-col px-3.5 py-3.5 w-full font-medium rounded-3xl bg-stone-50 max-w-[368px] min-w-[368px]">
            <header
                class="flex gap-10 justify-between items-center py-5 pr-4 w-full text-2xl text-black whitespace-nowrap">
                <h2 class="self-stretch my-auto">Categorias</h2>
                <img loading="lazy"
                    src="https://cdn.builder.io/api/v1/image/assets/TEMP/48614d5597ad3119605b8efc8f52c94525841714b61dbdd8fdbac72f4b0239ae?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                    alt="" class="object-contain shrink-0 self-stretch my-auto aspect-[1.7] w-[17px]" />
            </header>
            <nav class="flex flex-col mt-4 w-full">
                <div class="flex gap-10 justify-between items-center py-2.5 w-full text-xl text-violet-950">
                    <button class="self-stretch my-auto focus:outline-none" tabindex="0">Frontend Development</button>
                    <img loading="lazy"
                        src="https://cdn.builder.io/api/v1/image/assets/TEMP/684acf1777b28b3a541d44ea980f2556de341d2f4caeb86300ef4c79031c8439?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                        alt="" class="object-contain shrink-0 self-stretch my-auto w-5 aspect-square" />
                </div>
                <ul class="flex flex-col self-start pl-6 mt-2.5 text-base text-zinc-600 max-md:pl-5">
                    <li><button class="hover:text-violet-950 focus:outline-none" tabindex="0">HTML & CSS</button></li>
                    <li><button class="mt-5 hover:text-violet-950 focus:outline-none" tabindex="0">JavaScript</button>
                    </li>
                    <li><button class="mt-5 hover:text-violet-950 focus:outline-none" tabindex="0">React</button></li>
                    <li><button class="mt-5 hover:text-violet-950 focus:outline-none" tabindex="0">Vue.js</button></li>
                    <li><button class="mt-5 hover:text-violet-950 focus:outline-none" tabindex="0">Angular</button></li>
                </ul>
            </nav>
            <nav class="flex flex-col mt-4 w-full text-xl text-violet-950">
                <button class="flex gap-10 justify-between items-center py-2.5 w-full focus:outline-none">
                    <span class="self-stretch my-auto">Backend Development</span>
                    <img loading="lazy"
                        src="https://cdn.builder.io/api/v1/image/assets/TEMP/f22ecc8d7f910024dc3789091903bea8d47695df3b7f0689f4dd309505c06ac1?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                        alt="" class="object-contain shrink-0 self-stretch my-auto w-5 aspect-square" />
                </button>
            </nav>
            <nav class="flex flex-col justify-center py-2.5 mt-4 w-full text-xl text-violet-950">
                <button class="flex gap-10 justify-between items-center w-full focus:outline-none">
                    <span class="self-stretch my-auto">DevOps & Infraestructura</span>
                    <img loading="lazy"
                        src="https://cdn.builder.io/api/v1/image/assets/TEMP/f22ecc8d7f910024dc3789091903bea8d47695df3b7f0689f4dd309505c06ac1?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                        alt="" class="object-contain shrink-0 self-stretch my-auto w-5 aspect-square" />
                </button>
            </nav>
            <nav class="flex flex-col justify-center py-2.5 mt-4 w-full text-xl whitespace-nowrap text-violet-950">
                <button class="flex gap-10 justify-between items-center w-full focus:outline-none">
                    <span class="self-stretch my-auto">Cybersecuridad</span>
                    <img loading="lazy"
                        src="https://cdn.builder.io/api/v1/image/assets/TEMP/f22ecc8d7f910024dc3789091903bea8d47695df3b7f0689f4dd309505c06ac1?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                        alt="" class="object-contain shrink-0 self-stretch my-auto w-5 aspect-square" />
                </button>
            </nav>
            <nav class="flex flex-col justify-center py-2.5 mt-4 w-full text-xl text-violet-950">
                <button class="flex gap-10 justify-between items-center w-full focus:outline-none">
                    <span class="self-stretch my-auto">UI/UX Design</span>
                    <img loading="lazy"
                        src="https://cdn.builder.io/api/v1/image/assets/TEMP/f22ecc8d7f910024dc3789091903bea8d47695df3b7f0689f4dd309505c06ac1?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                        alt="" class="object-contain shrink-0 self-stretch my-auto w-5 aspect-square" />
                </button>
            </nav>
        </section>

        <section class="flex overflow-hidden flex-col px-3.5 py-3.5 mt-5 w-full rounded-3xl bg-stone-50 min-w-[368px]">
            <header
                class="flex gap-10 justify-between items-center py-5 pr-4 w-full text-2xl font-medium text-black whitespace-nowrap">
                <h2 class="self-stretch my-auto w-[135px]">Valoración</h2>
                <img loading="lazy"
                    src="https://cdn.builder.io/api/v1/image/assets/TEMP/48614d5597ad3119605b8efc8f52c94525841714b61dbdd8fdbac72f4b0239ae?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                    alt="" class="object-contain shrink-0 self-stretch my-auto aspect-[1.7] w-[17px]" />
            </header>
            <form>
                <div class="flex items-center pt-3.5 mt-4 w-full">
                    <input type="checkbox" id="rating4" class="sr-only" />
                    <label for="rating4" class="flex items-center cursor-pointer">
                        <span
                            class="flex shrink-0 self-stretch my-auto rounded-xl border-2 border-black border-solid h-[25px] w-[25px]"></span>
                        <span class="flex gap-2 items-center self-stretch px-4 my-auto">
                            <img loading="lazy"
                                src="https://cdn.builder.io/api/v1/image/assets/TEMP/d419feee27b8c359f443db866c2eb9c8ed5cfd562bff9ca0725ebcd606b652fe?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                                alt="Full star"
                                class="object-contain shrink-0 self-stretch my-auto aspect-[0.96] w-[22px]" />
                            <img loading="lazy"
                                src="https://cdn.builder.io/api/v1/image/assets/TEMP/d419feee27b8c359f443db866c2eb9c8ed5cfd562bff9ca0725ebcd606b652fe?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                                alt="Full star"
                                class="object-contain shrink-0 self-stretch my-auto aspect-[0.96] w-[22px]" />
                            <img loading="lazy"
                                src="https://cdn.builder.io/api/v1/image/assets/TEMP/d419feee27b8c359f443db866c2eb9c8ed5cfd562bff9ca0725ebcd606b652fe?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                                alt="Full star"
                                class="object-contain shrink-0 self-stretch my-auto aspect-[0.96] w-[22px]" />
                            <img loading="lazy"
                                src="https://cdn.builder.io/api/v1/image/assets/TEMP/d419feee27b8c359f443db866c2eb9c8ed5cfd562bff9ca0725ebcd606b652fe?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                                alt="Full star"
                                class="object-contain shrink-0 self-stretch my-auto aspect-[0.96] w-[22px]" />
                            <img loading="lazy"
                                src="https://cdn.builder.io/api/v1/image/assets/TEMP/4cc2b6859b48876738218eb34d90372b5aae6eb8698cc7218311ed9219dc2c67?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                                alt="Empty star"
                                class="object-contain shrink-0 self-stretch my-auto aspect-[0.96] w-[22px]" />
                        </span>
                        <span
                            class="flex-1 shrink self-stretch my-auto text-2xl font-medium text-black basis-[30px]">4.0
                            & Up</span>
                    </label>
                </div>

                <div class="flex items-center pt-3.5 mt-4 w-full">
                    <input type="checkbox" id="rating3" class="sr-only" />
                    <label for="rating3" class="flex items-center cursor-pointer">
                        <span
                            class="flex shrink-0 self-stretch my-auto rounded-xl border-2 border-black border-solid h-[26px] w-[25px]"></span>
                        <span class="flex gap-2 items-center self-stretch px-4 my-auto">
                            <img loading="lazy"
                                src="https://cdn.builder.io/api/v1/image/assets/TEMP/4796a1d72a51fc27ea2b6e1e083e97b6a692bf1b1c40fb1a7ec568f49df074ab?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                                alt="Full star"
                                class="object-contain shrink-0 self-stretch my-auto aspect-square w-[22px]" />
                            <img loading="lazy"
                                src="https://cdn.builder.io/api/v1/image/assets/TEMP/4796a1d72a51fc27ea2b6e1e083e97b6a692bf1b1c40fb1a7ec568f49df074ab?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                                alt="Full star"
                                class="object-contain shrink-0 self-stretch my-auto aspect-square w-[22px]" />
                            <img loading="lazy"
                                src="https://cdn.builder.io/api/v1/image/assets/TEMP/4796a1d72a51fc27ea2b6e1e083e97b6a692bf1b1c40fb1a7ec568f49df074ab?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                                alt="Full star"
                                class="object-contain shrink-0 self-stretch my-auto aspect-square w-[22px]" />
                            <img loading="lazy"
                                src="https://cdn.builder.io/api/v1/image/assets/TEMP/b801b0e87a803002f67da479f23c28dafe4f2883e214dd4fe18109a093344406?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                                alt="Empty star"
                                class="object-contain shrink-0 self-stretch my-auto aspect-square w-[22px]" />
                            <img loading="lazy"
                                src="https://cdn.builder.io/api/v1/image/assets/TEMP/b801b0e87a803002f67da479f23c28dafe4f2883e214dd4fe18109a093344406?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                                alt="Empty star"
                                class="object-contain shrink-0 self-stretch my-auto aspect-square w-[22px]" />
                        </span>
                        <span
                            class="flex-1 shrink self-stretch my-auto text-2xl font-medium text-black basis-[30px]">3.0
                            & Up</span>
                    </label>
                </div>

                <div class="flex items-center pt-3.5 mt-4 w-full">
                    <input type="checkbox" id="rating2" class="sr-only" />
                    <label for="rating2" class="flex items-center cursor-pointer">
                        <span
                            class="flex shrink-0 self-stretch my-auto rounded-xl border-2 border-black border-solid h-[26px] w-[25px]"></span>
                        <span class="flex gap-2 items-center self-stretch px-4 my-auto">
                            <img loading="lazy"
                                src="https://cdn.builder.io/api/v1/image/assets/TEMP/3e45be5f954dd73c918a5b76354108af18bcc38d2ac37444674e993679cba2d5?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                                alt="Full star"
                                class="object-contain shrink-0 self-stretch my-auto aspect-square w-[22px]" />
                            <img loading="lazy"
                                src="https://cdn.builder.io/api/v1/image/assets/TEMP/3e45be5f954dd73c918a5b76354108af18bcc38d2ac37444674e993679cba2d5?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                                alt="Full star"
                                class="object-contain shrink-0 self-stretch my-auto aspect-square w-[22px]" />
                            <img loading="lazy"
                                src="https://cdn.builder.io/api/v1/image/assets/TEMP/ccf5f10ed153b2c9be3337146a7e830b2e4adf34b7e549effb2480d8f87d2daf?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                                alt="Empty star"
                                class="object-contain shrink-0 self-stretch my-auto aspect-square w-[22px]" />
                            <img loading="lazy"
                                src="https://cdn.builder.io/api/v1/image/assets/TEMP/ccf5f10ed153b2c9be3337146a7e830b2e4adf34b7e549effb2480d8f87d2daf?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                                alt="Empty star"
                                class="object-contain shrink-0 self-stretch my-auto aspect-square w-[22px]" />
                            <img loading="lazy"
                                src="https://cdn.builder.io/api/v1/image/assets/TEMP/ccf5f10ed153b2c9be3337146a7e830b2e4adf34b7e549effb2480d8f87d2daf?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                                alt="Empty star"
                                class="object-contain shrink-0 self-stretch my-auto aspect-square w-[22px]" />
                        </span>
                        <span
                            class="flex-1 shrink self-stretch my-auto text-2xl font-medium text-black basis-[30px]">2.0
                            & Up</span>
                    </label>
                </div>
            </form>
        </section>

        <section
            class="flex overflow-hidden flex-col justify-center px-3.5 py-3.5 mt-5 w-full text-2xl font-medium text-black whitespace-nowrap rounded-3xl bg-stone-50 min-w-[368px]">
            <header class="flex gap-10 justify-between items-center py-5 pr-4 w-full">
                <h2 class="self-stretch my-auto w-[135px]">Lenguajes</h2>
                <img loading="lazy"
                    src="https://cdn.builder.io/api/v1/image/assets/TEMP/b4fb297066d728da39e4519040a89095c230480acf9eac5a3e158f6dd6d68e29?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                    alt="" class="object-contain shrink-0 self-stretch my-auto aspect-[1.7] w-[17px]" />
            </header>
        </section>

        <section
            class="flex overflow-hidden flex-col px-3.5 py-3.5 mt-5 w-full text-2xl font-medium text-black whitespace-nowrap rounded-3xl bg-stone-50 min-w-[368px]">
            <header class="flex gap-10 justify-between items-center py-5 pr-4 w-full text-2xl">
                <h2 class="self-stretch my-auto w-[135px]">Nivel</h2>
                <img loading="lazy"
                    src="https://cdn.builder.io/api/v1/image/assets/TEMP/3ef9227dd6f0f8d52d726bbd70e69aac652d4fabb6ec00f0cd35389c86c71cd1?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245"
                    alt="" class="object-contain shrink-0 self-stretch my-auto aspect-[1.7] w-[17px]" />
            </header>
            <form>
                <div class="flex items-center pt-3.5 mt-4 w-full">
                    <input type="checkbox" id="levelAll" class="sr-only" />
                    <label for="levelAll" class="flex items-center cursor-pointer">
                        <span
                            class="flex shrink-0 self-stretch my-auto rounded-xl border-2 border-black border-solid h-[25px] w-[25px]"></span>
                        <span class="flex-1 shrink self-stretch pl-5 my-auto min-w-[240px]">All</span>
                    </label>
                </div>

                <div class="flex items-center pt-3.5 mt-4 w-full">
                    <input type="checkbox" id="levelBeginner" class="sr-only" />
                    <label for="levelBeginner" class="flex items-center cursor-pointer">
                        <span
                            class="flex shrink-0 self-stretch my-auto rounded-xl border-2 border-black border-solid h-[25px] w-[25px]"></span>
                        <span class="flex-1 shrink self-stretch pl-5 my-auto min-w-[240px]">Principiante</span>
                    </label>
                </div>

                <div class="flex items-center pt-3.5 mt-4 w-full">
                    <input type="checkbox" id="levelIntermediate" class="sr-only" />
                    <label for="levelIntermediate" class="flex items-center cursor-pointer">
                        <span
                            class="flex shrink-0 self-stretch my-auto rounded-xl border-2 border-black border-solid h-[26px] w-[25px]"></span>
                        <span class="flex-1 shrink self-stretch pl-5 my-auto min-w-[240px]">Intermedio</span>
                    </label>
                </div>

                <div class="flex items-center pt-3.5 mt-4 w-full">
                    <input type="checkbox" id="levelAdvanced" class="sr-only" />
                    <label for="levelAdvanced" class="flex items-center cursor-pointer">
                        <span
                            class="flex shrink-0 self-stretch my-auto rounded-xl border-2 border-black border-solid h-[26px] w-[25px]"></span>
                        <span class="flex-1 shrink self-stretch pl-5 my-auto min-w-[240px]">Avanzado</span>
                    </label>
                </div>
            </form>
        </section>
    </aside>
</template>