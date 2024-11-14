<script setup lang="ts">
import {
  BookmarkPlus,
  Plus,
  BookOpen,
  LibraryBig,
  Puzzle,
  Pencil,
  Settings,
  FileText,
  FolderPlus,
  List,
  Edit,
  Video, type LucideIcon
} from 'lucide-vue-next';
import {useNavigation} from '~/composables/useNavigation'

const {hasRole} = useNavigation()
const isAdmin = computed(() => hasRole(['ADMIN']))

interface AdminAction {
  name: string;
  description: string;
  icon: LucideIcon;
  route: { name: string, params?: Record<string, string> };
  variant: 'default' | 'secondary' | 'destructive';
  category: 'roadmaps' | 'courses' | 'modules' | 'lessons' | 'exercises' | 'general';
}

const adminActions: AdminAction[] = [
  // Roadmaps
  {
    name: 'Crear Roadmap',
    description: 'Diseña nuevas rutas de aprendizaje',
    icon: Plus,
    route: {name: 'admin-roadmaps-slug', params: {slug: 'create'}},
    variant: 'default',
    category: 'roadmaps'
  },
  // {
  //   name: 'Gestionar Roadmaps',
  //   description: 'Administra las rutas de aprendizaje existentes',
  //   icon: List,
  //   route: {name: 'admin-roadmaps'},
  //   variant: 'default',
  //   category: 'roadmaps'
  // },
  {
    name: 'Asignar Cursos',
    description: 'Asocia cursos a los roadmaps existentes',
    icon: BookmarkPlus,
    route: {name: 'admin-roadmaps-asign-courses'},
    variant: 'default',
    category: 'roadmaps'
  },
  // Cursos
  {
    name: 'Crear Curso',
    description: 'Crea un nuevo curso desde cero',
    icon: Plus,
    route: {name: 'admin-courses-create'},
    variant: 'default',
    category: 'courses'
  },
  {
    name: 'Gestionar Cursos',
    description: 'Administra el catálogo de cursos',
    icon: BookOpen,
    route: {name: 'admin-courses'},
    variant: 'default',
    category: 'courses'
  },
  {
    name: 'Editar Curso',
    description: 'Modifica cursos existentes',
    icon: Edit,
    route: {name: 'admin-courses-edit'},
    variant: 'default',
    category: 'courses'
  },
  // Módulos
  {
    name: 'Crear Módulo',
    description: 'Añade nuevos módulos a los cursos',
    icon: FolderPlus,
    route: {name: 'admin-modules-create'},
    variant: 'default',
    category: 'modules'
  },
  {
    name: 'Gestionar Módulos',
    description: 'Organiza y edita módulos existentes',
    icon: LibraryBig,
    route: {name: 'admin-modules'},
    variant: 'default',
    category: 'modules'
  },
  {
    name: 'Editar Módulo',
    description: 'Modifica módulos existentes',
    icon: Pencil,
    route: {name: 'admin-modules-edit'},
    variant: 'default',
    category: 'modules'
  },
  // Lecciones
  {
    name: 'Crear Lección',
    description: 'Añade nuevas lecciones a los módulos',
    icon: FileText,
    route: {name: 'admin-lessons-create'},
    variant: 'default',
    category: 'lessons'
  },
  {
    name: 'Gestionar Lecciones',
    description: 'Administra el contenido de las lecciones',
    icon: Video,
    route: {name: 'admin-lessons'},
    variant: 'default',
    category: 'lessons'
  },
  // Ejercicios
  {
    name: 'Crear Ejercicio',
    description: 'Crea nuevos ejercicios prácticos',
    icon: Plus,
    route: {name: 'admin-exercises-create'},
    variant: 'default',
    category: 'exercises'
  },
  {
    name: 'Gestionar Ejercicios',
    description: 'Administra ejercicios existentes',
    icon: Puzzle,
    route: {name: 'admin-exercises'},
    variant: 'default',
    category: 'exercises'
  },
  {
    name: 'Asignar Ejercicios',
    description: 'Asigna ejercicios a las lecciones',
    icon: BookmarkPlus,
    route: {name: 'admin-exercises-assign'},
    variant: 'default',
    category: 'exercises'
  }
];

const categories: Record<AdminAction['category'], string> = {
  roadmaps: 'Gestión de Roadmaps',
  courses: 'Gestión de Cursos',
  modules: 'Gestión de Módulos',
  lessons: 'Gestión de Lecciones',
  exercises: 'Gestión de Ejercicios',
  general: 'Configuración General'
};

const groupedActions = computed(() => {
  return adminActions.reduce((acc, action) => {
    if (!acc[action.category]) {
      acc[action.category] = [];
    }
    acc[action.category].push(action);
    return acc;
  }, {} as Record<string, AdminAction[]>);
});

// Stats para el dashboard
const stats = ref([
  {label: 'Roadmaps Activos', value: '12'},
  {label: 'Cursos Totales', value: '48'},
  {label: 'Usuarios Activos', value: '1,234'},
  {label: 'Ejercicios Creados', value: '856'},
]);
</script>

<template>
  <section v-if="isAdmin" class="w-full min-h-screen bg-background">
    <!-- Header -->
    <div class="border-b">
      <div class="flex h-16 items-center px-4">
        <h1 class="text-2xl font-semibold">Panel Administrativo</h1>
        <div class="ml-auto flex items-center space-x-4">
          <Button variant="outline" size="sm">
            <Settings class="mr-2 h-4 w-4"/>
            Configuración
          </Button>
        </div>
      </div>
    </div>

    <div class="flex-1 space-y-4 p-8 pt-6">
      <!-- Stats Overview -->
      <div class="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <Card v-for="stat in stats" :key="stat.label">
          <CardContent class="p-6">
            <div class="text-2xl font-bold">{{ stat.value }}</div>
            <p class="text-xs text-muted-foreground">{{ stat.label }}</p>
          </CardContent>
        </Card>
      </div>

      <!-- Acciones Administrativas por Categoría -->
      <div v-for="(actions, category) in groupedActions" :key="category" class="space-y-4">
        <div class="flex items-center justify-between">
          <h2 class="text-xl font-semibold tracking-tight">
            {{ categories[category as keyof typeof categories] }}
          </h2>
        </div>

        <div class="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
          <Card
              v-for="action in actions" :key="action.name"
              class="hover:shadow-lg transition-shadow duration-200 dark:bg-secondary/30 dark:hover:shadow-primary">
            <NuxtLink :to="action.route" class="block">
              <CardHeader>
                <div class="flex items-center space-x-4">
                  <div class="p-2 bg-primary/10 dark:bg-primary rounded-full">
                    <component :is="action.icon" class="h-5 w-5 text-primary dark:text-current"/>
                  </div>
                  <div>
                    <CardTitle>{{ action.name }}</CardTitle>
                    <CardDescription>
                      {{ action.description }}
                    </CardDescription>
                  </div>
                </div>
              </CardHeader>
            </NuxtLink>
          </Card>
        </div>
      </div>
    </div>
  </section>
</template>