<script setup lang="ts">
const sections = [
  {
    id: 'general',
    title: 'Información General',
    tabs: ['introduccion', 'descripcion', 'elegibilidad']
  },
  {
    id: 'privacidad',
    title: 'Privacidad y Datos',
    tabs: ['datos-personales', 'proteccion']
  },
  {
    id: 'uso',
    title: 'Uso de la Plataforma',
    tabs: ['interaccion', 'prueba', 'licencia']
  },
  {
    id: 'legal',
    title: 'Aspectos Legales',
    tabs: ['modificaciones', 'terminacion', 'responsabilidad']
  }
]

const terms = {
  introduccion: {
    title: 'Introducción',
    content: `Bienvenido a Cortex, una plataforma educativa desarrollada y operada por Bytech SpA ("nosotros", "nuestro", "la Empresa"), ubicada en Cam. de La Laguna 14341, 7700251 Lo Barnechea, Región Metropolitana, Chile.`
  },
  descripcion: {
    title: 'Descripción del Servicio',
    content: 'Cortex es una plataforma educativa de código abierto diseñada para facilitar el aprendizaje de tecnologías de la información y programación, con un enfoque especial en jóvenes y niños.'
  },
  elegibilidad: {
    title: 'Elegibilidad',
    content: 'La edad mínima para utilizar Cortex es de 6 años. Para usuarios menores de 18 años, se requiere el consentimiento expreso de un padre, madre o tutor legal.'
  },
  'datos-personales': {
    title: 'Datos Personales',
    content: 'Recopilamos datos como nombre, correo electrónico y edad para proporcionar nuestros servicios.'
  },
  proteccion: {
    title: 'Protección de Datos',
    content: 'Cumplimos con todas las leyes y regulaciones aplicables de protección de datos en Chile.'
  },
  interaccion: {
    title: 'Interacción',
    content: 'La plataforma permite la interacción entre usuarios y el intercambio de contenido siguiendo nuestras normas de conducta.'
  },
  prueba: {
    title: 'Período de Prueba',
    content: 'Ofrecemos un período de prueba gratuito de 14 días para explorar las características premium.'
  },
  licencia: {
    title: 'Licencia',
    content: 'Cortex es una plataforma de código abierto bajo términos de licencia específicos.'
  },
  modificaciones: {
    title: 'Modificaciones',
    content: 'Nos reservamos el derecho de modificar estos términos y condiciones en cualquier momento.'
  },
  terminacion: {
    title: 'Terminación',
    content: 'Podemos terminar o suspender el acceso por incumplimiento de estos términos.'
  },
  responsabilidad: {
    title: 'Responsabilidad',
    content: 'Bytech SpA no será responsable por daños indirectos, incidentales o consecuentes.'
  }
}
</script>

<template>
  <Card class="w-full max-w-4xl mx-auto">
    <CardHeader>
      <CardTitle class="text-2xl font-bold">Términos y Condiciones - Cortex</CardTitle>
      <CardDescription>Última actualización: 19 de noviembre de 2024</CardDescription>
    </CardHeader>

    <CardContent>
      <Breadcrumb class="mb-6">
        <BreadcrumbList>
          <BreadcrumbItem>
            <BreadcrumbLink href="/">Inicio</BreadcrumbLink>
          </BreadcrumbItem>
          <BreadcrumbSeparator />
          <BreadcrumbItem>
            <BreadcrumbPage>Términos y Condiciones</BreadcrumbPage>
          </BreadcrumbItem>
        </BreadcrumbList>
      </Breadcrumb>

      <Tabs v-for="section in sections" :key="section.id" :default-value="section.tabs[0]" class="mb-8">
        <div class="mb-4">
          <h2 class="text-xl font-semibold">{{ section.title }}</h2>
          <Separator class="my-2" />
        </div>

        <TabsList class="mb-4">
          <TabsTrigger
              v-for="tab in section.tabs"
              :key="tab"
              :value="tab"
          >
            {{ terms[tab].title }}
          </TabsTrigger>
        </TabsList>

        <ScrollArea class="h-48 rounded-md border p-4">
          <TabsContent
              v-for="tab in section.tabs"
              :key="tab"
              :value="tab"
          >
            <h3 class="text-lg font-medium mb-2">{{ terms[tab].title }}</h3>
            <p class="text-sm text-gray-600">{{ terms[tab].content }}</p>
          </TabsContent>
        </ScrollArea>
      </Tabs>
    </CardContent>
  </Card>
</template>

<style scoped>
.scrollbar {
  scrollbar-width: thin;
  scrollbar-color: #cbd5e0 #f7fafc;
}

.scrollbar::-webkit-scrollbar {
  width: 8px;
}

.scrollbar::-webkit-scrollbar-track {
  background: #f7fafc;
}

.scrollbar::-webkit-scrollbar-thumb {
  background-color: #cbd5e0;
  border-radius: 4px;
}
</style>