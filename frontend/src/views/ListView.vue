<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-4 dark:text-gray-100">Todo-Listen</h1>

    <form @submit.prevent="createList" class="mb-6 flex gap-2">
      <input v-model="newListName" placeholder="Neue Liste" class="border rounded p-2 flex-1 min-w-0 dark:text-gray-100 dark:bg-gray-700" />
      <BaseButton type="submit">Erstellen</BaseButton>
    </form>

    <p v-if="loading" class="text-gray-500 dark:text-gray-400">Lade Listen …</p>
    <p v-else-if="todoLists.length === 0" class="text-gray-500 dark:text-gray-400">
      Noch keine Todo-Listen. Leg deine erste Liste an!
    </p>

    <ul v-else>
      <li v-for="list in todoLists" :key="list.id"
          class="border p-3 mb-2 rounded flex items-center justify-between hover:bg-gray-200 cursor-pointer dark:bg-gray-900 dark:hover:bg-gray-700 dark:text-gray-100"
          @click="$router.push(`/lists/${list.id}`)">
        <span>{{ list.name }}</span>
        <IconButton title="Liste löschen" @click.stop="askDelete(list)">
          <Trash2 class="w-4 h-4" />
        </IconButton>
      </li>
    </ul>

    <ConfirmDialog
      v-model="showConfirm"
      title="Liste löschen"
      :message="`Soll die Liste „${pendingList?.name}“ wirklich gelöscht werden?`"
      @confirm="confirmDelete"
      @cancel="showConfirm = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useTodoListStore } from '@/store/todoListStore';
import BaseButton from '@/components/BaseButton.vue'
import IconButton from '@/components/IconButton.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import { Trash2 } from 'lucide-vue-next'

const todoListStore = useTodoListStore()

const todoLists = ref([])
const newListName = ref('')
const loading = ref(true)

const showConfirm = ref(false)
const pendingList = ref(null)

const loadLists = async () => {
  loading.value = true
  try {
    await todoListStore.fetchTodoLists()
    todoLists.value = todoListStore.todoLists
  } finally {
    loading.value = false
  }
}

const createList = async () => {
  if (!newListName.value) return
  await todoListStore.createTodoList(newListName.value, false)
  newListName.value = ''
  loadLists()
}

const askDelete = (list) => {
  pendingList.value = list
  showConfirm.value = true
}

const confirmDelete = async () => {
  if (!pendingList.value) return
  await todoListStore.deleteTodoList(pendingList.value.id)
  showConfirm.value = false
  pendingList.value = null
  loadLists()
}

onMounted(loadLists)
</script>
