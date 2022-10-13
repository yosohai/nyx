package com.chint.dama.base.enums;

/**
 * 定义org.springframework.http.HttpStatus
 */
public enum ResultCodeEnum {

    // 应用模块公共
    SUCCESS(0, "成功"),
    FAILED(-1, "系统开小差了，建议您联系管理员"),
    SYSTEM_BUSY(1, "系统繁忙，请稍后再试"),
    PARAMETER_PROCESSOR_EXCEPTION(2, "参数处理异常"),
    BODY_PROCESSOR_EXCEPTION(3, "请求体参数处理异常"),

    //web
    WEB_400(400, "错误请求"),
    WEB_401(401, "访问未得到授权"),
    WEB_404(404, "资源未找到"),
    WEB_500(500, "服务器内部错误"),
    SERVER_BUSY(503, "服务器正忙，请稍后再试!"),

    /*常见异常:600-999*/
    RUNTIME_EXCEPTION(600, "未知异常"),
    NULL_POINTER_EXCEPTION(601, "空指针"),
    CLASS_CAST_EXCEPTION(602, "类型转换异常"),
    IO_EXCEPTION(603, "IO异常"),
    NO_SUCH_METHOD_EXCEPTION(604, "方法存在异常"),
    INDEX_OUT_OF_BOUNDS_EXCEPTION(605, "数组越界"),
    STACK_OVERFLOW_ERROR(606, "栈溢出"),
    ARITHMETIC_EXCEPTION(607, "算术运算错误"),
    OTHER_EXCEPTION(999, "其他异常"),

    /* 参数错误：10001-19999 */
    //sign error
    SIGN_NO_APPID(10001, "appId不能为空"),
    SIGN_NO_TIMESTAMP(10002, "timestamp不能为空"),
    SIGN_NO_SIGN(10003, "sign不能为空"),
    SIGN_NO_NONCE(10004, "nonce不能为空"),
    SIGN_TIMESTAMP_INVALID(10005, "timestamp无效"),
    SIGN_DUPLICATION(10006, "重复的请求"),
    SIGN_VERIFY_FAIL(10007, "sign签名校验失败"),
    PARAM_IS_INVALID(10008, "参数无效"),
    ARG_TYPE_MISMATCH(10009, "参数类型错误"),
    ARG_BIND_EXCEPTION(10010, "参数绑定错误"),
    ARG_VIOLATION(10011, "参数不符合要求"),
    ARG_MISSING(10012, "参数未找到"),
    AUTH_FAILED(100201, "鉴权失败"),

    ECHO_TIMEOUT(200703, "API组合接口请求超时"),
    RESULT_EXCEPTION(200704, "API查询结果异常"),
    EXECUTE_FAILED(200705, "API执行结果失败"),
    PARAM_VERIFY_FAILED(200707, "接口参数校验失败"),
    API_FORBIDDEN(200708, "API已经禁用"),
    API_NOT_FOUND(200709, "API未找到"),
    API_NO_AUTHORIZATION(200710, "API未授权"),
    API_METHOD_NOT_FOUND(200711, "API方法未找到"),
    API_NO_DATA_AUTH(200712, "API没有数据权限"),
    API_PARAM_CHECK_FAILED(200713, "参数{0}校验失败,必填"),
    CHECK_PHONE_NOT_EXIST(200714, "该账号不存在"),
    QUERY_RESULT_IS_EMPTY(200715, "查询结果为空"),

    /* 业务逻辑处理错误：300001-399999*/
    PHONE_NOT_EXIST(200716, "该手机号码不存在"),
    PHONE_HAS_EXIST(200717, "该手机号码已存在"),
    EMAIL_NOT_EXIST(200718, "该邮箱不存在"),
    EMAIL_HAS_EXIST(200719, "该邮箱已存在"),
    PROJECT_USER_NOT_EXIST(200720, "该用户没有被分配到该项目"),
    AUTH_CODE_IS_EMPTY(200721, "code不能为空"),
    AUTH_CODE_NOT_EXIST(200722, "code不存在或已失效"),
    ;
    /*
        其他自定义业务异常列表
     */

    private int status;
    private String message;

    ResultCodeEnum(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer status() {
        return this.status;
    }

    public String message() {
        return this.message;
    }

}
