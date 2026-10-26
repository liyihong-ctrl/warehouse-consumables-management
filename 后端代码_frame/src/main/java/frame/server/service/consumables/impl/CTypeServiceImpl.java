package frame.server.service.consumables.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import frame.server.dao.ConsumableDao;
import frame.server.dao.TypeDao;
import frame.server.dao.TypeWarehouseDao;
import frame.server.dao.WarehouseDao;
import frame.server.domain.Type;
import frame.server.domain.TypeWarehouse;
import frame.server.domain.Warehouse;
import frame.server.dto.TypeDto;
import frame.server.service.consumables.CTypeService;
import frame.server.utils.RandomUtil;
import frame.server.utils.StringUtil;
import frame.server.vo.ExcelDataModel;
import frame.server.vo.Page;
import frame.server.vo.consumables.CTypeController.CAddConsumableTypeModel;
import frame.server.vo.consumables.CTypeController.CEditConsumableTypeModel;
import frame.server.vo.consumables.CTypeController.CImportTypeDetailModel;
import frame.server.vo.consumables.CTypeController.SelectTypeByNameModel;

@Service("cTypeService")
public class CTypeServiceImpl implements CTypeService {

	@Autowired
	private ConsumableDao consumableDao;

	@Autowired
	private TypeDao typeDao;

	@Autowired
	private TypeWarehouseDao typeWarehouseDao;

	@Autowired
	private WarehouseDao warehouseDao;

	@Override
	public PageInfo<TypeDto> getAllTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PageInfo<TypeDto> getTypes(Page page) {
		// 开启分页
		PageHelper.startPage(page.getPageNum(), page.getPageSize());

		// 结果集
		PageInfo<TypeDto> pageInfo = null;

		// 解析检索条件
		JSONObject searchCondition = page.getSearchCondition();
		String warehouseId = searchCondition.getString("warehouseId");
		String searchStr = searchCondition.getString("searchStr");

		// 检索条件为空=>全量检索
		if (StringUtil.isBlank(searchStr)) {
			pageInfo = new PageInfo<TypeDto>(typeDao.c_selectTypeDtoByWarehouseId(warehouseId));
		}
		// 检索条件不为空=>根据类型名称检索
		else {
			SelectTypeByNameModel selectTypeByNameModel = new SelectTypeByNameModel(warehouseId, searchStr);
			pageInfo = new PageInfo<TypeDto>(typeDao.c_selectTypeDtoByWarehouseIdAndLikeName(selectTypeByNameModel));
		}

		return pageInfo;
	}

	@Override
	public Type getTypeByTypeId(String warehouseId, String typeId) {
		TypeWarehouse typeWarehouse = new TypeWarehouse(warehouseId, typeId);
		return typeDao.c_selectTypeByWarehouseIdAndTypeId(typeWarehouse);
	}

	@Override
	public boolean isTypeExist(String warehouseId, String typeId) {
		TypeWarehouse typeWarehouse = new TypeWarehouse(warehouseId, typeId);
		return typeWarehouseDao.c_selectCountByTypeIdAndWarehouseId(typeWarehouse) != 0;
	}

	@Override
	public boolean isTypeNameRepeat(String warehouseId, String typeName) {
		SelectTypeByNameModel model = new SelectTypeByNameModel(warehouseId, typeName);
		return typeDao.c_selectCountByWarehouseIdAndName(model) > 0;
	}

	@Override
	public void addType(CAddConsumableTypeModel model) {
		// 构造类型实体
		Type type = new Type(RandomUtil.genarateId(), model.getName(), model.getRemarks(), model.getParentId(),
				new Date(), false);

		// 构造类型仓库关联关系实体
		TypeWarehouse typeWarehouse = new TypeWarehouse(model.getWarehouseId(), type.getId());

		// 向数据库中插入数据
		typeDao.insertType(type);
		typeWarehouseDao.insertTypeWarehouse(typeWarehouse);
	}

