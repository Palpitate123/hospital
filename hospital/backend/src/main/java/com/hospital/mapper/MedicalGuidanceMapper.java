package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.MedicalGuidance;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 医疗导诊Mapper接口
 */
public interface MedicalGuidanceMapper extends BaseMapper<MedicalGuidance> {
    /**
     * 根据患者ID查询导诊记录
     */
    List<MedicalGuidance> selectByPatientId(@Param("patientId") Long patientId);
}
