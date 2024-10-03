// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  
  compatibilityDate: '2024-04-03',
  devtools: {enabled: true},
  runtimeConfig: {
    public: {
      apiBaseUrl: process.env.NUXT_API_BASE_URL
    }
  },
  vite: {
    optimizeDeps: {
      exclude: ['vee-validate'],
    },
  },
  extends:[
      "@cortex/shared"
  ],
  modules: [
    'nuxt-codemirror',
    '@nuxtjs/tailwindcss',
    '@pinia/nuxt',
    '@vueuse/nuxt',
    '@nuxtjs/color-mode',
    '@nuxt/eslint',
    '@nuxtjs/mdc',
    '@vee-validate/nuxt',
    '@nuxt/fonts',
    '@nuxtjs/seo',
    'nuxt-seo-experiments'
  ],
  srcDir: 'src',
  pinia: {
    storesDirs: ['src/stores/**'],
  },
  colorMode: {
    classSuffix: '',
  },
  fonts: {
    families: [
      {
        name: 'Montserrat',
        provider: 'fontsource',
        weights: ['400', '500', '600', '700', '800', '900'],
      },
    ],
  },
  site: {
    url: 'https://cortex.com',
    name: 'Cortex',
    description: 'Shaping Tech Futures',
  }
});