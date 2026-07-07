<template>
  <div class="app-container dark:bg-gray-800 dark:border-gray-700">
    <Navbar v-if="route.path !== '/login'" />
    <router-view />
  </div>
</template>

<script setup>
 import Navbar from '@/components/Navbar.vue'
 import { useRoute } from 'vue-router'

 const route = useRoute()
</script>

<style>
.app-container {
  max-width: 768px;
  margin: 0 auto;
  padding: 2rem;
}
</style>
