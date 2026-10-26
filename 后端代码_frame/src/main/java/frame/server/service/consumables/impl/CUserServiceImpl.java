package frame.server.service.consumables.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import frame.server.constant.DefaultAvatarConstant;
import frame.server.constant.GenderConstant;
import frame.server.constant.UserTypeEnum;
import frame.server.dao.RolePermissionDao;
import frame.server.dao.RoleWarehouseDao;
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
import frame.server.domain.UserWarehouseApplicationSetting;
import frame.server.domain.Warehouse;
import frame.server.dto.ApplicationUserDto;
import frame.server.dto.UserDto;
import frame.server.dto.UserInfoDto;
import frame.server.service.consumables.CUserService;
import frame.server.utils.PasswordGenerateUtil;
import frame.server.utils.RandomUtil;
import frame.server.utils.StringUtil;
import frame.server.utils.ValidatorUtil;
import frame.server.vo.ExcelDataModel;
import frame.server.vo.Page;
import frame.server.vo.consumables.CUserController.CAddUserModel;
import frame.server.vo.consumables.CUserController.CEditUserModel;
import frame.server.vo.consumables.CUserController.CImportUserDetailModel;
import frame.server.vo.consumables.CUserController.GetUsersSelectModel;

@Service("cuserService")
public class CUserServiceImpl implements CUserService {

	@Autowired
	private RolePermissionDao rolePermissionDao;

	@Autowired
	private RoleWarehouseDao roleWarehouseDao;

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
	public PageInfo<User> getAllUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PageInfo<UserDto> getUsers(Page page) {
		// 开启分页
		PageHelper.startPage(page.getPageNum(), page.getPageSize());
		
		// 结果集
		PageInfo<UserDto> pageInfo = null;
		
		// 解析检索条件
		JSONObject searchCondition = page.getSearchCondition();
		String warehouseId = searchCondition.getString("warehouseId");
		String searchType = searchCondition.getString("searchType");
		String searchStr = searchCondition.getString("searchStr");

		if (!StringUtil.isBlank(searchStr)) {
			GetUsersSelectModel model = new GetUsersSelectModel(warehouseId, searchStr);
			switch (searchType) {
			// 姓名
			case "1":
				pageInfo = new PageInfo<>(userDao.c_selectUserDtoLikeName(model));
				break;
			// 账号
			case "2":
				pageInfo = new PageInfo<>(userDao.c_selectUserDtoLikeAccount(model));
				break;
			// 角色
			case "3":
				pageInfo = new PageInfo<>(userDao.c_selectUserDtoByRoleName(model));
				break;
			}
		} else {
			pageInfo = new PageInfo<>(userDao.c_selectUserDtoByWarehouseId(warehouseId));
		}

		// 移除password
		for (UserDto user : pageInfo.getList()) {
			user.setPassword("");
		}

		return pageInfo;
	}


	@Override
	public PageInfo<ApplicationUserDto> getApplicationUsers(Page page) {// 开启分页
		PageHelper.startPage(page.getPageNum(), page.getPageSize());
		
		// 结果集
		PageInfo<ApplicationUserDto> pageInfo = null;
		
		// 解析检索条件
		JSONObject searchCondition = page.getSearchCondition();
		String warehouseId = searchCondition.getString("warehouseId");
		String searchType = searchCondition.getString("searchType");
		String searchStr = searchCondition.getString("searchStr");

		if (!StringUtil.isBlank(searchStr)) {
			GetUsersSelectModel model = new GetUsersSelectModel(warehouseId, searchStr);
			switch (searchType) {
			// 姓名
			case "1":
				pageInfo = new PageInfo<>(userDao.c_selectApplicationUserDtoLikeName(model));
				break;
			// 账号
			case "2":
				pageInfo = new PageInfo<>(userDao.c_selectApplicationUserDtoLikeAccount(model));
				break;
			}
		} else {
			pageInfo = new PageInfo<>(userDao.c_selectApplicationUserDtoByWarehouseId());
		}

		for (ApplicationUserDto user : pageInfo.getList()) {
			// 移除password
			user.setPassword("");
			user.getExtension().setInitializedPassword("");
			
			// 移除其他可申请仓库ID
			if(user.getCanApplicationWarehouseIds() != null && !user.getCanApplicationWarehouseIds().isEmpty()) {
				List<String> warehouseIds = new ArrayList<String>();
				for(String canApplicationWarehouseId: user.getCanApplicationWarehouseIds()) {
					if(canApplicationWarehouseId.equals(warehouseId)) {
						warehouseIds.add(warehouseId);
						break;
					}
				}
				user.setCanApplicationWarehouseIds(warehouseIds);
				user.setCanApplication(!user.getCanApplicationWarehouseIds().isEmpty());
			}
		}

		return pageInfo;
	}
	
