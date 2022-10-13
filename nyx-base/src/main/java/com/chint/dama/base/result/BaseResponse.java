package com.chint.dama.base.result;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.chint.dama.base.enums.ResultCodeEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

@Data
@Accessors(chain = true)
public class BaseResponse implements Serializable {
    private String id;
    private String queryId;
    private int total;
    private Result result;

    public static BaseResponse create(ResultCodeEnum resultCode) {
        BaseResponse baseResponse = new BaseResponse();
        Result result = new Result();
        result.setStatus(resultCode.getStatus());
        result.setMessage(resultCode.getMessage());
        result.setJsonData(new JSONObject());
        return baseResponse.setResult(result);
    }

    public static BaseResponse create(ResultCodeEnum resultCode, JSONObject data) {
        BaseResponse baseResponse = new BaseResponse();
        Result result = new Result();
        result.setStatus(resultCode.getStatus());
        result.setMessage(resultCode.getMessage());
        result.setJsonData(data);

        return baseResponse.setResult(result);
    }

    public static BaseResponse create(ResultCodeEnum resultCode, String message) {
        BaseResponse baseResponse = new BaseResponse();
        Result result = new Result();
        result.setStatus(resultCode.getStatus());
        result.setMessage(message);
        result.setJsonData(new JSONObject());
        return baseResponse.setResult(result);
    }

    public static BaseResponse create(Integer code, String msg, JSONObject data) {
        BaseResponse baseResponse = new BaseResponse();
        Result result = new Result();
        result.setStatus(Integer.valueOf(code));
        result.setMessage(msg);
        result.setJsonData(data);
        return baseResponse.setResult(result);
    }

    public static BaseResponse succeed(Object data) {
        BaseResponse baseResponse = new BaseResponse();
        Result result = new Result();
        result.setStatus(ResultCodeEnum.SUCCESS.status());
        result.setMessage(ResultCodeEnum.SUCCESS.getMessage());
        result.setJsonData(data);
        return baseResponse.setResult(result);
    }

    public static BaseResponse verifyFail(Map<String, StringBuffer> map) {
        BaseResponse baseResponse = new BaseResponse();
        Result result = new Result();
        result.setStatus(ResultCodeEnum.PARAM_VERIFY_FAILED.status());
        String errMsg = "";
        if (CollUtil.isNotEmpty(map)) {
            Collection<StringBuffer> values = map.values();
            errMsg = String.join(",", values);
        }
        result.setMessage(errMsg);
        result.setJsonData(new JSONObject());
        return baseResponse.setResult(result);
    }
}
