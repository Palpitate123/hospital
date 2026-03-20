import request from './request'

export function getDoctorPage(params) {
  return request({
    url: '/doctor/page',
    method: 'get',
    params
  })
}

export function getDoctorsByDept(deptId) {
  return request({
    url: `/doctor/dept/${deptId}`,
    method: 'get'
  })
}

export function getDoctorDetail(id) {
  return request({
    url: `/doctor/${id}`,
    method: 'get'
  })
}

export function addDoctor(data) {
  return request({
    url: '/doctor',
    method: 'post',
    data
  })
}

export function updateDoctor(data) {
  return request({
    url: '/doctor',
    method: 'put',
    data
  })
}

export function deleteDoctor(id) {
  return request({
    url: `/doctor/${id}`,
    method: 'delete'
  })
}

export function updateDoctorStatus(id, status) {
  return request({
    url: `/doctor/status/${id}`,
    method: 'put',
    params: { status }
  })
}
