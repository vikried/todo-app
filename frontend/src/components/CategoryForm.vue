<template>
  <h3 class="font-semibold text-lg mb-2 dark:text-gray-100 flex items-center justify-between">
    <span>{{ category.name }}</span>
    <IconButton v-if="editMode" title="Kategorie löschen" @click.stop="$emit('delete-category', category.id)">
      <Trash2 class="w-4 h-4" />
    </IconButton>
  </h3>
  <div v-if="editMode">
    <form @submit.prevent="onSubmitCreateTodo(category.id)" class="flex gap-2 mb-4">
      <input v-model="newTodoName" placeholder="Neues Todo" class="border rounded p-2 flex-1 min-w-0 dark:text-gray-100 dark:bg-gray-700"/>
      <BaseButton type="submit">Hinzufügen</BaseButton>
    </form>
  </div>

  <table class="w-full border text-sm">
    <thead>
      <tr class="bg-gray-100 dark:bg-gray-700">
        <th class="w-1/3 px-4 py-2 text-left dark:text-gray-100">
          Todo
        </th>
        <th class="w-1/3 px-4 py-2 text-left dark:text-gray-100" v-if="!isTemplate">Status</th>
        <th class="w-1/3 px-4 py-2 text-left dark:text-gray-100" v-if="editMode">Aktionen</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="todo in sortTodos(category.todos)" :key="todo.id" class="border-t">
        <td class="p-2 truncate max-w-[200px] dark:text-gray-100 dark:bg-gray-700" :title="todo.title">{{ todo.title }}</td>
        <td class="p-2 dark:bg-gray-700 cursor-pointer" @click="$emit('toggle-todo', todo)" v-if="!isTemplate">
          <CheckCircle2 v-if="todo.done" class="w-5 h-5 text-green-600 dark:text-green-400" />
          <Circle v-else class="w-5 h-5 text-gray-400 dark:text-gray-500" />
        </td>
        <td class="p-2 dark:bg-gray-700" v-if="editMode">
          <IconButton title="Todo löschen" @click.stop="$emit('delete-todo', todo)">
            <Trash2 class="w-4 h-4" />
          </IconButton>
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script setup>
import { ref, defineEmits } from 'vue'
import { Trash2, CheckCircle2, Circle } from 'lucide-vue-next'
import BaseButton from '@/components/BaseButton.vue'
import IconButton from '@/components/IconButton.vue'

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
