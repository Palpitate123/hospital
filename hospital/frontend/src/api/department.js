import request from './request';

// 科室API
const departmentAPI = {
  // 获取所有科室列表
  getAllDepartments: () => {
    return request({
      url: '/api/department/list',
      method: 'get'
    });
  },

  // 获取科室详情
  getDepartmentDetail: (deptId) => {
    return request({
      url: '/api/department/detail/' + deptId,
      method: 'get'
    });
  },

  // 添加科室
  addDepartment: (data) => {
    return request({
      url: '/api/department/add',
      method: 'post',
      data
    });
  },

  // 更新科室信息
  updateDepartment: (deptId, data) => {
    return request({
      url: '/api/department/update',
      method: 'put',
      data: {
        ...data,
        id: deptId
      }
    });
  },

  // 更新科室状态
  updateDepartmentStatus: (deptId, status) => {
    return request({
      url: '/api/department/status',
      method: 'put',
      data: { deptId, status }
    });
  },

  // 搜索科室
  searchDepartments: (params) => {
    return request({
      url: '/api/department/search',
      method: 'get',
      params
    });
  },

  // 删除科室
  deleteDepartment: (deptId) => {
    return request({
      url: '/api/department/delete/' + deptId,
      method: 'delete'
    });
  },

  // 获取科室医生列表
  getDepartmentDoctors: (deptId) => {
    return request({
      url: '/api/department/doctors/' + deptId,
      method: 'get'
    });
  }
};

export default departmentAPI;