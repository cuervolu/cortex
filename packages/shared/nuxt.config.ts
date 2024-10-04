import {fileURLToPath} from 'url'
import {dirname, join} from 'path'

const currentDir = dirname(fileURLToPath(import.meta.url))

// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({

  compatibilityDate: "2024-09-18",
  modules: [
    'nuxt-codemirror',
    "@nuxt/eslint",
    "@nuxtjs/tailwindcss",
    "shadcn-nuxt",
    '@nuxtjs/mdc'
  ],
  components: [
    {
      path: join(currentDir, './components/ui'),
      extensions: ['.vue'],
      priority: 2,
    },
    {
      path: join(currentDir, './components'),
      extensions: ['.vue'],
      priority: 1,
    }
  ],
  tailwindcss: {
    cssPath: join(currentDir, './assets/css/tailwind.css'),
  },
  shadcn: {
    prefix: '',
    componentDir: join(currentDir, './components/ui'),
  },
  build: {
    transpile: ['shadcn-vue'],
  },
  hooks: {
    'components:dirs': (dirs) => {

      dirs.unshift({
        path: join(currentDir, './components'),
        prefix: '',
        priority: 10
      })
    }
  }
})
