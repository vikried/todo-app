import { defineStore } from 'pinia'
import axios from 'axios'

export const useTodoStore = defineStore('todo', {
  state: () => ({
    todos: []
  }),
  actions: {
    async fetchTodos() {
      const response = await axios.get('http://localhost:8080/api/tasks')
      this.todos = response.data
    },
    async addTodo(title) {
      const response = await axios.post('http://localhost:8080/api/tasks', { title })
      this.todos.push(response.data)
    },
    async deleteTodo(id) {
      await axios.delete(`http://localhost:8080/api/tasks/${id}`)
      this.todos = this.todos.filter(t => t.id !== id)
    },
    async toggleTodo(id) {
      const response = await axios.put(`http://localhost:8080/api/tasks/${id}/toggle`)
    }
  }
})