	@Override
	public void updateType(CEditConsumableTypeModel model) {
		Type type = new Type();
		type.setId(model.getId());
		type.setName(model.getName().trim());
		type.setRemarks(model.getRemarks().trim());
		typeDao.update(type);
	}

	@Override
	public boolean isConsumableBoundType(String typeId) {
		// 判断有无消耗品绑定当前类型
		if (consumableDao.c_selectConsumableCountByTypeId(typeId) > 0) {
			return true;
		}

		// 根据类型ID检索所有子类型ID
		List<String> childrenTypeIds = typeDao.c_selectChildrenTypeIdByParentId(typeId);

		// 无子类
		if (childrenTypeIds == null || childrenTypeIds.isEmpty()) {
			return false;
		}

		// 有子类
		for (String childrenTypeId : childrenTypeIds) {
			if (isConsumableBoundType(childrenTypeId)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void deleteType(String typeId) {
		// 获取所有子类型
		List<TypeDto> types = typeDao.c_selectTypeDtoByParentId(typeId);

		// 有子类型
		if (types != null && !types.isEmpty()) {
			for (TypeDto type : types) {
				deleteType(type.getId());
			}
		}

		// 循环删除
		typeDao.delete(typeId);
	}

	@Override
	public ExcelDataModel getImportTemplateData(String warehouseId) {
		// 获取仓库数据
		Warehouse warehouse = warehouseDao.selectWarehouseById(warehouseId);

		ExcelDataModel excelData = new ExcelDataModel();

		// 设置文件名
		Date now = new Date();
		String fileName = warehouse.getName() + "_批量导入类型_样本_" + now.getTime();
		excelData.setName(fileName);

		// 设置表头
		List<String> heads = new ArrayList<String>();

		heads.add("类型名称1");
		heads.add("类型名称2");
		heads.add("类型名称3");
		heads.add("类型名称4");
		heads.add("类型名称5");
		heads.add("类型名称6");
		heads.add("类型名称7");
		heads.add("类型名称8");
		heads.add("类型名称9");
		heads.add("类型名称10");
		heads.add("备注");

		excelData.setTitles(heads);

		// 设置数据
		List<List<Object>> templateDatas = new ArrayList<>();

		List<Object> templateData1 = new ArrayList<Object>();
		templateData1.add("文具");
		templateData1.add("");
		templateData1.add("");
		templateData1.add("");
		templateData1.add("");
		templateData1.add("");
		templateData1.add("");
		templateData1.add("");
		templateData1.add("");
		templateData1.add("");
		templateData1.add("");
		templateDatas.add(templateData1);

		List<Object> templateData2 = new ArrayList<Object>();
		templateData2.add("");
		templateData2.add("书写工具系列");
		templateData2.add("");
		templateData2.add("");
		templateData2.add("");
		templateData2.add("");
		templateData2.add("");
		templateData2.add("");
		templateData2.add("");
		templateData2.add("");
		templateData2.add("");
		templateDatas.add(templateData2);

		List<Object> templateData3 = new ArrayList<Object>();
		templateData3.add("");
		templateData3.add("");
		templateData3.add("铅笔系列");
		templateData3.add("");
		templateData3.add("");
		templateData3.add("");
		templateData3.add("");
		templateData3.add("");
		templateData3.add("");
		templateData3.add("");
		templateData3.add("");
		templateDatas.add(templateData3);

		List<Object> templateData4 = new ArrayList<Object>();
		templateData4.add("");
		templateData4.add("");
		templateData4.add("");
		templateData4.add("普通铅笔");
		templateData4.add("");
		templateData4.add("");
		templateData4.add("");
		templateData4.add("");
		templateData4.add("");
		templateData4.add("");
		templateData4.add("");
		templateDatas.add(templateData4);

		List<Object> templateData5 = new ArrayList<Object>();
		templateData5.add("");
		templateData5.add("");
		templateData5.add("");
		templateData5.add("彩色铅笔");
		templateData5.add("");
		templateData5.add("");
		templateData5.add("");
		templateData5.add("");
		templateData5.add("");
		templateData5.add("");
		templateData5.add("");
		templateDatas.add(templateData5);

		List<Object> templateData6 = new ArrayList<Object>();
		templateData6.add("");
		templateData6.add("");
		templateData6.add("");
		templateData6.add("绘图铅笔");
		templateData6.add("");
		templateData6.add("");
		templateData6.add("");
		templateData6.add("");
		templateData6.add("");
		templateData6.add("");
		templateData6.add("");
		templateDatas.add(templateData6);

		List<Object> templateData7 = new ArrayList<Object>();
		templateData7.add("");
		templateData7.add("");
		templateData7.add("钢笔系列");
		templateData7.add("");
		templateData7.add("");
		templateData7.add("");
		templateData7.add("");
		templateData7.add("");
		templateData7.add("");
		templateData7.add("");
		templateData7.add("");
		templateDatas.add(templateData7);

		List<Object> templateData8 = new ArrayList<Object>();
		templateData8.add("");
		templateData8.add("");
		templateData8.add("油性笔系列");
		templateData8.add("");
		templateData8.add("");
		templateData8.add("");
		templateData8.add("");
		templateData8.add("");
		templateData8.add("");
		templateData8.add("");
		templateData8.add("");
		templateDatas.add(templateData8);

		List<Object> templateData9 = new ArrayList<Object>();
		templateData9.add("");
		templateData9.add("修正工具系列");
		templateData9.add("");
		templateData9.add("");
		templateData9.add("");
		templateData9.add("");
		templateData9.add("");
		templateData9.add("");
		templateData9.add("");
		templateData9.add("");
		templateData9.add("");
		templateDatas.add(templateData9);

		List<Object> templateData10 = new ArrayList<Object>();
		templateData10.add("");
		templateData10.add("文件管理系列");
		templateData10.add("");
		templateData10.add("");
		templateData10.add("");
		templateData10.add("");
		templateData10.add("");
		templateData10.add("");
		templateData10.add("");
		templateData10.add("");
		templateData10.add("");
		templateDatas.add(templateData10);

		excelData.setRows(templateDatas);

		return excelData;
	}

	@Override
	public JSONObject checkImportTypes(String warehouseId, List<CImportTypeDetailModel> importTypes) {
		JSONObject errorDatas = new JSONObject();
		
		// 循环校验
		for (int i = 0; i < importTypes.size(); i++) {
			JSONObject errorData = new JSONObject();

			// 校验类型名1
			if (!StringUtil.isBlank(importTypes.get(i).getName1())) {
				String name = importTypes.get(i).getName1().trim();
				if (name.length() > 20) {
					errorData.put("name1", "类型名称长度范围为1~20");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName2())) {
					errorData.put("name2", "类型名称1存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName3())) {
					errorData.put("name3", "类型名称1存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName4())) {
					errorData.put("name4", "类型名称1存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName5())) {
					errorData.put("name5", "类型名称1存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName6())) {
					errorData.put("name6", "类型名称1存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName7())) {
					errorData.put("name7", "类型名称1存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName8())) {
					errorData.put("name8", "类型名称1存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName9())) {
					errorData.put("name9", "类型名称1存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName10())) {
					errorData.put("name10", "类型名称1存在");
				}
			}
			// 校验类型名2
			else if (!StringUtil.isBlank(importTypes.get(i).getName2())) {
				String name = importTypes.get(i).getName2().trim();
				if (name.length() > 20) {
					errorData.put("name2", "类型名长度范围为1~20");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName3())) {
					errorData.put("name3", "类型名称2存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName4())) {
					errorData.put("name4", "类型名称2存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName5())) {
					errorData.put("name5", "类型名称2存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName6())) {
					errorData.put("name6", "类型名称2存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName7())) {
					errorData.put("name7", "类型名称2存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName8())) {
					errorData.put("name8", "类型名称2存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName9())) {
					errorData.put("name9", "类型名称2存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName10())) {
					errorData.put("name10", "类型名称2存在");
				}
			}
			// 校验类型名3
			else if (!StringUtil.isBlank(importTypes.get(i).getName3())) {
				String name = importTypes.get(i).getName3().trim();
				if (name.length() > 20) {
					errorData.put("name3", "类型名长度范围为1~20");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName4())) {
					errorData.put("name4", "类型名称3存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName5())) {
					errorData.put("name5", "类型名称3存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName6())) {
					errorData.put("name6", "类型名称3存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName7())) {
					errorData.put("name7", "类型名称3存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName8())) {
					errorData.put("name8", "类型名称3存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName9())) {
					errorData.put("name9", "类型名称3存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName10())) {
					errorData.put("name10", "类型名称3存在");
				}
			}
			// 校验类型名4
			else if (!StringUtil.isBlank(importTypes.get(i).getName4())) {
				String name = importTypes.get(i).getName4().trim();
				if (name.length() > 20) {
					errorData.put("name4", "类型名长度范围为1~20");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName5())) {
					errorData.put("name5", "类型名称4存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName6())) {
					errorData.put("name6", "类型名称4存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName7())) {
					errorData.put("name7", "类型名称4存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName8())) {
					errorData.put("name8", "类型名称4存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName9())) {
					errorData.put("name9", "类型名称4存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName10())) {
					errorData.put("name10", "类型名称4存在");
				}
			}
			// 校验类型名5
			else if (!StringUtil.isBlank(importTypes.get(i).getName5())) {
				String name = importTypes.get(i).getName5().trim();
				if (name.length() > 20) {
					errorData.put("name5", "类型名长度范围为1~20");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName6())) {
					errorData.put("name6", "类型名称5存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName7())) {
					errorData.put("name7", "类型名称5存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName8())) {
					errorData.put("name8", "类型名称5存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName9())) {
					errorData.put("name9", "类型名称5存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName10())) {
					errorData.put("name10", "类型名称5存在");
				}
			}
			// 校验类型名6
			else if (!StringUtil.isBlank(importTypes.get(i).getName6())) {
				String name = importTypes.get(i).getName6().trim();
				if (name.length() > 20) {
					errorData.put("name6", "类型名长度范围为1~20");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName7())) {
					errorData.put("name7", "类型名称6存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName8())) {
					errorData.put("name8", "类型名称6存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName9())) {
					errorData.put("name9", "类型名称6存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName10())) {
					errorData.put("name10", "类型名称6存在");
				}
			}
			// 校验类型名7
			else if (!StringUtil.isBlank(importTypes.get(i).getName7())) {
				String name = importTypes.get(i).getName7().trim();
				if (name.length() > 20) {
					errorData.put("name7", "类型名长度范围为1~20");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName8())) {
					errorData.put("name8", "类型名称7存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName9())) {
					errorData.put("name9", "类型名称7存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName10())) {
					errorData.put("name10", "类型名称7存在");
				}
			}
			// 校验类型名8
			else if (!StringUtil.isBlank(importTypes.get(i).getName8())) {
				String name = importTypes.get(i).getName8().trim();
				if (name.length() > 20) {
					errorData.put("name8", "类型名长度范围为1~20");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName9())) {
					errorData.put("name9", "类型名称8存在");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName10())) {
					errorData.put("name10", "类型名称8存在");
				}
			}
			// 校验类型名9
			else if (!StringUtil.isBlank(importTypes.get(i).getName9())) {
				String name = importTypes.get(i).getName9().trim();
				if (name.length() > 20) {
					errorData.put("name9", "类型名长度范围为1~20");
				}
				if (!StringUtil.isBlank(importTypes.get(i).getName10())) {
					errorData.put("name10", "类型名称9存在");
				}
			}
			// 校验类型名10
			else if (!StringUtil.isBlank(importTypes.get(i).getName10())) {
				String name = importTypes.get(i).getName10().trim();
				if (name.length() > 20) {
					errorData.put("name10", "类型名长度范围为1~20");
				}
			}

			// 校验备注
			if (!StringUtil.isBlank(importTypes.get(i).getRemarks())) {
				String remarks = importTypes.get(i).getRemarks().trim();
				if (remarks.length() > 50) {
					errorData.put("remarks", "备注长度范围为0~50");
				}
			}

			if (!errorData.isEmpty()) {
				errorDatas.put(Integer.toString(i), errorData);
			}
		}

		if (errorDatas.size() > 0) {
			return errorDatas;
		}

		return null;
	}

	@Override
	public List<TypeDto> importTypes(String warehouseId, List<CImportTypeDetailModel> importTypes) {
		// 获取仓库下全部类型
		List<Type> dbTypes = typeDao.c_selectTypeByWarehouseId(warehouseId);

		// 需要添加的类型
		List<CAddConsumableTypeModel> addTypes = new ArrayList<CAddConsumableTypeModel>();

		// 需要更新的类型
		List<CEditConsumableTypeModel> updateTypes = new ArrayList<CEditConsumableTypeModel>();

		// 结果集
		List<TypeDto> res = new ArrayList<TypeDto>();

		for (CImportTypeDetailModel importType : importTypes) {
			
		}

		return res;
	}

	@Override
	public ExcelDataModel getExportTypesData(String warehouseId) {
		// 获取仓库数据
		Warehouse warehouse = warehouseDao.selectWarehouseById(warehouseId);

		// 获取消耗品类型数据
		List<TypeDto> types = typeDao.c_selectTypeDtoByWarehouseId(warehouseId);

		ExcelDataModel excelData = new ExcelDataModel();

		// 设置文件名
		Date now = new Date();
		String fileName = warehouse.getName() + "_消耗品类型_" + now.getTime();
		excelData.setName(fileName);

		// 设置表头
		List<String> heads = new ArrayList<String>();

		int maximumDepth = 1;
		for (TypeDto type : types) {
			int depth = 1;
			if (type.getChildren() != null && !type.getChildren().isEmpty()) {
				depth = getMaximumDepth(type.getChildren(), 2);
			}
			if (maximumDepth < depth) {
				maximumDepth = depth;
			}
		}

		for (int i = 0; i < maximumDepth; i++) {
			heads.add("类型名称" + i);
		}

		heads.add("备注");
		heads.add("消耗品数()当前类型");
		heads.add("合计");

		excelData.setTitles(heads);

		// 设置数据
		List<List<Object>> templateDatas = new ArrayList<>();

		if (types == null || types.isEmpty()) {
			excelData.setRows(templateDatas);
			return excelData;
		}

		excelData.setRows(setExcelRows(templateDatas, types, maximumDepth, 1));

		return excelData;
	}

	private int getMaximumDepth(List<TypeDto> types, int index) {
		boolean isFirstAdd = true;
		for (TypeDto type : types) {
			if (type.getChildren() != null && !type.getChildren().isEmpty()) {
				if (isFirstAdd) {
					index++;
					isFirstAdd = false;
				}
				index = getMaximumDepth(type.getChildren(), index);
			}
		}
		return index;
	}

	private List<List<Object>> setExcelRows(List<List<Object>> templateDatas, List<TypeDto> types, int maximumDepth,
			int depth) {
		for (TypeDto type : types) {
			List<Object> templateData = new ArrayList<Object>();

			for (int i = 0; i < depth - 1; i++) {
				templateData.add("");
			}

			templateData.add(type.getName());

			for (int i = depth; i < maximumDepth; i++) {
				templateData.add("");
			}

			templateData.add(type.getRemarks());
			templateData.add(type.getCurTypeConsumableCount());
			templateData.add(type.getTotalAmount());
			templateDatas.add(templateData);
			if (type.getChildren() != null && !type.getChildren().isEmpty()) {
				this.setExcelRows(templateDatas, type.getChildren(), maximumDepth, depth + 1);
			}
		}
		return templateDatas;
	}

}
