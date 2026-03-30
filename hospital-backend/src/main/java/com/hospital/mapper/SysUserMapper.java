package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户+关联角色信息（登录用）
     */
    SysUser selectByUsernameWithRole(@Param("username") String username);

    /**
     * 根据用户ID查询用户+关联角色信息（JWT认证用）
     */
    SysUser selectByIdWithRole(@Param("userId") Long userId);
}
