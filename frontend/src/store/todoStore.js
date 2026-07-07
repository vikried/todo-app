import { defineStore } from 'pinia'
import api from '@/api.js'

export const useTodoStore = defineStore('todo', {
  state: () => ({
    todos: []
  }),
  actions: {
    async fetchTodos() {
      const response = await api.get('/todos')
      this.todos = response.data
    },
    async addTodo(data) {
      const response = await api.post('/todos', data)
      this.todos.push(response.data)
      return response.data
    },
    async deleteTodo(id) {
      await api.delete(`/todos/${id}`)
      this.todos = this.todos.filter(t => t.id !== id)
    },
    async updateTodo(todo, data) {
      const response = await api.patch(`/todos/${todo.id}`, data)
    },
    async findTodoById(todoId) {
      const response = await api.get(`/todos/${todoId}`)
      return response.data
    }
  }
})
