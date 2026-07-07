<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-4 dark:text-gray-100">Templates</h1>

    <form @submit.prevent="createTemplate" class="mb-6 flex gap-2">
      <input v-model="newTemplateName" placeholder="Neues Template" class="border rounded p-2 flex-1 min-w-0 dark:text-gray-100 dark:bg-gray-700" />
      <BaseButton type="submit">Erstellen</BaseButton>
    </form>

    <p v-if="loading" class="text-gray-500 dark:text-gray-400">Lade Templates …</p>
    <p v-else-if="templateLists.length === 0" class="text-gray-500 dark:text-gray-400">
      Noch keine Templates. Leg dein erstes Template an!
    </p>

    <ul v-else>
      <li v-for="template in templateLists" :key="template.id"
          class="border p-3 mb-2 rounded flex items-center justify-between hover:bg-gray-200 dark:hover:bg-gray-700 dark:bg-gray-900 dark:text-gray-100 cursor-pointer"
          @click="$router.push(`/lists/${template.id}`)">
        <span>{{ template.name }}</span>
        <IconButton title="Template löschen" @click.stop="askDelete(template)">
          <Trash2 class="w-4 h-4" />
        </IconButton>
      </li>
    </ul>

    <ConfirmDialog
      v-model="showConfirm"
      title="Template löschen"
      :message="`Soll das Template „${pendingTemplate?.name}“ wirklich gelöscht werden?`"
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

const templateLists = ref([])
const newTemplateName = ref('')
const loading = ref(true)

const showConfirm = ref(false)
const pendingTemplate = ref(null)

const loadTemplates = async () => {
  loading.value = true
  try {
    await todoListStore.fetchTemplates()
    templateLists.value = todoListStore.todoLists
  } finally {
    loading.value = false
  }
}

const createTemplate = async () => {
  if (!newTemplateName.value) return
  await todoListStore.createTodoList(newTemplateName.value, true)
  newTemplateName.value = ''
  loadTemplates()
}

const askDelete = (template) => {
  pendingTemplate.value = template
  showConfirm.value = true
}

const confirmDelete = async () => {
  if (!pendingTemplate.value) return
  await todoListStore.deleteTodoList(pendingTemplate.value.id)
  showConfirm.value = false
  pendingTemplate.value = null
  loadTemplates()
}

onMounted(loadTemplates)
</script>
