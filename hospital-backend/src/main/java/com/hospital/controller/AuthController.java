package com.hospital.controller;

import com.hospital.common.Result;
import com.hospital.dto.LoginDTO;
import com.hospital.dto.RegisterDTO;
import com.hospital.entity.SysUser;
import com.hospital.security.JwtTokenProvider;
import com.hospital.service.AuthService;
import com.hospital.vo.LoginVO;
import com.hospital.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理用户登录、注册、密码修改等认证相关请求
 * 
 * @author 徐凌
 * @version 1.0.0
 */
@Slf4j
@Api(tags = "认证管理")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * 用户登录
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Validated @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = authService.login(loginDTO);
        return Result.success("登录成功", loginVO);
    }

    /**
     * 用户注册
     */
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public Result<String> register(@Validated @RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO);
        return Result.success("注册成功");
    }

    /**
     * 获取当前登录用户信息
     */
    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public Result<UserVO> getUserInfo(@AuthenticationPrincipal SysUser user) {
        UserVO userVO = authService.getCurrentUser(user.getId());
        return Result.success(userVO);
    }

    /**
     * 修改密码
     */
    @ApiOperation("修改密码")
    @PutMapping("/password")
    public Result<String> updatePassword(@AuthenticationPrincipal SysUser user,
                                       @RequestParam String oldPassword,
                                       @RequestParam String newPassword) {
        authService.updatePassword(user.getId(), oldPassword, newPassword);
        return Result.success("密码修改成功");
    }

    /**
     * 更新用户信息
     */
    @ApiOperation("更新用户信息")
    @PutMapping("/info")
    public Result<String> updateUserInfo(@AuthenticationPrincipal SysUser user,
                                       @RequestParam(required = false) String nickName,
                                       @RequestParam(required = false) String remark) {
        authService.updateUserInfo(user.getId(), nickName, remark);
        return Result.success("信息更新成功");
    }

    /**
     * 退出登录
     */
    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success("退出成功");
    }
}
