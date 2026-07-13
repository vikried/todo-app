import { defineStore } from 'pinia'
import api from '@/api.js'

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
          const response = await api.get(`/categories/list/${listId}`)
          this.categories = response.data
          return this.categories
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
