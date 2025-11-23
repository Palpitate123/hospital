package com.hospital.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 医生信息展示类
 */
@Data
public class DoctorVO {
    private Long id;
    private Long userId;
    private Long deptId;
    private String title;
    private String specialty;
    private String introduction;
    private Integer experience;
    private BigDecimal consultationFee;
    private Integer dailyQuota;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    
    // 用户信息
    private String realName;
    private String phone;
    private String email;
    
    // 科室信息
    private String deptName;
    
    // 统计信息
    private Integer todayAppointmentCount;
    private Integer availableQuota;
    
    // 状态文本
    private String statusText;
    
    // 职称文本
    private String titleText;
}
