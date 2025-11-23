package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.entity.MedicalGuidance;
import java.util.List;
import java.util.Map;

/**
 * 医疗导诊服务接口
 */
public interface MedicalGuidanceService extends IService<MedicalGuidance> {

    /**
     * 根据患者ID查询导诊记录
     */
    List<MedicalGuidance> getGuidanceByPatientId(Long patientId);

    /**
     * 创建导诊记录
     */
    boolean createGuidance(MedicalGuidance guidance);

    /**
     * AI推荐科室
     */
    Map<String, Object> recommendDepartment(String symptoms, String description);

    /**
     * 获取导诊详情
     */
    MedicalGuidance getGuidanceDetail(Long guidanceId);

    /**
     * 更新导诊状态
     */
    boolean updateGuidanceStatus(Long guidanceId, Integer status);
}
