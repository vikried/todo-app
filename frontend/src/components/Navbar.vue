<template>
  <nav class="flex gap-4 border-b pb-2 mb-6">
    <RouterLink
      v-for="link in links"
      :key="link.path"
      :to="link.path"
      class="text-blue-600 hover:underline"
      active-class="font-bold underline"
    >
      {{ link.name }}
    </RouterLink>
  </nav>
</template>

<script setup>
const links = [
  { name: 'Home', path: '/' },
  { name: 'Templates', path: '/templates' },
  { name: 'Settings', path: '/settings' }
]
</script>
