import request from './request'

const patientAPI = {
  // 获取患者信息
  getPatientInfo() {
    return request({
      url: '/api/patient/profile',
      method: 'GET'
    })
  },

  // 获取患者信息根据ID
  getPatientById(id) {
    return request({
      url: `/api/patient/info/${id}`,
      method: 'GET'
    })
  },

  // 更新患者信息
  updatePatient(data) {
    return request({
      url: '/api/patient/profile',
      method: 'PUT',
      data: data
    })
  },

  // 患者注册
  registerPatient(patientData) {
    return request({
      url: '/api/patient/register',
      method: 'POST',
      data: patientData
    })
  },

  // 根据用户名获取患者信息
  getPatientByUsername(username) {
    return request({
      url: `/api/patient/username/${username}`,
      method: 'GET'
    })
  },
  
  // 获取所有患者列表
  getAllPatients() {
    return request({
      url: '/api/patient/list',
      method: 'GET'
    })
  }
}

export default patientAPI