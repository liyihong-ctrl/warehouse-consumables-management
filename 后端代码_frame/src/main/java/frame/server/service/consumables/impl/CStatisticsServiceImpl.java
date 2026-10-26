package frame.server.service.consumables.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import frame.server.constant.StatisticConstant;
import frame.server.dao.ConsumableDao;
import frame.server.dao.ConsumableOperationHistoryDao;
import frame.server.dao.WarehouseDao;
import frame.server.domain.Warehouse;
import frame.server.dto.ConsumableDto;
import frame.server.dto.ConsumableInfoDto;
import frame.server.dto.ConsumableStatisticDto;
import frame.server.service.consumables.CStatisticsService;
import frame.server.utils.DateUtil;
import frame.server.vo.ExcelDataModel;
import frame.server.vo.consumables.CStatisticsController.CConsumableStatisticsDetailModel;
import frame.server.vo.consumables.CStatisticsController.CConsumableStatisticsModel;
import frame.server.vo.consumables.CStatisticsController.CGetConsumableStatisticsSearchModel;
import frame.server.vo.consumables.CStatisticsController.CGetConsumableStatisticsSearchSingleModel;

@Service("cStatisticsService")
public class CStatisticsServiceImpl implements CStatisticsService {

	@Autowired
	private ConsumableDao consumableDao;

	@Autowired
	private ConsumableOperationHistoryDao consumableOperationHistoryDao;

	@Autowired
	private WarehouseDao warehouseDao;

