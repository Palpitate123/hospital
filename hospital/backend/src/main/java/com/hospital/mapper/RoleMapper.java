package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色Mapper接口
 */
public interface RoleMapper extends BaseMapper<Role> {
    /**
     * 根据用户ID查询角色列表
     */
    List<Role> selectByUserId(@Param("userId") Long userId);

    /**
     * 为用户分配角色
     */
    void insertUserRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 移除用户角色
     */
    void deleteUserRole(@Param("userId") Long userId);
    
    /**
     * 移除角色权限
     */
    void deleteRolePermissions(@Param("roleId") Long roleId);
    
    /**
     * 为角色分配权限
     */
    void insertRolePermission(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
}
