package com.lvwyh.reviewx.web.service.user;

import com.lvwyh.reviewx.web.ao.user.CreateUserAO;
import com.lvwyh.reviewx.web.vo.user.UserVO;

import java.util.List;

/**
 * 管理员用户管理服务。
 */
public interface UserManageService {

    /** 创建普通用户。 */
    UserVO createNormalUser(CreateUserAO ao);

    /** 查询用户列表。 */
    List<UserVO> listUsers();
}
