package frame.server.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.ShiroException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.alibaba.fastjson.JSONObject;

import frame.server.utils.ApiResultUtil;
import frame.server.vo.ApiResult;

/**
 * 异常Controller
 * 
 * @author Administrator
 *
 */
@RestControllerAdvice
public class ExceptionController extends BaseController {

	/**
	 * 捕捉所有Shiro异常
	 * 
	 * @param e
	 * @return
	 */
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ExceptionHandler(value = ShiroException.class)
	@ResponseBody
	public ApiResult handle401(ShiroException e) {
		return ApiResultUtil.unauthorized();
	}

	/**
	 * 单独捕捉Shiro(UnauthorizedException)异常
	 * 该异常为访问有权限管控的请求而该用户没有所需权限所抛出的异常
	 * 
	 * @return
	 */
	@ExceptionHandler(value = UnauthorizedException.class)
	@ResponseBody
	public ApiResult handle401() {
		return ApiResultUtil.unauthorized();
	}

	/**
	 * Springboot Validation 异常
	 * 具体不详
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	@ResponseBody
	public ApiResult validationBodyException(MethodArgumentNotValidException e) {
		BindingResult result = e.getBindingResult();
		final List<FieldError> errors = result.getFieldErrors();
		Map<String, String> errorMsg = new HashMap<String, String>();
		for (FieldError error : errors) {
			errorMsg.put(error.getField(), error.getDefaultMessage());
		}
		return ApiResultUtil.error(errorMsg);
	}

	/**
	 * Springboot Validation 异常
	 * 具体不详
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = ConstraintViolationException.class)
	@ResponseBody
	public ApiResult constraintViolationExceptionHandler(ConstraintViolationException e) {
		return ApiResultUtil.error(e.getMessage());
	}

	/**
	 * Springboot Validation 异常
	 * 具体不详
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(value = BindException.class)
	@ResponseBody
	public ApiResult BindExceptionHandler(BindException e) {
		BindingResult result = e.getBindingResult();
		final List<FieldError> errors = result.getFieldErrors();
		Map<String, String> errorMsg = new HashMap<String, String>();
		for (FieldError error : errors) {
			errorMsg.put(error.getField(), error.getDefaultMessage());
		}
		return ApiResultUtil.error(errorMsg);
	}

	/**
	 * Springboot Validation 异常
	 * 具体不详
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(HttpMessageConversionException.class)
	public ApiResult parameterTypeException(HttpMessageConversionException e) {
		return ApiResultUtil.error(e.getMessage());
	}

	/**
	 * 捕捉其他所有异常
	 * 
	 * @param request
	 * @param exception
	 * @return
	 */
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ApiResult globalException(HttpServletRequest request, Throwable exception) {
		exception.printStackTrace();

		JSONObject res = new JSONObject();
		res.put("status", this.getStatus(request).value());
		res.put("message", exception.getMessage());

		return ApiResultUtil.internalServerError(res);
	}

	private HttpStatus getStatus(HttpServletRequest request) {
		Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
		if (statusCode == null) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
		
		return HttpStatus.valueOf(statusCode);
	}

}
