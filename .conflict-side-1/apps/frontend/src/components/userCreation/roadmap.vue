<template>
    <div class="min-h-screen bg-gray-50">
      <div class="max-w-7xl mx-auto p-4 pt-8">

        <div class="text-center mb-16">
          <div class="flex justify-center mb-4">
            <div class="text-4xl font-bold text-primary">
              😊
            </div>
          </div>
          <h1 class="text-4xl font-bold text-gray-900 mb-4">Cortex</h1>
          <p class="text-xl text-gray-600 max-w-3xl mx-auto mb-8">
            Estamos construyendo un espacio donde cualquiera puede aprender y dominar
            la programación de forma gratuita, sin sentirse perdido o frustrado.
          </p>
          <RouterLink 
            to="/explore" 
            class="inline-block bg-primary text-white px-6 py-3 rounded-md hover:bg-primary/80 transition-colors"
          >
            Explorar Roadmaps
          </RouterLink>
        </div>

        <div class="bg-blue-50 rounded-lg p-8 mb-16">
          <div class="flex flex-col md:flex-row items-center justify-between gap-8">
            <div class="flex-1">
              <h2 class="text-2xl font-bold text-gray-900 mb-4">Propósito</h2>
              <p class="text-gray-600">
                Estamos aquí para ayudar a todos a desarrollarse profesionalmente en
                programación, sin importar su experiencia previa. Queremos compartir
                nuestra pasión por el desarrollo y ayudar a las personas a mejorar
                sus habilidades como parte de su movilidad profesional ascendente.
              </p>
            </div>
            <div class="w-64 h-64 bg-white rounded-lg shadow-lg flex items-center justify-center">
              <img
                src="~/assets/img/Cortex%20Logo.svg"
                alt="Developer illustration"
                class="w-48 h-48 object-contain"
              />
            </div>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-8 mb-16">
          <div class="bg-white p-6 rounded-lg shadow-md">
            <div class="text-center mb-4">
              <div class="inline-block bg-indigo-100 rounded-full p-3 w-14">
                <span class="text-2xl">1</span>
              </div>
            </div>
            <h3 class="text-xl font-semibold text-center mb-2">ALCANZAR MAESTRÍA</h3>
            <p class="text-gray-600 text-center">
              Queremos que las personas experimenten la satisfacción de la programación,
              alcanzando un nivel de fluidez y maestría en el código.
            </p>
          </div>

          <div class="bg-white p-6 rounded-lg shadow-md">
            <div class="text-center mb-4">
              <div class="inline-block bg-indigo-100 rounded-full p-3 w-14">
                <span class="text-2xl">2</span>
              </div>
            </div>
            <h3 class="text-xl font-semibold text-center mb-2">DESARROLLO DIVERTIDO</h3>
            <p class="text-gray-600 text-center">
              El desarrollo de habilidades de programación debe ser empoderador y
              divertido, no competitivo, frustrante o estresante.
            </p>
          </div>

          <div class="bg-white p-6 rounded-lg shadow-md">
            <div class="text-center mb-4">
              <div class="inline-block bg-indigo-100 rounded-full p-3 w-14">
                <span class="text-2xl">3</span>
              </div>
            </div>
            <h3 class="text-xl font-semibold text-center mb-2">MOVILIDAD PROFESIONAL</h3>
            <p class="text-gray-600 text-center">
              Queremos que todos, especialmente aquellos en desventaja económica,
              logren movilidad profesional ascendente a través del código.
            </p>
          </div>
        </div>

        <div class="bg-white rounded-lg shadow-md p-8">
          <h2 class="text-2xl font-bold text-gray-900 mb-4">Roadmaps de Desarrollo</h2>
          <p class="text-gray-600 mb-6">
            Nuestros roadmaps están diseñados para maximizar el potencial de tu
            desarrollo profesional. A través de planes personalizados y mentorías
            especializadas, garantizamos una mejora significativa en tus habilidades
            técnicas.
          </p>
        </div>
      </div>
    </div>
</template>