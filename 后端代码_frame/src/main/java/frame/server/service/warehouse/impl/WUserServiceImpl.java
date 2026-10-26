package frame.server.service.warehouse.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import frame.server.constant.DefaultAvatarConstant;
import frame.server.constant.RolesConstant;
import frame.server.constant.UserAccountConstant;
import frame.server.constant.UserTypeEnum;
import frame.server.dao.ApplicationDao;
import frame.server.dao.RoleDao;
import frame.server.dao.RolePermissionDao;
import frame.server.dao.UserDao;
import frame.server.dao.UserExtensionDao;
import frame.server.dao.UserPermissionDao;
import frame.server.dao.UserRoleDao;
import frame.server.dao.UserWarehouseApplicationSettingDao;
import frame.server.dao.UserWarehouseDao;
import frame.server.dao.WarehouseDao;
import frame.server.domain.Role;
import frame.server.domain.User;
import frame.server.domain.UserExtension;
import frame.server.domain.UserPermission;
import frame.server.domain.UserRole;
import frame.server.domain.UserWarehouse;
import frame.server.domain.Warehouse;
import frame.server.dto.UserDto;
import frame.server.dto.UserInfoDto;
import frame.server.service.warehouse.WUserService;
import frame.server.utils.PasswordGenerateUtil;
import frame.server.utils.RandomUtil;
import frame.server.utils.StringUtil;
import frame.server.vo.Page;
import frame.server.vo.warehouse.WUserController.AddAdministratorModel;
import frame.server.vo.warehouse.WUserController.EditAdministratorModel;
import frame.server.vo.warehouse.WUserController.UpdateAssignWarehouseModel;

@Service("wUserService")
public class WUserServiceImpl implements WUserService {

	@Autowired
	private ApplicationDao applicationDao;

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
	private UserWarehouseApplicationSettingDao userWarehouseApplicationSettingDao;

	@Autowired
	private UserWarehouseDao userWarehouseDao;

	@Autowired
	private WarehouseDao warehouseDao;

	@Override
	public List<User> getAllUsers() {
		return userDao.selectWarehouseAllUser();
	}

	@Override
	public PageInfo<UserDto> getUsers(Page page) {
		// 结果集
		PageInfo<UserDto> pageInfo = null;

		// 开启分页
		PageHelper.startPage(page.getPageNum(), page.getPageSize());

		// 获取检索条件
		JSONObject searchCondition = page.getSearchCondition();
		if (searchCondition == null || searchCondition.isEmpty()) {
			pageInfo = new PageInfo<>(userDao.w_selectUserDtos());
			return pageInfo;
		}

		String searchType = searchCondition.getString("searchType");
		String searchStr = searchCondition.getString("searchStr");

		if (StringUtil.isBlank(searchStr)) {
			pageInfo = new PageInfo<>(userDao.w_selectUserDtos());
		} else {
			switch (searchType) {
			// 姓名
			case "1":
				pageInfo = new PageInfo<>(userDao.w_selectUserDtoLikeUserName(searchStr));
				break;
			// 账号
			case "2":
				pageInfo = new PageInfo<>(userDao.w_selectUserDtoLikeUserAccount(searchStr));
				break;
			// 类型
			case "3":
				String type = "0";
				switch (searchStr) {
				case RolesConstant.SYSTEM_ADMIN:
					type = Integer.toString(UserTypeEnum.SYSTEMADMIN.getValue());
					break;
				case RolesConstant.WAREHOUSE_ADMIN:
					type = Integer.toString(UserTypeEnum.WAREHOUSEMANAGER.getValue());
					break;
				case RolesConstant.OTHER_EMPLOYEES:
					type = Integer.toString(UserTypeEnum.OTHEREMPLOYEES.getValue());
					break;
				default:
					return null;
				}
				pageInfo = new PageInfo<>(userDao.w_selectUserDtoByUserType(type));
				break;
			default:
				pageInfo = new PageInfo<>(userDao.w_selectUserDtos());
			}
		}

		// 移除password
		for (UserDto user : pageInfo.getList()) {
			user.setPassword("");
		}

		return pageInfo;
	}

