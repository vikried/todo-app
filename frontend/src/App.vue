<template>
  <div class="app-container dark:bg-gray-800 dark:border-gray-700">
    <OfflineBanner v-if="!isOnline" />
    <div v-if="pendingCount > 0" class="sticky top-0 z-30 flex items-center gap-2 bg-blue-100 text-blue-900 px-4 py-2 text-sm dark:bg-blue-900 dark:text-blue-100">
      <RefreshCw class="w-4 h-4 flex-shrink-0 animate-spin" />
      <span>{{ pendingCount }} Änderung{{ pendingCount === 1 ? '' : 'en' }} werden synchronisiert …</span>
    </div>
    <div v-if="conflictCount > 0" class="sticky top-0 z-30 flex items-center gap-2 bg-red-100 text-red-900 px-4 py-2 text-sm dark:bg-red-900 dark:text-red-100">
      <AlertTriangle class="w-4 h-4 flex-shrink-0" />
      <span class="flex-1">
        {{ conflictCount }} Änderung{{ conflictCount === 1 ? '' : 'en' }} konnte{{ conflictCount === 1 ? '' : 'n' }} nicht übernommen werden
        (Eintrag evtl. auf einem anderen Gerät gelöscht).
      </span>
      <button type="button" class="underline flex-shrink-0" @click="conflictCount = 0">Ausblenden</button>
    </div>
    <Navbar v-if="route.path !== '/login'" />
    <router-view />
  </div>
</template>

<script setup>
 import Navbar from '@/components/Navbar.vue'
 import OfflineBanner from '@/components/OfflineBanner.vue'
 import { useRoute } from 'vue-router'
 import { useOnlineStatus } from '@/composables/useOnlineStatus.js'
 import { pendingCount, conflictCount } from '@/offline/outboxReplay.js'
 import { RefreshCw, AlertTriangle } from 'lucide-vue-next'

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
