package com.hospital.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.dto.DoctorDTO;
import com.hospital.entity.HosDoctor;
import com.hospital.service.DoctorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 医生管理控制器
 * 处理医生管理相关请求
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Slf4j
@Api(tags = "医生管理")
@RestController
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @ApiOperation("分页查询医生列表")
    @GetMapping("/page")
    public Result<PageResult<Map<String, Object>>> getDoctorPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) String doctorName,
            @RequestParam(required = false) Integer status) {
        Page<HosDoctor> page = new Page<>(current, size);
        Page<Map<String, Object>> result = doctorService.getDoctorPage(page, deptId, doctorName, status);
        PageResult<Map<String, Object>> pageResult = new PageResult<>(
                result.getCurrent(), result.getSize(), result.getTotal(), result.getRecords());
        return Result.success(pageResult);
    }

    @ApiOperation("查询科室下的医生列表")
    @GetMapping("/dept/{deptId}")
    public Result<List<Map<String, Object>>> getDoctorsByDeptId(@PathVariable Long deptId) {
        List<Map<String, Object>> list = doctorService.getDoctorsByDeptId(deptId);
        return Result.success(list);
    }

    @ApiOperation("获取医生详情")
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getDoctorDetail(@PathVariable Long id) {
        Map<String, Object> detail = doctorService.getDoctorDetail(id);
        return Result.success(detail);
    }

    @ApiOperation("新增医生")
    @PreAuthorize("hasRole('admin')")
    @PostMapping
    public Result<String> addDoctor(@Validated @RequestBody DoctorDTO doctorDTO) {
        doctorService.addDoctor(doctorDTO);
        return Result.success("新增成功");
    }

    @ApiOperation("编辑医生")
    @PreAuthorize("hasRole('admin')")
    @PutMapping
    public Result<String> updateDoctor(@Validated @RequestBody DoctorDTO doctorDTO) {
        doctorService.updateDoctor(doctorDTO);
        return Result.success("编辑成功");
    }

    @ApiOperation("删除医生")
    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    public Result<String> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return Result.success("删除成功");
    }

    @ApiOperation("启用/停用医生账号")
    @PreAuthorize("hasRole('admin')")
    @PutMapping("/status/{id}")
    public Result<String> updateDoctorStatus(@PathVariable Long id, @RequestParam Integer status) {
        doctorService.updateDoctorStatus(id, status);
        return Result.success("状态更新成功");
    }
}
