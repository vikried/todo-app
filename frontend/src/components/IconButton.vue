<template>
  <button
    :type="type"
    class="inline-flex items-center justify-center p-1.5 rounded transition"
    :class="variantClass"
    :title="title"
  >
    <slot />
  </button>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  type: { type: String, default: 'button' },
  variant: { type: String, default: 'danger' }, // danger | neutral
  title: { type: String, default: '' }
})

const variants = {
  danger: 'text-red-600 hover:text-white hover:bg-red-600 dark:text-red-400 dark:hover:bg-red-600 dark:hover:text-white',
  neutral: 'text-gray-500 hover:text-gray-800 hover:bg-gray-200 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white'
}

const variantClass = computed(() => variants[props.variant] || variants.danger)
</script>
