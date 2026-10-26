package frame.server.service.warehouse.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import frame.server.constant.UserTypeEnum;
import frame.server.dao.ApplicationDao;
import frame.server.dao.UserDao;
import frame.server.dao.UserWarehouseApplicationSettingDao;
import frame.server.dao.UserWarehouseDao;
import frame.server.dao.WarehouseDao;
import frame.server.domain.User;
import frame.server.domain.UserWarehouse;
import frame.server.domain.Warehouse;
import frame.server.dto.WarehouseDto;
import frame.server.dto.WarehouseUpdateDto;
import frame.server.service.warehouse.WWarehouseService;
import frame.server.utils.RandomUtil;
import frame.server.utils.StringUtil;
import frame.server.vo.Page;
import frame.server.vo.warehouse.WWarehouseController.AddWarehouseModel;
import frame.server.vo.warehouse.WWarehouseController.EditWarehouseModel;
import frame.server.vo.warehouse.WWarehouseController.UpdateAssignUserModel;

@Service("wWarehouseService")
public class WWarehouseServiceImpl implements WWarehouseService {

	@Autowired
	private ApplicationDao applicationDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private UserWarehouseApplicationSettingDao userWarehouseApplicationSettingDao;

	@Autowired
	private UserWarehouseDao userWarehouseDao;

	@Autowired
	private WarehouseDao warehouseDao;

	@Override
	public List<Warehouse> getAllWarehouses() {
		return warehouseDao.selectAll();
	}

	@Override
	public PageInfo<WarehouseDto> getWarehouses(Page page) {
		PageHelper.startPage(page.getPageNum(), page.getPageSize());
		PageInfo<WarehouseDto> pageInfo = null;
		JSONObject searchCondition = page.getSearchCondition();
		if (searchCondition != null) {
			String searchStr = searchCondition.getString("searchStr");
			if (!StringUtil.isBlank(searchStr)) {
				pageInfo = new PageInfo<>(warehouseDao.selectLikeName(searchStr));
			} else {
				pageInfo = new PageInfo<>(warehouseDao.selectAll1());
			}
		} else {
			pageInfo = new PageInfo<>(warehouseDao.selectAll1());
		}

		return pageInfo;
	}

	@Override
	public WarehouseDto getWarehouseByWarehouseId(String warehouseId) {
		return warehouseDao.selectWarehouseById1(warehouseId);
	}

	@Override
	public boolean isWarehouseNameRepeat(String warehouseName) {
		return warehouseDao.selectWarehouseCountByName(warehouseName) != 0;
	}

	@Override
	public boolean isWarehouseCodeRepeat(String warehouseCode) {
		return warehouseDao.selectWarehouseCountByCode(warehouseCode) != 0;
	}

	@Override
	public WarehouseDto addWarehouse(AddWarehouseModel model) {
		// 获取或生成排序数
		Integer displayOrder = warehouseDao.selectMaxDisplayOrder();
		if (displayOrder == null) {
			displayOrder = 1;
		} else {
			displayOrder += 1;
		}

		// 构造仓库数据
		Warehouse warehouse = new Warehouse(RandomUtil.genarateId(), model.getCode(), model.getName(),
				model.getPictureUrl(), model.getSummary(), new Date(), displayOrder, false, false);

		warehouseDao.insertWarehouse(warehouse);
		
		// 将新增仓库与系统管理员挂钩
		List<String> SystemAdministratorIds = userDao.selectSystemAdministratorsId(Integer.toString(UserTypeEnum.SYSTEMADMIN.getValue()));
		List<UserWarehouse> userWarehouses = new ArrayList<UserWarehouse>();
		for(String saId : SystemAdministratorIds) {
			userWarehouses.add(new UserWarehouse(saId, warehouse.getId(), new Date()));
		}
		userWarehouseDao.insertUserWarehouses(userWarehouses);

		return warehouseDao.selectWarehouseById1(warehouse.getId());
	}

	@Override
	public WarehouseDto updateWarehouse(EditWarehouseModel model) {
		WarehouseUpdateDto warehouse = new WarehouseUpdateDto(model.getId(), model.getCode(), model.getName(),
				model.getPictureUrl(), model.getSummary());
		
		warehouseDao.updateWarehouse(warehouse);

		return warehouseDao.selectWarehouseById1(warehouse.getId());
	}

	@Override
	public boolean isWarehouseCanDeleteOrDisable(String warehouseId) {
		// 检查该仓库有处于审核中、审核通过但未取货的申请
		return applicationDao.selectNotCompletedByWarehouseId(warehouseId) == 0;
	}

	@Override
	public void updateWarehouseState(String warehouseId) {
		warehouseDao.updateWarehouseState(warehouseId);
	}

	@Override
	public void deleteWarehouse(String warehouseId) {
		// 删除申请人员可申请仓库配置关联关系
		userWarehouseApplicationSettingDao.deleteByWarehouseId(warehouseId);
		
		// 假删除仓库
		warehouseDao.updateWarehouseDeleteState(warehouseId);
	}

	@Override
	public List<User> getAllWarehouseManagers() {
		List<User> users = userDao.selectAllWarehouseManager();
		for(User user : users) {
			user.setPassword("");
		}
		return users;
	}

	@Override
	public boolean checkWarehouseManagerIds(List<String> userIds) {
		if(userIds == null || userIds.isEmpty()) {
			return true;
		}
		List<String> dbUserIds = userDao.selectAllWarehouseManagerIds();
		for(String userId : userIds) {
			boolean isExist = false;
			for(String dbUserId : dbUserIds) {
				if(dbUserId.equals(userId)) {
					isExist = true;
					break;
				}
			}
			if(!isExist) {
				return false;
			}
		}
		return true;
	}

	@Override
	public WarehouseDto updateAssignUsers(UpdateAssignUserModel model) {
		// 执行全删全插操作
		userWarehouseDao.deleteByWarehouseIdAndUserTypeIsWarehouseManager(model.getWarehouseId());
		
		if(model.getUserIds() == null || model.getUserIds().isEmpty()) {
			return warehouseDao.selectWarehouseById1(model.getWarehouseId());
		}
		// 插入数据
		List<UserWarehouse> userWarehouses = new ArrayList<UserWarehouse>();
		for(String userId : model.getUserIds()) {
			userWarehouses.add(new UserWarehouse(userId, model.getWarehouseId(), new Date()));
		}
		userWarehouseDao.insertUserWarehouses(userWarehouses);

		return warehouseDao.selectWarehouseById1(model.getWarehouseId());
	}

}
