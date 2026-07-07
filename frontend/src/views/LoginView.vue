<template>
  <div class="p-6 bg-white dark:bg-gray-900 dark:text-gray-100 rounded-lg shadow-md max-w-sm mx-auto mt-16">
    <h1 class="text-2xl font-bold mb-6">{{ isRegister ? 'Registrieren' : 'Login' }}</h1>

    <form @submit.prevent="submit" class="flex flex-col gap-3">
      <input
        v-model="username"
        placeholder="Benutzername"
        class="border rounded p-2 dark:bg-gray-700 dark:border-gray-600"
        required
      />
      <input
        v-model="password"
        type="password"
        placeholder="Passwort"
        class="border rounded p-2 dark:bg-gray-700 dark:border-gray-600"
        required
      />
      <p v-if="error" class="text-red-600 text-sm">{{ error }}</p>
      <button
        type="submit"
        :disabled="loading"
        class="bg-blue-600 hover:bg-blue-800 disabled:opacity-50 text-white px-4 py-2 rounded"
      >
        {{ isRegister ? 'Registrieren' : 'Anmelden' }}
      </button>
    </form>

    <button
      type="button"
      @click="isRegister = !isRegister; error = ''"
      class="mt-4 text-sm text-blue-600 hover:underline"
    >
      {{ isRegister ? 'Schon registriert? Zum Login' : 'Noch kein Konto? Jetzt registrieren' }}
    </button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/store/authStore'

const router = useRouter()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')
const isRegister = ref(false)
const error = ref('')
const loading = ref(false)

async function submit() {
  error.value = ''
  loading.value = true
  try {
    if (isRegister.value) {
      await authStore.register(username.value, password.value)
    }
    await authStore.login(username.value, password.value)
    router.push('/')
  } catch (e) {
    error.value = e.response?.data?.error || 'Anmeldung fehlgeschlagen. Bitte Zugangsdaten prüfen.'
  } finally {
    loading.value = false
  }
}
</script>
