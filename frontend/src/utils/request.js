import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'
import { useUserStore } from '../store'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    // 添加token
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    // 检查响应状态码
    if (res.code !== 200) {
      // 401: 未授权，需要重新登录
      if (res.code === 401) {
        ElMessage.error('登录已过期，请重新登录')
        const userStore = useUserStore()
        userStore.clearUserInfo()
        router.push('/login')
      } else {
        ElMessage.error(res.message || '请求失败')
      }
      return Promise.reject(new Error(res.message || '请求失败'))
    } else {
      return res
    }
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          const userStore = useUserStore()
          userStore.clearUserInfo()
          router.push('/login')
          break
        case 403:
          ElMessage.error('权限不足，请联系管理员')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
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

// 导出常用的请求方法
export const get = (url, params = {}) => {
  return request.get(url, { params })
}

export const post = (url, data = {}) => {
  return request.post(url, data)
}

export const put = (url, data = {}) => {
  return request.put(url, data)
}

export const del = (url) => {
  return request.delete(url)
}

export const patch = (url, data = {}) => {
  return request.patch(url, data)
}

export default request
