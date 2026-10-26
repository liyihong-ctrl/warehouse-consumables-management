package frame.server.vo.consumables.CStatisticsController;

import lombok.Data;

/**
 * 消耗品统计信息Model
 * 
 * @author Administrator
 *
 */
@Data
public class CConsumableStatisticsSearchModel {

	// 仓库ID
	private String warehouseId;
	
	// 统计类型
	// 1：出库
	// 2：在工减少
	// 3. 入库
	private short staticType;
	
	// 消耗品ID
	private String consumableId;
	
	// 开始日期
	private String startTime;
	
	// 结束日期
	private String endTime;

}
