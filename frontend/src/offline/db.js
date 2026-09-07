import { openDB } from 'idb'

const DB_NAME = 'todoapp-offline'
const DB_VERSION = 2

const dbPromise = openDB(DB_NAME, DB_VERSION, {
  upgrade(db, oldVersion) {
    if (oldVersion < 1) {
      db.createObjectStore('lists', { keyPath: 'id' })
      db.createObjectStore('categoriesByList')
      db.createObjectStore('meta')
    }
    if (oldVersion < 2) {
      db.createObjectStore('outbox', { keyPath: 'seq', autoIncrement: true })
    }
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

// Liest+schreibt die Kategorien einer Liste im Cache und spiegelt das
// Ergebnis zusätzlich in lists[listId].categories, damit beide Cache-
// Stellen (Listenübersicht und Listendetail) konsistent bleiben. mutatorFn
// bekommt das aktuelle Kategorien-Array und gibt das neue zurück (oder
// mutiert es in-place und gibt nichts zurück).
export async function mutateCategoriesForList(listId, mutatorFn) {
  const db = await dbPromise
  const tx = db.transaction(['categoriesByList', 'lists', 'meta'], 'readwrite')
  const key = Number(listId)
  let categories = (await tx.objectStore('categoriesByList').get(key)) || []
  categories = mutatorFn(categories) || categories
  tx.objectStore('categoriesByList').put(categories, key)
  const list = await tx.objectStore('lists').get(key)
  if (list) {
    list.categories = categories
    tx.objectStore('lists').put(list)
  }
  tx.objectStore('meta').put(new Date().toISOString(), 'lastSyncedAt')
  await tx.done
  return categories
}

// Outbox: Warteschlange für Änderungen, die offline vorgenommen wurden und
// bei Wiederverbindung nachgeholt werden müssen. autoIncrement auf "seq"
// liefert kostenlos eine stabile FIFO-Reihenfolge zum Abspielen.
export async function enqueueOutboxEntry(entry) {
  const db = await dbPromise
  return db.add('outbox', { ...entry, createdAt: new Date().toISOString() })
}

export async function getOutboxEntries() {
  const db = await dbPromise
  return db.getAll('outbox')
}

export async function deleteOutboxEntry(seq) {
  const db = await dbPromise
  return db.delete('outbox', seq)
}

export async function getOutboxCount() {
  const db = await dbPromise
  return db.count('outbox')
}