	@Override
	public User getUserByUserId(String userId) {
		return userDao.selectWarehouseUserById(userId);
	}

	@Override
	public boolean isAccountRepeat(String account) {
		return userDao.selectUserCountByAccount(account) != 0;
	}

	@Override
	public UserDto addUser(AddAdministratorModel model) {
		Date curDate = new Date();

		// 设置初始化密码
		String initializedPassword = PasswordGenerateUtil.randomPassword();

		// 设置用户头像 系统管理员 | 仓库管理员 | 申请人员
		String avatar = null;
		if (model.getType().equals(Integer.toString(UserTypeEnum.SYSTEMADMIN.getValue()))) {
			avatar = StringUtil.isBlank(model.getAvatar()) ? DefaultAvatarConstant.WAREHOUSE_DEFAULT_AVATAR
					: model.getAvatar();
		} else if (model.getType().equals(Integer.toString(UserTypeEnum.WAREHOUSEMANAGER.getValue()))) {
			avatar = StringUtil.isBlank(model.getAvatar()) ? DefaultAvatarConstant.CONSUMABLES_DEFAULT_AVATAR
					: model.getAvatar();
		} else {
			avatar = StringUtil.isBlank(model.getAvatar()) ? DefaultAvatarConstant.APPLICATION_DEFAULT_AVATAR
					: model.getAvatar();
		}

		// 初始化用户信息
		User user = new User(RandomUtil.genarateId(), model.getAccount(), DigestUtils.md5Hex(initializedPassword),
				model.getName(), model.getGender(), avatar, model.getEmail(), model.getPhone(), model.getType(),
				curDate, false, false);

		// 设置用户扩展信息
		UserExtension userExtension = new UserExtension();
		userExtension.setUserId(user.getId());
		userExtension.setInitializedPassword(initializedPassword);

		if (user.getType().equals(Integer.toString(UserTypeEnum.OTHEREMPLOYEES.getValue()))) {
			userExtension.setDepartment(model.getDepartment());
		}

		// 设置用户角色
		Role role = null;
		if (model.getType().equals(Integer.toString(UserTypeEnum.SYSTEMADMIN.getValue()))) {
			role = roleDao.selectByRoleStr(RolesConstant.system_admin.getStr());
		} else if (model.getType().equals(Integer.toString(UserTypeEnum.WAREHOUSEMANAGER.getValue()))) {
			role = roleDao.selectByRoleStr(RolesConstant.warehouse_admin.getStr());
		} else {
			role = roleDao.selectByRoleStr(RolesConstant.other_employees.getStr());
		}
		List<UserRole> userRoles = new ArrayList<UserRole>();
		userRoles.add(new UserRole(user.getId(), role.getId()));

		// 设置用户权限
		// 说明：系统管理员具备所有权限;仓库管理员仅仅具备消耗品管理系统权限;申请人员仅具备消耗品申请系统权限
		List<String> permissionIds = rolePermissionDao.selectPermissionIdsByRoleId(role.getId());
		List<UserPermission> userPermissions = new ArrayList<UserPermission>();
		for (String permissionId : permissionIds) {
			UserPermission userPermission = new UserPermission(user.getId(), permissionId);
			userPermissions.add(userPermission);
		}

		// 新增系统管理员同步设置所有仓库关联关系
		List<UserWarehouse> userWarehouses = new ArrayList<UserWarehouse>();
		List<String> warehouseIds = warehouseDao.selectAllId();
		if (user.getType().equals(Integer.toString(UserTypeEnum.SYSTEMADMIN.getValue()))) {
			if (warehouseIds != null && !warehouseIds.isEmpty()) {
				for (String warehouseId : warehouseIds) {
					userWarehouses.add(new UserWarehouse(user.getId(), warehouseId, curDate));
				}
			}
		}

		// 将数据保存至数据库
		userDao.insertUser(user);
		userExtensionDao.insertUserExtension(userExtension);
		userRoleDao.insertUserRoles(userRoles);
		userPermissionDao.insertUserPermissions(userPermissions);
		if (!userWarehouses.isEmpty()) {
			userWarehouseDao.insertUserWarehouses(userWarehouses);
		}

		UserDto userNew = userDao.selectUserDtoByUserId(user.getId());

		// 移除密码
		userNew.setPassword("");

		return userNew;
	}

