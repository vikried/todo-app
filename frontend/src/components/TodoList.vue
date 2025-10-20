<template>
  <ul>
    <li v-for="todo in todos" :key="todo.id">
      {{ todo.title }}
      {{ todo.categoryId }}
      <input type="checkbox" :checked="todo.done" @change="$emit('toggle-todo', todo)" class="mr-2" />
      <button @click="$emit('delete-todo', todo.id)">❌</button>
    </li>
  </ul>
</template>

<script setup>
defineProps({
  todos: Array
})
</script>