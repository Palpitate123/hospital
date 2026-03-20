import request from './request'

export function getDepartmentPage(params) {
  return request({
    url: '/department/page',
    method: 'get',
    params
  })
}

export function getDepartmentList() {
  return request({
    url: '/department/list',
    method: 'get'
  })
}

export function getDepartmentDetail(id) {
  return request({
    url: `/department/${id}`,
    method: 'get'
  })
}

export function addDepartment(data) {
  return request({
    url: '/department',
    method: 'post',
    data
  })
}

export function updateDepartment(data) {
  return request({
    url: '/department',
    method: 'put',
    data
  })
}

export function deleteDepartment(id) {
  return request({
    url: `/department/${id}`,
    method: 'delete'
  })
}

export function updateDepartmentStatus(id, status) {
  return request({
    url: `/department/status/${id}`,
    method: 'put',
    params: { status }
  })
}
