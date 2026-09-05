import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'
import { appBasePath } from './api.js'
import './style.css'

createApp(App)
  .use(router)
  .use(createPinia())
  .mount('#app')

// Service Worker nur registrieren, wenn die App unter der Domain-Wurzel läuft
// (Docker-Compose / Direkt-Port) - nie unter dem dynamischen Home-Assistant-
// Ingress-Präfix, da ein dort registrierter Service Worker nach einem
// Add-on-Update veraltete Assets ausliefern könnte, ohne dass sich der
// Ingress-Zugriff je auf zuverlässiges Offline-Verhalten verlassen könnte
// (siehe ADR-Kontext: Ingress-Session läuft nach 15 Min. Inaktivität ab).
if ('serviceWorker' in navigator && appBasePath === '') {
  navigator.serviceWorker.register('/sw.js', { scope: '/' }).catch((err) => {
    // z. B. kein sicherer Kontext (Service Worker brauchen HTTPS oder localhost)
    console.warn('Service-Worker-Registrierung fehlgeschlagen:', err);
  });
}


// main.js
const theme = localStorage.getItem('theme')
if (theme === 'dark') {
  document.documentElement.classList.add('dark')
} else {
  document.documentElement.classList.remove('dark')
}