package frame.server.service.consumables;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;

import frame.server.domain.Consumable;
import frame.server.dto.ConsumableDto;
import frame.server.dto.TypeDtoForWebCascader;
import frame.server.vo.ExcelDataModel;
import frame.server.vo.Page;
import frame.server.vo.consumables.CSearchConditionModel;
import frame.server.vo.consumables.CConsumableController.CAddConsumableModel;
import frame.server.vo.consumables.CConsumableController.CEditConsumableModel;
import frame.server.vo.consumables.CConsumableController.CImportConsumableDetailModel;
import frame.server.vo.consumables.CConsumableController.InboundModel;
import frame.server.vo.consumables.CConsumableController.OutboundModel;

public interface CConsumableService {
	
	/**
	 * 获取全部消耗品
	 * 
	 * @return
	 */
	List<ConsumableDto> getAllConsumables(String warehouseId);
	
	/**
	 * 获取全部消耗品(分页)
	 * 
	 * @return
	 */
	PageInfo<ConsumableDto> getConsumables(Page page);
	
	/**
	 * 根据检索条件获取全部消耗品
	 * 
	 * @return
	 */
	List<ConsumableDto> getAllConsumablesBySearchCondition(CSearchConditionModel model);
	
	/**
	 * 根据消耗品ID获取消耗品
	 * 
	 * @param warehouseId
	 * @param consumableId
	 * @return
	 */
	Consumable getConsumable(String warehouseId, String consumableId);
	
	/**
	 * 获取全部类型信息
	 * 
	 * @param warehouseId
	 * @return
	 */
	List<TypeDtoForWebCascader> getTypes(String warehouseId);
	
	/**
	 * 检查消耗品是否存在
	 * 
	 * @param warehouseId
	 * @param consumableId
	 * @return
	 */
	boolean isConsumableExist(String warehouseId, String consumableId);
	
	/**
	 * 检查消耗品是否被申请且处于未审核、审核中、审核通过状态
	 * 
	 * @param warehouseId
	 * @param consumableId
	 * @return
	 */
	boolean isConsumableApplied(String warehouseId, String consumableId);
	
	/**
	 * 检查消耗品Code是否存在
	 * 
	 * @param warehouseId
	 * @param code
	 * @return
	 */
	boolean isConsumableCodeExist(String warehouseId, String code);
	
	/**
	 * 新增消耗品
	 * 
	 * @param model
	 * @return
	 */
	@Transactional
	ConsumableDto addConsumable(String userId, CAddConsumableModel model);
	
	/**
	 * 更新消耗品
	 * 
	 * @param model
	 * @return
	 */
	@Transactional
	ConsumableDto editConsumable(CEditConsumableModel model);
	
	/**
	 * 删除消耗品
	 * 
	 * 仅删除指定仓库的指定消耗品，其余管理关系数据不对应删除
	 * 
	 * @param roleId
	 */
	@Transactional
	void deleteConsumable(String warehouseId, String userId, String consumableId);
	
	/**
	 * 导入消耗品：获取样本文件
	 * 
	 * @param warehouseId
	 * @return
	 */
	ExcelDataModel getImportTemplateData(String warehouseId);
	
	/**
	 * 导入消耗品：数据校验
	 * 
	 * @param warehouseId
	 * @param importConsumables
	 * @return
	 */
	JSONObject checkImportConsumables(String warehouseId, List<CImportConsumableDetailModel> importConsumables);
	
	/**
	 * 导入消耗品
	 * 
	 * @param warehouseId
	 * @param importConsumables
	 * @return
	 */
	@Transactional
	List<ConsumableDto> importConsumables(String warehouseId, String userId, List<CImportConsumableDetailModel> importConsumables);
	
	/**
	 * 导出消耗品
	 * 
	 * @param model
	 * @return
	 */
	ExcelDataModel getExportConsumablesData(CSearchConditionModel model);
	
	/**
	 * 出库
	 * 
	 * @param model
	 */
	@Transactional
	ConsumableDto outbound(String userId, OutboundModel model);
	
	/**
	 * 入库
	 * 
	 * @param model
	 */
	@Transactional
	ConsumableDto inbound(String userId, InboundModel model);

}
