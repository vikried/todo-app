<template>
  <div class="app-container dark:bg-gray-800 dark:border-gray-700">
    <OfflineBanner v-if="!isOnline" />
    <Navbar v-if="route.path !== '/login'" />
    <router-view />
  </div>
</template>

<script setup>
 import Navbar from '@/components/Navbar.vue'
 import OfflineBanner from '@/components/OfflineBanner.vue'
 import { useRoute } from 'vue-router'
 import { useOnlineStatus } from '@/composables/useOnlineStatus.js'

 const route = useRoute()
 const { isOnline } = useOnlineStatus()
</script>

<style>
.app-container {
  max-width: 768px;
  min-height: 100vh;
  margin: 0 auto;
  padding: 2rem;
}
</style>
