package com.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.common.R;
import com.hospital.entity.SysUser;
import com.hospital.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统用户后端控制器
 * 实现了密码加密存储、关联表物理清理、关联查询回显
 */
@RestController
@RequestMapping("/sys/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // 实例化密码加密工具
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 分页查询用户列表 - 关联角色名称显示
     * 解决“未分配”的问题
     */
    @GetMapping("/page")
    public R<Page<SysUser>> getUserPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String username) {

        Page<SysUser> page = new Page<>(current, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        if (username != null && !username.isEmpty()) {
            wrapper.like(SysUser::getUsername, username);
        }
        // 按创建时间降序排序
        wrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> result = sysUserService.page(page, wrapper);

        // 联动查询 sys_role 获取文字名称
        for (SysUser user : result.getRecords()) {
            String sql = "SELECT r.role_name FROM sys_role r " +
                    "JOIN sys_user_role ur ON r.id = ur.role_id " +
                    "WHERE ur.user_id = ?";
            List<String> names = jdbcTemplate.queryForList(sql, String.class, user.getId());
            if (!names.isEmpty()) {
                user.setRoleName(String.join(",", names));
            } else {
                user.setRoleName("未分配");
            }
        }
        return R.ok(result);
    }

    /**
     * 新增用户：加密保存 + 关联表插入
     */
    @PostMapping
    @Transactional(rollbackFor = Exception.class)
    public R<Void> save(@RequestBody SysUser sysUser) {
        // 1. 核心：BCrypt 加密处理
        if (sysUser.getPassword() != null && !sysUser.getPassword().isEmpty()) {
            sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        }

        sysUser.setDeleted(0);
        if (sysUser.getStatus() == null) sysUser.setStatus(1);

        // 2. 保存到 sys_user
        sysUserService.save(sysUser);

        // 3. 联动保存到关联表 sys_user_role
        if (sysUser.getRoleId() != null) {
            String sql = "INSERT INTO sys_user_role (user_id, role_id, create_time) VALUES (?, ?, NOW())";
            jdbcTemplate.update(sql, sysUser.getId(), sysUser.getRoleId());
        }
        return R.ok();
    }

    /**
     * 更新用户信息
     */
    @PutMapping
    public R<Void> update(@RequestBody SysUser sysUser) {
        // 如果修改了密码，则进行加密
        if (sysUser.getPassword() != null && !sysUser.getPassword().isEmpty()) {
            sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        }
        sysUserService.updateById(sysUser);
        return R.ok();
    }

    /**
     * 物理删除：彻底从数据库抹除记录
     */
    @DeleteMapping("/{id}")
    @Transactional(rollbackFor = Exception.class)
    public R<Void> delete(@PathVariable Long id) {
        // 1. 从用户主表物理删除
        sysUserService.removeById(id);

        // 2. 从关联表彻底清理编号
        String sql = "DELETE FROM sys_user_role WHERE user_id = ?";
        jdbcTemplate.update(sql, id);

        return R.ok();
    }
}
