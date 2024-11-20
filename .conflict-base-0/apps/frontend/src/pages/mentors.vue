<script setup>
import catImage from '@/assets/img/cuh-cat.gif';
import cinnamorollImage from '~/assets/img/cinnamoroll.webp'
import { Star, Briefcase, Award, GraduationCap } from 'lucide-vue-next';


const mentors = [
  {
    id: 1,
    name: "Juan Pérez",
    specialty: "Desarrollo Frontend",
    description: "Experto en Vue.js, Nuxt y tecnologías modernas de JavaScript con 8 años de experiencia en startups. Ex-líder técnico en Mercado Libre y mentor en Platzi.",
    photo: catImage,
    experience: "8 años",
    badges: ["Vue.js", "Nuxt.js", "JavaScript", "TypeScript", "React"],
    achievements: ["Tech Lead en Mercado Libre", "Mentor en Platzi"],
    availability: "Tardes y fines de semana",
    languages: ["Español", "Inglés"],
    rating: 4.9,
    totalMentorships: 145
  },
  {
    id: 2,
    name: "Ana González",
    specialty: "Machine Learning",
    description: "Ingeniera de datos con PhD en Inteligencia Artificial. Especialista en Python, TensorFlow y algoritmos avanzados de ML. Investigadora principal en proyectos de IA ética.",
    photo: catImage,
    experience: "10 años",
    badges: ["Python", "TensorFlow", "PyTorch", "Scikit-learn", "AWS"],
    achievements: ["PhD en IA", "Paper publicado en NeurIPS"],
    availability: "Mañanas y noches",
    languages: ["Español", "Inglés", "Portugués"],
    rating: 4.8,
    totalMentorships: 89
  },
  {
    id: 3,
    name: "Carlos López",
    specialty: "DevOps y Cloud",
    description: "Arquitecto cloud con múltiples certificaciones AWS y Azure. Experto en Kubernetes, Docker y CI/CD. Contributor activo en proyectos open source de infraestructura.",
    photo: catImage,
    experience: "12 años",
    badges: ["AWS", "Kubernetes", "Docker", "Terraform", "GitLab"],
    achievements: ["AWS Solutions Architect", "Azure DevOps Expert"],
    availability: "Flexible",
    languages: ["Español", "Inglés"],
    rating: 4.9,
    totalMentorships: 234
  },
  {
    id: 4,
    name: "María Fernández",
    specialty: "Diseño UX/UI",
    description: "Diseñadora con background en psicología cognitiva. Especialista en diseño de productos digitales centrados en el usuario y sistemas de diseño escalables.",
    photo: catImage,
    experience: "6 años",
    badges: ["Figma", "Design Systems", "User Research", "Prototyping", "Adobe XD"],
    achievements: ["Lead Designer en Banco Santander", "UX Awards 2023"],
    availability: "Mañanas y tardes",
    languages: ["Español", "Inglés", "Francés"],
    rating: 4.7,
    totalMentorships: 167
  },
  {
    id: 5,
    name: "Roberto Castro",
    specialty: "Seguridad Informática",
    description: "Experto en ciberseguridad con certificaciones CISSP y CEH. Especialista en pruebas de penetración y seguridad ofensiva. Consultor independiente para Fortune 500.",
    photo: catImage,
    experience: "15 años",
    badges: ["CISSP", "CEH", "Pentesting", "Forensics", "ISO 27001"],
    achievements: ["SANS Instructor", "Black Hat Speaker"],
    availability: "Bajo demanda",
    languages: ["Español", "Inglés"],
    rating: 5.0,
    totalMentorships: 78
  },
  {
    id: 6,
    name: "Laura Martínez",
    specialty: "Desarrollo Backend",
    description: "Arquitecta de software especializada en sistemas distribuidos y microservicios. Experta en Java Spring Boot y arquitecturas cloud-native.",
    photo: catImage,
    experience: "9 años",
    badges: ["Java", "Spring Boot", "Microservices", "MongoDB", "Kafka"],
    achievements: ["Google Cloud Professional", "Java Champion"],
    availability: "Tardes",
    languages: ["Español", "Inglés", "Alemán"],
    rating: 4.8,
    totalMentorships: 156
  },
  {
    id: 7,
    name: "Diego Ramírez",
    specialty: "Desarrollo Móvil",
    description: "Desarrollador mobile con experiencia en React Native y Flutter. Ex-líder técnico en Rappi y creador de múltiples apps con millones de descargas.",
    photo: catImage,
    experience: "7 años",
    badges: ["React Native", "Flutter", "iOS", "Android", "Firebase"],
    achievements: ["Mobile Lead en Rappi", "Google Developer Expert"],
    availability: "Flexible",
    languages: ["Español", "Inglés"],
    rating: 4.9,
    totalMentorships: 203
  },
  {
    id: 8,
    name: "Carmen Ruiz",
    specialty: "Data Science",
    description: "PhD en Estadística aplicada a la bioinformática. Experta en R y análisis predictivo con experiencia en investigación y aplicaciones industriales.",
    photo: catImage,
    experience: "11 años",
    badges: ["R", "Python", "SQL", "Bioinformatics", "Statistics"],
    achievements: ["PhD en Estadística", "Research Fellow MIT"],
    availability: "Mañanas",
    languages: ["Español", "Inglés", "Italiano"],
    rating: 4.7,
    totalMentorships: 91
  },
  {
    id: 9,
    name: "Miguel Torres",
    specialty: "Arquitectura de Software",
    description: "Arquitecto senior con amplia experiencia diseñando sistemas escalables y microservicios. Especialista en patrones de diseño y mejores prácticas.",
    photo: catImage,
    experience: "12 años",
    badges: ["System Design", "DDD", "Microservices", "Cloud Architecture", "SOA"],
    achievements: ["Principal Architect BBVA", "AWS Community Builder"],
    availability: "Tardes y fines de semana",
    languages: ["Español", "Inglés"],
    rating: 4.9,
    totalMentorships: 189
  },
  {
    id: 10,
    name: "Patricia Vega",
    specialty: "Testing y QA",
    description: "ISTQB Certified con especialización en automatización de pruebas y QA ágil. Experta en implementación de culturas de calidad en equipos de desarrollo.",
    photo: catImage,
    experience: "8 años",
    badges: ["Selenium", "Cypress", "TestNG", "Jenkins", "ISTQB"],
    achievements: ["QA Lead en Globant", "Testing Awards 2023"],
    availability: "Flexible",
    languages: ["Español", "Inglés"],
    rating: 4.8,
    totalMentorships: 134
  },
  {
    id: 11,
    name: "Fernando Silva",
    specialty: "Blockchain",
    description: "Desarrollador blockchain y experto en smart contracts. Contributor activo en Ethereum y creador de varios protocolos DeFi exitosos.",
    photo: catImage,
    experience: "6 años",
    badges: ["Solidity", "Web3.js", "DeFi", "Smart Contracts", "Ethereum"],
    achievements: ["Ethereum Core Contributor", "DeFi Builder Award"],
    availability: "Noches y fines de semana",
    languages: ["Español", "Inglés"],
    rating: 4.9,
    totalMentorships: 112
  },
  {
    id: 12,
    name: "Lucía Morales",
    specialty: "Base de Datos",
    description: "DBA certificada con expertise en PostgreSQL y MongoDB. Especialista en optimización de queries y diseño de bases de datos a escala.",
    photo: catImage,
    experience: "10 años",
    badges: ["PostgreSQL", "MongoDB", "Redis", "Database Design", "Performance"],
    achievements: ["MongoDB Certified DBA", "PostgreSQL Contributor"],
    availability: "Mañanas y tardes",
    languages: ["Español", "Inglés", "Portugués"],
    rating: 4.8,
    totalMentorships: 145
  },
  {
    id: 13,
    name: "Andrés Mendoza",
    specialty: "IA y Computer Vision",
    description: "Investigador en IA con maestría en Computer Vision. Especialista en deep learning aplicado a procesamiento de imágenes y video en tiempo real.",
    photo: catImage,
    experience: "7 años",
    badges: ["OpenCV", "PyTorch", "YOLO", "CNNs", "MLOps"],
    achievements: ["Research Lead en Samsung", "Computer Vision Patent"],
    availability: "Flexible",
    languages: ["Español", "Inglés", "Coreano"],
    rating: 4.9,
    totalMentorships: 88
  },
  {
    id: 14,
    name: "Valentina Ochoa",
    specialty: "Product Management",
    description: "Product Manager con background técnico en ingeniería de software. Experta en metodologías ágiles y desarrollo de producto basado en datos.",
    photo: catImage,
    experience: "9 años",
    badges: ["Agile", "Scrum", "Data Analytics", "UX", "Product Strategy"],
    achievements: ["PM Lead en Twitter", "Product School Instructor"],
    availability: "Mañanas",
    languages: ["Español", "Inglés", "Francés"],
    rating: 4.7,
    totalMentorships: 178
  },
  {
    id: 15,
    name: "Cristopher Hurtado",
    specialty: "Desarrollo de Videojuegos",
    description: "Game developer senior con experiencia en Unity y Unreal Engine. Ex-lead developer en Ubisoft y creador independiente de juegos exitosos.",
    photo: cinnamorollImage,
    experience: "10 años",
    badges: ["Unity", "Unreal Engine", "C++", "Game Design", "3D Modeling"],
    achievements: ["Lead Developer Ubisoft", "Best Indie Game 2022"],
    availability: "Tardes y fines de semana",
    languages: ["Español", "Inglés", "Japonés"],
    rating: 5.0,
    totalMentorships: 156
  }
];

