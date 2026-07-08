<template xmlns="http://www.w3.org/1999/html">
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-4 dark:text-gray-100">Liste: {{ list?.name }}</h1>
    <div class="flex items-center gap-2 mb-6 dark:text-gray-100">
      <BaseButton class="flex items-center gap-2" @click="toggleEditMode">
        <SquarePen v-if="!editMode" class="w-5 h-5"/>
        <Check v-if="editMode" class="w-5 h-5"/>
        {{ editMode ? 'Fertig' : 'Bearbeiten' }}
      </BaseButton>
      <BaseButton
        v-if="list?.template"
        class="flex items-center gap-2"
        @click="openTemplatePopup(list.id)"
      >
        <FilePlus class="w-5 h-5" />
        Liste erstellen
      </BaseButton>
      <BaseButton
        v-if="isOwner"
        class="flex items-center gap-2"
        @click="openSharePopup"
      >
        <Share2 class="w-5 h-5" />
        Teilen
      </BaseButton>
    </div>

    <p v-if="loading" class="text-gray-500 dark:text-gray-400">Lade Liste …</p>

    <section v-else class="mb-6">
      <div v-if="editMode" class="mt-8 border-t pt-4">
        <h2 class="text-xl font-semibold mb-2 dark:text-gray-100">Kategorien</h2>
        <form @submit.prevent="createCategoryAndAddToList(list.id)" class="flex gap-2 mb-4">
          <input v-model="newCategoryName" placeholder="Neue Kategorie" class="border rounded p-2 flex-1 min-w-0 dark:text-gray-100 dark:bg-gray-700"/>
          <BaseButton type="submit">Hinzufügen</BaseButton>
        </form>
      </div>

      <p v-if="!categories || categories.length === 0" class="text-gray-500 dark:text-gray-400">
        Noch keine Kategorien in dieser Liste.
      </p>

      <div v-for="category in categories" :key="category.id" class="mb-6 border rounded p-3">
        <CategoryForm :category="category" :editMode="editMode" :is-template="list?.template" @delete-category="askDeleteCategory" @delete-todo="askDeleteTodo" @toggle-todo="toggleTodoStatus" @create-todo="createTodoAndAddToCategory"/>
      </div>
    </section>
  </div>

  <!-- Popup: Liste aus Template -->
  <div
    v-if="showCreateListFromTemplatePopup"
    class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50"
  >
    <div class="bg-white rounded-xl shadow-lg p-6 w-full max-w-sm mx-4 dark:bg-gray-900 dark:text-gray-100">
      <h2 class="text-lg font-semibold mb-4 dark:text-gray-100">Neue Liste aus Template</h2>

      <label class="block mb-2 text-sm text-gray-600 dark:text-gray-100">Name der Liste:</label>
      <input
        v-model="newListName"
        type="text"
        class="border w-full px-3 py-2 rounded focus:outline-none focus:ring focus:ring-blue-300 dark:text-gray-100 dark:bg-gray-700"
        placeholder="z.B. WW Reise 2026"
      />

      <div class="flex justify-end gap-3 mt-4">
        <BaseButton variant="secondary" @click="showCreateListFromTemplatePopup = false">
          Abbrechen
        </BaseButton>
        <BaseButton @click="createListFromTemplate">
          Erstellen
        </BaseButton>
      </div>
    </div>
  </div>

  <!-- Popup: Liste teilen -->
  <div
    v-if="showSharePopup"
    class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50"
  >
    <div class="bg-white rounded-xl shadow-lg p-6 w-full max-w-sm mx-4 dark:bg-gray-900 dark:text-gray-100">
      <h2 class="text-lg font-semibold mb-4 dark:text-gray-100">Liste teilen</h2>

      <p v-if="!list?.sharedWith || list.sharedWith.length === 0" class="text-sm text-gray-500 dark:text-gray-400 mb-3">
        Diese Liste ist noch mit niemandem geteilt.
      </p>
      <ul v-else class="mb-4">
        <li v-for="username in list.sharedWith" :key="username"
            class="flex items-center justify-between border rounded p-2 mb-2 dark:border-gray-700">
          <span>{{ username }}</span>
          <IconButton title="Freigabe entfernen" @click="removeShare(username)">
            <X class="w-4 h-4" />
          </IconButton>
        </li>
      </ul>

      <label class="block mb-2 text-sm text-gray-600 dark:text-gray-100">Mit Nutzer teilen:</label>
      <select
        v-model="selectedShareUsername"
        class="border w-full px-3 py-2 rounded focus:outline-none focus:ring focus:ring-blue-300 dark:text-gray-100 dark:bg-gray-700"
      >
        <option disabled value="">Nutzer auswählen</option>
        <option v-for="username in availableShareUsers" :key="username" :value="username">
          {{ username }}
        </option>
      </select>
      <p v-if="availableShareUsers.length === 0" class="text-sm text-gray-500 dark:text-gray-400 mt-2">
        Keine weiteren Nutzer verfügbar.
      </p>

      <div class="flex justify-end gap-3 mt-4">
        <BaseButton variant="secondary" @click="showSharePopup = false">
          Schließen
        </BaseButton>
        <BaseButton :disabled="!selectedShareUsername" @click="addShare">
          Hinzufügen
        </BaseButton>
      </div>
    </div>
  </div>

  <ConfirmDialog
    v-model="showDeleteCategoryConfirm"
    title="Kategorie löschen"
    message="Die Kategorie und alle enthaltenen Todos werden unwiderruflich gelöscht."
    @confirm="confirmDeleteCategory"
    @cancel="showDeleteCategoryConfirm = false"
  />

  <ConfirmDialog
    v-model="showDeleteTodoConfirm"
    title="Todo löschen"
    message="Dieses Todo wird unwiderruflich gelöscht."
    @confirm="confirmDeleteTodo"
    @cancel="showDeleteTodoConfirm = false"
  />
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useTodoListStore } from '@/store/todoListStore';
import { useCategoryStore } from '@/store/categoryStore'
import {useTodoStore} from "@/store/todoStore.js";
import { useAuthStore } from '@/store/authStore';
import CategoryForm from "@/components/CategoryForm.vue";
import BaseButton from '@/components/BaseButton.vue'
import IconButton from '@/components/IconButton.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import { FilePlus, SquarePen, Check, Share2, X } from 'lucide-vue-next';

