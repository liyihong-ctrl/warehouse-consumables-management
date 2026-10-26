package frame.server.utils;

import com.alibaba.fastjson.JSONObject;

import frame.server.constant.ApiResultCodeEnum;
import frame.server.vo.ApiResult;

/**
 * HttpApi工具包
 * 
 * @author Administrator
 *
 */
public class ApiResultUtil {

	/**
	 * 成功
	 * 
	 * @return
	 */
	public static ApiResult success() {
		return new ApiResult(ApiResultCodeEnum.SUCCESS.getCode(), true, null, ApiResultCodeEnum.SUCCESS.getMsg());
	}

	/**
	 * 成功(带参数)
	 * 
	 * @param data
	 * @return
	 */
	public static ApiResult success(Object data) {
		return new ApiResult(ApiResultCodeEnum.SUCCESS.getCode(), true, data, ApiResultCodeEnum.SUCCESS.getMsg());
	}

	/**
	 * 参数错误
	 * 
	 * @param data
	 * @return
	 */
	public static ApiResult error(Object data) {
		if (data != null && data instanceof String) {
			JSONObject res = new JSONObject();
			res.put("msg", data.toString());
			return new ApiResult(ApiResultCodeEnum.SUCCESS.getCode(), false, res, ApiResultCodeEnum.SUCCESS.getMsg());
		}

		return new ApiResult(ApiResultCodeEnum.SUCCESS.getCode(), false, data, ApiResultCodeEnum.SUCCESS.getMsg());
	}

	/**
	 * 错误请求
	 * 
	 * @return
	 */
	public static ApiResult badRequest() {
		return new ApiResult(ApiResultCodeEnum.BAD_REQUEST.getCode(), false, null,
				ApiResultCodeEnum.BAD_REQUEST.getMsg());
	}

	/**
	 * 错误请求(带参数)
	 * 
	 * @param data
	 * @return
	 */
	public static ApiResult badRequest(Object data) {
		if (data != null && data instanceof String) {
			JSONObject res = new JSONObject();
			res.put("msg", data.toString());
			return new ApiResult(ApiResultCodeEnum.BAD_REQUEST.getCode(), false, res,
					ApiResultCodeEnum.BAD_REQUEST.getMsg());
		}

		return new ApiResult(ApiResultCodeEnum.BAD_REQUEST.getCode(), false, data,
				ApiResultCodeEnum.BAD_REQUEST.getMsg());
	}

	/**
	 * 404
	 * 
	 * @return
	 */
	public static ApiResult notFound() {
		return new ApiResult(ApiResultCodeEnum.NOT_FOUND.getCode(), false, null, ApiResultCodeEnum.NOT_FOUND.getMsg());
	}

	/**
	 * 未认证
	 * 
	 * @return
	 */
	public static ApiResult unauthorized() {
		return new ApiResult(ApiResultCodeEnum.UNAUTHORIZED.getCode(), false, null,
				ApiResultCodeEnum.UNAUTHORIZED.getMsg());
	}

	/**
	 * 服务器端错误
	 * 
	 * @param data
	 * @return
	 */
	public static ApiResult internalServerError(Object data) {
		return new ApiResult(ApiResultCodeEnum.INTERNAL_SERVER_ERROR.getCode(), false, data,
				ApiResultCodeEnum.INTERNAL_SERVER_ERROR.getMsg());
	}

	/**
	 * webscoket连接成功
	 * 
	 * @param data
	 * @return
	 */
	public static String connectionSucceeded() {
		return JSONObject.toJSONString(new ApiResult(ApiResultCodeEnum.CONNECTION_SUCCEEDED.getCode(), true, null,
				ApiResultCodeEnum.CONNECTION_SUCCEEDED.getMsg()));
	}

	/**
	 * webscoket心跳
	 * 
	 * @param data
	 * @return
	 */
	public static String heartCheck() {
		return JSONObject.toJSONString(new ApiResult(ApiResultCodeEnum.HEART_CHECK.getCode(), true, null,
				ApiResultCodeEnum.HEART_CHECK.getMsg()));
	}

	/**
	 * webscoket断开连接
	 * 
	 * @param data
	 * @return
	 */
	public static String disconnect() {
		return JSONObject.toJSONString(new ApiResult(ApiResultCodeEnum.DISCONNECT.getCode(), true, null,
				ApiResultCodeEnum.DISCONNECT.getMsg()));
	}

	/**
	 * 用户信息修改
	 * 
	 * @param data
	 * @return
	 */
	public static String userInformationModification(Object data) {
		return JSONObject.toJSONString(new ApiResult(ApiResultCodeEnum.USER_INFORMATION_MODIFICATION.getCode(), true,
				data, ApiResultCodeEnum.USER_INFORMATION_MODIFICATION.getMsg()));
	}

	/**
	 * 用户状态修改
	 * 
	 * @param data
	 * @return
	 */
	public static String userStatusModification(Object data) {
		return JSONObject.toJSONString(new ApiResult(ApiResultCodeEnum.USER_STATUS_MODIFICATION.getCode(), true, data,
				ApiResultCodeEnum.USER_STATUS_MODIFICATION.getMsg()));
	}

	/**
	 * 用户密码重置
	 * 
	 * @param data
	 * @return
	 */
	public static String resetPassword(Object data) {
		return JSONObject.toJSONString(new ApiResult(ApiResultCodeEnum.RESET_PASSWORD.getCode(), true, data,
				ApiResultCodeEnum.RESET_PASSWORD.getMsg()));
	}

	/**
	 * 用户角色重置
	 * 
	 * @param data
	 * @return
	 */
	public static String userRoleModification(Object data) {
		return JSONObject.toJSONString(new ApiResult(ApiResultCodeEnum.USER_ROLE_MODIFICATION.getCode(), true, data,
				ApiResultCodeEnum.USER_ROLE_MODIFICATION.getMsg()));
	}

	/**
	 * Excel校验错误
	 * 
	 * @param data
	 * @return
	 */
	public static ApiResult excelError(Object data) {
		return new ApiResult(ApiResultCodeEnum.EXCEL_ERROR.getCode(), false, data,
				ApiResultCodeEnum.EXCEL_ERROR.getMsg());
	}

}
