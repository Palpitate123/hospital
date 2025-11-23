package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 医疗导诊实体类
 */
@Data
@TableName("medical_guidance")
public class MedicalGuidance {
    private Long id;
    private Long patientId;
    private String symptomsDescription;
    private String detailedDescription;
    private Long recommendedDeptId;
    private String recommendedDeptName;
    private String recommendationReason;
    private Integer status; // 1: 未就诊, 2: 已就诊, 3: 已忽略
    private Date guidanceTime;
    private Date updateTime;
}
