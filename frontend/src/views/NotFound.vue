<template>
  <div class="not-found-container">
    <div class="not-found-content">
      <h1 class="not-found-code">404</h1>
      <h2 class="not-found-title">页面未找到</h2>
      <p class="not-found-message">很抱歉，您访问的页面不存在或已被移除</p>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/dashboard')
}
</script>

<style scoped>
.not-found-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f7fa;
}

.not-found-content {
  text-align: center;
  padding: 40px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.not-found-code {
  font-size: 80px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 20px;
}

.not-found-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

.not-found-message {
  font-size: 14px;
  color: #606266;
  margin-bottom: 30px;
}
</style>