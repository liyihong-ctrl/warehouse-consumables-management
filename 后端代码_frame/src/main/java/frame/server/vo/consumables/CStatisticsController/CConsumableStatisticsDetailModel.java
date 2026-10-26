package frame.server.vo.consumables.CStatisticsController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消耗品统计数据详细Model
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CConsumableStatisticsDetailModel {

	// 日期
	private String date;
	
	// 值
	private int value;
	
}
