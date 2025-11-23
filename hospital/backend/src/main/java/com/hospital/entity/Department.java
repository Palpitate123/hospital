package com.hospital.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 科室实体类
 */
@Data
@TableName("department")
public class Department {
    private Long id;
    private String deptName;
    private String description;
    private Integer sort;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
