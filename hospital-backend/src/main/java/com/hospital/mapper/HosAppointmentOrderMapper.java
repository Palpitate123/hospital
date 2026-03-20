package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.HosAppointmentOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 预约订单Mapper接口
 * 预约订单数据访问层
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Mapper
public interface HosAppointmentOrderMapper extends BaseMapper<HosAppointmentOrder> {

    /**
     * 查询订单列表（包含医生、用户信息）
     * 
     * @param params 查询参数
     * @return 订单列表
     */
    List<Map<String, Object>> selectOrderWithInfo(@Param("params") Map<String, Object> params);

    /**
     * 根据ID查询订单详情（包含医生、用户、科室信息）
     * 
     * @param id 订单ID
     * @return 订单详情
     */
    Map<String, Object> selectOrderDetailById(@Param("id") Long id);

    /**
     * 检查用户是否已预约同一医生同一时段
     * 
     * @param userId 用户ID
     * @param doctorId 医生ID
     * @param scheduleId 排班ID
     * @return 预约数量
     */
    int checkAppointmentExists(@Param("userId") Long userId, @Param("doctorId") Long doctorId, @Param("scheduleId") Long scheduleId);

    /**
     * 统计各科室挂号数量
     * 
     * @return 统计结果
     */
    List<Map<String, Object>> countByDepartment();

    /**
     * 统计各医生预约数量
     * 
     * @return 统计结果
     */
    List<Map<String, Object>> countByDoctor();
}
