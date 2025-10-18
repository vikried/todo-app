import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import TemplatesView from '@/views/TemplatesView.vue'
import SettingsView from '@/views/SettingsView.vue'

const routes = [
  { path: '/', component: HomeView },
  { path: '/templates', component: TemplatesView, name: 'Templates' },
  { path: '/settings', component: SettingsView, name: 'Settings' }
]

export default createRouter({
  history: createWebHistory(),
  routes
})