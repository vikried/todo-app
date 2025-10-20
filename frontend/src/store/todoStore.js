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
    async addTodo(title) {
      const response = await axios.post('http://localhost:8080/api/todos', { title })
      this.todos.push(response.data)
    },
    async deleteTodo(id) {
      await axios.delete(`http://localhost:8080/api/todos/${id}`)
      this.todos = this.todos.filter(t => t.id !== id)
    },
    async toggleTodo(todo) {
      const response = await axios.patch(`http://localhost:8080/api/todos/${todo.id}`, { ...todo, done: !todo.done })
    }
  }
})