package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.entity.Appointment;
import com.hospital.entity.Doctor;
import com.hospital.entity.User;
import com.hospital.exception.GlobalException;
import com.hospital.mapper.AppointmentMapper;
import com.hospital.mapper.DoctorMapper;
import com.hospital.mapper.UserMapper;
import com.hospital.service.AppointmentService;
import com.hospital.vo.AppointmentVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 预约服务实现类
 * 负责处理医院预约相关业务逻辑，包括预约创建、取消、状态更新等功能
 */
@Service
public class AppointmentServiceImpl extends ServiceImpl<AppointmentMapper, Appointment> implements AppointmentService {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据患者ID获取预约列表
     * @param patientId 患者ID
     * @return 预约信息列表
     */
    @Override
    public List<AppointmentVO> getAppointmentsByPatientId(Long patientId) {
        logger.info("获取患者ID: {} 的预约列表", patientId);
        List<Appointment> appointments = appointmentMapper.selectByPatientId(patientId);
        logger.info("患者ID: {} 共有 {} 条预约记录", patientId, appointments != null ? appointments.size() : 0);
        return convertToVOList(appointments);
    }

    /**
     * 根据医生ID获取预约列表
     * @param doctorId 医生ID
     * @return 预约信息列表
     */
    @Override
    public List<AppointmentVO> getAppointmentsByDoctorId(Long doctorId) {
        logger.info("获取医生ID: {} 的预约列表", doctorId);
        List<Appointment> appointments = appointmentMapper.selectByDoctorId(doctorId);
        logger.info("医生ID: {} 共有 {} 条预约记录", doctorId, appointments != null ? appointments.size() : 0);
        return convertToVOList(appointments);
    }

