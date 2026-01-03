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
      // --- 车辆管理模块 ---
      {
        path: 'vehicles',
        name: 'VehicleList',
        component: () => import('../views/vehicle/VehicleList.vue'),
        meta: { title: '车辆管理', requiresAuth: true }
      },
      {
        path: 'vehicles/add',
        name: 'VehicleAdd',
        component: () => import('../views/vehicle/VehicleForm.vue'),
        meta: { title: '车辆入库', requiresAuth: true }
      },
      {
        path: 'vehicles/edit/:vin',
        name: 'VehicleEdit',
        component: () => import('../views/vehicle/VehicleForm.vue'),
        meta: { title: '编辑车辆', requiresAuth: true }
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
        component: () => import('../views/customer/CustomerPageForm.vue'),
        meta: { title: '添加客户', requiresAuth: true }
      },
      {
        path: 'customers/edit/:id',
        name: 'CustomerEdit',
        component: () => import('../views/customer/CustomerPageForm.vue'),
        meta: { title: '编辑客户', requiresAuth: true }
      },
      // --- 销售管理模块 ---
      {
        path: 'sale-orders',
        name: 'SaleOrderList',
        component: () => import('../views/saleOrder/SaleOrderList.vue'),
        meta: { title: '销售管理', requiresAuth: true }
      },
      {
        path: 'sale-orders/add',
        name: 'SaleOrderAdd',
        component: () => import('../views/saleOrder/SaleOrderForm.vue'),
        meta: { title: '添加销售订单', requiresAuth: true }
      },
      {
        path: 'sale-orders/detail/:id',
        name: 'SaleOrderDetail',
        component: () => import('../views/saleOrder/SaleOrderDetail.vue'),
        meta: { title: '销售订单详情', requiresAuth: true }
      },
      // --- 进货管理模块 ---
      {
        path: 'purchase-orders',
        name: 'PurchaseOrderList',
        component: () => import('../views/purchaseOrder/PurchaseOrderList.vue'),
        meta: { title: '进货管理', requiresAuth: true }
      },
      {
        path: 'purchase-orders/add',
        name: 'PurchaseOrderAdd',
        component: () => import('../views/purchaseOrder/PurchaseOrderForm.vue'),
        meta: { title: '添加进货订单', requiresAuth: true }
      },
      {
        path: 'purchase-orders/detail/:id',
        name: 'PurchaseOrderDetail',
        component: () => import('../views/purchaseOrder/PurchaseOrderDetail.vue'),
        meta: { title: '进货订单详情', requiresAuth: true }
      },
      {
        path: 'purchase-orders/edit/:id',
        name: 'PurchaseOrderEdit',
        component: () => import('../views/purchaseOrder/PurchaseOrderForm.vue'),
        meta: { title: '编辑进货订单', requiresAuth: true }
      },
      // --- 仓库管理模块 ---
      {
        path: 'warehouses',
        name: 'WarehouseList',
        component: () => import('../views/warehouse/WarehouseList.vue'),
        meta: { title: '仓库管理', requiresAuth: true }
      },
      {
        path: 'warehouses/add',
        name: 'WarehouseAdd',
        component: () => import('../views/warehouse/WarehouseForm.vue'),
        meta: { title: '添加仓库', requiresAuth: true }
      },
      {
        path: 'warehouses/edit/:id',
        name: 'WarehouseEdit',
        component: () => import('../views/warehouse/WarehouseForm.vue'),
        meta: { title: '编辑仓库', requiresAuth: true }
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

// 路由守卫：处理登录验证和页面标题
router.beforeEach((to, from, next) => {
  // 设置浏览器标题
  document.title = `${to.meta.title || '系统'} - 汽车销售管理系统`

  // 检查路由是否需要认证
  if (to.matched.some(record => record.meta.requiresAuth)) {
    const token = localStorage.getItem('token')
    if (!token) {
      // 未登录，跳转到登录页，并记录原目标路径
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      next()
    }
  } else if (to.path === '/') {
    // 处理根路径访问
    const token = localStorage.getItem('token')
    if (token) {
      next('/dashboard')
    } else {
      next('/login')
    }
  } else if (to.path === '/login') {
    // 如果用户已经登录但访问登录页，重定向到仪表板
    const token = localStorage.getItem('token')
    if (token) {
      next('/dashboard')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router