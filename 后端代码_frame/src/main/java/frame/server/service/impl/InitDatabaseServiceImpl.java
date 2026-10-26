package frame.server.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import frame.server.constant.DefaultAvatarConstant;
import frame.server.constant.PermissionsConstant;
import frame.server.constant.ReferenceInformationConstant;
import frame.server.constant.RolesConstant;
import frame.server.constant.UserAccountConstant;
import frame.server.constant.UserTypeEnum;
import frame.server.dao.PermissionDao;
import frame.server.dao.ReferenceInformationTemplateDao;
import frame.server.dao.ReferenceInformationTemplateDetailDao;
import frame.server.dao.RoleDao;
import frame.server.dao.RolePermissionDao;
import frame.server.dao.UserDao;
import frame.server.dao.UserExtensionDao;
import frame.server.dao.UserPermissionDao;
import frame.server.dao.UserRoleDao;
import frame.server.dao.WarehouseDao;
import frame.server.dao.UserWarehouseDao;
import frame.server.domain.Permission;
import frame.server.domain.ReferenceInformationTemplate;
import frame.server.domain.ReferenceInformationTemplateDetail;
import frame.server.domain.Role;
import frame.server.domain.RolePermission;
import frame.server.domain.User;
import frame.server.domain.UserExtension;
import frame.server.domain.UserPermission;
import frame.server.domain.Warehouse;
import frame.server.domain.UserWarehouse;
import frame.server.domain.UserRole;
import frame.server.dto.ReferenceInformationTemplateDto;
import frame.server.service.InitDatabaseService;
import frame.server.utils.RandomUtil;

/**
 * 初始化数据库
 * 
 * 1. 初始化权限表 2. 初始化角色表 3. 初始化角色与权限对应关系 4. 初始化用户表 5. 初始化用户与角色对应关系 6. 初始化用户与权限对应关系
 * 
 * 说明：frame.server.PermissionsConstant保存系统权限信息，系统权限即系统边界
 * frame.server.RolesConstant保存初始化所需的角色信息
 * 
 * @author Administrator
 *
 */
@Service("initDatabaseService")
public class InitDatabaseServiceImpl implements InitDatabaseService {

	@Autowired
	private PermissionDao permissionDao;

	@Autowired
	private ReferenceInformationTemplateDao referenceInformationTemplateDao;

	@Autowired
	private ReferenceInformationTemplateDetailDao referenceInformationTemplateDetailDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private RolePermissionDao rolePermissionDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private UserExtensionDao userExtensionDao;

	@Autowired
	private UserPermissionDao userPermissionDao;

	@Autowired
	private UserRoleDao userRoleDao;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private WarehouseDao warehouseDao;

	@Autowired
	private UserWarehouseDao userWarehouseDao;