const todoListStore = useTodoListStore();
const categoryStore = useCategoryStore();
const todoStore = useTodoStore();
const authStore = useAuthStore();

const route = useRoute();
const listId = route.params.id;

const list = ref(null);
const categories = ref(null);
const newCategoryName = ref('');
const loading = ref(true);

const editMode = ref(false);

const showCreateListFromTemplatePopup = ref(false);
const newListName = ref('');
const selectedTemplateId = ref('');

const showDeleteCategoryConfirm = ref(false);
const pendingCategoryId = ref(null);
const showDeleteTodoConfirm = ref(false);
const pendingTodo = ref(null);

const showSharePopup = ref(false);
const shareableUsers = ref([]);
const selectedShareUsername = ref('');

const isOwner = computed(() => list.value && list.value.ownerUsername === authStore.currentUsername);
const availableShareUsers = computed(() =>
  shareableUsers.value.filter(username => !list.value?.sharedWith?.includes(username))
);

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

const askDeleteCategory = (categoryId) => {
  pendingCategoryId.value = categoryId;
  showDeleteCategoryConfirm.value = true;
}

const confirmDeleteCategory = async() => {
  if (pendingCategoryId.value == null) return;
  await categoryStore.deleteCategory(pendingCategoryId.value);
  showDeleteCategoryConfirm.value = false;
  pendingCategoryId.value = null;
  loadCategories();
}

const toggleTodoStatus = async(todo) => {
  await todoStore.updateTodo(todo, {...todo, done: !todo.done});
  loadCategories();
}

const askDeleteTodo = (todo) => {
  pendingTodo.value = todo;
  showDeleteTodoConfirm.value = true;
}

const confirmDeleteTodo = async() => {
  if (!pendingTodo.value) return;
  await todoStore.deleteTodo(pendingTodo.value.id);
  showDeleteTodoConfirm.value = false;
  pendingTodo.value = null;
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

function openTemplatePopup(templateId) {
  selectedTemplateId.value = templateId;
  showCreateListFromTemplatePopup.value = true;
}

const openSharePopup = async () => {
  selectedShareUsername.value = '';
  shareableUsers.value = await todoListStore.fetchShareableUsers();
  showSharePopup.value = true;
}

const addShare = async () => {
  if (!selectedShareUsername.value) return;
  list.value = await todoListStore.shareList(list.value.id, selectedShareUsername.value);
  selectedShareUsername.value = '';
}

const removeShare = async (username) => {
  list.value = await todoListStore.unshareList(list.value.id, username);
}

onMounted(async () => {
  loading.value = true;
  await Promise.all([loadList(), loadCategories()]);
  loading.value = false;
})
</script>
