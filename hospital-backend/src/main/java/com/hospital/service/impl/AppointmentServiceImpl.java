package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.dto.AppointmentDTO;
import com.hospital.entity.HosAppointmentOrder;
import com.hospital.entity.HosDoctor;
import com.hospital.entity.HosNumberSource;
import com.hospital.entity.HosSchedule;
import com.hospital.exception.BusinessException;
import com.hospital.mapper.HosAppointmentOrderMapper;
import com.hospital.mapper.HosDoctorMapper;
import com.hospital.mapper.HosNumberSourceMapper;
import com.hospital.mapper.HosScheduleMapper;
import com.hospital.service.AppointmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 预约订单服务实现类
 * 实现预约挂号核心业务逻辑
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Slf4j
@Service
public class AppointmentServiceImpl extends ServiceImpl<HosAppointmentOrderMapper, HosAppointmentOrder> implements AppointmentService {

    @Autowired
    private HosAppointmentOrderMapper hosAppointmentOrderMapper;

    @Autowired
    private HosNumberSourceMapper hosNumberSourceMapper;

    @Autowired
    private HosScheduleMapper hosScheduleMapper;

    @Autowired
    private HosDoctorMapper hosDoctorMapper;

    /**
     * 提交预约申请
     * 核心业务：号源扣减、订单生成、防超卖
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitAppointment(Long userId, AppointmentDTO appointmentDTO) {
        // 查询排班信息
        Map<String, Object> scheduleDetail = hosScheduleMapper.selectScheduleDetailById(appointmentDTO.getScheduleId());
        if (scheduleDetail == null) {
            throw new BusinessException("排班信息不存在");
        }

        Long doctorId = (Long) scheduleDetail.get("doctor_id");
        String deptName = (String) scheduleDetail.get("dept_name");
        LocalDate workDate = (LocalDate) scheduleDetail.get("work_date");
        String workTime = (String) scheduleDetail.get("work_time");

        // 检查排班是否已发布
        Integer scheduleStatus = (Integer) scheduleDetail.get("schedule_status");
        if (scheduleStatus == null || scheduleStatus != 1) {
            throw new BusinessException("该排班尚未发布，无法预约");
        }

        // 检查是否重复预约
        int existsCount = hosAppointmentOrderMapper.checkAppointmentExists(userId, doctorId, appointmentDTO.getScheduleId());
        if (existsCount > 0) {
            throw new BusinessException("您已预约过该医生该时段");
        }

        // 查询号源信息
        HosNumberSource numberSource = hosNumberSourceMapper.selectById(appointmentDTO.getSourceId());
        if (numberSource == null) {
            throw new BusinessException("号源信息不存在");
        }

        // 检查号源状态
        if (numberSource.getSourceStatus() != 1) {
            throw new BusinessException("号源不可预约");
        }

        // 检查剩余号源
        if (numberSource.getRemainNumber() <= 0) {
            throw new BusinessException("号源已约满");
        }

        // 扣减号源（使用乐观锁）
        int updateCount = hosNumberSourceMapper.decreaseRemainNumber(
                appointmentDTO.getSourceId(), numberSource.getVersion());
        if (updateCount == 0) {
            throw new BusinessException("预约失败，请重试");
        }

        // 创建预约订单
        HosAppointmentOrder order = new HosAppointmentOrder();
        order.setUserId(userId);
        order.setDoctorId(doctorId);
        order.setScheduleId(appointmentDTO.getScheduleId());
        order.setSourceId(appointmentDTO.getSourceId());
        order.setDeptName(deptName);
        order.setWorkDate(workDate);
        order.setWorkTime(workTime);
        order.setOrderStatus(1); // 待就诊
        this.save(order);

        // 更新号源状态（如果约满）
        HosNumberSource updatedSource = hosNumberSourceMapper.selectById(appointmentDTO.getSourceId());
        if (updatedSource.getRemainNumber() == 0) {
            updatedSource.setSourceStatus(2); // 已约满
            hosNumberSourceMapper.updateById(updatedSource);
        }

        log.info("预约成功: userId={}, orderId={}, doctorId={}, workDate={}", 
                userId, order.getId(), doctorId, workDate);
        return order.getId();
    }

    /**
     * 取消预约订单
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelAppointment(Long userId, Long orderId) {
        // 查询订单
        HosAppointmentOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单所属
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此订单");
        }

        // 检查订单状态
        if (order.getOrderStatus() != 1) {
            throw new BusinessException("订单无法取消");
        }

        // 更新订单状态
        order.setOrderStatus(2); // 已取消
        this.updateById(order);

        // 释放号源
        hosNumberSourceMapper.increaseRemainNumber(order.getSourceId());

        // 更新号源状态
        HosNumberSource numberSource = hosNumberSourceMapper.selectById(order.getSourceId());
        if (numberSource != null && numberSource.getSourceStatus() == 2) {
            numberSource.setSourceStatus(1); // 可预约
            hosNumberSourceMapper.updateById(numberSource);
        }

        log.info("取消预约成功: userId={}, orderId={}", userId, orderId);
        return true;
    }

    /**
     * 分页查询订单列表
     */
    @Override
    public Page<Map<String, Object>> getOrderPage(Page<HosAppointmentOrder> page, Long userId, Long doctorId,
                                                   String deptName, Integer orderStatus, String startDate, String endDate) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("doctorId", doctorId);
        params.put("deptName", deptName);
        params.put("orderStatus", orderStatus);
        params.put("startDate", startDate);
        params.put("endDate", endDate);

        List<Map<String, Object>> records = hosAppointmentOrderMapper.selectOrderWithInfo(params);

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
     * 获取订单详情
     */
    @Override
    public Map<String, Object> getOrderDetail(Long id) {
        Map<String, Object> detail = hosAppointmentOrderMapper.selectOrderDetailById(id);
        if (detail == null) {
            throw new BusinessException("订单不存在");
        }
        return detail;
    }

    /**
     * 强制取消订单（管理员）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean forceCancelOrder(Long orderId) {
        HosAppointmentOrder order = this.getById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        if (order.getOrderStatus() != 1) {
            throw new BusinessException("订单无法取消");
        }

        // 更新订单状态
        order.setOrderStatus(2);
        this.updateById(order);

        // 释放号源
        hosNumberSourceMapper.increaseRemainNumber(order.getSourceId());

        log.info("强制取消订单成功: orderId={}", orderId);
        return true;
    }

    /**
     * 统计各科室挂号数量
     */
    @Override
    public List<Map<String, Object>> countByDepartment() {
        return hosAppointmentOrderMapper.countByDepartment();
    }

    /**
     * 统计各医生预约数量
     */
    @Override
    public List<Map<String, Object>> countByDoctor() {
        return hosAppointmentOrderMapper.countByDoctor();
    }

    /**
     * 统计今日预约数量
     */
    @Override
    public Long countTodayAppointments() {
        LambdaQueryWrapper<HosAppointmentOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HosAppointmentOrder::getWorkDate, LocalDate.now());
        return this.count(wrapper);
    }

    /**
     * 统计总预约数量
     */
    @Override
    public Long countTotalAppointments() {
        return this.count();
    }
}
