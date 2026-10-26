package frame.server.domain;

import java.util.Date;

import frame.server.utils.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消耗品操作记录表
 * 说明：主要用于记录消耗品出库入库操作
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsumableOperationHistory {

	// ID
	private String id;
	
	// 仓库ID
	private String warehouseId;
	
	// 操作者ID
	private String userId;
	
	// 操作日期
	private Date createTime;
	
	public String getCreateTime() {
		if(createTime != null) {
			return DateUtil.convertDateToString(createTime, DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI);
		}
		return "";
	}
	
}
