package frame.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import frame.server.dao.UserDao;
import frame.server.dao.UserWarehouseDao;
import frame.server.domain.UserWarehouse;
import frame.server.domain.Warehouse;
import frame.server.dto.UserDto;
import frame.server.service.BaseService;

@Service("baseService")
public class BaseServiceImpl implements BaseService {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private UserWarehouseDao userWarehouseDao;

	@Override
	public UserDto getUserDtoByAccount(String account) {
		return userDao.selectUserDtoByAccount(account);
	}

	@Override
	public Warehouse getWarehouseByUserIdAndWarehouseId(String userId, String warehouseId) {
		return userWarehouseDao.selectEnabledWarehouseByUserIdAndWarehouseId(new UserWarehouse(userId, warehouseId, null));
	}

}
