package com.lvwyh.reviewx.web.ao.user;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 管理员新增普通用户请求参数。
 */
public class CreateUserAO {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 64, message = "用户名长度需为 3-64 位")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 64, message = "密码长度需为 6-64 位")
    private String password;

    @Size(max = 100, message = "昵称最多 100 个字符")
    private String nickName;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }
}
