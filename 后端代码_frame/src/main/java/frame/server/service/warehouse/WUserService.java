package frame.server.service.warehouse;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageInfo;

import frame.server.domain.User;
import frame.server.domain.Warehouse;
import frame.server.dto.UserDto;
import frame.server.vo.Page;
import frame.server.vo.warehouse.WUserController.AddAdministratorModel;
import frame.server.vo.warehouse.WUserController.EditAdministratorModel;
import frame.server.vo.warehouse.WUserController.UpdateAssignWarehouseModel;

public interface WUserService {
	
	/**
	 * 获取全部的用户
	 * 
	 * @return
	 */
	List<User> getAllUsers();
	
	/**
	 * 获取用户信息(分页)
	 * 
	 * @param page
	 * @return
	 */
	PageInfo<UserDto> getUsers(Page page);
	
	/**
	 * 根据用户ID查询用户
	 * 
	 * @param userId
	 * @return
	 */
	User getUserByUserId(String userId);
	
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
	UserDto addUser(AddAdministratorModel model);
	
	/**
	 * 更新用户信息
	 * 
	 * @param model
	 * @return
	 */
	@Transactional
	UserDto updateUser(EditAdministratorModel model);
	
	/**
	 * 更新用户状态
	 * 
	 * @param userId
	 * @return 返回更新状态
	 */
	@Transactional
	void updateAdministratorState(String userId);
	
	/**
	 * 检查申请人员是否有未完成的申请
	 * 
	 * @return
	 */
	boolean checkUserHasApplication(String userId);
	
	/**
	 * 删除用户
	 * 
	 * @param userId
	 */
	@Transactional
	void deleteAdministrator(String userId);
	
	/**
	 * 重置用户密码
	 * 
	 * @param userId
	 */
	void updateAdministratorPassword(String userId, boolean isRootAdmin);
	
	/**
	 * 获取用户分配仓库的初始化数据
	 * 
	 * @param userId
	 * @return
	 */
	Map<String, List<Warehouse>> getAssignedWarehouses(String userId);
	
	/**
	 * 校验仓库IDs是否全部合格
	 * 
	 * @param warehouseIds
	 * @return
	 */
	boolean checkWarehouseIds(List<String> warehouseIds);

	/**
	 * 更新仓库管理仓库分配信息
	 * 
	 * @param model
	 * @return
	 */
	@Transactional
	List<Warehouse> assignWarehouse(UpdateAssignWarehouseModel model);
}
