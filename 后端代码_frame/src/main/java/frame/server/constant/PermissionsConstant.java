package frame.server.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局权限 1. 仓库管理权限 2. 消耗品管理权限 3. 消耗品申请权限
 * 
 * @author Administrator
 *
 */
public class PermissionsConstant {

	// --------仓库管理权限--------

	// 登录权限
	public static final String W_LOGIN_NAME = "登录";
	public static final String W_LOGIN_STR = "w_login";

	// --------消耗品管理权限--------

	// 登录权限
	public static final String C_LOGIN_NAME = "登录";
	public static final String C_LOGIN_STR = "c_login";

	// 角色管理
	public static final String C_ROLE_ADD_NAME = "添加";
	public static final String C_ROLE_ADD_STR = "c_role_add";

	public static final String C_ROLE_DELETE_NAME = "删除";
	public static final String C_ROLE_DELETE_STR = "c_role_delete";

	public static final String C_ROLE_UPDATE_NAME = "修改";
	public static final String C_ROLE_UPDATE_STR = "c_role_update";

	public static final String C_ROLE_SELECT_NAME = "查看";
	public static final String C_ROLE_SELECT_STR = "c_role_select";

	public static final String C_ROLE_IMPORT_NAME = "批量导入";
	public static final String C_ROLE_IMPORT_STR = "c_role_import";

	public static final String C_ROLE_EXPORT_NAME = "批量导出";
	public static final String C_ROLE_EXPORT_STR = "c_role_export";

	// 用户管理权限
	public static final String C_USER_ADD_NAME = "添加";
	public static final String C_USER_ADD_STR = "c_user_add";

	public static final String C_USER_DELETE_NAME = "删除";
	public static final String C_USER_DELETE_STR = "c_user_delete";

	public static final String C_USER_UPDATE_NAME = "修改";
	public static final String C_USER_UPDATE_STR = "c_user_update";

	public static final String C_USER_SELECT_NAME = "查看";
	public static final String C_USER_SELECT_STR = "c_user_select";

	public static final String C_USER_STATE_MANAGEMENT_NAME = "状态管理";
	public static final String C_USER_STATE_MANAGEMENT_STR = "c_user_state_management";

	public static final String C_USER_IMPORT_NAME = "批量导入";
	public static final String C_USER_IMPORT_STR = "c_user_import";

	public static final String C_USER_EXPORT_NAME = "批量导出";
	public static final String C_USER_EXPORT_STR = "c_user_export";

	// 消耗品类型权限
	public static final String C_CONSUMABLESTYPE_ADD_NAME = "添加";
	public static final String C_CONSUMABLESTYPE_ADD_STR = "c_consumablesType_add";

	public static final String C_CONSUMABLESTYPE_DELETE_NAME = "删除";
	public static final String C_CONSUMABLESTYPE_DELETE_STR = "c_consumablesType_delete";

	public static final String C_CONSUMABLESTYPE_UPDATE_NAME = "修改";
	public static final String C_CONSUMABLESTYPE_UPDATE_STR = "c_consumablesType_update";

	public static final String C_CONSUMABLESTYPE_SELECT_NAME = "查看";
	public static final String C_CONSUMABLESTYPE_SELECT_STR = "c_consumablesType_select";

	public static final String C_CONSUMABLESTYPE_STATE_MANAGEMENT_NAME = "状态管理";
	public static final String C_CONSUMABLESTYPE_STATE_MANAGEMENT_STR = "c_consumablesType_state_management";

	public static final String C_CONSUMABLESTYPE_IMPORT_NAME = "批量导入";
	public static final String C_CONSUMABLESTYPE_IMPORT_STR = "c_consumablesType_import";

	public static final String C_CONSUMABLESTYPE_EXPORT_NAME = "批量导出";
	public static final String C_CONSUMABLESTYPE_EXPORT_STR = "c_consumablesType_export";

	// 消耗品基准信息模板
	public static final String C_REFERENCE_INFORMATION_TEMPLATE_ADD_NAME = "添加";
	public static final String C_REFERENCE_INFORMATION_TEMPLATE_ADD_STR = "c_referenceInformationTemplate_add";

