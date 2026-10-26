package frame.server.service.warehouse.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import frame.server.dao.UserDao;
import frame.server.dao.WarehouseDao;
import frame.server.service.warehouse.WHomeService;

@Service("wHomeService")
public class WHomeServiceImpl implements WHomeService {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private WarehouseDao warehouseDao;

	@Override
	public int getUserCount() {
		return userDao.selectSystemAdminAndWarehouseManagerCount();
	}

	@Override
	public int getWarehouseCount() {
		return warehouseDao.selectWarehousesCount();
	}

}
