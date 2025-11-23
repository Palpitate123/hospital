package com.hospital.controller;

import com.hospital.entity.Department;
import com.hospital.service.DepartmentService;
import com.hospital.utils.Result;
import com.hospital.vo.DoctorVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 科室控制器
 */
@RestController
@RequestMapping("/api/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 获取所有科室列表
     */
    @GetMapping("/list")
    public Result getAllDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        return Result.success(departments);
    }
    
    /**
     * 获取科室医生列表
     */
    @GetMapping("/doctors/{deptId}")
    public Result getDepartmentDoctors(@PathVariable Long deptId) {
        List<DoctorVO> doctors = departmentService.getDepartmentDoctors(deptId);
        return Result.success(doctors);
    }

    /**
     * 根据ID获取科室详情
     */
    @GetMapping("/detail/{deptId}")
    public Result getDepartmentDetail(@PathVariable Long deptId) {
        Map<String, Object> detail = departmentService.getDepartmentDetail(deptId);
        return Result.success(detail);
    }

    /**
     * 新增科室（仅管理员）
     */
    @PostMapping("/add")
    public Result addDepartment(@RequestBody Department department, HttpServletRequest request) {
        String roleCode = (String) request.getAttribute("roleCode");
        if (!"ROLE_ADMIN".equals(roleCode)) {
            return Result.fail("权限不足");
        }

        boolean result = departmentService.addDepartment(department);
        return result ? Result.success() : Result.fail("添加失败");
    }

    /**
     * 更新科室信息（仅管理员）
     */
    @PutMapping("/update")
    public Result updateDepartment(@RequestBody Department department, HttpServletRequest request) {
        String roleCode = (String) request.getAttribute("roleCode");
        if (!"ROLE_ADMIN".equals(roleCode)) {
            return Result.fail("权限不足");
        }

        boolean result = departmentService.updateDepartment(department);
        return result ? Result.success() : Result.fail("更新失败");
    }

    /**
     * 删除科室（仅管理员）
     */
    @DeleteMapping("/delete/{deptId}")
    public Result deleteDepartment(@PathVariable Long deptId, HttpServletRequest request) {
        String roleCode = (String) request.getAttribute("roleCode");
        if (!"ROLE_ADMIN".equals(roleCode)) {
            return Result.fail("权限不足");
        }

        try {
            boolean result = departmentService.deleteDepartment(deptId);
            return result ? Result.success() : Result.fail("删除失败");
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 搜索科室
     */
    @GetMapping("/search")
    public Result searchDepartments(@RequestParam String keyword) {
        List<Department> departments = departmentService.searchDepartments(keyword);
        return Result.success(departments);
    }

    /**
     * 更新科室状态（仅管理员）
     */
    @PutMapping("/status")
    public Result updateStatus(@RequestBody Map<String, Object> request, HttpServletRequest req) {
        String roleCode = (String) req.getAttribute("roleCode");
        if (!"ROLE_ADMIN".equals(roleCode)) {
            return Result.fail("权限不足");
        }

        Long deptId = Long.valueOf(request.get("deptId").toString());
        Integer status = (Integer) request.get("status");

        Department department = new Department();
        department.setId(deptId);
        department.setStatus(status);

        boolean result = departmentService.updateDepartment(department);
        return result ? Result.success() : Result.fail("更新失败");
    }
}