	public static final String C_REFERENCE_INFORMATION_TEMPLATE_DELETE_NAME = "删除";
	public static final String C_REFERENCE_INFORMATION_TEMPLATE_DELETE_STR = "c_referenceInformationTemplate_delete";

	public static final String C_REFERENCE_INFORMATION_TEMPLATE_EDIT_NAME = "修改";
	public static final String C_REFERENCE_INFORMATION_TEMPLATE_EDIT_STR = "c_referenceInformationTemplate_update";

	public static final String C_REFERENCE_INFORMATION_TEMPLATE_SELECT_NAME = "查看";
	public static final String C_REFERENCE_INFORMATION_TEMPLATE_SELECT_STR = "c_referenceInformationTemplate_select";

	// 报废管理权限
	public static final String C_SCRAP_ADD_NAME = "添加";
	public static final String C_SCRAP_ADD_STR = "c_scrap_add";

	public static final String C_SCRAP_DELETE_NAME = "删除";
	public static final String C_SCRAP_DELETE_STR = "c_scrap_delete";

	public static final String C_SCRAP_UPDATE_NAME = "修改";
	public static final String C_SCRAP_UPDATE_STR = "c_scrap_update";

	public static final String C_SCRAP_SELECT_NAME = "查看";
	public static final String C_SCRAP_SELECT_STR = "c_scrap_select";

	// 供应商管理权限
	public static final String C_SUPPLIER_ADD_NAME = "添加";
	public static final String C_SUPPLIER_ADD_STR = "c_supplier_add";

	public static final String C_SUPPLIER_DELETE_NAME = "删除";
	public static final String C_SUPPLIER_DELETE_STR = "c_supplier_delete";

	public static final String C_SUPPLIER_UPDATE_NAME = "修改";
	public static final String C_SUPPLIER_UPDATE_STR = "c_supplier_update";

	public static final String C_SUPPLIER_SELECT_NAME = "查看";
	public static final String C_SUPPLIER_SELECT_STR = "c_supplier_select";

	// 消耗品管理权限
	public static final String C_CONSUMABLES_ADD_NAME = "添加";
	public static final String C_CONSUMABLES_ADD_STR = "c_consumables_add";

	public static final String C_CONSUMABLES_DELETE_NAME = "删除";
	public static final String C_CONSUMABLES_DELETE_STR = "c_consumables_delete";

	public static final String C_CONSUMABLES_UPDATE_NAME = "修改";
	public static final String C_CONSUMABLES_UPDATE_STR = "c_consumables_update";

	public static final String C_CONSUMABLES_SELECT_NAME = "查看";
	public static final String C_CONSUMABLES_SELECT_STR = "c_consumables_select";

	public static final String C_CONSUMABLES_STATE_MANAGEMENT_NAME = "状态管理";
	public static final String C_CONSUMABLES_STATE_MANAGEMENT_STR = "c_consumables_state_management";

	public static final String C_CONSUMABLES_INBOUND_NAME = "入库";
	public static final String C_CONSUMABLES_INBOUND_STR = "c_consumables_inbound";

	public static final String C_CONSUMABLES_OUTBOUND_NAME = "出库";
	public static final String C_CONSUMABLES_OUTBOUND_STR = "c_consumables_outbound";

	public static final String C_CONSUMABLES_IMPORT_NAME = "批量导入";
	public static final String C_CONSUMABLES_IMPORT_STR = "c_consumables_import";

	public static final String C_CONSUMABLES_EXPORT_NAME = "批量导出";
	public static final String C_CONSUMABLES_EXPORT_STR = "c_consumables_export";

	// 消耗品申请管理权限
	public static final String C_APPLICATION_SELECT_NAME = "查看";
	public static final String C_APPLICATION_SELECT_STR = "c_application_select";

	public static final String C_APPLICATION_STATE_MANAGEMENT_NAME = "状态管理";
	public static final String C_APPLICATION_STATE_MANAGEMENT_STR = "c_application_state_management";

