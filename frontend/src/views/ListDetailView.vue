<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-4">Liste: {{ list?.name }}</h1>
    <button
      @click="toggleEditMode"
      class="bg-gray-200 px-4 py-2 rounded hover:bg-gray-300"
    >
      {{ editMode ? '✅ Speichern' : '✏️ Bearbeiten' }}
    </button>
    <br/>
    <br/>

    <section class="mb-6">
      <div v-if="editMode" class="mt-8 border-t pt-4">
        <h2 class="text-xl font-semibold mb-2">Kategorien</h2>
        <form @submit.prevent="createCategoryAndAddToList(list.id)" class="flex gap-2 mb-4">
          <input v-model="newCategoryName" placeholder="Neue Kategorie" class="border rounded p-2 flex-1"/>
          <button type="submit" class="bg-green-600 text-white px-4 py-2 rounded">Hinzufügen</button>
        </form>
      </div>

      <div v-for="category in categories" :key="category.id" class="mb-6 border rounded p-3">
        <h3 class="font-semibold text-lg mb-2">
          {{ category.name }}
          <button v-if="editMode"
                  @click.stop="deleteCategory(category.id)"
                  class="text-red-600 hover:text-red-800"
                  title="Kategorie löschen"
          >
            🗑️
          </button>
        </h3>
        <div v-if="editMode">
          <form @submit.prevent="createTodoAndAddToCategory(category.id)" class="flex gap-2 mb-4">
            <input v-model="newTodoName" placeholder="Neues Todo" class="border rounded p-2 flex-1"/>
            <button type="submit" class="bg-green-600 text-white px-4 py-2 rounded">Hinzufügen</button>
          </form>
        </div>

        <table class="w-full border text-sm">
          <thead>
            <tr class="bg-gray-100">
              <th class="w-1/3 px-4 py-2 text-left">
                Todo
              </th>
              <th class="w-1/3 px-4 py-2 text-left">Status</th>
              <th class="w-1/3 px-4 py-2 text-left" v-if="editMode">Aktionen</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="todo in sortTodos(category.todos)" :key="todo.id" class="border-t">
              <td class="p-2 truncate max-w-[200px]" :title="todo.title">{{ todo.title }}</td>
              <td class="p-2" @click="toggleTodoStatus(todo)">{{ todo.done ? '✅' : '❌' }}</td>
              <td class="p-2" v-if="editMode">
                <button @click.stop="deleteTodo(todo)"
                        class="text-red-600 hover:text-red-800"
                        title="Todo löschen"
                >
                  🗑️
                </button>

              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useTodoListStore } from '@/store/todoListStore';
import { useCategoryStore } from '@/store/categoryStore'
import {useTodoStore} from "@/store/todoStore.js";

const todoListStore = useTodoListStore();
const categoryStore = useCategoryStore();
const todoStore = useTodoStore();

const route = useRoute();
const listId = route.params.id;

const list = ref(null);
const categories = ref(null);
const newCategoryName = ref('');
const newTodoName = ref('');

const editMode = ref(false);

function sortTodos(todos) {
  if (!todos) return [];
  return [...todos].sort((a, b) => {
    const titleA = a.title?.toLowerCase() || "";
    const titleB = b.title?.toLowerCase() || "";
    return titleA.localeCompare(titleB);
  });
}

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

const createTodoAndAddToCategory = async(categoryId) => {
  if (!newTodoName.value) return;
  const data = { title: newTodoName.value};
  const createdTodo = await todoStore.addTodo(data);
  await categoryStore.addTodoToCategory(categoryId, createdTodo.id);
  newTodoName.value = '';
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

const toggleEditMode = () => {
  editMode.value = !editMode.value;
}

onMounted(() => {
  loadList();
  loadCategories();
})
</script>