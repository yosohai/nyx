package com.chint.dama.base.result;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

public class BaseResp implements Serializable {
    private Result result;

    public BaseResp() {

        Result result = new Result();
        result.setStatus(0);
        result.setMessage("success");
        this.result = result;
    }

    public BaseResp(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return this.result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String toString() {
        return JSON.toJSONString(this);
    }
}