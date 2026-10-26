package frame.server.vo.consumables.CStatisticsController;

import java.util.List;

import lombok.Data;

/**
 * 消耗品统计数据Model
 * 
 * @author Administrator
 *
 */
@Data
public class CConsumableStatisticsModel {

	// 消耗品ID
	private String consumableId;
	
	// 消耗品名称
	private String consumableName;
	
	// 统计信息
	List<CConsumableStatisticsDetailModel> data;
	
}
