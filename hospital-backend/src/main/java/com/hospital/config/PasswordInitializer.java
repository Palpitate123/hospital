package com.hospital.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hospital.entity.SysUser;
import com.hospital.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码初始化器
 * 系统启动时检查并重置管理员密码
 */
@Slf4j
@Component
public class PasswordInitializer implements CommandLineRunner {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 重置admin密码为123456
        resetAdminPassword();
    }

    private void resetAdminPassword() {
        try {
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getUsername, "admin");
            SysUser admin = sysUserMapper.selectOne(wrapper);
            
            if (admin != null) {
                String encodedPassword = passwordEncoder.encode("123456");
                admin.setPassword(encodedPassword);
                sysUserMapper.updateById(admin);
                log.info("管理员密码已重置为: 123456");
                log.info("加密后的密码: {}", encodedPassword);
            }
        } catch (Exception e) {
            log.error("重置管理员密码失败", e);
        }
    }
}
