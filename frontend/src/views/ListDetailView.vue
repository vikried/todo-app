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
          <input v-model="newCategoryName" placeholder="Neue Kategorie" class="border rounded p-2 flex-1" />
          <button type="submit" class="bg-green-600 text-white px-4 py-2 rounded">Hinzufügen</button>
        </form>
        </div>

        <div v-for="category in categories" :key="category.id" class="mb-6 border rounded p-3">
          <h3 class="font-semibold text-lg mb-2">{{ category.name }}</h3>

          <table class="w-full border text-sm">
            <thead>
              <tr class="bg-gray-100">
                <th class="p-2 text-left">Todo</th>
                <th class="p-2 text-left">Status</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="todo in category.todos" :key="todo.id" class="border-t">
                <td class="p-2">{{ todo.title }}</td>
                <td class="p-2">{{ todo.done ? '✅' : '❌' }}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </div>
  </template>

  <script setup>
  import { ref, onMounted } from 'vue'
  import axios from 'axios'
  import { useRoute } from 'vue-router'
  import { useTodoListStore } from '@/store/todoListStore';
  import { useCategoryStore } from '@/store/categoryStore'

  const todoListStore = useTodoListStore()
  const categoryStore = useCategoryStore()

  const route = useRoute()
  const listId = route.params.id

  const list = ref(null)
  const categories = ref(null)
  const newCategoryName = ref('')

  const editMode = ref(false)

  const loadList = async () => {
    list.value = await todoListStore.findListById(listId)
  }

  const loadCategories = async () => {
    categories.value = await categoryStore.findCategoriesByList(listId)
  }

  const createCategoryAndAddToList = async (listId) => {
    if (!newCategoryName.value) return
    const createdCategory = await categoryStore.addCategory(newCategoryName.value)
    await todoListStore.addCategoryToTodoList(listId, createdCategory.id)
    newCategoryName.value = ''
    loadCategories()
  }

  const toggleEditMode = () => {
    editMode.value = !editMode.value
  }

  onMounted(() => {
    loadList()
    loadCategories()
  })
  </script>