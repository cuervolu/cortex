<script setup>
import catImage from '@/assets/img/cuh-cat.gif';
import cinnamorollImage from '~/assets/img/cinnamoroll.webp'
import { Star, Briefcase, Award, GraduationCap } from 'lucide-vue-next';

const selectedMentor = ref(null)
const messages = ref([])
const newMessage = ref('')
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
const sendMessage = () => {
  if (!newMessage.value.trim()) return

  // Add the user's message
  messages.value.push({
    id: Date.now(),
    text: newMessage.value,
    sender: 'user',
    timestamp: new Date().toISOString()
  })
  
    // Simulate mentor response (replace with actual API call)
    setTimeout(() => {
    messages.value.push({
      id: Date.now() + 1,
      text: `Gracias por tu mensaje. Estoy aquí para ayudarte en ${selectedMentor.value.specialty}.`,
      sender: 'mentor',
      timestamp: new Date().toISOString()
    })
  }, 1000)

  // Clear input and scroll to bottom
  newMessage.value = ''
    nextTick(() => {
      scrollToBottom()
    })
  }

// Scroll to bottom of messages
const scrollToBottom = () => {
  const chatContainer = document.getElementById('chat-messages')
  if (chatContainer) {
    chatContainer.scrollTop = chatContainer.scrollHeight
  }
}

// Select a mentor (in a real app, this would likely come from a route or prop)
const selectMentor = (mentor) => {
  selectedMentor.value = mentor
  
  // Reset messages or fetch existing messages
  messages.value = [
    {
      id: 1,
      text: `Hola, soy ${mentor.name}. Estoy aquí para mentorizarte en ${mentor.specialty}. ¿En qué puedo ayudarte hoy?`,
      sender: 'mentor',
      timestamp: new Date().toISOString()
    }
  ]
}

// Initialize with first mentor
onMounted(() => {
  selectMentor(mentors[0])
})


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
  <div class="flex h-screen">

    <div class="w-64 bg-gray-100 border-r p-4 overflow-y-auto">
      <h2 class="text-xl font-bold mb-4">Mentores</h2>
      <div 
        v-for="mentor in mentors" 
        :key="mentor.id"
        @click="selectMentor(mentor)"
        class="flex items-center p-3 hover:bg-gray-200 cursor-pointer rounded-lg mb-2"
        :class="{ 'bg-blue-100': selectedMentor && selectedMentor.id === mentor.id }"
      >
        <img 
          :src="mentor.photo" 
          :alt="mentor.name" 
          class="w-10 h-10 rounded-full mr-3 object-cover"
        />
        <div>
          <div class="font-semibold">{{ mentor.name }}</div>
          <div class="text-sm text-gray-500">{{ mentor.specialty }}</div>
        </div>
      </div>
    </div>

    <div class="flex-1 flex flex-col">
      <div v-if="selectedMentor" class="bg-white border-b p-4 flex items-center">
        <img 
          :src="selectedMentor.photo" 
          :alt="selectedMentor.name" 
          class="w-12 h-12 rounded-full mr-4 object-cover"
        />
        <div>
          <h2 class="text-xl font-bold">{{ selectedMentor.name }}</h2>
          <p class="text-gray-500">{{ selectedMentor.specialty }}</p>
        </div>
      </div>

      <div 
        id="chat-messages"
        class="flex-1 overflow-y-auto p-4 space-y-4"
      >
        <template v-for="message in messages" :key="message.id">
          <div 
            class="flex"
            :class="{
              'justify-end': message.sender === 'user',
              'justify-start': message.sender === 'mentor'
            }"
          >
            <div 
              class="max-w-[70%] p-3 rounded-lg"
              :class="{
                'bg-blue-500 text-white': message.sender === 'user',
                'bg-blue-400': message.sender === 'mentor'
              }"
            >
              {{ message.text }}
              <div class="text-xs mt-1 opacity-70">
                {{ new Date(message.timestamp).toLocaleTimeString() }}
              </div>
            </div>
          </div>
        </template>
      </div>

      <div class="border-t p-4 bg-white flex items-center">
        <Textarea 
          v-model="newMessage"
          placeholder="Escribe tu mensaje..."
          class="mr-2 flex-1 resize-none"
          @keyup.enter="sendMessage"
          rows="1"
        />
        <Button 
          @click="sendMessage" 
          :disabled="!newMessage.trim()"
          class="flex items-center"
        >
          <Send class="mr-2 h-4 w-4" />
          Enviar
        </Button>
      </div>
    </div>
  </div>
</template>

<style scoped>
#chat-messages {
  scrollbar-width: thin;
  scrollbar-color: rgba(0,0,0,0.2) transparent;
}

#chat-messages::-webkit-scrollbar {
  width: 6px;
}

#chat-messages::-webkit-scrollbar-thumb {
  background-color: rgba(0,0,0,0.2);
  border-radius: 3px;
}

</style>