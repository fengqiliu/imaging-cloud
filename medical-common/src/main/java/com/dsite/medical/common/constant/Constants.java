package com.dsite.medical.common.constant;

/**
 * 通用常量
 */
public class Constants {

    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    /**
     * 成功标记
     */
    public static final int SUCCESS = 200;

    /**
     * 失败标记
     */
    public static final int FAIL = 500;

    /**
     * 登录成功
     */
    public static final int LOGIN_SUCCESS = 0;

    /**
     * 登录失败
     */
    public static final int LOGIN_FAIL = 1;

    /**
     * 验证码有效期（分钟）
     */
    public static final long CAPTCHA_EXPIRATION = 2;

    /**
     * 字符间间隔符
     */
    public static final String SEPARATOR = ":";

    /**
     * 角色标识字符长度
     */
    public static final int ROLE_KEY_LENGTH = 100;

    /**
     * 用户名长度限制
     */
    public static final int USERNAME_LENGTH = 30;

    /**
     * 密码长度限制
     */
    public static final int PASSWORD_LENGTH = 100;

    /**
     * 住户姓名限制
     */
    public static final int NAME_LENGTH = 20;

    /**
     * 手机号码限制
     */
    public static final int PHONE_LENGTH = 11;

    /**
     * 邮箱限制
     */
    public static final int EMAIL_LENGTH = 50;

    /**
     * 部门ancestors长度
     */
    public static final int ANCESTORS_LENGTH = 200;

    /**
     * 身份
     */
    public static final String[] ID_TYPE = {"居民身份证", "军官证", "护照", "士兵证", "港澳通行证"};

    /**
     * 通用UP状态
     */
    public static final String STATUS_NORMAL = "0";
    public static final String STATUS_DISABLE = "1";

    /**
     * 用户类型
     */
    public static final String USER_TYPE_SYSTEM = "00";
    public static final String USER_TYPE_NORMAL = "01";

    /**
     * 数据权限
     */
    public static final String DATA_SCOPE_ALL = "1";
    public static final String DATA_SCOPE_DEPT = "2";
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "3";
    public static final String DATA_SCOPE_SELF = "4";

    /**
     * 菜单类型
     */
    public static final String MENU_TYPE_DIR = "M";
    public static final String MENU_TYPE_MENU = "C";
    public static final String MENU_TYPE_BUTTON = "F";

    /**
     * 请求头
     */
    public static final String HEADER = "Authorization";

    /**
     * 请求头前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 缓存前缀
     */
    public static final String CACHE_PREFIX = "medical:";

    /**
     * 验证码
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 验证码有效期（分钟）
     */
    public static final long CAPTCHA_EXPIRE_TIME = 2;

    /**
     * 分享默认过期天数
     */
    public static final int SHARE_DEFAULT_EXPIRE_DAYS = 7;
}
