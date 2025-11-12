<template xmlns="http://www.w3.org/1999/html">
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-4">Liste: {{ list?.name }}</h1>
    <div class="flex items-center gap-2">
      <button
        @click="toggleEditMode"
        class="flex items-center gap-2 bg-blue-500 text-white px-3 py-2 rounded hover:bg-blue-800 transition"
      >
        <SquarePen v-if="!editMode" class="w-5 h-5"/>
        <Save v-if="editMode" class="w-5 h-5"/>
        {{ editMode ? 'Speichern' : 'Bearbeiten' }}
      </button><nobr/>
      <button
        v-if="list?.template"
        @click="openTemplatePopup(list.id)"
        class="flex items-center gap-2 bg-blue-500 text-white px-3 py-2 rounded hover:bg-blue-800 transition"
      >
        <FilePlus class="w-5 h-5" />
        Liste erstellen
      </button>
    </div>
    <br/>
    <br/>

    <section class="mb-6">
      <div v-if="editMode" class="mt-8 border-t pt-4">
        <h2 class="text-xl font-semibold mb-2">Kategorien</h2>
        <form @submit.prevent="createCategoryAndAddToList(list.id)" class="flex gap-2 mb-4">
          <input v-model="newCategoryName" placeholder="Neue Kategorie" class="border rounded p-2 flex-1"/>
          <button type="submit" class="bg-blue-600 text-white hover:bg-blue-800 px-4 py-2 rounded">Hinzufügen</button>
        </form>
      </div>

      <div v-for="category in categories" :key="category.id" class="mb-6 border rounded p-3">
        <CategoryForm :category="category" :editMode="editMode" :is-template="list?.template" @delete-category="deleteCategory" @delete-todo="deleteTodo" @toggle-todo="toggleTodoStatus" @create-todo="createTodoAndAddToCategory"/>
      </div>
    </section>
  </div>

  <!-- Popup -->
  <div
    v-if="showCreateListFromTemplatePopup"
    class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50"
  >
    <div class="bg-white rounded-xl shadow-lg p-6 w-96">
      <h2 class="text-lg font-semibold mb-4">Neue Liste aus Template</h2>

      <label class="block mb-2 text-sm text-gray-600">Name der Liste:</label>
      <input
        v-model="newListName"
        type="text"
        class="border w-full px-3 py-2 rounded focus:outline-none focus:ring focus:ring-blue-300"
        placeholder="z.B. WW Reise 2026"
      />

      <div class="flex justify-end gap-3 mt-4">
        <button
          @click="showCreateListFromTemplatePopup = false"
          class="px-3 py-2 rounded bg-gray-200 hover:bg-gray-300"
        >
          Abbrechen
        </button>
        <button
          @click="createListFromTemplate"
          class="px-3 py-2 rounded bg-blue-600 text-white hover:bg-blue-700"
        >
          Erstellen
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useTodoListStore } from '@/store/todoListStore';
import { useCategoryStore } from '@/store/categoryStore'
import {useTodoStore} from "@/store/todoStore.js";
import CategoryForm from "@/components/CategoryForm.vue";
import { FilePlus, SquarePen, Save } from 'lucide-vue-next';

const todoListStore = useTodoListStore();
const categoryStore = useCategoryStore();
const todoStore = useTodoStore();

const route = useRoute();
const listId = route.params.id;

const list = ref(null);
const categories = ref(null);
const newCategoryName = ref('');

const editMode = ref(false);

const showCreateListFromTemplatePopup = ref(false);
const newListName = ref('');
const selectedTemplateId = ref('');

const loadList = async() => {
  list.value = await todoListStore.findListById(listId);
}

const loadCategories = async() => {
  categories.value = await categoryStore.findCategoriesByList(listId);
}

const createCategoryAndAddToList = async(listId) => {
  if (!newCategoryName.value) return;
  const createdCategory = await categoryStore.addCategory(newCategoryName.value);
  await todoListStore.addCategoryToTodoList(listId, createdCategory.id);
  newCategoryName.value = '';
  loadCategories();
}

const createTodoAndAddToCategory = async(categoryId, newTodoName) => {
  const data = { title: newTodoName};
  const createdTodo = await todoStore.addTodo(data);
  await categoryStore.addTodoToCategory(categoryId, createdTodo.id);
  loadCategories();
}

const deleteCategory = async(categoryId) => {
  await categoryStore.deleteCategory(categoryId);
  loadCategories();
}

const toggleTodoStatus = async(todo) => {
  //const todo = await todoStore.findTodoById(todoId);
  await todoStore.updateTodo(todo, {...todo, done: !todo.done});
  loadCategories();
}

const deleteTodo = async(todo) => {
  await todoStore.deleteTodo(todo.id);
  loadCategories();
}

const createListFromTemplate = async() => {
  if (!newListName.value) return
  await todoListStore.createListFormTemplate(selectedTemplateId.value, newListName.value);
  newListName.value = '';
  showCreateListFromTemplatePopup.value = false;
}

const toggleEditMode = () => {
  editMode.value = !editMode.value;
}

// Logik: Popup öffnen
function openTemplatePopup(templateId) {
  selectedTemplateId.value = templateId;
  showCreateListFromTemplatePopup.value = true;
}

onMounted(() => {
  loadList();
  loadCategories();
})
</script>