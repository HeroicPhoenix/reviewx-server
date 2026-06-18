package com.lvwyh.reviewx.web.ao.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 登录请求参数。
 *
 * 对应 POST /api/auth/login，请求体中必须传入用户名和密码。
 */
@Schema(description = "登录入参")
public class LoginAO implements Serializable {

    /** 登录用户名。 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 登录密码明文，只用于本次校验，不会落库。 */
    @NotBlank(message = "密码不能为空")
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