    /**
     * 创建预约
     * @param appointment 预约信息
     * @return 是否创建成功
     */
    @Transactional
    @Override
    public boolean createAppointment(Appointment appointment) {
        logger.info("开始创建预约：患者ID={}, 医生ID={}, 预约日期={}, 时间段={}", 
                appointment.getPatientId(), appointment.getDoctorId(), 
                appointment.getAppointmentDate(), appointment.getAppointmentTime());
        
        try {
            // 参数验证
            if (appointment.getDoctorId() == null || appointment.getPatientId() == null) {
                logger.warn("预约创建失败：医生ID和患者ID不能为空");
                throw new GlobalException("医生ID和患者ID不能为空");
            }
            
            if (appointment.getAppointmentDate() == null || appointment.getAppointmentTime() == null) {
                logger.warn("预约创建失败：预约日期和时间不能为空");
                throw new GlobalException("预约日期和时间不能为空");
            }
            
            // 检查预约日期是否合理（不能是过去的时间）
            Date now = new Date();
            if (appointment.getAppointmentDate().before(now)) {
                logger.warn("预约创建失败：不能预约过去的时间");
                throw new GlobalException("不能预约过去的时间");
            }
            
            // 检查医生是否存在且出诊
            Doctor doctor = doctorMapper.selectById(appointment.getDoctorId());
            if (doctor == null || doctor.getStatus() == 0) {
                logger.warn("预约创建失败：医生ID={} 不存在或未出诊", appointment.getDoctorId());
                throw new GlobalException("医生不存在或未出诊");
            }

            // 检查当天预约数量是否超过限额
            int todayCount = appointmentMapper.selectTodayAppointmentCount(appointment.getDoctorId());
            if (todayCount >= doctor.getDailyQuota()) {
                logger.warn("预约创建失败：医生ID={} 今日预约已满，当前预约数={}，限额={}", 
                        appointment.getDoctorId(), todayCount, doctor.getDailyQuota());
                throw new GlobalException("该医生今日预约已满");
            }

            // 检查该时间段是否已被预约
            List<Appointment> existing = appointmentMapper.selectByDoctorDateSlot(
                    appointment.getDoctorId(), appointment.getAppointmentDate(), appointment.getAppointmentTime());
            if (!existing.isEmpty()) {
                logger.warn("预约创建失败：该时间段已被预约");
                throw new GlobalException("该时间段已被预约");
            }
            
            // 检查患者在同一天是否已经预约该医生
            QueryWrapper<Appointment> patientWrapper = new QueryWrapper<>();
            patientWrapper.eq("patient_id", appointment.getPatientId())
                        .eq("doctor_id", appointment.getDoctorId())
                        .eq("appointment_date", appointment.getAppointmentDate())
                        .in("status", 1, 2, 3); // 待就诊、已就诊、已完成
            if (count(patientWrapper) > 0) {
                logger.warn("预约创建失败：患者ID={} 已经在同一天预约了医生ID={}", 
                        appointment.getPatientId(), appointment.getDoctorId());
                throw new GlobalException("您已经在同一天预约了该医生");
            }

            // 设置预约状态和创建时间
            appointment.setStatus(1); // 待就诊
            appointment.setCreateTime(new Date());
            appointment.setUpdateTime(new Date());

            boolean result = save(appointment);
            if (result) {
                logger.info("预约创建成功：预约ID={}", appointment.getId());
            } else {
                logger.error("预约创建失败：保存到数据库时出错");
            }
            return result;
        } catch (GlobalException e) {
            logger.error("预约创建失败：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("预约创建过程中发生异常", e);
            throw new GlobalException("预约创建失败：系统异常");
        }
    }

    /**
     * 患者取消预约
     * @param appointmentId 预约ID
     * @param userId 操作用户ID
     * @return 是否取消成功
     */
    /**
     * 患者取消预约
     * @param appointmentId 预约ID
     * @param userId 患者ID
     * @return 是否取消成功
     */
    @Override
    @Transactional
    public boolean cancelAppointment(Long appointmentId, Long userId) {
        logger.info("开始取消预约：预约ID={}, 操作用户ID={}", appointmentId, userId);
        
        try {
            Appointment appointment = getById(appointmentId);
            if (appointment == null) {
                logger.warn("取消预约失败：预约ID={} 不存在", appointmentId);
                throw new GlobalException("预约不存在");
            }

            // 验证是否是患者本人
            if (!appointment.getPatientId().equals(userId)) {
                logger.warn("取消预约失败：用户ID={} 无权取消预约ID={}", userId, appointmentId);
                throw new GlobalException("无权取消该预约");
            }

            return cancelAppointmentInternal(appointment, "患者自助取消");
        } catch (GlobalException e) {
            logger.error("预约取消失败：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("预约取消过程中发生异常", e);
            throw new GlobalException("预约取消失败：系统异常");
        }
    }
    
    /**
     * 管理员取消预约
     * @param appointmentId 预约ID
     * @param reason 取消原因
     * @return 是否取消成功
     */
    @Override
    @Transactional
    public boolean cancelAppointment(Long appointmentId, String reason) {
        logger.info("管理员取消预约：预约ID={}, 原因={}", appointmentId, reason);
        
        try {
            Appointment appointment = getById(appointmentId);
            if (appointment == null) {
                logger.warn("管理员取消预约失败：预约ID={} 不存在", appointmentId);
                throw new GlobalException("预约不存在");
            }

            return cancelAppointmentInternal(appointment, reason);
        } catch (GlobalException e) {
            logger.error("管理员取消预约失败：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("管理员取消预约过程中发生异常", e);
            throw new GlobalException("预约取消失败：系统异常");
        }
    }
    
    /**
     * 内部取消预约方法
     * @param appointment 预约对象
     * @param reason 取消原因
     * @return 是否取消成功
     */
    private boolean cancelAppointmentInternal(Appointment appointment, String reason) {
        // 只能取消未就诊的预约
        if (appointment.getStatus() != 1) {
            logger.warn("取消预约失败：预约ID={} 状态为{}，只能取消待就诊的预约", 
                    appointment.getId(), appointment.getStatus());
            throw new GlobalException("只能取消待就诊的预约");
        }

        // 更新状态为已取消
        appointment.setStatus(4);
        appointment.setUpdateTime(new Date());
        
        boolean result = updateById(appointment);
        if (result) {
            logger.info("预约取消成功：预约ID={}, 原因={}", appointment.getId(), reason);
        } else {
            logger.error("预约取消失败：更新数据库时出错");
        }
        return result;
    }

    /**
     * 更新预约状态
     * @param appointmentId 预约ID
     * @param status 新状态
     * @return 是否更新成功
     */
    @Override
    public boolean updateAppointmentStatus(Long appointmentId, Integer status) {
        logger.info("开始更新预约状态：预约ID={}, 目标状态={}", appointmentId, status);
        
        try {
            Appointment appointment = getById(appointmentId);
            if (appointment == null) {
                logger.warn("更新预约状态失败：预约ID={} 不存在", appointmentId);
                throw new GlobalException("预约不存在");
            }
            
            // 验证状态转换的合理性
            Integer currentStatus = appointment.getStatus();
            if (!isValidStatusTransition(currentStatus, status)) {
                logger.warn("更新预约状态失败：无效的状态转换，从状态{}转换到状态{}", currentStatus, status);
                throw new GlobalException("无效的状态转换");
            }

            appointment.setStatus(status);
            appointment.setUpdateTime(new Date());
            boolean result = updateById(appointment);
            
            if (result) {
                logger.info("预约状态更新成功：预约ID={}, 从状态{}转换到状态{}", 
                        appointmentId, currentStatus, status);
            } else {
                logger.error("预约状态更新失败：更新数据库时出错");
            }
            return result;
        } catch (GlobalException e) {
            logger.error("更新预约状态失败：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("更新预约状态过程中发生异常", e);
            throw new GlobalException("预约状态更新失败：系统异常");
        }
    }
    
    /**
     * 验证状态转换是否有效
     * @param currentStatus 当前状态
     * @param newStatus 目标状态
     * @return 是否有效
     */
    private boolean isValidStatusTransition(Integer currentStatus, Integer newStatus) {
        // 已取消(4)的预约不能再转换状态
        if (currentStatus == 4) {
            return false;
        }
        
        // 待就诊(1)可以转换为已就诊(3)、已取消(4)
        if (currentStatus == 1) {
            return newStatus == 3 || newStatus == 4;
        }
        
        // 已就诊(3)可以转换为已完成(2)
        if (currentStatus == 3) {
            return newStatus == 2;
        }
        
        // 已完成(2)不能再转换状态
        if (currentStatus == 2) {
            return false;
        }
        
        return false;
    }

    /**
     * 获取医生日程安排
     * @param doctorId 医生ID
     * @param date 日期
     * @param timeSlot 时间段
     * @return 预约列表
     */
    @Override
    public List<Appointment> getDoctorSchedule(Long doctorId, Date date, Integer timeSlot) {
        logger.info("获取医生日程：医生ID={}, 日期={}, 时间段={}", doctorId, date, timeSlot);
        return appointmentMapper.selectByDoctorDateSlot(doctorId, date, timeSlot);
    }

    /**
     * 获取预约统计信息
     * @return 统计数据
     */
    @Override
    public Map<String, Object> getAppointmentStats() {
        logger.info("获取预约统计信息");
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", appointmentMapper.selectCount(null));
        stats.put("pending", appointmentMapper.selectCountByStatus(1));
        stats.put("completed", appointmentMapper.selectCountByStatus(2));
        stats.put("canceled", appointmentMapper.selectCountByStatus(4));
        logger.info("预约统计完成：总预约数={}, 待就诊={}, 已完成={}, 已取消={}",
                stats.get("total"), stats.get("pending"), stats.get("completed"), stats.get("canceled"));
        return stats;
    }

    /**
     * 获取预约详情
     * @param appointmentId 预约ID
     * @return 预约详情信息
     */
    @Override
    public Map<String, Object> getAppointmentDetail(Long appointmentId) {
        logger.info("获取预约详情：预约ID={}", appointmentId);
        
        try {
            Appointment appointment = getById(appointmentId);
            if (appointment == null) {
                logger.warn("获取预约详情失败：预约ID={} 不存在", appointmentId);
                throw new GlobalException("预约不存在");
            }

            Map<String, Object> detail = new HashMap<>();
            detail.put("appointment", appointment);

            // 获取患者信息
            User patient = userMapper.selectById(appointment.getPatientId());
            if (patient != null) {
                // 隐藏敏感信息
                User patientInfo = new User();
                patientInfo.setRealName(patient.getRealName());
                patientInfo.setGender(patient.getGender());
                patientInfo.setPhone(patient.getPhone());
                detail.put("patient", patientInfo);
            }

            // 获取医生信息
            Doctor doctor = doctorMapper.selectById(appointment.getDoctorId());
            if (doctor != null) {
                detail.put("doctor", doctor);
                // 获取医生用户信息
                User doctorUser = userMapper.selectById(doctor.getUserId());
                if (doctorUser != null) {
                    User doctorInfo = new User();
                    doctorInfo.setRealName(doctorUser.getRealName());
                    detail.put("doctorUser", doctorInfo);
                }
            }

            logger.info("获取预约详情成功：预约ID={}", appointmentId);
            return detail;
        } catch (GlobalException e) {
            logger.error("获取预约详情失败：{}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("获取预约详情过程中发生异常", e);
            throw new GlobalException("获取预约详情失败：系统异常");
        }
    }
    
    /**
     * 根据ID获取预约信息
     * @param appointmentId 预约ID
     * @return 预约VO对象
     */
    @Override
    public AppointmentVO getAppointmentById(Long appointmentId) {
        logger.info("根据ID获取预约信息：预约ID={}", appointmentId);
        Appointment appointment = getById(appointmentId);
        if (appointment == null) {
            logger.info("预约ID={} 不存在", appointmentId);
            return null;
        }
        
        return convertToVO(appointment);
    }
    
    /**
     * 检查时间段是否可用
     * @param doctorId 医生ID
     * @param date 日期
     * @param timeSlot 时间段
     * @return 是否可用
     */
    @Override
    public boolean checkTimeSlotAvailability(Long doctorId, Date date, Integer timeSlot) {
        logger.info("检查时间段可用性：医生ID={}, 日期={}, 时间段={}", doctorId, date, timeSlot);
        List<Appointment> appointments = appointmentMapper.selectByDoctorDateSlot(doctorId, date, timeSlot);
        boolean available = appointments == null || appointments.isEmpty();
        logger.info("时间段可用性检查结果：{}", available ? "可用" : "已被预约");
        return available;
    }
    
    /**
     * 获取医生今日预约数量
     * @param doctorId 医生ID
     * @return 今日预约数量
     */
    @Override
    public int getTodayAppointmentCount(Long doctorId) {
        logger.info("获取医生今日预约数量：医生ID={}", doctorId);
        return appointmentMapper.selectTodayAppointmentCount(doctorId);
    }
    
    /**
     * 分页获取预约列表，支持多条件查询
     * @param page 页码
     * @param pageSize 每页大小
     * @param patientName 患者姓名
     * @param doctorName 医生姓名
     * @param departmentId 科室ID
     * @param status 预约状态
     * @return 预约列表
     */
    @Override
    public List<AppointmentVO> getAppointmentList(Integer page, Integer pageSize, String patientName, 
                                         String doctorName, Long departmentId, Integer status) {
        logger.info("分页查询预约列表：页码={}, 每页大小={}, 患者姓名={}, 医生姓名={}, 科室ID={}, 状态={}", 
                page, pageSize, patientName, doctorName, departmentId, status);
        
        try {
            // 使用MyBatis-Plus的分页对象
            Page<Appointment> pageObj = new Page<>(page, pageSize);
            
            QueryWrapper<Appointment> wrapper = new QueryWrapper<>();
            
            // 构建查询条件
            if (patientName != null && !patientName.isEmpty()) {
                // 通过患者名称关联查询
                wrapper.apply("EXISTS (SELECT 1 FROM user p WHERE p.id = patient_id AND p.real_name LIKE CONCAT('%', {0}, '%'))", patientName);
            }
            
            if (doctorName != null && !doctorName.isEmpty()) {
                // 通过医生名称关联查询
                wrapper.apply("EXISTS (SELECT 1 FROM doctor d WHERE d.id = doctor_id AND d.name LIKE CONCAT('%', {0}, '%'))", doctorName);
            }
            
            if (departmentId != null) {
                // 通过科室ID关联查询
                wrapper.apply("EXISTS (SELECT 1 FROM doctor d WHERE d.id = doctor_id AND d.department_id = {0})", departmentId);
            }
            
            if (status != null) {
                wrapper.eq("status", status);
            }
            
            // 排序
            wrapper.orderByDesc("create_time");
            
            // 执行分页查询
            Page<Appointment> resultPage = page(pageObj, wrapper);
            List<Appointment> appointments = resultPage.getRecords();
            
            logger.info("分页查询完成：共查询到 {} 条记录", appointments.size());
            return convertToVOList(appointments);
        } catch (Exception e) {
            logger.error("分页查询预约列表过程中发生异常", e);
            throw new GlobalException("查询预约列表失败：系统异常");
        }
    }
    
    @Override
    public List<AppointmentVO> getRecentAppointments(Integer limit) {
        logger.info("获取最近的预约记录，限制数量：{}", limit);
        
        // 参数校验
        if (limit == null || limit < 1) {
            limit = 5; // 默认获取最近5条
        }
        
        // 构建查询条件，按创建时间倒序排列，限制数量
        QueryWrapper<Appointment> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time")
                   .last("LIMIT " + limit);
        
        // 查询最近的预约记录
        List<Appointment> appointments = this.list(queryWrapper);
        
        logger.info("查询到 {} 条最近预约记录", appointments != null ? appointments.size() : 0);
        
        // 转换为VO对象
        return convertToVOList(appointments);
    }
    
    /**
     * 将Appointment列表转换为AppointmentVO列表
     */
    private List<AppointmentVO> convertToVOList(List<Appointment> appointments) {
        List<AppointmentVO> voList = new ArrayList<>();
        if (appointments != null && !appointments.isEmpty()) {
            for (Appointment appointment : appointments) {
                voList.add(convertToVO(appointment));
            }
        }
        return voList;
    }
    
    /**
     * 将Appointment转换为AppointmentVO
     */
    private AppointmentVO convertToVO(Appointment appointment) {
        AppointmentVO vo = new AppointmentVO();
        
        // 设置基础信息
        vo.setId(appointment.getId());
        vo.setPatientId(appointment.getPatientId());
        vo.setDoctorId(appointment.getDoctorId());
        vo.setAppointmentDate(appointment.getAppointmentDate());
        vo.setTimeSlot(appointment.getAppointmentTime());
        vo.setSymptomDescription(appointment.getSymptoms());
        vo.setStatus(appointment.getStatus());
        vo.setCreateTime(appointment.getCreateTime());
        vo.setUpdateTime(appointment.getUpdateTime());
        
        // 设置时间段文本
        Integer timeSlot = appointment.getAppointmentTime();
        vo.setTimeSlotText(getTimeSlotText(timeSlot));
        
        // 设置状态文本
        vo.setStatusText(getStatusText(appointment.getStatus()));
        
        // 查询并设置患者信息
        User patient = userMapper.selectById(appointment.getPatientId());
        if (patient != null) {
            vo.setPatientName(patient.getRealName());
            vo.setPatientPhone(patient.getPhone());
            vo.setPatientGender(patient.getGender() != null ? patient.getGender().toString() : null);
            // 计算年龄
            if (patient.getBirthday() != null) {
                Calendar now = Calendar.getInstance();
                Calendar birth = Calendar.getInstance();
                birth.setTime(patient.getBirthday());
                int age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
                if (now.get(Calendar.MONTH) < birth.get(Calendar.MONTH) ||
                    (now.get(Calendar.MONTH) == birth.get(Calendar.MONTH) && now.get(Calendar.DAY_OF_MONTH) < birth.get(Calendar.DAY_OF_MONTH))) {
                    age--;
                }
                vo.setPatientAge(age);
            }
        }
        
        // 查询并设置医生信息
        Doctor doctor = doctorMapper.selectById(appointment.getDoctorId());
        if (doctor != null) {
            // 查询医生用户信息获取姓名
            User doctorUser = userMapper.selectById(doctor.getUserId());
            if (doctorUser != null) {
                vo.setDoctorName(doctorUser.getRealName());
            }
            vo.setDoctorTitle(doctor.getTitle());
            vo.setDepartmentId(doctor.getDeptId());
        }
        
        return vo;
    }
    
    /**
     * 获取时间段文本
     */
    private String getTimeSlotText(Integer timeSlot) {
        switch (timeSlot) {
            case 1: return "上午 08:00-09:00";
            case 2: return "上午 09:00-10:00";
            case 3: return "上午 10:00-11:00";
            case 4: return "下午 14:00-15:00";
            case 5: return "下午 15:00-16:00";
            case 6: return "下午 16:00-17:00";
            default: return "未知时间段";
        }
    }
    
    /**
     * 获取状态文本
     */
    private String getStatusText(Integer status) {
        switch (status) {
            case 1: return "待就诊";
            case 2: return "已完成";
            case 3: return "已就诊";
            case 4: return "已取消";
            default: return "未知状态";
        }
    }
}
