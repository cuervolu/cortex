<template>
    <div class="flex justify-center items-start min-h-screen bg-gradient-to-br from-gray-100 to-gray-200 py-10">
      <div class="bg-white shadow-2xl rounded-lg w-full max-w-4xl p-8 transform transition-all hover:scale-[1.01]">
        <!-- Sección de Introducción y Beneficios -->
        <section class="mb-10 text-center">
          <h1 class="text-4xl font-extrabold mb-6 text-gray-900">Programa de Mentoría</h1>
          <div class="bg-indigo-50 rounded-xl p-6 shadow-md">
            <p class="text-lg text-gray-700 mb-4 leading-relaxed">
              Un mentor es más que un instructor; es un guía dedicado que te acompaña en tu journey profesional y personal, 
              compartiendo conocimientos, experiencias y estrategias para impulsar tu desarrollo.
            </p>
          </div>
  
          <div class="grid md:grid-cols-3 gap-6 mt-8">
            <div class="bg-white rounded-xl p-6 shadow-md hover:shadow-lg transition-all">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 mx-auto mb-4 text-indigo-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <h3 class="text-xl font-bold mb-3 text-gray-800">Guía Personalizada</h3>
              <p class="text-gray-600">Recibirás orientación adaptada a tus metas y desafíos específicos.</p>
            </div>
            <div class="bg-white rounded-xl p-6 shadow-md hover:shadow-lg transition-all">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 mx-auto mb-4 text-indigo-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.768-.231-1.487-.634-2.174M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.768.231-1.487.634-2.174M14 10a4 4 0 11-8 0 4 4 0 018 0z" />
              </svg>
              <h3 class="text-xl font-bold mb-3 text-gray-800">Networking</h3>
              <p class="text-gray-600">Conexiones valiosas con profesionales de tu área de interés.</p>
            </div>
            <div class="bg-white rounded-xl p-6 shadow-md hover:shadow-lg transition-all">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-12 w-12 mx-auto mb-4 text-indigo-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <h3 class="text-xl font-bold mb-3 text-gray-800">Desarrollo Integral</h3>
              <p class="text-gray-600">Mejora tus habilidades técnicas y soft skills para crecer profesionalmente.</p>
            </div>
          </div>
        </section>
  
        <!-- Formulario de Postulación (resto del código anterior) -->
        <h2 class="text-3xl font-extrabold mb-6 text-gray-900 text-center">Postular a Mentoría</h2>
        <form @submit.prevent="submitForm">
          <!-- Resto del formulario como en el código anterior -->
          <div class="space-y-6">
            <div>
              <label 
                for="razon-postulacion" 
                class="block font-semibold mb-2 text-gray-700 transition-colors hover:text-indigo-600"
              >
                Razón de la Postulación
              </label>
              <textarea
                id="razon-postulacion"
                v-model="razonPostulacion"
                rows="5"
                required
                class="w-full border-2 border-gray-300 rounded-lg shadow-sm focus:border-indigo-500 focus:ring-2 focus:ring-indigo-300 text-gray-800 transition-all duration-300 ease-in-out hover:border-indigo-400"
                placeholder="Cuéntanos por qué quieres ser mentorado y qué esperas lograr"
              ></textarea>
            </div>
            
            <div>
              <label 
                for="conocimientos" 
                class="block font-semibold mb-2 text-gray-700 transition-colors hover:text-indigo-600"
              >
                Conocimientos
              </label>
              <textarea
                id="conocimientos"
                v-model="conocimientos"
                rows="5"
                required
                class="w-full border-2 border-gray-300 rounded-lg shadow-sm focus:border-indigo-500 focus:ring-2 focus:ring-indigo-300 text-gray-800 transition-all duration-300 ease-in-out hover:border-indigo-400"
                placeholder="Describe tus habilidades y conocimientos relevantes"
              ></textarea>
            </div>
            
            <div class="flex justify-end">
              <button
                type="submit"
                class="bg-indigo-600 text-white px-6 py-3 rounded-lg hover:bg-indigo-700 focus:outline-none focus:ring-4 focus:ring-indigo-500 focus:ring-opacity-50 transition-all duration-300 transform hover:scale-105 active:scale-95"
              >
                Enviar Postulación
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>
  
    <!-- Popup (igual que en el código anterior) -->
    <Teleport to="body">
    <transition 
      enter-active-class="transition ease-out duration-300"
      enter-from-class="opacity-0 scale-95"
      enter-to-class="opacity-100 scale-100"
      leave-active-class="transition ease-in duration-200"
      leave-from-class="opacity-100 scale-100"
      leave-to-class="opacity-0 scale-95"
    >
      <div
        v-if="showPopup"
        class="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-60 backdrop-blur-sm"
        @click.self="showPopup = false"
      >
        <div 
          class="bg-white rounded-2xl p-8 shadow-2xl w-full max-w-md transform transition-all duration-300 hover:scale-[1.02]"
          @click.stop
        >
          <div class="flex items-center mb-4">
            <svg 
              xmlns="http://www.w3.org/2000/svg" 
              class="h-10 w-10 text-green-500 mr-3" 
              fill="none" 
              viewBox="0 0 24 24" 
              stroke="currentColor"
            >
              <path 
                stroke-linecap="round" 
                stroke-linejoin="round" 
                stroke-width="2" 
                d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" 
              />
            </svg>
            <h3 class="text-2xl font-bold text-gray-800">
              ¡Postulación enviada!
            </h3>
          </div>
          <p class="text-gray-700 mb-6 text-base leading-relaxed">
            Tu postulación a la mentoría ha sido enviada correctamente. Pronto nos pondremos en contacto contigo.
          </p>
          <div class="flex justify-end space-x-3">
            <button
              class="bg-indigo-600 text-white px-6 py-2 rounded-lg hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2 transition-all transform hover:scale-105 active:scale-95"
              @click="showPopup = false"
            >
              Aceptar
            </button>
          </div>
        </div>
      </div>
    </transition>
  </Teleport>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  
  const razonPostulacion = ref('')
  const conocimientos = ref('')
  const showPopup = ref(false)
  
  const submitForm = () => {
    // Validación de campos
    if (!razonPostulacion.value.trim() || !conocimientos.value.trim()) {
      alert('Por favor, completa todos los campos')
      return
    }
  
    // Lógica para enviar la postulación
    console.log('Razón de la Postulación:', razonPostulacion.value)
    console.log('Conocimientos:', conocimientos.value)
    
    // Simular envío al backend
    showPopup.value = true
  }
  </script>
  
  <style scoped>
  /* Transiciones suaves y efectos hover */
  textarea, button {
    @apply transition-all duration-300 ease-in-out;
  }
  </style>