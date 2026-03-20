package com.hospital.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.dto.AppointmentDTO;
import com.hospital.entity.HosAppointmentOrder;
import com.hospital.entity.SysUser;
import com.hospital.service.AppointmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 预约订单控制器
 * 处理预约挂号相关请求
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Slf4j
@Api(tags = "预约订单管理")
@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @ApiOperation("提交预约申请")
    @PostMapping("/submit")
    public Result<Long> submitAppointment(@AuthenticationPrincipal SysUser user,
                                          @Validated @RequestBody AppointmentDTO appointmentDTO) {
        Long orderId = appointmentService.submitAppointment(user.getId(), appointmentDTO);
        return Result.success("预约成功", orderId);
    }

    @ApiOperation("取消预约订单")
    @PutMapping("/cancel/{id}")
    public Result<String> cancelAppointment(@AuthenticationPrincipal SysUser user, @PathVariable Long id) {
        appointmentService.cancelAppointment(user.getId(), id);
        return Result.success("取消成功");
    }

    @ApiOperation("查询我的预约订单")
    @GetMapping("/my")
    public Result<PageResult<Map<String, Object>>> getMyAppointments(
            @AuthenticationPrincipal SysUser user,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Integer orderStatus,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Page<HosAppointmentOrder> page = new Page<>(current, size);
        Page<Map<String, Object>> result = appointmentService.getOrderPage(page, user.getId(), null, 
                null, orderStatus, startDate, endDate);
        PageResult<Map<String, Object>> pageResult = new PageResult<>(
                result.getCurrent(), result.getSize(), result.getTotal(), result.getRecords());
        return Result.success(pageResult);
    }

    @ApiOperation("分页查询订单列表")
    @GetMapping("/page")
    public Result<PageResult<Map<String, Object>>> getOrderPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) String deptName,
            @RequestParam(required = false) Integer orderStatus,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Page<HosAppointmentOrder> page = new Page<>(current, size);
        Page<Map<String, Object>> result = appointmentService.getOrderPage(page, userId, doctorId, 
                deptName, orderStatus, startDate, endDate);
        PageResult<Map<String, Object>> pageResult = new PageResult<>(
                result.getCurrent(), result.getSize(), result.getTotal(), result.getRecords());
        return Result.success(pageResult);
    }

    @ApiOperation("获取订单详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getOrderDetail(@PathVariable Long id) {
        Map<String, Object> detail = appointmentService.getOrderDetail(id);
        return Result.success(detail);
    }

    @ApiOperation("强制取消订单")
    @PreAuthorize("hasRole('admin')")
    @PutMapping("/force-cancel/{id}")
    public Result<String> forceCancelOrder(@PathVariable Long id) {
        appointmentService.forceCancelOrder(id);
        return Result.success("取消成功");
    }

    @ApiOperation("统计各科室挂号数量")
    @GetMapping("/stats/department")
    public Result<List<Map<String, Object>>> countByDepartment() {
        List<Map<String, Object>> stats = appointmentService.countByDepartment();
        return Result.success(stats);
    }

    @ApiOperation("统计各医生预约数量")
    @GetMapping("/stats/doctor")
    public Result<List<Map<String, Object>>> countByDoctor() {
        List<Map<String, Object>> stats = appointmentService.countByDoctor();
        return Result.success(stats);
    }

    @ApiOperation("统计今日预约数量")
    @GetMapping("/stats/today")
    public Result<Long> countTodayAppointments() {
        Long count = appointmentService.countTodayAppointments();
        return Result.success(count);
    }

    @ApiOperation("统计总预约数量")
    @GetMapping("/stats/total")
    public Result<Long> countTotalAppointments() {
        Long count = appointmentService.countTotalAppointments();
        return Result.success(count);
    }
}
