package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.Permission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 权限Mapper接口
 */
public interface PermissionMapper extends BaseMapper<Permission> {
    /**
     * 根据角色ID查询权限列表
     */
    List<Permission> selectByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据用户ID查询权限列表
     */
    List<Permission> selectByUserId(@Param("userId") Long userId);
}
