<template>
  <ul>
    <li v-for="todo in todos" :key="todo.id">
      {{ todo.title }}
      <input type="checkbox" :checked="todo.done" @change="$emit('toggle-todo', todo.id)" class="mr-2" />
      <button @click="$emit('delete-todo', todo.id)">❌</button>
    </li>
  </ul>
</template>

<script setup>
defineProps({
  todos: Array
})
</script>