package frame.server.service.consumables;

import java.util.List;

import frame.server.vo.ExcelDataModel;
import frame.server.vo.consumables.CStatisticsController.CConsumableStatisticsModel;
import frame.server.vo.consumables.CStatisticsController.CGetConsumableStatisticsSearchModel;

public interface CStatisticsService {
	
	/**
	 * 获取消耗品统计信息
	 * 
	 * @param model
	 * @return
	 */
	List<CConsumableStatisticsModel> getConsumableStatics(CGetConsumableStatisticsSearchModel model);
	
	/**
	 * 导出消耗品统计信息
	 * 
	 * @param warehouseId
	 * @return
	 */
	ExcelDataModel getExportStatisticsData(CGetConsumableStatisticsSearchModel model);

}
