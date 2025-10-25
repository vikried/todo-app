import {defineStore} from 'pinia'
import axios from 'axios'

export const useTodoListStore = defineStore('todoList', {
    state: () => ({
        todoLists: [],
        selectedList: null
    }),
    actions: {
        async fetchTodoLists() {
            const response = await axios.get('http://localhost:8080/api/lists')
            this.todoLists = response.data
        },
        async deleteTodoList(id) {
            await axios.delete(`http://localhost:8080/api/lists/${id}`)
            this.todoLists = this.todoLists.filter(l => l.id !== id)
        },
        async createTodoList(name, template) {
            const response = await axios.post('http://localhost:8080/api/lists', {'name': name, 'template': template})
            this.todoLists.push(response.data)
        },
        async findListById(id) {
            const listById = this.todoLists.find(l => l.id === Number(id))
            if (listById) {
                this.selectedList = listById
                return listById
            }

            // Wenn nich im Store, dann vom Backend holen
            const res = await axios.get(`http://localhost:8080/api/lists/${id}`)
            this.selectedList = res.data
            return res.data
        },
        async addCategoryToTodoList(listId, categoryId) {
            await axios.put(`http://localhost:8080/api/lists/${listId}/categories`, { id: categoryId})
        },
        async updateTodoList(todoList, data) {
            const response = await axios.patch(`http://localhost:8080/api/lists/${todoList.id}`, data)
          }

    }
})