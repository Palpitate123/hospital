package com.hospital.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.common.PageResult;
import com.hospital.common.Result;
import com.hospital.dto.DepartmentDTO;
import com.hospital.entity.HosDepartment;
import com.hospital.service.DepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 科室管理控制器
 * 处理科室管理相关请求
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Slf4j
@Api(tags = "科室管理")
@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @ApiOperation("分页查询科室列表")
    @GetMapping("/page")
    public Result<PageResult<HosDepartment>> getDepartmentPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String deptName,
            @RequestParam(required = false) Integer status) {
        Page<HosDepartment> page = new Page<>(current, size);
        Page<HosDepartment> result = departmentService.getDepartmentPage(page, deptName, status);
        PageResult<HosDepartment> pageResult = new PageResult<>(
                result.getCurrent(), result.getSize(), result.getTotal(), result.getRecords());
        return Result.success(pageResult);
    }

    @ApiOperation("查询所有启用的科室列表")
    @GetMapping("/list")
    public Result<List<HosDepartment>> getEnabledDepartments() {
        List<HosDepartment> list = departmentService.getEnabledDepartments();
        return Result.success(list);
    }

    @ApiOperation("获取科室详情")
    @GetMapping("/{id}")
    public Result<HosDepartment> getDepartmentDetail(@PathVariable Long id) {
        HosDepartment department = departmentService.getDepartmentDetail(id);
        return Result.success(department);
    }

    @ApiOperation("新增科室")
    @PreAuthorize("hasRole('admin')")
    @PostMapping
    public Result<String> addDepartment(@Validated @RequestBody DepartmentDTO departmentDTO) {
        departmentService.addDepartment(departmentDTO);
        return Result.success("新增成功");
    }

    @ApiOperation("编辑科室")
    @PreAuthorize("hasRole('admin')")
    @PutMapping
    public Result<String> updateDepartment(@Validated @RequestBody DepartmentDTO departmentDTO) {
        departmentService.updateDepartment(departmentDTO);
        return Result.success("编辑成功");
    }

    @ApiOperation("删除科室")
    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    public Result<String> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return Result.success("删除成功");
    }

    @ApiOperation("启用/停用科室")
    @PreAuthorize("hasRole('admin')")
    @PutMapping("/status/{id}")
    public Result<String> updateDepartmentStatus(@PathVariable Long id, @RequestParam Integer status) {
        departmentService.updateDepartmentStatus(id, status);
        return Result.success("状态更新成功");
    }
}
