import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { ElMessage } from 'element-plus'
import axios from 'axios'

console.log('🚗 Vue应用开始初始化...')

// 确保DOM加载完成后再挂载应用
function initVueApp() {
    try {
        const app = createApp(App)
        console.log('✅ Vue应用创建成功')

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
        console.log('🚗 开始挂载Vue应用到DOM...')
        
        const container = document.getElementById('app')
        if (container) {
            console.log('✅ 找到挂载容器: #app')
            
            try {
                app.mount('#app')
                console.log('✅ Vue应用挂载成功')
                
                // 设置全局标志，方便调试页面检测
                window.__VUE_APP_MOUNTED__ = true
                window.__VUE_APP_INSTANCE__ = app
                console.log('✅ Vue应用全局标志已设置')
                
            } catch (mountError) {
                console.error('❌ Vue应用挂载失败:', mountError)
                showError('Vue应用挂载失败: ' + mountError.message)
            }
        } else {
            console.error('❌ 找不到挂载容器: #app')
            showError('找不到挂载容器: #app')
        }

        function showError(message) {
            // 显示错误信息给用户
            const errorDiv = document.createElement('div')
            errorDiv.style.cssText = 'position: fixed; top: 0; left: 0; width: 100%; background: #ff4444; color: white; padding: 10px; text-align: center; z-index: 9999;'
            errorDiv.innerHTML = `Vue应用初始化失败: ${message} - 请检查控制台获取详细信息`
            document.body.appendChild(errorDiv)
        }

    } catch (error) {
        console.error('❌ Vue应用初始化失败:', error)
        
        // 显示错误信息给用户
        const errorDiv = document.createElement('div')
        errorDiv.style.cssText = 'position: fixed; top: 0; left: 0; width: 100%; background: #ff4444; color: white; padding: 10px; text-align: center; z-index: 9999;'
        errorDiv.innerHTML = `Vue应用初始化失败: ${error.message} - 请检查控制台获取详细信息`
        document.body.appendChild(errorDiv)
    }
}

// 等待DOM加载完成
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initVueApp)
} else {
    initVueApp()
}
