package com.hospital.controller;

import com.hospital.entity.Appointment;
import com.hospital.entity.Doctor;
import com.hospital.mapper.DoctorMapper;
import com.hospital.service.AppointmentService;
import com.hospital.utils.Result;
import com.hospital.vo.AppointmentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 预约控制器
 */
@RestController
@RequestMapping("/api/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;
    
    @Autowired
    private DoctorMapper doctorMapper;

    /**
     * 创建预约
     */
    @PostMapping("/create")
    public Result createAppointment(@RequestBody Appointment appointment, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        appointment.setPatientId(userId);
        boolean result = appointmentService.createAppointment(appointment);
        return result ? Result.success() : Result.fail("预约失败");
    }

    /**
     * 获取当前患者的预约列表
     */
    @GetMapping("/patient-list")
    public Result getPatientAppointments(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<AppointmentVO> appointments = appointmentService.getAppointmentsByPatientId(userId);
        return Result.success(appointments);
    }
    
    /**
     * 获取指定患者的预约列表（管理员使用）
     */
    @GetMapping("/patient/{patientId}")
    public Result getPatientAppointments(@PathVariable Long patientId, HttpServletRequest request) {
        String roleCode = (String) request.getAttribute("roleCode");
        if (!"ROLE_ADMIN".equals(roleCode)) {
            return Result.fail("权限不足");
        }
        List<AppointmentVO> appointments = appointmentService.getAppointmentsByPatientId(patientId);
        return Result.success(appointments);
    }

    /**
     * 获取当前医生的预约列表
     */
    @GetMapping("/doctor-list")
    public Result getDoctorAppointments(HttpServletRequest request) {
        String roleCode = (String) request.getAttribute("roleCode");
        if ("ROLE_DOCTOR".equals(roleCode)) {
            Long userId = (Long) request.getAttribute("userId");
            // 从医生表中查询用户对应的医生信息
            Doctor doctor = doctorMapper.selectByUserId(userId);
            if (doctor == null) {
                return Result.fail("医生信息不存在");
            }
            List<AppointmentVO> appointments = appointmentService.getAppointmentsByDoctorId(doctor.getId());
            return Result.success(appointments);
        }
        return Result.fail("权限不足");
    }
    
    /**
     * 获取指定医生的预约列表（管理员使用）
     */
    @GetMapping("/doctor/{doctorId}")
    public Result getDoctorAppointments(@PathVariable Long doctorId, HttpServletRequest request) {
        String roleCode = (String) request.getAttribute("roleCode");
        if (!"ROLE_ADMIN".equals(roleCode)) {
            return Result.fail("权限不足");
        }
        List<AppointmentVO> appointments = appointmentService.getAppointmentsByDoctorId(doctorId);
        return Result.success(appointments);
    }

    /**
     * 取消预约（患者操作）
     */
    @PostMapping("/cancel/{appointmentId}")
    public Result cancelAppointment(@PathVariable Long appointmentId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        boolean result = appointmentService.cancelAppointment(appointmentId, userId);
        return result ? Result.success() : Result.fail("取消失败");
    }
    
    /**
     * 取消预约（管理员操作）
     */
    @PostMapping("/admin/cancel/{appointmentId}")
    public Result adminCancelAppointment(@PathVariable Long appointmentId, 
                                        @RequestParam String reason, 
                                        HttpServletRequest request) {
        String roleCode = (String) request.getAttribute("roleCode");
        if (!"ROLE_ADMIN".equals(roleCode)) {
            return Result.fail("权限不足");
        }
        boolean success = appointmentService.cancelAppointment(appointmentId, reason);
        return success ? Result.success() : Result.fail("取消预约失败");
    }

    /**
     * 更新预约状态（医生操作）
     */
    @PutMapping("/status")
    public Result updateStatus(@RequestBody Map<String, Object> request, HttpServletRequest req) {
        String roleCode = (String) req.getAttribute("roleCode");
        if (!"ROLE_DOCTOR".equals(roleCode) && !"ROLE_ADMIN".equals(roleCode)) {
            return Result.fail("权限不足");
        }

        Long appointmentId = Long.valueOf(request.get("appointmentId").toString());
        Integer status = (Integer) request.get("status");
        boolean result = appointmentService.updateAppointmentStatus(appointmentId, status);
        return result ? Result.success() : Result.fail("更新失败");
    }

    /**
     * 查询医生排班情况
     */
    @GetMapping("/schedule")
    public Result getDoctorSchedule(@RequestParam Long doctorId, @RequestParam String date, 
                                  @RequestParam(required = false) Integer timeSlot) {
        try {
            // 进行正确的日期格式转换
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date appointmentDate = sdf.parse(date);
            List<Appointment> schedule = appointmentService.getDoctorSchedule(doctorId, appointmentDate, timeSlot);
            return Result.success(schedule);
        } catch (ParseException e) {
            return Result.fail("日期格式错误，请使用yyyy-MM-dd格式");
        }
    }
    
    /**
     * 检查时间段是否可用
     */
    @GetMapping("/check-availability")
    public Result checkTimeSlotAvailability(@RequestParam Long doctorId, 
                                          @RequestParam String date, 
                                          @RequestParam Integer timeSlot) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date appointmentDate = sdf.parse(date);
            boolean available = appointmentService.checkTimeSlotAvailability(doctorId, appointmentDate, timeSlot);
            return Result.success(available);
        } catch (ParseException e) {
            return Result.fail("日期格式错误，请使用yyyy-MM-dd格式");
        }
    }
    
    /**
     * 获取医生今日预约数量
     */
    @GetMapping("/today-count/{doctorId}")
    public Result getTodayAppointmentCount(@PathVariable Long doctorId, HttpServletRequest request) {
        String roleCode = (String) request.getAttribute("roleCode");
        if (!"ROLE_DOCTOR".equals(roleCode) && !"ROLE_ADMIN".equals(roleCode)) {
            return Result.fail("权限不足");
        }
        int count = appointmentService.getTodayAppointmentCount(doctorId);
        return Result.success(count);
    }
    
    /**
     * 获取预约列表（支持两种模式：分页查询和获取最近记录）
     */
    @GetMapping("/list")
    public Result getAppointmentList(@RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer pageSize,
                                   @RequestParam(required = false) String patientName,
                                   @RequestParam(required = false) String doctorName,
                                   @RequestParam(required = false) Long departmentId,
                                   @RequestParam(required = false) Integer status,
                                   @RequestParam(required = false, defaultValue = "false") Boolean recent,
                                   HttpServletRequest request) {
        String roleCode = (String) request.getAttribute("roleCode");
        if (!"ROLE_ADMIN".equals(roleCode) && !"ROLE_DOCTOR".equals(roleCode)) {
            return Result.fail("权限不足");
        }
        
        // 处理获取最近记录的情况
        if (recent) {
            List<AppointmentVO> appointments = appointmentService.getRecentAppointments(5); // 获取最近5条记录
            return Result.success(appointments);
        }
        
        // 标准分页查询模式
        Integer actualPage = page != null ? page : 1;
        Integer actualPageSize = pageSize != null ? pageSize : 10;
        List<AppointmentVO> appointments = appointmentService.getAppointmentList(
                actualPage, actualPageSize, patientName, doctorName, departmentId, status);
        return Result.success(appointments);
    }

    /**
     * 获取预约详情（VO格式）
     */
    @GetMapping("/detail/{appointmentId}")
    public Result getAppointmentDetail(@PathVariable Long appointmentId) {
        AppointmentVO appointment = appointmentService.getAppointmentById(appointmentId);
        if (appointment != null) {
            return Result.success(appointment);
        } else {
            return Result.fail("预约不存在");
        }
    }
    
    /**
     * 获取预约详情（Map格式）
     */
    @GetMapping("/detail/map/{appointmentId}")
    public Result getAppointmentDetailMap(@PathVariable Long appointmentId) {
        Map<String, Object> detail = appointmentService.getAppointmentDetail(appointmentId);
        return Result.success(detail);
    }

    /**
     * 获取预约统计（管理员）
     */
    @GetMapping("/stats")
    public Result getAppointmentStats(HttpServletRequest request) {
        String roleCode = (String) request.getAttribute("roleCode");
        if (!"ROLE_ADMIN".equals(roleCode)) {
            return Result.fail("权限不足");
        }
        Map<String, Object> stats = appointmentService.getAppointmentStats();
        return Result.success(stats);
    }
}