const searchTerm = ref('');
const selectedSpecialty = ref('all');
const specialties = [...new Set(mentors.map(mentor => mentor.specialty))];
const isLoading = ref(true);

// Simulate loading
onMounted(() => {
  setTimeout(() => {
    isLoading.value = false;
  }, 1000);
});


const filteredMentors = computed(() => {
  return mentors.filter(mentor => {
    const matchesSearch = mentor.name.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
        mentor.specialty.toLowerCase().includes(searchTerm.value.toLowerCase()) ||
        mentor.description.toLowerCase().includes(searchTerm.value.toLowerCase());
    const matchesSpecialty = selectedSpecialty.value === 'all' || mentor.specialty === selectedSpecialty.value;
    return matchesSearch && matchesSpecialty;
  });
});
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <!-- Hero Section -->
    <div class="bg-gradient-to-r from-blue-600 to-purple-400 py-16">
      <div class="container mx-auto px-4">
        <h1 class="text-4xl md:text-5xl font-bold text-white text-center mb-4">
          Aprende de los Mejores Expertos
        </h1>
        <p class="text-xl text-blue-100 text-center max-w-2xl mx-auto">
          Conecta con mentores de clase mundial y acelera tu carrera en tecnología
        </p>
      </div>
    </div>

    <!-- Filters Section -->
    <div class="container mx-auto px-4 py-8">
      <div class="flex flex-col md:flex-row gap-4 mb-8">
        <Select v-model="selectedSpecialty" class="w-full md:w-72">
          <SelectTrigger>
            <SelectValue placeholder="Filtrar por especialidad" />
          </SelectTrigger>
          <SelectContent>
            <SelectGroup>
              <SelectLabel>Especialidades</SelectLabel>
              <SelectItem value="all">
                Todas las especialidades
              </SelectItem>
              <SelectItem
                  v-for="specialty in specialties"
                  :key="specialty"
                  :value="specialty"
              >
                {{ specialty }}
              </SelectItem>
            </SelectGroup>
          </SelectContent>
        </Select>
      </div>

      <!-- Stats Cards -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-12">
        <Card>
          <CardHeader class="flex flex-row items-center gap-4">
            <div class="p-2 bg-blue-100 rounded-lg">
              <GraduationCap class="text-blue-600 h-6 w-6" />
            </div>
            <div>
              <CardTitle class="text-xl">{{ mentors.length }}+ Mentores</CardTitle>
              <CardDescription>Expertos verificados</CardDescription>
            </div>
          </CardHeader>
        </Card>

        <Card>
          <CardHeader class="flex flex-row items-center gap-4">
            <div class="p-2 bg-green-100 rounded-lg">
              <Briefcase class="text-green-600 h-6 w-6" />
            </div>
            <div>
              <CardTitle class="text-xl">15+ Especialidades</CardTitle>
              <CardDescription>Áreas de expertise</CardDescription>
            </div>
          </CardHeader>
        </Card>

        <Card>
          <CardHeader class="flex flex-row items-center gap-4">
            <div class="p-2 bg-purple-100 rounded-lg">
              <Award class="text-purple-600 h-6 w-6" />
            </div>
            <div>
              <CardTitle class="text-xl">8+ años</CardTitle>
              <CardDescription>Experiencia promedio</CardDescription>
            </div>
          </CardHeader>
        </Card>
      </div>

      <!-- Mentors Grid -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <template v-if="isLoading">
          <div v-for="n in 6" :key="n">
            <Card>
              <Skeleton class="h-48 w-full rounded-t-xl" />
              <CardHeader>
                <Skeleton class="h-6 w-2/3 mb-2" />
                <Skeleton class="h-4 w-1/2" />
              </CardHeader>
              <CardContent>
                <Skeleton class="h-20 w-full mb-4" />
                <div class="flex gap-2 flex-wrap">
                  <Skeleton v-for="i in 4" :key="i" class="h-6 w-20" />
                </div>
              </CardContent>
            </Card>
          </div>
        </template>

        <Card
            v-else
            v-for="mentor in filteredMentors"
            :key="mentor.id"
            class="group overflow-hidden"
        >
          <div class="relative">
            <img
                :src="mentor.photo"
                :alt="mentor.name"
                class="w-full h-48 object-cover object-center"
            />
            <div class="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
          </div>

          <CardHeader>
            <div class="flex justify-between items-start">
              <div>
                <CardTitle>{{ mentor.name }}</CardTitle>
                <CardDescription class="text-blue-600 font-medium">
                  {{ mentor.specialty }}
                </CardDescription>
              </div>
              <Badge variant="secondary" class="flex items-center gap-1">
                <Star class="h-3 w-3" />
                {{ mentor.rating }}
              </Badge>
            </div>
          </CardHeader>

          <CardContent>
            <p class="text-gray-600 mb-4">{{ mentor.description }}</p>

            <div class="flex flex-wrap gap-2">
              <Badge
                  v-for="badge in mentor.badges"
                  :key="badge"
                  variant="outline"
              >
                {{ badge }}
              </Badge>
            </div>
          </CardContent>

          <CardFooter class="flex justify-between items-center">
            <Badge variant="secondary">{{ mentor.experience }}</Badge>
            <div class="flex items-center gap-2">
              <Badge variant="secondary">
                {{ mentor.totalMentorships }} mentorías
              </Badge>
            </div>
          </CardFooter>
        </Card>
      </div>
    </div>
  </div>
</template>

<style scoped>
.container {
  max-width: 1200px;
}
</style>