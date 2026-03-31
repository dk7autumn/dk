import request from './request'

export function getFishList(params) {
  return request({
    url: '/fish',
    method: 'get',
    params
  })
}

export function getFishById(id) {
  return request({
    url: `/fish/${id}`,
    method: 'get'
  })
}

export function addFish(data) {
  return request({
    url: '/fish',
    method: 'post',
    data
  })
}

export function updateFish(id, data) {
  return request({
    url: `/fish/${id}`,
    method: 'put',
    data
  })
}

export function deleteFish(id) {
  return request({
    url: `/fish/${id}`,
    method: 'delete'
  })
}
