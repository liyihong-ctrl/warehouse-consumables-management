package frame.server.service.consumables;

import org.springframework.transaction.annotation.Transactional;

import frame.server.dto.UserDto;
import frame.server.vo.consumables.CUserInfoController.CEditUserInfoModel;

public interface CUserInfoService {
	
	/**
	 * 更新用户信息
	 * 
	 * @param model
	 * @return
	 */
	@Transactional
	UserDto updateUserInfo(CEditUserInfoModel model);

	/**
	 * 更新用户密码
	 * 
	 * @param userId
	 * @param newPassword
	 */
	@Transactional
	void updateUserPassword(String userId, String newPassword);

}
