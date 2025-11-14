<template>
  <div class="p-6 bg-white dark:bg-gray-900 dark:text-gray-100 rounded-lg shadow-md">
    <h2 class="dark:text-white text-2xl font-semibold mb-4">Einstellungen</h2>
    <p class="text-gray-600 dark:text-gray-300">Hier kannst du App-Einstellungen anpassen.</p>

    <label class="block mt-6">
      <span class="font-medium">Theme:</span>
      <select
        v-model="selectedTheme"
        @change="changeTheme"
        class="border p-2 ml-2 rounded bg-gray-100 dark:bg-gray-800 dark:border-gray-700"
      >
        <option value="light">Hell</option>
        <option value="dark">Dunkel</option>
      </select>
    </label>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const selectedTheme = ref('light')

onMounted(() => {
  // Beim Laden prüfen, ob es ein gespeichertes Theme gibt
  const savedTheme = localStorage.getItem('theme')
  if (savedTheme === 'dark') {
    document.documentElement.classList.add('dark')
    selectedTheme.value = 'dark'
  } else {
    document.documentElement.classList.remove('dark')
    selectedTheme.value = 'light'
  }
})

function changeTheme() {
  const root = document.documentElement
  if (selectedTheme.value === 'dark') {
    root.classList.add('dark')
    localStorage.setItem('theme', 'dark')
  } else {
    root.classList.remove('dark')
    localStorage.setItem('theme', 'light')
  }
}
</script>