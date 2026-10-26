package frame.server.constant;

import frame.server.dto.SingleRoleDto;

/**
 * 全局角色
 * 
 * @author Administrator
 *
 */
public class RolesConstant {
	
	public static final String ROOT_ADMIN = "根管理员";
	public static final String ROOT_ADMIN_STR = "root_admin";
	
	public static final String SYSTEM_ADMIN = "系统管理员";
	public static final String SYSTEM_ADMIN_STR = "system_admin";
	
	public static final String WAREHOUSE_ADMIN = "仓库管理员";
	public static final String WAREHOUSE_ADMIN_STR = "warehouse_admin";
	
	public static final String GENERAL_EMPLOYEE = "普通员工";
	public static final String GENERAL_EMPLOYEE_STR = "general_employee";
	
	public static final String OTHER_EMPLOYEES = "申请人员";
	public static final String OTHER_EMPLOYEES_STR = "other_employee";
	
	public static final String TEMPORARY_EMPLOYEE = "临时员工";
	public static final String TEMPORARY_EMPLOYEE_STR = "temporary_employee";
	
	// 根管理员
	public static final SingleRoleDto root_admin = new SingleRoleDto(ROOT_ADMIN, ROOT_ADMIN_STR, (short)UserTypeEnum.ROOTADMIN.getValue());
	
	// 系统管理员
	public static final SingleRoleDto system_admin = new SingleRoleDto(SYSTEM_ADMIN, SYSTEM_ADMIN_STR, (short)UserTypeEnum.SYSTEMADMIN.getValue());
	
	// 仓库管理员
	public static final SingleRoleDto warehouse_admin = new SingleRoleDto(WAREHOUSE_ADMIN, WAREHOUSE_ADMIN_STR, (short)UserTypeEnum.WAREHOUSEMANAGER.getValue());
	
	// 普通员工
	public static final SingleRoleDto general_employee = new SingleRoleDto(GENERAL_EMPLOYEE, GENERAL_EMPLOYEE_STR, (short)UserTypeEnum.GENERALEMPLOYEE.getValue());
	
	// 其他员工
	public static final SingleRoleDto other_employees = new SingleRoleDto(OTHER_EMPLOYEES, OTHER_EMPLOYEES_STR, (short)UserTypeEnum.OTHEREMPLOYEES.getValue());
	
	// 临时员工
	public static final SingleRoleDto temporary_employee = new SingleRoleDto(TEMPORARY_EMPLOYEE, TEMPORARY_EMPLOYEE_STR, (short)UserTypeEnum.TEMPORARYEMPLOYEE.getValue());

}
