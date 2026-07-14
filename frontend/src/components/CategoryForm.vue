<template>
  <h3 class="font-semibold text-lg mb-2 dark:text-gray-100 flex items-center justify-between gap-2">
    <button
      v-if="!editingName"
      type="button"
      class="flex-1 min-w-0 flex items-center gap-1 text-left"
      @click="$emit('toggle-open')"
    >
      <ChevronRight class="w-4 h-4 flex-shrink-0 transition-transform" :class="{ 'rotate-90': isOpen }" />
      <span class="min-w-0 break-words">{{ category.name }}</span>
    </button>
    <input
      v-else
      ref="nameInput"
      v-model="nameDraft"
      class="flex-1 min-w-0 border rounded px-2 py-1 text-base font-normal dark:text-gray-100 dark:bg-gray-700"
      @keyup.enter="saveName"
      @keyup.escape="cancelEditName"
    />
    <span class="flex items-center gap-1 flex-shrink-0">
      <template v-if="editMode && editingName">
        <IconButton title="Speichern" @click.stop="saveName">
          <Check class="w-4 h-4" />
        </IconButton>
        <IconButton title="Abbrechen" @click.stop="cancelEditName">
          <X class="w-4 h-4" />
        </IconButton>
      </template>
      <template v-else-if="editMode">
        <IconButton title="Kategorie umbenennen" @click.stop="startEditName">
          <Pencil class="w-4 h-4" />
        </IconButton>
        <IconButton title="Kategorie löschen" @click.stop="$emit('delete-category', category.id)">
          <Trash2 class="w-4 h-4" />
        </IconButton>
      </template>
    </span>
  </h3>
  <div v-if="isOpen">
    <div v-if="editMode">
      <form @submit.prevent="onSubmitCreateTodo(category.id)" class="flex gap-2 mb-4">
        <input v-model="newTodoName" placeholder="Neues Todo" class="border rounded p-2 flex-1 min-w-0 dark:text-gray-100 dark:bg-gray-700"/>
        <BaseButton type="submit">Hinzufügen</BaseButton>
      </form>
    </div>

    <p v-if="!category.todos || category.todos.length === 0" class="text-sm text-gray-500 dark:text-gray-400">
      Noch keine Todos in dieser Kategorie.
    </p>
    <div v-else class="border rounded dark:border-gray-700 divide-y divide-gray-200 dark:divide-gray-600 overflow-hidden">
      <div v-for="todo in sortTodos(category.todos)" :key="todo.id"
           :draggable="editMode"
           class="dark:bg-gray-700 px-1"
           :class="{ 'cursor-grab': editMode }"
           @dragstart="onDragStart(todo, $event)">
        <div v-if="editMode" class="flex justify-end gap-1 pt-1">
          <template v-if="editingTodoId === todo.id">
            <IconButton title="Speichern" @click.stop="saveTodoName(todo)">
              <Check class="w-4 h-4" />
            </IconButton>
            <IconButton title="Abbrechen" @click.stop="cancelEditTodoName">
              <X class="w-4 h-4" />
            </IconButton>
          </template>
          <template v-else>
            <IconButton title="Todo umbenennen" @click.stop="startEditTodoName(todo)">
              <Pencil class="w-4 h-4" />
            </IconButton>
            <IconButton
              v-if="categories && categories.length > 1"
              title="In andere Kategorie verschieben"
              @click.stop="openMovePopup(todo)"
            >
              <Move class="w-4 h-4" />
            </IconButton>
            <IconButton title="Todo löschen" @click.stop="$emit('delete-todo', todo)">
              <Trash2 class="w-4 h-4" />
            </IconButton>
          </template>
        </div>
        <div class="flex items-start gap-1">
          <button v-if="!isTemplate && !editMode"
                  type="button"
                  class="flex-shrink-0 inline-flex items-center justify-center min-w-[44px] min-h-[44px]"
                  :title="todo.done ? 'Als offen markieren' : 'Als erledigt markieren'"
                  @click="$emit('toggle-todo', todo)">
            <CheckCircle2 v-if="todo.done" class="w-5 h-5 text-green-600 dark:text-green-400" />
            <Circle v-else class="w-5 h-5 text-gray-400 dark:text-gray-500" />
          </button>
          <span v-if="editingTodoId !== todo.id"
                class="flex-1 min-w-0 py-2 break-words"
                :class="todo.done && !isTemplate ? 'text-gray-400 dark:text-gray-500 line-through' : 'dark:text-gray-100'">
            {{ todo.title }}
          </span>
          <input
            v-else
            :ref="el => setTodoNameInput(todo.id, el)"
            v-model="todoNameDraft"
            class="flex-1 min-w-0 border rounded px-2 py-1 my-1 dark:text-gray-100 dark:bg-gray-700"
            @keyup.enter="saveTodoName(todo)"
            @keyup.escape="cancelEditTodoName"
          />
        </div>
      </div>
    </div>
  </div>

  <div
    v-if="movingTodo"
    class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50"
    @click.self="closeMovePopup"
  >
    <div class="bg-white rounded-xl shadow-lg p-6 w-full max-w-sm mx-4 dark:bg-gray-900 dark:text-gray-100">
      <h2 class="text-lg font-semibold mb-4 dark:text-gray-100 break-words">
        „{{ movingTodo.title }}" verschieben nach:
      </h2>
      <select
        v-model="moveTargetId"
        class="border w-full px-3 py-2 rounded mb-4 dark:text-gray-100 dark:bg-gray-700"
      >
        <option v-for="target in categories" :key="target.id" :value="target.id">
          {{ target.name }}
        </option>
      </select>
      <div class="flex justify-end gap-3">
        <BaseButton variant="secondary" @click="closeMovePopup">Abbrechen</BaseButton>
        <BaseButton :disabled="moveTargetId === category.id" @click="confirmMove">OK</BaseButton>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { Trash2, CheckCircle2, Circle, Pencil, Check, X, Move, ChevronRight } from 'lucide-vue-next'
