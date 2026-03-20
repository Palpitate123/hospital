package com.hospital.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 科室请求DTO
 * 接收科室新增/编辑请求参数
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Data
public class DepartmentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 科室ID（编辑时必填）
     */
    private Long id;

    /**
     * 科室名称
     */
    @NotBlank(message = "科室名称不能为空")
    private String deptName;

    /**
     * 科室简介
     */
    private String deptDesc;

    /**
     * 科室状态
     */
    private Integer status;
}
