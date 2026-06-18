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

    @Operation(summary = "登录并获取JWT")
    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginAO ao) {
        log.info("Login request: username={}", ao.getUsername());
        return ApiResponse.success("登录成功", authService.login(ao.getUsername(), ao.getPassword()));
    }

    @Operation(summary = "获取当前登录人信息")
    @RequirePermission("auth:me")
    @GetMapping("/me")
    public ApiResponse<MeVO> me() {
        return ApiResponse.success("查询成功", authService.me(LoginUserContext.require().getUserId()));
    }

    @Operation(summary = "退出登录")
    @RequirePermission("auth:logout")
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        authService.logout(LoginUserContext.require().getUserId());
        return ApiResponse.success("退出成功", null);
    }

    @Operation(summary = "修改当前登录用户密码")
    @PostMapping("/changePassword")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordAO ao) {
        authService.changePassword(LoginUserContext.require().getUserId(), ao.getOldPassword(), ao.getNewPassword());
        return ApiResponse.success("修改成功，请重新登录", null);
    }
}
