package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.entity.Doctor;
import com.hospital.vo.DoctorVO;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 医生服务接口
 */
public interface DoctorService extends IService<Doctor> {

    /**
     * 根据科室ID查询医生列表
     */
    List<Doctor> getDoctorsByDeptId(Long deptId);
    
    /**
     * 根据科室ID查询医生列表（VO格式）
     */
    List<DoctorVO> getDoctorVOsByDeptId(Long deptId);

    /**
     * 根据用户ID查询医生信息
     */
    Doctor getDoctorByUserId(Long userId);
    
    /**
     * 根据用户ID查询医生信息（VO格式）
     */
    DoctorVO getDoctorVOByUserId(Long userId);

    /**
     * 查询医生某天的预约数量
     */
    int getTodayAppointmentCount(Long doctorId);
    
    /**
     * 查询医生某天的预约数量
     */
    int getAppointmentCountByDate(Long doctorId, Date date);

    /**
     * 获取医生详细信息（包括用户信息和科室信息）
     */
    Map<String, Object> getDoctorDetail(Long doctorId);
    
    /**
     * 获取医生详细信息（VO格式）
     */
    DoctorVO getDoctorVOById(Long doctorId);

    /**
     * 更新医生信息
     */
    boolean updateDoctor(Doctor doctor);

    /**
     * 更新医生状态（出诊/停诊）
     */
    boolean updateDoctorStatus(Long doctorId, Integer status);
    
    /**
     * 搜索医生
     */
    List<DoctorVO> searchDoctors(String keyword);
    
    /**
     * 获取所有医生列表（VO格式）
     */
    List<DoctorVO> getAllDoctorVOs();
}
