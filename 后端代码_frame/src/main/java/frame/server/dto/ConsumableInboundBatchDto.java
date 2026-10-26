package frame.server.dto;

import java.util.Date;

import frame.server.utils.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消耗品入库批次
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsumableInboundBatchDto {
	
	// ID
	private String id;
	
	// 消耗品ID
	private String consumableId;
	
	// 入库数
	private int count;
	
	// 入库价格
	private double price;
	
	// 入库日期
	private Date createTime;

	public String getCreateTime() {
		if (createTime != null) {
			return DateUtil.convertDateToString(createTime, DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI);
		}
		return "";
	}

}
