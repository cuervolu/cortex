// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2024-04-03',
  devtools: { enabled: true },
  ssr: false,
  alias: {
    '@vue/devtools-api': '@vue/devtools-api',
  },
  vite: {
    // Better support for Tauri CLI output
    clearScreen: false,
    // https://github.com/tauri-apps/tauri/security/advisories/GHSA-2rcp-jvr4-r259
    define: {
      TAURI_PLATFORM: JSON.stringify(process.env.TAURI_PLATFORM),
      TAURI_ARCH: JSON.stringify(process.env.TAURI_ARCH),
      TAURI_FAMILY: JSON.stringify(process.env.TAURI_FAMILY),
      TAURI_PLATFORM_VERSION: JSON.stringify(
        process.env.TAURI_PLATFORM_VERSION
      ),
      TAURI_PLATFORM_TYPE: JSON.stringify(process.env.TAURI_PLATFORM_TYPE),
      TAURI_DEBUG: JSON.stringify(process.env.TAURI_DEBUG),
    },
    // Dev server configurations
    server: {
      // Tauri requires a consistent port
      strictPort: true,
      // Enables the development server to be discoverable by other devices for mobile development
      host: '0.0.0.0',
      hmr: {
        // Use websocket for mobile hot reloading
        protocol: 'ws',
        // Make sure it's available on the network
        host: '0.0.0.0',
        // Use a specific port for hmr
        port: 5183,
      },
    },
    optimizeDeps: {
      exclude: ['vee-validate'],
    },
  },
  srcDir: 'src',
  extends: ['@cortex/shared'],
  modules: [
    '@nuxtjs/tailwindcss',
    '@pinia/nuxt',
    '@vueuse/nuxt',
    '@nuxtjs/color-mode',
    '@nuxt/eslint',
    '@vee-validate/nuxt',
    '@nuxt/fonts',
    'nuxt-codemirror',
    '@nuxtjs/mdc',
  ],
  mdc: {
    highlight: {
      theme: 'material-theme-ocean',
      langs: ['js','jsx','json','ts','tsx','vue','css','html','bash','md','mdc','yaml', "rust"],
      wrapperStyle: true
    }
  },
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
      {
        name: 'CascadiaCode',
        provider: 'local',
        weights: ['400', '500', '600', '700', '800', '900'],
      }
    ],
  },
});