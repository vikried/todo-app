import { ref, onMounted, onUnmounted } from 'vue'

const isOnline = ref(navigator.onLine)

const setOnline = () => { isOnline.value = true }
const setOffline = () => { isOnline.value = false }

let listenerCount = 0

export function useOnlineStatus() {
  onMounted(() => {
    if (listenerCount === 0) {
      window.addEventListener('online', setOnline)
      window.addEventListener('offline', setOffline)
    }
    listenerCount++
  })

  onUnmounted(() => {
    listenerCount--
    if (listenerCount === 0) {
      window.removeEventListener('online', setOnline)
      window.removeEventListener('offline', setOffline)
    }
  })

  return { isOnline }
}
