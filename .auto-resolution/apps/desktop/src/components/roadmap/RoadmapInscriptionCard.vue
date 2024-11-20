<script setup lang="ts">

import { AppError, type RoadmapDetails } from '@cortex/shared/types';

import { defineProps } from 'vue';
import { useRouter } from 'vue-router';
import { useRoadmapEnrollment } from '@cortex/shared/composables/desktop/useRoadmapEnrollment'; 
import { useToast } from '@cortex/shared/components/ui/toast'; 
import { ArrowRight, Rocket } from 'lucide-vue-next';


const { enrollInRoadmap, loading } = useRoadmapEnrollment();
const { toast } = useToast();
const router = useRouter();

const props = defineProps<{
    roadmap: RoadmapDetails
}>();

const handleEnroll = async (roadmap: RoadmapDetails) => {
    try {
        await enrollInRoadmap(roadmap.id);

        toast({
            title: 'Success',
            description: 'Te has inscrito exitosamente en este roadmap',
        });

        await new Promise(resolve => setTimeout(resolve, 1500));

        router.push(`/my-roadmaps/${roadmap.slug}`);
    } catch (err: any) {
        if (err === 'API error: Already enrolled in this roadmap') {
            toast({
                title: 'Error',
                description: 'Ya estás inscrito en este roadmap',
            });
        } else {
            toast({
                title: 'Error',
                description: 'Ocurrió un error inesperado',
            });
        }
    }
};


</script>

<template>
    <Card class="bg-popover dark:bg-background border-[#E6C7FF] dark:border-border rounded-3xl">
        <CardContent class="p-6 inline-flex items-center justify-between w-full">
            <div class="flex gap-3 w-9/12">
                <Avatar class="bg-[#E7C9FF] dark:bg-secondary w-12 h-12">
                    <Rocket/>
                </Avatar>
                <div>
                    <h2 class="font-bold text-lg">Comienza tu Aventura</h2>
                    <span class="text-sm">
                        En Cortex, te guiamos en cada paso para que aprendas de forma práctica y
                        efectiva. ¡Comienza ahora y acelera tu camino hacia el dominio de la informática!
                    </span>
                </div>
            </div>
            <Button 
                class="gap-2 rounded-full"
                :disabled="loading"
                @click="handleEnroll(roadmap)"
            >
                <span>Get Started</span>
                <ArrowRight :size="18" />
            </Button>
        </CardContent>
    </Card>
    <Toaster />
</template>
