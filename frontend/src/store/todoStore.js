import { defineStore } from 'pinia'
import api, { isNetworkError } from '@/api.js'
import * as offlineDb from '@/offline/db.js'

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
      try {
        const response = await api.post('/todos', data)
        this.todos.push(response.data)
        return response.data
      } catch (error) {
        if (!isNetworkError(error)) throw error
        const optimisticTodo = {
          id: `temp-${crypto.randomUUID()}`,
          title: data.title,
          done: data.done ?? false,
          todoListId: data.todoListId,
          categoryId: data.categoryId ?? null
        }
        await offlineDb.enqueueOutboxEntry({
          op: 'createTodo',
          tempId: optimisticTodo.id,
          targetId: optimisticTodo.id,
          payload: data,
          listId: data.todoListId
        })
        return optimisticTodo
      }
    },
    async deleteTodo(id, listId) {
      try {
        await api.delete(`/todos/${id}`)
        this.todos = this.todos.filter(t => t.id !== id)
      } catch (error) {
        if (!isNetworkError(error)) throw error
        await offlineDb.enqueueOutboxEntry({ op: 'deleteTodo', targetId: id, payload: null, listId })
        if (listId) {
          await offlineDb.mutateCategoriesForList(listId, categories =>
            categories.map(c => ({ ...c, todos: (c.todos || []).filter(t => t.id !== id) }))
          )
        }
      }
    },
    async updateTodo(todo, data, listId) {
      try {
        const response = await api.patch(`/todos/${todo.id}`, data)
        return response.data
      } catch (error) {
        if (!isNetworkError(error)) throw error
        await offlineDb.enqueueOutboxEntry({ op: 'updateTodo', targetId: todo.id, payload: data, listId })
        if (listId) {
          await offlineDb.mutateCategoriesForList(listId, categories => {
            const oldCategory = categories.find(c => (c.todos || []).some(t => t.id === todo.id))
            const updatedTodo = { ...todo, ...data }
            if (oldCategory && data.categoryId != null && data.categoryId !== oldCategory.id) {
              oldCategory.todos = oldCategory.todos.filter(t => t.id !== todo.id)
              const newCategory = categories.find(c => c.id === data.categoryId)
              if (newCategory) newCategory.todos = [...(newCategory.todos || []), updatedTodo]
            } else if (oldCategory) {
              oldCategory.todos = oldCategory.todos.map(t => (t.id === todo.id ? updatedTodo : t))
            }
            return categories
          })
        }
        return { ...todo, ...data }
      }
    },
    async findTodoById(todoId) {
      const response = await api.get(`/todos/${todoId}`)
      return response.data
    }
  }
})
