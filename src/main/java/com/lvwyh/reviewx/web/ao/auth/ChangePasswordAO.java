package com.lvwyh.reviewx.web.ao.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 修改密码请求参数。
 *
 * 当前登录用户需要提供旧密码进行二次校验，修改成功后旧 Token 会失效。
 */
@Schema(description = "修改密码入参")
public class ChangePasswordAO implements Serializable {

    /** 当前密码，用于确认操作者身份。 */
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    /** 新密码，服务端会使用 BCrypt 加密后保存。 */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 64, message = "新密码长度必须在6到64位之间")
    private String newPassword;

    public String getOldPassword() { return oldPassword; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
