package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.dto.ScheduleDTO;
import com.hospital.entity.HosDoctor;
import com.hospital.entity.HosNumberSource;
import com.hospital.entity.HosSchedule;
import com.hospital.exception.BusinessException;
import com.hospital.mapper.HosDoctorMapper;
import com.hospital.mapper.HosNumberSourceMapper;
import com.hospital.mapper.HosScheduleMapper;
import com.hospital.service.ScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 排班服务实现类
 * 实现排班管理相关业务逻辑
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Slf4j
@Service
public class ScheduleServiceImpl extends ServiceImpl<HosScheduleMapper, HosSchedule> implements ScheduleService {

    @Autowired
    private HosScheduleMapper hosScheduleMapper;

    @Autowired
    private HosDoctorMapper hosDoctorMapper;

    @Autowired
    private HosNumberSourceMapper hosNumberSourceMapper;

    /**
     * 分页查询排班列表
     */
    @Override
    public Page<Map<String, Object>> getSchedulePage(Page<HosSchedule> page, Long deptId, Long doctorId,
                                                      String doctorName, String startDate, String endDate, Integer scheduleStatus) {
        Map<String, Object> params = new HashMap<>();
        params.put("deptId", deptId);
        params.put("doctorId", doctorId);
        params.put("doctorName", doctorName);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("scheduleStatus", scheduleStatus);

        List<Map<String, Object>> records = hosScheduleMapper.selectScheduleWithInfo(params);

        // 手动分页
        int total = records.size();
        int start = (int) ((page.getCurrent() - 1) * page.getSize());
        int end = (int) Math.min(start + page.getSize(), total);

        List<Map<String, Object>> pageRecords = records.subList(Math.max(0, start), end);

        Page<Map<String, Object>> resultPage = new Page<>(page.getCurrent(), page.getSize(), total);
        resultPage.setRecords(pageRecords);
        return resultPage;
    }

    /**
     * 查询医生的排班列表（患者端）
     */
    @Override
    public List<Map<String, Object>> getDoctorSchedules(Long doctorId, String startDate, String endDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("doctorId", doctorId);
        params.put("startDate", startDate);
        params.put("endDate", endDate);
        params.put("scheduleStatus", 1); // 只查询已发布的
        return hosScheduleMapper.selectScheduleWithInfo(params);
    }

    /**
     * 获取排班详情
     */
    @Override
    public Map<String, Object> getScheduleDetail(Long id) {
        Map<String, Object> detail = hosScheduleMapper.selectScheduleDetailById(id);
        if (detail == null) {
            throw new BusinessException("排班不存在");
        }
        return detail;
    }

