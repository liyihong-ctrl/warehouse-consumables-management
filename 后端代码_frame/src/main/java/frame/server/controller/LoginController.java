package frame.server.controller;

import java.util.Iterator;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import frame.server.annotation.OperationAnnotation;
import frame.server.constant.DefaultAvatarConstant;
import frame.server.constant.LoginTargetConstant;
import frame.server.constant.Messages;
import frame.server.constant.PermissionsConstant;
import frame.server.constant.RolesConstant;
import frame.server.domain.Permission;
import frame.server.domain.Warehouse;
import frame.server.dto.UserDto;
import frame.server.service.LoginService;
import frame.server.utils.ApiResultUtil;
import frame.server.utils.JwtUtil;
import frame.server.vo.ApiResult;
import frame.server.vo.LoginController.LoginModel;
import frame.server.vo.LoginController.RegisterModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 登录登出Controller
 * 
 * @author Administrator
 *
 */
@Api(tags = "登录登出控制器")
@RestController
public class LoginController extends BaseController {

	@Autowired
	private LoginService loginService;

	/**
	 * 登录
	 * 
	 * @param model
	 * @return
	 */
	@ApiOperation(value = "登录")
	@OperationAnnotation(value = "登录")
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	@ResponseBody
	public ApiResult login(@Validated LoginModel model) {
		// 数据校验
		if (!model.validateTarget() || !model.validateType()) {
			return ApiResultUtil.badRequest(Messages.accountOrPasswordError);
		}

		// 校验账号和密码格式
		if (!model.validateAccount() || !model.validatePassword()) {
			return ApiResultUtil.badRequest(Messages.accountOrPasswordError);
		}

		// 获取用户信息
		UserDto user = loginService.getUserInfo(model.getAccount());

		// 检查用户是否存在和密码是否正确
		if (user == null || !user.getPassword().equals(DigestUtils.md5Hex(model.getPassword()))) {
			return ApiResultUtil.error(Messages.accountOrPasswordError);
		}

		// 校验用户状态
		if (!user.getIsEnable()) {
			return ApiResultUtil.error(Messages.userDisabled);
		}

		// 检查用户权限
		boolean hasPermission = false;
		switch (model.getTarget()) {
		case LoginTargetConstant.CONSUMABLES:
			for (Permission permission : user.getPermissions()) {
				if (permission.getStr().equals(PermissionsConstant.C_LOGIN_STR)) {
					hasPermission = true;
				}
			}
			break;
		case LoginTargetConstant.APPLICATION:
			for (Permission permission : user.getPermissions()) {
				if (permission.getStr().equals(PermissionsConstant.A_LOGIN_STR)) {
					hasPermission = true;
				}
			}
			break;
		case LoginTargetConstant.WAREHOUSE:
			for (Permission permission : user.getPermissions()) {
				if (permission.getStr().equals(PermissionsConstant.W_LOGIN_STR)) {
					hasPermission = true;
				}
			}
			break;
		}
		if (!hasPermission) {
			return ApiResultUtil.badRequest(Messages.permissionDenied);
		}

		// 如果登录消耗品管理系统则需要检查当前用户是否有可操作仓库
		if (model.getTarget().equals(LoginTargetConstant.CONSUMABLES)) {
			boolean hasEnabledWarehouse = false;
			for (Warehouse warehouse : user.getWarehouses()) {
				if (warehouse.getIsEnable()) {
					hasEnabledWarehouse = true;
					break;
				}
			}
			if (!hasEnabledWarehouse) {
				return ApiResultUtil.error("登录失败：暂无可以操作的仓库");
			}
		}

		// 移除未启用的仓库
		Iterator<Warehouse> iterator = user.getWarehouses().iterator();
		while (iterator.hasNext()) {
			Warehouse cur = iterator.next();
			if (!cur.getIsEnable()) {
				iterator.remove();
			}
		}

		String token = JwtUtil.sign(user.getAccount(), user.getPassword());

		// 清除密码
		user.setPassword("");

		JSONObject res = new JSONObject();

		// 登录凭证
		res.put("Authorization", token);

		// 用户信息
		res.put("userInfo", user);

		// 系统权限
		res.put("systemPermissions", loginService.getSystemPermission());

		// 默认logo
		res.put("defaultLogo", DefaultAvatarConstant.getDefaultLogoUrl(model.getTarget()));

		// 跳转目标
		res.put("target", LoginTargetConstant.getTarget(model.getTarget()));

		return ApiResultUtil.success(res);
	}

	/**
	 * 注册
	 * 
	 * @param model
	 * @return
	 */
	@ApiOperation(value = "注册")
	@OperationAnnotation(value = "注册")
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	@ResponseBody
	public ApiResult register(@Validated RegisterModel model) {
		// 数据校验
		if (!model.validate()) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 获取用户信息
		UserDto user = loginService.getUserInfo(model.getAccount());

		// 校验账号是否已经存在
		if (user != null) {
			return ApiResultUtil.error("账号已存在");
		}

		// 执行注册操作
		loginService.register(model);

		return ApiResultUtil.success();
	}

	/**
	 * 登出
	 * 
	 * @param model
	 * @return
	 */
	@ApiOperation(value = "登出")
	@OperationAnnotation(value = "登出")
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	@ResponseBody
	public ApiResult logout() {
		return ApiResultUtil.success();
	}

	/**
	 * 仓库管理系统：切换仓库
	 * 
	 * @return
	 */
	@ApiOperation(value = "仓库管理系统：切换仓库")
	@OperationAnnotation(value = "仓库管理系统：切换仓库")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@RequestMapping(value = "/switchSystem", method = RequestMethod.GET)
	public ApiResult switchSystem() {
		return ApiResultUtil.success(loginService.getWarehouses(super.getUserId(), true));
	}

}
