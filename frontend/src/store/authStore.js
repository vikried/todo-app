import { defineStore } from 'pinia'
import api from '@/api.js'

const TOKEN_KEY = 'token'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || null
  }),
  getters: {
    isAuthenticated: (state) => !!state.token
  },
  actions: {
    async login(username, password) {
      const response = await api.post('/auth/login', { username, password })
      this.token = response.data.token
      localStorage.setItem(TOKEN_KEY, this.token)
    },
    async register(username, password) {
      await api.post('/auth/register', { username, password })
    },
    async logout() {
      try {
        if (this.token) {
          await api.post('/auth/logout')
        }
      } finally {
        this.token = null
        localStorage.removeItem(TOKEN_KEY)
      }
    }
  }
})
