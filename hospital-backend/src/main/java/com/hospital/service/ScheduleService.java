package com.hospital.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.dto.ScheduleDTO;
import com.hospital.entity.HosSchedule;

import java.util.Map;

/**
 * 排班服务接口
 * 处理排班管理相关业务
 * 
 * @author 徐凌
 * @version 1.0.0
 */
public interface ScheduleService extends IService<HosSchedule> {

    /**
     * 分页查询排班列表
     * 
     * @param page 分页参数
     * @param deptId 科室ID
     * @param doctorId 医生ID
     * @param doctorName 医生姓名
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param scheduleStatus 排班状态
     * @return 分页结果
     */
    Page<Map<String, Object>> getSchedulePage(Page<HosSchedule> page, Long deptId, Long doctorId,
                                               String doctorName, String startDate, String endDate, Integer scheduleStatus);

    /**
     * 查询医生的排班列表（患者端）
     * 
     * @param doctorId 医生ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 排班列表
     */
    java.util.List<Map<String, Object>> getDoctorSchedules(Long doctorId, String startDate, String endDate);

    /**
     * 获取排班详情
     * 
     * @param id 排班ID
     * @return 排班详情
     */
    Map<String, Object> getScheduleDetail(Long id);

    /**
     * 新增排班
     * 
     * @param scheduleDTO 排班信息
     * @return 是否成功
     */
    boolean addSchedule(ScheduleDTO scheduleDTO);

    /**
     * 编辑排班
     * 
     * @param scheduleDTO 排班信息
     * @return 是否成功
     */
    boolean updateSchedule(ScheduleDTO scheduleDTO);

    /**
     * 删除排班
     * 
     * @param id 排班ID
     * @return 是否成功
     */
    boolean deleteSchedule(Long id);

    /**
     * 发布排班号源
     * 
     * @param id 排班ID
     * @return 是否成功
     */
    boolean publishSchedule(Long id);

    /**
     * 下架排班号源
     * 
     * @param id 排班ID
     * @return 是否成功
     */
    boolean unpublishSchedule(Long id);
}
