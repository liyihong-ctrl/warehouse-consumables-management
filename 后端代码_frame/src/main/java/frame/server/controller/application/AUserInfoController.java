package frame.server.controller.application;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import frame.server.annotation.OperationAnnotation;
import frame.server.constant.Messages;
import frame.server.constant.PermissionsConstant;
import frame.server.constant.UserTypeEnum;
import frame.server.controller.BaseController;
import frame.server.dto.UserDto;
import frame.server.service.application.AUserInfoService;
import frame.server.utils.ApiResultUtil;
import frame.server.vo.ApiResult;
import frame.server.vo.application.AUserInfoController.AEditUserInfoModel;
import frame.server.vo.application.AUserInfoController.AEditUserPasswordModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 消耗品申请系统：个人信息管理Controller
 * 
 * @author Administrator
 *
 */
@Api(tags = "消耗品申请系统：个人信息管理控制器")
@RequestMapping("/aUserInfo")
@RestController
public class AUserInfoController extends BaseController {

	@Autowired
	private AUserInfoService aUserInfoService;

	/**
	 * 更新用户信息
	 * 
	 * @return
	 */
	@ApiOperation(value = "仓库管理系统：更新用户信息")
	@OperationAnnotation(value = "仓库管理系统：更新用户信息")
	@RequiresPermissions(PermissionsConstant.A_LOGIN_STR)
	@RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
	public ApiResult updateUserInfo(@Validated AEditUserInfoModel model) {
		UserDto user = super.getLoginUserInfo();

		// 校验当前用户类型 (系统管理员 || 其他人员)
		if (!(user.getType().equals(Integer.toString(UserTypeEnum.OTHEREMPLOYEES.getValue()))
				|| user.getType().equals(Integer.toString(UserTypeEnum.SYSTEMADMIN.getValue())))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}
		
		// 数据校验
		if(!model.validate()) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}
		
		// 申请人员需要校验部门
		if(user.getType().equals(Integer.toString(UserTypeEnum.OTHEREMPLOYEES.getValue())) && !model.validateDepartment()) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}
		
		// 校验用户ID
		if(!super.getUserId().equals(model.getId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}
		
		// 执行更新操作
		return ApiResultUtil.success(aUserInfoService.updateUserInfo(model));
	}

	/**
	 * 更新用户密码
	 * 
	 * @return
	 */
	@ApiOperation(value = "仓库管理系统：更新用户信息")
	@OperationAnnotation(value = "仓库管理系统：更新用户信息")
	@RequiresPermissions(PermissionsConstant.A_LOGIN_STR)
	@RequestMapping(value = "/updateUserPassword", method = RequestMethod.POST)
	public ApiResult updateUserPassword(@Validated AEditUserPasswordModel model) {
		UserDto user = super.getLoginUserInfo();

		// 校验当前用户类型 (系统管理员 || 其他人员)
		if (!(user.getType().equals(Integer.toString(UserTypeEnum.OTHEREMPLOYEES.getValue()))
				|| user.getType().equals(Integer.toString(UserTypeEnum.SYSTEMADMIN.getValue())))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}
		
		// 数据校验
		if(!model.validate()) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}
		
		// 校验用户旧密码
		if(!DigestUtils.md5Hex(model.getOldPass()).equals(user.getPassword())) {
			return ApiResultUtil.error("修改失败：旧密码错误");
		}
		
		// 执行更新操作
		aUserInfoService.updateUserPassword(user.getId(), model.getNewPass());
		
		return ApiResultUtil.success();
	}

}
