import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const app = createApp(App)

// 配置Axios全局实例
axios.defaults.baseURL = ''
axios.defaults.timeout = 10000

// 请求拦截器
axios.interceptors.request.use(
  config => {
    // 添加token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
axios.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    // 统一处理错误
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 未授权，跳转到登录页
          router.push('/login')
          break
        case 403:
          // 拒绝访问
          ElMessage.error('权限不足，请联系管理员')
          break
        case 404:
          // 资源不存在
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          // 服务器错误
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(error.response.data.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

// 全局挂载
app.config.globalProperties.$axios = axios
app.config.globalProperties.$message = ElMessage

// 安装插件
app.use(router)
app.use(store)
app.use(ElementPlus)

// 挂载应用
app.mount('#app')
