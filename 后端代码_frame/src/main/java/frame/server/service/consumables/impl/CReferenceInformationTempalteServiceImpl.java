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

import frame.server.dao.ReferenceInformationTemplateDao;
import frame.server.dao.ReferenceInformationTemplateDetailDao;
import frame.server.dao.WarehouseDao;
import frame.server.domain.ReferenceInformationTemplate;
import frame.server.domain.ReferenceInformationTemplateDetail;
import frame.server.domain.Warehouse;
import frame.server.dto.ReferenceInformationTemplateDto;
import frame.server.dto.ReferenceInformationTemplateUpdateDto;
import frame.server.service.consumables.CReferenceInformationTempalteService;
import frame.server.utils.RandomUtil;
import frame.server.utils.StringUtil;
import frame.server.vo.ExcelDataModel;
import frame.server.vo.Page;
import frame.server.vo.consumables.ReferenceInfomationTemplateDetailModel;
import frame.server.vo.consumables.CReferenceInformationController.CAddReferenceInformationTemplateModel;
import frame.server.vo.consumables.CReferenceInformationController.CEditReferenceInformationTemplateModel;
import frame.server.vo.consumables.CReferenceInformationController.CImportReferenceInfomationTempalteDetailModel;

@Service("cReferenceInformationTempalteService")
public class CReferenceInformationTempalteServiceImpl implements CReferenceInformationTempalteService {

	@Autowired
	private ReferenceInformationTemplateDao referenceInformationTemplateDao;

	@Autowired
	private ReferenceInformationTemplateDetailDao referenceInformationTemplateDetailDao;

	@Autowired
	private WarehouseDao warehouseDao;

	@Override
	public List<ReferenceInformationTemplateDto> getAll(String warehouseId) {
		return referenceInformationTemplateDao.c_selectReferenceInformationTemplateDtoByWarehouseId(warehouseId);
	}

	@Override
	public PageInfo<ReferenceInformationTemplateDto> getReferenceInformationTemplates(Page page) {
		// 启用分页
		PageHelper.startPage(page.getPageNum(), page.getPageSize());

		PageInfo<ReferenceInformationTemplateDto> pageInfo = null;

		// 获取检索条件
		JSONObject searchCondition = page.getSearchCondition();
		String warehouseId = searchCondition.getString("warehouseId");
		String searchStr = searchCondition.getString("searchStr");

		// 根据检索条件检索数据
		if (StringUtil.isBlank(searchStr)) {
			pageInfo = new PageInfo<ReferenceInformationTemplateDto>(
					referenceInformationTemplateDao.c_selectReferenceInformationTemplateDtoByWarehouseId(warehouseId));
		} else {
			ReferenceInformationTemplate referenceInformationTemplate = new ReferenceInformationTemplate();
			referenceInformationTemplate.setWarehouseId(warehouseId);
			referenceInformationTemplate.setName(searchStr);

			pageInfo = new PageInfo<ReferenceInformationTemplateDto>(
					referenceInformationTemplateDao.c_selectByWarehouseIdAndLikeName(referenceInformationTemplate));
		}

		return pageInfo;
	}

	@Override
	public ReferenceInformationTemplateDto getReferenceInformationTemplate(String warehouseId, String tempalteId) {
		ReferenceInformationTemplate referenceInformationTemplate = new ReferenceInformationTemplate();
		referenceInformationTemplate.setWarehouseId(warehouseId);
		referenceInformationTemplate.setId(tempalteId);
		
		return referenceInformationTemplateDao.c_selectByWarehouseIdAndReferenceInformationTemplateId(referenceInformationTemplate);
	}
	
	@Override
	public boolean checkReferenceInformationTemplateId(String warehouseId, String tempalteId) {
		ReferenceInformationTemplate referenceInformationTemplate = new ReferenceInformationTemplate();
		referenceInformationTemplate.setWarehouseId(warehouseId);
		referenceInformationTemplate.setId(tempalteId);

		return referenceInformationTemplateDao
				.c_selectCountByWarehouseIdAndReferenceInformationTemplateIdAndNotIsSystem(referenceInformationTemplate) > 0;
	}