	// 操作履历管理权限
	public static final String C_USEROPERATE_SELECT_NAME = "查看";
	public static final String C_USEROPERATE_SELECT_STR = "c_userOperate_select";

	public static final String C_USEROPERATE_EXPORT_NAME = "批量导出";
	public static final String C_USEROPERATE_EXPORT_STR = "c_userOperate_export";

	// 统计信息
	public static final String C_STATISTICS_SELECT_NAME = "查看";
	public static final String C_STATISTICS_SELECT_STR = "c_statistics_select";

	public static final String C_STATISTICS_EXPORT_NAME = "批量导出";
	public static final String C_STATISTICS_EXPORT_STR = "c_statistics_export";

	// --------消耗品申请系统--------

	// 登录权限
	public static final String A_LOGIN_NAME = "登录";
	public static final String A_LOGIN_STR = "a_login";

	/**
	 * 获取全部的权限名称
	 * 
	 * 按照一定的次序
	 * 
	 * @return
	 */
	public static final List<String> getPermissionNames() {
		List<String> permissionNames = new ArrayList<String>();

		permissionNames.add(PermissionsConstant.C_LOGIN_NAME);

		permissionNames.add(PermissionsConstant.C_ROLE_ADD_NAME);
		permissionNames.add(PermissionsConstant.C_ROLE_DELETE_NAME);
		permissionNames.add(PermissionsConstant.C_ROLE_UPDATE_NAME);
		permissionNames.add(PermissionsConstant.C_ROLE_SELECT_NAME);
		permissionNames.add(PermissionsConstant.C_ROLE_IMPORT_NAME);
		permissionNames.add(PermissionsConstant.C_ROLE_EXPORT_NAME);

		permissionNames.add(PermissionsConstant.C_USER_ADD_NAME);
		permissionNames.add(PermissionsConstant.C_USER_DELETE_NAME);
		permissionNames.add(PermissionsConstant.C_USER_UPDATE_NAME);
		permissionNames.add(PermissionsConstant.C_USER_SELECT_NAME);
		permissionNames.add(PermissionsConstant.C_USER_STATE_MANAGEMENT_NAME);
		permissionNames.add(PermissionsConstant.C_USER_IMPORT_NAME);
		permissionNames.add(PermissionsConstant.C_USER_EXPORT_NAME);

		permissionNames.add(PermissionsConstant.C_CONSUMABLESTYPE_ADD_NAME);
		permissionNames.add(PermissionsConstant.C_CONSUMABLESTYPE_DELETE_NAME);
		permissionNames.add(PermissionsConstant.C_CONSUMABLESTYPE_UPDATE_NAME);
		permissionNames.add(PermissionsConstant.C_CONSUMABLESTYPE_SELECT_NAME);
		permissionNames.add(PermissionsConstant.C_CONSUMABLESTYPE_STATE_MANAGEMENT_NAME);
		permissionNames.add(PermissionsConstant.C_CONSUMABLESTYPE_IMPORT_NAME);
		permissionNames.add(PermissionsConstant.C_CONSUMABLESTYPE_EXPORT_NAME);

		permissionNames.add(PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_ADD_NAME);
		permissionNames.add(PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_DELETE_NAME);
		permissionNames.add(PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_EDIT_NAME);
		permissionNames.add(PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_SELECT_NAME);

		permissionNames.add(PermissionsConstant.C_CONSUMABLES_ADD_NAME);
		permissionNames.add(PermissionsConstant.C_CONSUMABLES_DELETE_NAME);
		permissionNames.add(PermissionsConstant.C_CONSUMABLES_UPDATE_NAME);
		permissionNames.add(PermissionsConstant.C_CONSUMABLES_SELECT_NAME);
		permissionNames.add(PermissionsConstant.C_CONSUMABLES_STATE_MANAGEMENT_NAME);
		permissionNames.add(PermissionsConstant.C_CONSUMABLES_INBOUND_NAME);
		permissionNames.add(PermissionsConstant.C_CONSUMABLES_OUTBOUND_NAME);
		permissionNames.add(PermissionsConstant.C_CONSUMABLES_IMPORT_NAME);
		permissionNames.add(PermissionsConstant.C_CONSUMABLES_EXPORT_NAME);

		permissionNames.add(PermissionsConstant.C_APPLICATION_SELECT_NAME);
		permissionNames.add(PermissionsConstant.C_APPLICATION_STATE_MANAGEMENT_NAME);

		permissionNames.add(PermissionsConstant.C_STATISTICS_SELECT_NAME);
		permissionNames.add(PermissionsConstant.C_STATISTICS_EXPORT_NAME);

		return permissionNames;
	}

