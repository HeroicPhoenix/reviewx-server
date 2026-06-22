package com.lvwyh.reviewx.web.controller.user;

import com.lvwyh.reviewx.web.ao.user.CreateUserAO;
import com.lvwyh.reviewx.web.common.response.ApiResponse;
import com.lvwyh.reviewx.web.security.RequirePermission;
import com.lvwyh.reviewx.web.service.user.UserManageService;
import com.lvwyh.reviewx.web.vo.user.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 管理员用户管理接口。
 */
@Tag(name = "用户管理")
@Validated
@RestController
@RequestMapping("/api/user")
public class UserManageController {

    private final UserManageService userManageService;

    public UserManageController(UserManageService userManageService) {
        this.userManageService = userManageService;
    }

    /**
     * 查询用户列表。
     */
    @Operation(summary = "查询用户列表")
    @RequirePermission("user:manage")
    @GetMapping("/list")
    public ApiResponse<List<UserVO>> list() {
        return ApiResponse.success("查询成功", userManageService.listUsers());
    }

    /**
     * 新增普通用户。
     */
    @Operation(summary = "新增普通用户")
    @RequirePermission("user:manage")
    @PostMapping("/create")
    public ApiResponse<UserVO> create(@Valid @RequestBody CreateUserAO ao) {
        return ApiResponse.success("新增成功", userManageService.createNormalUser(ao));
    }
}
