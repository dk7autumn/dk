import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login, getUserInfo, logout } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)
  const permissions = ref([])
  const roles = ref([])

  async function loginAction(loginForm) {
    const res = await login(loginForm)
    token.value = res.data.token
    userInfo.value = res.data.userInfo
    permissions.value = res.data.userInfo.permissions
    roles.value = res.data.userInfo.roles
    localStorage.setItem('token', res.data.token)
    return res
  }

  async function getUserInfoAction() {
    const res = await getUserInfo()
    userInfo.value = res.data
    permissions.value = res.data.permissions
    roles.value = res.data.roles
    return res
  }

  function logoutAction() {
    token.value = ''
    userInfo.value = null
    permissions.value = []
    roles.value = []
    localStorage.removeItem('token')
    logout().catch(() => {})
  }

  function hasPermission(permission) {
    return permissions.value.includes(permission)
  }

  function hasRole(role) {
    return roles.value.includes(role)
  }

  return {
    token,
    userInfo,
    permissions,
    roles,
    loginAction,
    logoutAction,
    getUserInfoAction,
    hasPermission,
    hasRole
  }
})
