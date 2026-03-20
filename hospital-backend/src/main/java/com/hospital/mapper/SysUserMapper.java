package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统用户Mapper接口
 * 用户数据访问层
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户信息（包含角色信息）
     * 
     * @param username 用户名
     * @return 用户实体
     */
    SysUser selectByUsernameWithRole(@Param("username") String username);

    /**
     * 根据用户ID查询用户信息（包含角色信息）
     * 
     * @param userId 用户ID
     * @return 用户实体
     */
    SysUser selectByIdWithRole(@Param("userId") Long userId);
}
