<template>
  <form @submit.prevent="onSubmit">
    <input v-model="title" placeholder="Neue Aufgabe..." />
    <button type="submit">Hinzufügen</button>
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