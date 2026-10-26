package frame.server.service.consumables.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import frame.server.constant.PermissionsConstant;
import frame.server.constant.RolesConstant;
import frame.server.constant.UserTypeEnum;
import frame.server.dao.PermissionDao;
import frame.server.dao.RoleDao;
import frame.server.dao.RolePermissionDao;
import frame.server.dao.RoleWarehouseDao;
import frame.server.dao.UserPermissionDao;
import frame.server.dao.UserRoleDao;
import frame.server.dao.UserWarehouseDao;
import frame.server.dao.WarehouseDao;
import frame.server.domain.Permission;
import frame.server.domain.Role;
import frame.server.domain.RolePermission;
import frame.server.domain.RoleWarehouse;
import frame.server.domain.UserPermission;
import frame.server.domain.UserRole;
import frame.server.domain.UserWarehouse;
import frame.server.domain.Warehouse;
import frame.server.dto.RoleDto;
import frame.server.service.consumables.CRoleService;
import frame.server.utils.RandomUtil;
import frame.server.utils.StringUtil;
import frame.server.vo.ExcelDataModel;
import frame.server.vo.Page;
import frame.server.vo.consumables.CRoleController.CAddRoleModel;
import frame.server.vo.consumables.CRoleController.CEditRoleModel;
import frame.server.vo.consumables.CRoleController.CImportRoleDetailModel;
import frame.server.vo.consumables.CRoleController.GetRolesSelectModel;

@Service("croleService")
public class CRoleServiceImpl implements CRoleService {

	@Autowired
	private PermissionDao permissionDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private RolePermissionDao rolePermissionDao;

	@Autowired
	private RoleWarehouseDao roleWarehouseDao;

	@Autowired
	private UserPermissionDao userPermissionDao;

	@Autowired
	private UserRoleDao userRoleDao;

	@Autowired
	private UserWarehouseDao userWarehouseDao;

	@Autowired
	private WarehouseDao warehouseDao;

	@Override
	public List<Role> getAllRoles(String warehouseId) {
		return roleWarehouseDao.c_selectRolesByWarehouseId(warehouseId);
	}

	@Override
	public PageInfo<RoleDto> getRoles(Page page) {
		// 开启分页
		PageHelper.startPage(page.getPageNum(), page.getPageSize());

		PageInfo<RoleDto> pageInfo = null;

		JSONObject searchCondition = page.getSearchCondition();
		String warehouseId = searchCondition.getString("warehouseId");
		String searchStr = searchCondition.getString("searchStr");

		// 检索条件为空则全量检索
		if (StringUtil.isBlank(searchStr)) {
			pageInfo = new PageInfo<RoleDto>(roleWarehouseDao.c_selectRoleDtosByWarehouseId(warehouseId));
		}
		// 否则根据检索条件检索
		else {
			GetRolesSelectModel getRolesSelectModel = new GetRolesSelectModel(warehouseId, searchStr);
			pageInfo = new PageInfo<RoleDto>(
					roleWarehouseDao.c_selectRoleDtoByWarehouseIdAndLikeRoleName(getRolesSelectModel));
		}

		return pageInfo;
	}

	@Override
	public Role getRole(String warehouseId, String roleId) {
		RoleWarehouse roleWarehouse = new RoleWarehouse(roleId, warehouseId);
		return roleWarehouseDao.c_selectRoleByWarehouseIdAndRoleId(roleWarehouse);
	}

	@Override
	public boolean validateWarehouseId(String userId, String warehouseId) {
		return userWarehouseDao
				.selectEnabledCountByUserIdAndWarehouseId(new UserWarehouse(userId, warehouseId, null)) != 0;
	}

