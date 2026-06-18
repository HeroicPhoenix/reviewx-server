package com.lvwyh.reviewx.web.controller.auth;

import com.lvwyh.reviewx.web.ao.auth.ChangePasswordAO;
import com.lvwyh.reviewx.web.ao.auth.LoginAO;
import com.lvwyh.reviewx.web.common.response.ApiResponse;
import com.lvwyh.reviewx.web.security.LoginUserContext;
import com.lvwyh.reviewx.web.security.RequirePermission;
import com.lvwyh.reviewx.web.service.auth.AuthService;
import com.lvwyh.reviewx.web.vo.auth.LoginVO;
import com.lvwyh.reviewx.web.vo.auth.MeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 认证接口控制器。
 *
 * 提供登录、查询当前用户、登出和修改密码接口。
 */
@Tag(name = "认证管理")
@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LogManager.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 登录接口。
     *
     * 成功后返回 JWT，后续请求需要携带 Authorization: Bearer <token>。
     */
    @Operation(summary = "登录并获取JWT")
    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginAO ao) {
        log.info("Login request: username={}", ao.getUsername());
        return ApiResponse.success("登录成功", authService.login(ao.getUsername(), ao.getPassword()));
    }

    /**
     * 查询当前登录用户信息。
     */
    @Operation(summary = "获取当前登录人信息")
    @RequirePermission("auth:me")
    @GetMapping("/me")
    public ApiResponse<MeVO> me() {
        return ApiResponse.success("查询成功", authService.me(LoginUserContext.require().getUserId()));
    }

    /**
     * 登出接口。
     *
     * 通过提升 Token 版本使当前用户历史 Token 失效。
     */
    @Operation(summary = "退出登录")
    @RequirePermission("auth:logout")
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        authService.logout(LoginUserContext.require().getUserId());
        return ApiResponse.success("退出成功", null);
    }

    /**
     * 修改当前用户密码。
     *
     * 修改成功后旧 Token 失效，需要重新登录。
     */
    @Operation(summary = "修改当前登录用户密码")
    @PostMapping("/changePassword")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordAO ao) {
        authService.changePassword(LoginUserContext.require().getUserId(), ao.getOldPassword(), ao.getNewPassword());
        return ApiResponse.success("修改成功，请重新登录", null);
    }
}
