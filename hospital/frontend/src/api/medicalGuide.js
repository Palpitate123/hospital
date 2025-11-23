import request from './request';

const medicalGuideAPI = {
  // 智能推荐科室
  recommendDepartment(symptoms) {
    return request({
      url: '/api/guidance/recommend',
      method: 'POST',
      data: { symptomsDescription: symptoms }
    })
  },

  // 保存导诊记录
  saveMedicalGuide(data) {
    return request({
      url: '/api/guidance/save',
      method: 'POST',
      data: data
    })
  },

  // 获取患者导诊历史
  getPatientGuidanceHistory() {
    return request({
      url: '/api/guidance/history',
      method: 'GET'
    })
  },

  // 更新导诊状态
  updateGuidanceStatus(id, status) {
    return request({
      url: `/api/guidance/${id}/status`,
      method: 'PUT',
      params: { status: status }
    })
  },

  // 根据用户ID获取导诊记录
  getGuidanceByUserId() {
    return request({
      url: '/api/guidance/user',
      method: 'GET'
    })
  }
};

export default medicalGuideAPI;
