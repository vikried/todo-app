<template>
  <div class="app-container">
    <Navbar />
    <h1>Meine TODO-App 📝</h1>
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
