import request from './request'

export function getSaleList(params) {
  return request({
    url: '/sale',
    method: 'get',
    params
  })
}

export function getFishList() {
  return request({
    url: '/sale/fish-list',
    method: 'get'
  })
}

export function addSale(data) {
  return request({
    url: '/sale',
    method: 'post',
    data
  })
}

export function deleteSale(id) {
  return request({
    url: `/sale/${id}`,
    method: 'delete'
  })
}

// 统计相关
export function getDailyStats(date) {
  return request({
    url: '/sale/stats/daily',
    method: 'get',
    params: { date }
  })
}

export function getWeeklyStats(date) {
  return request({
    url: '/sale/stats/weekly',
    method: 'get',
    params: { date }
  })
}

export function getMonthlyStats(date) {
  return request({
    url: '/sale/stats/monthly',
    method: 'get',
    params: { date }
  })
}

export function getQuarterlyStats(date) {
  return request({
    url: '/sale/stats/quarterly',
    method: 'get',
    params: { date }
  })
}

export function getYearlyStats(year) {
  return request({
    url: '/sale/stats/yearly',
    method: 'get',
    params: { year }
  })
}
