package frame.server.service.consumables;

import java.util.List;

import frame.server.dto.ConsumableDto;

public interface CHomeService {
	
	/**
	 * 获取当前仓库角色数
	 * 
	 * @param warehouseId
	 * @return
	 */
	int getRoleCount(String warehouseId);
	
	/**
	 * 获取当前仓库人员数
	 * 
	 * @param warehouseId
	 * @return
	 */
	int getUserCount(String warehouseId);
	
	/**
	 * 获取当前仓库消耗品类型数
	 * 
	 * @param warehouseId
	 * @return
	 */
	int getTypeCount(String warehouseId);
	
	/**
	 * 获取当前仓库基准信息模板数
	 * 
	 * @param warehouseId
	 * @return
	 */
	int getTemplateCount(String warehouseId);
	
	/**
	 * 获取当前仓库消耗品数
	 * 
	 * @param warehouseId
	 * @return
	 */
	int getConsumableCount(String warehouseId);
	
	/**
	 * 获取当前仓库消申请数
	 * 
	 * @param warehouseId
	 * @return
	 */
	int getApplicationCount(String warehouseId);
	
	/**
	 * 获取当前仓库
	 * 
	 * @param warehouseId
	 * @return
	 */
	List<ConsumableDto> getAlarmData(String warehouseId);

}
