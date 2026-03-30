package com.dsite.medical.common.utils;

import cn.hutool.core.lang.UUID;

/**
 * ID生成工具类
 */
public class IdUtils {

    /**
     * 获取UUID字符串 (去横线)
     */
    public static String uuid() {
        return UUID.fastUUID().toString().replace("-", "");
    }

    /**
     * 生成患者ID
     */
    public static String patientId() {
        return "P" + SnowflakeIdWorker.getNextId();
    }

    /**
     * 生成检查ID
     */
    public static String examId() {
        return "E" + SnowflakeIdWorker.getNextId();
    }

    /**
     * 生成影像ID
     */
    public static String imageId() {
        return "I" + SnowflakeIdWorker.getNextId();
    }

    /**
     * 生成报告ID
     */
    public static String reportId() {
        return "R" + SnowflakeIdWorker.getNextId();
    }

    /**
     * 生成分享ID
     */
    public static String shareId() {
        return "S" + SnowflakeIdWorker.getNextId();
    }

    /**
     * 生成分享编号
     */
    public static String shareNo() {
        return uuid().substring(0, 12).toUpperCase();
    }

    /**
     * 生成AI解读ID
     */
    public static String aiInterpretId() {
        return "AI" + SnowflakeIdWorker.getNextId();
    }

    /**
     * 生成6位数字验证码
     */
    public static String accessCode() {
        return String.format("%06d", (int) (Math.random() * 1000000));
    }
}
