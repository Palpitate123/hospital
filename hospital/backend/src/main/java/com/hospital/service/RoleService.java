package com.hospital.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.entity.Role;
import com.hospital.entity.Permission;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService extends IService<Role> {
    
    /**
     * 根据用户ID查询角色列表
     */
    List<Role> getRolesByUserId(Long userId);
    
    /**
     * 根据角色ID查询权限列表
     */
    List<Permission> getPermissionsByRoleId(Long roleId);
    
    /**
     * 为用户分配角色
     */
    boolean assignRoleToUser(Long userId, Long roleId);
    
    /**
     * 为角色分配权限
     */
    boolean assignPermissionsToRole(Long roleId, List<Long> permissionIds);
    
    /**
     * 移除用户角色
     */
    boolean removeUserRole(Long userId);
    
    /**
     * 移除角色权限
     */
    boolean removeRolePermissions(Long roleId);
}