    /**
     * 新增排班
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addSchedule(ScheduleDTO scheduleDTO) {
        // 检查医生是否存在
        HosDoctor doctor = hosDoctorMapper.selectById(scheduleDTO.getDoctorId());
        if (doctor == null) {
            throw new BusinessException("医生不存在");
        }

        // 创建排班对象用于冲突检查
        HosSchedule checkSchedule = new HosSchedule();
        checkSchedule.setId(null);
        checkSchedule.setDoctorId(scheduleDTO.getDoctorId());
        checkSchedule.setWorkDate(scheduleDTO.getWorkDate());
        checkSchedule.setWorkTime(scheduleDTO.getWorkTime());

        // 检查排班是否冲突
        int conflictCount = hosScheduleMapper.checkScheduleConflict(checkSchedule);
        if (conflictCount > 0) {
            throw new BusinessException("该医生同一时段已存在排班");
        }

        // 创建排班记录
        HosSchedule schedule = new HosSchedule();
        BeanUtils.copyProperties(scheduleDTO, schedule);
        schedule.setScheduleStatus(0); // 默认未发布
        this.save(schedule);

        // 自动创建号源记录
        HosNumberSource numberSource = new HosNumberSource();
        numberSource.setScheduleId(schedule.getId());
        numberSource.setDoctorId(scheduleDTO.getDoctorId());
        numberSource.setTotalNumber(scheduleDTO.getTotalNumber());
        numberSource.setRemainNumber(scheduleDTO.getTotalNumber());
        numberSource.setSourceStatus(1); // 可预约
        numberSource.setVersion(0);
        hosNumberSourceMapper.insert(numberSource);

        log.info("新增排班成功: doctorId={}, workDate={}, workTime={}", 
                scheduleDTO.getDoctorId(), scheduleDTO.getWorkDate(), scheduleDTO.getWorkTime());
        return true;
    }

    /**
     * 编辑排班
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSchedule(ScheduleDTO scheduleDTO) {
        HosSchedule schedule = this.getById(scheduleDTO.getId());
        if (schedule == null) {
            throw new BusinessException("排班不存在");
        }

        // 如果修改了医生、日期或时段，检查是否冲突
        if (!schedule.getDoctorId().equals(scheduleDTO.getDoctorId()) ||
            !schedule.getWorkDate().equals(scheduleDTO.getWorkDate()) ||
            !schedule.getWorkTime().equals(scheduleDTO.getWorkTime())) {
            
            HosSchedule checkSchedule = new HosSchedule();
            checkSchedule.setId(scheduleDTO.getId());
            checkSchedule.setDoctorId(scheduleDTO.getDoctorId());
            checkSchedule.setWorkDate(scheduleDTO.getWorkDate());
            checkSchedule.setWorkTime(scheduleDTO.getWorkTime());
            
            int conflictCount = hosScheduleMapper.checkScheduleConflict(checkSchedule);
            if (conflictCount > 0) {
                throw new BusinessException("该医生同一时段已存在排班");
            }
        }

        // 更新排班信息
        schedule.setDoctorId(scheduleDTO.getDoctorId());
        schedule.setDeptId(scheduleDTO.getDeptId());
        schedule.setWorkDate(scheduleDTO.getWorkDate());
        schedule.setWorkTime(scheduleDTO.getWorkTime());
        schedule.setTotalNumber(scheduleDTO.getTotalNumber());
        this.updateById(schedule);

        // 更新号源信息
        HosNumberSource numberSource = hosNumberSourceMapper.selectByScheduleId(schedule.getId());
        if (numberSource != null) {
            int usedNumber = numberSource.getTotalNumber() - numberSource.getRemainNumber();
            numberSource.setTotalNumber(scheduleDTO.getTotalNumber());
            numberSource.setRemainNumber(scheduleDTO.getTotalNumber() - usedNumber);
            numberSource.setDoctorId(scheduleDTO.getDoctorId());
            hosNumberSourceMapper.updateById(numberSource);
        }

        log.info("编辑排班成功: scheduleId={}", scheduleDTO.getId());
        return true;
    }

    /**
     * 删除排班
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSchedule(Long id) {
        HosSchedule schedule = this.getById(id);
        if (schedule == null) {
            throw new BusinessException("排班不存在");
        }

        // 已发布的排班不能删除
        if (schedule.getScheduleStatus() == 1) {
            throw new BusinessException("排班已发布，无法删除");
        }

        // 删除号源
        HosNumberSource numberSource = hosNumberSourceMapper.selectByScheduleId(id);
        if (numberSource != null) {
            hosNumberSourceMapper.deleteById(numberSource.getId());
        }

        // 删除排班
        this.removeById(id);

        log.info("删除排班成功: scheduleId={}", id);
        return true;
    }

    /**
     * 发布排班号源
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishSchedule(Long id) {
        HosSchedule schedule = this.getById(id);
        if (schedule == null) {
            throw new BusinessException("排班不存在");
        }

        schedule.setScheduleStatus(1);
        this.updateById(schedule);

        // 更新号源状态
        HosNumberSource numberSource = hosNumberSourceMapper.selectByScheduleId(id);
        if (numberSource != null) {
            numberSource.setSourceStatus(1);
            hosNumberSourceMapper.updateById(numberSource);
        }

        log.info("发布排班号源成功: scheduleId={}", id);
        return true;
    }

    /**
     * 下架排班号源
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unpublishSchedule(Long id) {
        HosSchedule schedule = this.getById(id);
        if (schedule == null) {
            throw new BusinessException("排班不存在");
        }

        schedule.setScheduleStatus(2);
        this.updateById(schedule);

        // 更新号源状态
        HosNumberSource numberSource = hosNumberSourceMapper.selectByScheduleId(id);
        if (numberSource != null) {
            numberSource.setSourceStatus(3);
            hosNumberSourceMapper.updateById(numberSource);
        }

        log.info("下架排班号源成功: scheduleId={}", id);
        return true;
    }
}
