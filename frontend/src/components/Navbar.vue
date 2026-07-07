<template>
  <nav class="flex items-center gap-2 border-b border-gray-300 pb-2 mb-6">
    <RouterLink
      v-for="link in links"
      :key="link.path"
      :to="link.path"
      class="px-4 py-2 rounded-t-lg text-gray-600 hover:text-blue-600 hover:bg-blue-50 dark:bg-gray-900 dark:text-gray-100 transition-colors"
      active-class="bg-blue-100 text-blue-700 font-semibold border-b-2 border-blue-600"
    >
      {{ link.name }}
    </RouterLink>
    <button
      @click="logout"
      class="ml-auto px-4 py-2 rounded-t-lg text-gray-600 hover:text-red-600 hover:bg-red-50 dark:bg-gray-900 dark:text-gray-100 transition-colors"
    >
      Logout
    </button>
  </nav>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/authStore'

const router = useRouter()
const authStore = useAuthStore()

const links = [
  { name: 'Todo-Listen', path: '/'},
  { name: 'Templates', path: '/templates' },
  { name: 'Settings', path: '/settings' }
]

async function logout() {
  await authStore.logout()
  router.push('/login')
}
</script>
