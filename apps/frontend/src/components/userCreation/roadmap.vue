<template>
  <div class="min-h-screen bg-gray-50">
    <div class="max-w-7xl mx-auto p-4 pt-8 ">

      <div class="text-center mb-12">
        <h1 class="text-3xl font-bold text-gray-900 mb-4">Roadmaps de Desarrollo Profesional</h1>
        <p class="text-gray-600 max-w-3xl mx-auto mb-8">
          Nuestros roadmaps están diseñados para maximizar el potencial de tu equipo tecnológico. 
          A través de planes personalizados y mentorías especializadas, garantizamos una mejora 
          significativa en las habilidades técnicas, eficiencia operativa y productividad general. 
          Cada roadmap incluye evaluaciones periódicas, contenido actualizado y soporte continuo 
          para asegurar el éxito en el desarrollo profesional de tu equipo.
        </p>
      </div>

      <div class="flex justify-center max-w-xl mx-auto">
        <div class="grid grid-cols-1 gap-6 max-w-md">
          <RouterLink to="/explore" class="flex items-center">
            <div class="bg-white p-6 rounded-lg shadow-md hover:shadow-lg transition-shadow duration-300">
              <h2 class="text-xl font-semibold text-gray-900 mb-2">Explorar Roadmaps</h2>
              <p class="text-gray-600">Explora nuestro programa de formación técnica personalizada</p>
            </div>
          </RouterLink>
        </div>
      </div>

    </div>
  </div>
</template>