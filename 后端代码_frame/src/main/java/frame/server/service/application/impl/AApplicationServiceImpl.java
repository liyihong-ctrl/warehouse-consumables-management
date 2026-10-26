package frame.server.service.application.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import frame.server.constant.ApprovalStatusConstant;
import frame.server.constant.UserTypeEnum;
import frame.server.dao.ApplicationDao;
import frame.server.dao.ApplicationDetailDao;
import frame.server.dao.ConsumableDao;
import frame.server.dao.UserDao;
import frame.server.dao.UserWarehouseApplicationSettingDao;
import frame.server.dao.WarehouseDao;
import frame.server.domain.Application;
import frame.server.domain.ApplicationDetail;
import frame.server.domain.Warehouse;
import frame.server.dto.ApplicationDto;
import frame.server.dto.ConsumableDto;
import frame.server.dto.ConsumableInfoDto;
import frame.server.service.application.AApplicationService;
import frame.server.utils.RandomUtil;
import frame.server.utils.StringUtil;
import frame.server.vo.Page;
import frame.server.vo.application.AApplicationController.AAddApplicationModel;
import frame.server.vo.application.AApplicationController.AddApplicationDetailModel;
import frame.server.vo.consumables.CSearchConditionModel;

@Service("aApplicationService")
public class AApplicationServiceImpl implements AApplicationService {

	@Autowired
	private ApplicationDao applicationDao;

	@Autowired
	private ApplicationDetailDao applicationDetailDao;

	@Autowired
	private ConsumableDao consumableDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private UserWarehouseApplicationSettingDao userWarehouseApplicationSettingDao;

	@Autowired
	private WarehouseDao warehouseDao;

	@Override
	public PageInfo<ApplicationDto> getApplications(String userId, Page page) {
		// 开启分页
		PageHelper.startPage(page.getPageNum(), page.getPageSize());

		JSONObject searchCondition = page.getSearchCondition();
		String searchStr = searchCondition.getString("searchStr");

		PageInfo<ApplicationDto> pageInfo = null;

		// 检索条件为空则全量检索
		if (StringUtil.isBlank(searchStr)) {
			pageInfo = new PageInfo<ApplicationDto>(applicationDao.a_selectByUserId(userId));
		}
		// 否则根据检索条件检索
		else {
			Application application = new Application();
			application.setUserId(userId);
			application.setName(searchStr);

			pageInfo = new PageInfo<ApplicationDto>(applicationDao.a_selectByUserIdAndLikeName(application));
		}

		return pageInfo;
	}

	@Override
	public ApplicationDto getApplicationDetailById(String applicationId) {
		return applicationDao.a_selectApplicationDtoById(applicationId);
	}

	@Override
	public boolean canApplication(String userId, String userType) {
		if (userType.equals(Integer.toString(UserTypeEnum.OTHEREMPLOYEES.getValue()))) {
			List<String> warehouseIds = userWarehouseApplicationSettingDao.selectWarehouseIdByUserId(userId);
			if (warehouseIds != null && !warehouseIds.isEmpty()) {
				return true;
			}
		} else {
			return warehouseDao.selectEnabledWarehousesCount() > 0;
		}
		return false;
	}

	@Override
	public List<Warehouse> getWarehouses(String userId, String userType) {
		if (userType.equals(Integer.toString(UserTypeEnum.OTHEREMPLOYEES.getValue()))) {
			return warehouseDao.a_selectCanApplicationWarehouses(userId);
		} else {
			return warehouseDao.selectEnabledAll();
		}
	}

	@Override
	public PageInfo<ConsumableDto> getConsumables(Page page) {
		// 开启分页
		PageHelper.startPage(page.getPageNum(), page.getPageSize());

		// 结果集
		PageInfo<ConsumableDto> pageInfo = null;

		// 获取检索条件
		JSONObject searchCondition = page.getSearchCondition();
		String warehouseId = searchCondition.getString("warehouseId");
		String searchType = searchCondition.getString("searchType");
		String searchStr = searchCondition.getString("searchStr");

		// 构造检索实体
		CSearchConditionModel model = new CSearchConditionModel();
		model.setWarehouseId(searchCondition.getString("warehouseId"));
		model.setSearchStr(searchCondition.getString("searchStr"));

		if (StringUtil.isBlank(searchStr)) {
			pageInfo = new PageInfo<ConsumableDto>(consumableDao.a_selectByWarehouseId(warehouseId));
		} else {
			switch(searchType) {
			// Code
			case "1":
				pageInfo = new PageInfo<ConsumableDto>(consumableDao.a_selectByWarehouseIdAndCode(model));
				break;
			// 名称
			case "2":
				pageInfo = new PageInfo<ConsumableDto>(consumableDao.a_selectByWarehouseIdAndLikeName(model));
				break;
			}
		}

		return pageInfo;
	}

	@Override
	public boolean isWarehouseExist(String warehouseId) {
		return warehouseDao.a_selectWarehouseById(warehouseId) > 0;
	}

	@Override
	public boolean checkConsumables(AAddApplicationModel model) {
		// 获取消耗品数据
		List<AddApplicationDetailModel> consumables = JSONObject
				.parseArray(model.getConsumables().getString("consumables"), AddApplicationDetailModel.class);

		// 消耗品为空
		if (consumables == null || consumables.isEmpty()) {
			return false;
		}

		for (AddApplicationDetailModel consumable : consumables) {
			ConsumableInfoDto consumableInfo = new ConsumableInfoDto();

			consumableInfo.setWarehouseId(model.getWarehouseId());
			consumableInfo.setId(consumable.getId());

			ConsumableDto dbConsumable = consumableDao.a_selectByWarehouseIdAndConsumableId(consumableInfo);

			// 判断消耗品是否存在
			if (dbConsumable == null) {
				return false;
			}

			// 判断消耗品申请数是否大于库存数
			if (dbConsumable.getCount() < consumable.getApplicationCount()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ApplicationDto addApplication(String userId, AAddApplicationModel model) {
		Date now = new Date();

		// 构造消耗品申请实体
		Application application = new Application(RandomUtil.genarateId(), userId, model.getWarehouseId(),
				model.getName(), model.getRemarks(), now, false, false, ApprovalStatusConstant.NO_REVIEW, "");

		List<AddApplicationDetailModel> consumables = JSONObject
				.parseArray(model.getConsumables().getString("consumables"), AddApplicationDetailModel.class);

		// 构造消耗品申请详细
		List<ApplicationDetail> applicationDetails = new ArrayList<ApplicationDetail>();
		for (AddApplicationDetailModel consumable : consumables) {
			ApplicationDetail applicationDetail = new ApplicationDetail(RandomUtil.genarateId(), application.getId(),
					consumable.getId(), consumable.getApplicationCount(), "", false, false,
					ApprovalStatusConstant.UNDER_REVIEW, "");
			applicationDetails.add(applicationDetail);
		}

		applicationDao.insertApplication(application);
		applicationDetailDao.insertApplicationDetails(applicationDetails);

		return applicationDao.a_selectApplicationDtoById(application.getId());
	}

	@Override
	public void cancelApplication(String applicationId) {
		applicationDao.updateIsCancelApplication(applicationId);
	}

	@Override
	public String getWarehouseNameByApplicationId(String applicationId) {
		return warehouseDao.a_selectWarehouseByName(applicationId);
	}

	@Override
	public String getUserNameByApplicationId(String userId) {
		return userDao.a_selectUserNameByUserId(userId);
	}

}
