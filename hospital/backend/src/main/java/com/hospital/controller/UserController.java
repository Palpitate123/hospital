package com.hospital.controller;

import com.hospital.dto.LoginDTO;
import com.hospital.dto.LoginResponseDTO;
import com.hospital.entity.Role;
import com.hospital.entity.User;
import com.hospital.exception.GlobalException;
import com.hospital.service.UserService;
import com.hospital.utils.JWTUtils;
import com.hospital.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = {"http://localhost:8081", "http://192.168.0.123:8081"}, allowCredentials = "true") // 添加跨域支持
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    @Autowired
    private UserService userService;

    // @Autowired
    // private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtils jwtUtils;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        try {
            // 记录登录尝试
            logger.info("Login attempt: username=" + loginDTO.getUsername());
            
            // 查找用户
            User user = userService.findByUsername(loginDTO.getUsername());
            logger.info("User found: " + (user != null ? "Yes" : "No"));
            
            if (user == null) {
                logger.warn("Login failed: User not found for username=" + loginDTO.getUsername());
                throw new GlobalException("用户名或密码错误");
            }

            // 检查用户状态
            if (user.getStatus() == 0) {
                logger.warn("Login failed: User account disabled for username=" + loginDTO.getUsername());
                throw new GlobalException("账号已被禁用");
            }

            // 直接比较密码（不加密）
            if (!loginDTO.getPassword().equals(user.getPassword())) {
                logger.warn("Login failed: Invalid password for username=" + loginDTO.getUsername());
                throw new GlobalException("用户名或密码错误");
            }

            try {
                // 从数据库获取用户角色
                List<Role> userRoles = userService.getUserRoles(user.getId());
                logger.info("Retrieved user roles: " + (userRoles != null ? userRoles.size() : 0));
                
                String roleName = "患者";
                String roleCode = "ROLE_PATIENT";
                
                if (userRoles != null && !userRoles.isEmpty()) {
                    Role firstRole = userRoles.get(0);
                    roleName = firstRole.getRoleName();
                    roleCode = firstRole.getRoleCode();
                    logger.info("User role: " + roleName + " (" + roleCode + ")");
                }

                // 生成JWT token
                Map<String, Object> claims = new HashMap<>();
                claims.put("roleCode", roleCode);
                String token = jwtUtils.generateToken(user.getId(), claims);
                logger.info("Token generated successfully for user=" + loginDTO.getUsername());

                // 构建响应
                LoginResponseDTO responseDTO = new LoginResponseDTO();
                responseDTO.setToken(token);
                responseDTO.setUserId(user.getId());
                responseDTO.setUsername(user.getUsername());
                responseDTO.setRealName(user.getRealName());
                responseDTO.setRoleName(roleName);
                responseDTO.setRoleCode(roleCode);
                
                logger.info("Login successful for user=" + loginDTO.getUsername());
                return Result.success(responseDTO);
            } catch (Exception e) {
                logger.error("Error processing login after authentication for user={}", loginDTO.getUsername(), e);
                throw new GlobalException("登录处理失败: " + e.getMessage());
            }
        } catch (GlobalException e) {
            // 直接抛出自定义异常
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during login attempt", e);
            throw new GlobalException("登录过程中发生错误: " + e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody Map<String, Object> request) {
        User user = new User();
        user.setUsername((String) request.get("username"));
        user.setPassword((String) request.get("password"));
        user.setRealName((String) request.get("realName"));
        user.setPhone((String) request.get("phone"));
        user.setEmail((String) request.get("email"));
        // 直接使用gender字符串值
        String genderStr = (String) request.get("gender");
        if ("male".equals(genderStr) || "female".equals(genderStr)) {
            user.setGender(genderStr);
        } else {
            user.setGender("male"); // 默认设为男性
        }
        boolean result = userService.register(user, 1L); // 1L 是患者角色ID
        return result ? Result.success() : Result.fail("注册失败");
    }

    /**
     * 医生注册
     */
    @PostMapping("/register-doctor")
    public Result registerDoctor(@RequestBody Map<String, Object> request) {
        User user = new User();
        user.setUsername((String) request.get("username"));
        user.setPassword((String) request.get("password"));
        user.setRealName((String) request.get("realName"));
        user.setPhone((String) request.get("phone"));
        user.setEmail((String) request.get("email"));
        // 直接使用gender字符串值
        String genderStr = (String) request.get("gender");
        if ("male".equals(genderStr) || "female".equals(genderStr)) {
            user.setGender(genderStr);
        } else {
            user.setGender("male"); // 默认设为男性
        }

        Long deptId = Long.valueOf(request.get("deptId").toString());
        String title = (String) request.get("title");

        boolean result = userService.registerDoctor(user, deptId, title);
        return result ? Result.success() : Result.fail("注册失败");
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getById(userId);
        if (user == null) {
            throw new GlobalException("用户不存在");
        }
        // 隐藏敏感信息
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    public Result updateUser(@RequestBody User user, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        user.setId(userId);
        boolean result = userService.updateUser(user);
        return result ? Result.success() : Result.fail("更新失败");
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result changePassword(@RequestBody Map<String, String> request, HttpServletRequest req) {
        Long userId = (Long) req.getAttribute("userId");
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        // 验证旧密码（不加密）
        User user = userService.getById(userId);
        if (!oldPassword.equals(user.getPassword())) {
            throw new GlobalException("原密码错误");
        }

        boolean result = userService.resetPassword(userId, newPassword);
        return result ? Result.success() : Result.fail("修改失败");
    }
    
    /**
     * 获取所有用户（管理员功能）
     */
    @GetMapping("/all")
    public Result<List<User>> getAllUsers(HttpServletRequest request) {
        // 获取所有用户
        List<User> users = userService.getAllUsers();
        // 隐藏敏感信息
        for (User user : users) {
            user.setPassword(null);
        }
        return Result.success(users);
    }

    /**
     * 更新用户信息（管理员功能）
     */
    @PutMapping("/update/{userId}")
    public Result updateUserByAdmin(@PathVariable Long userId, @RequestBody User user) {
        user.setId(userId);
        boolean result = userService.updateUser(user);
        return result ? Result.success() : Result.fail("更新失败");
    }

    /**
     * 更新用户状态（管理员功能）
     */
    @PutMapping("/status/{userId}")
    public Result updateUserStatus(@PathVariable Long userId, @RequestBody Map<String, Integer> request) {
        User user = new User();
        user.setId(userId);
        user.setStatus(request.get("status"));
        boolean result = userService.updateById(user);
        return result ? Result.success() : Result.fail("更新失败");
    }

    /**
     * 删除用户（管理员功能）
     */
    @DeleteMapping("/delete/{userId}")
    public Result deleteUser(@PathVariable Long userId) {
        boolean result = userService.removeById(userId);
        return result ? Result.success() : Result.fail("删除失败");
    }

    /**
     * 重置密码（管理员功能）
     */
    @PutMapping("/reset-password/{userId}")
    public Result resetPasswordByAdmin(@PathVariable Long userId) {
        boolean result = userService.resetPassword(userId, "123456");
        return result ? Result.success() : Result.fail("重置失败");
    }
}