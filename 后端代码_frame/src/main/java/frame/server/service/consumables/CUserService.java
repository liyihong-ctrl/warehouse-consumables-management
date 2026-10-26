package frame.server.service.consumables;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

import frame.server.domain.Role;
import frame.server.domain.User;
import frame.server.dto.ApplicationUserDto;
import frame.server.dto.UserDto;
import frame.server.vo.ExcelDataModel;
import frame.server.vo.Page;
import frame.server.vo.consumables.CUserController.CAddUserModel;
import frame.server.vo.consumables.CUserController.CEditUserModel;
import frame.server.vo.consumables.CUserController.CImportUserDetailModel;

public interface CUserService {
	
	/**
	 * 获取全部用户
	 * 
	 * @return
	 */
	PageInfo<User> getAllUsers();
	
	/**
	 * 获取全部用户(分页)
	 * 
	 * @return
	 */
	PageInfo<UserDto> getUsers(Page page);
	
	/**
	 * 获取申请用户信息(分页)
	 * 
	 * @param page
	 * @return
	 */
	PageInfo<ApplicationUserDto> getApplicationUsers(Page page);
	
	/**
	 * 获取全部角色
	 * 
	 * @return
	 */
	List<Role> getAllRoles(String warehouseId);
	
	/**
	 * 校验仓库ID
	 * 1. 是否存在
	 * 2. 当前用户是否可用
	 * 
	 * @param warehouseId
	 * @return
	 */
	boolean validateWarehouseId(String warehouseId, String userId);
	
	/**
	 * 校验角色Ids是否存在于数据库
	 * 
	 * @param roleIds
	 * @return
	 */
	boolean validateRoleIds(String warehouseId, List<String> roleIds);
	
	/**
	 * 查询账号是否重复
	 * 
	 * @param account
	 * @return
	 */
	boolean isAccountRepeat(String account);
	
	/**
	 * 新增用户
	 * 
	 * @param model
	 * @return
	 */
	@Transactional
	UserDto addUser(CAddUserModel model);
	
	/**
	 * 根据用户ID查询用户
	 * 
	 * @param userId
	 * @return
	 */
	User getUserByUserId(String userId);
	
	/**
	 * 根据用户ID查询可申请用户
	 * 
	 * @param userId
	 * @return
	 */
	User getApplicationUserByUserId(String userId);
	
	/**
	 * 更新用户
	 * 
	 * @param model
	 * @return
	 */
	@Transactional
	UserDto updateUser(CEditUserModel model);
	
	/**
	 * 更新状态
	 * 
	 * @param model
	 * @return
	 */
	@Transactional
	void updateUserState(String userId);
	
	/**
	 * 删除用户
	 * 
	 * @param UserId
	 */
	@Transactional
	void deleteUser(String userId);
	
	/**
	 * 重置用户密码
	 * 
	 * @param userId
	 */
	@Transactional
	void resetUserPassword(String userId);

	/**
	 * 导入用户：获取样本文件
	 * 
	 * @param warehouseId
	 * @return
	 */
	ExcelDataModel getImportTemplateData(String warehouseId);
	
	/**
	 * 导入角色：检查字段
	 * 
	 * @param importRoles
	 * @return
	 */
	JSONObject checkImportUsers(String warehouseId, List<CImportUserDetailModel> importUsers);
	
	/**
	 * 导入用户
	 * 
	 * 账号存在则更新、账号不存在则添加
	 * 
	 * @param model
	 * @return
	 */
	@Transactional
	List<UserDto> importUsers(String warehouseId, List<CImportUserDetailModel> importUsers);
	
	/**
	 * 导出用户
	 * 
	 * @param warehouseId
	 * @return
	 */
	ExcelDataModel getExportUsersData(String warehouseId);
	
	/**
	 * 更新用户可申请仓库
	 * 
	 * @param warehouseId
	 * @param userId
	 */
	boolean updateUserApplicationState(String warehouseId, String userId);
}
