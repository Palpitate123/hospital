package com.hospital.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 预约请求DTO
 * 接收预约挂号请求参数
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Data
public class AppointmentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 排班ID
     */
    @NotNull(message = "排班信息不能为空")
    private Long scheduleId;

    /**
     * 号源ID
     */
    @NotNull(message = "号源信息不能为空")
    private Long sourceId;
}
