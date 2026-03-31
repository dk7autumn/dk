import request from './request'

export function getPermissionList() {
  return request({
    url: '/permission',
    method: 'get'
  })
}

export function getMenuTree() {
  return request({
    url: '/permission/menu',
    method: 'get'
  })
}

export function getPermissionById(id) {
  return request({
    url: `/permission/${id}`,
    method: 'get'
  })
}

export function addPermission(data) {
  return request({
    url: '/permission',
    method: 'post',
    data
  })
}

export function updatePermission(id, data) {
  return request({
    url: `/permission/${id}`,
    method: 'put',
    data
  })
}

export function deletePermission(id) {
  return request({
    url: `/permission/${id}`,
    method: 'delete'
  })
}
