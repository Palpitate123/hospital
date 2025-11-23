package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.entity.Department;
import com.hospital.vo.DoctorVO;
import java.util.List;
import java.util.Map;

/**
 * 科室服务接口
 */
public interface DepartmentService extends IService<Department> {

    /**
     * 获取所有科室列表
     */
    List<Department> getAllDepartments();

    /**
     * 根据科室ID查询科室信息
     */
    Department getDepartmentById(Long deptId);

    /**
     * 新增科室
     */
    boolean addDepartment(Department department);

    /**
     * 更新科室信息
     */
    boolean updateDepartment(Department department);

    /**
     * 删除科室
     */
    boolean deleteDepartment(Long deptId);

    /**
     * 获取科室详情，包括医生数量等统计信息
     */
    Map<String, Object> getDepartmentDetail(Long deptId);

    /**
     * 搜索科室
     */
    List<Department> searchDepartments(String keyword);
    
    /**
     * 获取科室医生列表
     */
    List<DoctorVO> getDepartmentDoctors(Long deptId);
}
