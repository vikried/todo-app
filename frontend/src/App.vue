<template>
  <div class="app-container dark:bg-gray-800 dark:border-gray-700">
    <Navbar />
    <router-view />
  </div>
</template>

<script setup>
 import Navbar from '@/components/Navbar.vue'
</script>

<style>
.app-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem;
}
</style>
