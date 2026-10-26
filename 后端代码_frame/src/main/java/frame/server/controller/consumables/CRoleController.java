package frame.server.controller.consumables;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;

import frame.server.annotation.OperationAnnotation;
import frame.server.constant.Messages;
import frame.server.constant.PermissionsConstant;
import frame.server.controller.BaseController;
import frame.server.domain.Role;
import frame.server.domain.RoleWarehouse;
import frame.server.dto.RoleDto;
import frame.server.service.consumables.CRoleService;
import frame.server.utils.ApiResultUtil;
import frame.server.utils.ExportExcelUtils;
import frame.server.vo.ApiResult;
import frame.server.vo.ExcelDataModel;
import frame.server.vo.Page;
import frame.server.vo.consumables.CRoleController.CAddRoleModel;
import frame.server.vo.consumables.CRoleController.CEditRoleModel;
import frame.server.vo.consumables.CRoleController.CImportRoleDetailModel;
import frame.server.vo.consumables.CRoleController.CImportRoleModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 消耗品管理系统-角色管理Controller
 * 
 * @author Administrator
 *
 */
@Api(tags = "消耗品管理系统-角色管理控制器")
@RequestMapping("/cRole")
@RestController
public class CRoleController extends BaseController {

	@Autowired
	private CRoleService croleService;

	/**
	 * 获取全部角色信息
	 * 
	 * @param page
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统-获取全部角色信息")
	@OperationAnnotation(value = "消耗品管理系统-获取全部角色信息")
	@RequiresPermissions(PermissionsConstant.C_ROLE_SELECT_STR)
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ApiResult allRoles(String warehouseId) {
		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(warehouseId)) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 执行检索操作
		List<Role> roles = croleService.getAllRoles(warehouseId);

		return ApiResultUtil.success(roles);
	}

	/**
	 * 获取全部角色信息(分页)
	 * 
	 * @param page
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统-获取全部角色信息(分页)")
	@OperationAnnotation(value = "消耗品管理系统-获取全部角色信息(分页)")
	@RequiresPermissions(PermissionsConstant.C_ROLE_SELECT_STR)
	@RequestMapping(value = "/getRoles", method = RequestMethod.POST)
	public ApiResult getRoles(Page page) {
		JSONObject searchCondition = page.getSearchCondition();

		// 校验参数
		if (searchCondition == null || StringUtil.isEmpty(searchCondition.getString("warehouseId"))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(searchCondition.getString("warehouseId"))) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 执行检索操作
		PageInfo<RoleDto> pageInfo = croleService.getRoles(page);

		return ApiResultUtil.success(pageInfo);
	}

	/**
	 * 新增角色
	 * 
	 * @param model
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统-新增角色")
	@OperationAnnotation(value = "消耗品管理系统-新增角色")
	@RequiresPermissions(PermissionsConstant.C_ROLE_ADD_STR)
	@RequestMapping(value = "/addRole", method = RequestMethod.POST)
	public ApiResult addRole(@Validated CAddRoleModel model) {
		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 数据校验
		if (!model.validate()) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验权限Strs
		if (!croleService.validatePermissionIds(model.getPermissionStrs())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验角色名称是否重复
		if (croleService.isRoleNameExist(model.getWarehouseId(), model.getName())) {
			return ApiResultUtil.badRequest("角色名称已存在");
		}

		// 校验角色标识是否重复
		if (croleService.isRoleStrExist(model.getWarehouseId(), model.getStr())) {
			return ApiResultUtil.badRequest("角色标识已存在");
		}

		// 执行新增角色操作
		RoleDto role = croleService.addRole(model);

		return ApiResultUtil.success(role);
	}

	/**
	 * 更新角色
	 * 
	 * Webscoket主动提示用户角色更新
	 * 
	 * @param model
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "消耗品管理系统-更新角色")
	@OperationAnnotation(value = "消耗品管理系统-更新角色")
	@RequiresPermissions(PermissionsConstant.C_ROLE_UPDATE_STR)
	@RequestMapping(value = "/updateRole", method = RequestMethod.POST)
	public ApiResult updateRole(@Validated CEditRoleModel model) throws IOException {
		// 校验登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 数据校验
		if (!model.validate()) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		Role role = croleService.getRole(model.getWarehouseId(), model.getId());

		// 校验角色是否存在
		if (role == null) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 校验权限IDs
		if (!croleService.validatePermissionIds(model.getPermissionStrs())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 若修改了角色名称则校验角色名称是否重复
		if (!model.getName().equals(role.getName())
				&& croleService.isRoleNameExist(model.getWarehouseId(), model.getName())) {
			return ApiResultUtil.badRequest("角色名称已存在");
		}

		// 若修改了角色标识校验角色编号是否重复
		if (!model.getStr().equals(role.getStr())
				&& croleService.isRoleStrExist(model.getWarehouseId(), model.getStr())) {
			return ApiResultUtil.badRequest("角色标识已存在");
		}

		// 执行更新角色操作
		RoleDto roleNew = croleService.updateRole(model);

		return ApiResultUtil.success(roleNew);
	}

	/**
	 * 删除角色
	 * 
	 * @param roleId
	 * @return
	 */
	@ApiOperation(value = "消耗品管理系统-删除角色")
	@OperationAnnotation(value = "消耗品管理系统-删除角色")
	@RequiresPermissions(PermissionsConstant.C_ROLE_DELETE_STR)
	@RequestMapping(value = "/deleteRole", method = RequestMethod.POST)
	public ApiResult deleteRole(RoleWarehouse model) {
		// 检查登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		Role role = croleService.getRole(model.getWarehouseId(), model.getRoleId());

		// 检查角色是否存在
		if (role == null) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 检查该角色是否已分配用户
		if (!croleService.canDeleteRole(model.getRoleId())) {
			return ApiResultUtil.error("无法删除：该角色已经分配了用户");
		}

		// 执行删除操作
		croleService.deleteRole(model.getRoleId());

		return ApiResultUtil.success();
	}

