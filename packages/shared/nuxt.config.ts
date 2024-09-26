import { fileURLToPath } from 'url'
import { dirname, join } from 'path'

const currentDir = dirname(fileURLToPath(import.meta.url))

// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: "2024-09-18",
  modules: ["@nuxt/eslint", "@nuxtjs/tailwindcss", "shadcn-nuxt"],
  components: [
    {
      path: join(currentDir, './components/ui'),
      // this is required else Nuxt will autoImport `.ts` file
      extensions: ['.vue'],
      // prefix for your components, eg: UiButton
    },
  ],
  tailwindcss: {
    cssPath: join(currentDir, './assets/css/tailwind.css'),
  },
  shadcn: {
    /**
     * Prefix for all the imported component
     */
    prefix: '',
    /**
     * Directory that the component lives in.
     * @default "./components/ui"
     */
    componentDir: join(currentDir, './components/ui'),
  }
})
