package frame.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import frame.server.utils.ApiResultUtil;
import frame.server.vo.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "权限验证失败控制器")
@RestController
public class UnauthorizedController extends BaseController {

	@ApiOperation(value = "权限验证未通过")
	@RequestMapping(value = "/unauthorized", method = RequestMethod.GET)
	@ResponseBody
	public ApiResult unauthorized() {
		return ApiResultUtil.unauthorized();
	}
	
}
