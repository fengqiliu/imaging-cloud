package com.dsite.medical.framework.web.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录用户信息
 */
@Data
public class LoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 状态 */
    private String status;

    /** 院区ID */
    private Long deptId;
}