	@Override
	public UserDto updateUser(EditAdministratorModel model) {
		// 构造用户信息实体
		UserInfoDto userInfo = new UserInfoDto(model.getId(), model.getName(), model.getGender(), model.getAvatar(),
				model.getEmail(), model.getPhone(), model.getType());

		// 构造用户扩展信息实体
		UserExtension userExtension = new UserExtension();
		userExtension.setUserId(userInfo.getId());
		if (userInfo.getType().equals(Integer.toString(UserTypeEnum.OTHEREMPLOYEES.getValue()))) {
			userExtension.setDepartment(model.getDepartment());
		} else {
			userExtension.setDepartment("");
		}

		// 检测用户类型是否更新;若更新了类型需要更新角色表和权限表
		User userOld = userDao.selectWarehouseUserById(userInfo.getId());

		// 未更新用户类型
		if (userOld.getType().equals(userInfo.getType())) {
			userDao.updateUserInfo(userInfo);
			userExtensionDao.w_updateUserDepartment(userExtension);

			UserDto userNew = userDao.selectUserDtoByUserId(model.getId());
			userNew.setPassword("");

			return userNew;
		}

		// 已经更新用户类型
		// 配置新角色
		Role role = null;
		if (model.getType().equals(Integer.toString(UserTypeEnum.SYSTEMADMIN.getValue()))) {
			role = roleDao.selectByRoleStr(RolesConstant.system_admin.getStr());
		} else if (model.getType().equals(Integer.toString(UserTypeEnum.WAREHOUSEMANAGER.getValue()))) {
			role = roleDao.selectByRoleStr(RolesConstant.warehouse_admin.getStr());
		} else {
			role = roleDao.selectByRoleStr(RolesConstant.other_employees.getStr());
		}
		List<UserRole> userRoles = new ArrayList<UserRole>();
		userRoles.add(new UserRole(model.getId(), role.getId()));

		// 配置新权限
		List<String> permissionIds = rolePermissionDao.selectPermissionIdsByRoleId(role.getId());
		List<UserPermission> userPermissions = new ArrayList<UserPermission>();
		for (String permissionId : permissionIds) {
			UserPermission userPermission = new UserPermission(model.getId(), permissionId);
			userPermissions.add(userPermission);
		}

		// 更新用户信息
		userDao.updateUserInfo(userInfo);
		userExtensionDao.w_updateUserDepartment(userExtension);

		// 清除旧角色和旧权限
		userRoleDao.deleteByUserId(userInfo.getId());
		userPermissionDao.deleteByUserId(userInfo.getId());

		// 设置新角色和新权限
		userRoleDao.insertUserRoles(userRoles);
		userPermissionDao.insertUserPermissions(userPermissions);

		// 若用户当前角色为申请人员，则移除所有仓库关联关系
		if (userInfo.getType().equals(Integer.toString(UserTypeEnum.OTHEREMPLOYEES.getValue()))) {
			userWarehouseDao.deleteByUserId(userInfo.getId());
		}
		// 若用户当前角色为系统管理员，则添加所有仓库关联管理
		else if (userInfo.getType().equals(Integer.toString(UserTypeEnum.SYSTEMADMIN.getValue()))) {
			Date curDate = new Date();
			List<String> warehouseIds = warehouseDao.selectAllId();
			if (warehouseIds != null && !warehouseIds.isEmpty()) {
				List<UserWarehouse> userWarehouses = new ArrayList<UserWarehouse>();
				for (String warehouseId : warehouseIds) {
					userWarehouses.add(new UserWarehouse(userInfo.getId(), warehouseId, curDate));
				}

				// 清除旧关联关系
				userWarehouseDao.deleteByUserId(userInfo.getId());

				// 配置新关联关系
				userWarehouseDao.insertUserWarehouses(userWarehouses);
			}
		}
		
		// 若用户旧角色为申请人员，则删除用户可申请仓库配置的关联关系
		if(userOld.getType().equals(Integer.toString(UserTypeEnum.OTHEREMPLOYEES.getValue()))) {
			userWarehouseApplicationSettingDao.deleteByUserId(userInfo.getId());
		}

		UserDto userNew = userDao.selectUserDtoByUserId(model.getId());
		userNew.setPassword("");

		return userNew;
	}

