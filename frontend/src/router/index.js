import { createRouter, createWebHistory } from 'vue-router'
// 引入布局组件
import MainLayout from '../layouts/MainLayout.vue'

// 定义路由
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: MainLayout, // 使用 MainLayout 作为父级路由
    redirect: '/dashboard',
    // 所有需要显示在 Layout 内部的页面都放在 children 里
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '系统概览', requiresAuth: true }
      },
      // --- 厂商管理模块 ---
      {
        path: 'manufacturers',
        name: 'ManufacturerList',
        component: () => import('../views/manufacturer/ManufacturerList.vue'),
        meta: { title: '厂商管理', requiresAuth: true }
      },
      {
        path: 'manufacturers/add',
        name: 'ManufacturerAdd',
        component: () => import('../views/manufacturer/ManufacturerForm.vue'),
        meta: { title: '添加厂商', requiresAuth: true }
      },
      {
        path: 'manufacturers/edit/:id',
        name: 'ManufacturerEdit',
        component: () => import('../views/manufacturer/ManufacturerForm.vue'),
        meta: { title: '编辑厂商', requiresAuth: true }
      },
      // --- 客户管理模块 ---
      {
        path: 'customers',
        name: 'CustomerList',
        component: () => import('../views/customer/CustomerList.vue'),
        meta: { title: '客户管理', requiresAuth: true }
      },
      {
        path: 'customers/add',
        name: 'CustomerAdd',
        component: () => import('../views/customer/CustomerForm.vue'),
        meta: { title: '添加客户', requiresAuth: true }
      },
      {
        path: 'customers/edit/:id',
        name: 'CustomerEdit',
        component: () => import('../views/customer/CustomerForm.vue'),
        meta: { title: '编辑客户', requiresAuth: true }
      },
      // --- 车型管理模块 ---
      {
        path: 'car-models',
        name: 'CarModelList',
        component: () => import('../views/carModel/CarModelList.vue'),
        meta: { title: '车型管理', requiresAuth: true }
      },
      {
        path: 'car-models/add',
        name: 'CarModelAdd',
        component: () => import('../views/carModel/CarModelForm.vue'),
        meta: { title: '添加车型', requiresAuth: true }
      },
      {
        path: 'car-models/edit/:id',
        name: 'CarModelEdit',
        component: () => import('../views/carModel/CarModelForm.vue'),
        meta: { title: '编辑车型', requiresAuth: true }
      },
      // --- 【新增】仓库管理模块 (替代原有的车辆管理) ---
      {
        path: 'warehouse/inventory', // 库存车辆 (按车型统计)
        name: 'InventoryList',
        component: () => import('../views/warehouse/InventoryList.vue'),
        meta: { title: '库存车辆', requiresAuth: true }
      },
      {
        path: 'warehouse/details',   // 仓库明细 (具体每一辆车)
        name: 'WarehouseDetailList',
        component: () => import('../views/warehouse/WarehouseDetailList.vue'),
        meta: { title: '仓库明细', requiresAuth: true }
      },
      {
        path: 'warehouse/stats',     // 进销存统计
        name: 'WarehouseStats',
        component: () => import('../views/warehouse/WarehouseStats.vue'),
        meta: { title: '进销存统计', requiresAuth: true }
      },

      // --- 销售管理模块 ---
      {
        path: 'sales',
        name: 'SaleOrderList',
        component: () => import('../views/sale/SaleOrderList.vue'),
        meta: { title: '销售订单', requiresAuth: true }
      },
      {
        path: 'sales/create',
        name: 'SaleOrderCreate',
        component: () => import('../views/sale/SaleOrderForm.vue'),
        meta: { title: '新建销售单', requiresAuth: true }
      },
      // --- 进货管理模块 ---
      {
        path: 'purchase',
        name: 'PurchaseList',
        component: () => import('../views/purchase/PurchaseOrderList.vue'),
        meta: { title: '进货管理', requiresAuth: true }
      },
      {
        path: 'purchase/add',
        name: 'PurchaseAdd',
        component: () => import('../views/purchase/PurchaseOrderForm.vue'),
        meta: { title: '新建采购单', requiresAuth: true }
      }
    ]
  },
  // 404页面 (放在最后)
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue'),
    meta: { title: '页面未找到' }
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || '系统'} - 汽车销售管理系统`
  if (to.matched.some(record => record.meta.requiresAuth)) {
    const token = localStorage.getItem('token')
    if (!token) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router