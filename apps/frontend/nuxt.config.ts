// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({

  compatibilityDate: '2024-04-03',
  devtools: {enabled: true},
  runtimeConfig: {
    public: {
      apiBaseUrl: process.env.NUXT_PUBLIC_API_BASE_URL || 'http://localhost:8088/api/v1',
    },
  },
  vite: {
    optimizeDeps: {
      exclude: ['vee-validate'],
    },
    define: {
      global: 'window',
    },
  },
  plugins: [
    '~/plugins/websocket.ts',
    '~/plugins/error.ts',
  ],
  extends: [
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
    'nuxt-seo-experiments',
    '@sidebase/nuxt-auth',
    '@pinia/colada-nuxt'
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
  },
  auth: {
    baseURL: process.env.NUXT_PUBLIC_API_BASE_URL || 'http://localhost:8088/api/v1/',
    globalAppMiddleware: true,
    provider: {
      type: "local",
      endpoints: {
        signIn: {path: 'auth/authenticate', method: 'post'},
        getSession: {path: 'user/me', method: 'get'},
      },
      token: {
        signInResponseTokenPointer: '/token',
        type: 'Bearer',
        headerName: 'Authorization',
        maxAgeInSeconds: 1800,
        sameSiteAttribute: 'lax',
      },
      pages: {
        login: '/auth/login',
      },
      session: {
        dataType: {
          id: 'number',
          username: 'string',
          email: 'string',
          first_name: 'string',
          last_name: 'string',
          full_name: 'string',
          avatar_url: 'string | null',
          date_of_birth: 'string',
          country_code: 'string',
          gender: 'string',
          account_locked: 'boolean',
          enabled: 'boolean',
          roles: 'string[]',
          has_password: 'boolean',
        }
      }
    }
  }
});