<template>
  <div class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <el-icon class="logo-icon"><HotWater /></el-icon>
        <span v-if="!isCollapse" class="logo-text">水产销售系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="side-menu"
        background-color="#ffffff"
        text-color="#334155"
        active-text-color="#6366f1"
        :collapse="isCollapse"
        router
        :collapse-transition="false"
      >
        <el-menu-item index="/layout/dashboard" class="menu-item">
          <el-icon class="menu-icon"><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/layout/fish" class="menu-item">
          <el-icon class="menu-icon"><Shop /></el-icon>
          <span>鱼类管理</span>
        </el-menu-item>
        <el-menu-item index="/layout/sale" class="menu-item">
          <el-icon class="menu-icon"><ShoppingCart /></el-icon>
          <span>销售记录</span>
        </el-menu-item>
        <el-sub-menu index="system" class="menu-item">
          <template #title>
            <el-icon class="menu-icon"><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/layout/system/user" v-if="hasPermission('system:user:menu')">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/layout/system/role" v-if="hasPermission('system:role:menu')">
            <el-icon><Avatar /></el-icon>
            <span>角色管理</span>
          </el-menu-item>
          <el-menu-item index="/layout/system/permission" v-if="hasPermission('system:permission:menu')">
            <el-icon><Lock /></el-icon>
            <span>权限管理</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="toggleCollapse">
            <component :is="isCollapse ? 'Expand' : 'Fold'" />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/layout/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="route.path !== '/layout/dashboard'">{{ breadcrumbTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="36" class="avatar">
                {{ userStore.userInfo?.nickname?.charAt(0).toUpperCase() || 'U' }}
              </el-avatar>
              <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
              <el-icon class="arrow-icon"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessageBox } from 'element-plus'
import {
  HomeFilled, Shop, ShoppingCart, Setting, User, Avatar, Lock,
  Expand, Fold, ArrowDown, SwitchButton, HotWater
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const activeMenu = computed(() => route.path)

const breadcrumbTitle = computed(() => {
  const titles = {
    '/layout/fish': '鱼类管理',
    '/layout/sale': '销售记录',
    '/layout/system/user': '用户管理',
    '/layout/system/role': '角色管理',
    '/layout/system/permission': '权限管理'
  }
  return titles[route.path] || ''
})

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const hasPermission = (permission) => {
  return userStore.hasPermission(permission)
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      userStore.logoutAction()
      router.push('/login')
    } catch {
      // 取消退出
    }
  }
}
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  background: #f3f4f6;
}

.sidebar {
  background-color: #ffffff;
  transition: width 0.3s ease;
  overflow-x: hidden;
  box-shadow: 4px 0 24px rgba(0, 0, 0, 0.05);
  border-right: 1px solid #e2e8f0;
}

.logo {
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 0 20px;
  background-color: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.15);
}

.logo-icon {
  font-size: 28px;
  color: #6366f1;
}

.logo-text {
  color: #1e293b;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 1px;
}

.side-menu {
  border-right: none;
  padding-top: 16px;
  background-color: #ffffff;
}

/* 子菜单标题样式 - 与顶级菜单项对齐 */
.side-menu > .el-sub-menu > .el-sub-menu__title {
  height: 50px;
  line-height: 50px;
  margin: 4px 12px;
  padding: 0 16px !important;
  border-radius: 8px;
  transition: all 0.3s ease;
  color: #334155 !important;
}

.side-menu > .el-sub-menu > .el-sub-menu__title:hover {
  background: #f1f5f9 !important;
}

/* 系统管理子菜单展开时标题颜色 */
.side-menu > .el-sub-menu.is-opened > .el-sub-menu__title {
  color: #6366f1 !important;
}

.menu-item {
  height: 50px;
  line-height: 50px;
  margin: 4px 12px;
  padding: 0 16px !important;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.menu-item:hover {
  background: #f1f5f9 !important;
}

/* 基础字体颜色 - 所有菜单项（不包括子菜单标题） */
.side-menu .el-menu-item span,
.side-menu .el-sub-menu .el-menu-item span {
  color: #334155 !important;
}

.menu-item:hover span {
  color: #6366f1 !important;
}

/* 顶级菜单项激活样式 */
.side-menu > .el-menu-item.is-active {
  background: #f1f5f9 !important;
}

.side-menu > .el-menu-item.is-active span {
  color: #6366f1 !important;
  font-weight: 600;
}

/* 子菜单内的菜单项激活样式 - 紫色字体 */
.side-menu .el-sub-menu .el-menu-item.is-active {
  background: #eff6ff !important;
}

.side-menu .el-sub-menu .el-menu-item.is-active span {
  color: #6366f1 !important;
  font-weight: 600;
}

/* 子菜单展开时标题颜色 */
.side-menu .el-sub-menu.is-opened > .el-sub-menu__title {
  color: #6366f1 !important;
}

.menu-icon {
  font-size: 20px;
  margin-right: 12px;
  color: #64748b;
}

.menu-item:hover .menu-icon,
.side-menu .el-menu-item:hover .menu-icon,
.side-menu .el-sub-menu .el-menu-item:hover .menu-icon {
  color: #6366f1;
}

.header {
  background-color: #ffffff;
  border-bottom: 1px solid #e2e8f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.collapse-btn {
  font-size: 22px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #64748b;
  padding: 8px;
  border-radius: 8px;
}

.collapse-btn:hover {
  color: #6366f1;
  background: #f1f5f9;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 24px;
  transition: all 0.3s ease;
}

.user-info:hover {
  background: #f1f5f9;
}

.avatar {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
}

.username {
  color: #334155;
  font-weight: 500;
  font-size: 14px;
}

.arrow-icon {
  color: #94a3b8;
  font-size: 16px;
}

.main-content {
  background-color: #f8fafc;
  padding: 24px;
  overflow-y: auto;
}
</style>
