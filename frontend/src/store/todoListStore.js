import { defineStore } from 'pinia'
import api from '@/api.js'

export const useTodoListStore = defineStore('todoList', {
    state: () => ({
        todoLists: [],
        selectedList: null
    }),
    actions: {
        async fetchTodoLists() {
            const response = await api.get('/lists')
            this.todoLists = response.data
        },
        async fetchTemplates() {
            const response = await api.get('/lists/templates')
            this.todoLists = response.data
        },
        async createListFormTemplate(templateId, newListName) {
            const response = await api.post(`/lists/from-template/${templateId}?newListName=${newListName}`)
        },
        async deleteTodoList(id) {
            await api.delete(`/lists/${id}`)
            this.todoLists = this.todoLists.filter(l => l.id !== id)
        },
        async createTodoList(name, template) {
            const response = await api.post('/lists', { 'name': name, 'template': template })
            this.todoLists.push(response.data)
        },
        async importTodoList(file, name, template) {
            const formData = new FormData()
            formData.append('file', file)
            formData.append('name', name)
            formData.append('template', template)
            const response = await api.post('/lists/import', formData)
            this.todoLists.push(response.data)
            return response.data
        },
        async findListById(id) {
            const listById = this.todoLists.find(l => l.id === Number(id))
            if (listById) {
                this.selectedList = listById
                return listById
            }

            // Wenn nicht im Store, dann vom Backend holen
            const res = await api.get(`/lists/${id}`)
            this.selectedList = res.data
            return res.data
        },
        async addCategoryToTodoList(listId, categoryId) {
            await api.put(`/lists/${listId}/categories`, { id: categoryId })
        },
        async updateTodoList(todoList, data) {
            const response = await api.patch(`/lists/${todoList.id}`, data)
            return response.data
        },
        async shareList(listId, username) {
            const response = await api.post(`/lists/${listId}/share`, { username })
            this.selectedList = response.data
            return response.data
        },
        async unshareList(listId, username) {
            const response = await api.delete(`/lists/${listId}/share/${username}`)
            this.selectedList = response.data
            return response.data
        },
        async fetchShareableUsers() {
            const response = await api.get('/users')
            return response.data
        }
    }
})
