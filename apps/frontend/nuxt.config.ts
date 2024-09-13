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
  modules: [
    '@cortex/shared',
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
    'shadcn-nuxt',
  ],
  srcDir: 'src',
  shadcn: {
    /**
     * Prefix for all the imported component
     */
    prefix: '',
    /**
     * Directory that the component lives in.
     * @default "./components/ui"
     */
    componentDir: 'src/components/ui',
  },
  pinia: {
    storesDirs: ['src/stores/**'],
  },
  tailwindcss: {
    cssPath: ['~/assets/css/tailwind.css', {injectPosition: 'first'}],
    configPath: 'tailwind.config.js',
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
});
