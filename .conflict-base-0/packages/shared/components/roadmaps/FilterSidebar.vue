<script setup>
const categories = [
    {
        name: "Frontend Development",
        subcategories: ["HTML & CSS", "JavaScript", "React", "Vue.js", "Angular"]
    },
    {
        name: "Backend Development",
        subcategories: ["Node.js", "Python", "Java", "Ruby", "PHP"]
    },
    {
        name: "DevOps & Infraestructura",
        subcategories: ["AWS", "Docker", "Kubernetes", "CI/CD", "Terraform"]
    },
    {
        name: "Cybersecuridad",
        subcategories: ["Network Security", "Application Security", "Cryptography", "Penetration Testing", "Incident Response"]
    },
    {
        name: "UI/UX Design",
        subcategories: ["Wireframing", "Prototyping", "User Research", "Interaction Design", "Visual Design"]
    }
];

const ratings = [
    { id: 'rating4', label: '4.0 & Up', stars: 4 },
    { id: 'rating3', label: '3.0 & Up', stars: 3 },
    { id: 'rating2', label: '2.0 & Up', stars: 2 }
];

const levels = [
    { id: 'levelAll', label: 'All' },
    { id: 'levelBeginner', label: 'Principiante' },
    { id: 'levelIntermediate', label: 'Intermedio' },
    { id: 'levelAdvanced', label: 'Avanzado' }
];

</script>

<template>
    <aside class="flex overflow-hidden flex-col min-w-[240px] w-[368px] gap-5">

        <!-- Categorias -->
        <Card class="rounded-2xl">
            <CardContent>
                <Accordion type="single" collapsible defaultValue="item-1">
                    <AccordionItem value="item-1" class="border-none">
                        <AccordionTrigger class="text-xl">Categorias</AccordionTrigger>
                        <AccordionContent>
                            <Accordion type="single" collapsible>
                                <template v-for="(category, index) in categories" :key="index">
                                    <AccordionItem :value="category.name.toLowerCase().replace(/ /g, '-')">
                                        <AccordionTrigger class="text-lg">{{ category.name }}</AccordionTrigger>
                                        <AccordionContent>
                                            <ul class="pl-4" v-if="category.subcategories.length">
                                                <li v-for="(subcategory, subIndex) in category.subcategories" :key="subIndex">
                                                    <button :class="{'mt-2': subIndex > 0}" tabindex="0">{{ subcategory }}</button>
                                                </li>
                                            </ul>
                                        </AccordionContent>
                                    </AccordionItem>
                                </template>
                            </Accordion>
                        </AccordionContent>
                    </AccordionItem>
                </Accordion>
            </CardContent>
        </Card>

        <!-- Valoración -->
        
        <Card class="rounded-2xl">
            <CardContent>
                <Accordion type="single" collapsible>
                    <AccordionItem value="item-2" class="border-none">
                        <AccordionTrigger class="text-xl">Valoracion</AccordionTrigger>
                        <AccordionContent>
                            <Accordion class="flex flex-col gap-4" type="single" collapsible>
                                <template v-for="(rating, index) in ratings" :key="index">
                                    <div class="flex items-center space-x-2">
                                        <Checkbox :id="rating.id" />
                                        <div class="flex items-center gap-1.5">
                                            <template v-for="star in 5">
                                                <img :src="star <= rating.stars ? 'https://cdn.builder.io/api/v1/image/assets/TEMP/d419feee27b8c359f443db866c2eb9c8ed5cfd562bff9ca0725ebcd606b652fe?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245' : 'https://cdn.builder.io/api/v1/image/assets/TEMP/4cc2b6859b48876738218eb34d90372b5aae6eb8698cc7218311ed9219dc2c67?placeholderIfAbsent=true&apiKey=d42bf65751ec4e64b1dd8eba349e3245'" 
                                                    :alt="star <= rating.stars ? 'Full Star' : 'Empty Star'" 
                                                    class="w-[22px] aspect-square" />
                                            </template>
                                        </div>
                                        <Label :for="rating.id" class="text-lg font-medium leading-none">
                                            {{ rating.label }}
                                        </Label>
                                    </div>
                                </template>
                            </Accordion>
                        </AccordionContent>
                    </AccordionItem>
                </Accordion>
            </CardContent>
        </Card>

        <!-- Nivel -->
        <Card class="rounded-2xl">
            <CardContent>
                <Accordion type="single" collapsible>
                    <AccordionItem value="item-3" class="border-none">
                        <AccordionTrigger class="text-xl">Nivel</AccordionTrigger>
                        <AccordionContent>
                            <Accordion class="flex flex-col gap-4" type="single" collapsible>
                                <template v-for="(level, index) in levels" :key="index">
                                    <div class="flex items
                                    -center space-x-2">
                                        <Checkbox :id="level.id" />
                                        <Label :for="level.id" class="text-lg font-medium leading-none">
                                        {{ level.label }}
                                        </Label>
                                    </div>
                                </template>
                            </Accordion>
                        </AccordionContent>
                    </AccordionItem>
                </Accordion>
            </CardContent>
        </Card>
        
    </aside>
</template>