<script setup lang="ts">
import {
  AlarmClock,
  BookOpen,
  FileBadge,
  LayoutList,
  NotebookText,
  Search,
  Users,
  Clock,
  ChevronRight,
  Star,
  Bell,
  MessageCircle
} from 'lucide-vue-next'
import {computed, ref} from 'vue'
import StudentIcon from '@cortex/shared/components/icons/StudentIcon.vue';
import InstagramIcon from '@cortex/shared/components/icons/InstagramIcon.vue';
import TwitterIcon from '@cortex/shared/components/icons/TwitterIcon.vue';
import LinkedinIcon from '@cortex/shared/components/icons/LinkedinIcon.vue';
import GithubIcon from '@cortex/shared/components/icons/GithubIcon.vue';
import type {RoadmapMentor} from '@cortex/shared/types';
import {VueMarkdownIt} from "@f3ve/vue-markdown-it";

export interface RoadmapOverviewProps {
  description: string
  createdAt: string
  updatedAt: string | null
  mentor: RoadmapMentor
  options: MarkdownIt.Options
}

const props = defineProps<RoadmapOverviewProps>()

const formatDate = (date: string) => new Date(date).toLocaleDateString()

// obtener iniciales mentor con mentor.full_name
const initials = computed(() => {
  const [firstName, lastName] = props.mentor.full_name.split(' ')
  return `${firstName.charAt(0)}${lastName.charAt(0)}`
})

const activeTab = ref('panorama')

// Dummy data
const notes = [
  { id: 1, title: 'Introduction to React', content: 'React is a JavaScript library for building user interfaces. It allows developers to create reusable UI components that can be composed to build complex applications.', tags: ['basics', 'components'], lastUpdated: '2023-06-10' },
  { id: 2, title: 'State Management in React', content: 'Learn about useState for local component state and useContext for sharing state across components without prop drilling. Explore how to manage complex state with useReducer.', tags: ['state', 'hooks'], lastUpdated: '2023-06-12' },
  { id: 3, title: 'React Hooks Overview', content: 'Explore various React hooks like useEffect for side effects, useMemo for memoizing expensive computations, and useCallback for memoizing functions. Understand how custom hooks can encapsulate and share stateful logic.', tags: ['hooks', 'advanced'], lastUpdated: '2023-06-15' },
]

const announcements = [
  { id: 1, title: 'New Advanced React Patterns Course', date: '2023-06-15', content: "We're excited to announce our new course on Advanced React Patterns! This course covers higher-order components, render props, compound components, and more. Early bird discount available for the first 100 enrollments.", icon: BookOpen },
  { id: 2, title: 'Live Q&A Session with React Experts', date: '2023-06-20', content: 'Join us for an exclusive live Q&A session with industry-leading React experts. Get your burning questions answered and gain insights into best practices and upcoming trends in React development. Limited spots available!', icon: Users },
  { id: 3, title: 'Platform Maintenance Notice', date: '2023-06-25', content: 'Our platform will undergo scheduled maintenance on June 25th from 2-4 AM EST to improve performance and introduce new features. During this time, the platform will be inaccessible. We apologize for any inconvenience and appreciate your patience.', icon: Clock },
]

const reviews = [
  { id: 1, author: 'John Doe', avatar: '/placeholder.svg?height=40&width=40', rating: 5, content: 'This React course is phenomenal! The instructor breaks down complex concepts into easily digestible chunks. I especially loved the sections on hooks and state management. Highly recommended for anyone looking to level up their React skills.', helpful: 42, date: '2023-06-01' },
  { id: 2, author: 'Jane Smith', avatar: '/placeholder.svg?height=40&width=40', rating: 4, content: 'Very informative course with a wealth of practical examples. The content on advanced patterns was particularly enlightening. My only suggestion would be to include more hands-on exercises to reinforce the concepts learned.', helpful: 28, date: '2023-06-05' },
  { id: 3, author: 'Bob Johnson', avatar: '/placeholder.svg?height=40&width=40', rating: 5, content: 'As someone transitioning from Angular to React, this course was a godsend. The instructor\'s clear explanations and real-world examples made the learning process smooth and enjoyable. I feel much more confident in my React skills now!', helpful: 35, date: '2023-06-08' },
]

