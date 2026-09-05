import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'
import { VitePWA } from 'vite-plugin-pwa'

// https://vite.dev/config/
export default defineConfig({
  // Relative statt absolute Asset-Pfade: Home Assistant Ingress liefert die
  // App unter einem dynamischen Pfad-Präfix (/api/hassio_ingress/<token>/)
  // aus. Absolute Pfade wie "/assets/x.js" würden dabei immer gegen die
  // Domain-Wurzel aufgelöst und landeten so nicht beim Add-on. nginx
  // injiziert dafür passend einen <base href>-Tag (siehe nginx.conf).
  base: './',
  plugins: [
    vue(),
    vueDevTools(),
    VitePWA({
      // Registrierung erfolgt manuell in main.js (nur unter appBasePath === ''),
      // damit der Service Worker nie unter dem dynamischen Ingress-Präfix
      // registriert wird (siehe main.js).
      injectRegister: false,
      strategies: 'generateSW',
      manifest: {
        name: 'Todo-App',
        short_name: 'Todo-App',
        description: 'Familien-Todo-App',
        start_url: '.',
        scope: '.',
        display: 'standalone',
        background_color: '#f9fafb',
        theme_color: '#2563eb',
        icons: [
          { src: 'pwa-192x192.png', sizes: '192x192', type: 'image/png' },
          { src: 'pwa-512x512.png', sizes: '512x512', type: 'image/png' },
          { src: 'pwa-512x512.png', sizes: '512x512', type: 'image/png', purpose: 'maskable' },
        ],
      },
      workbox: {
        globPatterns: ['**/*.{js,css,html,ico,png,svg,webmanifest}'],
        navigateFallback: 'index.html',
        navigateFallbackDenylist: [/^\/api\//],
        cleanupOutdatedCaches: true,
      },
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    proxy: {
      // Spiegelt den /api-Proxy von nginx (docker-compose & Add-on) für den
      // lokalen "npm run dev"-Workflow, damit api.js überall denselben
      // relativen "/api"-Pfad verwenden kann.
      '/api': 'http://localhost:8080',
    },
  },
})
