import {defineStore} from 'pinia'
import axios from 'axios'

export const useCategoryStore = defineStore('category', {
  state: () => ({
     categories: []
  }),
  actions: {
      async fetchCategories() {
          const response = await axios.get('http://localhost:8080/api/categories')
          this.categories = response.data
      },
      async findCategoriesByList(listId) {
          const response = await axios.get(`http://localhost:8080/api/categories/list/${listId}`)
          this.categories = response.data
          return this.categories
      },
      async addCategory(name) {
          const response = await axios.post('http://localhost:8080/api/categories', { name: name })
          this.categories.push(response.data)
          return response.data
      },
      async deleteCategory(categoryId) {
          await axios.delete(`http://localhost:8080/api/categories/${categoryId}`)
          this.categories = this.categories.filter(c => c.id !== categoryId)
      },
      async addTodoToCategory(categoryId, todoId) {
          await axios.put(`http://localhost:8080/api/categories/${categoryId}/todos`, {id: todoId})
      }
  }
})