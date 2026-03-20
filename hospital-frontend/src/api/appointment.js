import request from './request'

export function submitAppointment(data) {
  return request({
    url: '/appointment/submit',
    method: 'post',
    data
  })
}

export function cancelAppointment(id) {
  return request({
    url: `/appointment/cancel/${id}`,
    method: 'put'
  })
}

export function getMyAppointments(params) {
  return request({
    url: '/appointment/my',
    method: 'get',
    params
  })
}

export function getOrderPage(params) {
  return request({
    url: '/appointment/page',
    method: 'get',
    params
  })
}

export function getOrderDetail(id) {
  return request({
    url: `/appointment/${id}`,
    method: 'get'
  })
}

export function forceCancelOrder(id) {
  return request({
    url: `/appointment/force-cancel/${id}`,
    method: 'put'
  })
}

export function getDepartmentStats() {
  return request({
    url: '/appointment/stats/department',
    method: 'get'
  })
}

export function getDoctorStats() {
  return request({
    url: '/appointment/stats/doctor',
    method: 'get'
  })
}

export function getTodayStats() {
  return request({
    url: '/appointment/stats/today',
    method: 'get'
  })
}

export function getTotalStats() {
  return request({
    url: '/appointment/stats/total',
    method: 'get'
  })
}
