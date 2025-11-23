package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.entity.Role;
import com.hospital.entity.User;
import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    /**
     * 根据用户名查询用户
     */
    User findByUsername(String username);

    /**
     * 用户注册
     */
    boolean register(User user, Long roleId);

    /**
     * 医生注册
     */
    boolean registerDoctor(User user, Long deptId, String title);

    /**
     * 更新用户信息
     */
    boolean updateUser(User user);

    /**
     * 重置密码
     */
    boolean resetPassword(Long userId, String newPassword);
    
    /**
     * 获取用户角色列表
     */
    List<Role> getUserRoles(Long userId);
    
    /**
     * 获取所有用户
     */
    List<User> getAllUsers();
}
