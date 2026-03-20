package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联Mapper接口
 * 用户角色关联数据访问层
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

}
