package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.HosDepartment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 科室Mapper接口
 * 科室数据访问层
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Mapper
public interface HosDepartmentMapper extends BaseMapper<HosDepartment> {

}
