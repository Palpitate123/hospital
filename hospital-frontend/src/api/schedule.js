import request from './request'

export function getSchedulePage(params) {
  return request({
    url: '/schedule/page',
    method: 'get',
    params
  })
}

export function getDoctorSchedules(doctorId, params) {
  return request({
    url: `/schedule/doctor/${doctorId}`,
    method: 'get',
    params
  })
}

export function getScheduleDetail(id) {
  return request({
    url: `/schedule/${id}`,
    method: 'get'
  })
}

export function addSchedule(data) {
  return request({
    url: '/schedule',
    method: 'post',
    data
  })
}

export function updateSchedule(data) {
  return request({
    url: '/schedule',
    method: 'put',
    data
  })
}

export function deleteSchedule(id) {
  return request({
    url: `/schedule/${id}`,
    method: 'delete'
  })
}

export function publishSchedule(id) {
  return request({
    url: `/schedule/publish/${id}`,
    method: 'put'
  })
}

export function unpublishSchedule(id) {
  return request({
    url: `/schedule/unpublish/${id}`,
    method: 'put'
  })
}
