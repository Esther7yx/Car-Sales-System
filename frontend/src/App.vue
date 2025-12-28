<template>
  <div id="app">
    <MainLayout v-if="isAuthenticated" />
    <router-view v-else />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from './store'
import MainLayout from './layouts/MainLayout.vue'

const userStore = useUserStore()

// 检查用户是否已认证
const isAuthenticated = computed(() => {
  return userStore.token && userStore.token !== ''
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  background-color: #f5f7fa;
}

#app {
  width: 100%;
  height: 100vh;
}
</style>
