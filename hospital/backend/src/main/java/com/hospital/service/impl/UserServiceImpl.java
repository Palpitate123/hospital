package com.hospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.entity.Doctor;
import com.hospital.entity.Role;
import com.hospital.entity.User;
import com.hospital.exception.GlobalException;
import com.hospital.mapper.DoctorMapper;
import com.hospital.mapper.RoleMapper;
import com.hospital.mapper.UserMapper;
import com.hospital.service.RoleService;
import com.hospital.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private DoctorMapper doctorMapper;
    
    @Autowired
    private RoleService roleService;

    // @Autowired
    // private BCryptPasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Transactional
    @Override
    public boolean register(User user, Long roleId) {
        // 验证必填字段
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new GlobalException("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new GlobalException("密码不能为空");
        }
        if (user.getRealName() == null || user.getRealName().trim().isEmpty()) {
            throw new GlobalException("真实姓名不能为空");
        }
        
        // 验证用户名长度
        if (user.getUsername().length() < 4 || user.getUsername().length() > 20) {
            throw new GlobalException("用户名长度必须在4-20个字符之间");
        }
        
        // 验证密码强度
        if (user.getPassword().length() < 6) {
            throw new GlobalException("密码长度至少为6个字符");
        }

        // 检查用户名是否已存在
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            throw new GlobalException("用户名已存在");
        }

        // 检查手机号是否已存在
        if (user.getPhone() != null && !user.getPhone().trim().isEmpty()) {
            // 简单的手机号格式验证
            if (!user.getPhone().matches("^1[3-9]\\d{9}$")) {
                throw new GlobalException("手机号格式不正确");
            }
            if (userMapper.selectByPhone(user.getPhone()) != null) {
                throw new GlobalException("手机号已被注册");
            }
        }

        // 检查邮箱是否已存在
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            // 简单的邮箱格式验证
            if (!user.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                throw new GlobalException("邮箱格式不正确");
            }
            if (userMapper.selectByEmail(user.getEmail()) != null) {
                throw new GlobalException("邮箱已被注册");
            }
        }

        // 明文存储密码，不进行加密
        // 注意：这里直接使用用户输入的密码
        user.setStatus(1); // 默认启用
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        // 保存用户
        boolean result = save(user);
        if (result) {
            // 分配角色
            roleMapper.insertUserRole(user.getId(), roleId);
        }
        return result;
    }

    @Transactional
    @Override
    public boolean registerDoctor(User user, Long deptId, String title) {
        // 医生注册，先注册为普通用户
        boolean result = register(user, 2L); // 2L 是医生角色ID
        if (result) {
            // 创建医生信息
            Doctor doctor = new Doctor();
            doctor.setUserId(user.getId());
            doctor.setDeptId(deptId);
            doctor.setTitle(title);
            doctor.setConsultationFee(new BigDecimal(50)); // 默认挂号费
            doctor.setDailyQuota(20); // 默认每日限额
            doctor.setStatus(1); // 默认出诊
            doctorMapper.insert(doctor);
        }
        return result;
    }

    @Override
    public boolean updateUser(User user) {
        // 不允许修改密码和用户名
        user.setPassword(null);
        user.setUsername(null);
        return updateById(user);
    }

    @Override
    public boolean resetPassword(Long userId, String newPassword) {
        User user = new User();
        user.setId(userId);
        user.setPassword(newPassword); // 明文存储新密码
        return updateById(user);
    }
    
    @Override
    public List<Role> getUserRoles(Long userId) {
        return roleService.getRolesByUserId(userId);
    }
    
    @Override
    public List<User> getAllUsers() {
        return userMapper.selectList(null);
    }
}
