package frame.server.service.warehouse;

import org.springframework.transaction.annotation.Transactional;

import frame.server.dto.UserDto;
import frame.server.vo.warehouse.WUserInfoController.WEditUserInfoModel;

public interface WUserInfoService {

	/**
	 * 更新用户信息
	 * 
	 * @param model
	 * @return
	 */
	@Transactional
	UserDto updateUserInfo(WEditUserInfoModel model);
	
	/**
	 * 更新用户密码
	 * 
	 * @param userId
	 * @param newPassword
	 */
	void updateUserPassword(String userId, String newPassword);
	
}
