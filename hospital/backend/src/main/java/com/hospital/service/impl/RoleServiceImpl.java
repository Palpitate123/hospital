package com.hospital.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.entity.Role;
import com.hospital.entity.Permission;
import com.hospital.mapper.RoleMapper;
import com.hospital.mapper.PermissionMapper;
import com.hospital.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    
    @Autowired
    private RoleMapper roleMapper;
    
    @Autowired
    private PermissionMapper permissionMapper;
    
    @Override
    public List<Role> getRolesByUserId(Long userId) {
        return roleMapper.selectByUserId(userId);
    }
    
    @Override
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        return permissionMapper.selectByRoleId(roleId);
    }
    
    @Transactional
    @Override
    public boolean assignRoleToUser(Long userId, Long roleId) {
        // 先移除用户现有的角色
        roleMapper.deleteUserRole(userId);
        // 为用户分配新角色
        roleMapper.insertUserRole(userId, roleId);
        return true;
    }
    
    @Transactional
    @Override
    public boolean assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        // 先移除角色现有的所有权限
        roleMapper.deleteRolePermissions(roleId);
        // 为角色分配新的权限
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long permissionId : permissionIds) {
                roleMapper.insertRolePermission(roleId, permissionId);
            }
        }
        return true;
    }
    
    @Override
    public boolean removeUserRole(Long userId) {
        roleMapper.deleteUserRole(userId);
        return true;
    }
    
    @Override
    public boolean removeRolePermissions(Long roleId) {
        roleMapper.deleteRolePermissions(roleId);
        return true;
    }
}
