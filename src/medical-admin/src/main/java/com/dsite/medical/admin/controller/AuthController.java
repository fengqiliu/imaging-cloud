package com.dsite.medical.admin.controller;

import com.dsite.medical.common.core.domain.AjaxResult;
import com.dsite.medical.framework.security.JwtToken;
import com.dsite.medical.framework.web.domain.LoginUser;
import com.dsite.medical.system.domain.entity.SysUser;
import com.dsite.medical.system.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证Controller
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtToken jwtToken;

    @Operation(summary = "登录")
    @PostMapping("/login")
    public AjaxResult login(@RequestBody Map<String, String> loginBody) {
        String username = loginBody.get("username");
        String password = loginBody.get("password");

        // 查询用户
        SysUser user = userService.selectUserByUserName(username);
        if (user == null) {
            return AjaxResult.error("用户不存在");
        }
        if (!"0".equals(user.getStatus())) {
            return AjaxResult.error("用户已被禁用");
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return AjaxResult.error("密码错误");
        }

        // 生成Token
        String token = jwtToken.createToken(username, user.getUserId());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getUserId());
        result.put("username", user.getUserName());
        result.put("nickName", user.getNickName());

        return AjaxResult.success(result);
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/info")
    public AjaxResult getUserInfo() {
        // 这里从SecurityContextHolder获取当前登录用户
        // 简化实现，返回固定信息
        return AjaxResult.success(new HashMap<String, Object>() {{
            put("roles", new String[]{"admin"});
            put("username", "admin");
            put("nickName", "管理员");
            put("avatar", "");
        }});
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public AjaxResult logout() {
        return AjaxResult.success();
    }
}
