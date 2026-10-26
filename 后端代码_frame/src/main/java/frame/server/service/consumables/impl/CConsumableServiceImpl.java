package frame.server.service.consumables.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import frame.server.constant.ConsumableMaxCountConstant;
import frame.server.constant.ConsumableOperateTypeConstant;
import frame.server.constant.ReferenceInformationConstant;
import frame.server.dao.ApplicationDao;
import frame.server.dao.ConsumableDao;
import frame.server.dao.ConsumableInboundBatchDao;
import frame.server.dao.ConsumableOperationDetailDao;
import frame.server.dao.ConsumableOperationHistoryDao;
import frame.server.dao.ConsumableReferenceInformationDao;
import frame.server.dao.TypeDao;
import frame.server.dao.WarehouseDao;
import frame.server.domain.Consumable;
import frame.server.domain.ConsumableInboundBatch;
import frame.server.domain.ConsumableOperationDetail;
import frame.server.domain.ConsumableOperationHistory;
import frame.server.domain.ConsumableReferenceInformation;
import frame.server.domain.Type;
import frame.server.domain.Warehouse;
import frame.server.dto.ConsumableDto;
import frame.server.dto.ConsumableInfoDto;
import frame.server.dto.TypeDtoForWebCascader;
import frame.server.service.consumables.CConsumableService;
import frame.server.utils.RandomUtil;
import frame.server.utils.StringUtil;
import frame.server.utils.ValidatorUtil;
import frame.server.vo.ExcelDataModel;
import frame.server.vo.Page;
import frame.server.vo.consumables.CSearchConditionModel;
import frame.server.vo.consumables.ReferenceInformationModel;
import frame.server.vo.consumables.CConsumableController.CAddConsumableModel;
import frame.server.vo.consumables.CConsumableController.CEditConsumableModel;
import frame.server.vo.consumables.CConsumableController.CImportConsumableDetailModel;
import frame.server.vo.consumables.CConsumableController.CSearchApplicationModel;
import frame.server.vo.consumables.CConsumableController.InboundModel;
import frame.server.vo.consumables.CConsumableController.OutboundModel;

@Service("cConsumableService")
public class CConsumableServiceImpl implements CConsumableService {

	@Autowired
	private ApplicationDao applicationDao;

	@Autowired
	private ConsumableDao consumableDao;

	@Autowired
	private ConsumableInboundBatchDao consumableInboundBatchDao;

	@Autowired
	private ConsumableOperationDetailDao consumableOperationDetailDao;

	@Autowired
	private ConsumableOperationHistoryDao consumableOperationHistoryDao;

	@Autowired
	private ConsumableReferenceInformationDao consumableReferenceInformationDao;

	@Autowired
	private TypeDao typeDao;

	@Autowired
	private WarehouseDao warehouseDao;

	@Override
	public List<ConsumableDto> getAllConsumables(String warehouseId) {
		return consumableDao.c_selectByWarehouseId(warehouseId);
	}

	@Override
	public PageInfo<ConsumableDto> getConsumables(Page page) {
		// 开启分页
		PageHelper.startPage(page.getPageNum(), page.getPageSize());

		PageInfo<ConsumableDto> pageInfo = null;

		CSearchConditionModel model = new CSearchConditionModel();

		JSONObject searchCondition = page.getSearchCondition();

		model.setWarehouseId(searchCondition.getString("warehouseId"));
		model.setSearchType(searchCondition.getString("searchType"));
		model.setSearchStr(searchCondition.getString("searchStr"));
		model.setStartTime(searchCondition.getString("startTime"));
		model.setEndTime(searchCondition.getString("endTime"));
		model.setIsViewAlarmData(searchCondition.getBooleanValue("isViewAlarmData"));

		// 检索条件为空
		if (StringUtil.isBlank(model.getSearchStr())
				&& (StringUtil.isBlank(model.getStartTime()) || StringUtil.isBlank(model.getEndTime()))) {
			if (!model.getIsViewAlarmData()) {
				pageInfo = new PageInfo<ConsumableDto>(consumableDao.c_selectByWarehouseId(model.getWarehouseId()));
			} else {
				pageInfo = new PageInfo<ConsumableDto>(
						consumableDao.c_selectAlarmByWarehouseId(model.getWarehouseId()));
			}
		} else {
			if (StringUtil.isBlank(model.getSearchStr())) {
				if (!model.getIsViewAlarmData()) {
					pageInfo = new PageInfo<ConsumableDto>(consumableDao.c_selectByWarehouseIdAndInTime(model));
				} else {
					pageInfo = new PageInfo<ConsumableDto>(
							consumableDao.c_selectAlarmDataByWarehouseIdAndInTime(model));
				}
			} else if (StringUtil.isBlank(model.getStartTime()) || StringUtil.isBlank(model.getEndTime())) {
				switch (model.getSearchType()) {
				// code
				case "1":
					if (!model.getIsViewAlarmData()) {
						pageInfo = new PageInfo<ConsumableDto>(consumableDao.c_selectByWarehouseIdAndCode(model));
					} else {
						pageInfo = new PageInfo<ConsumableDto>(
								consumableDao.c_selectAlarmDataByWarehouseIdAndCode(model));
					}
					break;
				// 消耗品名称
				case "2":
					if (!model.getIsViewAlarmData()) {
						pageInfo = new PageInfo<ConsumableDto>(consumableDao.c_selectByWarehouseIdAndLikeName(model));
					} else {
						pageInfo = new PageInfo<ConsumableDto>(
								consumableDao.c_selectAlarmDataByWarehouseIdAndLikeName(model));
					}
					break;
				// 类型名称
				case "3":
					if (!model.getIsViewAlarmData()) {
						pageInfo = new PageInfo<ConsumableDto>(
								consumableDao.c_selectByWarehouseIdAndLikeTypeName(model));
					} else {
						pageInfo = new PageInfo<ConsumableDto>(
								consumableDao.c_selectAlarmDataByWarehouseIdAndLikeTypeName(model));
					}
					break;
				}
			} else {
				switch (model.getSearchType()) {
				// code
				case "1":
					if (!model.getIsViewAlarmData()) {
						pageInfo = new PageInfo<ConsumableDto>(
								consumableDao.c_selectByWarehouseIdAndCodeAndInTime(model));
					} else {
						pageInfo = new PageInfo<ConsumableDto>(
								consumableDao.c_selectAlarmDataByWarehouseIdAndCodeAndInTime(model));
					}
					break;
				// 消耗品名称
				case "2":
					if (!model.getIsViewAlarmData()) {
						pageInfo = new PageInfo<ConsumableDto>(
								consumableDao.c_selectByWarehouseIdAndLikeNameAndInTime(model));
					} else {
						pageInfo = new PageInfo<ConsumableDto>(
								consumableDao.c_selectAlarmDataByWarehouseIdAndLikeNameAndInTime(model));
					}
					break;
				// 类型名称
				case "3":
					if (!model.getIsViewAlarmData()) {
						pageInfo = new PageInfo<ConsumableDto>(
								consumableDao.c_selectByWarehouseIdAndLikeTypeNameAndInTime(model));
					} else {
						pageInfo = new PageInfo<ConsumableDto>(
								consumableDao.c_selectAlarmDataByWarehouseIdAndLikeTypeNameAndInTime(model));
					}
					break;
				}
			}
		}

		return pageInfo;
	}

