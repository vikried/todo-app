import { openDB } from 'idb'

const DB_NAME = 'todoapp-offline'
const DB_VERSION = 1

const dbPromise = openDB(DB_NAME, DB_VERSION, {
  upgrade(db) {
    db.createObjectStore('lists', { keyPath: 'id' })
    db.createObjectStore('categoriesByList')
    db.createObjectStore('meta')
  }
})

// Speichert Listen inkl. der darin verschachtelten Kategorien (GET /lists
// liefert dieselben categories[]/todos[] wie GET /categories/list/{id}),
// damit auch Listen, die nie einzeln geöffnet wurden, offline vollständig
// verfügbar sind.
export async function putLists(lists) {
  const db = await dbPromise
  const tx = db.transaction(['lists', 'categoriesByList', 'meta'], 'readwrite')
  for (const list of lists) {
    tx.objectStore('lists').put(list)
    if (Array.isArray(list.categories)) {
      tx.objectStore('categoriesByList').put(list.categories, list.id)
    }
  }
  tx.objectStore('meta').put(new Date().toISOString(), 'lastSyncedAt')
  await tx.done
}

export async function getAllLists() {
  const db = await dbPromise
  return db.getAll('lists')
}

export async function getList(id) {
  const db = await dbPromise
  return db.get('lists', Number(id))
}

export async function putCategoriesForList(listId, categories) {
  const db = await dbPromise
  const tx = db.transaction(['categoriesByList', 'meta'], 'readwrite')
  tx.objectStore('categoriesByList').put(categories, Number(listId))
  tx.objectStore('meta').put(new Date().toISOString(), 'lastSyncedAt')
  await tx.done
}

export async function getCategoriesForList(listId) {
  const db = await dbPromise
  return db.get('categoriesByList', Number(listId))
}

export async function getLastSyncedAt() {
  const db = await dbPromise
  return db.get('meta', 'lastSyncedAt')
}
