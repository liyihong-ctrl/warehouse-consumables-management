package frame.server.constant;

/**
 * HTTP状态码及描述
 * 
 * @author Administrator
 *
 */
public enum ApiResultCodeEnum {

	// 请求成功：登录功能，用户名或密码格式都是对的，但是数据库查不到
	SUCCESS(200, "Success"),

	SUCCESS_NO_CONTENT(204, "SUCCESS_NO_CONTENT"),

	PARTIAL_CONTENT(206, "PARTIAL_CONTENT"),

	MOVED_PERMANENTLY(301, "MOVED_PERMANENTLY"),

	FOUND(302, "FOUND"),

	SEE_OTHER(303, "SEE_OTHER"),

	NOT_MODIFIED(304, "NOT_MODIFIED"),

	TEMPORARY_REDIRECT(307, "TEMPORARY_REDIRECT"),

	// 参数出现错误：登录功能，要求是手机号，但是邮箱
	// 默认前端进行了校验，若后端检测出了问题则直接报400错误
	BAD_REQUEST(400, "BAD_REQUEST"),

	// 未登录
	UNAUTHORIZED(401, "UNAUTHORIZED"),

	FORBIDDEN(403, "FORBIDDEN"),

	NOT_FOUND(404, "NOT_FOUND"),

	// 系统错误
	INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR"),

	SERVICE_UNAVALIABLE(503, "SERVICE_UNAVALIABLE"),
	
	//自定义
	// webscoket连接成功
	CONNECTION_SUCCEEDED(1000, "CONNECTION_SUCCEEDED"),
	
	// webscoket心跳
	HEART_CHECK(1001, "HEART_CHECK"),
	
	// webscoket断开连接
	DISCONNECT(1002, "DISCONNECT"),
	
	// 用户信息修改
	USER_INFORMATION_MODIFICATION(1100, "USER_INFORMATION_MODIFICATION"),
	
	// 用户状态修改
	USER_STATUS_MODIFICATION(1200, "USER_STATUS_MODIFICATION"),
	
	// 用户密码重置
	RESET_PASSWORD(1300, "RESET_PASSWORD"),
	
	// 用户角色修改
	USER_ROLE_MODIFICATION(1400, "USER_ROLE_MODIFICATION"),
	
	// Excel错误
	EXCEL_ERROR(10000, "EXCEL_ERROR")
	
	;

	private int code;

	private String msg;

	ApiResultCodeEnum(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

}
