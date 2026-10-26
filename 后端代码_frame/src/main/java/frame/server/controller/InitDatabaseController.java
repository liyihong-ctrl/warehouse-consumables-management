package frame.server.controller;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import frame.server.annotation.OperationAnnotation;
import frame.server.constant.RolesConstant;
import frame.server.domain.RolePermission;
import frame.server.domain.UserPermission;
import frame.server.domain.UserRole;
import frame.server.service.InitDatabaseService;
import frame.server.utils.ApiResultUtil;
import frame.server.vo.ApiResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 数据库初始化Controller
 * 
 * @author Administrator
 *
 */
@Api(tags = "数据库初始化控制器")
@RestController
@RequestMapping("/init")
public class InitDatabaseController {
	
	@Autowired
	private InitDatabaseService initDatabaseService;
	
	/**
	 * 初始化数据库
	 * 
	 * @return
	 */
	@ApiOperation(value = "初始化数据库")
//	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@OperationAnnotation(value = "初始化数据库")
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ApiResult initAll() {
		JSONObject res = new JSONObject();
		res.put("permission", initDatabaseService.initPermission());
		res.put("role", initDatabaseService.initRole());
		res.put("rolePermission", initDatabaseService.initRolePermission());
		res.put("user", initDatabaseService.initUser());
		res.put("userRole", initDatabaseService.initUserRole());
		res.put("userPermission", initDatabaseService.initUserPermission());
		res.put("referenceInformationTemplate", initDatabaseService.initReferenceInformationTemplate());
		return ApiResultUtil.success(res);
	}

	/**
	 * 初始化权限表
	 * 
	 * @return
	 */
	@ApiOperation(value = "初始化权限表")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@OperationAnnotation(value = "初始化权限表")
	@RequestMapping(value = "/permission", method = RequestMethod.GET)
	public ApiResult initPermission() {
		return ApiResultUtil.success(initDatabaseService.initPermission());
	}

	/**
	 * 初始化角色表
	 * 
	 * @return
	 */
	@ApiOperation(value = "初始化角色表")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@OperationAnnotation(value = "初始化角色表")
	@RequestMapping(value = "/role", method = RequestMethod.GET)
	public ApiResult initRole() {
		return ApiResultUtil.success(initDatabaseService.initRole());
	}

	/**
	 * 初始化角色权限表
	 * 
	 * @return
	 */
	@ApiOperation(value = "初始化角色权限表")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@OperationAnnotation(value = "初始化角色权限表")
	@RequestMapping(value = "/rolePermission", method = RequestMethod.GET)
	public ApiResult initRolePermission() {
		List<RolePermission> rolePermissions = initDatabaseService.initRolePermission();
		if(rolePermissions.isEmpty()) {
			return ApiResultUtil.error("未初始化「权限表」或「角色表」");
		}
		return ApiResultUtil.success(rolePermissions);
	}

	/**
	 * 初始化人员表
	 * 
	 * @return
	 */
	@ApiOperation(value = "初始化人员表")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@OperationAnnotation(value = "初始化人员表")
	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public ApiResult initUser() {
		return ApiResultUtil.success(initDatabaseService.initUser());
	}

	/**
	 * 初始化人员角色表
	 * 
	 * @return
	 */
	@ApiOperation(value = "初始化人员角色表")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@OperationAnnotation(value = "初始化人员角色表")
	@RequestMapping(value = "/userRole", method = RequestMethod.GET)
	public ApiResult initUserRole() {
		List<UserRole> userRoles = initDatabaseService.initUserRole();
		if(userRoles.isEmpty()) {
			return ApiResultUtil.error("未初始化「角色表」或「人员表」");
		}
		return ApiResultUtil.success(userRoles);
	}
	
	/**
	 * 初始化人员权限表
	 * 
	 * @return
	 */
	@ApiOperation(value = "初始化人员权限表")
	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@OperationAnnotation(value = "初始化人员权限表")
	@RequestMapping(value = "/userPermission", method = RequestMethod.GET)
	public ApiResult initUserPermission() {
		List<UserPermission> userPermission = initDatabaseService.initUserPermission();
		if(userPermission.isEmpty()) {
			return ApiResultUtil.error("未初始化「权限表」或「角色表」或或「角色权限表」「人员表」或「人员角色表」");
		}
		return ApiResultUtil.success(userPermission);
	}
	
	/**
	 * 初始化基准信息模板表
	 * 
	 * @return
	 */
	@ApiOperation(value = "初始化基准信息模板表")
//	@RequiresRoles(RolesConstant.SYSTEM_ADMIN)
	@OperationAnnotation(value = "初始化基准信息模板表")
	@RequestMapping(value = "/referenceInformationTemplate", method = RequestMethod.GET)
	public ApiResult initReferenceInformationTemplate() {
		return ApiResultUtil.success(initDatabaseService.initReferenceInformationTemplate());
	}

}
