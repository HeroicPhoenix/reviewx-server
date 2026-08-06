package com.lvwyh.reviewx.web.controller.auth;

import com.lvwyh.reviewx.web.ao.auth.ChangePasswordAO;
import com.lvwyh.reviewx.web.ao.auth.LoginAO;
import com.lvwyh.reviewx.web.common.response.ApiResponse;
import com.lvwyh.reviewx.web.security.ClientAddressResolver;
import com.lvwyh.reviewx.web.security.LoginUserContext;
import com.lvwyh.reviewx.web.security.RequirePermission;
import com.lvwyh.reviewx.web.service.auth.AuthService;
import com.lvwyh.reviewx.web.vo.auth.LoginVO;
import com.lvwyh.reviewx.web.vo.auth.MeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;

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
    private final ClientAddressResolver clientAddressResolver;

    public AuthController(AuthService authService,
                          ClientAddressResolver clientAddressResolver) {
        this.authService = authService;
        this.clientAddressResolver = clientAddressResolver;
    }

    /**
     * 登录接口。
     *
     * 成功后返回 JWT，后续请求需要携带 Authorization: Bearer <token>。
     */
    @Operation(summary = "登录并获取JWT")
    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginAO ao, HttpServletRequest request) {
        String clientAddress = clientAddressResolver.resolve(request);
        // 来源地址仅写服务端日志，登录失败响应始终不向前端暴露管理员白名单判断结果。
        log.info("Login request: username={}, remoteAddress={}, xRealIp={}, xForwardedFor={}, clientAddress={}",
                ao.getUsername(), logValue(request.getRemoteAddr()), logValue(request.getHeader("X-Real-IP")),
                logValue(request.getHeader("X-Forwarded-For")), logValue(clientAddress));
        return ApiResponse.success("登录成功", authService.login(ao.getUsername(), ao.getPassword(), clientAddress));
    }

    /** 清理并限制不可信请求头的日志长度，避免伪造换行或超长日志。 */
    private String logValue(String value) {
        if (!StringUtils.hasText(value)) {
            return "-";
        }
        String safeValue = value.replace('\r', ' ').replace('\n', ' ').trim();
        return safeValue.length() <= 255 ? safeValue : safeValue.substring(0, 255);
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
    @RequirePermission("auth:change-password")
    @PostMapping("/changePassword")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordAO ao) {
        authService.changePassword(LoginUserContext.require().getUserId(), ao.getOldPassword(), ao.getNewPassword());
        return ApiResponse.success("修改成功，请重新登录", null);
    }
}
