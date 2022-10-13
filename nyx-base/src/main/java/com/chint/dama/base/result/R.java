package com.chint.dama.base.result;

import com.chint.dama.base.enums.ResultCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class R implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 状态码
     */
    private Integer status;
    /**
     * 字符串状态码，兼容字段
     */
    private String code;
    /**
     * 用户可见的异常，友好的提示信息,
     */
    private String message;

    /**
     * 把ResultCode枚举转换为ResResult
     */
    public void setResultCode(ResultCodeEnum codeEnum) {
        this.setStatus(codeEnum.getStatus());
        this.setMessage(codeEnum.getMessage());
    }

}
