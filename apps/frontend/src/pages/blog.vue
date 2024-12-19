<script lang="ts" setup>


interface BlogPost {
  id: number
  title: string
  description: string
  tags: string[]
  date: string
  imageUrl: string
}

const blogPosts = ref<BlogPost[]>([
  {
    id: 1,
    title: "Introducción a Cortex",
    description: "Descubre cómo Cortex está revolucionando la inteligencia artificial empresarial.",
    tags: ["AI", "Tecnología", "Innovación"],
    date: "12 Dic 2024",
    imageUrl: "https://res.cloudinary.com/dukgkrpft/image/upload/v1731019897/modules/curso-de-aventuras-de-python/kkw5iykmkrlsfn2lrar3.jpg"
  },
  {
    id: 2,
    title: "Casos de Uso de Machine Learning",
    description: "Exploramos aplicaciones prácticas de machine learning en diferentes industrias.",
    tags: ["Machine Learning", "Negocios", "Estrategia"],
    date: "05 Dic 2024",
    imageUrl: "https://res.cloudinary.com/dukgkrpft/image/upload/v1729610745/modules/pistas-de-carreras-de-go/file.jpg"
  },
  {
    id: 3,
    title: "El Futuro de la IA Generativa",
    description: "Análisis profundo de las tendencias emergentes en inteligencia artificial generativa.",
    tags: ["IA Generativa", "Tendencias", "Tecnología"],
    date: "28 Nov 2024",
    imageUrl: "https://res.cloudinary.com/dukgkrpft/image/upload/v1731019929/modules/los-primeros-pasos-en-el-reino-gui-fundamentos-de-java-swing/whrhvaq5bjp3wovjvvi9.jpg"
  }
])
</script>

<template>
    <div class="container mx-auto px-4 py-8">
      <h2 class="text-4xl font-bold mb-6 text-center text-blue-600 hover:text-blue-700 transition-colors duration-300">
        Blog de Cortex
      </h2>
      <div class="grid md:grid-cols-3 gap-6">
        <Card 
          v-for="post in blogPosts" 
          :key="post.id" 
          class="hover:shadow-lg transition-shadow duration-300"
        >
          <div class="relative">
            <img 
              :src="post.imageUrl" 
              :alt="post.title" 
              class="w-full h-48 object-cover rounded-t-lg"
            />
          </div>
          <CardHeader>
            <CardTitle class="text-xl font-bold mb-2 leading-tight">
              {{ post.title }}
            </CardTitle>
            <CardDescription>{{ post.description }}</CardDescription>
          </CardHeader>
          <CardContent>
            <div class="flex flex-wrap gap-2 mb-4">
              <Badge 
                v-for="tag in post.tags" 
                :key="tag" 
                variant="secondary"
              >
                {{ tag }}
              </Badge>
            </div>
          </CardContent>
          <CardFooter class="flex justify-between items-center">
            <span class="text-sm text-muted-foreground">{{ post.date }}</span>
            <Button variant="outline" size="sm">
              Leer más 
              <ArrowRight class="ml-2 h-4 w-4" />
            </Button>
          </CardFooter>
        </Card>
      </div>
      <div class="text-center mt-8">
        <Button>
          Ver todos los posts 
          <ArrowRight class="ml-2 h-4 w-4" />
        </Button>
      </div>
    </div>
  </template>
  
  <style scoped>
  
  </style>