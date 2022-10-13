package com.chint.iot.protocol.tcp.common;

/**
 * @author zhanglei5
 * @Description: BaseResponse
 * @date 2021-03-24 14:38
 */
public class BaseResponse<T> {
	private String code;
	private String msg;
	private T data;

	public static <T> BaseResponse<T> create(ResultCode resultCode){
		BaseResponse<T> baseResponse = new BaseResponse<T>();
		baseResponse.setResult(resultCode);
		baseResponse.setData(null);
		return baseResponse;
	}

	public static <T> BaseResponse<T> create(ResultCode resultCode, T data) {
		BaseResponse<T> baseResponse = new BaseResponse();
		baseResponse.setResult(resultCode);
		baseResponse.setData(data);
		return baseResponse;
	}

	public static <T> BaseResponse<T> create(String code, String msg, T data) {
		BaseResponse<T> baseResponse = new BaseResponse();
		baseResponse.setCode(code);
		baseResponse.setMsg(msg);
		baseResponse.setData(data);
		return baseResponse;
	}

	public BaseResponse() {
		this.setResult(ResultCode.SUCCESS);
	}

	public BaseResponse(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public BaseResponse(String code, String msg, T data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return this.msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return this.data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public void setResult(ResultCode resultInfo) {
		this.code = resultInfo.code;
		this.msg = resultInfo.msg;
	}
}
