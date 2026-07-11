<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-4 dark:text-gray-100">Templates</h1>

    <form @submit.prevent="createTemplate" class="mb-6 flex gap-2">
      <input v-model="newTemplateName" placeholder="Neues Template" class="border rounded p-2 flex-1 min-w-0 dark:text-gray-100 dark:bg-gray-700" />
      <BaseButton type="submit">Erstellen</BaseButton>
    </form>

    <button
      type="button"
      @click="showImport = !showImport"
      class="flex items-center gap-1 text-sm text-blue-600 dark:text-blue-400 hover:underline mb-4"
    >
      <ChevronRight class="w-4 h-4 transition-transform" :class="{ 'rotate-90': showImport }" />
      {{ showImport ? 'Import ausblenden' : 'Template importieren' }}
    </button>

    <div v-if="showImport" class="mb-2">
      <form @submit.prevent="importTemplate" class="flex flex-wrap gap-2 items-center">
        <input v-model="importName" placeholder="Name des importierten Templates" class="border rounded p-2 flex-1 min-w-0 dark:text-gray-100 dark:bg-gray-700" />
        <input
          ref="fileInput"
          type="file"
          accept=".csv,.json"
          @change="onFileSelected"
          class="text-sm text-gray-600 dark:text-gray-300 file:mr-2 file:py-2 file:px-3 file:rounded file:border-0 file:bg-gray-200 file:text-gray-800 file:cursor-pointer hover:file:bg-gray-300 dark:file:bg-gray-700 dark:file:text-gray-100 dark:hover:file:bg-gray-600"
        />
        <BaseButton type="submit" variant="secondary" :disabled="!importFile || importing">
          {{ importing ? 'Importiere …' : 'Importieren' }}
        </BaseButton>
      </form>
      <p class="text-xs text-gray-500 dark:text-gray-400 mt-2">
        CSV mit Spalten „category" (optional) und „title", oder JSON im Format
        <code>&#123;"categories":[&#123;"name":"…","todos":["…"]&#125;],"todos":["…"]&#125;</code>.
      </p>
      <p v-if="importError" class="text-sm text-red-600 dark:text-red-400 mt-2">{{ importError }}</p>
    </div>

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
import { Trash2, ChevronRight } from 'lucide-vue-next'

const todoListStore = useTodoListStore()

const templateLists = ref([])
const newTemplateName = ref('')
const loading = ref(true)

const showConfirm = ref(false)
const pendingTemplate = ref(null)

const showImport = ref(false)
const importName = ref('')
const importFile = ref(null)
const importing = ref(false)
const importError = ref('')
const fileInput = ref(null)

const onFileSelected = (event) => {
  importFile.value = event.target.files[0] ?? null
  importError.value = ''
}

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

const importTemplate = async () => {
  if (!importFile.value) return
  importing.value = true
  importError.value = ''
  try {
    const name = importName.value || importFile.value.name.replace(/\.(csv|json)$/i, '')
    await todoListStore.importTodoList(importFile.value, name, true)
    importName.value = ''
    importFile.value = null
    if (fileInput.value) fileInput.value.value = ''
    loadTemplates()
  } catch (err) {
    importError.value = err.response?.data?.error || 'Import fehlgeschlagen. Bitte Datei prüfen.'
  } finally {
    importing.value = false
  }
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
