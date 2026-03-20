package com.hospital.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hospital.common.ResultCode;
import com.hospital.dto.LoginDTO;
import com.hospital.dto.RegisterDTO;
import com.hospital.entity.SysRole;
import com.hospital.entity.SysUser;
import com.hospital.entity.SysUserRole;
import com.hospital.exception.BusinessException;
import com.hospital.mapper.SysRoleMapper;
import com.hospital.mapper.SysUserMapper;
import com.hospital.mapper.SysUserRoleMapper;
import com.hospital.security.JwtTokenProvider;
import com.hospital.service.AuthService;
import com.hospital.vo.LoginVO;
import com.hospital.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证服务实现类
 * 实现用户登录、注册、密码修改等认证相关业务逻辑
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Slf4j
@Service
public class AuthServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements AuthService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    /**
     * 用户登录
     * 验证用户名密码，生成JWT令牌
     */
    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 查询用户信息（包含角色）
        SysUser user = sysUserMapper.selectByUsernameWithRole(loginDTO.getUsername());

        // 验证用户是否存在
        if (user == null) {
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // 验证密码是否正确
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.LOGIN_ERROR);
        }

        // 验证账号状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        // 获取用户角色信息
        String roleCode = user.getRoleCode() != null ? user.getRoleCode() : "patient";
        String roleName = user.getRoleName() != null ? user.getRoleName() : "患者";

        // 生成JWT令牌
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), roleCode);

        // 构建返回结果
        LoginVO loginVO = new LoginVO();
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setNickName(user.getNickName());
        loginVO.setRoleCode(roleCode);
        loginVO.setRoleName(roleName);
        loginVO.setToken(token);
        loginVO.setExpiresIn(jwtExpiration);

        log.info("用户登录成功: {}", user.getUsername());
        return loginVO;
    }

    /**
     * 用户注册
     * 创建新用户账号，默认分配患者角色
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(RegisterDTO registerDTO) {
        // 验证两次密码是否一致
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new BusinessException("两次密码输入不一致");
        }

        // 检查用户名是否已存在
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, registerDTO.getUsername());
        if (this.count(wrapper) > 0) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        // 创建用户账号
        SysUser user = new SysUser();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setNickName(StringUtils.hasText(registerDTO.getNickName()) ? registerDTO.getNickName() : registerDTO.getUsername());
        user.setStatus(1);
        user.setRemark("用户自主注册");

        // 保存用户
        this.save(user);

        // 查询患者角色
        LambdaQueryWrapper<SysRole> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(SysRole::getRoleCode, "patient");
        SysRole patientRole = sysRoleMapper.selectOne(roleWrapper);

        if (patientRole != null) {
            // 分配患者角色
            SysUserRole userRole = new SysUserRole();
            userRole.setUserId(user.getId());
            userRole.setRoleId(patientRole.getId());
            sysUserRoleMapper.insert(userRole);
        }

        log.info("用户注册成功: {}", user.getUsername());
        return true;
    }

    /**
     * 修改密码
     * 验证原密码后更新新密码
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePassword(Long userId, String oldPassword, String newPassword) {
        // 查询用户
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证原密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 验证新密码不能与原密码相同
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BusinessException("新密码不能与原密码相同");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(user);

        log.info("用户修改密码成功: {}", user.getUsername());
        return true;
    }

    /**
     * 获取当前登录用户信息
     */
    @Override
    public UserVO getCurrentUser(Long userId) {
        SysUser user = sysUserMapper.selectByIdWithRole(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        // 获取角色信息
        String roleCode = user.getRoleCode() != null ? user.getRoleCode() : "patient";
        String roleName = user.getRoleName() != null ? user.getRoleName() : "患者";
        userVO.setRoleCode(roleCode);
        userVO.setRoleName(roleName);

        return userVO;
    }

    /**
     * 更新用户信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserInfo(Long userId, String nickName, String remark) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (StringUtils.hasText(nickName)) {
            user.setNickName(nickName);
        }
        if (remark != null) {
            user.setRemark(remark);
        }

        this.updateById(user);
        return true;
    }

    /**
     * 分页查询用户列表
     */
    @Override
    public Page<UserVO> getUserPage(Page<SysUser> page, String username, Integer status) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), SysUser::getUsername, username);
        wrapper.eq(status != null, SysUser::getStatus, status);
        wrapper.orderByDesc(SysUser::getCreateTime);

        Page<SysUser> userPage = this.page(page, wrapper);

        // 转换为VO
        Page<UserVO> voPage = new Page<>();
        BeanUtils.copyProperties(userPage, voPage, "records");

        List<UserVO> voList = userPage.getRecords().stream().map(user -> {
            UserVO vo = new UserVO();
            BeanUtils.copyProperties(user, vo);
            // 获取用户角色信息
            SysUser userWithRole = sysUserMapper.selectByIdWithRole(user.getId());
            if (userWithRole != null) {
                vo.setRoleCode("patient");
                vo.setRoleName("患者");
            }
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 启用/停用用户账号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserStatus(Long userId, Integer status) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        user.setStatus(status);
        this.updateById(user);

        log.info("用户账号状态更新: userId={}, status={}", userId, status);
        return true;
    }
}