	@Override
	public boolean checkReferenceInformationTemplateNameIsExist(String warehouseId, String tempalteName) {
		ReferenceInformationTemplate referenceInformationTemplate = new ReferenceInformationTemplate();
		referenceInformationTemplate.setWarehouseId(warehouseId);
		referenceInformationTemplate.setName(tempalteName);

		return referenceInformationTemplateDao
				.c_selectCountByWarehouseIdAndReferenceInformationTemplateName(referenceInformationTemplate) > 0;
	}

	@Override
	public ReferenceInformationTemplateDto addReferenceInformationTemplate(
			CAddReferenceInformationTemplateModel model) {
		// 构造基准信息模板实体
		ReferenceInformationTemplate referenceInformationTemplate = new ReferenceInformationTemplate(
				RandomUtil.genarateId(), model.getWarehouseId(), model.getName().trim(), model.getRemarks().trim(),
				new Date(), false, false);

		// 构造基准信息详细
		// 解析json格式的基准信息详细字符串
		List<ReferenceInfomationTemplateDetailModel> referenceInformationList = JSONObject
				.parseArray(model.getDetails().getString("details"), ReferenceInfomationTemplateDetailModel.class);

		// 生成消耗品基准信息
		List<ReferenceInformationTemplateDetail> referenceInformationTemplateDetails = new ArrayList<ReferenceInformationTemplateDetail>();
		if (referenceInformationList != null && !referenceInformationList.isEmpty()) {
			int i = 1;
			for (ReferenceInfomationTemplateDetailModel referenceInformation : referenceInformationList) {
				if (!StringUtil.isBlank(referenceInformation.getKey().trim())) {
					referenceInformationTemplateDetails
							.add(new ReferenceInformationTemplateDetail(RandomUtil.genarateId(),
									referenceInformationTemplate.getId(), referenceInformation.getKey().trim(), i++));
				}

			}
		}

		// 将数据保存至数据库中
		referenceInformationTemplateDao.insertReferenceInformationTemplate(referenceInformationTemplate);
		if (!referenceInformationTemplateDetails.isEmpty()) {
			referenceInformationTemplateDetailDao
					.insertReferenceInformationTemplateDetails(referenceInformationTemplateDetails);
		}

		return referenceInformationTemplateDao
				.c_selectByWarehouseIdAndReferenceInformationTemplateId(referenceInformationTemplate);
	}

	@Override
	public ReferenceInformationTemplateDto updateReferenceInformationTemplate(
			CEditReferenceInformationTemplateModel model) {
		// 构造基准信息模板修改实体
		ReferenceInformationTemplateUpdateDto referenceInformationTemplateInfo = new ReferenceInformationTemplateUpdateDto(
				model.getId(), model.getName().trim(), model.getRemarks().trim());

		// 构造基准信息详细
		// 解析json格式的基准信息详细字符串
		List<ReferenceInfomationTemplateDetailModel> referenceInformationList = JSONObject
				.parseArray(model.getDetails().getString("details"), ReferenceInfomationTemplateDetailModel.class);

		// 生成消耗品基准信息
		List<ReferenceInformationTemplateDetail> referenceInformationTemplateDetails = new ArrayList<ReferenceInformationTemplateDetail>();
		if (referenceInformationList != null && !referenceInformationList.isEmpty()) {
			int i = 1;
			for (ReferenceInfomationTemplateDetailModel referenceInformation : referenceInformationList) {
				if (!StringUtil.isBlank(referenceInformation.getKey().trim())) {
					referenceInformationTemplateDetails.add(new ReferenceInformationTemplateDetail(
							RandomUtil.genarateId(), referenceInformationTemplateInfo.getId(),
							referenceInformation.getKey().trim(), i++));
				}

			}
		}

		// 将更新数据保存至数据库中
		referenceInformationTemplateDao.update(referenceInformationTemplateInfo);
		referenceInformationTemplateDetailDao
				.deleteByReferenceInformationTemplateId(referenceInformationTemplateInfo.getId());
		if (!referenceInformationTemplateDetails.isEmpty()) {
			referenceInformationTemplateDetailDao
					.insertReferenceInformationTemplateDetails(referenceInformationTemplateDetails);
		}

		ReferenceInformationTemplate referenceInformationTemplate = new ReferenceInformationTemplate();
		referenceInformationTemplate.setWarehouseId(model.getWarehouseId());
		referenceInformationTemplate.setId(referenceInformationTemplateInfo.getId());

		return referenceInformationTemplateDao
				.c_selectByWarehouseIdAndReferenceInformationTemplateId(referenceInformationTemplate);
	}

