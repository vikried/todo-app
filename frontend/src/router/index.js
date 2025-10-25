import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import TemplatesView from '@/views/TemplatesView.vue'
import SettingsView from '@/views/SettingsView.vue'
import ListView from "@/views/ListView.vue";
import ListDetailView from "@/views/ListDetailView.vue";

const routes = [
  { path: '/', component: HomeView },
  { path: '/lists', component: ListView, name: 'Todo-Listen' },
  { path: '/lists/:id', component: ListDetailView, name: "Todo-Listen-Details", props: true },
  { path: '/templates', component: TemplatesView, name: 'Templates' },
  { path: '/settings', component: SettingsView, name: 'Settings' }
]

export default createRouter({
  history: createWebHistory(),
  routes
})