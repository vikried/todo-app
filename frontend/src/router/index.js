import { createRouter, createWebHistory } from 'vue-router'
import TemplatesView from '@/views/TemplatesView.vue'
import SettingsView from '@/views/SettingsView.vue'
import LoginView from '@/views/LoginView.vue'
import ListView from "@/views/ListView.vue";
import ListDetailView from "@/views/ListDetailView.vue";

const routes = [
  { path: '/login', component: LoginView, name: 'Login' },
  { path: '/', component: ListView, name: 'Todo-Listen' },
  { path: '/lists/:id', component: ListDetailView, name: "Todo-Listen-Details", props: true },
  { path: '/templates', component: TemplatesView, name: 'Templates' },
  { path: '/settings', component: SettingsView, name: 'Settings' }
]

// Läuft die App hinter einem dynamischen Pfad-Präfix (z. B. Home Assistant
// Ingress: /api/hassio_ingress/<token>/), spiegelt der von nginx injizierte
// <base href>-Tag diesen Präfix wider. Router-Basis daraus zur Laufzeit
// ableiten statt eines fest einkompilierten Werts.
const baseElement = document.querySelector('base')
const routerBase = baseElement
  ? new URL(baseElement.href, window.location.href).pathname
  : '/'

const router = createRouter({
  history: createWebHistory(routerBase),
  routes
})

router.beforeEach((to) => {
  const isAuthenticated = !!localStorage.getItem('token')

  if (to.path !== '/login' && !isAuthenticated) {
    return '/login'
  }
  if (to.path === '/login' && isAuthenticated) {
    return '/'
  }
})

export default router
