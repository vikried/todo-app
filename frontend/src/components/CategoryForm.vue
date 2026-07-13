<template>
  <h3 class="font-semibold text-lg mb-2 dark:text-gray-100 flex items-center justify-between gap-2">
    <span v-if="!editingName" class="flex-1 min-w-0 break-words">{{ category.name }}</span>
    <input
      v-else
      ref="nameInput"
      v-model="nameDraft"
      class="flex-1 min-w-0 border rounded px-2 py-1 text-base font-normal dark:text-gray-100 dark:bg-gray-700"
      @keyup.enter="saveName"
      @keyup.escape="cancelEditName"
      @blur="saveName"
    />
    <span class="flex items-center gap-1 flex-shrink-0">
      <IconButton v-if="editMode && !editingName" title="Kategorie umbenennen" @click.stop="startEditName">
        <Pencil class="w-4 h-4" />
      </IconButton>
      <IconButton v-if="editMode" title="Kategorie löschen" @click.stop="$emit('delete-category', category.id)">
        <Trash2 class="w-4 h-4" />
      </IconButton>
    </span>
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
         class="flex items-center gap-1 dark:bg-gray-700 flex-wrap">
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
      <select
        v-if="editMode && categories && categories.length > 1"
        title="In andere Kategorie verschieben"
        class="flex-shrink-0 text-sm border rounded p-1 dark:text-gray-100 dark:bg-gray-700 max-w-[45%]"
        :value="category.id"
        @change="onMoveTodo(todo, $event.target.value)"
      >
        <option v-for="target in categories" :key="target.id" :value="target.id">
          {{ target.id === category.id ? '✓ ' + target.name : target.name }}
        </option>
      </select>
      <IconButton v-if="editMode" title="Todo löschen" class="flex-shrink-0 mr-1" @click.stop="$emit('delete-todo', todo)">
        <Trash2 class="w-4 h-4" />
      </IconButton>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { Trash2, CheckCircle2, Circle, Pencil } from 'lucide-vue-next'
import BaseButton from '@/components/BaseButton.vue'
import IconButton from '@/components/IconButton.vue'

const props = defineProps({
  category: {},
  categories: { type: Array, default: () => [] },
  editMode: false,
  isTemplate: false
});

const emit = defineEmits([
  'delete-category',
  'delete-todo',
  'toggle-todo',
  'create-todo',
  'rename-category',
  'move-todo'
]);

const newTodoName = ref('');

const onSubmitCreateTodo = (categoryId) => {
  if (!newTodoName.value.trim()) return;
  emit('create-todo', categoryId, newTodoName.value);
  newTodoName.value = '';
}

const editingName = ref(false);
const nameDraft = ref('');
const nameInput = ref(null);

const startEditName = async () => {
  nameDraft.value = props.category.name;
  editingName.value = true;
  await nextTick();
  nameInput.value?.focus();
  nameInput.value?.select();
}

const saveName = () => {
  if (!editingName.value) return;
  editingName.value = false;
  const trimmed = nameDraft.value.trim();
  if (trimmed && trimmed !== props.category.name) {
    emit('rename-category', props.category, trimmed);
  }
}

const cancelEditName = () => {
  editingName.value = false;
}

const onMoveTodo = (todo, newCategoryId) => {
  const targetId = Number(newCategoryId);
  if (targetId === props.category.id) return;
  emit('move-todo', todo, targetId);
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