	@Override
	public void updateAdministratorState(String userId) {
		userDao.updateUserState(userId);
	}

	@Override
	public boolean checkUserHasApplication(String userId) {
		return applicationDao.w_selectUndoneApplicationByUserId(userId) > 0;
	}

	@Override
	public void deleteAdministrator(String userId) {
		// 删除申请人员可申请仓库配置关联关系
		userWarehouseApplicationSettingDao.deleteByUserId(userId);
		
		// 假删除用户
		userDao.updateUserDeleteState(userId);
	}

	@Override
	public void updateAdministratorPassword(String userId, boolean isRootAdmin) {
		User user = new User();
		user.setId(userId);
		if (isRootAdmin) {
			user.setPassword(UserAccountConstant.root_admin_password);
		} else {
			UserExtension userExtension = userExtensionDao.selectByUserId(userId);
			user.setPassword(DigestUtils.md5Hex(userExtension.getInitializedPassword()));
		}
		userDao.updateUserPassword(user);
	}

	@Override
	public Map<String, List<Warehouse>> getAssignedWarehouses(String userId) {
		// 数据库中没有仓库数据
		int warehouseCount = warehouseDao.selectWarehousesCount();
		if (warehouseCount == 0) {
			return null;
		}

		// 查询出所有已经分配的仓库数据
		List<Warehouse> assignedWarehouses = userWarehouseDao.selectWarehousesByUserId(userId);

		Map<String, List<Warehouse>> res = new HashMap<String, List<Warehouse>>();

		res.put("assignedWarehouse", assignedWarehouses);

		// 若已经分配的数量 == 全部仓库数，则直接返回
		// 说明：这块放开，用户在分配仓库页面上可以进行增加或删除操作，这样比较方便
		/*
		 * if (assignedWarehouses.size() == warehouseCount) { res.put("warehouses", new
		 * ArrayList<Warehouse>()); return res; }
		 */

		// 查询出全部仓库数据
		List<Warehouse> warehouses = warehouseDao.selectAll();

		res.put("warehouses", warehouses);

		return res;
	}

	@Override
	public boolean checkWarehouseIds(List<String> warehouseIds) {
		if (warehouseIds == null) {
			return true;
		}

		for (String warehouseId : warehouseIds) {
			if (warehouseDao.selectWarehouseById(warehouseId) == null) {
				return false;
			}
		}

		return true;
	}

	@Override
	public List<Warehouse> assignWarehouse(UpdateAssignWarehouseModel model) {
		// 全删全插
		userWarehouseDao.deleteByUserId(model.getUserId());

		if (model.getWarehouseIds() == null) {
			return new ArrayList<Warehouse>();
		}

		// 构造数据
		List<UserWarehouse> userWarehouses = new ArrayList<UserWarehouse>();
		for (String warehouseId : model.getWarehouseIds()) {
			userWarehouses.add(new UserWarehouse(model.getUserId(), warehouseId, new Date()));
		}
		userWarehouseDao.insertUserWarehouses(userWarehouses);

		return userWarehouseDao.selectWarehousesByUserId(model.getUserId());
	}

}