	/**
	 * 获取全部的权限Str
	 * 
	 * 按照一定的次序
	 * 
	 * @return
	 */
	public static final List<String> getPermissionStrs() {
		List<String> permissionStrs = new ArrayList<String>();

		permissionStrs.add(PermissionsConstant.C_LOGIN_STR);

		permissionStrs.add(PermissionsConstant.C_ROLE_ADD_STR);
		permissionStrs.add(PermissionsConstant.C_ROLE_DELETE_STR);
		permissionStrs.add(PermissionsConstant.C_ROLE_UPDATE_STR);
		permissionStrs.add(PermissionsConstant.C_ROLE_SELECT_STR);
		permissionStrs.add(PermissionsConstant.C_ROLE_IMPORT_STR);
		permissionStrs.add(PermissionsConstant.C_ROLE_EXPORT_STR);

		permissionStrs.add(PermissionsConstant.C_USER_ADD_STR);
		permissionStrs.add(PermissionsConstant.C_USER_DELETE_STR);
		permissionStrs.add(PermissionsConstant.C_USER_UPDATE_STR);
		permissionStrs.add(PermissionsConstant.C_USER_SELECT_STR);
		permissionStrs.add(PermissionsConstant.C_USER_STATE_MANAGEMENT_STR);
		permissionStrs.add(PermissionsConstant.C_USER_IMPORT_STR);
		permissionStrs.add(PermissionsConstant.C_USER_EXPORT_STR);

		permissionStrs.add(PermissionsConstant.C_CONSUMABLESTYPE_ADD_STR);
		permissionStrs.add(PermissionsConstant.C_CONSUMABLESTYPE_DELETE_STR);
		permissionStrs.add(PermissionsConstant.C_CONSUMABLESTYPE_UPDATE_STR);
		permissionStrs.add(PermissionsConstant.C_CONSUMABLESTYPE_SELECT_STR);
		permissionStrs.add(PermissionsConstant.C_CONSUMABLESTYPE_STATE_MANAGEMENT_STR);
		permissionStrs.add(PermissionsConstant.C_CONSUMABLESTYPE_IMPORT_STR);
		permissionStrs.add(PermissionsConstant.C_CONSUMABLESTYPE_EXPORT_STR);

		permissionStrs.add(PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_ADD_STR);
		permissionStrs.add(PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_DELETE_STR);
		permissionStrs.add(PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_EDIT_STR);
		permissionStrs.add(PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_SELECT_STR);

		permissionStrs.add(PermissionsConstant.C_CONSUMABLES_ADD_STR);
		permissionStrs.add(PermissionsConstant.C_CONSUMABLES_DELETE_STR);
		permissionStrs.add(PermissionsConstant.C_CONSUMABLES_UPDATE_STR);
		permissionStrs.add(PermissionsConstant.C_CONSUMABLES_SELECT_STR);
		permissionStrs.add(PermissionsConstant.C_CONSUMABLES_STATE_MANAGEMENT_STR);
		permissionStrs.add(PermissionsConstant.C_CONSUMABLES_INBOUND_STR);
		permissionStrs.add(PermissionsConstant.C_CONSUMABLES_OUTBOUND_STR);
		permissionStrs.add(PermissionsConstant.C_CONSUMABLES_IMPORT_STR);
		permissionStrs.add(PermissionsConstant.C_CONSUMABLES_EXPORT_STR);

		permissionStrs.add(PermissionsConstant.C_APPLICATION_SELECT_STR);
		permissionStrs.add(PermissionsConstant.C_APPLICATION_STATE_MANAGEMENT_STR);

		permissionStrs.add(PermissionsConstant.C_USEROPERATE_SELECT_STR);
		permissionStrs.add(PermissionsConstant.C_USEROPERATE_EXPORT_STR);

		permissionStrs.add(PermissionsConstant.C_STATISTICS_SELECT_STR);
		permissionStrs.add(PermissionsConstant.C_STATISTICS_EXPORT_STR);

		return permissionStrs;
	}

