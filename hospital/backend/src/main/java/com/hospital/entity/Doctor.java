package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 医生实体类
 */
@Data
@TableName("doctor")
public class Doctor {
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
}
