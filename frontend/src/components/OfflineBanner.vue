<template>
  <div class="sticky top-0 z-30 flex items-center gap-2 bg-amber-100 text-amber-900 px-4 py-2 text-sm dark:bg-amber-900 dark:text-amber-100">
    <WifiOff class="w-4 h-4 flex-shrink-0" />
    <span>Offline – Änderungen werden lokal gespeichert und automatisch synchronisiert, sobald wieder eine Verbindung besteht.</span>
  </div>
</template>

<script setup>
import { WifiOff } from 'lucide-vue-next'
</script>