	@Override
	public List<ConsumableDto> getAllConsumablesBySearchCondition(CSearchConditionModel model) {
		List<ConsumableDto> consumables = new ArrayList<ConsumableDto>();

		// 检索条件为空
		if (StringUtil.isBlank(model.getSearchStr())
				&& (StringUtil.isBlank(model.getStartTime()) || StringUtil.isBlank(model.getEndTime()))) {
			if (!model.getIsViewAlarmData()) {
				consumables = consumableDao.c_selectByWarehouseId(model.getWarehouseId());
			} else {
				consumables = consumableDao.c_selectAlarmByWarehouseId(model.getWarehouseId());
			}
		} else {
			if (StringUtil.isBlank(model.getSearchStr())) {
				if (!model.getIsViewAlarmData()) {
					consumables = consumableDao.c_selectByWarehouseIdAndInTime(model);
				} else {
					consumables = consumableDao.c_selectAlarmDataByWarehouseIdAndInTime(model);
				}
			} else if (StringUtil.isBlank(model.getStartTime()) || StringUtil.isBlank(model.getEndTime())) {
				switch (model.getSearchType()) {
				// code
				case "1":
					if (!model.getIsViewAlarmData()) {
						consumables = consumableDao.c_selectByWarehouseIdAndCode(model);
					} else {
						consumables = consumableDao.c_selectAlarmDataByWarehouseIdAndCode(model);
					}
					break;
				// 消耗品名称
				case "2":
					if (!model.getIsViewAlarmData()) {
						consumables = consumableDao.c_selectByWarehouseIdAndLikeName(model);
					} else {
						consumables = consumableDao.c_selectAlarmDataByWarehouseIdAndLikeName(model);
					}
					break;
				// 类型名称
				case "3":
					if (!model.getIsViewAlarmData()) {
						consumables = consumableDao.c_selectByWarehouseIdAndLikeTypeName(model);
					} else {
						consumables = consumableDao.c_selectAlarmDataByWarehouseIdAndLikeTypeName(model);
					}
					break;
				}
			} else {
				switch (model.getSearchType()) {
				// code
				case "1":
					if (!model.getIsViewAlarmData()) {
						consumables = consumableDao.c_selectByWarehouseIdAndCodeAndInTime(model);
					} else {
						consumables = consumableDao.c_selectAlarmDataByWarehouseIdAndCodeAndInTime(model);
					}
					break;
				// 消耗品名称
				case "2":
					if (!model.getIsViewAlarmData()) {
						consumables = consumableDao.c_selectByWarehouseIdAndLikeNameAndInTime(model);
					} else {
						consumables = consumableDao.c_selectAlarmDataByWarehouseIdAndLikeNameAndInTime(model);
					}
					break;
				// 类型名称
				case "3":
					if (!model.getIsViewAlarmData()) {
						consumables = consumableDao.c_selectByWarehouseIdAndLikeTypeNameAndInTime(model);
					} else {
						consumables = consumableDao.c_selectAlarmDataByWarehouseIdAndLikeTypeNameAndInTime(model);
					}
					break;
				}
			}
		}

		return consumables;
	}

	@Override
	public Consumable getConsumable(String warehouseId, String consumableId) {
		ConsumableInfoDto consumableInfo = new ConsumableInfoDto();
		consumableInfo.setWarehouseId(warehouseId);
		consumableInfo.setId(consumableId);

		return consumableDao.selectByWarehouseIdAndConsumableId(consumableInfo);
	}

	@Override
	public List<TypeDtoForWebCascader> getTypes(String warehouseId) {
		return typeDao.c_selectTypeDtoForWebCascaderByWarehouseId(warehouseId);
	}

	@Override
	public boolean isConsumableExist(String warehouseId, String consumableId) {
		ConsumableInfoDto consumableInfo = new ConsumableInfoDto();
		consumableInfo.setWarehouseId(warehouseId);
		consumableInfo.setId(consumableId);

		return consumableDao.selectCountByWarehouseIdAndConsumableId(consumableInfo) > 0;
	}

	@Override
	public boolean isConsumableApplied(String warehouseId, String consumableId) {
		CSearchApplicationModel model = new CSearchApplicationModel(warehouseId, consumableId);
		return applicationDao.c_selectCountByWarehouseIdAndConsumableIdAndIsIncompleteStatus(model) > 0;
	}

	@Override
	public boolean isConsumableCodeExist(String warehouseId, String code) {
		Consumable consumable = new Consumable();
		consumable.setWarehouseId(warehouseId);
		consumable.setCode(code);
		return consumableDao.c_selectConsumableCountByWarehouseIdAndConsumableCode(consumable) > 0;
	}

	@Override
	public ConsumableDto addConsumable(String userId, CAddConsumableModel model) {
		// 生成消耗品实体
		Consumable consumable = model.createConsumable();

		// 生成消耗品基准信息数据集
		List<ConsumableReferenceInformation> referenceInformations = new ArrayList<ConsumableReferenceInformation>();
		if (model.getReferenceInformations() != null && !model.getReferenceInformations().isEmpty()) {
			// JSON转JAVA
			List<ReferenceInformationModel> referenceInformationList = JSONObject.parseArray(
					model.getReferenceInformations().getString("referenceInformations"),
					ReferenceInformationModel.class);

			// 生成消耗品基准信息
			if (referenceInformationList != null && !referenceInformationList.isEmpty()) {
				int displayOrder = 1;
				for (ReferenceInformationModel referenceInformation : referenceInformationList) {
					String key = referenceInformation.getKey();
					String value = referenceInformation.getValue();

					if (!StringUtil.isBlank(key) || !StringUtil.isBlank(value)) {
						referenceInformations.add(new ConsumableReferenceInformation(RandomUtil.genarateId(),
								consumable.getId(), referenceInformation.getKey(), referenceInformation.getValue(),
								displayOrder++));
					}
				}
			}
		}

		// 保存数据
		consumableDao.insertConsumable(consumable);
		if (!referenceInformations.isEmpty()) {
			consumableReferenceInformationDao.insertReferenceInformations(referenceInformations);
		}

		// 保存批次
		ConsumableInboundBatch consumableInboundBatch = new ConsumableInboundBatch(RandomUtil.genarateId(),
				consumable.getId(), consumable.getCount(), consumable.getPrice(), new Date());
		consumableInboundBatchDao.insertConsumableInboundBatch(consumableInboundBatch);

		// 保存操作记录
		ConsumableOperationHistory consumableOperationHistory = new ConsumableOperationHistory(RandomUtil.genarateId(),
				consumable.getWarehouseId(), userId, new Date());
		ConsumableOperationDetail consumableOperationDetail = new ConsumableOperationDetail(RandomUtil.genarateId(),
				consumableOperationHistory.getId(), consumable.getId(), ConsumableOperateTypeConstant.ADD_CONSUMABLE,
				consumable.getCount(), "");

		// 将操作记录保存至数据库
		consumableOperationHistoryDao.insertConsumableOperationHistory(consumableOperationHistory);
		consumableOperationDetailDao.insertConsumableOperationDetail(consumableOperationDetail);

		ConsumableInfoDto selectModel = new ConsumableInfoDto();
		selectModel.setWarehouseId(consumable.getWarehouseId());
		selectModel.setId(consumable.getId());

		return consumableDao.selectByWarehouseIdAndConsumableId(selectModel);
	}

