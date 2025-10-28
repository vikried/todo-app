import { defineStore } from 'pinia'
import axios from 'axios'

export const useTodoStore = defineStore('todo', {
  state: () => ({
    todos: []
  }),
  actions: {
    async fetchTodos() {
      const response = await axios.get('http://localhost:8080/api/todos')
      this.todos = response.data
    },
    async addTodo(data) {
      const response = await axios.post('http://localhost:8080/api/todos', data)
      this.todos.push(response.data)
      return response.data
    },
    async deleteTodo(id) {
      await axios.delete(`http://localhost:8080/api/todos/${id}`)
      this.todos = this.todos.filter(t => t.id !== id)
    },
    async updateTodo(todo, data) {
      const response = await axios.patch(`http://localhost:8080/api/todos/${todo.id}`, data)
    },
    async findTodoById(todoId) {
      const response = await axios.get(`http://localhost:8080/api/todos/${todoId}`)
      return response.data
    }
  }
})