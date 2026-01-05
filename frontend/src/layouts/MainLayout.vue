<template>
  <div class="main-layout">
    <div class="sidebar" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
      <div class="sidebar-header">
        <h2 v-if="!sidebarCollapsed" class="logo-title">🚗 汽车销售系统</h2>
        <h2 v-else class="logo-title">🚗</h2>
      </div>

      <el-menu
          :default-active="activeMenu"
          :collapse="sidebarCollapsed"
          :collapse-transition="false"
          background-color="#001529"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          router
          class="sidebar-menu"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>系统概览</template>
        </el-menu-item>

        <el-sub-menu index="purchase">
          <template #title>
            <el-icon><ShoppingCart /></el-icon>
            <span>进货管理</span>
          </template>
          <el-menu-item index="/purchase">采购单列表</el-menu-item>
          <el-menu-item index="/purchase/add">新建采购单</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="manufacturer">
          <template #title>
            <el-icon><OfficeBuilding /></el-icon>
            <span>厂商管理</span>
          </template>
          <el-menu-item index="/manufacturers">厂商列表</el-menu-item>
          <el-menu-item index="/manufacturers/add">添加厂商</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="customer">
          <template #title>
            <el-icon><User /></el-icon>
            <span>客户管理</span>
          </template>
          <el-menu-item index="/customers">客户列表</el-menu-item>
          <el-menu-item index="/customers/add">添加客户</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="car-model">
          <template #title>
            <el-icon><SetUp /></el-icon>
            <span>车型管理</span>
          </template>
          <el-menu-item index="/car-models">车型列表</el-menu-item>
          <el-menu-item index="/car-models/add">添加车型</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="sale">
          <template #title>
            <el-icon><Money /></el-icon>
            <span>销售管理</span>
          </template>
          <el-menu-item index="/sales">销售订单</el-menu-item>
          <el-menu-item index="/sales/create">新建销售单</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="warehouse">
          <template #title>
            <el-icon><House /></el-icon>
            <span>仓库管理</span>
          </template>
          <el-menu-item index="/warehouse/inventory">库存车辆</el-menu-item>
          <el-menu-item index="/warehouse/details">仓库明细</el-menu-item>
          <el-menu-item index="/warehouse/stats">进销存统计</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </div>

    <div class="main-content">
      <div class="header">
        <div class="header-left">
          <el-icon
              class="trigger-btn"
              @click="toggleSidebar"
          >
            <Expand v-if="sidebarCollapsed" />
            <Fold v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ pageTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-dropdown trigger="click">
            <span class="user-info">
              <el-avatar :size="32" :src="userAvatar" icon="UserFilled" />
              <span class="username">{{ userName }}</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <div class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useSystemStore, useUserStore } from '../store'
import {
  Fold,
  Expand,
  Odometer,
  OfficeBuilding,
  SetUp,
  ArrowDown,
  UserFilled,
  User,
  ShoppingCart,
  House,
  Money
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const systemStore = useSystemStore()
const userStore = useUserStore()

const sidebarCollapsed = computed(() => systemStore.sidebarCollapsed)
const activeMenu = computed(() => route.path)

const pageTitle = computed(() => {
  if (route.meta && route.meta.title) {
    return route.meta.title
  }
  // 简单的兜底映射
  const routeName = route.name
  return routeName || '当前页面'
})

const userName = computed(() => userStore.userInfo?.realName || userStore.userInfo?.username || '管理员')
const userAvatar = computed(() => '')

const toggleSidebar = () => {
  systemStore.toggleSidebar()
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.main-layout {
  display: flex;
  height: 100vh;
  width: 100vw;
  overflow: hidden;
}

/* 侧边栏样式 */
.sidebar {
  width: 220px;
  background-color: #001529;
  color: #fff;
  transition: width 0.3s;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.sidebar-collapsed {
  width: 64px;
}

.sidebar-header {
  height: 60px;
  line-height: 60px;
  background-color: #002140;
  text-align: center;
  overflow: hidden;
}

.logo-title {
  margin: 0;
  font-size: 18px;
  color: white;
  white-space: nowrap;
}

.sidebar-menu {
  border-right: none;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}

/* 主内容区域样式 */
.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: #f0f2f5;
}

/* 头部样式 */
.header {
  height: 60px;
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.trigger-btn {
  font-size: 20px;
  cursor: pointer;
  transition: color 0.3s;
}

.trigger-btn:hover {
  color: #409EFF;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px;
  border-radius: 4px;
}

.user-info:hover {
  background-color: #f6f6f6;
}

.username {
  margin-left: 8px;
  margin-right: 8px;
  font-size: 14px;
  color: #333;
}

/* 内容区域 */
.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

/* 路由切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>