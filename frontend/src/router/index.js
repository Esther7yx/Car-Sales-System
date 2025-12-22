import { createRouter, createWebHistory } from 'vue-router'

// 定义路由
const routes = [
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: { requiresAuth: true }
  },
  // 厂商管理
  {
    path: '/manufacturers',
    name: 'ManufacturerList',
    component: () => import('../views/manufacturer/ManufacturerList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/manufacturers/add',
    name: 'ManufacturerAdd',
    component: () => import('../views/manufacturer/ManufacturerForm.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/manufacturers/edit/:id',
    name: 'ManufacturerEdit',
    component: () => import('../views/manufacturer/ManufacturerForm.vue'),
    meta: { requiresAuth: true }
  },
  // 车型管理
  {
    path: '/car-models',
    name: 'CarModelList',
    component: () => import('../views/carModel/CarModelList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/car-models/add',
    name: 'CarModelAdd',
    component: () => import('../views/carModel/CarModelForm.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/car-models/edit/:id',
    name: 'CarModelEdit',
    component: () => import('../views/carModel/CarModelForm.vue'),
    meta: { requiresAuth: true }
  },
  // 车辆管理
  {
    path: '/vehicles',
    name: 'VehicleList',
    component: () => import('../views/vehicle/VehicleList.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/vehicles/add',
    name: 'VehicleAdd',
    component: () => import('../views/vehicle/VehicleForm.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/vehicles/edit/:vin',
    name: 'VehicleEdit',
    component: () => import('../views/vehicle/VehicleForm.vue'),
    meta: { requiresAuth: true }
  },
  // 404页面
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('../views/NotFound.vue')
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 检查路由是否需要认证
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 检查是否已登录
    const token = localStorage.getItem('token')
    if (!token) {
      // 未登录，跳转到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      // 已登录，继续
      next()
    }
  } else {
    // 不需要认证，直接放行
    next()
  }
})

export default router
