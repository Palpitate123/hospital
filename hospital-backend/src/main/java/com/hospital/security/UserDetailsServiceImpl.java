package com.hospital.security;

import com.hospital.entity.SysUser;
import com.hospital.mapper.SysUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 用户详情服务实现类
 * 实现Spring Security的UserDetailsService接口，提供用户认证信息
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 根据用户名加载用户详情
     * 用于Spring Security认证
     * 
     * @param username 用户名
     * @return 用户详情
     * @throws UsernameNotFoundException 用户未找到异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户信息（包含角色信息）
        SysUser user = sysUserMapper.selectByUsernameWithRole(username);

        if (user == null) {
            log.error("用户不存在: {}", username);
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 检查账号状态
        if (user.getStatus() == 0) {
            log.error("账号已停用: {}", username);
            throw new UsernameNotFoundException("账号已停用: " + username);
        }

        // 构建角色权限（从用户对象中获取角色编码）
        String roleCode = "patient"; // 默认角色
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleCode);

        // 返回Spring Security的User对象
        return new User(
                user.getUsername(),
                user.getPassword(),
                user.getStatus() == 1, // 账号是否可用
                true, // 账号是否未过期
                true, // 凭证是否未过期
                true, // 账号是否未锁定
                Collections.singletonList(authority)
        );
    }
}
