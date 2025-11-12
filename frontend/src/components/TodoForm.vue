<template>
  <form @submit.prevent="onSubmit" class="mb-6 flex gap-2">
    <input v-model="title" placeholder="Neue Aufgabe..." class="border rounded p-2 flex-1" />
    <button type="submit" class="bg-blue-600 hover:bg-blue-800 text-white px-4 py-2 rounded">Hinzufügen</button>
  </form>
</template>

<script setup>
import { ref } from 'vue'

const title = ref('')
const emit = defineEmits(['add-todo'])

const onSubmit = () => {
  if (!title.value.trim()) return
  emit('add-todo', title.value)
  title.value = ''
}
</script>