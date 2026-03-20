package com.hospital.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.dto.DoctorDTO;
import com.hospital.entity.HosDoctor;

import java.util.Map;

/**
 * 医生服务接口
 * 处理医生管理相关业务
 * 
 * @author 徐凌
 * @version 1.0.0
 */
public interface DoctorService extends IService<HosDoctor> {

    /**
     * 分页查询医生列表
     * 
     * @param page 分页参数
     * @param deptId 科室ID
     * @param doctorName 医生姓名
     * @param status 状态
     * @return 分页结果
     */
    Page<Map<String, Object>> getDoctorPage(Page<HosDoctor> page, Long deptId, String doctorName, Integer status);

    /**
     * 查询科室下的医生列表
     * 
     * @param deptId 科室ID
     * @return 医生列表
     */
    java.util.List<Map<String, Object>> getDoctorsByDeptId(Long deptId);

    /**
     * 获取医生详情
     * 
     * @param id 医生ID
     * @return 医生详情
     */
    Map<String, Object> getDoctorDetail(Long id);

    /**
     * 新增医生
     * 
     * @param doctorDTO 医生信息
     * @return 是否成功
     */
    boolean addDoctor(DoctorDTO doctorDTO);

    /**
     * 编辑医生
     * 
     * @param doctorDTO 医生信息
     * @return 是否成功
     */
    boolean updateDoctor(DoctorDTO doctorDTO);

    /**
     * 删除医生
     * 
     * @param id 医生ID
     * @return 是否成功
     */
    boolean deleteDoctor(Long id);

    /**
     * 启用/停用医生账号
     * 
     * @param id 医生ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateDoctorStatus(Long id, Integer status);
}
