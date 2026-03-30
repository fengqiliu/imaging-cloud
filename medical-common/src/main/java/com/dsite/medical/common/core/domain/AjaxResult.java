package com.dsite.medical.common.core.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 响应结果
 */
@Data
public class AjaxResult extends HashMap<String, Object> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int SUCCESS = 200;
    public static final int ERROR = 500;

    public AjaxResult() {
    }

    public AjaxResult(int code, String msg) {
        super.put("code", code);
        super.put("msg", msg);
    }

    public AjaxResult(int code, String msg, Object data) {
        super.put("code", code);
        super.put("msg", msg);
        if (data != null) {
            super.put("data", data);
        }
    }

    public static AjaxResult success() {
        return new AjaxResult(SUCCESS, "操作成功");
    }

    public static AjaxResult success(String msg) {
        return new AjaxResult(SUCCESS, msg);
    }

    public static AjaxResult success(Object data) {
        return new AjaxResult(SUCCESS, "操作成功", data);
    }

    public static AjaxResult success(String msg, Object data) {
        return new AjaxResult(SUCCESS, msg, data);
    }

    public static AjaxResult error() {
        return new AjaxResult(ERROR, "操作失败");
    }

    public static AjaxResult error(String msg) {
        return new AjaxResult(ERROR, msg);
    }

    public static AjaxResult error(int code, String msg) {
        return new AjaxResult(code, msg);
    }
}
