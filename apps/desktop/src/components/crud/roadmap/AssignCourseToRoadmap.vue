<script setup lang="ts">
import Card from '@cortex/shared/components/ui/card/Card.vue';
import dragable from 'vuedraggable';

import { defineComponent, ref } from 'vue';
import { Search } from 'lucide-vue-next';

const drag = ref(true);

const dragOptions = ref({
    animation: 200,
    group: "description",
    disabled: false,
    forceFallback: true,
});


const generateList = (startIndex: number, count: number) => {
    return Array.from({ length: count }, (_, i) => ({
        name: `Item ${startIndex + i}`,
        order: startIndex + i,
        fixed: false
    }));
};

const list1 = ref(generateList(1, 3));
const list2 = ref(generateList(4, 8));


defineComponent({
    components: {
        dragable,
    },
});

//Filtrar items con search

const searchQueryList1 = ref('');
const searchQueryList2 = ref('');

// Manejadores de eventos para Tauri
const onDragStart = (evt: any) => {
    drag.value = true;
    evt.preventDefault();
};

const onDragEnd = (evt: any) => {
    drag.value = false;
    evt.preventDefault();
};

</script>

<template>
    <div class="flex flex-col w-full p-8">
        <div class="pb-5">
            <h2 class="text-3xl font-semibold">Asignar Cursos Roadmap</h2>
        </div>
        <div class="flex flex-wrap gap-3">
            <Card class="flex-1 min-w-[300px] xl:min-w-[370px] h-fit">
                <CardHeader>
                    <h2 class="text-2xl font-semibold">Roadmap</h2>
                </CardHeader>
                <CardContent>
                    <div class="flex flex-col gap-4">
                        <div class="flex items-center gap-3 rounded-xl border overflow-hidden relative">
                            <img src="http://res.cloudinary.com/dukgkrpft/image/upload/v1729610776/roadmaps/la-busqueda-del-castillo-typescript/file.jpg" alt="Roadmap Image" class="w-full h-[230px] object-cover">
                            <div class="absolute inset-0 bg-gradient-to-t from-black/80 to-transparent"></div>
                            <div class="absolute bottom-4 left-4 text-white flex flex-col gap-2">
                                <h3 class="text-xl font-bold">La Búsqueda del Castillo TypeScript</h3>
                                <div class="flex gap-2">
                                    <Badge class="text-nowrap">TypeScript</Badge>
                                    <Badge class="text-nowrap">JavaScript</Badge>
                                    <Badge class="text-nowrap">Desarrollo Web</Badge>
                                </div>
                            </div>
                        </div>
                    </div>
                </CardContent>
                <CardFooter class="flex flex-col">
                    <div class="w-full">
                        <span class="text-lg font-semibold text-start">Cursos Asignados: {{ list1.length }}</span>
                    </div>
                    <div class="w-full">
                        <Button class="w-full mt-4">Guardar</Button>
                        <Button class="w-full mt-4" variant="outline">Cancelar</Button>
                    </div>
                </CardFooter>
            </Card>
            <div class="flex flex-wrap gap-5 justify-between w-full xl:w-3/5">
                <div class="basis-[350px] bg-popover/80 border-secondary-foreground dark:border-border dark:bg-card grow flex flex-col gap-4 border-2 border-dashed rounded-2xl p-4">
                    <h2 class="text-lg font-bold">Cursos Roadmap</h2>
                    <div class="relative w-full items-center">
                        <Input id="search" v-model="searchQueryList1" type="text" placeholder="Search..." class="pl-10" />
                        <span class="absolute start-0 inset-y-0 flex items-center justify-center px-2">
                        <Search class="size-6 text-muted-foreground" />
                        </span>
                    </div>
                    <ScrollArea class="flex max-h-[400px] pr-3">
                        <dragable class="w-full h-full min-h-200px" :component-data="{
                            type: 'transition-group',
                            name: !drag ? 'flip-list' : null
                            }" v-model="list1" v-bind="dragOptions" @start="drag = true" @end="drag = false" item-key="order">
                            <template #item="{ element }">
                                <Card v-if="element.name.toLowerCase().includes(searchQueryList1.toLowerCase())" 
                                    class="bg-white dark:bg-secondary rounded-lg shadow-sm p-4 mb-2 flex items-center justify-between hover:bg-gray-50 dark:hover:bg-secondary/40 transition-colors card-content">
                                    <div class="flex items-center gap-3">
                                        <i :class="[
                                            element.fixed ? 'fa fa-anchor' : 'glyphicon glyphicon-pushpin',
                                            'text-gray-600 hover:text-blue-600 transition-colors'
                                        ]" 
                                        @click="element.fixed = !element.fixed" 
                                        aria-hidden="true"></i>
                                        <span>{{ element.name }}</span>
                                    </div>
                                </Card>
                            </template>
                            <template #header v-if="list1.length === 0">
                                <div class="text-gray-500 text-center py-4">
                                    No hay elementos en la lista
                                </div>
                            </template>
                        </dragable>
                        <ScrollBar orientation="vertical"/>
                    </ScrollArea>
                </div>
                <div class="basis-[350px] bg-popover/80 border-secondary-foreground dark:border-border dark:bg-card grow flex flex-col gap-4 border-2 border-dashed rounded-2xl p-4">
                    <h2 class="text-lg font-bold">Cursos</h2>
                    <div class="relative w-full items-center">
                        <Input id="search" v-model="searchQueryList2" type="text" placeholder="Search..." class="pl-10" />
                        <span class="absolute start-0 inset-y-0 flex items-center justify-center px-2">
                        <Search class="size-6 text-muted-foreground" />
                        </span>
                    </div>
                    <ScrollArea class="flex max-h-[400px] pr-3">
                        <dragable class="w-full h-full min-h-[200px]" :component-data="{
                            type: 'transition-group',
                            name: !drag ? 'flip-list' : null
                            }" v-model="list2" v-bind="dragOptions" @start="drag = true" @end="drag = false" item-key="order">
                            <template #item="{ element }">
                                <Card v-if="element.name.toLowerCase().includes(searchQueryList2.toLowerCase())"
                                class="bg-white dark:bg-secondary rounded-lg shadow-sm p-4 mb-2 flex items-center justify-between hover:bg-gray-50 dark:hover:bg-secondary/40 transition-colors card-content">
                                    <div class="flex items-center gap-3">
                                        <i :class="[
                                            element.fixed ? 'fa fa-anchor' : 'glyphicon glyphicon-pushpin',
                                            'text-gray-600 hover:text-blue-600 transition-colors'
                                        ]" 
                                        @click="element.fixed = !element.fixed" 
                                        aria-hidden="true"></i>
                                        <span>{{ element.name }}</span>
                                    </div>
                                </Card>
                            </template>
                            <template #header v-if="list2.length === 0">
                                <div class="text-gray-500 text-center py-4">
                                    No hay elementos en la lista
                                </div>
                            </template>
                        </dragable>
                    </ScrollArea>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
/* Aplicar no-select específicamente a los elementos de la tarjeta */
.card-content {
    user-select: none;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
}
</style>