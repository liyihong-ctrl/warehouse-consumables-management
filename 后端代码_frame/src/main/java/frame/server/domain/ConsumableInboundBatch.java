package frame.server.domain;

import java.util.Date;

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
public class ConsumableInboundBatch {
	
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

}
