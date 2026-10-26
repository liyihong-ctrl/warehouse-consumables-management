package frame.server.controller.warehouse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import frame.server.annotation.OperationAnnotation;
import frame.server.constant.Messages;
import frame.server.constant.RolesConstant;
import frame.server.controller.BaseController;
import frame.server.dto.UserDto;
import frame.server.service.warehouse.WUserInfoService;
import frame.server.utils.ApiResultUtil;
import frame.server.vo.ApiResult;
import frame.server.vo.warehouse.WUserInfoController.WEditUserInfoModel;
import frame.server.vo.warehouse.WUserInfoController.WEditUserPasswordModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 仓库管理系统-个人信息管理Controller
 * 
 * @author Administrator
 *
 */
@Api(tags = "仓库管理系统：个人信息管理控制器")
@RequestMapping("/wUserInfo")
@RestController
public class WUserInfoController extends BaseController {
	
	@Autowired
	private WUserInfoService wUserInfoService;

	/**
	 * 更新用户信息
	 * 
	 * @return
	 */
	@ApiOperation(value = "仓库管理系统：更新用户信息")
	@OperationAnnotation(value = "仓库管理系统：更新用户信息")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
	public ApiResult updateUserInfo(@Validated WEditUserInfoModel model) {
		// 数据校验
		if(!model.validate()) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}
		
		// 校验用户ID
		if(!super.getUserId().equals(model.getId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}
		
		// 执行更新操作
		UserDto user = wUserInfoService.updateUserInfo(model);
		
		return ApiResultUtil.success(user);
	}

	/**
	 * 更新用户密码
	 * 
	 * @return
	 */
	@ApiOperation(value = "仓库管理系统：更新用户密码")
	@OperationAnnotation(value = "仓库管理系统：更新用户密码")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@RequestMapping(value = "/updateUserPassword", method = RequestMethod.POST)
	public ApiResult updateUserPassword(@Validated WEditUserPasswordModel model) {
		// 数据校验
		if(!model.validate()) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}
		
		UserDto user = super.getLoginUserInfo();
		
		// 校验用户旧密码
		if(!DigestUtils.md5Hex(model.getOldPass()).equals(user.getPassword())) {
			return ApiResultUtil.error("修改失败：旧密码错误");
		}
		
		// 执行更新操作
		wUserInfoService.updateUserPassword(user.getId(), model.getNewPass());
		
		return ApiResultUtil.success();
	}
	
}
