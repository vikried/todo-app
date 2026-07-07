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

const router = createRouter({
  history: createWebHistory(),
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
