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

  <p v-if="!category.todos || category.todos.length === 0" class="text-sm text-gray-500 dark:text-gray-400">
    Noch keine Todos in dieser Kategorie.
  </p>
  <div v-else class="border rounded dark:border-gray-700 divide-y dark:divide-gray-700 overflow-hidden">
    <div v-for="todo in sortTodos(category.todos)" :key="todo.id"
         class="flex items-center gap-1 dark:bg-gray-700">
      <button v-if="!isTemplate"
              type="button"
              class="flex-shrink-0 inline-flex items-center justify-center min-w-[44px] min-h-[44px]"
              :title="todo.done ? 'Als offen markieren' : 'Als erledigt markieren'"
              @click="$emit('toggle-todo', todo)">
        <CheckCircle2 v-if="todo.done" class="w-5 h-5 text-green-600 dark:text-green-400" />
        <Circle v-else class="w-5 h-5 text-gray-400 dark:text-gray-500" />
      </button>
      <span class="flex-1 min-w-0 py-2 break-words"
            :class="todo.done && !isTemplate ? 'text-gray-400 dark:text-gray-500 line-through' : 'dark:text-gray-100'">
        {{ todo.title }}
      </span>
      <IconButton v-if="editMode" title="Todo löschen" class="flex-shrink-0 mr-1" @click.stop="$emit('delete-todo', todo)">
        <Trash2 class="w-4 h-4" />
      </IconButton>
    </div>
  </div>
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
