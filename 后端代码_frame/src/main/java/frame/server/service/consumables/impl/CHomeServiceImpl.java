package frame.server.service.consumables.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import frame.server.dao.ApplicationDao;
import frame.server.dao.ConsumableDao;
import frame.server.dao.ReferenceInformationTemplateDao;
import frame.server.dao.RoleWarehouseDao;
import frame.server.dao.TypeWarehouseDao;
import frame.server.dao.UserWarehouseDao;
import frame.server.dto.ConsumableDto;
import frame.server.service.consumables.CHomeService;

@Service("cHomeService")
public class CHomeServiceImpl implements CHomeService {

	@Autowired
	private ApplicationDao applicationDao;

	@Autowired
	private ConsumableDao consumableDao;

	@Autowired
	private ReferenceInformationTemplateDao referenceInformationTemplateDao;

	@Autowired
	private RoleWarehouseDao roleWarehouseDao;

	@Autowired
	private TypeWarehouseDao typeWarehouseDao;

	@Autowired
	private UserWarehouseDao userWarehouseDao;

	@Override
	public int getRoleCount(String warehouseId) {
		return roleWarehouseDao.c_selectRoleCountByWarehouseId(warehouseId);
	}

	@Override
	public int getUserCount(String warehouseId) {
		return userWarehouseDao.c_selectUsersCountByWarehouseId(warehouseId);
	}

	@Override
	public int getTypeCount(String warehouseId) {
		return typeWarehouseDao.selectCountByWarehouseId(warehouseId);
	}

	@Override
	public int getTemplateCount(String warehouseId) {
		return referenceInformationTemplateDao.c_selectCountByWarehouseId(warehouseId);
	}

	@Override
	public int getConsumableCount(String warehouseId) {
		return consumableDao.c_selectCountByWarehouseId(warehouseId);
	}

	@Override
	public int getApplicationCount(String warehouseId) {
		return applicationDao.selectCountByWarehouseId(warehouseId);
	}

	@Override
	public List<ConsumableDto> getAlarmData(String warehouseId) {
		return consumableDao.c_selectAlarmByWarehouseId(warehouseId);
	}

}
