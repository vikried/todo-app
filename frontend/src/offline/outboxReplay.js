import { ref } from 'vue'
import api, { isNetworkError } from '@/api.js'
import { getOutboxEntries, deleteOutboxEntry } from './db.js'

export const pendingCount = ref(0)
export const conflictCount = ref(0)

getOutboxEntries().then((entries) => { pendingCount.value = entries.length })

const isTempId = (value) => typeof value === 'string' && value.startsWith('temp-')
const resolve = (value, idMap) => (isTempId(value) ? (idMap[value] ?? value) : value)

function resolveTempIds(entry, idMap) {
  entry.targetId = resolve(entry.targetId, idMap)
  if (entry.payload && 'id' in entry.payload) {
    entry.payload.id = resolve(entry.payload.id, idMap)
  }
  if (entry.payload && 'categoryId' in entry.payload) {
    entry.payload.categoryId = resolve(entry.payload.categoryId, idMap)
  }
}

// Roher api-Call pro Operation - bewusst NICHT über die Store-Actions, die
// bei einem Netzwerkfehler selbst wieder in die Outbox schreiben würden.
// Ein Fehlschlag hier muss beim Replay-Loop selbst ankommen (siehe unten).
function applyEntry(entry) {
  switch (entry.op) {
    case 'createTodo':
      return api.post('/todos', entry.payload)
    case 'updateTodo':
      return api.patch(`/todos/${entry.targetId}`, entry.payload)
    case 'deleteTodo':
      return api.delete(`/todos/${entry.targetId}`)
    case 'createCategory':
      return api.post('/categories', entry.payload)
    case 'updateCategory':
      return api.patch(`/categories/${entry.targetId}`, entry.payload)
    case 'deleteCategory':
      return api.delete(`/categories/${entry.targetId}`)
    case 'linkTodoToCategory':
      return api.put(`/categories/${entry.targetId}/todos`, entry.payload)
    case 'linkCategoryToList':
      return api.put(`/lists/${entry.targetId}/categories`, entry.payload)
    case 'updateList':
      return api.patch(`/lists/${entry.targetId}`, entry.payload)
    default:
      return Promise.reject(new Error(`Unbekannte Outbox-Operation: ${entry.op}`))
  }
}

let replaying = false

export async function triggerReplay() {
  if (replaying || !navigator.onLine) return
  const entries = await getOutboxEntries()
  if (entries.length === 0) return
  replaying = true
  const idMap = {}
  try {
    for (const entry of entries) {
      resolveTempIds(entry, idMap)
      try {
        const result = await applyEntry(entry)
        if (entry.tempId) idMap[entry.tempId] = result.data.id
        await deleteOutboxEntry(entry.seq)
        pendingCount.value--
      } catch (err) {
        if (isNetworkError(err)) break // wieder offline: Rest bleibt stehen, nächster Trigger macht weiter
        console.warn('[offline-sync] Outbox-Eintrag verworfen (Ziel evtl. anderswo geändert/gelöscht):', entry, err)
        conflictCount.value++
        await deleteOutboxEntry(entry.seq)
        pendingCount.value--
      }
    }
  } finally {
    replaying = false
  }
  if (pendingCount.value === 0) {
    const { useTodoListStore } = await import('@/store/todoListStore.js')
    useTodoListStore().fetchTodoLists()
  }
}
