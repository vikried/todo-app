<template>
  <div
    v-if="modelValue"
    class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50"
  >
    <div class="bg-white rounded-xl shadow-lg p-6 w-96 dark:bg-gray-900 dark:text-gray-100">
      <h2 class="text-lg font-semibold mb-4 dark:text-gray-100">{{ title }}</h2>
      <p class="text-gray-600 dark:text-gray-300 mb-6">{{ message }}</p>

      <div class="flex justify-end gap-3">
        <BaseButton variant="secondary" @click="$emit('cancel')">Abbrechen</BaseButton>
        <BaseButton variant="danger" @click="$emit('confirm')">Löschen</BaseButton>
      </div>
    </div>
  </div>
</template>

<script setup>
import BaseButton from '@/components/BaseButton.vue'

defineProps({
  modelValue: { type: Boolean, default: false },
  title: { type: String, default: 'Wirklich löschen?' },
  message: { type: String, default: 'Diese Aktion kann nicht rückgängig gemacht werden.' }
})

defineEmits(['confirm', 'cancel'])
</script>