	/**
	 * 获取全部的权限名称(名称进行格式化操作)
	 * 
	 * 按照一定的次序
	 * 
	 * @return
	 */
	public static final List<String> getExportPermissionNames() {
		List<String> permissionNames = new ArrayList<String>();

		permissionNames.add(PermissionsConstant.C_LOGIN_NAME + "权限");

		permissionNames.add("角色管理权限：" + PermissionsConstant.C_ROLE_ADD_NAME);
		permissionNames.add("角色管理权限：" + PermissionsConstant.C_ROLE_DELETE_NAME);
		permissionNames.add("角色管理权限：" + PermissionsConstant.C_ROLE_UPDATE_NAME);
		permissionNames.add("角色管理权限：" + PermissionsConstant.C_ROLE_SELECT_NAME);
		permissionNames.add("角色管理权限：" + PermissionsConstant.C_ROLE_IMPORT_NAME);
		permissionNames.add("角色管理权限：" + PermissionsConstant.C_ROLE_EXPORT_NAME);

		permissionNames.add("用户管理权限：" + PermissionsConstant.C_USER_ADD_NAME);
		permissionNames.add("用户管理权限：" + PermissionsConstant.C_USER_DELETE_NAME);
		permissionNames.add("用户管理权限：" + PermissionsConstant.C_USER_UPDATE_NAME);
		permissionNames.add("用户管理权限：" + PermissionsConstant.C_USER_SELECT_NAME);
		permissionNames.add("用户管理权限：" + PermissionsConstant.C_USER_STATE_MANAGEMENT_NAME);
		permissionNames.add("用户管理权限：" + PermissionsConstant.C_USER_IMPORT_NAME);
		permissionNames.add("用户管理权限：" + PermissionsConstant.C_USER_EXPORT_NAME);

		permissionNames.add("类型管理权限：" + PermissionsConstant.C_CONSUMABLESTYPE_ADD_NAME);
		permissionNames.add("类型管理权限：" + PermissionsConstant.C_CONSUMABLESTYPE_DELETE_NAME);
		permissionNames.add("类型管理权限：" + PermissionsConstant.C_CONSUMABLESTYPE_UPDATE_NAME);
		permissionNames.add("类型管理权限：" + PermissionsConstant.C_CONSUMABLESTYPE_SELECT_NAME);
		permissionNames.add("类型管理权限：" + PermissionsConstant.C_CONSUMABLESTYPE_STATE_MANAGEMENT_NAME);
		permissionNames.add("类型管理权限：" + PermissionsConstant.C_CONSUMABLESTYPE_IMPORT_NAME);
		permissionNames.add("类型管理权限：" + PermissionsConstant.C_CONSUMABLESTYPE_EXPORT_NAME);

		permissionNames.add("基准信息模板：" + PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_ADD_NAME);
		permissionNames.add("基准信息模板：" + PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_DELETE_NAME);
		permissionNames.add("基准信息模板：" + PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_EDIT_NAME);
		permissionNames.add("基准信息模板：" + PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_SELECT_NAME);

		permissionNames.add("消耗品管理权限：" + PermissionsConstant.C_CONSUMABLES_ADD_NAME);
		permissionNames.add("消耗品管理权限：" + PermissionsConstant.C_CONSUMABLES_DELETE_NAME);
		permissionNames.add("消耗品管理权限：" + PermissionsConstant.C_CONSUMABLES_UPDATE_NAME);
		permissionNames.add("消耗品管理权限：" + PermissionsConstant.C_CONSUMABLES_SELECT_NAME);
		permissionNames.add("消耗品管理权限：" + PermissionsConstant.C_CONSUMABLES_STATE_MANAGEMENT_NAME);
		permissionNames.add("消耗品管理权限：" + PermissionsConstant.C_CONSUMABLES_INBOUND_NAME);
		permissionNames.add("消耗品管理权限：" + PermissionsConstant.C_CONSUMABLES_OUTBOUND_NAME);
		permissionNames.add("消耗品管理权限：" + PermissionsConstant.C_CONSUMABLES_IMPORT_NAME);
		permissionNames.add("消耗品管理权限：" + PermissionsConstant.C_CONSUMABLES_EXPORT_NAME);

		permissionNames.add("申请管理权限：" + PermissionsConstant.C_APPLICATION_SELECT_NAME);
		permissionNames.add("申请管理权限：" + PermissionsConstant.C_APPLICATION_STATE_MANAGEMENT_NAME);

		permissionNames.add("操作履历管理权限：" + PermissionsConstant.C_USEROPERATE_SELECT_NAME);
		permissionNames.add("操作履历管理权限：" + PermissionsConstant.C_USEROPERATE_EXPORT_NAME);

		permissionNames.add("统计信息管理权限：" + PermissionsConstant.C_STATISTICS_SELECT_NAME);
		permissionNames.add("统计信息管理权限：" + PermissionsConstant.C_STATISTICS_EXPORT_NAME);

		return permissionNames;
	}

