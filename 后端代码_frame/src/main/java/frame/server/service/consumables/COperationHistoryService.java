package frame.server.service.consumables;

import com.github.pagehelper.PageInfo;

import frame.server.dto.ConsumableOperationHistoryDto;
import frame.server.vo.ExcelDataModel;
import frame.server.vo.Page;
import frame.server.vo.consumables.CSearchConditionModel;

public interface COperationHistoryService {
	
	/**
	 * 获取操作履历
	 * 
	 * @param page
	 * @return
	 */
	PageInfo<ConsumableOperationHistoryDto> getOperationHistorys(Page page);
	
	/**
	 * 导出操作履历
	 * 
	 * @param model
	 * @return
	 */
	ExcelDataModel getExportRolesData(CSearchConditionModel model);

}
