package frame.server.service.consumables.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import frame.server.constant.ConsumableOperateTypeConstant;
import frame.server.dao.ConsumableOperationHistoryDao;
import frame.server.dao.WarehouseDao;
import frame.server.domain.Warehouse;
import frame.server.dto.ConsumableOperationDetailDto;
import frame.server.dto.ConsumableOperationHistoryDto;
import frame.server.service.consumables.COperationHistoryService;
import frame.server.utils.StringUtil;
import frame.server.vo.ExcelDataModel;
import frame.server.vo.Page;
import frame.server.vo.consumables.CSearchConditionModel;

@Service("cOperationHistoryService")
public class COperationHistoryServiceImpl implements COperationHistoryService {

	@Autowired
	private ConsumableOperationHistoryDao consumableOperationHistoryDao;

	@Autowired
	private WarehouseDao warehouseDao;

	@Override
	public PageInfo<ConsumableOperationHistoryDto> getOperationHistorys(Page page) {
		// 开启分页
		PageHelper.startPage(page.getPageNum(), page.getPageSize());

		// 获取检索数据
		JSONObject searchCondition = page.getSearchCondition();

		// 构造检索数据实体
		CSearchConditionModel model = new CSearchConditionModel();
		model.setWarehouseId(searchCondition.getString("warehouseId"));
		model.setSearchType(searchCondition.getString("searchType"));
		model.setSearchStr(searchCondition.getString("searchStr"));
		model.setStartTime(searchCondition.getString("startTime"));
		model.setEndTime(searchCondition.getString("endTime"));

		PageInfo<ConsumableOperationHistoryDto> pageInfo = null;

		// 检索条件为空
		if (StringUtil.isBlank(model.getSearchStr())
				&& (StringUtil.isBlank(model.getStartTime()) || StringUtil.isBlank(model.getEndTime()))) {
			pageInfo = new PageInfo<ConsumableOperationHistoryDto>(
					consumableOperationHistoryDao.c_selectByWarehouseId(model.getWarehouseId()));
		} else {
			if (StringUtil.isBlank(model.getSearchStr())) {
				pageInfo = new PageInfo<ConsumableOperationHistoryDto>(
						consumableOperationHistoryDao.c_selectByWarehouseIdAndInTime(model));
			} else if (StringUtil.isBlank(model.getStartTime()) || StringUtil.isBlank(model.getEndTime())) {
				switch (model.getSearchType()) {
				// 用户名称
				case "1":
					pageInfo = new PageInfo<ConsumableOperationHistoryDto>(
							consumableOperationHistoryDao.c_selectByWarehouseIdAndLikeUserName(model));
					break;
				// 消耗品名称
				case "2":
					pageInfo = new PageInfo<ConsumableOperationHistoryDto>(
							consumableOperationHistoryDao.c_selectByWarehouseIdAndLikeConsumableName(model));
					break;
				}
			} else {
				switch (model.getSearchType()) {
				// 用户名称
				case "1":
					pageInfo = new PageInfo<ConsumableOperationHistoryDto>(
							consumableOperationHistoryDao.c_selectByWarehouseIdAndLikeUserNameAndInTime(model));
					break;
				// 消耗品名称
				case "2":
					pageInfo = new PageInfo<ConsumableOperationHistoryDto>(
							consumableOperationHistoryDao.c_selectByWarehouseIdAndLikeConsumableNameAndInTime(model));
					break;
				}
			}
		}

		return pageInfo;
	}

	@Override
	public ExcelDataModel getExportRolesData(CSearchConditionModel model) {
		// 获取仓库数据
		Warehouse warehouse = warehouseDao.selectWarehouseById(model.getWarehouseId());

		List<ConsumableOperationHistoryDto> operationHistorys = null;

		// 检索条件为空
		if (StringUtil.isBlank(model.getSearchStr())
				&& (StringUtil.isBlank(model.getStartTime()) || StringUtil.isBlank(model.getEndTime()))) {
			operationHistorys = consumableOperationHistoryDao.c_selectByWarehouseId(model.getWarehouseId());
		} else {
			if (StringUtil.isBlank(model.getSearchStr())) {
				operationHistorys = consumableOperationHistoryDao.c_selectByWarehouseIdAndInTime(model);
			} else if (StringUtil.isBlank(model.getStartTime()) || StringUtil.isBlank(model.getEndTime())) {
				switch (model.getSearchType()) {
				// 用户名称
				case "1":
					operationHistorys = consumableOperationHistoryDao.c_selectByWarehouseIdAndLikeUserName(model);
					break;
				// 消耗品名称
				case "2":
					operationHistorys = consumableOperationHistoryDao.c_selectByWarehouseIdAndLikeConsumableName(model);
					break;
				}
			} else {
				switch (model.getSearchType()) {
				// 用户名称
				case "1":
					operationHistorys = consumableOperationHistoryDao
							.c_selectByWarehouseIdAndLikeUserNameAndInTime(model);
					break;
				// 消耗品名称
				case "2":
					operationHistorys = consumableOperationHistoryDao
							.c_selectByWarehouseIdAndLikeConsumableNameAndInTime(model);
					break;
				}
			}
		}

		ExcelDataModel excelData = new ExcelDataModel();

		// 设置文件名
		Date now = new Date();
		String fileName = warehouse.getName() + "_消耗品操作履历_" + now.getTime();
		excelData.setName(fileName);

		// 设置表头
		List<String> heads = new ArrayList<String>();

		heads.add("用户名");
		heads.add("用户账号");
		heads.add("操作日期");
		heads.add("消耗品操作数");

		int operationDetailMaxCount = 0;
		for (ConsumableOperationHistoryDto operationHistory : operationHistorys) {
			if (operationHistory.getDetails() != null && !operationHistory.getDetails().isEmpty()) {
				if (operationHistory.getDetails().size() > operationDetailMaxCount) {
					operationDetailMaxCount = operationHistory.getDetails().size();
				}
			}
		}

		for (int i = 0; i < operationDetailMaxCount; i++) {
			heads.add("消耗品Code");
			heads.add("消耗品名称");
			heads.add("消耗品类型");
			heads.add("操作类型");
			heads.add("操作数");
			heads.add("备注");
		}

		excelData.setTitles(heads);

		// 设置数据
		List<List<Object>> templateDatas = new ArrayList<>();
		
		for (ConsumableOperationHistoryDto operationHistory : operationHistorys) {
			List<Object> templateData = new ArrayList<Object>();
			templateData.add(operationHistory.getUserName());
			templateData.add(operationHistory.getUserAccount());
			templateData.add(operationHistory.getCreateTime());
			templateData.add(operationHistory.getDetails().size());
			
			for(ConsumableOperationDetailDto detail : operationHistory.getDetails()) {
				templateData.add(detail.getConsumableCode());
				templateData.add(detail.getConsumableName());
				templateData.add(detail.getConsumableTypeName());
				templateData.add(ConsumableOperateTypeConstant.getOperationName(detail.getOperationType()));
				templateData.add(detail.getOperationCount());
				templateData.add(detail.getRemarks());
			}

			templateDatas.add(templateData);
		}
		
		excelData.setRows(templateDatas);
		return excelData;
	}

}
