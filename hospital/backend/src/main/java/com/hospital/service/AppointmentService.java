package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.entity.Appointment;
import com.hospital.vo.AppointmentVO;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 预约服务接口
 */
public interface AppointmentService extends IService<Appointment> {

    /**
     * 根据患者ID查询预约列表
     */
    List<AppointmentVO> getAppointmentsByPatientId(Long patientId);

    /**
     * 根据医生ID查询预约列表
     */
    List<AppointmentVO> getAppointmentsByDoctorId(Long doctorId);

    /**
     * 创建新预约
     */
    boolean createAppointment(Appointment appointment);

    /**
     * 取消预约
     */
    boolean cancelAppointment(Long appointmentId, Long userId);
    
    /**
     * 取消预约（带原因）
     */
    boolean cancelAppointment(Long appointmentId, String reason);

    /**
     * 更新预约状态
     */
    boolean updateAppointmentStatus(Long appointmentId, Integer status);

    /**
     * 查询医生某天某个时间段的预约情况
     */
    List<Appointment> getDoctorSchedule(Long doctorId, Date date, Integer timeSlot);

    /**
     * 统计预约数据
     */
    Map<String, Object> getAppointmentStats();

    /**
     * 查询预约详情
     */
    Map<String, Object> getAppointmentDetail(Long appointmentId);
    
    /**
     * 根据ID获取预约详情
     */
    AppointmentVO getAppointmentById(Long appointmentId);
    
    /**
     * 检查医生指定时间段是否有预约
     */
    boolean checkTimeSlotAvailability(Long doctorId, Date date, Integer timeSlot);
    
    /**
     * 获取医生今日预约数量
     */
    int getTodayAppointmentCount(Long doctorId);
    
    /**
     * 分页查询预约列表
     */
    List<AppointmentVO> getAppointmentList(Integer page, Integer pageSize, String patientName, 
                                       String doctorName, Long departmentId, Integer status);
    
    /**
     * 获取最近的预约记录（用于Dashboard显示）
     */
    List<AppointmentVO> getRecentAppointments(Integer limit);
}
