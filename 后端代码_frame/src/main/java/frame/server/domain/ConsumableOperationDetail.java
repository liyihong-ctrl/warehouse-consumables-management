package frame.server.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消耗品操作记录详情表
 * 
 * @author Administrator
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsumableOperationDetail {
	
	// ID
	private String id;

	// 消耗品操作记录ID
	private String consumableOperationHistoryId;
	
	// 消耗品ID
	private String consumableId;
	
	// 操作类型
	// 1.出库、2.入库、3.在工减少
	private int operationType;
	
	// 操作数
	private int operationCount;
	
	// 备注
	private String remarks;
	
}
