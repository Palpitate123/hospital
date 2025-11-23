package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.Appointment;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 预约Mapper接口
 */
public interface AppointmentMapper extends BaseMapper<Appointment> {
    /**
     * 根据患者ID查询预约列表
     */
    List<Appointment> selectByPatientId(@Param("patientId") Long patientId);

    /**
     * 根据医生ID查询预约列表
     */
    List<Appointment> selectByDoctorId(@Param("doctorId") Long doctorId);

    /**
     * 查询医生某天某个时间段的预约情况
     */
    Integer countByDoctorAndTime(@Param("doctorId") Long doctorId, @Param("appointmentDate") String date, @Param("appointmentTime") String time);
    
    /**
     * 查询医生今日预约数量
     */
    int selectTodayAppointmentCount(@Param("doctorId") Long doctorId);
    
    /**
     * 查询医生某天某个时间段的预约列表
     */
    List<Appointment> selectByDoctorDateSlot(@Param("doctorId") Long doctorId, 
                                           @Param("appointmentDate") Date date, 
                                           @Param("timeSlot") Integer timeSlot);
    
    /**
     * 根据状态统计预约数量
     */
    int selectCountByStatus(@Param("status") Integer status);
}
