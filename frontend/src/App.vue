<template>
  <div class="app-container">
    <h1>Meine TODO-App 📝</h1>
    <router-view />
  </div>
</template>

<script setup>
</script>

<style>
.app-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem;
}
</style>

<!--
<script setup>
import { ref, onMounted } from "vue";
import api from "./api.js";

const tasks = ref([]);
const newTask = ref("");

const loadTasks = async () => {
  const res = await api.get("/tasks");
  tasks.value = res.data;
};

const addTask = async () => {
  if (!newTask.value) return;
  await api.post("/tasks", { title: newTask.value, description: "", done: false });
  newTask.value = "";
  loadTasks();
};

const toggleTask = async (task) => {
  await api.put(`/tasks/${task.id}`, { ...task, done: !task.done });
  loadTasks();
};

const deleteTask = async (id) => {
  await api.delete(`/tasks/${id}`);
  loadTasks();
};

onMounted(loadTasks);
</script>

<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-4">Meine ToDo-Liste</h1>

    <div class="flex mb-4">
      <input v-model="newTask" placeholder="Neue Aufgabe..." class="border p-2 flex-1" />
      <button @click="addTask" class="ml-2 bg-blue-500 text-white px-4 py-2 rounded">
        Hinzufügen
      </button>
    </div>

    <ul>
      <li v-for="task in tasks" :key="task.id" class="flex items-center mb-2">
        <input type="checkbox" :checked="task.done" @change="toggleTask(task)" class="mr-2" />
        <span :class="task.done ? 'line-through text-gray-500' : ''">{{ task.title }}</span>
        <button @click="deleteTask(task.id)" class="ml-auto text-red-600">🗑️</button>
      </li>
    </ul>
  </div>
</template>

<style>
body {
  font-family: system-ui, sans-serif;
}
</style>
-->