	/**
	 * 下载模板文件
	 * 
	 * @param warehouseId
	 * @throws Exception
	 */
	@ApiOperation(value = "下载模板文件")
	@RequestMapping(value = "/template", method = RequestMethod.GET)
	public void getImportRolesTemplate(String warehouseId) throws Exception {
		ExcelDataModel data = croleService.getImportTemplateData(warehouseId);
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();
		ExportExcelUtils.exportExcel(response, data.getName() + ".xlsx", data);
	}

	/**
	 * 批量导入角色
	 * 
	 * 不保存操作数据
	 * 
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "消耗品管理系统-批量导入角色")
	@RequiresPermissions(PermissionsConstant.C_ROLE_IMPORT_STR)
	@RequestMapping(value = "/importRoles", method = RequestMethod.POST)
	public ApiResult importRoles(@Validated CImportRoleModel model) throws IOException {
		// 检查登录用户是否有权限操作指定仓库
		if (!super.validateUserWarehouse(model.getWarehouseId())) {
			return ApiResultUtil.badRequest(Messages.badRequest);
		}

		// 数据转换
		List<CImportRoleDetailModel> importRoles = JSONObject.parseArray(model.getExcelData(),
				CImportRoleDetailModel.class);

		// 数据校验
		JSONObject errorData = croleService.checkImportRoles(importRoles);
		
		if (errorData != null) {
			return ApiResultUtil.excelError(errorData);
		}

		// 执行导入操作
		List<RoleDto> res =  croleService.importRoles(model.getWarehouseId(), importRoles);

		return ApiResultUtil.success(res);
	}

	/**
	 * 批量导出角色
	 * 
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "消耗品管理系统-批量导出角色")
	@OperationAnnotation(value = "消耗品管理系统-批量导出角色")
	@RequestMapping(value = "/exportRoles", method = RequestMethod.GET)
	public void exportRoles(String warehouseId) throws Exception {
		ExcelDataModel data = croleService.getExportRolesData(warehouseId);
		
		HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getResponse();
		ExportExcelUtils.exportExcel(response, data.getName() + ".xlsx", data);
	}

}
