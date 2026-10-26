package frame.server.service.warehouse.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import frame.server.dao.UserDao;
import frame.server.domain.User;
import frame.server.dto.UserDto;
import frame.server.service.warehouse.WUserInfoService;
import frame.server.vo.warehouse.WUserInfoController.WEditUserInfoModel;

@Service("wUserInfoService")
public class WUserInfoServiceImpl implements WUserInfoService {
	
	@Autowired
	private UserDao userDao;

	@Override
	public UserDto updateUserInfo(WEditUserInfoModel model) {
		userDao.w_updateUserInfoBySelf(model);
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
