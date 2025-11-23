package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 预约实体类
 */
@Data
@TableName("appointment")
public class Appointment {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private Date appointmentDate;
    private Integer appointmentTime;
    private Integer status;
    private String symptoms;
    private String diagnosis;
    private Date createTime;
    private Date updateTime;
}