	@Override
	public List<Role> getAllRoles(String warehouseId) {
		return roleWarehouseDao.c_selectRolesByWarehouseId(warehouseId);
	}

	@Override
	public boolean validateWarehouseId(String warehouseId, String userId) {
		return userWarehouseDao
				.selectEnabledCountByUserIdAndWarehouseId(new UserWarehouse(userId, warehouseId, null)) != 0;
	}

	@Override
	public boolean validateRoleIds(String warehouseId, List<String> roleIds) {
		List<String> dbRoleIds = roleWarehouseDao.c_selectRoleIdsByWarehouseId(warehouseId);

		for (String roleId : roleIds) {
			boolean isExist = false;
			for (String dbRoleId : dbRoleIds) {
				if (roleId.equals(dbRoleId)) {
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
	public boolean isAccountRepeat(String account) {
		return userDao.selectUserCountByAccount(account) != 0;
	}

	@Override
	public UserDto addUser(CAddUserModel model) {
		Date curDate = new Date();

		// 设置初始化密码
		String initializedPassword = PasswordGenerateUtil.randomPassword();

		// 设置用户头像
		String avatar = StringUtil.isBlank(model.getAvatar()) ? DefaultAvatarConstant.CONSUMABLES_DEFAULT_AVATAR
				: model.getAvatar();

		// 设置用户类型
		String userType = Integer.toString(UserTypeEnum.GENERALEMPLOYEE.getValue());

		// 初始化用户信息
		User user = new User(RandomUtil.genarateId(), model.getAccount(), DigestUtils.md5Hex(initializedPassword),
				model.getName(), model.getGender(), avatar, model.getEmail(), model.getPhone(), userType, curDate,
				false, false);

		// 设置用户扩展信息
		UserExtension userExtension = new UserExtension();
		userExtension.setUserId(user.getId());
		userExtension.setInitializedPassword(initializedPassword);

		// 设置用户角色
		List<UserRole> userRoles = new ArrayList<UserRole>();
		for (String roleId : model.getRoleIds()) {
			userRoles.add(new UserRole(user.getId(), roleId));
		}

		// 设置用户权限
		List<String> premissionIds = new ArrayList<String>();
		for (String roleId : model.getRoleIds()) {
			premissionIds.addAll(rolePermissionDao.selectPermissionIdsByRoleId(roleId));
		}
		List<UserPermission> userPermissions = new ArrayList<UserPermission>();
		for (String permissionId : premissionIds) {
			userPermissions.add(new UserPermission(user.getId(), permissionId));
		}
		HashSet<UserPermission> hashSet = new HashSet<UserPermission>(userPermissions);
		userPermissions.clear();
		userPermissions.addAll(hashSet);

		// 设置用户与仓库绑定关系
		UserWarehouse userWarehouse = new UserWarehouse(user.getId(), model.getWarehouseId(), curDate);

		// 将数据保存至数据库
		userDao.insertUser(user);
		userExtensionDao.insertUserExtension(userExtension);
		userRoleDao.insertUserRoles(userRoles);
		userPermissionDao.insertUserPermissions(userPermissions);
		userWarehouseDao.insertUserWarehouse(userWarehouse);

		UserDto userNew = userDao.selectUserDtoByUserId(user.getId());
		userNew.setPassword("");

		return userNew;
	}

	@Override
	public User getUserByUserId(String userId) {
		return userDao.c_selectUserById(userId);
	}

	@Override
	public User getApplicationUserByUserId(String userId) {
		return userDao.c_selectApplicationUserById(userId);
	}

	@Override
	public UserDto updateUser(CEditUserModel model) {
		// 构造用户信息实体
		UserInfoDto user = new UserInfoDto(model.getId(), model.getName(), model.getGender(), model.getAvatar(),
				model.getEmail(), model.getPhone(), Integer.toString(UserTypeEnum.GENERALEMPLOYEE.getValue()));

		// 配置新角色
		List<UserRole> userRoles = new ArrayList<UserRole>();
		for (String roleId : model.getRoleIds()) {
			userRoles.add(new UserRole(user.getId(), roleId));
		}

		// 配置新权限
		List<String> premissionIds = new ArrayList<String>();
		for (String roleId : model.getRoleIds()) {
			premissionIds.addAll(rolePermissionDao.selectPermissionIdsByRoleId(roleId));
		}
		List<UserPermission> userPermissions = new ArrayList<UserPermission>();
		for (String permissionId : premissionIds) {
			userPermissions.add(new UserPermission(user.getId(), permissionId));
		}
		HashSet<UserPermission> hashSet = new HashSet<UserPermission>(userPermissions);
		userPermissions.clear();
		userPermissions.addAll(hashSet);

		// 更新用户信息
		userDao.updateUserInfo(user);

		// 清除旧角色和旧权限
		userRoleDao.deleteByUserId(user.getId());
		userPermissionDao.deleteByUserId(user.getId());

		// 设置新角色和新权限
		userRoleDao.insertUserRoles(userRoles);
		userPermissionDao.insertUserPermissions(userPermissions);

		UserDto userNew = userDao.selectUserDtoByUserId(model.getId());
		userNew.setPassword("");

		return userNew;
	}

	@Override
	public void updateUserState(String userId) {
		userDao.updateUserState(userId);
	}

	@Override
	public void deleteUser(String userId) {
		// 同步删除用户角色关联关系
		userRoleDao.deleteByUserId(userId);

		// 同步删除用户权限关联关系
		userPermissionDao.deleteByUserId(userId);

		// 同步删除用户仓库关联关系
		userWarehouseDao.deleteByUserId(userId);

		// 假删除用户
		userDao.updateUserDeleteState(userId);
	}

	@Override
	public void resetUserPassword(String userId) {
		User user = new User();
		user.setId(userId);
		UserExtension userExtension = userExtensionDao.selectByUserId(userId);
		user.setPassword(DigestUtils.md5Hex(userExtension.getInitializedPassword()));
		userDao.updateUserPassword(user);
	}

	@Override
	public ExcelDataModel getImportTemplateData(String warehouseId) {
		// 获取仓库数据
		Warehouse warehouse = warehouseDao.selectWarehouseById(warehouseId);

		ExcelDataModel excelData = new ExcelDataModel();

		// 设置文件名
		Date now = new Date();
		String fileName = warehouse.getName() + "_批量导入用户_样本_" + now.getTime();
		excelData.setName(fileName);

		// 设置表头
		List<String> heads = new ArrayList<String>();

		heads.add("姓名");
		heads.add("账号");
		heads.add("性别");
		heads.add("邮箱");
		heads.add("电话");
		heads.add("角色");
		heads.add("是否启用");

		excelData.setTitles(heads);

		// 设置数据
		List<List<Object>> templateDatas = new ArrayList<>();

		List<Object> templateData = new ArrayList<Object>();

		templateData.add("张三");
		templateData.add("zhangsan@163.com");
		templateData.add("男");
		templateData.add("zhangsan@163.com");
		templateData.add("13012345678");
		templateData.add("类型管理员、消耗品管理员");
		templateData.add("否");

		templateDatas.add(templateData);

		excelData.setRows(templateDatas);

		return excelData;
	}

	@Override
	public JSONObject checkImportUsers(String warehouseId, List<CImportUserDetailModel> importUsers) {
		JSONObject errorDatas = new JSONObject();

		// 获取仓库所有角色
		List<Role> dbRoles = roleWarehouseDao.c_selectRolesByWarehouseId(warehouseId);

		// 检查数据
		for (int i = 0; i < importUsers.size(); i++) {
			JSONObject errorData = new JSONObject();

			// 检查姓名
			if (StringUtil.isBlank(importUsers.get(i).getName())) {
				errorData.put("name", "姓名不能为空");
			} else {
				String name = importUsers.get(i).getName().trim();
				if (name.length() > 20) {
					errorData.put("name", "姓名长度范围为1~20");
				}
			}

			// 检查账号
			if (StringUtil.isBlank(importUsers.get(i).getAccount())) {
				errorData.put("account", "账号不能为空");
			} else {
				String account = importUsers.get(i).getAccount().trim();
				if (!(ValidatorUtil.isMobile(account) || ValidatorUtil.isEmail(account))) {
					errorData.put("account", "账号格式错误");
				} else {
					for (int j = 0; j < importUsers.size(); j++) {
						if (i != j) {
							if (!StringUtil.isBlank(importUsers.get(j).getAccount())) {
								if (account.equals(importUsers.get(j).getAccount())) {
									errorData.put("account", "账号重复");
								}
							}
						}
					}
				}
			}

			// 检查性别
			if (StringUtil.isBlank(importUsers.get(i).getGender())) {
				errorData.put("gender", "性别不能为空");
			} else {
				String gender = importUsers.get(i).getGender().trim();
				if (!(gender.equals(GenderConstant.MALE.getName()) || gender.equals(GenderConstant.FEMALE.getName()))) {
					errorData.put("gender", "性别格式错误");
				}
			}

			// 检查邮箱
			if (!StringUtil.isBlank(importUsers.get(i).getEmail())) {
				String email = importUsers.get(i).getEmail().trim();
				if (!ValidatorUtil.isEmail(email)) {
					errorData.put("email", "邮箱格式错误");
				}
			}

			// 检查电话
			if (!StringUtil.isBlank(importUsers.get(i).getPhone())) {
				String phone = importUsers.get(i).getPhone().trim();
				if (!ValidatorUtil.isMobile(phone)) {
					errorData.put("phone", "电话格式错误");
				}
			}

			// 检查角色
			if (StringUtil.isBlank(importUsers.get(i).getRoleNames())) {
				errorData.put("roleNames", "角色不能为空");
			} else {
				if (dbRoles == null) {
					errorData.put("roleNames", "角色名不存在");
				}

				String roleNames = importUsers.get(i).getRoleNames().trim();
				String[] names = roleNames.split("、");
				// 判断角色名称是否存在
				for (int n = 0; n < names.length; n++) {
					boolean isNameExist = false;
					for (Role dbRole : dbRoles) {
						if (names[n].equals(dbRole.getName())) {
							isNameExist = true;
							break;
						}
					}
					if (!isNameExist) {
						errorData.put("roleNames", "角色名不存在");
						break;
					}
				}
			}

			// 检查是否启用
			if (StringUtil.isBlank(importUsers.get(i).getIsEnable())) {
				errorData.put("isEnable", "是否启用不能为空");
			} else {
				String isEnable = importUsers.get(i).getIsEnable().trim();
				if (!(isEnable.equals("是") || isEnable.equals("否"))) {
					errorData.put("isEnable", "是否启用格式错误");
				}
			}

			if (!errorData.isEmpty()) {
				errorDatas.put(Integer.toString(i), errorData);
			}
		}

		if (errorDatas.size() > 0) {
			return errorDatas;
		}

		return null;
	}

	@Override
	public List<UserDto> importUsers(String warehouseId, List<CImportUserDetailModel> importUsers) {
		// 获取仓库所有用户
		List<UserDto> dbUsers = userDao.c_selectUserDtoByWarehouseId(warehouseId);

		// 获取仓库所有角色
		List<Role> dbRoles = roleWarehouseDao.c_selectRolesByWarehouseId(warehouseId);

		// 需要添加的用户
		List<CAddUserModel> addUsers = new ArrayList<CAddUserModel>();

		// 需要更新信息的用户
		List<CEditUserModel> updateUsers = new ArrayList<CEditUserModel>();
		
		// 需要更新启用状态的用户ID
		List<String> updateUserStateIds = new ArrayList<String>();

		// 结果集
		List<UserDto> res = new ArrayList<UserDto>();

		for (CImportUserDetailModel importUser : importUsers) {
			boolean isAdd = true;
			String dbUserId = "";
			String dbAvatar = "";

			for (UserDto dbUser : dbUsers) {
				if (importUser.getAccount().equals(dbUser.getAccount())) {
					dbUserId = dbUser.getId();
					dbAvatar = dbUser.getAvatar();
					
					String dbUserIsEnable = dbUser.getIsEnable() ? "是" :"否";
					if(!importUser.getIsEnable().trim().equals(dbUserIsEnable)) {
						updateUserStateIds.add(dbUserId);
					}
					
					isAdd = false;
					break;
				}
			}

			if (isAdd) {
				CAddUserModel addUser = new CAddUserModel();
				addUser.setWarehouseId(warehouseId);
				addUser.setAccount(importUser.getAccount().trim());
				addUser.setName(importUser.getName().trim());
				addUser.setGender(importUser.getGender().trim());

				if (!StringUtil.isBlank(importUser.getEmail())) {
					addUser.setEmail(importUser.getEmail().trim());
				}

				if (!StringUtil.isBlank(importUser.getPhone())) {
					addUser.setPhone(importUser.getPhone().trim());
				}

				String roleNames = importUser.getRoleNames().trim();
				String[] names = roleNames.split("、");
				List<String> roleIds = new ArrayList<String>();
				// 判断角色名称是否存在
				for (int n = 0; n < names.length; n++) {
					for (Role dbRole : dbRoles) {
						if(names[n].equals(dbRole.getName())) {
							roleIds.add(dbRole.getId());
							break;
						}
					}
				}
				
				addUser.setRoleIds(roleIds);
				
				addUsers.add(addUser);
			} else {
				CEditUserModel updateUser = new CEditUserModel();
				updateUser.setId(dbUserId);
				updateUser.setWarehouseId(warehouseId);
				updateUser.setAccount(importUser.getAccount().trim());
				updateUser.setName(importUser.getName().trim());
				updateUser.setGender(importUser.getGender().trim());
				updateUser.setAvatar(dbAvatar);

				if (!StringUtil.isBlank(importUser.getEmail())) {
					updateUser.setEmail(importUser.getEmail().trim());
				}

				if (!StringUtil.isBlank(importUser.getPhone())) {
					updateUser.setPhone(importUser.getPhone().trim());
				}

				String roleNames = importUser.getRoleNames().trim();
				String[] names = roleNames.split("、");
				List<String> roleIds = new ArrayList<String>();
				// 判断角色名称是否存在
				for (int n = 0; n < names.length; n++) {
					for (Role dbRole : dbRoles) {
						if(names[n].equals(dbRole.getName())) {
							roleIds.add(dbRole.getId());
							break;
						}
					}
				}
				
				updateUser.setRoleIds(roleIds);
				
				updateUsers.add(updateUser);
			}
		}
		
		if(!addUsers.isEmpty()) {
			for(CAddUserModel addUser : addUsers) {
				UserDto user = addUser(addUser);
				
				for (CImportUserDetailModel importUser : importUsers) {
					if(importUser.getAccount().trim().equals(user.getAccount())) {
						String dbUserIsEnable = user.getIsEnable() ? "是" :"否";
						if(!importUser.getIsEnable().trim().equals(dbUserIsEnable)) {
							updateUserStateIds.add(user.getId());
						}
					}
				}
				
				res.add(user);
			}
		}
		
		if(!updateUsers.isEmpty()) {
			for(CEditUserModel updateUser : updateUsers) {
				updateUser(updateUser);
			}
		}
		
		// 统一更新用户启用状态
		for(String userId : updateUserStateIds) {
			userDao.updateUserState(userId);
		}

		return res;
	}

	@Override
	public ExcelDataModel getExportUsersData(String warehouseId) {
		// 获取仓库数据
		Warehouse warehouse = warehouseDao.selectWarehouseById(warehouseId);

		ExcelDataModel excelData = new ExcelDataModel();

		// 设置文件名
		Date now = new Date();
		String fileName = warehouse.getName() + "_用户_" + now.getTime();
		excelData.setName(fileName);

		// 设置表头
		List<String> heads = new ArrayList<String>();

		heads.add("姓名");
		heads.add("账号");
		heads.add("性别");
		heads.add("邮箱");
		heads.add("电话");
		heads.add("角色");
		heads.add("是否启用");

		excelData.setTitles(heads);

		// 设置数据
		List<List<Object>> templateDatas = new ArrayList<>();

		// 获取全部用户
		List<UserDto> users = userDao.c_selectUserDtoByWarehouseId(warehouseId);

		if (users != null) {
			for (UserDto user : users) {
				List<Object> templateData = new ArrayList<Object>();

				templateData.add(user.getName());
				templateData.add(user.getAccount());
				templateData.add(user.getGender());
				templateData.add(user.getEmail());
				templateData.add(user.getPhone());

				// 设置角色
				String roleNames = "";
				for (int i = 0; i < user.getRoles().size(); i++) {
					roleNames += user.getRoles().get(i).getName();
					if (i < user.getRoles().size() - 1) {
						roleNames += "、";
					}
				}
				templateData.add(roleNames);

				templateData.add(user.getIsEnable() ? "是" : "否");

				templateDatas.add(templateData);
			}
		}

		excelData.setRows(templateDatas);

		return excelData;
	}

	@Override
	public boolean updateUserApplicationState(String warehouseId, String userId) {
		UserWarehouseApplicationSetting setting = new UserWarehouseApplicationSetting(userId, warehouseId);
		if(userWarehouseApplicationSettingDao.selectCountByUserIdAndWarehouseId(setting) > 0) {
			userWarehouseApplicationSettingDao.deleteByUserIdAndWarehouseId(setting);
			return false;
		} else {
			userWarehouseApplicationSettingDao.insertApplication(setting);
			return true;
		}
	}

}