	@Override
	public List<CConsumableStatisticsModel> getConsumableStatics(CGetConsumableStatisticsSearchModel model) {
		// 获取日期间隔的日期yyyy-MM-dd
		List<String> days = DateUtil.getDays(model.getStartTime(), model.getEndTime());
		
		// 结果集
		List<CConsumableStatisticsModel> res = new ArrayList<CConsumableStatisticsModel>();

		// 构造检索实体
		CGetConsumableStatisticsSearchSingleModel searchSingleModel = new CGetConsumableStatisticsSearchSingleModel();
		searchSingleModel.setWarehouseId(model.getWarehouseId());
		searchSingleModel.setStartTime(model.getStartTime());
		searchSingleModel.setEndTime(model.getEndTime());
		
		// 根据统计类型检索数据
		switch (model.getStaticType()) {
		// 出库数量
		case StatisticConstant.OUTBOUND_QUANTITY:
			for(String consumableId: model.getConsumableIds()) {
				// 设置检索实体的消耗品ID
				searchSingleModel.setConsumableId(consumableId);
				
				// 获取统计数据
				List<ConsumableStatisticDto> statisticsDatas = consumableOperationHistoryDao.c_selectOutboundStaticDataBySearchModel(searchSingleModel);
				
				// 重组为前端需要的结果集
				CConsumableStatisticsModel cConsumableStatisticsModel = new CConsumableStatisticsModel();
				
				// 若为空
				if(statisticsDatas == null || statisticsDatas.isEmpty()) {
					// 获取消耗品数据
					ConsumableInfoDto consumable = new ConsumableInfoDto();
					consumable.setWarehouseId(searchSingleModel.getWarehouseId());
					consumable.setId(searchSingleModel.getConsumableId());;
					ConsumableDto dbConsumable =  consumableDao.a_selectByWarehouseIdAndConsumableId(consumable);
					// 设置消耗品ID和名称
					cConsumableStatisticsModel.setConsumableId(dbConsumable.getId());
					cConsumableStatisticsModel.setConsumableName(dbConsumable.getName());
				} else {
					// 设置消耗品ID和名称
					cConsumableStatisticsModel.setConsumableId(statisticsDatas.get(0).getId());
					cConsumableStatisticsModel.setConsumableName(statisticsDatas.get(0).getName());
				}
				
				// 构造详情
				List<CConsumableStatisticsDetailModel> details = new ArrayList<CConsumableStatisticsDetailModel>();
				for(String day : days) {
					CConsumableStatisticsDetailModel detail = new CConsumableStatisticsDetailModel(day, 0);
					for(ConsumableStatisticDto statisticsData : statisticsDatas) {
						if(day.equals(statisticsData.getOperateTime())) {
							detail.setValue(statisticsData.getOperateCount());
							break;
						}
					}
					details.add(detail);
				}
				cConsumableStatisticsModel.setData(details);
				res.add(cConsumableStatisticsModel);
			}
			break;
		// 在工减少
		case StatisticConstant.REDUCED_WORK:
			for(String consumableId: model.getConsumableIds()) {
				// 设置检索实体的消耗品ID
				searchSingleModel.setConsumableId(consumableId);
				
				// 获取统计数据
				List<ConsumableStatisticDto> statisticsDatas = consumableOperationHistoryDao.c_selectReducedWorkStaticDataBySearchModel(searchSingleModel);
				
				// 重组为前端需要的结果集
				CConsumableStatisticsModel cConsumableStatisticsModel = new CConsumableStatisticsModel();
				
				// 若为空
				if(statisticsDatas == null || statisticsDatas.isEmpty()) {
					// 获取消耗品数据
					ConsumableInfoDto consumable = new ConsumableInfoDto();
					consumable.setWarehouseId(searchSingleModel.getWarehouseId());
					consumable.setId(searchSingleModel.getConsumableId());;
					ConsumableDto dbConsumable =  consumableDao.a_selectByWarehouseIdAndConsumableId(consumable);
					// 设置消耗品ID和名称
					cConsumableStatisticsModel.setConsumableId(dbConsumable.getId());
					cConsumableStatisticsModel.setConsumableName(dbConsumable.getName());
				} else {
					// 设置消耗品ID和名称
					cConsumableStatisticsModel.setConsumableId(statisticsDatas.get(0).getId());
					cConsumableStatisticsModel.setConsumableName(statisticsDatas.get(0).getName());
				}
				
				// 构造详情
				List<CConsumableStatisticsDetailModel> details = new ArrayList<CConsumableStatisticsDetailModel>();
				for(String day : days) {
					CConsumableStatisticsDetailModel detail = new CConsumableStatisticsDetailModel(day, 0);
					for(ConsumableStatisticDto statisticsData : statisticsDatas) {
						if(day.equals(statisticsData.getOperateTime())) {
							detail.setValue(statisticsData.getOperateCount());
							break;
						}
					}
					details.add(detail);
				}
				cConsumableStatisticsModel.setData(details);
				res.add(cConsumableStatisticsModel);
			}
			break;
		// 入库(包含新增)
		case StatisticConstant.INBOUND_QUANTITY:
			for(String consumableId: model.getConsumableIds()) {
				// 设置检索实体的消耗品ID
				searchSingleModel.setConsumableId(consumableId);
				
				// 获取统计数据
				List<ConsumableStatisticDto> statisticsDatas = consumableOperationHistoryDao.c_selectInboundStaticDataBySearchModel(searchSingleModel);
				
				// 重组为前端需要的结果集
				CConsumableStatisticsModel cConsumableStatisticsModel = new CConsumableStatisticsModel();
				
				// 若为空
				if(statisticsDatas == null || statisticsDatas.isEmpty()) {
					// 获取消耗品数据
					ConsumableInfoDto consumable = new ConsumableInfoDto();
					consumable.setWarehouseId(searchSingleModel.getWarehouseId());
					consumable.setId(searchSingleModel.getConsumableId());;
					ConsumableDto dbConsumable =  consumableDao.a_selectByWarehouseIdAndConsumableId(consumable);
					// 设置消耗品ID和名称
					cConsumableStatisticsModel.setConsumableId(dbConsumable.getId());
					cConsumableStatisticsModel.setConsumableName(dbConsumable.getName());
				} else {
					// 设置消耗品ID和名称
					cConsumableStatisticsModel.setConsumableId(statisticsDatas.get(0).getId());
					cConsumableStatisticsModel.setConsumableName(statisticsDatas.get(0).getName());
				}
				
				// 构造详情
				List<CConsumableStatisticsDetailModel> details = new ArrayList<CConsumableStatisticsDetailModel>();
				for(String day : days) {
					CConsumableStatisticsDetailModel detail = new CConsumableStatisticsDetailModel(day, 0);
					for(ConsumableStatisticDto statisticsData : statisticsDatas) {
						if(day.equals(statisticsData.getOperateTime())) {
							detail.setValue(statisticsData.getOperateCount());
							break;
						}
					}
					details.add(detail);
				}
				cConsumableStatisticsModel.setData(details);
				res.add(cConsumableStatisticsModel);
			}
			break;
		}
		return res;
	}

	@Override
	public ExcelDataModel getExportStatisticsData(CGetConsumableStatisticsSearchModel model) {
		// 获取仓库数据
		Warehouse warehouse = warehouseDao.selectWarehouseById(model.getWarehouseId());
		
		// 生成数据集
		List<CConsumableStatisticsModel> res = getConsumableStatics(model);
		
		ExcelDataModel excelData = new ExcelDataModel();

		// 设置文件名
		Date now = new Date();
		String fileName = warehouse.getName() + "_统计信息_" + now.getTime();
		excelData.setName(fileName);

		// 设置表头
		List<String> heads = new ArrayList<String>();
		
		heads.add("消耗品名称");
		
		for(CConsumableStatisticsDetailModel detail: res.get(0).getData()) {
			heads.add(detail.getDate());
		}

		excelData.setTitles(heads);

		// 设置数据
		List<List<Object>> templateDatas = new ArrayList<>();
		
		for(CConsumableStatisticsModel consumableStatistic : res) {
			List<Object> templateData = new ArrayList<Object>();
			
			templateData.add(consumableStatistic.getConsumableName());
			
			for(CConsumableStatisticsDetailModel detail : consumableStatistic.getData()) {
				templateData.add(detail.getValue());
			}

			templateDatas.add(templateData);
		}
		
		excelData.setRows(templateDatas);
		
		return excelData;
	}

}
