const animate = require("tailwindcss-animate")
const path = require('path')
const typography = require('@tailwindcss/typography')


/** @type {import('tailwindcss').Config} */
module.exports = {
  darkMode: ["class"],
  safelist: ["dark"],
  prefix: "",
  content: [
    path.join(__dirname, '../..', 'apps/frontend/src/**/*.{js,ts,jsx,tsx,vue}'),
    path.join(__dirname, '../..', 'apps/desktop/src/**/*.{js,ts,jsx,tsx,vue}'),
    path.join(__dirname, 'components/**/*.{js,ts,jsx,tsx,vue}'),
  ],
  theme: {
    container: {
      center: true,
      padding: "2rem",
      screens: {
        "2xl": "1400px",
      },
    },
    extend: {
      colors: {
        border: "hsl(var(--border))",
        input: "hsl(var(--input))",
        ring: "hsl(var(--ring))",
        background: "hsl(var(--background))",
        foreground: "hsl(var(--foreground))",
        primary: {
          DEFAULT: "hsl(var(--primary))",
          foreground: "hsl(var(--primary-foreground))",
        },
        secondary: {
          DEFAULT: "hsl(var(--secondary))",
          foreground: "hsl(var(--secondary-foreground))",
        },
        destructive: {
          DEFAULT: "hsl(var(--destructive))",
          foreground: "hsl(var(--destructive-foreground))",
        },
        muted: {
          DEFAULT: "hsl(var(--muted))",
          foreground: "hsl(var(--muted-foreground))",
        },
        accent: {
          DEFAULT: "hsl(var(--accent))",
          foreground: "hsl(var(--accent-foreground))",
        },
        popover: {
          DEFAULT: "hsl(var(--popover))",
          foreground: "hsl(var(--popover-foreground))",
        },
        card: {
          DEFAULT: "hsl(var(--card))",
          foreground: "hsl(var(--card-foreground))",
        },
      },
      borderRadius: {
      	xl: "calc(var(--radius) + 4px)",
        lg: "var(--radius)",
        md: "calc(var(--radius) - 2px)",
        sm: "calc(var(--radius) - 4px)",
      },
      keyframes: {
        "accordion-down": {
          from: { height: 0 },
          to: { height: "var(--radix-accordion-content-height)" },
        },
        "accordion-up": {
          from: { height: "var(--radix-accordion-content-height)" },
          to: { height: 0 },
        },
        "collapsible-down": {
          from: { height: 0 },
          to: { height: 'var(--radix-collapsible-content-height)' },
        },
        "collapsible-up": {
          from: { height: 'var(--radix-collapsible-content-height)' },
          to: { height: 0 },
        },
      },
      animation: {
        "accordion-down": "accordion-down 0.2s ease-out",
        "accordion-up": "accordion-up 0.2s ease-out",
        "collapsible-down": "collapsible-down 0.2s ease-in-out",
        "collapsible-up": "collapsible-up 0.2s ease-in-out",
      },
      fontFamily: {
        sans: ['Montserrat', 'sans-serif'],
        mono: ['"Source Code Pro"', 'monospace'],
      },
      backgroundImage: {
        'auth-background': "url('~/assets/img/auth-background.svg')",
        'auth-background-dark': "url('~/assets/img/auth-background-dark.svg')",
      },
      typography: (theme) => ({
        DEFAULT: {
          css: {
            maxWidth: "90ch",
            'h1, h2, h3, h4, h5, h6': {
              textDecoration: 'none',
            },
          },
        },
        invert: {
          css: {
            'h1, h2, h3, h4, h5, h6': {
              textDecoration: 'none',
            },
          },
        },
      }),
    },
  },
  ignoreFiles: ['/lost+found/**/*'],
  plugins: [animate, typography],
}
