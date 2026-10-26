package frame.server.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import frame.server.constant.DefaultAvatarConstant;
import frame.server.constant.RolesConstant;
import frame.server.constant.UserTypeEnum;
import frame.server.dao.PermissionDao;
import frame.server.dao.RoleDao;
import frame.server.dao.RolePermissionDao;
import frame.server.dao.UserDao;
import frame.server.dao.UserExtensionDao;
import frame.server.dao.UserPermissionDao;
import frame.server.dao.UserRoleDao;
import frame.server.dao.UserWarehouseDao;
import frame.server.dao.WarehouseDao;
import frame.server.domain.Permission;
import frame.server.domain.Role;
import frame.server.domain.User;
import frame.server.domain.UserExtension;
import frame.server.domain.UserPermission;
import frame.server.domain.UserRole;
import frame.server.domain.Warehouse;
import frame.server.dto.UserDto;
import frame.server.service.LoginService;
import frame.server.utils.PasswordGenerateUtil;
import frame.server.utils.RandomUtil;
import frame.server.vo.LoginController.RegisterModel;

@Service("loginService")
public class LoginServiceImpl implements LoginService {

	@Autowired
	private PermissionDao permissionDao;

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
	private UserWarehouseDao userWarehouseDao;

	@Autowired
	private WarehouseDao warehouseDao;

	@Override
	public UserDto getUserInfo(String account) {
		return userDao.selectUserDtoByAccount(account);
	}

	@Override
	public List<Permission> getSystemPermission() {
		return permissionDao.selectAll();
	}

	@Override
	public List<Warehouse> getWarehouses(String userId, boolean isSystemManager) {
		if (isSystemManager) {
			return warehouseDao.selectEnabledAll();
		}
		return userWarehouseDao.selectEnabledWarehousesByUserId(userId);
	}

	@Override
	public void register(RegisterModel model) {
		Date curDate = new Date();

		// 设置初始化密码
		String initializedPassword = PasswordGenerateUtil.randomPassword();

		// 构造用户实体
		User user = new User(RandomUtil.genarateId(), model.getAccount(), DigestUtils.md5Hex(model.getPassword()),
				model.getName().trim(), model.getGender(), DefaultAvatarConstant.APPLICATION_DEFAULT_AVATAR,
				model.getEmail(), model.getPhone(), Integer.toString(UserTypeEnum.OTHEREMPLOYEES.getValue()), curDate,
				false, false);

		// 构造用户扩展信息实体
		UserExtension userExtension = new UserExtension();
		userExtension.setUserId(user.getId());
		userExtension.setInitializedPassword(initializedPassword);
		userExtension.setDepartment(model.getDepartment());

		// 设置用户角色
		Role role = roleDao.selectByRoleStr(RolesConstant.OTHER_EMPLOYEES_STR);
		List<UserRole> userRoles = new ArrayList<UserRole>();
		userRoles.add(new UserRole(user.getId(), role.getId()));

		// 设置用户权限
		List<String> permissionIds = rolePermissionDao.selectPermissionIdsByRoleId(role.getId());
		List<UserPermission> userPermissions = new ArrayList<UserPermission>();
		for (String permissionId : permissionIds) {
			UserPermission userPermission = new UserPermission(user.getId(), permissionId);
			userPermissions.add(userPermission);
		}

		// 将数据保存至数据库
		userDao.insertUser(user);
		userExtensionDao.insertUserExtension(userExtension);
		userRoleDao.insertUserRoles(userRoles);
		userPermissionDao.insertUserPermissions(userPermissions);
	}

}
