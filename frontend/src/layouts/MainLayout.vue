<template>
  <div class="main-layout">
    <!-- 侧边栏 -->
    <div class="sidebar" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
      <div class="sidebar-header">
        <h2 v-if="!sidebarCollapsed">汽车销售系统</h2>
        <h2 v-else>汽车</h2>
        <el-button 
          type="text" 
          class="toggle-btn"
          @click="toggleSidebar"
        >
          <el-icon>
            <Fold v-if="!sidebarCollapsed" />
            <Expand v-else />
          </el-icon>
        </el-button>
      </div>
      
      <!-- 导航菜单 -->
      <el-menu
        :default-active="activeMenu"
        :collapse="sidebarCollapsed"
        :collapse-transition="false"
        router
        class="sidebar-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><TrendCharts /></el-icon>
          <span>系统概览</span>
        </el-menu-item>
        
        <el-sub-menu index="manufacturer">
          <template #title>
            <el-icon><OfficeBuilding /></el-icon>
            <span>厂商管理</span>
          </template>
          <el-menu-item index="/manufacturers">厂商列表</el-menu-item>
          <el-menu-item index="/manufacturers/add">添加厂商</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="car-model">
          <template #title>
            <el-icon><Van /></el-icon>
            <span>车型管理</span>
          </template>
          <el-menu-item index="/car-models">车型列表</el-menu-item>
          <el-menu-item index="/car-models/add">添加车型</el-menu-item>
        </el-sub-menu>
        
        <el-sub-menu index="vehicle">
          <template #title>
            <el-icon><Van /></el-icon>
            <span>车辆管理</span>
          </template>
          <el-menu-item index="/vehicles">车辆列表</el-menu-item>
          <el-menu-item index="/vehicles/add">添加车辆</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </div>
    
    <!-- 主内容区域 -->
    <div class="main-content" :class="{ 'content-collapsed': sidebarCollapsed }">
      <!-- 顶部导航栏 -->
      <div class="header">
        <div class="header-left">
          <span class="page-title">{{ pageTitle }}</span>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              <el-avatar :size="32" :src="userAvatar" />
              <span class="username">{{ userName }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
      
      <!-- 页面内容 -->
      <div class="content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useSystemStore } from '../store'
import { useUserStore } from '../store'
import { 
  Fold, 
  Expand, 
  TrendCharts,
  OfficeBuilding,
  Van 
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const systemStore = useSystemStore()
const userStore = useUserStore()

// 侧边栏折叠状态
const sidebarCollapsed = computed(() => systemStore.sidebarCollapsed)

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 页面标题
const pageTitle = computed(() => {
  const routeName = route.name
  const titleMap = {
    'Dashboard': '系统概览',
    'ManufacturerList': '厂商管理',
    'ManufacturerAdd': '添加厂商',
    'ManufacturerEdit': '编辑厂商',
    'CarModelList': '车型管理',
    'CarModelAdd': '添加车型',
    'CarModelEdit': '编辑车型',
    'VehicleList': '车辆管理',
    'VehicleAdd': '添加车辆',
    'VehicleEdit': '编辑车辆'
  }
  return titleMap[routeName] || '汽车销售系统'
})

// 用户信息
const userName = computed(() => userStore.userInfo?.username || '管理员')
const userAvatar = computed(() => userStore.userInfo?.avatar || '')

// 切换侧边栏
const toggleSidebar = () => {
  systemStore.toggleSidebar()
}

// 退出登录
const handleLogout = () => {
  userStore.clearUserInfo()
  router.push('/login')
}

onMounted(() => {
  // 确保用户已登录
  if (!userStore.token) {
    router.push('/login')
  }
})
</script>

<style scoped>
.main-layout {
  display: flex;
  height: 100vh;
  background-color: #f5f7fa;
}

/* 侧边栏样式 */
.sidebar {
  width: 240px;
  background-color: #001529;
  color: #fff;
  transition: width 0.3s ease;
  overflow: hidden;
}

.sidebar-collapsed {
  width: 64px;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-bottom: 1px solid #303030;
}

.sidebar-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #fff;
}

.toggle-btn {
  color: #fff;
  font-size: 18px;
}

.sidebar-menu {
  border: none;
  background-color: transparent;
}

.sidebar-menu :deep(.el-menu-item),
.sidebar-menu :deep(.el-sub-menu__title) {
  color: #bfbfbf;
  background-color: transparent;
}

.sidebar-menu :deep(.el-menu-item:hover),
.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background-color: #1890ff;
  color: #fff;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: #1890ff;
  color: #fff;
}

.sidebar-menu :deep(.el-icon) {
  color: #bfbfbf;
}

.sidebar-menu :deep(.el-menu-item.is-active .el-icon),
.sidebar-menu :deep(.el-menu-item:hover .el-icon),
.sidebar-menu :deep(.el-sub-menu__title:hover .el-icon) {
  color: #fff;
}

/* 主内容区域样式 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  transition: margin-left 0.3s ease;
}

.content-collapsed {
  margin-left: 0;
}

/* 头部样式 */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 64px;
  background-color: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #262626;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.user-info:hover {
  background-color: #f5f5f5;
}

.username {
  margin-left: 8px;
  font-size: 14px;
  color: #262626;
}

/* 内容区域 */
.content {
  flex: 1;
  padding: 24px;
  overflow: auto;
}
</style>