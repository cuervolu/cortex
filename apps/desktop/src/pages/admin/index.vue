<script setup lang="ts">
import {UserPlus, Users, Plus, BookOpen} from 'lucide-vue-next'

type ButtonVariant = 'default' | 'destructive' | 'outline' | 'secondary' | 'ghost' | 'link'

interface Action {
  label: string
  icon: any
  route: string
  variant: ButtonVariant
}

interface ActionSection {
  title: string
  description: string
  actions: Action[]
}

const {data: session} = useAuth()

const stats = [
  {label: 'Usuarios Activos', value: '1,234'},
  {label: 'Roadmaps Creados', value: '12'},
  {label: 'Cursos Activos', value: '48'},
  {label: 'Ejercicios Totales', value: '856'},
]

const mainActions: ActionSection[] = [
  {
    title: 'Gestión de Usuarios',
    description: 'Administra los usuarios de la plataforma',
    actions: [
      {
        label: 'Añadir Usuario',
        icon: UserPlus,
        route: '/admin/users/create',
        variant: 'default' as ButtonVariant
      },
      {
        label: 'Ver Usuarios',
        icon: Users,
        route: '/admin/users',
        variant: 'outline' as ButtonVariant
      }
    ]
  },
  {
    title: 'Gestión de Roadmaps',
    description: 'Administra las rutas de aprendizaje',
    actions: [
      {
        label: 'Crear Roadmap',
        icon: Plus,
        route: '/admin/roadmaps/create',
        variant: 'default' as ButtonVariant
      },
      {
        label: 'Ver Roadmaps',
        icon: BookOpen,
        route: '/admin/roadmaps',
        variant: 'outline' as ButtonVariant
      }
    ]
  }
]
</script>

<template>
  <div class="container mx-auto p-4 sm:p-8 space-y-8">
    <!-- Header -->
    <div class="space-y-2">
      <h1 class="text-3xl font-bold tracking-tight">
        Bienvenido, {{ session?.first_name }}
      </h1>
      <p class="text-muted-foreground">
        Esto es lo que está ocurriendo hoy con su plataforma.
      </p>
    </div>

    <!-- Stats Overview -->
    <div class="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
      <Card v-for="stat in stats" :key="stat.label" class="bg-card">
        <CardContent class="p-6">
          <div class="text-2xl font-bold">{{ stat.value }}</div>
          <p class="text-xs text-muted-foreground">{{ stat.label }}</p>
        </CardContent>
      </Card>
    </div>

    <!-- Main Actions -->
    <div class="grid gap-6 sm:grid-cols-1 lg:grid-cols-2">
      <Card v-for="section in mainActions" :key="section.title" class="bg-card">
        <CardHeader>
          <CardTitle>{{ section.title }}</CardTitle>
          <CardDescription>{{ section.description }}</CardDescription>
        </CardHeader>
        <CardContent class="flex gap-4">
          <div class="flex flex-col sm:flex-row gap-4 w-full">
            <Button
                v-for="action in section.actions"
                :key="action.label"
                :variant="action.variant"
                @click="navigateTo(action.route)"
                class="w-full sm:w-auto"
            >
              <component :is="action.icon" class="mr-2 h-4 w-4"/>
              {{ action.label }}
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  </div>
</template>