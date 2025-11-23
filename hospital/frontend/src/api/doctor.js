import request from './request';

// 医生API
const doctorAPI = {
  // 获取所有医生列表
  getAllDoctors: () => {
    return request({
      url: '/api/doctor/all',
      method: 'get'
    });
  },

  // 根据科室ID获取医生列表
  getDoctorsByDeptId: (deptId) => {
    return request({
      url: '/doctor/dept/' + deptId,
      method: 'get'
    });
  },

  // 获取医生详情
  getDoctorDetail: (doctorId) => {
    return request({
      url: '/doctor/' + doctorId,
      method: 'get'
    });
  },

  // 更新医生信息
  updateDoctor: (doctorId, data) => {
    return request({
      url: '/doctor/' + doctorId,
      method: 'put',
      data: data
    });
  },

  // 更新医生状态
  updateDoctorStatus: (doctorId, status) => {
    return request({
      url: '/doctor/' + doctorId + '/status',
      method: 'put',
      data: { status: status }
    });
  },

  // 删除医生
  deleteDoctor: (doctorId) => {
    return request({
      url: '/doctor/' + doctorId,
      method: 'delete'
    });
  },

  // 搜索医生
  searchDoctors: (keyword) => {
    return request({
      url: '/doctor/search',
      method: 'get',
      params: { keyword: keyword }
    });
  }
};

export default doctorAPI;