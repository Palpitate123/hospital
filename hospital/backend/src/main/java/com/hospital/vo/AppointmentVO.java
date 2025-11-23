package com.hospital.vo;

import lombok.Data;
import java.util.Date;

/**
 * 预约VO类，用于返回预约的详细信息
 */
@Data
public class AppointmentVO {
    // 预约ID
    private Long id;
    
    // 患者信息
    private Long patientId;
    private String patientName;
    private String patientPhone;
    private String patientGender;
    private Integer patientAge;
    
    // 医生信息
    private Long doctorId;
    private String doctorName;
    private String doctorTitle;
    
    // 科室信息
    private Long departmentId;
    private String departmentName;
    
    // 预约信息
    private Date appointmentDate;
    private Integer timeSlot;
    private String timeSlotText;
    private String symptomDescription;
    private Integer status;
    private String statusText;
    private String cancelReason;
    
    // 创建和更新时间
    private Date createTime;
    private Date updateTime;
    
    // 就诊信息
    private String diagnosisResult;
    private String prescription;
}
