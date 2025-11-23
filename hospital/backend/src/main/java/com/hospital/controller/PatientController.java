package com.hospital.controller;

import com.hospital.entity.Role;
import com.hospital.entity.User;
import com.hospital.service.UserService;
import com.hospital.utils.Result;
import com.hospital.vo.PatientVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 患者控制器
 */
@RestController
@RequestMapping("/api/patient")
public class PatientController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前登录患者的信息
     */
    @GetMapping("/profile")
    public Result getPatientProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getById(userId);
        if (user != null) {
            // 隐藏敏感信息
            User safeUser = new User();
            safeUser.setId(user.getId());
            safeUser.setRealName(user.getRealName());
            safeUser.setGender(user.getGender());
            safeUser.setPhone(user.getPhone());
            safeUser.setEmail(user.getEmail());
            safeUser.setBirthday(user.getBirthday());
            safeUser.setIdCard(user.getIdCard());
            safeUser.setAddress(user.getAddress());
            safeUser.setCreateTime(user.getCreateTime());
            return Result.success(safeUser);
        }
        return Result.fail("患者信息不存在");
    }
    
    /**
     * 获取所有患者列表（管理员功能）
     */
    @GetMapping("/list")
    public Result getAllPatients(HttpServletRequest request) {
        // 验证权限：只有管理员可以查看所有患者
        String roleCode = (String) request.getAttribute("roleCode");
        if (!"ROLE_ADMIN".equals(roleCode)) {
            return Result.fail("权限不足");
        }
        
        List<User> patients = userService.getAllUsers();
        // 过滤出患者角色的用户
        patients = patients.stream()
                .filter(user -> { 
                    List<Role> roles = userService.getUserRoles(user.getId());
                    return roles != null && !roles.isEmpty() && 
                           roles.stream().anyMatch(role -> "ROLE_PATIENT".equals(role.getRoleCode()));
                })
                .collect(java.util.stream.Collectors.toList());
                
        // 转换为VO对象，隐藏敏感信息
        List<PatientVO> patientVOs = new ArrayList<>();
        for (User user : patients) {
            PatientVO vo = new PatientVO();
            vo.setId(user.getId());
            vo.setRealName(user.getRealName());
            vo.setGender(user.getGender());
            vo.setPhone(user.getPhone());
            vo.setEmail(user.getEmail());
            vo.setBirthday(user.getBirthday());
            vo.setAddress(user.getAddress());

            vo.setCreateTime(user.getCreateTime());
            patientVOs.add(vo);
        }
        
        return Result.success(patientVOs);
    }

    /**
     * 更新患者个人信息
     */
    @PutMapping("/profile")
    public Result updatePatientProfile(@RequestBody User userRequest, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        
        // 验证只能更新自己的信息
        User user = userService.getById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        
        // 不允许修改的字段
        userRequest.setId(userId);
        userRequest.setUsername(null);
        userRequest.setPassword(null);
        userRequest.setStatus(null);
        userRequest.setCreateTime(null);
        
        // 更新用户信息
        boolean result = userService.updateUser(userRequest);
        return result ? Result.success() : Result.fail("更新失败");
    }

    /**
     * 修改患者密码
     */
    @PutMapping("/change-password")
    public Result changePassword(@RequestBody Map<String, String> request, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        
        // 验证参数
        if (oldPassword == null || oldPassword.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            return Result.fail("请输入旧密码和新密码");
        }
        
        // 验证新密码强度
        if (newPassword.length() < 6) {
            return Result.fail("新密码长度至少为6个字符");
        }
        
        // 验证旧密码是否正确
        User user = userService.getById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        
        if (!oldPassword.equals(user.getPassword())) {
            return Result.fail("旧密码错误");
        }
        
        // 修改密码
        boolean result = userService.resetPassword(userId, newPassword);
        return result ? Result.success() : Result.fail("密码修改失败");
    }

    /**
     * 更新患者联系方式
     */
    @PutMapping("/contact")
    public Result updateContact(@RequestBody Map<String, String> request, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        String phone = request.get("phone");
        String email = request.get("email");
        
        // 获取当前用户信息
        User user = userService.getById(userId);
        if (user == null) {
            return Result.fail("用户不存在");
        }
        
        // 更新手机号（如果提供）
        if (phone != null && !phone.isEmpty()) {
            // 验证手机号格式
            if (!phone.matches("^1[3-9]\\d{9}$")) {
                return Result.fail("手机号格式不正确");
            }
            user.setPhone(phone);
        }
        
        // 更新邮箱（如果提供）
        if (email != null && !email.isEmpty()) {
            // 验证邮箱格式
            if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                return Result.fail("邮箱格式不正确");
            }
            user.setEmail(email);
        }
        
        // 保存更新
        boolean result = userService.updateUser(user);
        return result ? Result.success() : Result.fail("联系方式更新失败");
    }
    
    /**
     * 根据ID获取患者信息（供医生或管理员使用）
     */
    @GetMapping("/info/{patientId}")
    public Result getPatientInfo(@PathVariable Long patientId, HttpServletRequest request) {
        // 验证权限：只有医生或管理员可以查看患者信息
        String roleCode = (String) request.getAttribute("roleCode");
        if (!"ROLE_DOCTOR".equals(roleCode) && !"ROLE_ADMIN".equals(roleCode)) {
            return Result.fail("权限不足");
        }
        
        User user = userService.getById(patientId);
        if (user == null) {
            return Result.fail("患者不存在");
        }
        
        // 转换为VO对象，隐藏敏感信息
        PatientVO patientVO = new PatientVO();
        patientVO.setId(user.getId());
        patientVO.setRealName(user.getRealName());
        patientVO.setGender(user.getGender());
        patientVO.setPhone(user.getPhone());
        patientVO.setEmail(user.getEmail());
        patientVO.setBirthday(user.getBirthday());
        patientVO.setAddress(user.getAddress());

        patientVO.setCreateTime(user.getCreateTime());
        
        return Result.success(patientVO);
    }
}
