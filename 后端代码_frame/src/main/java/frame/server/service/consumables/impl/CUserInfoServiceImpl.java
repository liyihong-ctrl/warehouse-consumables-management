package frame.server.service.consumables.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import frame.server.dao.UserDao;
import frame.server.domain.User;
import frame.server.dto.UserDto;
import frame.server.service.consumables.CUserInfoService;
import frame.server.vo.consumables.CUserInfoController.CEditUserInfoModel;

@Service("cUserInfoService")
public class CUserInfoServiceImpl implements CUserInfoService {
	
	@Autowired
	private UserDao userDao;

	@Override
	public UserDto updateUserInfo(CEditUserInfoModel model) {
		userDao.c_updateInfoBySelf(model);
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
