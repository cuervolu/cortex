<script setup lang="ts">
import { ref } from 'vue'



const roadmaps = ref([
  {
    id: 1,
    title: 'Desarrollo Web Frontend',
    description: 'Ruta de aprendizaje completa para convertirse en desarrollador Frontend',
    progress: 65,
    lastUpdated: '2024-03-15',
    technologies: ['HTML', 'CSS', 'JavaScript', 'React']
  },
  {
    id: 2,
    title: 'Backend con Node.js',
    description: 'Aprende desarrollo Backend con Node.js y Express',
    progress: 30,
    lastUpdated: '2024-03-10',
    technologies: ['Node.js', 'Express', 'MongoDB']
  },
  // Puedes agregar más roadmaps de ejemplo aquí
])
</script>

<template>
    <div class="min-h-screen bg-gray-50">
      <div class="max-w-7xl mx-auto p-4 pt-8">
        <!-- Header -->
        <div class="flex justify-between items-center mb-6">
          <h1 class="text-2xl font-bold text-gray-900">Mis Roadmaps</h1>
          <button class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors">
            Crear Roadmap
          </button>
        </div>
  
        <!-- Grid de Roadmaps -->
        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <div v-for="roadmap in roadmaps" :key="roadmap.id" class="bg-white rounded-lg shadow-lg overflow-hidden">
            <div class="p-6">
              <h3 class="text-xl font-semibold text-gray-900 mb-2">{{ roadmap.title }}</h3>
              <p class="text-gray-600 mb-4">{{ roadmap.description }}</p>
              
              <!-- Barra de progreso -->
              <div class="w-full bg-gray-200 rounded-full h-2.5 mb-4">
                <div class="bg-blue-600 h-2.5 rounded-full" :style="{ width: `${roadmap.progress}%` }" />
              </div>
              
              <!-- Tecnologías -->
              <div class="flex flex-wrap gap-2 mb-4">
                <span 
                  v-for="tech in roadmap.technologies" 
                  :key="tech"
                  class="px-2 py-1 bg-gray-100 text-gray-600 text-sm rounded-md"
                >
                  {{ tech }}
                </span>
              </div>
              
              <!-- Footer -->
              <div class="flex justify-between items-center text-sm text-gray-500">
                <span>Última actualización: {{ new Date(roadmap.lastUpdated).toLocaleDateString() }}</span>
                <button class="text-blue-600 hover:text-blue-700">Editar</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>