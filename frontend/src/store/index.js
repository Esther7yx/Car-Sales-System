import { createPinia, defineStore } from 'pinia'
import { ref, computed } from 'vue'

const pinia = createPinia()

// 默认导出pinia实例
export default pinia

// 用户状态管理
export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  // 设置用户信息
  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  // 设置token
  const setToken = (t) => {
    token.value = t
    localStorage.setItem('token', t)
  }

  // 清除用户信息
  const clearUserInfo = () => {
    userInfo.value = null
    token.value = ''
    localStorage.removeItem('userInfo')
    localStorage.removeItem('token')
  }

  // 退出登录
  const logout = () => {
    clearUserInfo()
  }

  // 判断是否登录
  const isLoggedIn = computed(() => {
    return !!token.value && !!userInfo.value
  })

  return {
    token,
    userInfo,
    isLoggedIn,
    setUserInfo,
    setToken,
    clearUserInfo,
    logout
  }
})

// 系统状态管理
export const useSystemStore = defineStore('system', () => {
  const loading = ref(false)
  const sidebarCollapsed = ref(false)

  // 设置加载状态
  const setLoading = (status) => {
    loading.value = status
  }

  // 切换侧边栏折叠状态
  const toggleSidebar = () => {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  return {
    loading,
    sidebarCollapsed,
    setLoading,
    toggleSidebar
  }
})
