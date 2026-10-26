package frame.server.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import frame.server.domain.Permission;
import frame.server.domain.Warehouse;
import frame.server.dto.UserDto;
import frame.server.vo.LoginController.RegisterModel;

public interface LoginService {

	/**
	 * 根据用户账户获取用户信息
	 * 
	 * @param model
	 * @return
	 */
	UserDto getUserInfo(String account);
	
	/**
	 * 获取系统权限
	 * 
	 * @return
	 */
	List<Permission> getSystemPermission();
	
	/**
	 * 获取当前用户可操作的仓库数据
	 * 
	 * @param userId
	 * @return
	 */
	List<Warehouse> getWarehouses(String userId, boolean isSystemManager);
	
	/**
	 * 注册用户
	 * 
	 * @param model
	 */
	@Transactional
	void register(RegisterModel model);

}
