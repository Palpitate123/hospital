package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色Mapper接口
 * 角色数据访问层
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

}
