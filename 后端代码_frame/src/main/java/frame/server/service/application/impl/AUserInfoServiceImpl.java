package frame.server.service.application.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import frame.server.dao.UserDao;
import frame.server.dao.UserExtensionDao;
import frame.server.domain.User;
import frame.server.domain.UserExtension;
import frame.server.dto.UserDto;
import frame.server.service.application.AUserInfoService;
import frame.server.vo.application.AUserInfoController.AEditUserInfoModel;

@Service("aUserInfoService")
public class AUserInfoServiceImpl implements AUserInfoService {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private UserExtensionDao userExtensionDao;

	@Override
	public UserDto updateUserInfo(AEditUserInfoModel model) {
		// 更新用户信息
		userDao.a_updateUserInfoBySelf(model);
		
		// 更新用户部门信息
		UserExtension userExt = new UserExtension();
		userExt.setUserId(model.getId());
		userExt.setDepartment(model.getDepartment());
		userExtensionDao.a_updateUserDepartment(userExt);
		
		return userDao.selectUserDtoByUserId(model.getId());
	}

	@Override
	public void updateUserPassword(String userId, String newPassword) {
		User user = new User();
		user.setId(userId);
		user.setPassword(DigestUtils.md5Hex(newPassword));
		userDao.updateUserPassword(user);
	}

}