	@Override
	public List<Permission> initPermission() {
		permissionDao.clear();

		List<Permission> permissions = new ArrayList<Permission>();

		// 仓库管理系统
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.W_LOGIN_NAME,
				PermissionsConstant.W_LOGIN_STR, 0));

		// 消耗品管理系统
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_LOGIN_NAME,
				PermissionsConstant.C_LOGIN_STR, 100));

		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_ROLE_ADD_NAME,
				PermissionsConstant.C_ROLE_ADD_STR, 200));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_ROLE_DELETE_NAME,
				PermissionsConstant.C_ROLE_DELETE_STR, 210));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_ROLE_UPDATE_NAME,
				PermissionsConstant.C_ROLE_UPDATE_STR, 220));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_ROLE_SELECT_NAME,
				PermissionsConstant.C_ROLE_SELECT_STR, 230));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_ROLE_IMPORT_NAME,
				PermissionsConstant.C_ROLE_IMPORT_STR, 240));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_ROLE_EXPORT_NAME,
				PermissionsConstant.C_ROLE_EXPORT_STR, 250));

		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_USER_ADD_NAME,
				PermissionsConstant.C_USER_ADD_STR, 300));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_USER_DELETE_NAME,
				PermissionsConstant.C_USER_DELETE_STR, 310));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_USER_UPDATE_NAME,
				PermissionsConstant.C_USER_UPDATE_STR, 320));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_USER_SELECT_NAME,
				PermissionsConstant.C_USER_SELECT_STR, 330));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_USER_STATE_MANAGEMENT_NAME,
				PermissionsConstant.C_USER_STATE_MANAGEMENT_STR, 340));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_USER_IMPORT_NAME,
				PermissionsConstant.C_USER_IMPORT_STR, 350));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_USER_EXPORT_NAME,
				PermissionsConstant.C_USER_EXPORT_STR, 360));

		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_CONSUMABLESTYPE_ADD_NAME,
				PermissionsConstant.C_CONSUMABLESTYPE_ADD_STR, 400));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_CONSUMABLESTYPE_DELETE_NAME,
				PermissionsConstant.C_CONSUMABLESTYPE_DELETE_STR, 410));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_CONSUMABLESTYPE_UPDATE_NAME,
				PermissionsConstant.C_CONSUMABLESTYPE_UPDATE_STR, 420));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_CONSUMABLESTYPE_SELECT_NAME,
				PermissionsConstant.C_CONSUMABLESTYPE_SELECT_STR, 430));
		permissions.add(
				new Permission(RandomUtil.genarateId(), PermissionsConstant.C_CONSUMABLESTYPE_STATE_MANAGEMENT_NAME,
						PermissionsConstant.C_CONSUMABLESTYPE_STATE_MANAGEMENT_STR, 440));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_CONSUMABLESTYPE_IMPORT_NAME,
				PermissionsConstant.C_CONSUMABLESTYPE_IMPORT_STR, 450));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_CONSUMABLESTYPE_EXPORT_NAME,
				PermissionsConstant.C_CONSUMABLESTYPE_EXPORT_STR, 460));

		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_CONSUMABLES_ADD_NAME,
				PermissionsConstant.C_CONSUMABLES_ADD_STR, 500));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_CONSUMABLES_DELETE_NAME,
				PermissionsConstant.C_CONSUMABLES_DELETE_STR, 510));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_CONSUMABLES_UPDATE_NAME,
				PermissionsConstant.C_CONSUMABLES_UPDATE_STR, 520));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_CONSUMABLES_SELECT_NAME,
				PermissionsConstant.C_CONSUMABLES_SELECT_STR, 530));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_CONSUMABLES_STATE_MANAGEMENT_NAME,
				PermissionsConstant.C_CONSUMABLES_STATE_MANAGEMENT_STR, 540));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_CONSUMABLES_INBOUND_NAME,
				PermissionsConstant.C_CONSUMABLES_INBOUND_STR, 550));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_CONSUMABLES_OUTBOUND_NAME,
				PermissionsConstant.C_CONSUMABLES_OUTBOUND_STR, 560));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_CONSUMABLES_IMPORT_NAME,
				PermissionsConstant.C_CONSUMABLES_IMPORT_STR, 570));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_CONSUMABLES_EXPORT_NAME,
				PermissionsConstant.C_CONSUMABLES_EXPORT_STR, 580));

		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_APPLICATION_SELECT_NAME,
				PermissionsConstant.C_APPLICATION_SELECT_STR, 600));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_APPLICATION_STATE_MANAGEMENT_NAME,
				PermissionsConstant.C_APPLICATION_STATE_MANAGEMENT_STR, 610));

		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_USEROPERATE_SELECT_NAME,
				PermissionsConstant.C_USEROPERATE_SELECT_STR, 700));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_USEROPERATE_EXPORT_NAME,
				PermissionsConstant.C_USEROPERATE_EXPORT_STR, 710));

		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_STATISTICS_SELECT_NAME,
				PermissionsConstant.C_STATISTICS_SELECT_STR, 800));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_STATISTICS_EXPORT_NAME,
				PermissionsConstant.C_STATISTICS_EXPORT_STR, 810));

		// 消耗品基准信息模板
		permissions.add(
				new Permission(RandomUtil.genarateId(), PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_ADD_NAME,
						PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_ADD_STR, 900));
		permissions.add(new Permission(RandomUtil.genarateId(),
				PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_DELETE_NAME,
				PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_DELETE_STR, 910));
		permissions.add(
				new Permission(RandomUtil.genarateId(), PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_EDIT_NAME,
						PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_EDIT_STR, 920));
		permissions.add(new Permission(RandomUtil.genarateId(),
				PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_SELECT_NAME,
				PermissionsConstant.C_REFERENCE_INFORMATION_TEMPLATE_SELECT_STR, 930));

		// 报废管理权限
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_SCRAP_ADD_NAME,
				PermissionsConstant.C_SCRAP_ADD_STR, 1040));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_SCRAP_DELETE_NAME,
				PermissionsConstant.C_SCRAP_DELETE_STR, 1050));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_SCRAP_UPDATE_NAME,
				PermissionsConstant.C_SCRAP_UPDATE_STR, 1060));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_SCRAP_SELECT_NAME,
				PermissionsConstant.C_SCRAP_SELECT_STR, 1070));

		// 供应商管理权限
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_SUPPLIER_ADD_NAME,
				PermissionsConstant.C_SUPPLIER_ADD_STR, 1000));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_SUPPLIER_DELETE_NAME,
				PermissionsConstant.C_SUPPLIER_DELETE_STR, 1010));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_SUPPLIER_UPDATE_NAME,
				PermissionsConstant.C_SUPPLIER_UPDATE_STR, 1020));
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.C_SUPPLIER_SELECT_NAME,
				PermissionsConstant.C_SUPPLIER_SELECT_STR, 1030));

		// 消耗品申请系统
		permissions.add(new Permission(RandomUtil.genarateId(), PermissionsConstant.A_LOGIN_NAME,
				PermissionsConstant.A_LOGIN_STR, 2000));

		permissionDao.insertPermissions(permissions);
		return permissionDao.selectAll();
	}

	@Override
	public List<Role> initRole() {
		roleDao.clear();

		List<Role> roles = new ArrayList<Role>();

		Date now = new Date();
		roles.add(
				new Role(RandomUtil.genarateId(), RolesConstant.root_admin.getName(), RolesConstant.root_admin.getStr(),
						(short) RolesConstant.root_admin.getType(), "系统默认", now, false, false));
		roles.add(new Role(RandomUtil.genarateId(), RolesConstant.system_admin.getName(),
				RolesConstant.system_admin.getStr(), (short) RolesConstant.system_admin.getType(), "系统默认", now, false,
				false));
		roles.add(new Role(RandomUtil.genarateId(), RolesConstant.warehouse_admin.getName(),
				RolesConstant.warehouse_admin.getStr(), (short) RolesConstant.warehouse_admin.getType(), "系统默认", now,
				false, false));
		roles.add(new Role(RandomUtil.genarateId(), RolesConstant.general_employee.getName(),
				RolesConstant.general_employee.getStr(), (short) RolesConstant.general_employee.getType(), "系统默认", now,
				false, false));
		roles.add(new Role(RandomUtil.genarateId(), RolesConstant.other_employees.getName(),
				RolesConstant.other_employees.getStr(), (short) RolesConstant.other_employees.getType(), "系统默认", now,
				false, false));
		roles.add(new Role(RandomUtil.genarateId(), RolesConstant.temporary_employee.getName(),
				RolesConstant.temporary_employee.getStr(), (short) RolesConstant.temporary_employee.getType(), "系统默认",
				now, false, false));

		roleDao.insertRoles(roles);
		return roleDao.selectAll();
	}

	@Override
	public List<RolePermission> initRolePermission() {
		rolePermissionDao.clear();

		List<Permission> permissions = permissionDao.selectAll();
		List<Role> roles = roleDao.selectAll();

		if (permissions.isEmpty() || roles.isEmpty()) {
			rolePermissionDao.clear();
			System.err.println("初始化\"RolePermission\"错误: 未初始化Permission或Role。");
			return null;
		}

		List<RolePermission> rolePermissions = new ArrayList<RolePermission>();
		for (Role role : roles) {
			switch (role.getStr()) {
			// 根管理员与系统管理员具备全部权限
			case RolesConstant.ROOT_ADMIN_STR:
			case RolesConstant.SYSTEM_ADMIN_STR:
				for (Permission permission : permissions) {
					RolePermission rolePermission = new RolePermission(role.getId(), permission.getId());
					rolePermissions.add(rolePermission);
				}
				break;
			// 仓库管理员具备仓库管理系统全部权限
			case RolesConstant.WAREHOUSE_ADMIN_STR:
				for (Permission permission : permissions) {
					if (permission.getStr().startsWith("c_")) {
						RolePermission rolePermission = new RolePermission(role.getId(), permission.getId());
						rolePermissions.add(rolePermission);
					}
				}
				break;
			// 其他人员具备消耗品申请系统全部权限
			case RolesConstant.OTHER_EMPLOYEES_STR: 
				for (Permission permission : permissions) {
					if (permission.getStr().startsWith("a_")) {
						RolePermission rolePermission = new RolePermission(role.getId(), permission.getId());
						rolePermissions.add(rolePermission);
					}
				}
				break;
			}
		}

		rolePermissionDao.insertRolePermissions(rolePermissions);
		return rolePermissionDao.selectAll();
	}

	@Override
	public List<User> initUser() {
		userExtensionDao.clear();
		userDao.clear();

		List<User> users = new ArrayList<User>();
		List<UserExtension> userExtensions = new ArrayList<UserExtension>();

		Date now = new Date();
		// 取消根管理员，统一初始化为系统管理员
		String userId = RandomUtil.genarateId();
		users.add(new User(userId, UserAccountConstant.root_admin_account, UserAccountConstant.root_admin_password,
				UserAccountConstant.root_admin_name, UserAccountConstant.root_admin_gender,
				DefaultAvatarConstant.WAREHOUSE_DEFAULT_AVATAR, "", "",
				Short.toString((short) UserTypeEnum.SYSTEMADMIN.getValue()), now, true, false));
		userExtensions.add(new UserExtension(userId, "", ""));

		userDao.insertUsers(users);
		userExtensionDao.insertUserExtensions(userExtensions);
		// 创建默认仓库
		userWarehouseDao.clear();
		warehouseDao.clear();
		String warehouseId = RandomUtil.genarateId();
		Warehouse warehouse = new Warehouse(warehouseId, "", "默认仓库", "", "系统初始化仓库", now, 0, true, false);
		warehouseDao.insertWarehouse(warehouse);
		
		// 关联用户与仓库
		userWarehouseDao.insertUserWarehouse(new UserWarehouse(userId, warehouseId, now));
		// 创建报废表
		try {
			String sql = "CREATE TABLE IF NOT EXISTS scrap ("
				+ "id VARCHAR(64) PRIMARY KEY, "
				+ "warehouse_id VARCHAR(64), "
				+ "consumable_id VARCHAR(64), "
				+ "quantity INT DEFAULT 0, "
				+ "reason VARCHAR(500), "
				+ "operator VARCHAR(100), "
				+ "status INT DEFAULT 0, "
				+ "create_time DATETIME, "
				+ "is_delete TINYINT(1) DEFAULT 0"
				+ ")";
			jdbcTemplate.execute(sql);
		} catch (Exception e) {

			System.err.println("创建报废表失败: " + e.getMessage());
		}

		return userDao.selectWarehouseAllUser();
	}

	@Override
	public List<UserRole> initUserRole() {
		userRoleDao.clear();

		List<Role> roles = roleDao.selectAll();
		List<User> users = userDao.selectWarehouseAllUser();

		if (users.isEmpty()) {
			System.err.println("初始化\"UserRole\"错误: 未初始化User。");
			return null;
		} else {
			if (roles.isEmpty()) {
				for (User user : users) {
					userRoleDao.deleteByUserId(user.getId());
				}
				System.err.println("初始化\"UserRole\"错误: 未初始化Role。");
				return null;
			}
		}

		List<UserRole> userRoles = new ArrayList<UserRole>();
		for (User user : users) {
			userRoleDao.deleteByUserId(user.getId());

			// 系统管理员
			if (user.getAccount().equals(UserAccountConstant.root_admin_account)) {
				for (Role role : roles) {
					if (role.getStr().equals(RolesConstant.system_admin.getStr())) {
						UserRole userRole = new UserRole(user.getId(), role.getId());
						userRoles.add(userRole);
					}
				}
			}
		}

		userRoleDao.insertUserRoles(userRoles);
		return userRoleDao.selectAll();
	}

	@Override
	public List<UserPermission> initUserPermission() {
		userPermissionDao.clear();

		List<Permission> permissions = permissionDao.selectAll();
		List<Role> roles = roleDao.selectAll();
		List<RolePermission> rolePermissions = rolePermissionDao.selectAll();
		List<User> users = userDao.selectWarehouseAllUser();
		List<UserRole> userRoles = userRoleDao.selectAll();

		if (permissions.isEmpty() || roles.isEmpty() || rolePermissions.isEmpty() || users.isEmpty()
				|| userRoles.isEmpty()) {
			System.err.println("初始化\"UserPermission\"错误: 未初始化其余表。");
			return null;
		}

		List<UserPermission> userPermissions = new ArrayList<UserPermission>();
		for (User user : users) {
			userPermissionDao.deleteByUserId(user.getId());

			for (UserRole userRole : userRoles) {
				if (userRole.getUserId().equals(user.getId())) {
					for (RolePermission rolePermission : rolePermissions) {
						if (rolePermission.getRoleId().equals(userRole.getRoleId())) {
							UserPermission userPermission = new UserPermission(user.getId(),
									rolePermission.getPermissionId());
							userPermissions.add(userPermission);
						}
					}
				}
			}
		}

		userPermissionDao.insertUserPermissions(userPermissions);
		return userPermissionDao.selectAll();
	}

	@Override
	public List<ReferenceInformationTemplateDto> initReferenceInformationTemplate() {
		// 获取系统内置基准信息模板ID
		String templateId = referenceInformationTemplateDao.c_selectSystemTemplate();
		
		// 删除系统内置基准信息详细
		referenceInformationTemplateDetailDao.deleteByReferenceInformationTemplateId(templateId);
		
		// 删除系统内置基准信息模板
		referenceInformationTemplateDao.deleteSystemTemplate();
		
		ReferenceInformationTemplate template = new ReferenceInformationTemplate(RandomUtil.genarateId(), "", "基准信息标准模板", "系统内置", new Date(), true, false);
		List<ReferenceInformationTemplateDetail> templateDetails = new ArrayList<ReferenceInformationTemplateDetail>();
		
		int i = 1;

		templateDetails.add(new ReferenceInformationTemplateDetail(RandomUtil.genarateId(), template.getId(), ReferenceInformationConstant.LOCATION, i++));
		templateDetails.add(new ReferenceInformationTemplateDetail(RandomUtil.genarateId(), template.getId(), ReferenceInformationConstant.SUBCATEGORY, i++));
		templateDetails.add(new ReferenceInformationTemplateDetail(RandomUtil.genarateId(), template.getId(), ReferenceInformationConstant.APPLICABLE_PROJECT_0, i++));
		templateDetails.add(new ReferenceInformationTemplateDetail(RandomUtil.genarateId(), template.getId(), ReferenceInformationConstant.APPLICABLE_PROJECT_1, i++));
		templateDetails.add(new ReferenceInformationTemplateDetail(RandomUtil.genarateId(), template.getId(), ReferenceInformationConstant.APPLICABLE_PROJECT_2, i++));
		templateDetails.add(new ReferenceInformationTemplateDetail(RandomUtil.genarateId(), template.getId(), ReferenceInformationConstant.APPLICABLE_MODELS, i++));
		templateDetails.add(new ReferenceInformationTemplateDetail(RandomUtil.genarateId(), template.getId(), ReferenceInformationConstant.ITEM_TYPE1, i++));
		templateDetails.add(new ReferenceInformationTemplateDetail(RandomUtil.genarateId(), template.getId(), ReferenceInformationConstant.ITEM_TYPE2, i++));
		templateDetails.add(new ReferenceInformationTemplateDetail(RandomUtil.genarateId(), template.getId(), ReferenceInformationConstant.UNIT, i++));
		templateDetails.add(new ReferenceInformationTemplateDetail(RandomUtil.genarateId(), template.getId(), ReferenceInformationConstant.SAFETY_STOCK, i++));
		templateDetails.add(new ReferenceInformationTemplateDetail(RandomUtil.genarateId(), template.getId(), ReferenceInformationConstant.CURRENT_INVENTORY, i++));
		templateDetails.add(new ReferenceInformationTemplateDetail(RandomUtil.genarateId(), template.getId(), ReferenceInformationConstant.OUT_OF_STOCK_LAST_WEEK, i++));
		templateDetails.add(new ReferenceInformationTemplateDetail(RandomUtil.genarateId(), template.getId(), ReferenceInformationConstant.OUT_OF_THE_MONTH, i++));
		templateDetails.add(new ReferenceInformationTemplateDetail(RandomUtil.genarateId(), template.getId(), ReferenceInformationConstant.UNIT_PRICE, i++));
		templateDetails.add(new ReferenceInformationTemplateDetail(RandomUtil.genarateId(), template.getId(), ReferenceInformationConstant.INVENTORY_AAMOUNT, i++));
		templateDetails.add(new ReferenceInformationTemplateDetail(RandomUtil.genarateId(), template.getId(), ReferenceInformationConstant.INDUSTRY_BODY, i++));
		templateDetails.add(new ReferenceInformationTemplateDetail(RandomUtil.genarateId(), template.getId(), ReferenceInformationConstant.PURCHASING_CYCLE, i++));
		
		referenceInformationTemplateDao.insertReferenceInformationTemplate(template);
		referenceInformationTemplateDetailDao.insertReferenceInformationTemplateDetails(templateDetails);
		
		return referenceInformationTemplateDao.c_selectReferenceInformationTemplateDtoByWarehouseId("");
	}

}
