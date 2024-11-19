<script setup lang="ts">
import {Code, Users, Laptop, GraduationCap, Brain, Target, Shield} from 'lucide-vue-next'
import xdImage from '~/assets/img/xd.webp'
import cinnamorollImage from '~/assets/img/cinnamoroll.webp'
import godImage from '~/assets/img/god.webp'

const testimonios = [
  {
    nombre: "Vicente Zapata",
    rol: "Profesor de Informática",
    avatar: xdImage,
    comentario:
        "Cortex me ha ayudado a comprender conceptos complejos de una manera clara y práctica. ¡Increíble plataforma!",
  },
  {
    nombre: "Cristopher Hurtado",
    rol: "Desarrollador Junior",
    avatar: cinnamorollImage,
    comentario:
        "Gracias a Cortex, pude aprender a programar desde cero y ahora tengo mi propia empresa de desarrollo web.",
  },
  {
    nombre: "Jeanette Herminia Leonelli Riffo",
    rol: "Profesora de Informática",
    avatar: godImage,
    comentario:
        "Uso Cortex como complemento en mis clases. El contenido es excelente y siempre actualizado. Nunca me ha fallado y por eso les di un 7.",
  },
];
</script>

<template>
  <section class="py-16 px-4 md:px-6 lg:px-8 bg-gradient-to-b from-background to-muted/50">
    <div class="max-w-6xl mx-auto space-y-16">
      <!-- Header Card -->
      <Card class="border-none shadow-xl bg-background/60 backdrop-blur">
        <CardHeader>
          <CardTitle
              class="text-4xl font-bold text-center bg-gradient-to-r from-primary to-primary-foreground bg-clip-text text-transparent">
            Sobre Cortex
          </CardTitle>
          <CardDescription class="text-center text-lg">
            Transformando la educación en tecnología
          </CardDescription>
        </CardHeader>
      </Card>

      <!-- Main Content -->
      <div class="grid grid-cols-1 md:grid-cols-2 gap-12 items-center">
        <div class="relative group">
          <div
              class="absolute -inset-1 bg-gradient-to-r from-primary to-primary-foreground rounded-lg blur opacity-25 group-hover:opacity-75 transition duration-1000"></div>
          <img
              src="~/assets/img/cortex.webp"
              alt="Estudiantes aprendiendo con Cortex"
              class="relative rounded-lg shadow-2xl w-full h-auto transform group-hover:scale-[1.01] transition duration-500"
          />
        </div>

        <Card class="border-none shadow-xl bg-background/60 backdrop-blur">
          <CardHeader>
            <CardTitle
                class="text-2xl font-semibold bg-gradient-to-r from-primary to-primary-foreground bg-clip-text text-transparent">
              Educación en Ciencias de la Computación para Todos
            </CardTitle>
          </CardHeader>
          <CardContent class="space-y-6">
            <p class="text-muted-foreground text-lg leading-relaxed">
              Cortex es una plataforma educativa de código abierto dedicada a enseñar
              ciencias de la computación de manera accesible y efectiva. Nuestra
              misión es democratizar la educación en tecnología, ofreciendo cursos de
              alta calidad de forma gratuita y en línea.
            </p>
            <div class="grid grid-cols-2 gap-4">
              <div class="flex items-start space-x-2">
                <Code class="w-5 h-5 mt-1 text-primary"/>
                <span class="text-muted-foreground">Cursos interactivos y prácticos</span>
              </div>
              <div class="flex items-start space-x-2">
                <Users class="w-5 h-5 mt-1 text-primary"/>
                <span class="text-muted-foreground">Comunidad de aprendizaje colaborativo</span>
              </div>
              <div class="flex items-start space-x-2">
                <Laptop class="w-5 h-5 mt-1 text-primary"/>
                <span class="text-muted-foreground">Contenido actualizado y relevante</span>
              </div>
              <div class="flex items-start space-x-2">
                <GraduationCap class="w-5 h-5 mt-1 text-primary"/>
                <span class="text-muted-foreground">Certificaciones reconocidas</span>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      <!-- Testimonials Section -->
      <div class="space-y-8">
        <Card class="border-none shadow-xl bg-background/60 backdrop-blur">
          <CardHeader>
            <CardTitle
                class="text-3xl font-bold text-center bg-gradient-to-r from-primary to-primary-foreground bg-clip-text text-transparent">
              Lo que dicen nuestros estudiantes
            </CardTitle>
            <CardDescription class="text-center text-lg">
              Experiencias reales de nuestra comunidad
            </CardDescription>
          </CardHeader>
          <CardContent class="px-8 pb-8">
            <Carousel class="w-full">
              <CarouselContent>
                <CarouselItem v-for="(testimonio, index) in testimonios" :key="index"
                              class="md:basis-1/2 lg:basis-1/3">
                  <Card
                      class="h-full border shadow-md hover:shadow-xl transition-shadow duration-300">
                    <CardHeader>
                      <div class="flex items-center space-x-4">
                        <Avatar class="h-12 w-12 ring-2 ring-primary/20">
                          <AvatarImage :src="testimonio.avatar" :alt="testimonio.nombre"/>
                          <AvatarFallback class="bg-primary/10">{{
                              testimonio.nombre[0]
                            }}
                          </AvatarFallback>
                        </Avatar>
                        <div>
                          <h4 class="font-semibold">{{ testimonio.nombre }}</h4>
                          <p class="text-sm text-muted-foreground">{{ testimonio.rol }}</p>
                        </div>
                      </div>
                    </CardHeader>
                    <CardContent>
                      <p class="text-muted-foreground italic">
                        "{{ testimonio.comentario }}"
                      </p>
                    </CardContent>
                  </Card>
                </CarouselItem>
              </CarouselContent>
              <CarouselPrevious class="hidden md:flex"/>
              <CarouselNext class="hidden md:flex"/>
            </Carousel>
          </CardContent>
        </Card>
      </div>
      <!-- How Cortex Works Section -->
      <div class="space-y-8">
        <Card class="border-none shadow-xl bg-background/60 backdrop-blur">
          <CardHeader>
            <CardTitle
                class="text-3xl font-bold text-center bg-gradient-to-r from-primary to-primary-foreground bg-clip-text text-transparent">
              ¿Cómo funciona Cortex?
            </CardTitle>
            <CardDescription class="text-center text-lg">
              Desarrollando el futuro de la educación tecnológica
            </CardDescription>
          </CardHeader>
          <CardContent class="px-8 pb-8">
            <div class="grid grid-cols-1 gap-8 md:grid-cols-3">
              <!-- Reflective Section -->
              <Card
                  class="border border-primary/10 shadow-lg hover:shadow-xl transition-all duration-300 bg-background/40">
                <CardHeader className="space-y-4 text-center">
                  <div
                      class="mx-auto bg-primary/10 w-16 h-16 rounded-full flex items-center justify-center">
                    <Brain class="w-8 h-8 text-primary"/>
                  </div>
                  <CardTitle class="text-xl">Reflexivos</CardTitle>
                </CardHeader>
                <CardContent>
                  <p class="text-muted-foreground text-center">
                    Nos enfocamos en estrategias orientadas al producto, mirando más allá del
                    impacto inmediato. Trabajamos con colaboradores clave para mejorar Cortex
                    diariamente, manteniendo siempre nuestra visión a largo plazo.
                  </p>
                </CardContent>
              </Card>

              <!-- Small Team Section -->
              <Card
                  class="border border-primary/10 shadow-lg hover:shadow-xl transition-all duration-300 bg-background/40">
                <CardHeader className="space-y-4 text-center">
                  <div
                      class="mx-auto bg-primary/10 w-16 h-16 rounded-full flex items-center justify-center">
                    <Target class="w-8 h-8 text-primary"/>
                  </div>
                  <CardTitle class="text-xl">Equipo Pequeño, Gran Visión</CardTitle>
                </CardHeader>
                <CardContent>
                  <p class="text-muted-foreground text-center">
                    Priorizamos la calidad en todo lo que hacemos. Nuestro equipo central asegura
                    que Cortex evolucione alineado con su visión. A través de donaciones, expandimos
                    nuestro equipo de manera sostenible y responsable.
                  </p>
                </CardContent>
              </Card>

              <!-- Ethics Section -->
              <Card
                  class="border border-primary/10 shadow-lg hover:shadow-xl transition-all duration-300 bg-background/40">
                <CardHeader className="space-y-4 text-center">
                  <div
                      class="mx-auto bg-primary/10 w-16 h-16 rounded-full flex items-center justify-center">
                    <Shield class="w-8 h-8 text-primary"/>
                  </div>
                  <CardTitle class="text-xl">Basados en la Ética</CardTitle>
                </CardHeader>
                <CardContent>
                  <p class="text-muted-foreground text-center">
                    Actuamos éticamente como organización, tratando a nuestro equipo con respeto. No
                    utilizamos técnicas o tecnologías que no quisiéramos que se usaran con nosotros
                    mismos.
                  </p>
                </CardContent>
              </Card>
            </div>

            <!-- Bottom Description -->
            <div class="mt-8 text-center max-w-3xl mx-auto">
              <p class="text-muted-foreground text-lg">
                Cortex se desarrolla a través de una combinación de liderazgo estratégico y una
                comunidad colaborativa. Creemos que cada decisión merece su tiempo de análisis, por
                lo que pensamos cuidadosamente antes de actuar.
              </p>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  </section>
</template>

