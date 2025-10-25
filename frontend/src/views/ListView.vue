<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-4">Todo-Listen</h1>

    <form @submit.prevent="createList" class="mb-6 flex gap-2">
      <input v-model="newListName" placeholder="Neue Liste" class="border rounded p-2 flex-1" />
      <button type="submit" class="bg-blue-600 text-white px-4 py-2 rounded">Erstellen</button>
    </form>

    <ul>
      <li v-for="list in todoLists" :key="list.id" class="border p-3 mb-2 rounded hover:bg-gray-50 cursor-pointer"
          @click="$router.push(`/lists/${list.id}`)">
        {{ list.name }}
        <button
          @click.stop="deleteList(list.id)"
          class="text-red-600 hover:text-red-800"
          title="Liste löschen"
        >
          🗑️
        </button>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useTodoListStore } from '@/store/todoListStore';

const todoListStore = useTodoListStore()

const todoLists = ref([])
const newListName = ref('')

const loadLists = async () => {
  todoListStore.fetchTodoLists().then(() => {
    todoLists.value = todoListStore.todoLists
  })
}

const createList = async () => {
  if (!newListName.value) return
  await todoListStore.createTodoList(newListName.value, false)
  newListName.value = ''
  loadLists()
}

const deleteList = async (id) => {
  await todoListStore.deleteTodoList(id)
  loadLists()
}

onMounted(loadLists)
</script>