<template>
  <div class="container mx-auto px-4 py-8">
    <h1 class="text-4xl font-bold text-center mb-8">Conoce a nuestros Mentores</h1>
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
        v-for="mentor in mentors"
        :key="mentor.id"
        class="bg-white shadow-md rounded-lg p-4 flex flex-col items-center text-center"
      >
        <img
          :src="mentor.photo"
          :alt="mentor.name"
          class="w-24 h-24 rounded-full object-cover mb-4"
        />
        <h2 class="text-xl font-semibold">{{ mentor.name }}</h2>
        <p class="text-gray-600 mb-4">{{ mentor.specialty }}</p>
        <p class="text-gray-700">{{ mentor.description }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import catImage from '@/assets/img/cuh-cat.gif';

const mentors = [
  {
    id: 1,
    name: "Juan Pérez",
    specialty: "Desarrollo Frontend",
    description: "Experto en Vue.js, Nuxt y tecnologías modernas de JavaScript.",
    photo: catImage,
  },
  {
    id: 2,
    name: "Ana González",
    specialty: "Machine Learning",
    description: "Ingeniera de datos con experiencia en Python y algoritmos avanzados.",
    photo: catImage,
  },
  {
    id: 3,
    name: "Carlos López",
    specialty: "DevOps y Cloud",
    description: "Especialista en AWS, Kubernetes y pipelines CI/CD.",
    photo: catImage,
  },
  {
    id: 4,
    name: "María Fernández",
    specialty: "Diseño UX/UI",
    description: "Apasionada por crear experiencias de usuario intuitivas y atractivas.",
    photo: catImage,
  },
  {
    id: 5,
    name: "Roberto Castro",
    specialty: "Seguridad Informática",
    description: "Conocedor de ciberseguridad y estrategias de protección de datos.",
    photo: catImage,
  },
];
</script>

<style scoped>
.container {
  max-width: 1200px;
}
</style>
