package com.hospital.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.dto.ScheduleDTO;
import com.hospital.entity.HosSchedule;
import com.hospital.service.ScheduleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Api(tags = "排班管理")
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation("分页查询排班列表")
    @GetMapping("/page")
    public Result<PageResult<Map<String, Object>>> getSchedulePage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) String doctorName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Integer scheduleStatus) {
        Page<HosSchedule> page = new Page<>(current, size);
        Page<Map<String, Object>> result = scheduleService.getSchedulePage(page, deptId, doctorId, 
                doctorName, startDate, endDate, scheduleStatus);
        PageResult<Map<String, Object>> pageResult = new PageResult<>(
                result.getCurrent(), result.getSize(), result.getTotal(), result.getRecords());
        return Result.success(pageResult);
    }

    @ApiOperation("查询医生的排班列表")
    @GetMapping("/doctor/{doctorId}")
    public Result<List<Map<String, Object>>> getDoctorSchedules(
            @PathVariable Long doctorId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        String startStr = startDate != null ? startDate.toString() : null;
        String endStr = endDate != null ? endDate.toString() : null;
        List<Map<String, Object>> list = scheduleService.getDoctorSchedules(doctorId, startStr, endStr);
        return Result.success(list);
    }

    @ApiOperation("获取排班详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getScheduleDetail(@PathVariable Long id) {
        Map<String, Object> detail = scheduleService.getScheduleDetail(id);
        return Result.success(detail);
    }

    @ApiOperation("新增排班")
    @PreAuthorize("hasRole('admin')")
    @PostMapping
    public Result<String> addSchedule(@Validated @RequestBody ScheduleDTO scheduleDTO) {
        scheduleService.addSchedule(scheduleDTO);
        return Result.success("新增成功");
    }

    @ApiOperation("编辑排班")
    @PreAuthorize("hasRole('admin')")
    @PutMapping
    public Result<String> updateSchedule(@Validated @RequestBody ScheduleDTO scheduleDTO) {
        scheduleService.updateSchedule(scheduleDTO);
        return Result.success("编辑成功");
    }

    @ApiOperation("删除排班")
    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    public Result<String> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return Result.success("删除成功");
    }

    @ApiOperation("发布排班号源")
    @PreAuthorize("hasRole('admin')")
    @PutMapping("/publish/{id}")
    public Result<String> publishSchedule(@PathVariable Long id) {
        scheduleService.publishSchedule(id);
        return Result.success("发布成功");
    }

    @ApiOperation("下架排班号源")
    @PreAuthorize("hasRole('admin')")
    @PutMapping("/unpublish/{id}")
    public Result<String> unpublishSchedule(@PathVariable Long id) {
        scheduleService.unpublishSchedule(id);
        return Result.success("下架成功");
    }
}
