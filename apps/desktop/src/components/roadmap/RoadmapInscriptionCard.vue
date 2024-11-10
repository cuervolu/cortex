<script setup lang="ts">
import { ArrowRight, Rocket } from 'lucide-vue-next';



</script>

<template>
    <Card class="bg-popover dark:bg-background border-[#E6C7FF] dark:border-border rounded-3xl">
        <CardContent class="p-6 inline-flex items-center justify-between w-full">
            <div class="flex gap-3 w-9/12">
                <Avatar class="bg-[#E7C9FF] dark:bg-secondary w-12 h-12">
                    <Rocket />
                </Avatar>
                <div>
                    <h2 class="font-bold text-lg">Comienza tu Aventura</h2>
                    <span class="text-sm">En Cortex, te guiamos en cada paso para que aprendas de forma práctica y
                        efectiva. ¡Comienza ahora y acelera tu camino hacia el dominio del código!</span>
                </div>
            </div>
            <Button class="gap-2 rounded-full">
                <span>Get Started</span>
                <ArrowRight :size="18" />
            </Button>
        </CardContent>
    </Card>
</template>