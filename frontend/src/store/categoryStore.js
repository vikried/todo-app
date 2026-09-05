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
          const response = await api.post('/categories', { name: name })
          this.categories.push(response.data)
          return response.data
      },
      async deleteCategory(categoryId) {
          await api.delete(`/categories/${categoryId}`)
          this.categories = this.categories.filter(c => c.id !== categoryId)
      },
      async addTodoToCategory(categoryId, todoId) {
          await api.put(`/categories/${categoryId}/todos`, { id: todoId })
      },
      async updateCategory(categoryId, data) {
          const response = await api.patch(`/categories/${categoryId}`, data)
          const idx = this.categories.findIndex(c => c.id === categoryId)
          if (idx !== -1) this.categories[idx] = response.data
          return response.data
      }
  }
})
