package com.chint.dama.base.result;

import com.chint.dama.base.enums.ResultCodeEnum;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorResult extends R {

    /**
     * 异常的名字
     */
    private String exception;

    private String url;

    /**
     * 异常堆栈信息
     */
    private String errors;

    private Map<String, Object> map = Maps.newHashMap();

    /**
     * 对异常提示语进行封装
     */
    public static ErrorResult fail(ResultCodeEnum resultCode, Throwable e, String message) {
        ErrorResult result = ErrorResult.fail(resultCode, e);
        result.setMessage(message);
        return result;
    }

    /**
     * 对异常枚举进行封装
     */
    public static ErrorResult fail(ResultCodeEnum resultCode, Throwable e) {

        ErrorResult result = new ErrorResult();
        result.setMessage(resultCode.message());
        result.setStatus(resultCode.status());
        result.setException(e.getClass().getName());
        result.setErrors(Throwables.getStackTraceAsString(e));
        return result;
    }

    /**
     * 失败，指定status、msg
     */
    public static Result fail(Integer status, String msg) {
        Result result = new Result();
        result.setStatus(status);
        result.setMessage(msg);
        return result;
    }

    /**
     * 失败，指定status、msg
     */
    public static Result fail(Integer status, String msg, Object data) {
        Result<Object> result = new Result();
        result.setStatus(status);
        result.setMessage(msg);
        return result;
    }

    /**
     * 失败，指定ResultCode枚举
     */
    public static Result fail(ResultCodeEnum resultCode) {
        Result<Object> result = new Result();
        result.setResultCode(resultCode);
        return result;
    }
}
