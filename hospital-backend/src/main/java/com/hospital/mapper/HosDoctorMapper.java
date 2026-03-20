package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.HosDoctor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 医生Mapper接口
 * 医生数据访问层
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Mapper
public interface HosDoctorMapper extends BaseMapper<HosDoctor> {

    /**
     * 查询医生列表（包含科室信息）
     * 
     * @param params 查询参数
     * @return 医生列表
     */
    List<Map<String, Object>> selectDoctorWithDept(@Param("params") Map<String, Object> params);

    /**
     * 根据ID查询医生详情（包含科室信息）
     * 
     * @param id 医生ID
     * @return 医生详情
     */
    Map<String, Object> selectDoctorDetailById(@Param("id") Long id);

    /**
     * 根据科室ID统计医生数量
     * 
     * @param deptId 科室ID
     * @return 医生数量
     */
    int countByDeptId(@Param("deptId") Long deptId);
}
