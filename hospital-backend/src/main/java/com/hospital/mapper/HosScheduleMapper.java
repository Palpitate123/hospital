package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.HosSchedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 排班Mapper接口
 * 排班数据访问层
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Mapper
public interface HosScheduleMapper extends BaseMapper<HosSchedule> {

    /**
     * 查询排班列表（包含医生、科室、号源信息）
     * 
     * @param params 查询参数
     * @return 排班列表
     */
    List<Map<String, Object>> selectScheduleWithInfo(@Param("params") Map<String, Object> params);

    /**
     * 根据ID查询排班详情（包含医生、科室、号源信息）
     * 
     * @param id 排班ID
     * @return 排班详情
     */
    Map<String, Object> selectScheduleDetailById(@Param("id") Long id);

    /**
     * 检查排班是否冲突
     * 
     * @param schedule 排班信息
     * @return 冲突数量
     */
    int checkScheduleConflict(@Param("schedule") HosSchedule schedule);
}
