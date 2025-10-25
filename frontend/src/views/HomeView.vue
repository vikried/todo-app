<template>
  <div>
    <h2 class="text-2xl font-bold mb-4">Meine Aufgaben</h2>
    <TodoForm @add-todo="addTodo" />
    <TodoList :todos="todos" @delete-todo="deleteTodo" @toggle-todo="toggleTodo" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import TodoForm from '@/components/TodoForm.vue'
import TodoList from '@/components/TodoList.vue'
import { useTodoStore } from '@/store/todoStore'

const todoStore = useTodoStore()

const todos = ref([])

onMounted(() => {
  todoStore.fetchTodos().then(() => {
    todos.value = todoStore.todos
  })
})

const addTodo = async (title) => {
  await todoStore.addTodo(title)
  todos.value = todoStore.todos
}

const deleteTodo = async (id) => {
  await todoStore.deleteTodo(id)
  todos.value = todoStore.todos
}

const toggleTodo = async (todo) => {
  await todoStore.updateTodo(todo, { ...todo, done: !todo.done })
  todos.value = todoStore.todos
}
</script>