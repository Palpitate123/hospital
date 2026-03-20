package com.hospital.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.dto.DepartmentDTO;
import com.hospital.entity.HosDepartment;

import java.util.List;

/**
 * 科室服务接口
 * 处理科室管理相关业务
 * 
 * @author 徐凌
 * @version 1.0.0
 */
public interface DepartmentService extends IService<HosDepartment> {

    /**
     * 分页查询科室列表
     * 
     * @param page 分页参数
     * @param deptName 科室名称（模糊查询）
     * @param status 状态
     * @return 分页结果
     */
    Page<HosDepartment> getDepartmentPage(Page<HosDepartment> page, String deptName, Integer status);

    /**
     * 查询所有启用的科室列表
     * 
     * @return 科室列表
     */
    List<HosDepartment> getEnabledDepartments();

    /**
     * 新增科室
     * 
     * @param departmentDTO 科室信息
     * @return 是否成功
     */
    boolean addDepartment(DepartmentDTO departmentDTO);

    /**
     * 编辑科室
     * 
     * @param departmentDTO 科室信息
     * @return 是否成功
     */
    boolean updateDepartment(DepartmentDTO departmentDTO);

    /**
     * 删除科室
     * 
     * @param id 科室ID
     * @return 是否成功
     */
    boolean deleteDepartment(Long id);

    /**
     * 启用/停用科室
     * 
     * @param id 科室ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateDepartmentStatus(Long id, Integer status);

    /**
     * 获取科室详情
     * 
     * @param id 科室ID
     * @return 科室详情
     */
    HosDepartment getDepartmentDetail(Long id);
}
