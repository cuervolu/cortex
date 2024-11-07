<script setup lang="ts">
import { ref } from 'vue'
import { ChevronDown, ChevronUp, BookOpen } from 'lucide-vue-next'

interface Lesson {
  id: number;
  title: string;
}

interface Module {
  id: number;
  title: string;
  lessons: Lesson[];
  lessonCount: number;
  exerciseCount: number;
}

interface Course {
  id: number;
  title: string;
  moduleCount: number;
  lessonCount: number;
  modules: Module[];
}

const course: Course = {
  id: 1,
  title: "Introduction to Programming Concepts",
  moduleCount: 2,
  lessonCount: 6,
  modules: [
    {
      id: 1,
      title: "Programming Fundamentals",
      lessons: [
        { id: 1, title: "What is Programming?" },
        { id: 2, title: "Variables and Data Types" },
        { id: 3, title: "Operators and Expressions" },
      ],
      lessonCount: 3,
      exerciseCount: 9,
    },
    {
      id: 2,
      title: "Control Structures",
      lessons: [
        { id: 4, title: "Conditional Statements" },
        { id: 5, title: "Loops" },
        { id: 6, title: "Functions" },
      ],
      lessonCount: 3,
      exerciseCount: 10,
    },
  ],
};

const expandedModules = ref<number[]>([1]);

const toggleModule = (moduleId: number) => {
  if (expandedModules.value.includes(moduleId)) {
    expandedModules.value = expandedModules.value.filter(id => id !== moduleId);
  } else {
    expandedModules.value.push(moduleId);
  }
};
</script>
<template>
  <div class="self-stretch bg-background m-[30px]">
    <div class="bg-purple-900 text-white py-16 px-4">
      <div class="container mx-auto">
        <h1 class="text-4xl font-bold mb-2">{{ course.title }}</h1>
        <p class="text-xl">{{ course.moduleCount }} modules / {{ course.lessonCount }} lessons</p>
      </div>
    </div>
    <div class="container mx-auto py-8 px-4">
      <h2 class="text-2xl font-semibold mb-6">Course content</h2>
      <Card v-for="module in course.modules" :key="module.id" class="mb-4">
        <CardHeader class="p-4 cursor-pointer" @click="toggleModule(module.id)">
          <CardTitle class="flex items-center justify-between">
            <div class="flex items-center space-x-3">
              <BookOpen class="h-6 w-6" />
              <span>{{ module.title }}</span>
            </div>
            <div class="flex items-center space-x-4">
              <span class="text-sm text-gray-500">
                {{ module.lessonCount }} lessons / {{ module.exerciseCount }} exercises
              </span>
              <Button variant="ghost" size="icon">
                <ChevronUp v-if="expandedModules.includes(module.id)" class="h-4 w-4" />
                <ChevronDown v-else class="h-4 w-4" />
              </Button>
            </div>
          </CardTitle>
        </CardHeader>
        <CardContent v-if="expandedModules.includes(module.id)" class="p-4 pt-0">
          <div v-for="lesson in module.lessons" :key="lesson.id" class="py-2 pl-9 border-t first:border-t-0">
            <div class="flex items-center space-x-3">
              <BookOpen class="h-4 w-4 text-gray-500" />
              <span>{{ lesson.title }}</span>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  </div>
</template>

