import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

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
