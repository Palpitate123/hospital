package com.hospital.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 排班请求DTO
 * 接收排班新增/编辑请求参数
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Data
public class ScheduleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 排班ID（编辑时必填）
     */
    private Long id;

    /**
     * 医生ID
     */
    @NotNull(message = "医生不能为空")
    private Long doctorId;

    /**
     * 科室ID
     */
    @NotNull(message = "科室不能为空")
    private Long deptId;

    /**
     * 出诊日期
     */
    @NotNull(message = "出诊日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate workDate;

    /**
     * 出诊时段
     */
    @NotNull(message = "出诊时段不能为空")
    private String workTime;

    /**
     * 号源总量
     */
    @NotNull(message = "号源总量不能为空")
    private Integer totalNumber;
}
