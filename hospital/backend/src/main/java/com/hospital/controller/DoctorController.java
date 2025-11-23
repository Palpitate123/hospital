package com.hospital.controller;

import com.hospital.entity.Doctor;
import com.hospital.service.DoctorService;
import com.hospital.utils.Result;
import com.hospital.vo.DoctorVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 医生控制器
 */
@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    /**
     * 根据科室ID查询医生列表（基础信息）
     */
    @GetMapping("/by-dept/{deptId}")
    public Result getDoctorsByDept(@PathVariable Long deptId) {
        List<Doctor> doctors = doctorService.getDoctorsByDeptId(deptId);
        return Result.success(doctors);
    }
    
    /**
     * 根据科室ID查询医生列表（详细信息）
     */
    @GetMapping("/by-dept/vo/{deptId}")
    public Result getDoctorVOsByDept(@PathVariable Long deptId) {
        List<DoctorVO> doctors = doctorService.getDoctorVOsByDeptId(deptId);
        return Result.success(doctors);
    }
    
    /**
     * 获取所有医生列表（详细信息）
     */
    @GetMapping("/all")
    public Result getAllDoctors() {
        List<DoctorVO> doctors = doctorService.getAllDoctorVOs();
        return Result.success(doctors);
    }
    
    /**
     * 搜索医生
     */
    @GetMapping("/search")
    public Result searchDoctors(@RequestParam(required = false, defaultValue = "") String keyword) {
        if (keyword == null || keyword.trim().length() < 2) {
            return Result.fail("搜索关键词至少需要2个字符");
        }
        List<DoctorVO> doctors = doctorService.searchDoctors(keyword);
        return Result.success(doctors);
    }

    /**
     * 获取医生详情（Map格式）
     */
    @GetMapping("/detail/{doctorId}")
    public Result getDoctorDetail(@PathVariable Long doctorId) {
        Map<String, Object> detail = doctorService.getDoctorDetail(doctorId);
        return Result.success(detail);
    }
    
    /**
     * 获取医生详情（VO格式）
     */
    @GetMapping("/detail/vo/{doctorId}")
    public Result getDoctorVO(@PathVariable Long doctorId) {
        DoctorVO doctor = doctorService.getDoctorVOById(doctorId);
        if (doctor != null) {
            return Result.success(doctor);
        } else {
            return Result.fail("医生不存在");
        }
    }

    /**
     * 更新医生信息
     */
    @PutMapping("/update")
    public Result updateDoctor(@RequestBody Doctor doctor, HttpServletRequest request) {
        // 验证是否是医生本人或管理员
        Long userId = (Long) request.getAttribute("userId");
        Doctor currentDoctor = doctorService.getDoctorByUserId(userId);
        
        // 简化处理：如果不是管理员，只能更新自己的信息
        String roleCode = (String) request.getAttribute("roleCode");
        if (!"ROLE_ADMIN".equals(roleCode) && currentDoctor != null) {
            doctor.setId(currentDoctor.getId());
        }
        
        boolean result = doctorService.updateDoctor(doctor);
        return result ? Result.success() : Result.fail("更新失败");
    }

    /**
     * 更新医生状态
     */
    @PutMapping("/status")
    public Result updateStatus(@RequestBody Map<String, Object> request, HttpServletRequest req) {
        // 只有管理员可以修改医生状态
        String roleCode = (String) req.getAttribute("roleCode");
        if (!"ROLE_ADMIN".equals(roleCode)) {
            return Result.fail("权限不足");
        }
        
        Long doctorId = Long.valueOf(request.get("doctorId").toString());
        Integer status = (Integer) request.get("status");
        
        boolean result = doctorService.updateDoctorStatus(doctorId, status);
        return result ? Result.success() : Result.fail("更新失败");
    }

    /**
     * 获取当前登录医生的信息（Map格式）
     */
    @GetMapping("/current")
    public Result getCurrentDoctor(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Doctor doctor = doctorService.getDoctorByUserId(userId);
        if (doctor == null) {
            return Result.fail("不是医生用户");
        }
        return Result.success(doctorService.getDoctorDetail(doctor.getId()));
    }
    
    /**
     * 获取当前登录医生的信息（VO格式）
     */
    @GetMapping("/current/vo")
    public Result getCurrentDoctorVO(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        DoctorVO doctor = doctorService.getDoctorVOByUserId(userId);
        if (doctor == null) {
            return Result.fail("不是医生用户");
        }
        return Result.success(doctor);
    }
}
