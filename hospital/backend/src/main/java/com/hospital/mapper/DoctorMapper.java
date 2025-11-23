package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.Doctor;
import com.hospital.vo.DoctorVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 医生Mapper接口
 */
public interface DoctorMapper extends BaseMapper<Doctor> {
    /**
     * 根据科室ID查询医生列表
     */
    List<Doctor> selectByDeptId(@Param("deptId") Long deptId);

    /**
     * 根据用户ID查询医生信息
     */
    Doctor selectByUserId(@Param("userId") Long userId);

    /**
     * 查询当日预约数量
     */
    Integer countTodayAppointments(@Param("doctorId") Long doctorId, @Param("date") String date);
    
    /**
     * 根据科室ID查询医生VO列表
     */
    List<DoctorVO> selectDoctorVOsByDeptId(@Param("deptId") Long deptId);
    
    /**
     * 搜索医生
     * @param keyword 搜索关键词
     * @return 医生VO列表
     */
    List<DoctorVO> searchDoctors(@Param("keyword") String keyword);
}
