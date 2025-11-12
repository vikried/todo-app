<template>
  <h3 class="font-semibold text-lg mb-2">
    {{ category.name }}
    <button v-if="editMode"
            @click.stop="$emit('delete-category', category.id)"
            class="text-red-600 hover:text-red-800"
            title="Kategorie löschen"
    >
      🗑️
    </button>
  </h3>
  <div v-if="editMode">
    <form @submit.prevent="onSubmitCreateTodo(category.id)" class="flex gap-2 mb-4">
      <input v-model="newTodoName" placeholder="Neues Todo" class="border rounded p-2 flex-1"/>
      <button type="submit" class="bg-blue-600 hover:bg-blue-800 text-white px-4 py-2 rounded">Hinzufügen</button>
    </form>
  </div>

  <table class="w-full border text-sm">
    <thead>
      <tr class="bg-gray-100">
        <th class="w-1/3 px-4 py-2 text-left">
          Todo
        </th>
        <th class="w-1/3 px-4 py-2 text-left" v-if="!isTemplate">Status</th>
        <th class="w-1/3 px-4 py-2 text-left" v-if="editMode">Aktionen</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="todo in sortTodos(category.todos)" :key="todo.id" class="border-t">
        <td class="p-2 truncate max-w-[200px]" :title="todo.title">{{ todo.title }}</td>
        <td class="p-2" @click="$emit('toggle-todo', todo)" v-if="!isTemplate">{{ todo.done ? '✅' : '❌' }}</td>
        <td class="p-2" v-if="editMode">
          <button @click.stop="$emit('delete-todo', todo)"
                  class="text-red-600 hover:text-red-800"
                  title="Todo löschen"
          >
            🗑️
          </button>

        </td>
      </tr>
    </tbody>
  </table>
</template>

<script setup>
import { ref, defineEmits } from 'vue'

defineProps({
  category: {},
  editMode: false,
  isTemplate: false
});

const emit = defineEmits([
  'delete-category',
  'delete-todo',
  'toggle-todo',
  'create-todo'
]);

const newTodoName = ref('');

const onSubmitCreateTodo = (categoryId) => {
  if (!newTodoName.value.trim()) return;
  emit('create-todo', categoryId, newTodoName.value);
  newTodoName.value = '';
}

function sortTodos(todos) {
  if (!todos) return [];
  return [...todos].sort((a, b) => {
    const titleA = a.title?.toLowerCase() || "";
    const titleB = b.title?.toLowerCase() || "";
    return titleA.localeCompare(titleB);
  });
}
</script>