import BaseButton from '@/components/BaseButton.vue'
import IconButton from '@/components/IconButton.vue'

const props = defineProps({
  category: {},
  categories: { type: Array, default: () => [] },
  editMode: false,
  isTemplate: false,
  isOpen: { type: Boolean, default: true }
});

const emit = defineEmits([
  'delete-category',
  'delete-todo',
  'toggle-todo',
  'create-todo',
  'rename-category',
  'rename-todo',
  'move-todo',
  'toggle-open'
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

const editingTodoId = ref(null);
const todoNameDraft = ref('');
let todoNameInputEl = null;

const setTodoNameInput = (todoId, el) => {
  if (editingTodoId.value === todoId) {
    todoNameInputEl = el;
  }
}

const startEditTodoName = async (todo) => {
  editingTodoId.value = todo.id;
  todoNameDraft.value = todo.title;
  await nextTick();
  todoNameInputEl?.focus();
  todoNameInputEl?.select();
}

const saveTodoName = (todo) => {
  if (editingTodoId.value !== todo.id) return;
  editingTodoId.value = null;
  todoNameInputEl = null;
  const trimmed = todoNameDraft.value.trim();
  if (trimmed && trimmed !== todo.title) {
    emit('rename-todo', todo, trimmed);
  }
}

const cancelEditTodoName = () => {
  editingTodoId.value = null;
  todoNameInputEl = null;
}

const movingTodo = ref(null);
const moveTargetId = ref(null);

const openMovePopup = (todo) => {
  movingTodo.value = todo;
  moveTargetId.value = props.category.id;
}

const closeMovePopup = () => {
  movingTodo.value = null;
  moveTargetId.value = null;
}

const confirmMove = () => {
  if (!movingTodo.value || moveTargetId.value === props.category.id) return;
  emit('move-todo', movingTodo.value, moveTargetId.value);
  closeMovePopup();
}

const onDragStart = (todo, event) => {
  if (!props.editMode) return;
  event.dataTransfer.setData('text/plain', String(todo.id));
  event.dataTransfer.effectAllowed = 'move';
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
