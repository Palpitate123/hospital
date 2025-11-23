import request from './request';

const appointmentAPI = {
  // 获取患者的预约列表
  getPatientAppointments() {
    return request({
      url: '/api/appointment/patient-list',
      method: 'GET'
    })
  },

  // 获取医生的预约列表
  getDoctorAppointments() {
    return request({
      url: '/api/appointment/doctor-list',
      method: 'GET'
    })
  },

  // 创建预约
  createAppointment(data) {
    return request({
      url: '/api/appointment/create',
      method: 'POST',
      data: data
    })
  },

  // 更新预约状态
  updateAppointmentStatus(appointmentId, status) {
    return request({
      url: '/api/appointment/status',
      method: 'PUT',
      data: { appointmentId, status }
    })
  },

  // 获取预约详情
  getAppointmentDetail(id) {
    return request({
      url: `/api/appointment/detail/${id}`,
      method: 'GET'
    })
  },

  // 取消预约（管理员）
  cancelAppointment(appointmentId, reason) {
    return request({
      url: `/api/appointment/admin/cancel/${appointmentId}`,
      method: 'POST',
      params: { reason }
    })
  },
  
  // 获取所有预约列表（管理员使用）
  getAppointments(params) {
    return request({
      url: '/api/appointment/list',
      method: 'GET',
      params: params
    })
  },
  
  // 获取最近的预约记录（用于Dashboard）
  getRecentAppointments() {
    return request({
      url: '/api/appointment/list',
      method: 'GET',
      params: {
        recent: true
      }
    })
  }
};

export default appointmentAPI;
