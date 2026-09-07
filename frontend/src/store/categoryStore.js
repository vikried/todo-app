import { defineStore } from 'pinia'
import api, { isNetworkError } from '@/api.js'
import * as offlineDb from '@/offline/db.js'

export const useCategoryStore = defineStore('category', {
  state: () => ({
     categories: []
  }),
  actions: {
      async fetchCategories() {
          const response = await api.get('/categories')
          this.categories = response.data
      },
      async findCategoriesByList(listId) {
          try {
              const response = await api.get(`/categories/list/${listId}`)
              this.categories = response.data
              offlineDb.putCategoriesForList(listId, response.data)
              return this.categories
          } catch (error) {
              if (!isNetworkError(error)) throw error
              // Liste evtl. nie einzeln geöffnet (nur über die Übersicht
              // gesehen) - dann als Fallback aus den in GET /lists bereits
              // mitgelieferten, verschachtelten Kategorien der Liste lesen.
              let cached = await offlineDb.getCategoriesForList(listId)
              if (!cached) {
                  const cachedList = await offlineDb.getList(listId)
                  cached = cachedList?.categories
              }
              if (!cached) throw error
              this.categories = cached
              return this.categories
          }
      },
      async addCategory(name) {
          try {
              const response = await api.post('/categories', { name: name })
              this.categories.push(response.data)
              return response.data
          } catch (error) {
              if (!isNetworkError(error)) throw error
              const optimisticCategory = { id: `temp-${crypto.randomUUID()}`, name, todos: [] }
              await offlineDb.enqueueOutboxEntry({
                  op: 'createCategory',
                  tempId: optimisticCategory.id,
                  targetId: optimisticCategory.id,
                  payload: { name },
                  listId: null
              })
              return optimisticCategory
          }
      },
      async deleteCategory(categoryId, listId) {
          try {
              await api.delete(`/categories/${categoryId}`)
              this.categories = this.categories.filter(c => c.id !== categoryId)
          } catch (error) {
              if (!isNetworkError(error)) throw error
              await offlineDb.enqueueOutboxEntry({ op: 'deleteCategory', targetId: categoryId, payload: null, listId })
              if (listId) {
                  await offlineDb.mutateCategoriesForList(listId, categories =>
                      categories.filter(c => c.id !== categoryId)
                  )
              }
          }
      },
      async addTodoToCategory(categoryId, todo, listId) {
          try {
              await api.put(`/categories/${categoryId}/todos`, { id: todo.id })
          } catch (error) {
              if (!isNetworkError(error)) throw error
              await offlineDb.enqueueOutboxEntry({
                  op: 'linkTodoToCategory',
                  targetId: categoryId,
                  payload: { id: todo.id },
                  listId
              })
              if (listId) {
                  await offlineDb.mutateCategoriesForList(listId, categories => {
                      const category = categories.find(c => c.id === categoryId)
                      if (category) category.todos = [...(category.todos || []), { ...todo, categoryId }]
                      return categories
                  })
              }
          }
      },
      async updateCategory(categoryId, data, listId) {
          try {
              const response = await api.patch(`/categories/${categoryId}`, data)
              const idx = this.categories.findIndex(c => c.id === categoryId)
              if (idx !== -1) this.categories[idx] = response.data
              return response.data
          } catch (error) {
              if (!isNetworkError(error)) throw error
              await offlineDb.enqueueOutboxEntry({ op: 'updateCategory', targetId: categoryId, payload: data, listId })
              if (listId) {
                  await offlineDb.mutateCategoriesForList(listId, categories => {
                      const category = categories.find(c => c.id === categoryId)
                      if (category) Object.assign(category, data)
                      return categories
                  })
              }
              return { id: categoryId, ...data }
          }
      }
  }
})
