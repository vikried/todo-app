import { defineStore } from 'pinia'
import api, { isNetworkError } from '@/api.js'
import * as offlineDb from '@/offline/db.js'

export const useTodoListStore = defineStore('todoList', {
    state: () => ({
        todoLists: [],
        selectedList: null
    }),
    actions: {
        async fetchTodoLists() {
            try {
                const response = await api.get('/lists')
                this.todoLists = response.data
                offlineDb.putLists(response.data)
            } catch (error) {
                if (!isNetworkError(error)) throw error
                this.todoLists = (await offlineDb.getAllLists()).filter(l => !l.template)
            }
        },
        async fetchTemplates() {
            try {
                const response = await api.get('/lists/templates')
                this.todoLists = response.data
                offlineDb.putLists(response.data)
            } catch (error) {
                if (!isNetworkError(error)) throw error
                this.todoLists = (await offlineDb.getAllLists()).filter(l => l.template)
            }
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
        async saveListAsTemplate(listId, name) {
            const response = await api.post(`/lists/${listId}/save-as-template?name=${encodeURIComponent(name)}`)
            this.todoLists.push(response.data)
            return response.data
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
            try {
                const res = await api.get(`/lists/${id}`)
                this.selectedList = res.data
                offlineDb.putLists([res.data])
                return res.data
            } catch (error) {
                if (!isNetworkError(error)) throw error
                const cached = await offlineDb.getList(id)
                if (!cached) throw error
                this.selectedList = cached
                return cached
            }
        },
        async addCategoryToTodoList(listId, category) {
            try {
                await api.put(`/lists/${listId}/categories`, { id: category.id })
            } catch (error) {
                if (!isNetworkError(error)) throw error
                await offlineDb.enqueueOutboxEntry({
                    op: 'linkCategoryToList',
                    targetId: listId,
                    payload: { id: category.id },
                    listId
                })
                await offlineDb.mutateCategoriesForList(listId, categories => [...categories, category])
            }
        },
        async updateTodoList(todoList, data) {
            try {
                const response = await api.patch(`/lists/${todoList.id}`, data)
                return response.data
            } catch (error) {
                if (!isNetworkError(error)) throw error
                await offlineDb.enqueueOutboxEntry({ op: 'updateList', targetId: todoList.id, payload: data, listId: todoList.id })
                const cached = await offlineDb.getList(todoList.id)
                const updated = { ...(cached || todoList), ...data }
                await offlineDb.putLists([updated])
                return updated
            }
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
