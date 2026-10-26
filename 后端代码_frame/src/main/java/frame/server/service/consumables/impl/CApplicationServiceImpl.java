package frame.server.service.consumables.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import frame.server.constant.ApprovalStatusConstant;
import frame.server.dao.ApplicationDao;
import frame.server.domain.Application;
import frame.server.dto.ApplicationDto;
import frame.server.service.consumables.CApplicationService;
import frame.server.utils.StringUtil;
import frame.server.vo.Page;
import frame.server.vo.consumables.CApplicationController.ASearchConditionModel;
import frame.server.vo.consumables.CApplicationController.ApprovalModel;

@Service("capplicationService")
public class CApplicationServiceImpl implements CApplicationService {

	@Autowired
	private ApplicationDao applicationDao;

	@Override
	public PageInfo<ApplicationDto> getApplications(Page page) {
		// 开启分页
		PageHelper.startPage(page.getPageNum(), page.getPageSize());

		PageInfo<ApplicationDto> pageInfo = null;

		ASearchConditionModel model = new ASearchConditionModel();

		JSONObject searchCondition = page.getSearchCondition();
		model.setWarehouseId(searchCondition.getString("warehouseId"));
		model.setSearchType(searchCondition.getString("searchType"));
		model.setSearchStr(searchCondition.getString("searchStr"));
		model.setStartTime(searchCondition.getString("startTime"));
		model.setEndTime(searchCondition.getString("endTime"));

		// 检索条件为空
		if (StringUtil.isBlank(model.getSearchStr())
				&& (StringUtil.isBlank(model.getStartTime()) || StringUtil.isBlank(model.getEndTime()))) {
			pageInfo = new PageInfo<ApplicationDto>(applicationDao.c_selectByWarehouseId(model.getWarehouseId()));
		} else {
			if (StringUtil.isBlank(model.getSearchStr())) {
				pageInfo = new PageInfo<ApplicationDto>(applicationDao.c_selectByWarehouseIdAndInTime(model));
			} else if (StringUtil.isBlank(model.getStartTime()) || StringUtil.isBlank(model.getEndTime())) {
				Application application = new Application();
				application.setWarehouseId(model.getWarehouseId());
				application.setName(model.getSearchStr());
				switch (model.getSearchType()) {
				// 申请名
				case "1":
					pageInfo = new PageInfo<ApplicationDto>(applicationDao.c_selectByWarehouseIdAndLikeName(application));
					break;
				// 用户名
				case "2":
					pageInfo = new PageInfo<ApplicationDto>(
							applicationDao.c_selectByWarehouseIdAndLikeUserName(application));
					break;
				default:
					pageInfo = new PageInfo<ApplicationDto>(applicationDao.c_selectByWarehouseId(model.getWarehouseId()));
					break;
				}
			} else {
				switch (model.getSearchType()) {
				// 申请名
				case "1":
					pageInfo = new PageInfo<ApplicationDto>(applicationDao.c_selectByWarehouseIdAndLikeNameAndInTime(model));
					break;
				// 用户名
				case "2":
					pageInfo = new PageInfo<ApplicationDto>(
							applicationDao.c_selectByWarehouseIdAndLikeUserNameAndInTime(model));
					break;
				default:
					pageInfo = new PageInfo<ApplicationDto>(applicationDao.c_selectByWarehouseId(model.getWarehouseId()));
					break;
				}
			}
		}

		return pageInfo;
	}

	@Override
	public Application getApplication(String applicationId) {
		return applicationDao.c_selectByApplicationId(applicationId);
	}

	@Override
	public void setApplicationApprovalUnderReview(String applicationId) {
		Application application = new Application();
		application.setId(applicationId);
		application.setApprovalStatus(ApprovalStatusConstant.UNDER_REVIEW);
		applicationDao.updateApplicationState(application);
	}

	@Override
	public void doApproval(ApprovalModel model) {
		applicationDao.c_updateApplicationState(model);
	}

}
