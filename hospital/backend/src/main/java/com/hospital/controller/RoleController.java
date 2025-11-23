package com.hospital.controller;

import com.hospital.annotation.RequiresPermission;
import com.hospital.entity.Role;
import com.hospital.entity.Permission;
import com.hospital.service.RoleService;
import com.hospital.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 角色控制器
 */
@RestController
@RequestMapping("/api/role")
public class RoleController {
    
    @Autowired
    private RoleService roleService;
    
    /**
     * 获取所有角色列表
     */
    @GetMapping("/list")
    @RequiresPermission(value = "role:list", description = "查看角色列表")
    public Result listRoles() {
        List<Role> roles = roleService.list();
        return Result.success(roles);
    }
    
    /**
     * 获取角色详情
     */
    @GetMapping("/detail/{id}")
    @RequiresPermission(value = "role:view", description = "查看角色详情")
    public Result getRoleDetail(@PathVariable Long id) {
        Role role = roleService.getById(id);
        if (role == null) {
            return Result.fail("角色不存在");
        }
        return Result.success(role);
    }
    
    /**
     * 创建角色
     */
    @PostMapping("/create")
    @RequiresPermission(value = "role:create", description = "创建角色")
    public Result createRole(@RequestBody Role role) {
        boolean result = roleService.save(role);
        return result ? Result.success() : Result.fail("创建失败");
    }
    
    /**
     * 更新角色
     */
    @PutMapping("/update")
    @RequiresPermission(value = "role:update", description = "更新角色")
    public Result updateRole(@RequestBody Role role) {
        boolean result = roleService.updateById(role);
        return result ? Result.success() : Result.fail("更新失败");
    }
    
    /**
     * 删除角色
     */
    @DeleteMapping("/delete/{id}")
    @RequiresPermission(value = "role:delete", description = "删除角色")
    public Result deleteRole(@PathVariable Long id) {
        boolean result = roleService.removeById(id);
        return result ? Result.success() : Result.fail("删除失败");
    }
    
    /**
     * 为角色分配权限
     */
    @PostMapping("/assign-permissions")
    @RequiresPermission(value = "role:assign", description = "为角色分配权限")
    public Result assignPermissions(@RequestBody Map<String, Object> request) {
        Long roleId = Long.valueOf(request.get("roleId").toString());
        List<Long> permissionIds = (List<Long>) request.get("permissionIds");
        
        boolean result = roleService.assignPermissionsToRole(roleId, permissionIds);
        return result ? Result.success() : Result.fail("分配权限失败");
    }
    
    /**
     * 获取角色权限列表
     */
    @GetMapping("/permissions/{roleId}")
    @RequiresPermission(value = "role:permissions", description = "查看角色权限")
    public Result getRolePermissions(@PathVariable Long roleId) {
        List<Permission> permissions = roleService.getPermissionsByRoleId(roleId);
        return Result.success(permissions);
    }
    
    /**
     * 为用户分配角色
     */
    @PostMapping("/assign-to-user")
    @RequiresPermission(value = "user:assignRole", description = "为用户分配角色")
    public Result assignRoleToUser(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        Long roleId = Long.valueOf(request.get("roleId").toString());
        
        boolean result = roleService.assignRoleToUser(userId, roleId);
        return result ? Result.success() : Result.fail("分配角色失败");
    }
    
    /**
     * 获取用户角色列表
     */
    @GetMapping("/user/{userId}")
    @RequiresPermission(value = "user:roles", description = "查看用户角色")
    public Result getUserRoles(@PathVariable Long userId) {
        List<Role> roles = roleService.getRolesByUserId(userId);
        return Result.success(roles);
    }
}
