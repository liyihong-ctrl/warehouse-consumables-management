package frame.server.service.consumables;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

import frame.server.domain.Type;
import frame.server.dto.TypeDto;
import frame.server.vo.ExcelDataModel;
import frame.server.vo.Page;
import frame.server.vo.consumables.CTypeController.CAddConsumableTypeModel;
import frame.server.vo.consumables.CTypeController.CEditConsumableTypeModel;
import frame.server.vo.consumables.CTypeController.CImportTypeDetailModel;

public interface CTypeService {
	
	/**
	 * 获取全部类型
	 * 
	 * @return
	 */
	PageInfo<TypeDto> getAllTypes();
	
	/**
	 * 获取全部类型(分页)
	 * 
	 * @return
	 */
	PageInfo<TypeDto> getTypes(Page page);
	
	/**
	 * 根据类型ID获取类型信息
	 * 
	 * @param warehouseId
	 * @param typeId
	 * @return
	 */
	Type getTypeByTypeId(String warehouseId, String typeId);
	
	/**
	 * 检查指定类型是否存在
	 * 
	 * @param typeId
	 * @return
	 */
	boolean isTypeExist(String warehouseId, String typeId);
	
	/**
	 * 检查类型名是否重复
	 * 
	 * @return
	 */
	boolean isTypeNameRepeat(String warehouseId, String typeName);
	
	/**
	 * 新增类型
	 * 
	 * @param model
	 * @return
	 */
	@Transactional
	void addType(CAddConsumableTypeModel model);
	
	/**
	 * 更新类型
	 * 
	 * @param model
	 * @return
	 */
	@Transactional
	void updateType(CEditConsumableTypeModel model);
	
	/**
	 * 判断消耗品是否绑定了类型
	 * 
	 * @param typeId
	 * @return
	 */
	boolean isConsumableBoundType(String typeId);
	
	/**
	 * 删除类型
	 * 
	 * @param TypeId
	 */
	@Transactional
	void deleteType(String typeId);

	/**
	 * 获取样本文件
	 * 
	 * @param warehouseId
	 * @return
	 */
	ExcelDataModel getImportTemplateData(String warehouseId);
	
	/**
	 * 导入类型：数据校验
	 * 
	 * @param importTypes
	 * @return
	 */
	JSONObject checkImportTypes(String warehouseId, List<CImportTypeDetailModel> importTypes);
	
	/**
	 * 导入类型
	 * 
	 * @param model
	 * @return
	 */
	@Transactional
	List<TypeDto> importTypes(String warehouseId, List<CImportTypeDetailModel> importTypes);
	
	/**
	 * 导出类型
	 * 
	 * @param warehouseId
	 * @return
	 */
	ExcelDataModel getExportTypesData(String warehouseId);

}