	@Override
	public ConsumableDto editConsumable(CEditConsumableModel model) {
		// 生成消耗品修改实体
		ConsumableInfoDto consumable = new ConsumableInfoDto(model.getId(), model.getWarehouseId(), model.getTypeId(),
				model.getCode(), model.getName(), model.getCount(), model.getSafetyCount(), model.getPrice(),
				model.getSummary());

		// 生成消耗品基准信息修改数据集
		List<ConsumableReferenceInformation> referenceInformations = new ArrayList<ConsumableReferenceInformation>();
		if (model.getReferenceInformations() != null && !model.getReferenceInformations().isEmpty()) {
			// JSON转JAVA
			List<ReferenceInformationModel> referenceInformationList = JSONObject.parseArray(
					model.getReferenceInformations().getString("referenceInformations"),
					ReferenceInformationModel.class);

			// 生成消耗品基准信息
			if (referenceInformationList != null && !referenceInformationList.isEmpty()) {
				int displayOrder = 1;
				for (ReferenceInformationModel referenceInformation : referenceInformationList) {
					String key = referenceInformation.getKey();
					String value = referenceInformation.getValue();

					if (!StringUtil.isBlank(key) || !StringUtil.isBlank(value)) {
						referenceInformations.add(new ConsumableReferenceInformation(RandomUtil.genarateId(),
								consumable.getId(), referenceInformation.getKey(), referenceInformation.getValue(),
								displayOrder++));
					}
				}
			}
		}

		// 更新数据
		consumableDao.update(consumable);
		// 消耗品基准信息执行全删全插操作
		consumableReferenceInformationDao.deleteByConsumableId(consumable.getId());
		if (!referenceInformations.isEmpty()) {
			consumableReferenceInformationDao.insertReferenceInformations(referenceInformations);
		}

		// 若价格更新，则需要更新最后一次入库别的价格
		ConsumableInboundBatch oldConsumableInboundBatch = consumableInboundBatchDao
				.selectByConsumableIdLastTime(consumable.getId());
		if (oldConsumableInboundBatch != null && consumable.getPrice() != oldConsumableInboundBatch.getPrice()) {
			ConsumableInboundBatch newConsumableInboundBatch = new ConsumableInboundBatch();
			newConsumableInboundBatch.setId(oldConsumableInboundBatch.getId());
			newConsumableInboundBatch.setPrice(consumable.getPrice());
			consumableInboundBatchDao.updatePrice(newConsumableInboundBatch);
		}

		ConsumableInfoDto selectModel = new ConsumableInfoDto();
		selectModel.setWarehouseId(consumable.getWarehouseId());
		selectModel.setId(consumable.getId());

		return consumableDao.selectByWarehouseIdAndConsumableId(selectModel);
	}

	@Override
	public void deleteConsumable(String warehouseId, String userId, String consumableId) {
		// 获取即将删除的消耗品数量
		Consumable consumabe = new Consumable();
		consumabe.setWarehouseId(warehouseId);
		consumabe.setId(consumableId);
		int consumableCount = consumableDao.c_selectConsumableCountByWarehouseIdAndConsumableId(consumabe);

		// 构造删除实体
		ConsumableInfoDto consumable = new ConsumableInfoDto();
		consumable.setWarehouseId(warehouseId);
		consumable.setId(consumableId);

		// 数据库执行删除操作
		consumableDao.deleteByConsumableId(consumable);

		// 保存操作记录
		ConsumableOperationHistory consumableOperationHistory = new ConsumableOperationHistory(RandomUtil.genarateId(),
				consumabe.getWarehouseId(), userId, new Date());
		ConsumableOperationDetail consumableOperationDetail = new ConsumableOperationDetail(RandomUtil.genarateId(),
				consumableOperationHistory.getId(), consumableId, ConsumableOperateTypeConstant.DELETE_CONSUMABLE,
				consumableCount, "");

		// 将操作记录保存至数据库
		consumableOperationHistoryDao.insertConsumableOperationHistory(consumableOperationHistory);
		consumableOperationDetailDao.insertConsumableOperationDetail(consumableOperationDetail);
	}

	@Override
	public ExcelDataModel getImportTemplateData(String warehouseId) {
		// 获取仓库数据
		Warehouse warehouse = warehouseDao.selectWarehouseById(warehouseId);

		ExcelDataModel excelData = new ExcelDataModel();

		// 设置文件名
		Date now = new Date();
		String fileName = warehouse.getName() + "_批量导入消耗品_样本_" + now.getTime();
		excelData.setName(fileName);

		// 设置表头
		List<String> heads = new ArrayList<String>();

		heads.add("Code");
		heads.add("名称");
		heads.add("类型");
		heads.add("数量");
		heads.add("安全在工数");
		heads.add("未税单价");
		heads.add("简介");

		// 基准信息数最多为20
		for (int i = 1; i <= 20; i++) {
			heads.add("基准信息名" + i);
			heads.add("基准信息值" + i);
		}

		excelData.setTitles(heads);

		// 设置数据
		List<List<Object>> templateDatas = new ArrayList<>();

		List<Object> templateData = new ArrayList<Object>();
		templateData.add("Code(1-20位数字或字母)");
		templateData.add("消耗品名称");
		templateData.add("消耗品类型名");
		templateData.add(100);
		templateData.add(100);
		templateData.add(100.00);
		templateData.add("消耗品简介");

		// 系统基准信息
		templateData.add(ReferenceInformationConstant.LOCATION);
		templateData.add("");

		templateData.add(ReferenceInformationConstant.SUBCATEGORY);
		templateData.add("");

		templateData.add(ReferenceInformationConstant.APPLICABLE_PROJECT_0);
		templateData.add("");

		templateData.add(ReferenceInformationConstant.APPLICABLE_PROJECT_1);
		templateData.add("");

		templateData.add(ReferenceInformationConstant.APPLICABLE_PROJECT_2);
		templateData.add("");

		templateData.add(ReferenceInformationConstant.APPLICABLE_MODELS);
		templateData.add("");

		templateData.add(ReferenceInformationConstant.ITEM_TYPE1);
		templateData.add("");

		templateData.add(ReferenceInformationConstant.ITEM_TYPE2);
		templateData.add("");

		templateData.add(ReferenceInformationConstant.UNIT);
		templateData.add("");

		templateData.add(ReferenceInformationConstant.SAFETY_STOCK);
		templateData.add("");

		templateData.add(ReferenceInformationConstant.CURRENT_INVENTORY);
		templateData.add("");

		templateData.add(ReferenceInformationConstant.OUT_OF_STOCK_LAST_WEEK);
		templateData.add("");

		templateData.add(ReferenceInformationConstant.OUT_OF_THE_MONTH);
		templateData.add("");

		templateData.add(ReferenceInformationConstant.UNIT_PRICE);
		templateData.add("");

		templateData.add(ReferenceInformationConstant.INVENTORY_AAMOUNT);
		templateData.add("");

		templateData.add(ReferenceInformationConstant.INDUSTRY_BODY);
		templateData.add("");

		templateData.add(ReferenceInformationConstant.PURCHASING_CYCLE);
		templateData.add("");

		templateDatas.add(templateData);

		excelData.setRows(templateDatas);

		return excelData;
	}