	/**
	 * 角色导出时设置权限样本
	 * 
	 * 按照一定的次序
	 * 
	 * @return
	 */
	public static final List<String> getExportPermissionSettingTemplate() {
		List<String> permissionSettingTemplate = new ArrayList<String>();

		permissionSettingTemplate.add("有");

		permissionSettingTemplate.add("无");
		permissionSettingTemplate.add("无");
		permissionSettingTemplate.add("无");
		permissionSettingTemplate.add("无");

		permissionSettingTemplate.add("有");
		permissionSettingTemplate.add("有");
		permissionSettingTemplate.add("有");
		permissionSettingTemplate.add("有");
		permissionSettingTemplate.add("有");
		permissionSettingTemplate.add("有");

		permissionSettingTemplate.add("无");
		permissionSettingTemplate.add("无");
		permissionSettingTemplate.add("无");
		permissionSettingTemplate.add("无");
		permissionSettingTemplate.add("无");
		permissionSettingTemplate.add("无");
		permissionSettingTemplate.add("无");

		permissionSettingTemplate.add("无");
		permissionSettingTemplate.add("无");
		permissionSettingTemplate.add("无");
		permissionSettingTemplate.add("无");
		permissionSettingTemplate.add("无");
		permissionSettingTemplate.add("无");
		permissionSettingTemplate.add("无");
		
		permissionSettingTemplate.add("有");
		permissionSettingTemplate.add("有");
		permissionSettingTemplate.add("有");
		permissionSettingTemplate.add("有");

		permissionSettingTemplate.add("无");
		permissionSettingTemplate.add("无");
		permissionSettingTemplate.add("无");
		permissionSettingTemplate.add("无");

		permissionSettingTemplate.add("有");
		permissionSettingTemplate.add("有");
		permissionSettingTemplate.add("有");
		permissionSettingTemplate.add("有");
		permissionSettingTemplate.add("有");
		permissionSettingTemplate.add("有");
		permissionSettingTemplate.add("有");
		permissionSettingTemplate.add("有");
		permissionSettingTemplate.add("有");
		
		permissionSettingTemplate.add("无");
		permissionSettingTemplate.add("无");
		
		permissionSettingTemplate.add("无");
		permissionSettingTemplate.add("无");
		
		permissionSettingTemplate.add("无");
		permissionSettingTemplate.add("无");

		return permissionSettingTemplate;
	}

}
