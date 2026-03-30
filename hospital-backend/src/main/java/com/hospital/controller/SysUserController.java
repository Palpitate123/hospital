package com.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hospital.common.R; // 确保这个包路径和你项目中其他Controller一致
import com.hospital.entity.SysUser;
import com.hospital.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sys/user") // 修正：去掉#{，直接写路径字符串
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 分页查询用户列表
     */
    @GetMapping("/page") // 修正：去掉#{，直接写路径字符串
    public R<Page<SysUser>> getUserPage(
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {

        Page<SysUser> page = new Page<>(current, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        // 模糊查询用户名
        wrapper.like(username != null && !username.isEmpty(), SysUser::getUsername, username);
        // 精确查询状态
        wrapper.eq(status != null, SysUser::getStatus, status);
        // 过滤逻辑删除数据
        wrapper.eq(SysUser::getDeleted, 0);

        Page<SysUser> result = sysUserService.page(page, wrapper);
        return R.ok(result);
    }

    /**
     * 修改用户状态（启用/停用）
     */
    @PutMapping("/status/{id}/{status}")
    public R<Void> updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        sysUserService.updateById(user);
        return R.ok();
    }
}