	@Override
	public boolean validatePermissionIds(List<String> permissionStrs) {
		// 获取全部权限
		List<Permission> permissions = permissionDao.selectAll();

		// 检验权限存在且仅属于仓库管理系统中
		for (String permissionStr : permissionStrs) {
			boolean isExist = false;
			for (Permission permission : permissions) {
				if (permissionStr.equals(permission.getStr())
						&& !permission.getStr().equals(PermissionsConstant.W_LOGIN_STR)
						&& !permission.getStr().equals(PermissionsConstant.A_LOGIN_STR)) {
					isExist = true;
					break;
				}
			}
			if (!isExist) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean isRoleNameExist(String warehouseId, String roleName) {
		// 判断角色名称是否为系统内置
		if (roleName.equals(RolesConstant.ROOT_ADMIN) || roleName.equals(RolesConstant.SYSTEM_ADMIN)
				|| roleName.equals(RolesConstant.WAREHOUSE_ADMIN) || roleName.equals(RolesConstant.GENERAL_EMPLOYEE)
				|| roleName.equals(RolesConstant.OTHER_EMPLOYEES)
				|| roleName.equals(RolesConstant.TEMPORARY_EMPLOYEE)) {
			return true;
		}

		// 判断角色名称是否在当前仓库内重复
		List<String> roleNames = roleWarehouseDao.c_selectRoleNamesByWarehouseId(warehouseId);
		for (String name : roleNames) {
			if (roleName.equals(name)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isRoleStrExist(String warehouseId, String roleStr) {
		// 判断角色名称是否为系统内置
		if (roleStr.equals(RolesConstant.ROOT_ADMIN_STR) || roleStr.equals(RolesConstant.SYSTEM_ADMIN_STR)
				|| roleStr.equals(RolesConstant.WAREHOUSE_ADMIN_STR)
				|| roleStr.equals(RolesConstant.GENERAL_EMPLOYEE_STR)
				|| roleStr.equals(RolesConstant.OTHER_EMPLOYEES_STR)
				|| roleStr.equals(RolesConstant.TEMPORARY_EMPLOYEE_STR)) {
			return true;
		}

		// 判断角色标识是否在当前仓库内重复
		List<String> roleStrs = roleWarehouseDao.c_selectRoleStrsByWarehouseId(warehouseId);
		for (String str : roleStrs) {
			if (roleStr.equals(str)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public RoleDto addRole(CAddRoleModel model) {
		// 保存角色
		Role role = new Role(RandomUtil.genarateId(), model.getName(), model.getStr(),
				UserTypeEnum.GENERALEMPLOYEE.getValue(), model.getRemarks(), new Date(), true, false);
		roleDao.insertRole(role);

		// 保存角色权限对应关系
		// 获取全部权限
		List<Permission> permissions = permissionDao.selectAll();

		// 检验权限存在且仅属于仓库管理系统中
		List<RolePermission> rolePermissions = new ArrayList<RolePermission>();
		for (String permissionStr : model.getPermissionStrs()) {
			for (Permission permission : permissions) {
				if (permissionStr.equals(permission.getStr())
						&& !permission.getStr().equals(PermissionsConstant.W_LOGIN_STR)
						&& !permission.getStr().equals(PermissionsConstant.A_LOGIN_STR)) {
					rolePermissions.add(new RolePermission(role.getId(), permission.getId()));
				}
			}
		}
		rolePermissionDao.insertRolePermissions(rolePermissions);

		// 保存角色仓库对应关系
		RoleWarehouse roleWarehouse = new RoleWarehouse(role.getId(), model.getWarehouseId());
		roleWarehouseDao.insertRoleWarehouse(roleWarehouse);

		return roleDao.selectRoleDtoByRoleId(role.getId());
	}

	@Override
	public boolean isRoleExist(String roleId, String warehouseId) {
		RoleWarehouse roleWarehouse = new RoleWarehouse(roleId, warehouseId);
		return roleWarehouseDao.selectCountByWarehouseIdAndRoleId(roleWarehouse) != 0;
	}

	@Override
	public List<String> getUserIdsByRoleId(String roleId) {
		return userRoleDao.c_selectUserIdsByRoleId(roleId);
	}

	@Override
	public RoleDto updateRole(CEditRoleModel model) {
		// 更新角色
		Role role = new Role(model.getId(), model.getName(), model.getStr(), UserTypeEnum.GENERALEMPLOYEE.getValue(),
				model.getRemarks(), new Date(), true, false);
		roleDao.update(role);

		// 更新角色权限
		// 全删全插
		rolePermissionDao.deleteByRoleId(role.getId());

		// 获取全部权限
		List<Permission> permissions = permissionDao.selectAll();

		// 检验权限存在且仅属于仓库管理系统中
		List<RolePermission> rolePermissions = new ArrayList<RolePermission>();
		for (String permissionStr : model.getPermissionStrs()) {
			for (Permission permission : permissions) {
				if (permissionStr.equals(permission.getStr())
						&& !permission.getStr().equals(PermissionsConstant.W_LOGIN_STR)
						&& !permission.getStr().equals(PermissionsConstant.A_LOGIN_STR)) {
					rolePermissions.add(new RolePermission(role.getId(), permission.getId()));
				}
			}
		}
		rolePermissionDao.insertRolePermissions(rolePermissions);

		// 角色更新后需要对应更新已分配的用户权限
		List<UserRole> userRoles = userRoleDao.selectAll();
		List<UserPermission> userPermissions = new ArrayList<UserPermission>();
		List<String> userIds = userRoleDao.c_selectUserIdsByRoleId(role.getId());
		if (userIds != null && userIds.size() > 0) {
			for (String userId : userIds) {
				userPermissionDao.deleteByUserId(userId);
				for (UserRole userRole : userRoles) {
					if (userRole.getUserId().equals(userId)) {
						for (RolePermission rolePermission : rolePermissions) {
							if (rolePermission.getRoleId().equals(userRole.getRoleId())) {
								UserPermission userPermission = new UserPermission(userId,
										rolePermission.getPermissionId());
								userPermissions.add(userPermission);
							}
						}
					}
				}
			}

			userPermissionDao.insertUserPermissions(userPermissions);
		}

		return roleDao.selectRoleDtoByRoleId(role.getId());
	}

	@Override
	public boolean canDeleteRole(String roleId) {
		// 检测角色下是否还有用户
		return userRoleDao.c_selectUserCountByRoleId(roleId) == 0;
	}

	@Override
	public void deleteRole(String roleId) {
		// 删除仓库角色关联关系
		roleWarehouseDao.deleteByRoleId(roleId);

		// 删除用户角色关联关系
		userRoleDao.deleteByRoleId(roleId);

		// 删除角色
		roleDao.updateRoleDeleteState(roleId);
	}

	@Override
	public ExcelDataModel getImportTemplateData(String warehouseId) {
		// 获取仓库数据
		Warehouse warehouse = warehouseDao.selectWarehouseById(warehouseId);

		ExcelDataModel excelData = new ExcelDataModel();

		// 设置文件名
		Date now = new Date();
		String fileName = warehouse.getName() + "_批量导入角色_样本_" + now.getTime();
		excelData.setName(fileName);

		// 设置表头
		List<String> heads = new ArrayList<String>();

		heads.add("角色名称");
		heads.add("角色标识");
		heads.add("备注");
		heads.addAll(PermissionsConstant.getExportPermissionNames());

		excelData.setTitles(heads);

		// 设置数据
		List<List<Object>> templateDatas = new ArrayList<>();

		List<Object> templateData = new ArrayList<Object>();

		templateData.add("消耗品管理员");
		templateData.add("xhpgly");
		templateData.add("具备登录权限、角色管理权限、基准信息模板管理权限、消耗品管理权限");
		templateData.addAll(PermissionsConstant.getExportPermissionSettingTemplate());

		templateDatas.add(templateData);

		excelData.setRows(templateDatas);

		return excelData;
	}

	@Override
	public JSONObject checkImportRoles(List<CImportRoleDetailModel> importRoles) {
		JSONObject errorDatas = new JSONObject();

		// 检查文件内角色名称和角色标识是否重复
		for (int i = 0; i < importRoles.size(); i++) {
			JSONObject errorData = new JSONObject();
			for (int j = 0; j < importRoles.size(); j++) {
				if (i != j) {
					if (importRoles.get(i).getName().equals(importRoles.get(j).getName())) {
						errorData.put("name", "角色名称重复");
					}
					if (importRoles.get(i).getStr().equals(importRoles.get(j).getStr())) {
						errorData.put("str", "角色标识重复");
					}
					if (!errorData.isEmpty()) {
						break;
					}
				}
			}
			if (!errorData.isEmpty()) {
				errorDatas.put(Integer.toString(i), errorData);
			}
		}
		if (!errorDatas.isEmpty()) {
			return errorDatas;
		}

		// 检查各个字段是否为空、长度是否合格、字段值是否正确
		for (int i = 0; i < importRoles.size(); i++) {
			JSONObject errorData = new JSONObject();
			// 检查角色名
			if (StringUtil.isBlank(importRoles.get(i).getName())) {
				errorData.put("name", "角色名称不能为空");
			} else {
				String name = importRoles.get(i).getName().trim();
				if (name.length() < 0 || 20 < name.length()) {
					errorData.put("name", "角色名称长度范围为1~20");
				}
			}

			// 检查角色标识
			if (StringUtil.isBlank(importRoles.get(i).getStr())) {
				errorData.put("str", "角色标识不能为空");
			} else {
				String str = importRoles.get(i).getStr().trim();
				if (str.length() < 0 || 20 < str.length()) {
					errorData.put("str", "角色标识长度范围为1~20");
				}
			}

			// 检查备注
			if (!StringUtil.isBlank(importRoles.get(i).getRemarks())) {
				String remarks = importRoles.get(i).getRemarks().trim();
				if (!StringUtil.isBlank(remarks) && remarks.length() > 50) {
					errorData.put("remarks", "角色备注长度范围为0~50");
				}
			}

			// 检查登录权限
			String errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_login());
			if (errorMsg != null) {
				errorData.put("c_login", errorMsg);
				errorMsg = null;
			}

			// 检查角色管理权限
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_role_add());
			if (errorMsg != null) {
				errorData.put("c_role_add", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_role_delete());
			if (errorMsg != null) {
				errorData.put("c_role_delete", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_role_update());
			if (errorMsg != null) {
				errorData.put("c_role_update", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_role_select());
			if (errorMsg != null) {
				errorData.put("c_role_select", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_role_import());
			if (errorMsg != null) {
				errorData.put("c_role_import", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_role_export());
			if (errorMsg != null) {
				errorData.put("c_role_export", errorMsg);
				errorMsg = null;
			}

			// 检查用户管理权限
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_user_add());
			if (errorMsg != null) {
				errorData.put("c_user_add", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_user_delete());
			if (errorMsg != null) {
				errorData.put("c_user_delete", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_user_update());
			if (errorMsg != null) {
				errorData.put("c_user_update", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_user_select());
			if (errorMsg != null) {
				errorData.put("c_user_select", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_user_state_management());
			if (errorMsg != null) {
				errorData.put("c_user_state_management", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_user_import());
			if (errorMsg != null) {
				errorData.put("c_user_import", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_user_export());
			if (errorMsg != null) {
				errorData.put("c_user_export", errorMsg);
				errorMsg = null;
			}

			// 检查消耗品类型管理权限
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_consumablesType_add());
			if (errorMsg != null) {
				errorData.put("c_consumablesType_add", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_consumablesType_delete());
			if (errorMsg != null) {
				errorData.put("c_consumablesType_delete", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_consumablesType_update());
			if (errorMsg != null) {
				errorData.put("c_consumablesType_update", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_consumablesType_select());
			if (errorMsg != null) {
				errorData.put("c_consumablesType_select", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel
					.validatePermission(importRoles.get(i).getC_consumablesType_state_management());
			if (errorMsg != null) {
				errorData.put("c_consumablesType_state_management", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_consumablesType_import());
			if (errorMsg != null) {
				errorData.put("c_consumablesType_import", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_consumablesType_export());
			if (errorMsg != null) {
				errorData.put("c_consumablesType_export", errorMsg);
				errorMsg = null;
			}

			// 检查基准信息管理权限
			errorMsg = CImportRoleDetailModel
					.validatePermission(importRoles.get(i).getC_referenceInformationTemplate_add());
			if (errorMsg != null) {
				errorData.put("c_referenceInformationTemplate_add", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel
					.validatePermission(importRoles.get(i).getC_referenceInformationTemplate_delete());
			if (errorMsg != null) {
				errorData.put("c_referenceInformationTemplate_delete", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel
					.validatePermission(importRoles.get(i).getC_referenceInformationTemplate_update());
			if (errorMsg != null) {
				errorData.put("c_referenceInformationTemplate_update", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel
					.validatePermission(importRoles.get(i).getC_referenceInformationTemplate_select());
			if (errorMsg != null) {
				errorData.put("c_referenceInformationTemplate_select", errorMsg);
				errorMsg = null;
			}

			// 检查消耗品管理权限
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_consumables_add());
			if (errorMsg != null) {
				errorData.put("c_consumables_add", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_consumables_delete());
			if (errorMsg != null) {
				errorData.put("c_consumables_delete", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_consumables_update());
			if (errorMsg != null) {
				errorData.put("c_consumables_update", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_consumables_select());
			if (errorMsg != null) {
				errorData.put("c_consumables_select", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel
					.validatePermission(importRoles.get(i).getC_consumables_state_management());
			if (errorMsg != null) {
				errorData.put("c_consumables_state_management", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_consumables_inbound());
			if (errorMsg != null) {
				errorData.put("c_consumables_inbound", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_consumables_outbound());
			if (errorMsg != null) {
				errorData.put("c_consumables_outbound", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_consumables_import());
			if (errorMsg != null) {
				errorData.put("c_consumables_import", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_consumables_export());
			if (errorMsg != null) {
				errorData.put("c_consumables_export", errorMsg);
				errorMsg = null;
			}

			// 检查申请管理权限
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_application_select());
			if (errorMsg != null) {
				errorData.put("c_application_select", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel
					.validatePermission(importRoles.get(i).getC_application_state_management());
			if (errorMsg != null) {
				errorData.put("c_application_state_management", errorMsg);
				errorMsg = null;
			}

			// 检查操作履历管理权限
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_userOperate_select());
			if (errorMsg != null) {
				errorData.put("c_userOperate_select", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_userOperate_export());
			if (errorMsg != null) {
				errorData.put("c_userOperate_export", errorMsg);
				errorMsg = null;
			}

			// 检查统计信息管理权限
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_statistics_select());
			if (errorMsg != null) {
				errorData.put("c_statistics_select", errorMsg);
				errorMsg = null;
			}
			errorMsg = CImportRoleDetailModel.validatePermission(importRoles.get(i).getC_statistics_export());
			if (errorMsg != null) {
				errorData.put("c_statistics_export", errorMsg);
				errorMsg = null;
			}

			if (errorData.size() > 0) {
				errorDatas.put(Integer.toString(i), errorData);
			}
		}

		if (errorDatas.size() > 0) {
			return errorDatas;
		}

		// 检查权限关联关系
		for (int i = 0; i < importRoles.size(); i++) {
			JSONObject errorData = new JSONObject();
			// 检查登录权限
			if (importRoles.get(i).getC_login().trim().equals("无")) {
				errorData.put("c_login", "登录权限必选");
			}

			// 检查角色管理权限
			if (importRoles.get(i).getC_role_select().trim().equals("无")) {
				if (importRoles.get(i).getC_role_add().trim().equals("有")
						|| importRoles.get(i).getC_role_delete().trim().equals("有")
						|| importRoles.get(i).getC_role_update().trim().equals("有")
						|| importRoles.get(i).getC_role_import().trim().equals("有")
						|| importRoles.get(i).getC_role_export().trim().equals("有")) {
					errorData.put("c_role_select", "角色管理权限：查看 需要设置为有");
				}
			}

			// 检查用户管理权限
			if (importRoles.get(i).getC_user_select().trim().equals("无")) {
				if (importRoles.get(i).getC_user_add().trim().equals("有")
						|| importRoles.get(i).getC_user_delete().trim().equals("有")
						|| importRoles.get(i).getC_user_update().trim().equals("有")
						|| importRoles.get(i).getC_user_state_management().trim().equals("有")
						|| importRoles.get(i).getC_user_import().trim().equals("有")
						|| importRoles.get(i).getC_user_export().trim().equals("有")) {
					errorData.put("c_user_select", "用户管理权限：查看 需要设置为有");
				}
			}

			// 检查消耗品类型管理权限
			if (importRoles.get(i).getC_consumablesType_select().trim().equals("无")) {
				if (importRoles.get(i).getC_consumablesType_add().trim().equals("有")
						|| importRoles.get(i).getC_consumablesType_delete().trim().equals("有")
						|| importRoles.get(i).getC_consumablesType_update().trim().equals("有")
						|| importRoles.get(i).getC_consumablesType_state_management().trim().equals("有")
						|| importRoles.get(i).getC_consumablesType_import().trim().equals("有")
						|| importRoles.get(i).getC_consumablesType_export().trim().equals("有")) {
					errorData.put("c_consumablesType_select", "类型管理权限：查看 需要设置为有");
				}
			}

			// 检查基准信息管理权限
			if (importRoles.get(i).getC_referenceInformationTemplate_select().trim().equals("无")) {
				if (importRoles.get(i).getC_referenceInformationTemplate_add().trim().equals("有")
						|| importRoles.get(i).getC_referenceInformationTemplate_delete().trim().equals("有")
						|| importRoles.get(i).getC_referenceInformationTemplate_update().trim().equals("有")) {
					errorData.put("c_referenceInformationTemplate_select", "基准信息模板：查看 需要设置为有");
				}
			}

			// 检查消耗品管理权限
			if (importRoles.get(i).getC_consumables_select().trim().equals("无")) {
				if (importRoles.get(i).getC_consumables_add().trim().equals("有")
						|| importRoles.get(i).getC_consumables_delete().trim().equals("有")
						|| importRoles.get(i).getC_consumables_update().trim().equals("有")
						|| importRoles.get(i).getC_consumables_state_management().trim().equals("有")
						|| importRoles.get(i).getC_consumables_inbound().trim().equals("有")
						|| importRoles.get(i).getC_consumables_outbound().trim().equals("有")
						|| importRoles.get(i).getC_consumables_import().trim().equals("有")
						|| importRoles.get(i).getC_consumables_export().trim().equals("有")) {
					errorData.put("c_consumables_select", "消耗品管理权限：查看 需要设置为有");
				}
			}

			// 检查申请管理权限
			if (importRoles.get(i).getC_application_select().trim().equals("无")) {
				if (importRoles.get(i).getC_application_state_management().trim().equals("有")) {
					errorData.put("c_application_select", "申请管理权限：查看 需要设置为有");
				}
			}

			// 检查操作履历管理权限
			if (importRoles.get(i).getC_userOperate_select().trim().equals("无")) {
				if (importRoles.get(i).getC_userOperate_export().trim().equals("有")) {
					errorData.put("c_userOperate_select", "操作履历管理权限：查看 需要设置为有");
				}
			}

			// 检查统计信息管理权限
			if (importRoles.get(i).getC_statistics_select().trim().equals("无")) {
				if (importRoles.get(i).getC_statistics_export().trim().equals("有")) {
					errorData.put("c_statistics_select", "统计信息管理权限：查看 需要设置为有");
				}
			}

			if (errorData.size() > 0) {
				errorDatas.put(Integer.toString(i), errorData);
			}
		}

		if (errorDatas.size() > 0) {
			return errorDatas;
		}

		return null;
	}

	@Override
	public List<RoleDto> importRoles(String warehouseId, List<CImportRoleDetailModel> importRoles) {
		// 获取仓库内所有角色数据
		List<RoleDto> dbRoles = roleWarehouseDao.c_selectRoleDtosByWarehouseId(warehouseId);
		
		// 需要添加的角色
		List<CAddRoleModel> addRoles = new ArrayList<CAddRoleModel>();
		// 需要更新的角色
		List<CEditRoleModel> updateRoles = new ArrayList<CEditRoleModel>();
		
		// 结果集
		List<RoleDto> res = new ArrayList<RoleDto>();
		
		// 循环导入数据
		for(CImportRoleDetailModel importRole : importRoles) {
			boolean isAdd = true;
			String dbRoleId = "";
			for(RoleDto dbRole: dbRoles) {
				if(importRole.getName().equals(dbRole.getName())) {
					dbRoleId = dbRole.getId();
					isAdd = false;
					break;
				}
			}
			if(isAdd) {
				CAddRoleModel addRole = new CAddRoleModel();
				// 设置基本信息
				addRole.setWarehouseId(warehouseId);
				addRole.setName(importRole.getName().trim());
				addRole.setStr(importRole.getStr().trim());

				if(!StringUtil.isBlank(importRole.getRemarks())) {
					addRole.setRemarks(importRole.getRemarks().trim());
				} else {
					addRole.setRemarks("");
				}
				
				addRole.setPermissionStrs(getImportRolePermissionStrs(importRole));
				
				// 设置权限信息
				addRoles.add(addRole);
			} else {
				CEditRoleModel updateRole = new CEditRoleModel();
				// 设置基本信息
				updateRole.setId(dbRoleId);
				updateRole.setWarehouseId(warehouseId);
				updateRole.setName(importRole.getName().trim());
				updateRole.setStr(importRole.getStr().trim());

				if(!StringUtil.isBlank(importRole.getRemarks())) {
					updateRole.setRemarks(importRole.getRemarks().trim());
				} else {
					updateRole.setRemarks("");
				}
				
				updateRole.setPermissionStrs(getImportRolePermissionStrs(importRole));
				
				// 设置权限信息
				updateRoles.add(updateRole);
			}
		}
		
		if(!addRoles.isEmpty()) {
			for(CAddRoleModel model : addRoles) {
				res.add(addRole(model));
			}
		}
		
		if(!updateRoles.isEmpty()) {
			for(CEditRoleModel model : updateRoles) {
				updateRole(model);
			}
		}
		
		return res;
	}
	
	private List<String> getImportRolePermissionStrs(CImportRoleDetailModel importRole) {
		List<String> permissionStrs= new ArrayList<String>();
		
		// 登录
		if(importRole.getC_login().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_LOGIN_STR);
		}
		
		// 角色管理权限
		if(importRole.getC_role_add().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_ROLE_ADD_STR);
		}
		if(importRole.getC_role_delete().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_ROLE_DELETE_STR);
		}
		if(importRole.getC_role_update().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_ROLE_UPDATE_STR);
		}
		if(importRole.getC_role_select().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_ROLE_SELECT_STR);
		}
		if(importRole.getC_role_import().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_ROLE_IMPORT_STR);
		}
		if(importRole.getC_role_export().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_ROLE_EXPORT_STR);
		}
		
		// 用户管理权限
		if(importRole.getC_user_add().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_USER_ADD_STR);
		}
		if(importRole.getC_user_delete().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_USER_DELETE_STR);
		}
		if(importRole.getC_user_update().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_USER_UPDATE_STR);
		}
		if(importRole.getC_user_select().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_USER_SELECT_STR);
		}
		if(importRole.getC_user_state_management().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_USER_STATE_MANAGEMENT_STR);
		}
		if(importRole.getC_user_import().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_USER_IMPORT_STR);
		}
		if(importRole.getC_user_export().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_USER_EXPORT_STR);
		}
		
		// 消耗品类型管理系统
		if(importRole.getC_consumablesType_add().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_CONSUMABLESTYPE_ADD_STR);
		}
		if(importRole.getC_consumablesType_delete().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_CONSUMABLESTYPE_DELETE_STR);
		}
		if(importRole.getC_consumablesType_update().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_CONSUMABLESTYPE_UPDATE_STR);
		}
		if(importRole.getC_consumablesType_select().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_CONSUMABLESTYPE_SELECT_STR);
		}
		if(importRole.getC_consumablesType_state_management().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_CONSUMABLESTYPE_STATE_MANAGEMENT_STR);
		}
		if(importRole.getC_consumablesType_import().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_CONSUMABLESTYPE_IMPORT_STR);
		}
		if(importRole.getC_consumablesType_export().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_CONSUMABLESTYPE_EXPORT_STR);
		}
		
		// 基准信息模板管理系统
		if(importRole.getC_referenceInformationTemplate_add().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_ADD_STR);
		}
		if(importRole.getC_referenceInformationTemplate_delete().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_DELETE_STR);
		}
		if(importRole.getC_referenceInformationTemplate_update().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_EDIT_STR);
		}
		if(importRole.getC_referenceInformationTemplate_select().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_SELECT_STR);
		}
		
		// 消耗品管理系统
		if(importRole.getC_consumables_add().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_CONSUMABLES_ADD_STR);
		}
		if(importRole.getC_consumables_delete().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_CONSUMABLES_DELETE_STR);
		}
		if(importRole.getC_consumables_update().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_CONSUMABLES_UPDATE_STR);
		}
		if(importRole.getC_consumables_select().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_CONSUMABLES_SELECT_STR);
		}
		if(importRole.getC_consumables_state_management().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_CONSUMABLES_STATE_MANAGEMENT_STR);
		}
		if(importRole.getC_consumables_inbound().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_CONSUMABLES_INBOUND_STR);
		}
		if(importRole.getC_consumables_outbound().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_CONSUMABLES_OUTBOUND_STR);
		}
		if(importRole.getC_consumables_import().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_CONSUMABLES_IMPORT_STR);
		}
		if(importRole.getC_consumables_export().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_CONSUMABLES_EXPORT_STR);
		}
		
		// 申请管理权限
		if(importRole.getC_application_select().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_APPLICATION_SELECT_STR);
		}
		if(importRole.getC_application_state_management().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_APPLICATION_STATE_MANAGEMENT_STR);
		}
		
		// 操作履历管理权限
		if(importRole.getC_userOperate_select().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_USEROPERATE_SELECT_STR);
		}
		if(importRole.getC_userOperate_export().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_USEROPERATE_EXPORT_STR);
		}
		
		// 统计信息管理权限
		if(importRole.getC_statistics_select().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_STATISTICS_SELECT_STR);
		}
		if(importRole.getC_statistics_export().trim().equals("有")) {
			permissionStrs.add(PermissionsConstant.C_STATISTICS_EXPORT_STR);
		}
		
		return permissionStrs;
		
	}

	@Override
	public ExcelDataModel getExportRolesData(String warehouseId) {
		// 获取仓库数据
		Warehouse warehouse = warehouseDao.selectWarehouseById(warehouseId);

		ExcelDataModel excelData = new ExcelDataModel();

		// 设置文件名
		Date now = new Date();
		String fileName = warehouse.getName() + "_角色_" + now.getTime();
		excelData.setName(fileName);

		// 设置表头
		List<String> heads = new ArrayList<String>();

		heads.add("角色名称");
		heads.add("角色标识");
		heads.add("备注");
		heads.addAll(PermissionsConstant.getExportPermissionNames());

		excelData.setTitles(heads);

		// 设置数据
		List<List<Object>> templateDatas = new ArrayList<>();

		// 获取全部角色
		List<RoleDto> roles = roleWarehouseDao.c_selectRoleDtosByWarehouseId(warehouseId);
		for (RoleDto role : roles) {
			List<Object> templateData = new ArrayList<Object>();
			templateData.add(role.getName());
			templateData.add(role.getStr());
			templateData.add(role.getRemarks());
			// 根据表头顺序设置权限
			for (String permissionStr : PermissionsConstant.getPermissionStrs()) {
				String hasPermission = "无";
				for (Permission permission : role.getPermissions()) {
					if (permissionStr.equals(permission.getStr())) {
						hasPermission = "有";
						break;
					}
				}
				templateData.add(hasPermission);
			}

			templateDatas.add(templateData);
		}

		excelData.setRows(templateDatas);

		return excelData;
	}

}
