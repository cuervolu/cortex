<script setup lang="ts">
import { ref } from 'vue'


const courses = ref([
  {
    id: 1,
    title: 'Introducción a Vue.js 3',
    description: 'Aprende los fundamentos de Vue.js 3 y Composition API',
    thumbnail: '/api/placeholder/400/250',
    students: 125,
    progress: 80,
    lastUpdated: '2024-03-20',
    status: 'published'
  },
  {
    id: 2,
    title: 'TypeScript Avanzado',
    description: 'Domina TypeScript y sus características avanzadas',
    thumbnail: '/api/placeholder/400/250',
    students: 89,
    progress: 100,
    lastUpdated: '2024-03-18',
    status: 'published'
  },
  // Puedes agregar más cursos de ejemplo aquí
])
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-7xl mx-auto p-4 pt-8">
      <!-- Header -->
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold text-gray-900">Mis Cursos</h1>
        <button class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors">
          Crear Curso
        </button>
      </div>

      <!-- Grid de Cursos -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div v-for="course in courses" :key="course.id" class="bg-white rounded-lg shadow-lg overflow-hidden">
          <!-- Thumbnail -->
          <img :src="course.thumbnail" :alt="course.title" class="w-full h-48 object-cover">
          
          <div class="p-6">
            <!-- Badge de estado -->
            <div class="flex justify-between items-center mb-4">
              <span 
                class="px-2 py-1 text-sm rounded-full"
                :class="{
                  'bg-green-100 text-green-800': course.status === 'published',
                  'bg-yellow-100 text-yellow-800': course.status === 'draft'
                }"
              >
                {{ course.status === 'published' ? 'Publicado' : 'Borrador' }}
              </span>
              <span class="text-sm text-gray-500">{{ course.students }} estudiantes</span>
            </div>

            <h3 class="text-xl font-semibold text-gray-900 mb-2">{{ course.title }}</h3>
            <p class="text-gray-600 mb-4">{{ course.description }}</p>
            
            <!-- Barra de progreso -->
            <div class="w-full bg-gray-200 rounded-full h-2.5 mb-4">
              <div class="bg-blue-600 h-2.5 rounded-full" :style="{ width: `${course.progress}%` }" />
            </div>
            
            <!-- Footer -->
            <div class="flex justify-between items-center text-sm text-gray-500">
              <span>Actualizado: {{ new Date(course.lastUpdated).toLocaleDateString() }}</span>
              <div class="flex gap-3">
                <button class="text-blue-600 hover:text-blue-700">Editar</button>
                <button class="text-blue-600 hover:text-blue-700">Ver</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>