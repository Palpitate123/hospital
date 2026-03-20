package com.hospital.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hospital.dto.LoginDTO;
import com.hospital.dto.RegisterDTO;
import com.hospital.entity.SysUser;
import com.hospital.vo.LoginVO;
import com.hospital.vo.UserVO;

/**
 * 认证服务接口
 * 处理用户登录、注册、密码修改等认证相关业务
 * 
 * @author 徐凌
 * @version 1.0.0
 */
public interface AuthService extends IService<SysUser> {

    /**
     * 用户登录
     * 验证用户名密码，生成JWT令牌
     * 
     * @param loginDTO 登录请求参数
     * @return 登录响应信息（包含令牌）
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户注册
     * 创建新用户账号，默认分配患者角色
     * 
     * @param registerDTO 注册请求参数
     * @return 是否注册成功
     */
    boolean register(RegisterDTO registerDTO);

    /**
     * 修改密码
     * 验证原密码后更新新密码
     * 
     * @param userId 用户ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    boolean updatePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 获取当前登录用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getCurrentUser(Long userId);

    /**
     * 更新用户信息
     * 
     * @param userId 用户ID
     * @param nickName 昵称
     * @param remark 备注
     * @return 是否更新成功
     */
    boolean updateUserInfo(Long userId, String nickName, String remark);

    /**
     * 分页查询用户列表
     * 
     * @param page 分页参数
     * @param username 用户名（模糊查询）
     * @param status 状态
     * @return 分页结果
     */
    Page<UserVO> getUserPage(Page<SysUser> page, String username, Integer status);

    /**
     * 启用/停用用户账号
     * 
     * @param userId 用户ID
     * @param status 状态
     * @return 是否操作成功
     */
    boolean updateUserStatus(Long userId, Integer status);
}
