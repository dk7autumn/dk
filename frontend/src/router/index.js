import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/auth/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/layout',
    component: () => import('@/components/Layout.vue'),
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Dashboard.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: '/fish',
        name: 'Fish',
        component: () => import('@/views/fish/FishList.vue'),
        meta: { title: '鱼类管理', icon: 'Basket' }
      },
      {
        path: '/sale',
        name: 'Sale',
        component: () => import('@/views/sale/SaleList.vue'),
        meta: { title: '销售记录', icon: 'ShoppingCart' }
      },
      {
        path: '/stats',
        name: 'Stats',
        component: () => import('@/views/stats/StatsView.vue'),
        meta: { title: '统计分析', icon: 'DataAnalysis' }
      },
      {
        path: '/system/user',
        name: 'SystemUser',
        component: () => import('@/views/system/UserManage.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: '/system/role',
        name: 'SystemRole',
        component: () => import('@/views/system/RoleManage.vue'),
        meta: { title: '角色管理', icon: 'Avatar' }
      },
      {
        path: '/system/permission',
        name: 'SystemPermission',
        component: () => import('@/views/system/PermissionManage.vue'),
        meta: { title: '权限管理', icon: 'Lock' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()

  if (to.meta.requiresAuth === false) {
    next()
    return
  }

  if (!userStore.token) {
    next('/login')
    return
  }

  // 如果是第一次访问，获取用户信息
  if (!userStore.userInfo) {
    userStore.getUserInfoAction().then(() => {
      next()
    }).catch(() => {
      next('/login')
    })
    return
  }

  next()
})

export default router
