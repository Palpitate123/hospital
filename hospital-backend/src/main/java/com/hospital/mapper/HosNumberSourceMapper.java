package com.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hospital.entity.HosNumberSource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 号源Mapper接口
 * 号源数据访问层
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Mapper
public interface HosNumberSourceMapper extends BaseMapper<HosNumberSource> {

    /**
     * 扣减号源库存（使用乐观锁）
     * 
     * @param id 号源ID
     * @param version 版本号
     * @return 影响行数
     */
    int decreaseRemainNumber(@Param("id") Long id, @Param("version") Integer version);

    /**
     * 增加号源库存
     * 
     * @param id 号源ID
     * @return 影响行数
     */
    int increaseRemainNumber(@Param("id") Long id);

    /**
     * 根据排班ID查询号源信息
     * 
     * @param scheduleId 排班ID
     * @return 号源信息
     */
    HosNumberSource selectByScheduleId(@Param("scheduleId") Long scheduleId);
}