	@Override
	public JSONObject checkImportConsumables(String warehouseId, List<CImportConsumableDetailModel> importConsumables) {
		// 获取仓库所有类型数据
		List<Type> dbTypes = typeDao.c_selectTypeByWarehouseId(warehouseId);

		JSONObject errorDatas = new JSONObject();

		// 循环校验
		for (int i = 0; i < importConsumables.size(); i++) {
			JSONObject errorData = new JSONObject();

			CImportConsumableDetailModel curImportConsumable = importConsumables.get(i);

			// 校验code
			if (StringUtil.isBlank(curImportConsumable.getCode())) {
				errorData.put("code", "Code不能为空");
			} else {
				String code = curImportConsumable.getCode().trim();
				if (!ValidatorUtil.isConsumableCode(code)) {
					errorData.put("code", "Code格式错误(1-20个字母、数字)");
				}

				// 校验code是否重复
				for (int j = 0; j < importConsumables.size(); j++) {
					if (code.equals(importConsumables.get(j).getCode().trim())) {
						if (i != j) {
							errorData.put("code", "Code重复");
						}
					}
				}
			}

			// 校验消耗品名称
			if (StringUtil.isBlank(curImportConsumable.getName())) {
				errorData.put("name", "消耗品名称不能为空");
			} else {
				String name = curImportConsumable.getName().trim();
				if (name.length() > 50) {
					errorData.put("name", "消耗品名称长度范围为1~50");
				}
			}

			// 校验类型名
			if (StringUtil.isBlank(curImportConsumable.getTypeName())) {
				errorData.put("typeName", "消耗品类型名称不能为空");
			} else {
				String typeName = curImportConsumable.getTypeName().trim();
				if (typeName.length() > 20) {
					errorData.put("typeName", "消耗品类型长度范围为1~20");
				} else {
					// 校验类型名是否存在
					boolean isExist = false;
					for (Type dbType : dbTypes) {
						if (typeName.equals(dbType.getName())) {
							isExist = true;
							break;
						}
					}
					if (!isExist) {
						errorData.put("typeName", "消耗品类型名不存在");
					}
				}
			}

			// 校验数量
			int count = curImportConsumable.getCount();
			if (count < 0 || ConsumableMaxCountConstant.MAXINVENTORYCOUNT < count) {
				errorData.put("count", "数量范围为1~9999999");
			}

			// 校验安全在工数
			int safetyCount = curImportConsumable.getSafetyCount();
			if (safetyCount < 0 || ConsumableMaxCountConstant.MAXINVENTORYCOUNT < safetyCount) {
				errorData.put("safetyCount", "安全在工数范围为1~9999999");
			}

			// 校验价格
			double price = curImportConsumable.getPrice();
			if (price < 0 || ConsumableMaxCountConstant.MAXPRICECOUNT < price) {
				errorData.put("price", "未税单价范围为1~9999999.99");
			}

			// 校验简介
			if (!StringUtil.isBlank(curImportConsumable.getSummary())) {
				String summary = curImportConsumable.getSummary().trim();
				if (summary.length() > 255) {
					errorData.put("summary", "简介长度范围为0~255");
				}
			}

			// 校验基准信息 名和值长度不超过255
			// 1
			if (StringUtil.isBlank(curImportConsumable.getReferenceInformationKey1())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue1())) {
				errorData.put("referenceInformationKey1", "消耗品基准信息名1不能为空");
			} else if (!StringUtil.isBlank(curImportConsumable.getReferenceInformationKey1())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue1())) {
				String key = curImportConsumable.getReferenceInformationKey1().trim();
				String value = curImportConsumable.getReferenceInformationValue1().trim();

				if (key.length() > 255) {
					errorData.put("referenceInformationKey1", "消耗品基准信息名1长度范围为0~255");
				}
				if (value.length() > 255) {
					errorData.put("referenceInformationValue1", "消耗品基准信息值1长度范围为0~255");
				}
			}

