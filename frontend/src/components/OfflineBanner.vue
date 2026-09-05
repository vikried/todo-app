<template>
  <div class="sticky top-0 z-30 flex items-center gap-2 bg-amber-100 text-amber-900 px-4 py-2 text-sm dark:bg-amber-900 dark:text-amber-100">
    <WifiOff class="w-4 h-4 flex-shrink-0" />
    <span>Offline – zeigt zuletzt gespeicherten Stand. Bearbeiten ist erst wieder online möglich.</span>
  </div>
</template>

<script setup>
import { WifiOff } from 'lucide-vue-next'
</script>
