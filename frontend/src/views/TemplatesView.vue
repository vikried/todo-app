<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-4">Templates</h1>

    <form @submit.prevent="createTemplate" class="mb-6 flex gap-2">
      <input v-model="newTemplateName" placeholder="Neues Template" class="border rounded p-2 flex-1" />
      <button type="submit" class="bg-blue-600 text-white px-4 py-2 rounded">Erstellen</button>
    </form>

    <ul>
      <li v-for="template in templateLists" :key="template.id" class="border p-3 mb-2 rounded hover:bg-gray-50 cursor-pointer"
          @click="$router.push(`/lists/${template.id}`)">
        {{ template.name }}
        <button
          @click.stop="deleteTemplate(template.id)"
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

const templateLists = ref([])
const newTemplateName = ref('')

const loadTemplates = async () => {
  todoListStore.fetchTemplates().then(() => {
    templateLists.value = todoListStore.todoLists
  })
}

const createTemplate = async () => {
  if (!newTemplateName.value) return
  await todoListStore.createTodoList(newTemplateName.value, true)
  newTemplateName.value = ''
  loadTemplates()
}

const deleteTemplate = async (id) => {
  await todoListStore.deleteTodoList(id)
  loadTemplates()
}

onMounted(loadTemplates)
</script>