</script>

<template>
  <Tabs defaultValue="panorama" class="relative mr-auto w-full">
    <TabsList class="w-full justify-start rounded-none border-b bg-transparent p-0">
      <div class="mx-3">
        <Search class="h-4 w-4 text-muted-foreground" />
      </div>
      <TabsTrigger
        v-for="tab in ['panorama', 'notas', 'comunicados', 'reseñas']"
        :key="tab"
        :value="tab"
        class="relative rounded-none border-b-2 border-b-transparent bg-transparent px-4 pb-3 pt-2 font-semibold text-muted-foreground shadow-none transition-none focus-visible:ring-0 data-[state=active]:border-b-primary data-[state=active]:text-foreground data-[state=active]:shadow-none"
        @click="activeTab = tab"
      >
        {{ tab.charAt(0).toUpperCase() + tab.slice(1) }}
      </TabsTrigger>
    </TabsList>
    <TabsContent value="panorama" class="flex flex-col p-8 gap-6">
      <div class="flex">
        <span class="font-bold text-lg w-3/5">Resumen</span>
        <div class="flex-wrap flex justify-between gap-3 text-sm w-3/4">
          <div class="flex gap-2 sm:min-w-[240px]">
            <BookOpen/>
            <span>Nivel de Destreza: Cualquiera</span>
          </div>
          <div class="flex gap-2 sm:min-w-[240px]">
            <StudentIcon :width="24" class="fill-current"/>
            <span>Estudiantes: 215,118</span>
          </div>
          <div class="flex gap-2 sm:min-w-[240px]">
            <NotebookText/>
            <span>Lecturas: 4</span>
          </div>
          <div class="flex gap-2 sm:min-w-[240px]">
            <LayoutList/>
            <span>Ejercicios: 10</span>
          </div>
          <div class="flex gap-2 sm:min-w-[240px]">
            <AlarmClock/>
            <span>Duración: 1.8h</span>
          </div>
          <div class="flex gap-2 sm:min-w-[240px]">
            <FileBadge/>
            <span>Certifiación: Si</span>
          </div>
        </div>
      </div>
      <Separator/>
      <div class="flex">
        <span class="font-bold text-lg w-3/5">Descripcion</span>
        <div class="flex-col flex justify-between gap-3 text-sm w-3/4">
          <VueMarkdownIt :source="description" :options="options"/>
        </div>
      </div>
      <Separator/>
      <div class="flex">
        <span class="font-bold text-lg w-3/5">Creado por</span>
        <div class="flex-col flex justify-between gap-3 text-sm w-3/4">
          <div class="flex items-center gap-3">
            <Avatar class="w-12 h-12">
              <AvatarImage
                v-if="mentor.avatar_url"
                :src="mentor.avatar_url"
                :alt="mentor.username || ''"
              />
              <AvatarFallback>{{ initials }}</AvatarFallback>
            </Avatar>
            <div class="flex flex-col">
              <span class="font-bold">{{ mentor.full_name }}</span>
              <span>@{{ mentor.username }}</span>
            </div>
          </div>
          <!-- Social Media Buttons -->
          <div class="flex gap-2">
            <!--Instagram -->
            <Button class="rounded-full w-8 h-8 p-0">
              <InstagramIcon class="fill-white w-4"/>
            </Button>
            <!--Twitter -->
            <Button class="rounded-full w-8 h-8 p-0">
              <TwitterIcon class="fill-white w-4"/>
            </Button>
            <!-- Linkedin -->
            <Button class="rounded-full w-8 h-8 p-0">
              <LinkedinIcon class="fill-white w-4"/>
            </Button>
            <!-- Github -->
            <Button class="rounded-full w-8 h-8 p-0">
              <GithubIcon class="fill-white w-4"/>
            </Button>
          </div>
        </div>
      </div>
    </TabsContent>
    <TabsContent value="notas">
      <h2 class="text-3xl font-bold mb-6">Notas del Curso</h2>
      <ScrollArea class="h-[600px] w-full rounded-md border p-4">
        <Card 
          v-for="note in notes" 
          :key="note.id" 
          class="mb-6 hover:shadow-lg transition-shadow duration-300"
        >
          <CardHeader>
            <CardTitle class="text-xl">{{ note.title }}</CardTitle>
            <CardDescription>Última actualización: {{ note.lastUpdated }}</CardDescription>
          </CardHeader>
          <CardContent>
            <p class="text-muted-foreground mb-4">{{ note.content }}</p>
            <div class="flex flex-wrap gap-2">
              <Badge 
                v-for="tag in note.tags" 
                :key="tag" 
                variant="secondary"
              >
                {{ tag }}
              </Badge>
            </div>
          </CardContent>
          <CardFooter>
            <Button variant="outline" class="w-full">
              Ver nota completa
              <ChevronRight class="ml-2 h-4 w-4" />
            </Button>
          </CardFooter>
        </Card>
      </ScrollArea>
    </TabsContent>
    <TabsContent value="comunicados">
      <h2 class="text-3xl font-bold mb-6">Comunicados</h2>
      <Card 
        v-for="announcement in announcements" 
        :key="announcement.id" 
        class="mb-6 hover:shadow-lg transition-shadow duration-300"
      >
        <CardHeader class="flex flex-row items-center gap-4">
          <div class="bg-primary/10 p-3 rounded-full">
            <component 
              :is="announcement.icon" 
              class="h-6 w-6 text-primary" 
            />
          </div>
          <div>
            <CardTitle>{{ announcement.title }}</CardTitle>
            <CardDescription>{{ announcement.date }}</CardDescription>
          </div>
        </CardHeader>
        <CardContent>
          <p class="text-muted-foreground">{{ announcement.content }}</p>
        </CardContent>
        <CardFooter>
          <Button variant="outline" class="w-full">
            Leer más
            <ChevronRight class="ml-2 h-4 w-4" />
          </Button>
        </CardFooter>
      </Card>
    </TabsContent>

    <TabsContent value="reseñas">
      <h2 class="text-3xl font-bold mb-6">Reseñas del Curso</h2>
      <div class="mb-8">
        <h3 class="text-xl font-semibold mb-2">Calificación general</h3>
        <div class="flex items-center gap-4">
          <div class="text-4xl font-bold">4.7</div>
          <div class="flex">
            <Star 
              v-for="star in 5" 
              :key="star" 
              class="h-6 w-6 text-yellow-400 fill-yellow-400" 
            />
          </div>
          <div class="text-muted-foreground">(128 reseñas)</div>
        </div>
        <Progress :model-value="94" class="mt-2" />
      </div>
      <Card 
        v-for="review in reviews" 
        :key="review.id" 
        class="mb-6 hover:shadow-lg transition-shadow duration-300"
      >
        <CardHeader>
          <div class="flex items-center gap-4">
            <Avatar>
              <AvatarImage :src="review.avatar" :alt="review.author" />
              <AvatarFallback>{{ review.author.charAt(0) }}</AvatarFallback>
            </Avatar>
            <div>
              <CardTitle>{{ review.author }}</CardTitle>
              <CardDescription>{{ review.date }}</CardDescription>
            </div>
            <Badge variant="outline" class="ml-auto">
              {{ review.rating }} <Star class="h-4 w-4 ml-1 fill-current" />
            </Badge>
          </div>
        </CardHeader>
        <CardContent>
          <p class="text-muted-foreground">{{ review.content }}</p>
        </CardContent>
        <CardFooter class="flex justify-between">
          <Button variant="ghost" size="sm">
            <MessageCircle class="mr-2 h-4 w-4" />
            Responder
          </Button>
          <Button variant="ghost" size="sm">
            <Bell class="mr-2 h-4 w-4" />
            {{ review.helpful }} personas encontraron esto útil
          </Button>
        </CardFooter>
      </Card>
    </TabsContent>
  </Tabs>
</template>