			// 2
			if (StringUtil.isBlank(curImportConsumable.getReferenceInformationKey2())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue2())) {
				errorData.put("referenceInformationKey2", "消耗品基准信息名2不能为空");
			} else if (!StringUtil.isBlank(curImportConsumable.getReferenceInformationKey2())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue2())) {
				String key = curImportConsumable.getReferenceInformationKey2().trim();
				String value = curImportConsumable.getReferenceInformationValue2().trim();

				if (key.length() > 255) {
					errorData.put("referenceInformationKey2", "消耗品基准信息名2长度范围为0~255");
				}
				if (value.length() > 255) {
					errorData.put("referenceInformationValue2", "消耗品基准信息值2长度范围为0~255");
				}
			}

			// 3
			if (StringUtil.isBlank(curImportConsumable.getReferenceInformationKey3())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue3())) {
				errorData.put("referenceInformationKey3", "消耗品基准信息名3不能为空");
			} else if (!StringUtil.isBlank(curImportConsumable.getReferenceInformationKey3())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue3())) {
				String key = curImportConsumable.getReferenceInformationKey3().trim();
				String value = curImportConsumable.getReferenceInformationValue3().trim();

				if (key.length() > 255) {
					errorData.put("referenceInformationKey3", "消耗品基准信息名3长度范围为0~255");
				}
				if (value.length() > 255) {
					errorData.put("referenceInformationValue3", "消耗品基准信息值3长度范围为0~255");
				}
			}

			// 4
			if (StringUtil.isBlank(curImportConsumable.getReferenceInformationKey4())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue4())) {
				errorData.put("referenceInformationKey4", "消耗品基准信息名4不能为空");
			} else if (!StringUtil.isBlank(curImportConsumable.getReferenceInformationKey4())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue4())) {
				String key = curImportConsumable.getReferenceInformationKey4().trim();
				String value = curImportConsumable.getReferenceInformationValue4().trim();

				if (key.length() > 255) {
					errorData.put("referenceInformationKey4", "消耗品基准信息名4长度范围为0~255");
				}
				if (value.length() > 255) {
					errorData.put("referenceInformationValue4", "消耗品基准信息值4长度范围为0~255");
				}
			}

			// 5
			if (StringUtil.isBlank(curImportConsumable.getReferenceInformationKey5())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue5())) {
				errorData.put("referenceInformationKey5", "消耗品基准信息名5不能为空");
			} else if (!StringUtil.isBlank(curImportConsumable.getReferenceInformationKey5())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue5())) {
				String key = curImportConsumable.getReferenceInformationKey5().trim();
				String value = curImportConsumable.getReferenceInformationValue5().trim();

				if (key.length() > 255) {
					errorData.put("referenceInformationKey5", "消耗品基准信息名5长度范围为0~255");
				}
				if (value.length() > 255) {
					errorData.put("referenceInformationValue5", "消耗品基准信息值5长度范围为0~255");
				}
			}

			// 6
			if (StringUtil.isBlank(curImportConsumable.getReferenceInformationKey6())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue6())) {
				errorData.put("referenceInformationKey6", "消耗品基准信息名6不能为空");
			} else if (!StringUtil.isBlank(curImportConsumable.getReferenceInformationKey6())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue6())) {
				String key = curImportConsumable.getReferenceInformationKey6().trim();
				String value = curImportConsumable.getReferenceInformationValue6().trim();

				if (key.length() > 255) {
					errorData.put("referenceInformationKey6", "消耗品基准信息名6长度范围为0~255");
				}
				if (value.length() > 255) {
					errorData.put("referenceInformationValue6", "消耗品基准信息值6长度范围为0~255");
				}
			}

			// 7
			if (StringUtil.isBlank(curImportConsumable.getReferenceInformationKey7())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue7())) {
				errorData.put("referenceInformationKey7", "消耗品基准信息名7不能为空");
			} else if (!StringUtil.isBlank(curImportConsumable.getReferenceInformationKey7())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue7())) {
				String key = curImportConsumable.getReferenceInformationKey7().trim();
				String value = curImportConsumable.getReferenceInformationValue7().trim();

				if (key.length() > 255) {
					errorData.put("referenceInformationKey7", "消耗品基准信息名7长度范围为0~255");
				}
				if (value.length() > 255) {
					errorData.put("referenceInformationValue7", "消耗品基准信息值7长度范围为0~255");
				}
			}

			// 8
			if (StringUtil.isBlank(curImportConsumable.getReferenceInformationKey8())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue8())) {
				errorData.put("referenceInformationKey8", "消耗品基准信息名8不能为空");
			} else if (!StringUtil.isBlank(curImportConsumable.getReferenceInformationKey8())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue8())) {
				String key = curImportConsumable.getReferenceInformationKey8().trim();
				String value = curImportConsumable.getReferenceInformationValue8().trim();

				if (key.length() > 255) {
					errorData.put("referenceInformationKey8", "消耗品基准信息名8长度范围为0~255");
				}
				if (value.length() > 255) {
					errorData.put("referenceInformationValue8", "消耗品基准信息值8长度范围为0~255");
				}
			}

			// 9
			if (StringUtil.isBlank(curImportConsumable.getReferenceInformationKey9())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue9())) {
				errorData.put("referenceInformationKey9", "消耗品基准信息名9不能为空");
			} else if (!StringUtil.isBlank(curImportConsumable.getReferenceInformationKey9())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue9())) {
				String key = curImportConsumable.getReferenceInformationKey9().trim();
				String value = curImportConsumable.getReferenceInformationValue9().trim();

				if (key.length() > 255) {
					errorData.put("referenceInformationKey9", "消耗品基准信息名9长度范围为0~255");
				}
				if (value.length() > 255) {
					errorData.put("referenceInformationValue9", "消耗品基准信息值9长度范围为0~255");
				}
			}

			// 10
			if (StringUtil.isBlank(curImportConsumable.getReferenceInformationKey10())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue10())) {
				errorData.put("referenceInformationKey10", "消耗品基准信息名10不能为空");
			} else if (!StringUtil.isBlank(curImportConsumable.getReferenceInformationKey10())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue10())) {
				String key = curImportConsumable.getReferenceInformationKey10().trim();
				String value = curImportConsumable.getReferenceInformationValue10().trim();

				if (key.length() > 255) {
					errorData.put("referenceInformationKey10", "消耗品基准信息名10长度范围为0~255");
				}
				if (value.length() > 255) {
					errorData.put("referenceInformationValue10", "消耗品基准信息值10长度范围为0~255");
				}
			}

			// 11
			if (StringUtil.isBlank(curImportConsumable.getReferenceInformationKey11())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue11())) {
				errorData.put("referenceInformationKey11", "消耗品基准信息名11不能为空");
			} else if (!StringUtil.isBlank(curImportConsumable.getReferenceInformationKey11())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue11())) {
				String key = curImportConsumable.getReferenceInformationKey11().trim();
				String value = curImportConsumable.getReferenceInformationValue11().trim();

				if (key.length() > 255) {
					errorData.put("referenceInformationKey11", "消耗品基准信息名11长度范围为0~255");
				}
				if (value.length() > 255) {
					errorData.put("referenceInformationValue11", "消耗品基准信息值11长度范围为0~255");
				}
			}

			// 12
			if (StringUtil.isBlank(curImportConsumable.getReferenceInformationKey12())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue12())) {
				errorData.put("referenceInformationKey12", "消耗品基准信息名12不能为空");
			} else if (!StringUtil.isBlank(curImportConsumable.getReferenceInformationKey12())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue12())) {
				String key = curImportConsumable.getReferenceInformationKey12().trim();
				String value = curImportConsumable.getReferenceInformationValue12().trim();

				if (key.length() > 255) {
					errorData.put("referenceInformationKey12", "消耗品基准信息名12长度范围为0~255");
				}
				if (value.length() > 255) {
					errorData.put("referenceInformationValue12", "消耗品基准信息值12长度范围为0~255");
				}
			}

			// 13
			if (StringUtil.isBlank(curImportConsumable.getReferenceInformationKey13())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue13())) {
				errorData.put("referenceInformationKey13", "消耗品基准信息名13不能为空");
			} else if (!StringUtil.isBlank(curImportConsumable.getReferenceInformationKey13())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue13())) {
				String key = curImportConsumable.getReferenceInformationKey13().trim();
				String value = curImportConsumable.getReferenceInformationValue13().trim();

				if (key.length() > 255) {
					errorData.put("referenceInformationKey13", "消耗品基准信息名13长度范围为0~255");
				}
				if (value.length() > 255) {
					errorData.put("referenceInformationValue13", "消耗品基准信息值13长度范围为0~255");
				}
			}

			// 14
			if (StringUtil.isBlank(curImportConsumable.getReferenceInformationKey14())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue14())) {
				errorData.put("referenceInformationKey14", "消耗品基准信息名14不能为空");
			} else if (!StringUtil.isBlank(curImportConsumable.getReferenceInformationKey14())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue14())) {
				String key = curImportConsumable.getReferenceInformationKey14().trim();
				String value = curImportConsumable.getReferenceInformationValue14().trim();

				if (key.length() > 255) {
					errorData.put("referenceInformationKey14", "消耗品基准信息名14长度范围为0~255");
				}
				if (value.length() > 255) {
					errorData.put("referenceInformationValue14", "消耗品基准信息值14长度范围为0~255");
				}
			}

			// 15
			if (StringUtil.isBlank(curImportConsumable.getReferenceInformationKey15())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue15())) {
				errorData.put("referenceInformationKey15", "消耗品基准信息名15不能为空");
			} else if (!StringUtil.isBlank(curImportConsumable.getReferenceInformationKey15())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue15())) {
				String key = curImportConsumable.getReferenceInformationKey15().trim();
				String value = curImportConsumable.getReferenceInformationValue15().trim();

				if (key.length() > 255) {
					errorData.put("referenceInformationKey15", "消耗品基准信息名15长度范围为0~255");
				}
				if (value.length() > 255) {
					errorData.put("referenceInformationValue15", "消耗品基准信息值15长度范围为0~255");
				}
			}

			// 16
			if (StringUtil.isBlank(curImportConsumable.getReferenceInformationKey16())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue16())) {
				errorData.put("referenceInformationKey16", "消耗品基准信息名16不能为空");
			} else if (!StringUtil.isBlank(curImportConsumable.getReferenceInformationKey16())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue16())) {
				String key = curImportConsumable.getReferenceInformationKey16().trim();
				String value = curImportConsumable.getReferenceInformationValue16().trim();

				if (key.length() > 255) {
					errorData.put("referenceInformationKey16", "消耗品基准信息名16长度范围为0~255");
				}
				if (value.length() > 255) {
					errorData.put("referenceInformationValue16", "消耗品基准信息值16长度范围为0~255");
				}
			}

			// 17
			if (StringUtil.isBlank(curImportConsumable.getReferenceInformationKey17())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue17())) {
				errorData.put("referenceInformationKey17", "消耗品基准信息名17不能为空");
			} else if (!StringUtil.isBlank(curImportConsumable.getReferenceInformationKey17())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue17())) {
				String key = curImportConsumable.getReferenceInformationKey17().trim();
				String value = curImportConsumable.getReferenceInformationValue17().trim();

				if (key.length() > 255) {
					errorData.put("referenceInformationKey17", "消耗品基准信息名17长度范围为0~255");
				}
				if (value.length() > 255) {
					errorData.put("referenceInformationValue17", "消耗品基准信息值17长度范围为0~255");
				}
			}

			// 18
			if (StringUtil.isBlank(curImportConsumable.getReferenceInformationKey18())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue18())) {
				errorData.put("referenceInformationKey18", "消耗品基准信息名18不能为空");
			} else if (!StringUtil.isBlank(curImportConsumable.getReferenceInformationKey18())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue18())) {
				String key = curImportConsumable.getReferenceInformationKey18().trim();
				String value = curImportConsumable.getReferenceInformationValue18().trim();

				if (key.length() > 255) {
					errorData.put("referenceInformationKey18", "消耗品基准信息名18长度范围为0~255");
				}
				if (value.length() > 255) {
					errorData.put("referenceInformationValue18", "消耗品基准信息值18长度范围为0~255");
				}
			}

			// 19
			if (StringUtil.isBlank(curImportConsumable.getReferenceInformationKey19())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue19())) {
				errorData.put("referenceInformationKey19", "消耗品基准信息名19不能为空");
			} else if (!StringUtil.isBlank(curImportConsumable.getReferenceInformationKey19())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue19())) {
				String key = curImportConsumable.getReferenceInformationKey19().trim();
				String value = curImportConsumable.getReferenceInformationValue19().trim();

				if (key.length() > 255) {
					errorData.put("referenceInformationKey19", "消耗品基准信息名19长度范围为0~255");
				}
				if (value.length() > 255) {
					errorData.put("referenceInformationValue19", "消耗品基准信息值19长度范围为0~255");
				}
			}

			// 20
			if (StringUtil.isBlank(curImportConsumable.getReferenceInformationKey20())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue20())) {
				errorData.put("referenceInformationKey20", "消耗品基准信息名20不能为空");
			} else if (!StringUtil.isBlank(curImportConsumable.getReferenceInformationKey20())
					&& !StringUtil.isBlank(curImportConsumable.getReferenceInformationValue20())) {
				String key = curImportConsumable.getReferenceInformationKey20().trim();
				String value = curImportConsumable.getReferenceInformationValue20().trim();

				if (key.length() > 255) {
					errorData.put("referenceInformationKey20", "消耗品基准信息名20长度范围为0~255");
				}
				if (value.length() > 255) {
					errorData.put("referenceInformationValue20", "消耗品基准信息值20长度范围为0~255");
				}
			}

			if (!errorData.isEmpty()) {
				errorDatas.put(Integer.toString(i), errorData);
			}
		}

		if (!errorDatas.isEmpty()) {
			return errorDatas;
		}

		return null;
	}

	@Override
	public List<ConsumableDto> importConsumables(String warehouseId, String userId,
			List<CImportConsumableDetailModel> importConsumables) {
		// 获取仓库所有类型数据
		List<Type> dbTypes = typeDao.c_selectTypeByWarehouseId(warehouseId);

		// 获取仓库所有消耗品数据
		List<ConsumableDto> dbConsumables = consumableDao.c_selectByWarehouseId(warehouseId);

		// 消耗品新增集合
		List<CAddConsumableModel> addConsumables = new ArrayList<CAddConsumableModel>();

		// 消耗品更新集合
		List<CEditConsumableModel> updateConsumables = new ArrayList<CEditConsumableModel>();

		// 消耗品数量更新ID集合
		List<Map<String, Object>> updateConsumableCountIds = new ArrayList<Map<String, Object>>();

		// 结果集
		List<ConsumableDto> res = new ArrayList<ConsumableDto>();

		for (CImportConsumableDetailModel importConsumable : importConsumables) {
			boolean isAdd = true;
			String dbConsumableId = "";

			// 判断是新增还是更新
			for (ConsumableDto dbConsumable : dbConsumables) {
				if (importConsumable.getCode().trim().equals(dbConsumable.getCode())) {
					isAdd = false;
					dbConsumableId = dbConsumable.getId();

					// 数量更新了，要同步操作表
					if (importConsumable.getCount() != dbConsumable.getCount()) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("consumableId", dbConsumable.getId());
						// 出库
						if (importConsumable.getCount() < dbConsumable.getCount()) {
							map.put("type", ConsumableOperateTypeConstant.NORMAL_DELIVERY);
							map.put("count", dbConsumable.getCount() - importConsumable.getCount());
						}
						// 入库：默认为A类型
						else {
							map.put("type", ConsumableOperateTypeConstant.INBOUND_A);
							map.put("count", importConsumable.getCount() - dbConsumable.getCount());
						}

						updateConsumableCountIds.add(map);
					}
					break;
				}
			}

			if (isAdd) {
				CAddConsumableModel addConsumable = new CAddConsumableModel();

				addConsumable.setWarehouseId(warehouseId);

				String typeId = "";
				for (Type type : dbTypes) {
					if (importConsumable.getTypeName().trim().equals(type.getName())) {
						typeId = type.getId();
						break;
					}
				}
				addConsumable.setTypeId(typeId);

				addConsumable.setCode(importConsumable.getCode().trim());
				addConsumable.setName(importConsumable.getName().trim());
				addConsumable.setCount(importConsumable.getCount());
				addConsumable.setSafetyCount(importConsumable.getSafetyCount());
				addConsumable.setPrice(importConsumable.getPrice());

				if (!StringUtil.isBlank(importConsumable.getSummary())) {
					addConsumable.setSummary(importConsumable.getSummary().trim());
				}

				List<ReferenceInformationModel> referenceInformations = new ArrayList<ReferenceInformationModel>();
				// 1
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey1())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue1())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey1().trim(),
									importConsumable.getReferenceInformationValue1().trim()));
				}

				// 2
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey2())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue2())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey2().trim(),
									importConsumable.getReferenceInformationValue2().trim()));
				}

				// 3
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey3())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue3())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey3().trim(),
									importConsumable.getReferenceInformationValue3().trim()));
				}

				// 4
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey4())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue4())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey4().trim(),
									importConsumable.getReferenceInformationValue4().trim()));
				}

				// 5
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey5())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue5())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey5().trim(),
									importConsumable.getReferenceInformationValue5().trim()));
				}

				// 6
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey6())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue6())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey6().trim(),
									importConsumable.getReferenceInformationValue6().trim()));
				}

				// 7
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey7())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue7())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey7().trim(),
									importConsumable.getReferenceInformationValue7().trim()));
				}

				// 8
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey8())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue8())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey8().trim(),
									importConsumable.getReferenceInformationValue8().trim()));
				}

				// 9
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey9())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue9())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey9().trim(),
									importConsumable.getReferenceInformationValue9().trim()));
				}

				// 10
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey10())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue10())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey10().trim(),
									importConsumable.getReferenceInformationValue10().trim()));
				}

				// 11
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey11())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue11())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey11().trim(),
									importConsumable.getReferenceInformationValue11().trim()));
				}

				// 12
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey12())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue12())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey12().trim(),
									importConsumable.getReferenceInformationValue12().trim()));
				}

				// 13
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey13())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue13())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey13().trim(),
									importConsumable.getReferenceInformationValue13().trim()));
				}

				// 14
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey14())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue14())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey14().trim(),
									importConsumable.getReferenceInformationValue14().trim()));
				}

				// 15
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey15())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue15())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey15().trim(),
									importConsumable.getReferenceInformationValue15().trim()));
				}

				// 16
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey16())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue16())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey16().trim(),
									importConsumable.getReferenceInformationValue16().trim()));
				}

				// 17
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey17())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue17())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey17().trim(),
									importConsumable.getReferenceInformationValue17().trim()));
				}

				// 18
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey18())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue18())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey18().trim(),
									importConsumable.getReferenceInformationValue18().trim()));
				}

				// 19
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey19())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue19())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey19().trim(),
									importConsumable.getReferenceInformationValue19().trim()));
				}

				// 20
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey20())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue20())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey20().trim(),
									importConsumable.getReferenceInformationValue20().trim()));
				}

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("referenceInformations", referenceInformations);

				addConsumable.setReferenceInformations(JSONObject.parseObject(JSONObject.toJSONString(map)));

				addConsumables.add(addConsumable);
			} else {
				CEditConsumableModel updateConsumable = new CEditConsumableModel();
				updateConsumable.setWarehouseId(warehouseId);

				String typeId = "";
				for (Type type : dbTypes) {
					if (importConsumable.getTypeName().trim().equals(type.getName())) {
						typeId = type.getId();
						break;
					}
				}
				updateConsumable.setTypeId(typeId);
				updateConsumable.setId(dbConsumableId);

				updateConsumable.setCode(importConsumable.getCode().trim());
				updateConsumable.setName(importConsumable.getName().trim());
				updateConsumable.setCount(importConsumable.getCount());
				updateConsumable.setSafetyCount(importConsumable.getSafetyCount());
				updateConsumable.setPrice(importConsumable.getPrice());

				if (!StringUtil.isBlank(importConsumable.getSummary())) {
					updateConsumable.setSummary(importConsumable.getSummary().trim());
				}

				List<ReferenceInformationModel> referenceInformations = new ArrayList<ReferenceInformationModel>();
				// 1
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey1())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue1())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey1().trim(),
									importConsumable.getReferenceInformationValue1().trim()));
				}

				// 2
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey2())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue2())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey2().trim(),
									importConsumable.getReferenceInformationValue2().trim()));
				}

				// 3
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey3())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue3())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey3().trim(),
									importConsumable.getReferenceInformationValue3().trim()));
				}

				// 4
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey4())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue4())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey4().trim(),
									importConsumable.getReferenceInformationValue4().trim()));
				}

				// 5
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey5())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue5())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey5().trim(),
									importConsumable.getReferenceInformationValue5().trim()));
				}

				// 6
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey6())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue6())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey6().trim(),
									importConsumable.getReferenceInformationValue6().trim()));
				}

				// 7
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey7())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue7())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey7().trim(),
									importConsumable.getReferenceInformationValue7().trim()));
				}

				// 8
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey8())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue8())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey8().trim(),
									importConsumable.getReferenceInformationValue8().trim()));
				}

				// 9
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey9())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue9())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey9().trim(),
									importConsumable.getReferenceInformationValue9().trim()));
				}

				// 10
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey10())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue10())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey10().trim(),
									importConsumable.getReferenceInformationValue10().trim()));
				}

				// 11
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey11())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue11())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey11().trim(),
									importConsumable.getReferenceInformationValue11().trim()));
				}

				// 12
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey12())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue12())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey12().trim(),
									importConsumable.getReferenceInformationValue12().trim()));
				}

				// 13
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey13())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue13())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey13().trim(),
									importConsumable.getReferenceInformationValue13().trim()));
				}

				// 14
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey14())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue14())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey14().trim(),
									importConsumable.getReferenceInformationValue14().trim()));
				}

				// 15
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey15())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue15())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey15().trim(),
									importConsumable.getReferenceInformationValue15().trim()));
				}

				// 16
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey16())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue16())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey16().trim(),
									importConsumable.getReferenceInformationValue16().trim()));
				}

				// 17
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey17())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue17())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey17().trim(),
									importConsumable.getReferenceInformationValue17().trim()));
				}

				// 18
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey18())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue18())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey18().trim(),
									importConsumable.getReferenceInformationValue18().trim()));
				}

				// 19
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey19())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue19())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey19().trim(),
									importConsumable.getReferenceInformationValue19().trim()));
				}

				// 20
				if (!StringUtil.isBlank(importConsumable.getReferenceInformationKey20())
						&& !StringUtil.isBlank(importConsumable.getReferenceInformationValue20())) {
					referenceInformations
							.add(new ReferenceInformationModel(importConsumable.getReferenceInformationKey20().trim(),
									importConsumable.getReferenceInformationValue20().trim()));
				}

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("referenceInformations", referenceInformations);

				updateConsumable.setReferenceInformations(JSONObject.parseObject(JSONObject.toJSONString(map)));

				updateConsumables.add(updateConsumable);
			}
		}

		// 保存操作履历
		ConsumableOperationHistory consumableOperationHistory = new ConsumableOperationHistory(RandomUtil.genarateId(),
				warehouseId, userId, new Date());
		List<ConsumableOperationDetail> consumableOperationDetails = new ArrayList<ConsumableOperationDetail>();

		// 批量新增消耗品
		if (!addConsumables.isEmpty()) {
			for (CAddConsumableModel addConsumable : addConsumables) {
				// 生成消耗品实体
				Consumable consumable = addConsumable.createConsumable();

				consumableDao.insertConsumable(consumable);

				if (addConsumable.getReferenceInformations() != null
						&& !addConsumable.getReferenceInformations().isEmpty()) {
					// JSON转JAVA
					List<ReferenceInformationModel> referenceInformationList = JSONObject.parseArray(
							addConsumable.getReferenceInformations().getString("referenceInformations"),
							ReferenceInformationModel.class);

					// 生成消耗品基准信息
					if (referenceInformationList != null && !referenceInformationList.isEmpty()) {
						List<ConsumableReferenceInformation> referenceInformations = new ArrayList<ConsumableReferenceInformation>();
						int displayOrder = 1;
						for (ReferenceInformationModel referenceInformation : referenceInformationList) {
							String key = referenceInformation.getKey();
							String value = referenceInformation.getValue();

							if (!StringUtil.isBlank(key) || !StringUtil.isBlank(value)) {
								referenceInformations.add(new ConsumableReferenceInformation(RandomUtil.genarateId(),
										consumable.getId(), referenceInformation.getKey(),
										referenceInformation.getValue(), displayOrder++));
							}

						}
						consumableReferenceInformationDao.insertReferenceInformations(referenceInformations);
					}

					// 保存批次
					ConsumableInboundBatch consumableInboundBatch = new ConsumableInboundBatch(RandomUtil.genarateId(),
							consumable.getId(), consumable.getCount(), consumable.getPrice(), new Date());
					consumableInboundBatchDao.insertConsumableInboundBatch(consumableInboundBatch);
				}

				// 生成操作履历详情
				consumableOperationDetails.add(new ConsumableOperationDetail(RandomUtil.genarateId(),
						consumableOperationHistory.getId(), consumable.getId(),
						ConsumableOperateTypeConstant.ADD_CONSUMABLE, consumable.getCount(), ""));
			}
		}

		// 批量更新消耗品
		if (!updateConsumables.isEmpty()) {
			// 需要保存操作履历
			if (!updateConsumableCountIds.isEmpty()) {
				for (Map<String, Object> map : updateConsumableCountIds) {
					String consumableId = (String) map.get("consumableId");
					int type = (int) map.get("type");
					int count = (int) map.get("count");

					// 出库或者入库其实际在库数据需要更新
					switch (type) {
					// 出库
					case ConsumableOperateTypeConstant.NORMAL_DELIVERY:
						// 出库数
						int updateCount = count;
						// 消耗品出库执行先入先出原则更新批次表
						List<ConsumableInboundBatch> consumableInboundBatchs = consumableInboundBatchDao
								.selectByConsumableId(consumableId);
						for (ConsumableInboundBatch consumableInboundBatch : consumableInboundBatchs) {
							if (updateCount <= 0) {
								break;
							}

							if (consumableInboundBatch.getCount() != 0) {
								if (consumableInboundBatch.getCount() <= updateCount) {
									updateCount -= consumableInboundBatch.getCount();
									consumableInboundBatch.setCount(0);
									consumableInboundBatchDao.updateCount(consumableInboundBatch);
								} else {
									consumableInboundBatch.setCount(consumableInboundBatch.getCount() - updateCount);
									consumableInboundBatchDao.updateCount(consumableInboundBatch);
									updateCount = 0;
								}
							}
						}
						break;
					// 入库
					case ConsumableOperateTypeConstant.INBOUND_A:
						for (CEditConsumableModel updateConsumable : updateConsumables) {
							if (consumableId.equals(updateConsumable.getId())) {
								ConsumableInboundBatch consumableInboundBatch = new ConsumableInboundBatch(
										RandomUtil.genarateId(), updateConsumable.getId(), count, updateConsumable.getPrice(),
										new Date());
								consumableInboundBatchDao.insertConsumableInboundBatch(consumableInboundBatch);
								break;
							}
						}
						break;
					}

					// 生成操作履历详情
					consumableOperationDetails.add(new ConsumableOperationDetail(RandomUtil.genarateId(),
							consumableOperationHistory.getId(), consumableId, type, count, ""));
				}
			}

			// 循环更新
			for (CEditConsumableModel updateConsumable : updateConsumables) {
				editConsumable(updateConsumable);
			}
		}

		// 将操作记录保存至数据库
		if (!addConsumables.isEmpty() || !updateConsumableCountIds.isEmpty()) {
			consumableOperationHistoryDao.insertConsumableOperationHistory(consumableOperationHistory);
			for (ConsumableOperationDetail consumableOperationDetail : consumableOperationDetails) {
				consumableOperationDetailDao.insertConsumableOperationDetail(consumableOperationDetail);
			}
		}

		return res;
	}

	@Override
	public ExcelDataModel getExportConsumablesData(CSearchConditionModel model) {
		// 获取仓库数据
		Warehouse warehouse = warehouseDao.selectWarehouseById(model.getWarehouseId());

		List<ConsumableDto> consumables = getAllConsumablesBySearchCondition(model);

		ExcelDataModel excelData = new ExcelDataModel();

		// 设置文件名
		Date now = new Date();
		String fileName = warehouse.getName() + "_消耗品_" + now.getTime();
		excelData.setName(fileName);

		// 设置表头
		List<String> heads = new ArrayList<String>();

		heads.add("Code");
		heads.add("名称");
		heads.add("类型");
		heads.add("数量");
		heads.add("安全在工数");
		heads.add("未税单价");
		heads.add("库存金额");
		heads.add("简介");
		heads.add("入库日期");

		int referenceInformationMaxCount = 0;
		for (ConsumableDto consumable : consumables) {
			if (consumable.getReferenceInformations() != null && !consumable.getReferenceInformations().isEmpty()) {
				if (consumable.getReferenceInformations().size() > referenceInformationMaxCount) {
					referenceInformationMaxCount = consumable.getReferenceInformations().size();
				}
			}
		}

		for (int i = 1; i <= referenceInformationMaxCount; i++) {
			heads.add("基准信息名" + i);
			heads.add("基准信息值" + i);
		}

		excelData.setTitles(heads);

		// 设置数据
		List<List<Object>> templateDatas = new ArrayList<>();

		for (ConsumableDto consumable : consumables) {
			List<Object> templateData = new ArrayList<Object>();
			templateData.add(consumable.getCode());
			templateData.add(consumable.getName());
			templateData.add(consumable.getTypeName());
			templateData.add(consumable.getCount());
			templateData.add(consumable.getSafetyCount());
			templateData.add(consumable.getPrice());
			templateData.add(consumable.getTotalPrice());
			templateData.add(consumable.getSummary());
			templateData.add(consumable.getCreateTime());

			for (ConsumableReferenceInformation referenceInformation : consumable.getReferenceInformations()) {
				templateData.add(referenceInformation.getReferenceKey());
				templateData.add(referenceInformation.getReferenceValue());
			}

			templateDatas.add(templateData);
		}

		excelData.setRows(templateDatas);

		return excelData;
	}

	@Override
	public ConsumableDto outbound(String userId, OutboundModel model) {
		// 构造出库实体
		ConsumableInfoDto consumable = new ConsumableInfoDto();
		consumable.setId(model.getId());
		consumable.setWarehouseId(model.getWarehouseId());
		consumable.setCount(model.getOutboundCount());

		// 数据库执行出库操作
		consumableDao.outbound(consumable);

		// 出库数
		int updateCount = consumable.getCount();
		// 消耗品出库执行先入先出原则更新批次表
		List<ConsumableInboundBatch> consumableInboundBatchs = consumableInboundBatchDao
				.selectByConsumableId(consumable.getId());
		for (ConsumableInboundBatch consumableInboundBatch : consumableInboundBatchs) {
			if (updateCount <= 0) {
				break;
			}

			if (consumableInboundBatch.getCount() != 0) {
				if (consumableInboundBatch.getCount() <= updateCount) {
					updateCount -= consumableInboundBatch.getCount();
					consumableInboundBatch.setCount(0);
					consumableInboundBatchDao.updateCount(consumableInboundBatch);
				} else {
					consumableInboundBatch.setCount(consumableInboundBatch.getCount() - updateCount);
					consumableInboundBatchDao.updateCount(consumableInboundBatch);
					updateCount = 0;
				}
			}
		}

		// 保存操作记录
		ConsumableOperationHistory consumableOperationHistory = new ConsumableOperationHistory(RandomUtil.genarateId(),
				consumable.getWarehouseId(), userId, new Date());
		ConsumableOperationDetail consumableOperationDetail = new ConsumableOperationDetail(RandomUtil.genarateId(),
				consumableOperationHistory.getId(), model.getId(), model.getType(), model.getOutboundCount(),
				model.getRemarks());

		// 将操作记录保存至数据库
		consumableOperationHistoryDao.insertConsumableOperationHistory(consumableOperationHistory);
		consumableOperationDetailDao.insertConsumableOperationDetail(consumableOperationDetail);

		ConsumableInfoDto selectModel = new ConsumableInfoDto();
		selectModel.setWarehouseId(consumable.getWarehouseId());
		selectModel.setId(consumable.getId());

		return consumableDao.selectByWarehouseIdAndConsumableId(selectModel);
	}

	@Override
	public ConsumableDto inbound(String userId, InboundModel model) {
		// 构造入库实体
		ConsumableInfoDto consumable = new ConsumableInfoDto();
		consumable.setId(model.getId());
		consumable.setWarehouseId(model.getWarehouseId());
		consumable.setCount(model.getInboundCount());
		consumable.setPrice(model.getInboundPrice());

		// 数据库执行入库操作
		consumableDao.inbound(consumable);

		// 保存批次
		ConsumableInboundBatch consumableInboundBatch = new ConsumableInboundBatch(RandomUtil.genarateId(),
				consumable.getId(), consumable.getCount(), consumable.getPrice(), new Date());
		consumableInboundBatchDao.insertConsumableInboundBatch(consumableInboundBatch);

		// 保存操作记录
		ConsumableOperationHistory consumableOperationHistory = new ConsumableOperationHistory(RandomUtil.genarateId(),
				consumable.getWarehouseId(), userId, new Date());
		ConsumableOperationDetail consumableOperationDetail = new ConsumableOperationDetail(RandomUtil.genarateId(),
				consumableOperationHistory.getId(), model.getId(), model.getType(), model.getInboundCount(),
				model.getRemarks());

		// 将操作记录保存至数据库
		consumableOperationHistoryDao.insertConsumableOperationHistory(consumableOperationHistory);
		consumableOperationDetailDao.insertConsumableOperationDetail(consumableOperationDetail);

		ConsumableInfoDto selectModel = new ConsumableInfoDto();
		selectModel.setWarehouseId(consumable.getWarehouseId());
		selectModel.setId(consumable.getId());

		return consumableDao.selectByWarehouseIdAndConsumableId(selectModel);
	}

}
