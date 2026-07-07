<template>
  <button
    :type="type"
    :disabled="disabled"
    class="px-4 py-2 rounded font-medium transition disabled:opacity-50 disabled:cursor-not-allowed"
    :class="variantClass"
  >
    <slot />
  </button>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  type: { type: String, default: 'button' },
  variant: { type: String, default: 'primary' }, // primary | secondary | danger
  disabled: { type: Boolean, default: false }
})

const variants = {
  primary: 'bg-blue-600 hover:bg-blue-800 text-white',
  secondary: 'bg-gray-200 hover:bg-gray-300 text-gray-800 dark:bg-gray-700 dark:hover:bg-gray-600 dark:text-gray-100',
  danger: 'bg-red-600 hover:bg-red-700 text-white'
}

const variantClass = computed(() => variants[props.variant] || variants.primary)
</script>