	@Override
	public void deleteReferenceInformationTemplate(String warehouseId, String tempalteId) {
		ReferenceInformationTemplate referenceInformationTemplate = new ReferenceInformationTemplate();
		referenceInformationTemplate.setWarehouseId(warehouseId);
		referenceInformationTemplate.setId(tempalteId);

		referenceInformationTemplateDao
				.deleteByWarehouseIdAndReferenceInformationTemplateId(referenceInformationTemplate);
	}

	@Override
	public JSONObject checkImportReferenceInformationTemplates(
			List<CImportReferenceInfomationTempalteDetailModel> importReferenceInformationTemplates) {
		JSONObject errorDatas = new JSONObject();

		// 循环校验
		for (int i = 0; i < importReferenceInformationTemplates.size(); i++) {
			JSONObject errorData = new JSONObject();

			CImportReferenceInfomationTempalteDetailModel detail = importReferenceInformationTemplates.get(i);

			// 校验名称
			if (StringUtil.isBlank(detail.getName())) {
				errorData.put("name", "名称不能为空");
			} else {
				String name = detail.getName().trim();
				if (name.length() > 20) {
					errorData.put("name", "名称长度范围为1~20");
				}
			}

			// 校验备注
			if (!StringUtil.isBlank(detail.getRemarks())) {
				String name = detail.getRemarks().trim();
				if (name.length() > 255) {
					errorData.put("remarks", "备注长度范围为0~255");
				}
			}

			// 校验基准信息名
			if (!StringUtil.isBlank(detail.getReferenceInformationKey1())) {
				String key = detail.getReferenceInformationKey1().trim();
				if (key.length() > 255) {
					errorData.put("referenceInformationKey1", "基准信息名长度范围为1~255");
				}
			}
			if (!StringUtil.isBlank(detail.getReferenceInformationKey2())) {
				String key = detail.getReferenceInformationKey2().trim();
				if (key.length() > 255) {
					errorData.put("referenceInformationKey2", "基准信息名长度范围为1~255");
				}
			}
			if (!StringUtil.isBlank(detail.getReferenceInformationKey3())) {
				String key = detail.getReferenceInformationKey3().trim();
				if (key.length() > 255) {
					errorData.put("referenceInformationKey3", "基准信息名长度范围为1~255");
				}
			}
			if (!StringUtil.isBlank(detail.getReferenceInformationKey4())) {
				String key = detail.getReferenceInformationKey4().trim();
				if (key.length() > 255) {
					errorData.put("referenceInformationKey4", "基准信息名长度范围为1~255");
				}
			}
			if (!StringUtil.isBlank(detail.getReferenceInformationKey5())) {
				String key = detail.getReferenceInformationKey5().trim();
				if (key.length() > 255) {
					errorData.put("referenceInformationKey5", "基准信息名长度范围为1~255");
				}
			}
			if (!StringUtil.isBlank(detail.getReferenceInformationKey6())) {
				String key = detail.getReferenceInformationKey6().trim();
				if (key.length() > 255) {
					errorData.put("referenceInformationKey6", "基准信息名长度范围为1~255");
				}
			}
			if (!StringUtil.isBlank(detail.getReferenceInformationKey7())) {
				String key = detail.getReferenceInformationKey7().trim();
				if (key.length() > 255) {
					errorData.put("referenceInformationKey7", "基准信息名长度范围为1~255");
				}
			}
			if (!StringUtil.isBlank(detail.getReferenceInformationKey8())) {
				String key = detail.getReferenceInformationKey8().trim();
				if (key.length() > 255) {
					errorData.put("referenceInformationKey8", "基准信息名长度范围为1~255");
				}
			}
			if (!StringUtil.isBlank(detail.getReferenceInformationKey9())) {
				String key = detail.getReferenceInformationKey9().trim();
				if (key.length() > 255) {
					errorData.put("referenceInformationKey9", "基准信息名长度范围为1~255");
				}
			}
			if (!StringUtil.isBlank(detail.getReferenceInformationKey10())) {
				String key = detail.getReferenceInformationKey10().trim();
				if (key.length() > 255) {
					errorData.put("referenceInformationKey10", "基准信息名长度范围为1~255");
				}
			}
			if (!StringUtil.isBlank(detail.getReferenceInformationKey11())) {
				String key = detail.getReferenceInformationKey11().trim();
				if (key.length() > 255) {
					errorData.put("referenceInformationKey11", "基准信息名长度范围为1~255");
				}
			}
			if (!StringUtil.isBlank(detail.getReferenceInformationKey12())) {
				String key = detail.getReferenceInformationKey12().trim();
				if (key.length() > 255) {
					errorData.put("referenceInformationKey12", "基准信息名长度范围为1~255");
				}
			}
			if (!StringUtil.isBlank(detail.getReferenceInformationKey13())) {
				String key = detail.getReferenceInformationKey13().trim();
				if (key.length() > 255) {
					errorData.put("referenceInformationKey13", "基准信息名长度范围为1~255");
				}
			}
			if (!StringUtil.isBlank(detail.getReferenceInformationKey14())) {
				String key = detail.getReferenceInformationKey14().trim();
				if (key.length() > 255) {
					errorData.put("referenceInformationKey14", "基准信息名长度范围为1~255");
				}
			}
			if (!StringUtil.isBlank(detail.getReferenceInformationKey15())) {
				String key = detail.getReferenceInformationKey15().trim();
				if (key.length() > 255) {
					errorData.put("referenceInformationKey15", "基准信息名长度范围为1~255");
				}
			}
			if (!StringUtil.isBlank(detail.getReferenceInformationKey16())) {
				String key = detail.getReferenceInformationKey16().trim();
				if (key.length() > 255) {
					errorData.put("referenceInformationKey16", "基准信息名长度范围为1~255");
				}
			}
			if (!StringUtil.isBlank(detail.getReferenceInformationKey17())) {
				String key = detail.getReferenceInformationKey17().trim();
				if (key.length() > 255) {
					errorData.put("referenceInformationKey17", "基准信息名长度范围为1~255");
				}
			}
			if (!StringUtil.isBlank(detail.getReferenceInformationKey18())) {
				String key = detail.getReferenceInformationKey18().trim();
				if (key.length() > 255) {
					errorData.put("referenceInformationKey18", "基准信息名长度范围为1~255");
				}
			}
			if (!StringUtil.isBlank(detail.getReferenceInformationKey19())) {
				String key = detail.getReferenceInformationKey19().trim();
				if (key.length() > 255) {
					errorData.put("referenceInformationKey19", "基准信息名长度范围为1~255");
				}
			}
			if (!StringUtil.isBlank(detail.getReferenceInformationKey20())) {
				String key = detail.getReferenceInformationKey20().trim();
				if (key.length() > 255) {
					errorData.put("referenceInformationKey20", "基准信息名长度范围为1~255");
				}
			}

			if (!errorData.isEmpty()) {
				errorDatas.put(Integer.toString(i), errorData);
			}
		}

		if (!errorDatas.isEmpty()) {
			return errorDatas;
		}

		// 检查文件内基准信息模板名称是否重复
		for (int i = 0; i < importReferenceInformationTemplates.size(); i++) {
			JSONObject errorData = new JSONObject();
			for (int j = 0; j < importReferenceInformationTemplates.size(); j++) {
				if (i != j) {
					if (importReferenceInformationTemplates.get(i).getName()
							.equals(importReferenceInformationTemplates.get(j).getName())) {
						errorData.put("name", "名称重复");
					}
					if (!errorData.isEmpty()) {
						break;
					}
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
	public List<ReferenceInformationTemplateDto> importTempaltes(String warehouseId, String userId,
			List<CImportReferenceInfomationTempalteDetailModel> importReferenceInformationTemplates) {
		// 获取当前仓库全部模板
		List<ReferenceInformationTemplateDto> dbTemplates = referenceInformationTemplateDao
				.c_selectReferenceInformationTemplateDtoByWarehouseId(warehouseId);

		// 新增模板
		List<CAddReferenceInformationTemplateModel> addTempates = new ArrayList<CAddReferenceInformationTemplateModel>();

		// 更新模板
		List<CEditReferenceInformationTemplateModel> updateTemplates = new ArrayList<CEditReferenceInformationTemplateModel>();

		for (CImportReferenceInfomationTempalteDetailModel importReferenceInfomationTempalte : importReferenceInformationTemplates) {
			boolean isAdd = true;
			String dbTemplateId = "";

			for (ReferenceInformationTemplateDto dbTempalte : dbTemplates) {
				if (dbTempalte.getIsSystem()) {
					continue;
				}

				if (importReferenceInfomationTempalte.getName().trim().equals(dbTempalte.getName())) {
					isAdd = false;
					dbTemplateId = dbTempalte.getId();
				}
			}

			if (isAdd) {
				CAddReferenceInformationTemplateModel addTempalte = new CAddReferenceInformationTemplateModel();
				addTempalte.setWarehouseId(warehouseId);
				addTempalte.setName(importReferenceInfomationTempalte.getName().trim());

				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getRemarks())) {
					addTempalte.setRemarks(importReferenceInfomationTempalte.getRemarks().trim());
				}

				List<ReferenceInfomationTemplateDetailModel> templateDetail = new ArrayList<ReferenceInfomationTemplateDetailModel>();
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey1())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey1().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey2())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey2().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey3())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey3().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey4())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey4().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey5())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey5().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey6())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey6().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey7())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey7().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey8())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey8().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey9())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey9().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey10())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey10().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey11())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey11().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey12())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey12().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey13())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey13().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey14())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey14().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey15())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey15().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey16())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey16().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey17())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey17().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey18())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey18().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey19())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey19().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey20())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey20().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("details", templateDetail);

				addTempalte.setDetails(JSONObject.parseObject(JSONObject.toJSONString(map)));

				addTempates.add(addTempalte);
			} else {
				CEditReferenceInformationTemplateModel updateTempalte = new CEditReferenceInformationTemplateModel();
				updateTempalte.setId(dbTemplateId);
				updateTempalte.setWarehouseId(warehouseId);
				updateTempalte.setName(importReferenceInfomationTempalte.getName().trim());

				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getRemarks())) {
					updateTempalte.setRemarks(importReferenceInfomationTempalte.getRemarks().trim());
				}

				List<ReferenceInfomationTemplateDetailModel> templateDetail = new ArrayList<ReferenceInfomationTemplateDetailModel>();
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey1())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey1().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey2())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey2().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey3())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey3().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey4())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey4().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey5())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey5().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey6())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey6().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey7())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey7().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey8())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey8().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey9())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey9().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey10())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey10().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey11())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey11().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey12())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey12().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey13())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey13().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey14())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey14().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey15())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey15().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey16())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey16().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey17())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey17().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey18())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey18().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey19())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey19().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}
				if (!StringUtil.isBlank(importReferenceInfomationTempalte.getReferenceInformationKey20())) {
					String key = importReferenceInfomationTempalte.getReferenceInformationKey20().trim();
					if (key.length() < 255) {
						templateDetail.add(new ReferenceInfomationTemplateDetailModel(key));
					}
				}

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("details", templateDetail);

				updateTempalte.setDetails(JSONObject.parseObject(JSONObject.toJSONString(map)));

				updateTemplates.add(updateTempalte);
			}
		}

		// 结果集
		List<ReferenceInformationTemplateDto> res = new ArrayList<ReferenceInformationTemplateDto>();

		if (!addTempates.isEmpty()) {
			for (CAddReferenceInformationTemplateModel addTempate : addTempates) {
				res.add(addReferenceInformationTemplate(addTempate));
			}
		}

		if (!updateTemplates.isEmpty()) {
			for (CEditReferenceInformationTemplateModel updateTempate : updateTemplates) {
				updateReferenceInformationTemplate(updateTempate);
			}
		}

		return res;
	}

	@Override
	public ExcelDataModel getImportTemplateData(String warehouseId) {
		// 获取仓库数据
		Warehouse warehouse = warehouseDao.selectWarehouseById(warehouseId);

		// 获取系统内置基准信息模板
		List<ReferenceInformationTemplateDto> templates = referenceInformationTemplateDao
				.c_selectSystemReferenceInformationTemplateDtoByWarehouseId();

		ExcelDataModel excelData = new ExcelDataModel();

		// 设置文件名
		Date now = new Date();
		String fileName = warehouse.getName() + "_基准信息模板_样本_" + now.getTime();
		excelData.setName(fileName);

		// 设置表头
		List<String> heads = new ArrayList<String>();

		heads.add("名称");
		heads.add("备注");

		int templateMaxCount = 0;
		for (ReferenceInformationTemplateDto template : templates) {
			if (template.getDetails() != null && !template.getDetails().isEmpty()) {
				if (template.getDetails().size() > templateMaxCount) {
					templateMaxCount = template.getDetails().size();
				}
			}
		}

		for (int i = 1; i <= templateMaxCount; i++) {
			heads.add("基准信息名" + i);
		}

		excelData.setTitles(heads);

		// 设置数据
		List<List<Object>> templateDatas = new ArrayList<>();

		if (templates != null) {
			for (ReferenceInformationTemplateDto template : templates) {
				List<Object> templateData = new ArrayList<Object>();

				templateData.add(template.getName());
				templateData.add(template.getRemarks());

				for (ReferenceInformationTemplateDetail detail : template.getDetails()) {
					templateData.add(detail.getReferenceKey());
				}

				templateDatas.add(templateData);
			}
		}

		excelData.setRows(templateDatas);

		return excelData;
	}

	@Override
	public ExcelDataModel getExportTempaltesData(String warehouseId) {
		// 获取仓库数据
		Warehouse warehouse = warehouseDao.selectWarehouseById(warehouseId);

		// 获取全部基准信息模板
		List<ReferenceInformationTemplateDto> templates = referenceInformationTemplateDao
				.c_selectReferenceInformationTemplateDtoByWarehouseId(warehouseId);

		ExcelDataModel excelData = new ExcelDataModel();

		// 设置文件名
		Date now = new Date();
		String fileName = warehouse.getName() + "_基准信息模板_" + now.getTime();
		excelData.setName(fileName);

		// 设置表头
		List<String> heads = new ArrayList<String>();

		heads.add("名称");
		heads.add("备注");
		heads.add("创建日期");

		int templateMaxCount = 0;
		for (ReferenceInformationTemplateDto template : templates) {
			if (template.getDetails() != null && !template.getDetails().isEmpty()) {
				if (template.getDetails().size() > templateMaxCount) {
					templateMaxCount = template.getDetails().size();
				}
			}
		}

		for (int i = 1; i <= templateMaxCount; i++) {
			heads.add("基准信息名" + i);
		}

		excelData.setTitles(heads);

		// 设置数据
		List<List<Object>> templateDatas = new ArrayList<>();

		if (templates != null) {
			for (ReferenceInformationTemplateDto template : templates) {
				List<Object> templateData = new ArrayList<Object>();

				templateData.add(template.getName());
				templateData.add(template.getRemarks());
				templateData.add(template.getCreateTime());

				for (ReferenceInformationTemplateDetail detail : template.getDetails()) {
					templateData.add(detail.getReferenceKey());
				}

				templateDatas.add(templateData);
			}
		}

		excelData.setRows(templateDatas);

		return excelData;
	}

}
