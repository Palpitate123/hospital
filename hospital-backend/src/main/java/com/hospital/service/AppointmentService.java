package com.hospital.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.dto.AppointmentDTO;
import com.hospital.entity.HosAppointmentOrder;

import java.util.List;
import java.util.Map;

/**
 * 预约订单服务接口
 * 处理预约挂号相关业务
 * 
 * @author 徐凌
 * @version 1.0.0
 */
public interface AppointmentService extends IService<HosAppointmentOrder> {

    /**
     * 提交预约申请
     * 核心业务：号源扣减、订单生成、防超卖
     * 
     * @param userId 用户ID
     * @param appointmentDTO 预约信息
     * @return 订单ID
     */
    Long submitAppointment(Long userId, AppointmentDTO appointmentDTO);

    /**
     * 取消预约订单
     * 
     * @param userId 用户ID
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean cancelAppointment(Long userId, Long orderId);

    /**
     * 分页查询订单列表
     * 
     * @param page 分页参数
     * @param userId 用户ID（患者端查询）
     * @param doctorId 医生ID（医生端查询）
     * @param deptName 科室名称
     * @param orderStatus 订单状态
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 分页结果
     */
    Page<Map<String, Object>> getOrderPage(Page<HosAppointmentOrder> page, Long userId, Long doctorId,
                                            String deptName, Integer orderStatus, String startDate, String endDate);

    /**
     * 获取订单详情
     * 
     * @param id 订单ID
     * @return 订单详情
     */
    Map<String, Object> getOrderDetail(Long id);

    /**
     * 强制取消订单（管理员）
     * 
     * @param orderId 订单ID
     * @return 是否成功
     */
    boolean forceCancelOrder(Long orderId);

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

    /**
     * 统计今日预约数量
     * 
     * @return 预约数量
     */
    Long countTodayAppointments();

    /**
     * 统计总预约数量
     * 
     * @return 预约数量
     */
    Long countTotalAppointments();
}
