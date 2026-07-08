import { defineStore } from 'pinia'
import api from '@/api.js'

const TOKEN_KEY = 'token'
const USERNAME_KEY = 'username'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || null,
    username: localStorage.getItem(USERNAME_KEY) || null
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
    currentUsername: (state) => state.username
  },
  actions: {
    async login(username, password) {
      const response = await api.post('/auth/login', { username, password })
      this.token = response.data.token
      this.username = response.data.username
      localStorage.setItem(TOKEN_KEY, this.token)
      localStorage.setItem(USERNAME_KEY, this.username)
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
        this.username = null
        localStorage.removeItem(TOKEN_KEY)
        localStorage.removeItem(USERNAME_KEY)
      }
    }
  }
})
