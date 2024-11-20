// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2024-04-03',
  devtools: {enabled: true},
  ssr: false,
  telemetry: false,
  alias: {
    '@vue/devtools-api': '@vue/devtools-api',
  },
  runtimeConfig: {
    public: {
      apiBaseUrl: process.env.NUXT_PUBLIC_API_BASE_URL || 'http://localhost:8088/api/v1',
    },
  },
  // Enables the development server to be discoverable by other devices when running on iOS physical devices
  devServer: {host: process.env.TAURI_DEV_HOST || 'localhost'},
  vite: {
    // Better support for Tauri CLI output
    clearScreen: false,
    envPrefix: ['VITE_', 'TAURI_'],
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
    server: {
      // Tauri requires a consistent port
      strictPort: true,
    },
    optimizeDeps: {
      exclude: ['vee-validate'],
    },
  },
  plugins: [
    '~/plugins/auth-sync.ts',
    '~/plugins/error-handler.ts',
  ],
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
    '@sidebase/nuxt-auth',
    '@nuxt/image',
    'nuxt-tiptap-editor',
  ],
  mdc: {
    components: {
      prose: true,
      map: {
        h1: 'ProseH1',
        h2: 'ProseH2',
        h3: 'ProseH3',
        p: 'ProseP',
        ul: 'ProseUl',
        li: 'ProseLi',
        code: 'ProseCode'
      },
    },
    headings: {
      anchorLinks: {
        h1: false,
        h2: false,
        h3: false,
        h4: false,
        h5: false,
        h6: false
      }
    },
    highlight: {
      theme: 'material-theme-ocean',
      langs: ['js', 'jsx', 'json', 'ts', 'tsx', 'vue', 'css', 'html', 'bash', 'md', 'mdc', 'yaml', "rust", "java", "python", "go", "csharp"],
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
        name: 'Source Code Pro',
        provider: 'fontsource',
        weights: ['400', '500', '600', '700', '800', '900'],
      },
    ],
  },
  tiptap: {
    prefix: 'Tiptap', //prefix for Tiptap imports, composables not included
    lowlight: {
      theme: 'github-dark',
    },
  },
  auth: {
    baseURL: process.env.NUXT_PUBLIC_API_BASE_URL || 'http://localhost:8088/api/v1/',
    globalAppMiddleware: true,
    provider: {
      type: "local",
      endpoints: {
        signIn: {path: 'auth/authenticate', method: 'post'},
        getSession: {path: 'user/me', method: 'get'},
        signUp: false,
        refresh: {path: 'auth/refresh-token', method: 'post'},
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
          roles: 'string[]'
        }
      }
    }
  }
});